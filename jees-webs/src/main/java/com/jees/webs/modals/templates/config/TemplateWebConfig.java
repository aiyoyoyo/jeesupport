package com.jees.webs.modals.templates.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.utils.UrlUtil;
import com.jees.webs.modals.install.interf.IInstallModel;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.modals.templates.struct.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * 配置页面文件访问规则
 */
@Log4j2
@Configuration
public class TemplateWebConfig implements WebMvcConfigurer {
    /**
     * 拦截器
     *
     * @param _registry 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry _registry) {
        InterceptorRegistration R = _registry.addInterceptor(Objects.requireNonNull(CommonContextHolder.getBean(HandlerInterceptor.class))).addPathPatterns("/**");
        StringTokenizer st = CommonConfig.getStringTokenizer("spring.resources.static-locations");
        while (st.hasMoreTokens()) {
            String url = UrlUtil.path2url(st.nextToken(), false);
            if (url != null) R.excludePathPatterns("/" + url + "/**");
        }
        TemplateService templateService = CommonContextHolder.getBean(TemplateService.class);
        Objects.requireNonNull(templateService).getTemplateAll().forEach(t -> {
            if (templateService.isTemplate(t.getName())) {
                R.excludePathPatterns("/" + t.getAssets() + "/**");
            }
            R.excludePathPatterns("/" + t.getName() + "/" + t.getAssets() + "/**");
        });
    }

    /**
     * 这里根据模版设置，注册静态资源
     *
     * @param _registry 拦截器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry _registry) {
        // 此处的静态根路径不能和模版中静态目录assets重名
        StringTokenizer st = CommonConfig.getStringTokenizer("spring.web.resources.static-locations");
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            String url = UrlUtil.path2url(path, false);
            if (url != null) {
                log.debug("--注册静态资源：RES=[/**], PATH=[{}]", path);
                _registry.addResourceHandler("/**").addResourceLocations(path);
            }
        }
        TemplateService templateService = CommonContextHolder.getBean(TemplateService.class);
        Objects.requireNonNull(templateService).getTemplateAll().forEach(t -> {
            String url = t.getName() + "/" + t.getAssets();
            String ass_path = t.getTemplatePath() + "/" + t.getAssets() + "/";
            url = url + "/**";
            if (templateService.isDefault(t.getName())) {
                // 将默认模板路径指定为根路径
                log.debug("--注册静态资源：RES=[{}/**], PATH=[{}]", t.getAssets(), ass_path);
                _registry.addResourceHandler(t.getAssets() + "/**").addResourceLocations(ass_path);
            }
            log.debug("--注册静态资源：RES=[{}], PATH=[{}]", url, ass_path);
            _registry.addResourceHandler(url).addResourceLocations(ass_path);
            // 这段主要作用是将自定义路径下的内容作为资源文件访问
            String security_suffix = CommonConfig.get("jees.webs.security.suffix", "");

            Collection<Page> pages = templateService.getTemplatePages(t);
            String index_page = CommonConfig.getString("jees.webs.modals.templates.index", "index");
            String page_suffix = CommonConfig.getString("spring.thymeleaf.suffix", ".html");
            if (templateService.isDefault(t.getName())) {
                pages.forEach(p -> {
                    if (!p.getPath().endsWith(index_page + page_suffix)) {
                        String res_url;
                        String path = p.getPath();
                        if (!path.endsWith(page_suffix)) {
                            // 自定义资源路径，且只有符合要求的才认为是资源文件
                            res_url =  path.replace(t.getName() + "/", "");
                            path = t.getTemplatePath().replace(t.getName(), "") + path.replace(p.getFilename(),"");
                            if( security_suffix.equals( ".*" ) || security_suffix.contains(p.getFileSuffix()) ){
                                log.debug("--注册静态资源：RES=[{}], PATH=[{}]", res_url, path);
                                _registry.addResourceHandler(res_url.replace(p.getFilename(),"**")).addResourceLocations(path);
                            }
                        }
                    }
                });
            }
            pages.forEach(p -> {
                String path = p.getPath();
                if (!path.endsWith(index_page + page_suffix) && !path.endsWith(page_suffix)) {
                    String res_url = path;
                    path = t.getTemplatePath().replace(t.getName(), "") + path.replace(p.getFilename(),"");;
                    // 自定义资源路径，且只有符合要求的才认为是资源文件
                    if( security_suffix.equals( ".*" ) || security_suffix.contains(p.getFileSuffix()) ){
                        log.debug("--注册静态资源：RES=[{}], PATH=[{}]", res_url, path);
                        _registry.addResourceHandler(res_url).addResourceLocations(path);
                    }
                }
            });
        });
    }

    /**
     * 根据模版文件夹内的文件，加载页面及对应URL映射
     *
     * @param _registry 拦截器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry _registry) {
        TemplateService templateService = CommonContextHolder.getBean(TemplateService.class);
        Objects.requireNonNull(templateService).getTemplateAll().forEach(t -> {
            Collection<Page> pages = templateService.getTemplatePages(t);
            String index_page = CommonConfig.getString("jees.webs.modals.templates.index", "index");
            String page_suffix = CommonConfig.getString("spring.thymeleaf.suffix", ".html");
            String security_suffix = CommonConfig.get("jees.webs.security.suffix", "");
            if (templateService.isDefault(t.getName())) {
                pages.forEach(p -> {
                    if (p.getPath().endsWith(index_page + page_suffix)) {
                        String path = p.getPath().replace(page_suffix, "");
                        String url = p.getUrl().replace(index_page, "");
                        url = url.replace(t.getName(), "");
                        log.debug("--映射访问路径：URL=[{}], PATH=[{}]", url, path);
                        _registry.addViewController(url).setViewName(path);
                    } else {
                        String url = p.getUrl().replace(t.getName(), "");
                        String path = p.getPath();
                        // 不能映射资源路径，会导致加载变成文档而不是资源
                        if( path.endsWith( page_suffix ) ){
                            log.debug("--映射访问路径：URL=[{}], PATH=[{}]", url, path);
                            _registry.addViewController(url).setViewName(path);
                        }
                    }
                });
                String path = t.getName() + "/" + index_page + page_suffix;
                log.debug("--映射访问路径：URL=[/], PATH=[{}]", path);
                _registry.addViewController("/").setViewName(path);
            }
            pages.forEach(p -> {
                String path = p.getPath();
                if (path.endsWith(index_page + page_suffix)) {
                    path = path.replace(page_suffix, "");
                    String url = "/" + p.getUrl().replace(index_page, "");
                    log.debug("--映射访问路径：URL=[{}], PATH=[{}]", url, path);
                    _registry.addViewController(url).setViewName(path);
                } else {
                    if( path.endsWith( page_suffix ) ){
                        log.debug("--映射访问路径：URL=[{}], PATH=[{}]", p.getUrl(), p.getPath());
                        _registry.addViewController(p.getUrl()).setViewName(p.getPath());
                    }
                }
            });
        });

        IInstallModel iIM = CommonContextHolder.getBean(IInstallModel.class);
        if (iIM != null && !iIM.isFinish()) {
            iIM.setViewController(_registry);
        }
    }
}
