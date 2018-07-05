package com.jees.webs.config;

import com.jees.common.CommonConfig;
import com.jees.webs.entity.Page;
import com.jees.webs.support.ITemplateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class AbsWebConfig implements WebMvcConfigurer {
    public static String               defPage;
    @Autowired
    ITemplateService            templateService;
    @Autowired
    ResourcePatternResolver     resourcePatternResolver;
    @Autowired
    HandlerInterceptor          handlerInterceptor;

    private List<Page> _load_template_2_page(){
        List<Page> view_pages = new ArrayList<>();
        if( defPage == null ) defPage = CommonConfig.getString( "jees.webs.defPage", "index" );

        String root_tpl = "classpath:/templates/";

        String tmp_path = "";
        try {
            tmp_path = resourcePatternResolver.getResource( root_tpl ).getURL().getPath();
        } catch (IOException e) {
            log.error( "模版路径错误：PATH=[" + root_tpl + "]" );
        }

        String root_path = tmp_path;
        templateService.getTemplateAll().forEach( t -> {
            String tpl_path = root_tpl + t.getName();
            String res_path = tpl_path + "/**/*.html";

            try {
                Resource[] rfs = resourcePatternResolver.getResources( res_path );
                for( Resource r : rfs ){
                    String r_path = r.getURI().getPath();

                    if( r_path.indexOf( "/" + t.getAssets() + "/" ) != -1 ) continue;
                    if( r_path.startsWith("_") ) continue;
                    if( r_path.indexOf("/_") != -1 ) continue;

                    r_path = r_path.replace( root_path , "" );

                    int b_idx = r_path.indexOf( "/" );
                    int l_idx = r_path.lastIndexOf( "/" );
                    String tpl = r_path.substring(0, b_idx );
                    String file = r_path.substring( l_idx + 1 );
                    String url = r_path.replace( tpl, "" ).replace( file, "" );
                    String path = r_path.replace( ".html", "" );

                    if( file.equalsIgnoreCase( defPage + ".html" ) ){
                        url = tpl + url;
                    }else{
                        url = tpl + url + file.replace(".html", "" );
                    }

                    if( templateService.isDefault( tpl ) ) url = url.replace( tpl, "" );
                    t.addPage( new Page( url, path, tpl ) );
                }
            } catch (IOException e) {
                log.warn( "模版加载失败：" + tpl_path );
            }

            view_pages.addAll( t.getPages().values() );
        } );
        return view_pages;
    }

    /**
     * 拦截器
     * @param _registry
     */
    @Override
    public void addInterceptors( InterceptorRegistry _registry) {
        InterceptorRegistration R = _registry.addInterceptor( handlerInterceptor ).addPathPatterns( "/**" );

        templateService.getTemplateAll().forEach( t -> {
            if( templateService.isTemplate( t.getName() ) ){
                R.excludePathPatterns( "/" + t.getAssets() + "/**" );
            }

            R.excludePathPatterns( "/" + t.getName() + "/" + t.getAssets() + "/**" );
        } );
    }

    /**
     * 这里根据模版设置，注册静态资源
     * @param _registry
     */
    @Override
    public void addResourceHandlers( ResourceHandlerRegistry _registry ) {
        templateService.getTemplateAll().forEach( t-> {
            String url = "/" + t.getName() + "/" + t.getAssets();
            String path = "classpath:/templates" + url + "/";
            log.debug( "--注册静态资源：RES=[" + url + "], PATH=[" + path + "]" );
            if( templateService.isDefault( t.getName() ) ){
                _registry.addResourceHandler("/" + t.getAssets() + "/**" ).addResourceLocations( path );
            }
            _registry.addResourceHandler( url + "/**" ).addResourceLocations( path );
        } );
    }

    /**
     * 根据模版文件夹内的文件，加载页面及对应URL映射
     * @param _registry
     */
    @Override
    public void addViewControllers( ViewControllerRegistry _registry ) {
        List<Page> list = _load_template_2_page();

        list.forEach( p -> {
            _registry.addViewController( p.getUrl() ).setViewName( p.getPath() );
        } );
    }
}
