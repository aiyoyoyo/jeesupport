package com.jees.webs.controller;

import com.jees.common.CommonConfig;
import com.jees.common.CommonLogger;
import com.jees.webs.support.AbsControllerService;
import com.jees.webs.support.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过路径控制器，配对URL和文件路径。登陆和登出为固定入口。
 * @author aiyoyoyo
 */
public abstract class PathController extends AbsControllerService {
    public static String    defPage;

    @Autowired
    ITemplateService    templateService;
    @Autowired
    SessionRegistry     sessionRegistry;

    private String _get_tpl_path(String _tpl, HttpServletRequest _request ) {
        String use_template = "";

        use_template = templateService.getTemplateAndTheme( _tpl, _request );
        CommonLogger.getLogger( this.getClass() ).debug( "--访问模版：TPL=[" + _tpl + "], RT=[" + use_template + "]" );

        return use_template;
    }

    private String _filter_path( String _tpl, String _uri ){
        if( defPage == null ) defPage = CommonConfig.getString( "jees.webs.def-page", "index" );
        String path = "";
        CommonLogger.getLogger( this.getClass() ).debug( "--访问模版地址：PATH=[" + path + "]"  );
        if( _uri.isEmpty() || _uri.equals( "/" )  ) path = _tpl + "/" + defPage;
        else path = _tpl + _uri;

        CommonLogger.getLogger( this.getClass() ).debug( "--访问模版地址：PATH=[" + path + "]"  );
        return path;
    }

    @RequestMapping( "/" )
    String index( HttpServletRequest _request ){
        String uri = _request.getRequestURI();
        String path = _get_tpl_path( templateService.getDefaultTemplate().getName(), _request );

        return _filter_path( path, uri );
    }

    @RequestMapping( "/{_tpl}" )
    String tpl( @PathVariable String _tpl, HttpServletRequest _request ){
        String uri = _request.getRequestURI();
        String return_url = "";
        CommonLogger.getLogger( this.getClass() ).debug( "--使用路径访问模版：TPL=[" + _tpl + "], URI=[" + uri + "]" );

        if( templateService.isTemplate( _tpl ) ){
            if( uri.startsWith( "/" + _tpl ) ) uri = uri.substring( _tpl.length() + 1 );
        }else{
            _tpl = templateService.getDefaultTemplate().getName();
        }

        return_url = _filter_path( _get_tpl_path( _tpl, _request ), uri );

        return return_url;
    }

    @RequestMapping( "/logout" )
    String logout( HttpServletRequest _request, HttpServletResponse _response ){
        CommonLogger.getLogger( this.getClass() ).debug( "--用户登出" );

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null)
            new SecurityContextLogoutHandler().logout( _request, _response, auth);

        sessionRegistry.removeSessionInformation( _request.getSession().getId() );

        String path = _get_tpl_path( templateService.getDefaultTemplate().getName(), _request );

        return _filter_path( path, "" );
    }

    @RequestMapping( "/${jees.webs.login}")
    String login( HttpServletRequest _request, HttpServletResponse _response ){
        if( sessionRegistry.getSessionInformation( _request.getSession().getId() ) != null ){
            CommonLogger.getLogger( this.getClass() ).debug( "--用户已处于登陆状态。");
            return index( _request );
        }

        String path = _get_tpl_path( templateService.getDefaultTemplate().getName(), _request );

        return _filter_path( path, "/" + CommonConfig.getString("jees.webs.login", "login") );
    }
}
