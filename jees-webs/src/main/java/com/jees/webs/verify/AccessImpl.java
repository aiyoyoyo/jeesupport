package com.jees.webs.verify;

import com.jees.webs.abs.AbsVerifyService;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.IAccessService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义过滤规则
 * 使用：_hs.authorizeRequests().anyRequest().access("@pathAccessImpl.hasPath(request, authentication)");
 */
@Component
@Log4j2
public class AccessImpl implements IAccessService {
    @Autowired
    AbsVerifyService verifyService;

    /**
     * 校验请求URI路径
     */
    @Override
    public boolean hasPath(HttpServletRequest _request, Authentication _auth) {
        Object obj = _auth.getPrincipal();
        if (obj instanceof UserDetails) {
            SuperUser user = (SuperUser) obj;
            Set<SuperMenu> menus = user.getMenus();
//            Collection<? extends GrantedAuthority> auths = user.getAuthorities();
//            if (verifyService.getUsers().containsKey(user.getUsername())) {
//                auths = verifyService.getUsers().get(user.getUsername()).getAuthorities();
//            }
            String uri = _request.getRequestURI();
            log.debug("--校验路径：URI=[" + uri + "]");
            return allowMenu(uri, menus);
        }
        return false;
    }

    /**
     * 校验黑名单IP地址
     */
    @Override
    public boolean hasBlackIP(HttpServletRequest _request, Authentication _auth) {
        Object obj = _auth.getDetails();
        if (obj instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails web = (WebAuthenticationDetails) obj;
            String remote_ip = web.getRemoteAddress();
            return allowIP(remote_ip, _request);
        }
        return true;
    }

    /**
     * 校验黑名单用户
     */
    @Override
    public boolean hasBlackUser(HttpServletRequest _request, Authentication _auth) {
        Object obj = _auth.getPrincipal();
        if (obj instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) obj;
            String remote_name = userDetails.getUsername();
            return allowUser(remote_name, _request);
        }
        return true;
    }

    public boolean allowMenu(String _uri, Set<SuperMenu> _menus) {
        for (SuperMenu menu : _menus) {
            String url = menu.getUrl();
            if (_uri.equals(url)) return true;
            if (_uri.startsWith(url)) return true;
            if (!menu.getMenus().isEmpty()) {
                List<SuperMenu> sub_menus = menu.getMenus();
                for (SuperMenu sub_menu : sub_menus) {
                    String sub_url = sub_menu.getUrl();
                    if (_uri.equals(sub_url)) return true;
                    if (_uri.startsWith(sub_url)) return true;
                }
            }
        }
        return false;
    }

    public boolean allowPath(String _uri, Collection<? extends GrantedAuthority> _auths) {
        boolean res_flag = false;
        for (GrantedAuthority auth : _auths) {
            String a = auth.getAuthority();
            if (a.contains("/**")) {
                a = a.replace("/**", "");
                res_flag = _uri.startsWith(a);
            } else {
                res_flag = _uri.equals(a);
            }
            if (res_flag) return true;
        }
        return false;
    }

    public boolean allowIP(String _ip, HttpServletRequest _request) {
        // 遍历IP黑名单
        if (verifyService.getBlackList().containsKey("ip")) {
            boolean res_flag = true;
            String[] ips = (String[]) verifyService.getBlackList().get("ip");
            for (String ip : ips) {
                Pattern pattern = Pattern.compile(ip);
                Matcher matcher = pattern.matcher(_ip);
                if (matcher.find()) {
                    // throw new BadCredentialsException("Invalid IP");
                    if (_request != null)
                        _request.getSession().setAttribute("access_msg", "Invalid IP");
                    res_flag = false;
                }
                if (!res_flag) return false;
            }
        }
        return true;
    }

    public boolean allowUser(String _name, HttpServletRequest _request) {
        // 遍历user黑名单
        if (verifyService.getBlackList().containsKey("user")) {
            boolean res_flag = true;
            String[] users = (String[]) verifyService.getBlackList().get("user");
            for (String user : users) {
                res_flag = !user.equals(_name);
                if (_request != null)
                    _request.getSession().setAttribute("access_msg", "该用户无权限");
                if (!res_flag) return false;
            }
        }
        // 遍历role黑名单
        if (verifyService.getBlackList().containsKey("role")) {
            boolean res_flag = true;
            String[] roles = (String[]) verifyService.getBlackList().get("role");
            for (String role : roles) {
                if (verifyService.getRoleList().containsKey(role)) {
                    String[] users = (String[]) verifyService.getRoleList().get(role);
                    for (String user : users) {
                        res_flag = !user.equals(_name);
                    }
                }
                if (_request != null)
                    _request.getSession().setAttribute("access_msg", "该角色无权限");
                if (!res_flag) return false;
            }
        }
        return true;
    }

}
