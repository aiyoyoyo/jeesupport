package com.jees.webs.security.service;

import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.configs.LocalConfig;
import com.jees.webs.security.struct.PageAccess;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易的授权服务
 */
@Log4j2
@Service
public class VerifyModelService {

    // 用户授权信息
    @Getter
    Map<String, SuperUser> users = new ConcurrentHashMap<>();
    // 角色授权信息
    @Getter
    Map<String, SuperRole> roles = new ConcurrentHashMap<>();
    // 页面授权信息
    @Getter
    Map<String, PageAccess> auths = new ConcurrentHashMap<>();

    LocalConfig localConfig;
    public void initialize(SecurityService.SecurityModel _model){
        switch ( _model ){
            case LOCAL: // 本地文件配置方案
                localConfig = CommonContextHolder.getBean( LocalConfig.class );
                localConfig.initialize();
            break;
        }

    }

    public SuperUser findUserByUsername( String _username ) {
        return this.users.getOrDefault( _username.trim().toLowerCase(), null );
    }

    /**
     * 判断用户是否可以访问
     * 超级管理员>IP黑名单>允许匿名>全局账号黑名单>全局角色黑名单>页面账号黑名单>页面账号>页面角色>用户全允许
     * @param request
     * @param authentication
     * @return
     */
    public boolean velidateRequest(HttpServletRequest request, Authentication authentication) {
        String uri = request.getRequestURI();
        Object principal = authentication.getPrincipal();

        PageAccess page = this.auths.get( uri );
        if( page == null ){
            return false;
        }
        boolean is_super = false;
        boolean is_deny = false;
        boolean is_auth = false;
        boolean is_anonymous = false;

        if( principal instanceof SuperUser ){
            SuperUser user = (SuperUser) principal;
            if( page.getUsers().contains( "*" ) || page.getUsers().contains( user.getUsername() ) ){
                is_auth = true;
            }
            if( page.getDenys().contains( "*" ) || page.getDenys().contains( user.getUsername() ) ){
                is_deny = true;
            }
            if( page.isAnonymous() ){
                is_deny = false;
                is_auth = false;
                is_anonymous = true;
            }
            Iterator<SimpleGrantedAuthority> auth_it = user.getAuthorities().iterator();
            while ( auth_it.hasNext() ){
                SimpleGrantedAuthority auth = auth_it.next();
                String user_auth = auth.getAuthority();
                if( user_auth.equalsIgnoreCase(ISupportEL.ROLE_SUPERMAN)
                        || user_auth.equalsIgnoreCase(ISupportEL.ROLE_ADMIN) ){
                    is_super = true;
                }
                if( page.getRoles().contains( "*" ) || page.getRoles().contains( user_auth ) ){
                    is_auth = true;
                }
            }
        }
        //TODO 可以用错误码代替
        //超级管理员>IP黑名单>允许匿名>全局账号黑名单>全局角色黑名单>页面账号黑名单>页面账号>页面角色>用户全允许
        if( is_super ){
            return true;
        }
        if( is_anonymous ){
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

    /**
     * 验证元素授权信息
     * @param _request
     * @param _tag
     * @param _validate
     * @return
     */
    public boolean validateElement(HttpServletRequest _request, IProcessableElementTag _tag, String _validate ){
        if( _validate != null && _validate.equalsIgnoreCase("false") ) return true;

        String uri = _request.getRequestURI();
        PageAccess page = this.auths.get( uri );
        if( page == null ){
            return false;
        }

        boolean success = false;
        // 从session中提取登录信息
        SessionInformation session = CommonContextHolder.getBean(SessionRegistry.class).getSessionInformation(_request.getSession().getId());

        SuperUser user = (SuperUser) session.getPrincipal();
        String user_name = user.getUsername();
        Set<SimpleGrantedAuthority> user_roles = user.getAuthorities();
        // 判断ID授权
        success = _verify_page_attribute( _tag, "id", "#", page, user_name, user_roles );
        // 判断样式授权
        success = _verify_page_attribute( _tag, "class", ".", page, user_name, user_roles );
        // 正常都通过才会是true
        return success;
    }

    /**
     * 验证ID、CLASS授权的通用逻辑
     * @param _tag
     * @param _attr
     * @param _lab
     * @param _page
     * @param _user
     * @param _roles
     * @return
     */
    private boolean _verify_page_attribute( IProcessableElementTag _tag, String _attr, String _lab, PageAccess _page, String _user, Set<SimpleGrantedAuthority> _roles ){
        boolean result = false;
        IAttribute attr = _tag.getAttribute(_attr);
        if( attr != null ) {
            String[] values = attr.getValue().split(" ");
            for (String value : values) {
                result = _verify_page_elements( _page, _lab + value, _user, _roles );
                if( !result ){
                    return false;
                }
            }
        }
        return result;
    }

    /**
     * 根据标记名来判断是否授权，多个授权，有一个没有授权则立即返回没有权限
     * @param _page
     * @param _el
     * @param _user
     * @param _roles
     * @return
     */
    private boolean _verify_page_elements( PageAccess _page, String _el, String _user, Set<SimpleGrantedAuthority> _roles ){
        PageAccess pa = _page.getElPage( _el );
        boolean deny_has = false;
        boolean user_has = false;
        boolean role_has = false;
        if (pa != null) {
            if (pa.getDenys().contains( _user ) ) {
                deny_has = true;
            }
            for( SimpleGrantedAuthority role : _roles ){
                if( pa.getDenys().contains( role.getAuthority() ) ){
                    deny_has = true;
                    break;
                }
            }
            if (pa.getUsers().contains( _user )) {
                user_has = true;
            }
            for( SimpleGrantedAuthority role : _roles ){
                if( pa.getRoles().contains( role.getAuthority() )
                        || role.getAuthority().equalsIgnoreCase(ISupportEL.ROLE_ADMIN)
                        || role.getAuthority().equalsIgnoreCase(ISupportEL.ROLE_SUPERMAN)
                ){
                    role_has = true;
                    break;
                }
            }
        }else{
            deny_has = true;
        }
        // 如果黑名单有，则没有权限
        return deny_has ? false : ( user_has || role_has );
    }
}
