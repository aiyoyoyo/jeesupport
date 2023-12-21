package com.jees.webs.core.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.crypto.MD5Utils;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.security.interf.IVerifyLogin;
import com.jees.webs.security.interf.IVerifySerivce;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Log4j2
@Service
public class SecurityService implements PasswordEncoder {
    public enum SecurityModel {
        NONE,
        LOCAL,
        DATABASE,
        MIXED;

        public static SecurityModel cast(String _val) {
            switch (_val) {
                case "mixed":
                    return MIXED;
                case "database":
                    return DATABASE;
                case "local":
                    return LOCAL;
                case "none":
                default:
                    return NONE;
            }
        }
    }

    boolean encodePwd;
    boolean cross;
    SecurityModel model;

    public SecurityService() {
        this.model = SecurityModel.cast(CommonConfig.getString("jees.webs.security.model", "local"));
        this.cross = CommonConfig.getBoolean("jees.webs.security.cross", false);
        this.encodePwd = CommonConfig.getBoolean("jees.webs.security.encodePwd", true);
        log.info("权限配置方案：" + model);
    }

    public boolean isEnable() {
        return this.model != SecurityService.SecurityModel.NONE;
    }

    @SuppressWarnings("SpringElInspection")
    public void setHttpSecurity(HttpSecurity _hs) throws Exception {
        String verify_service = CommonConfig.getString("jees.webs.security.verifyService", "verifyService");
        IVerifySerivce verifyService = CommonContextHolder.getBean(verify_service);
        verifyService.initialize(this.model);
        String login_page = "/" + CommonConfig.getString("jees.webs.security.login", "login");
        String logout_page = "/" + CommonConfig.getString("jees.webs.security.logout", "logout");

        SecurityManagerService service = CommonContextHolder.getBean(SecurityManagerService.class);
        Objects.requireNonNull(service);
        _hs.authorizeRequests()
                .and().logout().logoutUrl(logout_page)
                .addLogoutHandler(new SecurityContextLogoutHandler())
                .logoutSuccessHandler(service.getSecurityLogoutSuccessHandler())
                .logoutSuccessUrl(login_page)
                .permitAll()
                .and().formLogin().loginPage(login_page)
                .successHandler(service.getSecurityAuthenticationSuccessHandler())
                .failureHandler(service.getSecurityAuthenticationFailureHandler()).permitAll()
                .and().authorizeRequests().anyRequest().access("@" + verify_service + ".validate(request, authentication )")
                .and().exceptionHandling().accessDeniedHandler(service.getAccessDeniedHandler())
        ;
    }

    public String encodePwd(String _pwd) {
        if (this.encodePwd) {
            _pwd = MD5Utils.s_encode(_pwd);
        }
        return _pwd;
    }

    @Override
    public String encode(CharSequence _pwd) {
        return this.encodePwd((String) _pwd);
    }

    @Override
    public boolean matches(CharSequence _pwd, String _encode) {
        boolean match = false;
        switch (this.model) {
            case LOCAL:
                match = _encode.equals(this.encodePwd((String) _pwd));
                break;
            case DATABASE:
                // 数据库验证
                break;
            case MIXED:
                match = _encode.equals(this.encodePwd((String) _pwd));
                if (match) {
                    // 数据库验证
                    log.warn("数据验证未实现！");
                }
                break;
            case NONE:
            default:
                // 无验证
                break;
        }
        return match;
    }

    public UserDetails thirdMatches(String _username, CharSequence _pwd) {
        boolean match = false;
        // 第三方验证
        boolean third = CommonConfig.get("jees.webs.security.third.enable", false);
        if (third) {
            IVerifyLogin login_impl = CommonContextHolder.getBean(IVerifyLogin.class);
            if (login_impl != null) {
                match = login_impl.matches(_username, _pwd);
            }
        }
        SuperUser user = null;
        if (match) {
            String role = CommonConfig.get("jees.webs.security.third.role", "ThirdUser");
            user = new SuperUser();
            user.setUsername(_username);
            user.setPassword(_pwd.toString());
            user.setThirdUser(true);
            user.getAuthorities().add(new SimpleGrantedAuthority(role));
        }
        return user;
    }

    @Override
    public boolean upgradeEncoding(String _encode) {
        return false;
    }
}
