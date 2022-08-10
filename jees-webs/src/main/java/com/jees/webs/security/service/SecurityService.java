package com.jees.webs.security.service;

import com.jees.common.CommonConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SecurityService {
    enum SecurityModel{
        NONE,
        LOCAL,
        DATABASE,
        MIXED;
        public static SecurityModel cast( String _val ){
            switch( _val ) {
                case "mixed": return MIXED;
                case "database": return DATABASE;
                case "local": return LOCAL;
                case "none":
                default:
                    return NONE;
            }
        }
    }

    boolean cross;
    SecurityModel model;

    public SecurityService(){
        this.model = SecurityModel.cast( CommonConfig.getString( "jees.webs.security.model", "local" ) );
        this.cross = CommonConfig.getBoolean( "jees.webs.security.cross", false );
        log.info( "权限配置方案：" + model );
    }

    public boolean isEnable(){
        return this.model != SecurityService.SecurityModel.NONE;
    }

    public void setHttpSecurity(HttpSecurity _hs) throws Exception {
        String login_page = "/" + CommonConfig.getString( "jees.webs.security.login", "login" );
        String logout_page = "/" + CommonConfig.getString( "jees.webs.security.logout", "logout" );
        _hs.authorizeRequests()
                .and().formLogin().loginPage( login_page ).loginProcessingUrl( login_page )
//                .successHandler( successHandler() ).failureHandler( failureHandler() )
                .permitAll()
//                .and().exceptionHandling().accessDeniedHandler( deniedHandler() )
                .and().logout().logoutUrl( logout_page ).permitAll()
                .and().authorizeRequests().anyRequest().access( "@accessService.validate( request, authentication )" );
    }
}
