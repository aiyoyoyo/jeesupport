package com.jees.webs.security.service;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.FileUtil;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.struct.PageAccess;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Service
public class VerifyModelService {

    Map<String, SuperUser> users = new ConcurrentHashMap<>();
    Map<String, SuperRole> roles = new ConcurrentHashMap<>();
    Map<String, PageAccess> auths = new ConcurrentHashMap<>();

    public void initialize(SecurityService.SecurityModel _model){
        switch ( _model ){
            case LOCAL:
                this.loadConfig();
            break;
        }

    }

    public void loadConfig(){
        _load_config_line( null );
    }

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
                is_read_auth.set( false );
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
            }
            if(is_read_auth.get()){
                if( _node == null || _node.equalsIgnoreCase( "auth" ) ) {
                    _load_config_line_auth( _line );
                }
            }
            if(is_read_user.get()){
                if( _node == null || _node.equalsIgnoreCase( "users" ) ) {
                    _load_config_line_users( _line );
                }
            }
            if(is_read_role.get()){
                if( _node == null || _node.equalsIgnoreCase( "role" ) ) {
                    _load_config_line_roles( _line );
                }
            }
        } );
    }

    String lastAuth = null;
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

        PageAccess page = this.auths.get(auth_url);
        if (page == null) {
            page = new PageAccess();
            page.setUrl(auth_url);
            this.auths.put(auth_url, page);
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
        String[] auth_str = _line.split("=");
        if( auth_str.length == 1 ) return;
        String[] auth_data = auth_str[1].split(",");
        switch (auth_str[0].trim()) {
            case "role":
                _page.addRoles(auth_data);
                break;
            case "deny":
                _page.addDenys(auth_data);
                break;
            case "user":
                _page.addUsers(auth_data);
                break;
        }
    }
    /**
     * 读取用户行内容
     * @param _line
     */
    private void _load_config_line_users( String _line ){
        String[] user_str = _line.split( "=" );
        if( user_str.length == 1 ) return;
        SuperUser user = new SuperUser();
        user.setEnabled( true );
        user.setLocked( false );
        user.setUsername( user_str[0].trim() );
        user.setPassword( user_str[1].trim() );
        this.users.put( user.getUsername().toLowerCase(), user );
    }

    /**
     * 读取角色行内容
     * @param _line
     */
    private void _load_config_line_roles( String _line ){
        // 角色 = 用户1, ..., 用户N
        String[] role_str = _line.split( "=" );
        if( role_str.length == 1 ) return;
        String[] role_user_str = role_str[1].split(",");

        SuperRole role = new SuperRole();
        role.setId( this.roles.size() );
        role.setName( role_str[0].trim() );

        Set<String> users = new HashSet<>();
        role.setUsers( users );
        for( String role_user : role_user_str ){
            if( !users.contains( role_user ) ){
                users.add( role_user );
            }
        }

        for( String role_user : role_user_str ) {
            SuperUser user = this.findUserByUsername(role_user.trim());
            if (user == null) {
                log.warn("角色[" + role_str[0] + "]未找到相关用户[" + role_user + "]信息!");
                return;
            }else{
                user.addRole( role );
            }
        }
    }

    public SuperUser findUserByUsername( String _username ) {
        return this.users.getOrDefault( _username.trim().toLowerCase(), null );
    }

    public boolean velidateRequest(HttpServletRequest request, Authentication authentication) {
        String uri = request.getRequestURI();
        Object principal = authentication.getPrincipal();

        PageAccess page = this.auths.get( uri );

        boolean is_super = false;
        boolean is_deny = false;
        boolean is_auth = false;

        if( principal instanceof SuperUser ){
            SuperUser user = (SuperUser) principal;
            if( page.getUsers().contains( "*" ) || page.getUsers().contains( user.getUsername() ) ){
                is_auth = true;
            }
            if( page.getDenys().contains( "*" ) || page.getDenys().contains( user.getUsername() ) ){
                is_deny = true;
            }

            Iterator<SimpleGrantedAuthority> auth_it = user.getAuthorities().iterator();
            while ( auth_it.hasNext() ){
                SimpleGrantedAuthority auth = auth_it.next();
                String user_auth = auth.getAuthority();
                if( user_auth.equalsIgnoreCase( "super" ) ){
                    is_super = true;
                }
                if( page.getRoles().contains( "*" ) || page.getRoles().contains( user_auth ) ){
                    is_auth = true;
                }
            }
        }
        //TODO 可以用错误码代替
        if( is_super ){
            return true;
        }
        if( is_deny ){
            return false;
        }
        if( is_auth ){
            return true;
        }
        return false;
    }
}
