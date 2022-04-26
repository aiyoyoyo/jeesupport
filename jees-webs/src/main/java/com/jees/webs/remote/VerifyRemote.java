package com.jees.webs.remote;

import com.jees.common.CommonConfig;
import com.jees.webs.abs.AbsSuperService;
import com.jees.webs.abs.AbsVerifyService;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.IAccessService;
import com.jees.webs.support.ISupportEL;
import com.jees.webs.verify.AccessImpl;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Log4j2
@RemoteProxy
public class VerifyRemote implements ISupportEL {

    @Autowired
    AbsSuperService ASS;
    @Autowired
    AbsVerifyService AVS;
    @Autowired
    AccessImpl AI;



    @RemoteMethod
    public List<SuperUser> loadUsers() {
        return AVS.mergeUsers();
    }

    @RemoteMethod
    public List<SuperRole> loadRoles() {
        return AVS.mergeRoles();
    }

    @RemoteMethod
    public List<SuperMenu> loadMenus() {
        return AVS.mergeMenus();
    }

    @RemoteMethod
    public Map<String, Object> check(String _path) throws Exception {
        boolean allow = true;
        if (CommonConfig.getBoolean("jees.webs.verify.enable", false)) {
            WebContext wc = WebContextFactory.get();
            HttpSession session = wc.getSession();
            HttpServletRequest request = wc.getHttpServletRequest();
            SuperUser user = (SuperUser) session.getAttribute("USER");
            Collection<? extends GrantedAuthority> auths = user.getAuthorities();
            String ip = request.getRemoteAddr();
            String name = user.getUsername();
            Set<SuperMenu> menus = user.getMenus();
            log.debug("--vue router check");
            boolean allow_path = AI.allowMenu(_path, menus);
            boolean allow_ip = AI.allowIP(ip, null);
            boolean allow_user = AI.allowUser(name, null);
            if (!allow_path || !allow_ip || !allow_user) allow = false;
        }
        boolean finalAllow = allow;
        return new HashMap<String, Object>() {
            {
                put("allow", finalAllow);
            }
        };
    }

}
