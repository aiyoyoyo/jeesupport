package com.jees.webs.security.configs;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.FileUtil;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.service.VerifyService;
import com.jees.webs.security.struct.PageAccess;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 本地文件加载授权信息到VerifyModelService里面
 */
@Log4j2
@Component
public class LocalConfig {
    @Autowired
    VerifyService verifyService;

    public void initialize(){
        this._load_config_line( null );
        // 系统匿名配置
        // /login,/logout,/error*
        verifyService.getAnonymous().add( "/" + CommonConfig.getString( "jees.webs.security.login", "login" ) );
        verifyService.getAnonymous().add( "/" + CommonConfig.getString( "jees.webs.security.logout", "logout" ) );
        verifyService.getAnonymous().add( "/" + CommonConfig.getString( "jees.webs.security.error", "error*" ) );
        // 将全局匿名配置加载
        String[] anons = CommonConfig.getArray( "jees.webs.security.anonymous", String.class );
        for( String anon : anons ){
            verifyService.getAnonymous().add( anon );
        }
    }

    /**
     * 加载每行内容，并分解成授权信息
     * @param _node
     */
    private void _load_config_line( String _node ){
        // 配置文件夹需要带上/结束
        String cfg_file = CommonConfig.get( "spring.config.location", "config/" );
        AtomicReference<Boolean> is_read_auth = new AtomicReference<>(true);
        AtomicReference<Boolean> is_read_user = new AtomicReference<>(false);
        AtomicReference<Boolean> is_read_role = new AtomicReference<>(false);
        AtomicReference<Boolean> is_read_blank = new AtomicReference<>(false);
        FileUtil.read( FileUtil.classpath() + "/" + cfg_file + "/verify.cfg", (_line )->{
            // 逐行解释
            // 路径节点： [*]，[/]，[/parent]，[/parent/child]
            // 用户节点： [users]
            if( _line.trim().startsWith("#") ) return;
            if( _line.equalsIgnoreCase( "[users]" ) ){
                is_read_auth.set(false);
                is_read_user.set(true);
                return;
            }else if( _line.equalsIgnoreCase( "[roles]" ) ){
                is_read_user.set(false);
                is_read_role.set(true);
                return;
            } else if( _line.equalsIgnoreCase( "[black]" ) ){
                is_read_role.set(false);
                is_read_blank.set(true);
                return;
            } else if( _line.startsWith("[") && _line.endsWith( "]")){
                is_read_auth.set( true );
                is_read_user.set( false );
                is_read_role.set( false );
                is_read_blank.set( false );
            }
            if(is_read_auth.get()){
                if( _node == null || _node.equalsIgnoreCase( "auth" ) ) {
                    // 解析待分析的授权地址
                    _load_config_line_auth( _line );
                }
            }
            if(is_read_user.get()){
                if( _node == null || _node.equalsIgnoreCase( "users" ) ) {
                    // 解析授权地址中的用户
                    _load_config_line_users( _line );
                }
            }
            if(is_read_role.get()){
                if( _node == null || _node.equalsIgnoreCase( "role" ) ) {
                    // 解析授权中的角色
                    _load_config_line_roles( _line );
                }
            }
            if(is_read_blank.get()){
                if( _node == null || _node.equalsIgnoreCase( "black" ) ) {
                    // 解析授权中的角色
                    _load_config_line_blacks( _line );
                }
            }
        } );
    }

    String lastAuth = null;

    /**
     * 解析待授权地址下的配置信息
     * @param _line
     */
    private void _load_config_line_auth( String _line ){
        if( _line.startsWith( "[" ) && _line.endsWith( "]" ) ){
            this.lastAuth = _line.replace( "[", "" ).replace( "]", "" );
        }

        String auth_url;
        String[] auth_arr = null;
        // 这里分元素授权
        if( this.lastAuth.indexOf( ":" ) != -1 ){
            // 需要分割并处理元素
            auth_arr = this.lastAuth.split( ":" );
            auth_url = auth_arr[0];
        }else {
            // 普通的页面授权加载
            auth_url = this.lastAuth;
        }

        PageAccess page = verifyService.getAuths().get(auth_url);
        if (page == null) {
            page = new PageAccess();
            page.setUrl(auth_url);
            verifyService.getAuths().put(auth_url, page);
        }else{
            if( auth_arr != null ){
                String[] elements = auth_arr[1].split( "," );
                for( String element : elements ){
                    PageAccess el_page = page.getElPage( element );
                    // 获取每个元素，并记录授权
                    if( el_page == null ) {
                        el_page = new PageAccess();
                        el_page.setUrl(element);
                        page.addElPage( element, el_page );
                    }else {
                        _load_config_line_auth_page( el_page, _line );
                    }
                }
            }else{
                _load_config_line_auth_page(page, _line);
            }
        }
    }

    private void _load_config_line_auth_page( PageAccess _page, String _line ){
        String[] line_str = _line.split("=");
        if( line_str.length == 1 ){
            return;
        }
        String[] auth_data = line_str[1].split(",");
        switch (line_str[0].trim()) {
            case "role":
                _page.addRoles(auth_data);
                break;
            case "deny":
                _page.addDenys(auth_data);
                break;
            case "user":
                _page.addUsers(auth_data);
                break;
            case "anonymous":
                _page.setAnonymous( Boolean.parseBoolean( line_str[1].trim() ));
                if( !verifyService.getAnonymous().contains( _page.getUrl())){
                    verifyService.getAnonymous().add(_page.getUrl());
                }
                break;
        }
    }
    /**
     * 读取用户行内容
     * @param _line
     */
    private void _load_config_line_users( String _line ){
        String[] line_str = _line.split( "=" );
        if( line_str.length == 1 ){
            return;
        }
        SuperUser user = new SuperUser();
        user.setEnabled( true );
        user.setLocked( false );
        user.setUsername( line_str[0].trim() );
        user.setPassword( line_str[1].trim() );
        verifyService.getUsers().put( user.getUsername().toLowerCase(), user );
    }

    /**
     * 读取角色行内容
     * @param _line
     */
    private void _load_config_line_roles( String _line ){
        // 角色 = 用户1, ..., 用户N
        String[] line_str = _line.split( "=" );
        if( line_str.length == 1 ){
            return;
        }
        String[] role_user_str = line_str[1].split(",");

        SuperRole role = new SuperRole();
        role.setId( verifyService.getRoles().size() );
        role.setName( line_str[0].trim() );

        Set<String> users = new HashSet<>();
        role.setUsers( users );
        for( String role_user : role_user_str ){
            if( !users.contains( role_user ) ){
                users.add( role_user );
            }
        }

        for( String role_user : role_user_str ) {
            SuperUser user = verifyService.findUserByUsername(role_user.trim());
            if (user == null) {
                log.warn("角色[" + line_str[0] + "]未找到相关用户[" + role_user + "]信息!");
                return;
            }else{
                user.addRole( role );
            }
        }
    }

    /**
     * 读取黑名单行内容
     * @param _line
     */
    private void _load_config_line_blacks( String _line ){
        String[] line_str = _line.split( "=" );
        if( line_str.length == 1 ){
            return;
        }
        String line_key = line_str[0].trim();
        String[] line_val = line_str[1].split(",");
        for( String val : line_val ) {
            val = val.trim();
            switch (line_key) {
                case "user":
                    if( !verifyService.getBUsers().contains( val ) ){
                        verifyService.getBUsers().add( val );
                    }
                    break;
                case "role":
                    if( !verifyService.getBRoles().contains( val ) ){
                        verifyService.getBRoles().add( val );
                    }
                    break;
                case "ip":
                    if( !verifyService.getBIps().contains( val ) ){
                        verifyService.getBIps().add( val );
                    }
                    break;
            }
        }
    }

    // 提供修改本地验证文件的接口
    List<String> cfgLines = new ArrayList<>();

    /**
     * 重新加载配置文件内容
     */
    public void loadConfig(){
        cfgLines.clear();
        String cfg_file = CommonConfig.get( "spring.config.location", "config/" );
        FileUtil.read( FileUtil.classpath() + "/" + cfg_file + "verify.cfg", (_line )->{
            cfgLines.add(_line);
        });
    }
    public void writeConfig(){
        String cfg_file = CommonConfig.get( "spring.config.location", "config/" );
        StringBuffer sb = new StringBuffer();
        for( String line : cfgLines ){
            System.out.println( line );
            sb.append( line );
        }
//        try {
//            FileUtil.write( sb.toString(), FileUtil.classpath() + "/" + cfg_file + "verify.cfg", false);
//        } catch (IOException e) {
//            log.error( "写入文件失败：", e);
//        }
    }
    // 基础配置项的新增、修改和删除，部分固定内容不允许删除
    /**
     * 找到此项的索引起止位置
     * @param _name 配置项，不含[]
     * @return int[] 如果任意一个索引为-1表示位置不正确
     */
    private int[] _find_config_index( String _name ){
        int[] indexes = new int[]{-1,-1};
        String cfg_name = "[" + _name + "]";
        int begin = -1;
        int end = -1;
        int exp = 0; // 结束索引前注释数量
        for( int i = 0; i < cfgLines.size(); i ++ ){
            String name = cfgLines.get( i ).trim();
            if( name.equalsIgnoreCase( cfg_name ) ){
                begin = i;
            }else if( begin != -1 && name.startsWith("[") && name.endsWith( "]" ) ){
                end = i;
                break;
            }else if( begin != -1 && name.startsWith( "#") ){
                exp ++;
            }
        }
        if( begin != -1 && end != -1 ){
            indexes[0] = begin;
            indexes[1] = end - exp;
        }
        return indexes;
    }

    public void addItem( String _name, String _item, String _value ) throws Exception {
        int[] indexes = this._find_config_index(_name);
        for( int i = indexes[0]; i < indexes[1]; i ++ ){
            String line = cfgLines.get( i );
            if( line.startsWith("#" ) ){
                continue;
            }
            if( line.indexOf( "=") == -1 ){
                continue;
            }
            String[] o_item = line.split( "=" );
            if( o_item[0].equalsIgnoreCase( _item ) ){
                throw new Exception( "不允许新增同名项目：" + _name + "->" + _item );
            }
        }
        if( indexes[0] == -1 || indexes[1] == -1 ){
            throw new Exception( "没有找到要修改的项目：" + _name + "->" + _item );
        }
        cfgLines.add( indexes[1], _item + " = " + _value);
        this.writeConfig();
    }

    public void changeItem( String _name, String _item, String _value ) throws Exception {
        int[] indexes = this._find_config_index( _name);
        boolean finder = false;
        for( int i = indexes[0]; i < indexes[1]; i ++ ){
            String line = cfgLines.get( i );
            if( line.startsWith("#" ) ){
                continue;
            }
            if( line.indexOf( "=") == -1 ){
                continue;
            }
            String[] o_item = line.split( "=" );
            if( o_item[0].trim().equalsIgnoreCase( _item ) ){
                cfgLines.set( i, _item + " = " + _value );
                finder = true;
                this.writeConfig();
                break;
            }
        }
        if(!finder) {
            throw new Exception("没有找到要修改的项目：" + _name + "->" + _item);
        }
    }

    public void removeItem( String _name, String _item ) throws Exception {
        boolean no_access = false;
        if( _name.equalsIgnoreCase("users")){
            if( _item.equalsIgnoreCase( "admin" )){
                no_access = true;
            }
        }else if( _name.equalsIgnoreCase( "roles")){
            if( _item.equalsIgnoreCase( "admin" )){
                no_access = true;
            }
        }else if( _name.equalsIgnoreCase( "black")){

        }else if( _name.equalsIgnoreCase( "*")){

        }else if( _name.equalsIgnoreCase( "/")){

        }
        if( no_access ) {
            throw new Exception("该项目不允许删除：" + _name + "->" + _item);
        }
        int[] indexes = this._find_config_index( _name);
        boolean finder = false;
        for( int i = indexes[0]; i < indexes[1]; i ++ ){
            String line = cfgLines.get( i );
            if( line.startsWith("#" ) ){
                continue;
            }
            if( line.indexOf( "=") == -1 ){
                continue;
            }
            String[] o_item = line.split( "=" );
            if( o_item[0].trim().equalsIgnoreCase( _item ) ){
                finder = true;
                cfgLines.remove(i);
                this.writeConfig();
                break;
            }
        }
        if(!finder) {
            throw new Exception("没有找到要修改的项目：" + _name + "->" + _item);
        }
    }

    public void addPage( String _page ) throws Exception {
        int[] indexes = _find_config_index(_page);
        if( indexes[0] == -1 && indexes[1] == -1 ){
            cfgLines.add( "[" + _page + "]" );
            cfgLines.add( "user = " );
            cfgLines.add( "role = " );
            cfgLines.add( "deny = " );
            cfgLines.add( "anonymous = false" );

            this.writeConfig();
        }else{
            throw new Exception("不允许添加已存在页面：" + _page );
        }
    }

    public void removePage( String _page ) throws Exception {
        int[] indexes = _find_config_index(_page);
        if( indexes[0] != -1 && indexes[1] != -1 ){
            for( int i = 0; i < indexes[1] - indexes[0]; i++){
                cfgLines.remove( indexes[0] );
            }

            this.writeConfig();
        }else{
            throw new Exception("要移除的页面不存在：" + _page );
        }
    }
}
