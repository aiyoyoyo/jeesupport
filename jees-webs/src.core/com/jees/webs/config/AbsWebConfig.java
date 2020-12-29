package com.jees.webs.config;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.UrlUtil;
import com.jees.webs.entity.Page;
import com.jees.webs.entity.SuperMenu;
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
import java.util.StringTokenizer;

@Log4j2
public abstract class AbsWebConfig implements WebMvcConfigurer {
    public static String               defPage;
    @Autowired
    ITemplateService< SuperMenu > templateService;
    @Autowired
    ResourcePatternResolver       resourcePatternResolver;
    @Autowired
    HandlerInterceptor            handlerInterceptor;
    @Autowired
    InstallConfig                 installConfig;

    static String rootTpl;

    public String getRootTpl(){
        if( rootTpl == null)
            rootTpl = CommonConfig.getString( "spring.thymeleaf.prefix", "classpath:/templates/");
        return rootTpl;
    }

    private String _try_tpl_path(){

        String tmp_path = "";
        try {
            tmp_path = resourcePatternResolver.getResource( getRootTpl() ).getURL().getPath();
            if( tmp_path.startsWith( "/" ) )tmp_path = tmp_path.replaceFirst( "/", "" );
        } catch (IOException e) {
            log.error( "模版路径错误：PATH=[" + getRootTpl() + "]" );
        }

        return tmp_path;
    }

    private List<Page> _load_template_pages(){
        List<Page> view_pages = new ArrayList<>();
        if( defPage == null ) defPage = CommonConfig.getString( "jees.webs.defPage", "index" );

        String root_path = _try_tpl_path();
        templateService.getTemplateAll().forEach( t -> {
            String tpl_path = getRootTpl() + t.getName();
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
                    if( templateService.isDefault( tpl ) ) url = url.replace( "/" + templateService.getDefaultTemplate().getName(), "" );
                    Page p = new Page( url, path, tpl );
                    t.addPage( p );
                }
            } catch (IOException e) {
                log.warn( "模版加载失败：" + tpl_path );
            }

            view_pages.addAll( t.getPages().values() );
        } );
        return view_pages;
    }

    private List<Page> _load_install_pages(){
        List<Page> view_pages = new ArrayList<>();
        String root_path = CommonConfig.getString( "spring.thymeleaf.prefix" );
        String install_path = CommonConfig.getString( "jees.webs.install.tpl", "install" );
        String res_path = root_path + install_path + "/**/*.html";
        try {
            Resource[] rfs = resourcePatternResolver.getResources( res_path );
            for( int i = 0; i < rfs.length; i ++ ){
                Resource r = rfs[i];

                String r_path = r.getURI().getPath();
                int idx = r_path.lastIndexOf( "/" );
                String file = r_path.substring( idx + 1 );
                String url = install_path + "/" + file.replace( ".html", "" );
                String path = url;

                Page page = new Page( url, path, install_path );
                view_pages.add( page );
            }
        } catch (IOException e) {
            log.warn( "安装系统模版加载失败：" + res_path );
        }
        return view_pages;
    }

    /**
     * 拦截器
     * @param _registry 拦截器
     */
    @Override
    public void addInterceptors( InterceptorRegistry _registry) {
        InterceptorRegistration R = _registry.addInterceptor( handlerInterceptor ).addPathPatterns( "/**" );

        StringTokenizer st = CommonConfig.getStringTokenizer("spring.resources.static-locations");
        while( st.hasMoreTokens() ){
            String url = UrlUtil.path2url( st.nextToken(), false );
            if( url != null ) R.excludePathPatterns( "/" + url + "/**" );
        }

        templateService.getTemplateAll().forEach( t -> {
            if( templateService.isTemplate( t.getName() ) ){
                R.excludePathPatterns( "/" + t.getAssets() + "/**" );
            }

            R.excludePathPatterns( "/" + t.getName() + "/" + t.getAssets() + "/**" );
        } );
    }

    /**
     * 这里根据模版设置，注册静态资源
     * @param _registry 拦截器
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

        // 此处的静态根路径不能和模版中静态目录assets重名
        StringTokenizer st = CommonConfig.getStringTokenizer("spring.resources.static-locations");
        ResourceHandlerRegistration handler = null;
        while( st.hasMoreTokens() ){
            String path = st.nextToken();
            String url = UrlUtil.path2url( path, false );
            if( url != null ){
                log.debug( "--注册静态资源：RES=[" + url + "], PATH=[" + path + "]" );
                _registry.addResourceHandler(url + "/**").addResourceLocations( path );
            }
        }
    }

    /**
     * 根据模版文件夹内的文件，加载页面及对应URL映射
     * @param _registry 拦截器
     */
    @Override
    public void addViewControllers( ViewControllerRegistry _registry ) {
        List<Page> list = _load_template_pages();
        list.forEach( p -> _registry.addViewController( p.getUrl() ).setViewName( p.getFile() ) );

        if( !installConfig.isInstalled() ){
            list = _load_install_pages();
            list.forEach( p -> _registry.addViewController( p.getUrl() ).setViewName( p.getFile() ) );

            if( list.size() > 0 ){
                Page p = list.get( 0 );
                _registry.addViewController( "" )
                        .setViewName( p.getFile() );
                _registry.addViewController( "/" + CommonConfig.getString( "jees.webs.install.tpl", "install" ) )
                        .setViewName( p.getFile() );
            }
        }
    }
}
