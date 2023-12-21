package com.jees.webs.modals.templates.config;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.tool.utils.UrlUtil;
import com.jees.webs.modals.install.interf.IInstallModel;
import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.modals.templates.struct.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

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
        StringTokenizer st = CommonConfig.getStringTokenizer("spring.resources.static-locations");
        while (st.hasMoreTokens()) {
            String path = st.nextToken();
            String url = UrlUtil.path2url(path, false);
            if (url != null) {
                log.debug("--注册静态资源：RES=[" + url + "], PATH=[" + path + "]");
                _registry.addResourceHandler(url + "/**").addResourceLocations(path);
            }
        }
        TemplateService templateService = CommonContextHolder.getBean(TemplateService.class);
        Objects.requireNonNull(templateService).getTemplateAll().forEach(t -> {
            String url = t.getName() + "/" + t.getAssets();
            String ass_path = t.getTemplatePath() + "/" + t.getAssets() + "/";
            log.debug("--注册静态资源：RES=[" + url + "], PATH=[" + ass_path + "]");
            if (templateService.isDefault(t.getName())) {
                // 将默认模板路径指定为根路径
                _registry.addResourceHandler(t.getAssets() + "/**").addResourceLocations(ass_path);
            }
            _registry.addResourceHandler(url + "/**").addResourceLocations(ass_path);
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
            if (templateService.isDefault(t.getName())) {
                pages.forEach(p -> {
                    if (p.getPath().endsWith(index_page + page_suffix)) {
                        String path = p.getPath().replace(page_suffix, "");
                        String url = p.getUrl().replace(index_page, "");
                        _registry.addViewController(url.replace(t.getName(), "")).setViewName(path);
                    } else {
                        _registry.addViewController(p.getUrl().replace(t.getName(), "")).setViewName(p.getPath());
                    }
                });
                _registry.addViewController("/").setViewName(t.getName() + "/" + index_page + page_suffix);
            }
            pages.forEach(p -> {
                if (p.getPath().endsWith(index_page + page_suffix)) {
                    String path = p.getPath().replace(page_suffix, "");
                    String url = p.getUrl().replace(index_page, "");
                    _registry.addViewController(url).setViewName(path);
                } else {
                    _registry.addViewController(p.getUrl()).setViewName(p.getPath());
                }
            });
        });

        IInstallModel iIM = CommonContextHolder.getBean(IInstallModel.class);
        if (iIM != null && !iIM.isFinish()) {
            iIM.setViewController(_registry);
        }
    }
}
