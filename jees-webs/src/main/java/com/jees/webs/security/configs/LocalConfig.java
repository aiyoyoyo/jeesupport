package com.jees.webs.security.configs;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.FileUtil;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.service.VerifyModelService;
import com.jees.webs.security.struct.PageAccess;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 本地文件加载授权信息到VerifyModelService里面
 */
@Log4j2
@Component
public class LocalConfig {
    @Autowired
    VerifyModelService verifyModelService;

    public void initialize(){
        this._load_config_line( null );
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

        PageAccess page = verifyModelService.getAuths().get(auth_url);
        if (page == null) {
            page = new PageAccess();
            page.setUrl(auth_url);
            verifyModelService.getAuths().put(auth_url, page);
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
        if( line_str.length == 1 ) return;
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
                break;
        }
    }
    /**
     * 读取用户行内容
     * @param _line
     */
    private void _load_config_line_users( String _line ){
        String[] line_str = _line.split( "=" );
        if( line_str.length == 1 ) return;
        SuperUser user = new SuperUser();
        user.setEnabled( true );
        user.setLocked( false );
        user.setUsername( line_str[0].trim() );
        user.setPassword( line_str[1].trim() );
        verifyModelService.getUsers().put( user.getUsername().toLowerCase(), user );
    }

    /**
     * 读取角色行内容
     * @param _line
     */
    private void _load_config_line_roles( String _line ){
        // 角色 = 用户1, ..., 用户N
        String[] line_str = _line.split( "=" );
        if( line_str.length == 1 ) return;
        String[] role_user_str = line_str[1].split(",");

        SuperRole role = new SuperRole();
        role.setId( verifyModelService.getRoles().size() );
        role.setName( line_str[0].trim() );

        Set<String> users = new HashSet<>();
        role.setUsers( users );
        for( String role_user : role_user_str ){
            if( !users.contains( role_user ) ){
                users.add( role_user );
            }
        }

        for( String role_user : role_user_str ) {
            SuperUser user = verifyModelService.findUserByUsername(role_user.trim());
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
        if( line_str.length == 1 ) return;
        String line_key = line_str[0].trim();
        String[] line_val = line_str[1].split(",");
        for( String val : line_val ) {
            switch (line_key) {
                case "user":
                    if( !verifyModelService.getBUsers().contains( val ) ){
                        verifyModelService.getBUsers().add( val );
                    }
                    break;
                case "role":
                    if( !verifyModelService.getBRoles().contains( val ) ){
                        verifyModelService.getBRoles().add( val );
                    }
                    break;
                case "ip":
                    if( !verifyModelService.getBIps().contains( val ) ){
                        verifyModelService.getBIps().add( val );
                    }
                    break;
            }
        }
    }
}
