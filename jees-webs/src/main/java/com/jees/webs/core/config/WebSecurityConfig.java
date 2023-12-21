package com.jees.webs.core.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.service.ExUserDetailsService;
import com.jees.webs.core.service.SecurityService;
import com.jees.webs.modals.dwr.config.DwrConfig;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.security.service.AuthenticationProviderService;
import com.jees.webs.security.service.SecurityManagerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;

/**
 * Spring security核心配置项
 *
 * @author aiyoyoyo
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "rawtypes"})
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class WebSecurityConfig {
    @Autowired
    ExUserDetailsService userDetailsService;
    @Autowired
    DwrConfig dwrConfig;
    @Autowired
    SecurityService securityService;
    @Autowired
    AuthenticationProviderService authenticationProviderService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity _hs) throws Exception {
        if (CommonConfig.getBoolean("jees.webs.security.header.frameOptions", false)) {
            _hs.headers().frameOptions().disable();
        }
        _hs.sessionManagement().maximumSessions(CommonConfig.getInteger("jees.webs.maxSession", 1000))
                .sessionRegistry(Objects.requireNonNull(CommonContextHolder.getBean(SecurityManagerService.class)).getSessionRegistry());

        if (CommonConfig.getBoolean("jees.webs.security.cross", false)) {
            _hs.csrf().ignoringAntMatchers("/**");
        } else {
            _hs.csrf().disable();
        }

        dwrConfig.setHttpSecurity(_hs);
        if (securityService.isEnable()) {
            TemplateService templateService = CommonContextHolder.getBean( TemplateService.class );
            Objects.requireNonNull(templateService).setHttpSecurity(_hs);
            securityService.setHttpSecurity(_hs);
        } else {
            log.warn("服务器未启用安全访问，默认可以访问所有页面文件！");
            _hs.authorizeRequests().antMatchers("/**").permitAll();
        }
        return _hs.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity _hs) throws Exception {
        return _hs.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(securityService)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(AuthenticationManager _authMgr, HttpSecurity _hs) throws Exception {
        //使用自定义验证
        SecurityFilterChain sfc = _hs.authenticationProvider(authenticationProviderService).build();
        return sfc;
    }
}