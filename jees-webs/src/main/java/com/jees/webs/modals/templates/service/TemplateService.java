package com.jees.webs.modals.templates.service;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.struct.Page;
import com.jees.webs.modals.templates.struct.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("unused")
@Log4j2
@Service
public class TemplateService implements ISupportEL {
    /**
     * 模板对象
     */
    Map<String, Template> templates = new HashMap<>();
    String sIndexPage; // 默认首页名称
    String sRootTpl; // 模板根路径
    String sTplName; // 当前模板名称

    @Autowired
    ResourcePatternResolver loader;

    public String getRootTpl() {
        if (sRootTpl == null) {
            sRootTpl = CommonConfig.getString("spring.thymeleaf.prefix", "classpath:templates/");
            sRootTpl = sRootTpl.replaceAll("%20", " ");
            if (!sRootTpl.startsWith("classpath:")) {
                sRootTpl = "classpath:" + sRootTpl;
            }
        }
        return sRootTpl;
    }

    public TemplateService() {
        // 加载配置中的模版和主题
        StringTokenizer tpl_st = CommonConfig.getStringTokenizer("jees.webs.modals.templates.dirs");
        while (tpl_st.hasMoreTokens()) {
            String tpl = tpl_st.nextToken().trim().toLowerCase();
            Template tpl_cfg = _load_template_config(tpl);
            templates.put(tpl, tpl_cfg);
            if (sTplName == null) {
                sTplName = tpl_cfg.getName();
            }
        }

        if (sTplName == null) {
            sTplName = "default";
            Template tpl_cfg = _load_template_config(sTplName);
            templates.put(sTplName, tpl_cfg);
            log.warn("未找到有效模版，将使用默认模版路径：" + sTplName);
        }

        sIndexPage = CommonConfig.getString("jees.webs.modals.templates.index", "index");
        log.info("已加载模版：" + Arrays.toString(templates.keySet().toArray()));
    }

    /**
     * 加载模版配置
     */
    private Template _load_template_config(String _tpl) {
        Template tpl_cfg = new Template();
        tpl_cfg.setName(_tpl);
        tpl_cfg.setAssets(CommonConfig.get("jees.webs.modals.templates." + _tpl + ".assets", "assets"));
        tpl_cfg.setTemplatePath(getRootTpl() + _tpl);
        return tpl_cfg;
    }
    /**
     * 加载模板页面，排除资源路径
     * 排除以下路径: 资源路径(assets)、_[dir_name]（下划线开头文件/文件夹）
     *
     * @param _tpl   模板名即路径
     * @param _asset 模板资源文件夹名
     */
    private Map<String, Page> _load_template_pages(String _tpl, String _asset) {
        Map<String, Page> tpl_pages = new HashMap<>();
        Resource[] rfs = new Resource[0];
//        ResourcePatternResolver loader = CommonContextHolder.getBean(ResourcePatternResolver.class);
        try {
            if( loader != null ){
                rfs = loader.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "templates/"+_tpl+"/**/*.*");
            }
        } catch (IOException e) {
            log.error("模板[" + _tpl + "]加载失败：", e);
        }
        for (Resource r : rfs) {
            try {
                String r_path;
                r.getURI();
                String uri_str = r.getURI().toString();
                if (uri_str.startsWith("jar:file")) {
                    // jar包的文件
                    uri_str = uri_str.substring(uri_str.lastIndexOf("!") + 1);
                    r_path = uri_str;
                } else {
                    r_path = r.getURI().getPath();
                }
                if (r_path.contains("/" + _asset + "/")) continue;
                if (r_path.startsWith("_")) continue;
                if (r_path.contains("/_")) continue;
                if (r_path.startsWith("/")) r_path = r_path.substring(1);
                String[] path_arr = r_path.split("/");
                StringBuilder page_url = null;
                String page_file = null;
                String page_suffix = path_arr[path_arr.length - 1];

                StringBuilder page_path = null;
                for (int i = 0; i < path_arr.length; i++) {
                    String path = path_arr[i];
                    if (page_url == null && path.equalsIgnoreCase("templates")) {
                        page_url = new StringBuilder();
                        page_path = new StringBuilder();
                    } else if (page_url != null) {
                        if (i == path_arr.length - 1) { // 结尾
                            String[] tmp_path = path.split("\\.");
                            if (tmp_path.length > 1) {
                                page_path.append(path);
                                page_url.append(tmp_path[0]);
                            } else {
                                page_path.append(path).append("/").append(sIndexPage).append(".html");
                                page_url.append(tmp_path[0]);
                            }
                        } else {
                            page_url.append(path).append("/");
                            page_path.append(path).append("/");
                        }
                    }
                    page_file = path;
                }

                String url = Objects.requireNonNull(page_url).toString();
                String path = page_path.toString();
                // 修正一些url的使用问题
                if( page_suffix.contains(".html") ){
                    // 是页面路径不处理
                    page_suffix = ".html";
                }else{
                    page_suffix = "." + page_suffix.split("\\.")[1];
                    url += "/" + page_suffix;
                }
                log.debug("页面文件路径:{}->{}", url, path);
                Page page = new Page();
                page.setTpl(_tpl);
                page.setUrl(url);
                page.setPath(path);
                page.setFilepath(r_path);
                page.setFilename(page_file);
                page.setFileSuffix(page_suffix);

                tpl_pages.put(page.getUrl(), page);
            } catch (Exception e) {
                log.error("模板[" + _tpl + "]页面加载失败：", e);
            }
        }
        return tpl_pages;
    }

    public Template getTemplate(String _tpl) {
        return templates.get(_tpl);
    }


    public List<String> getTemplateNames() {
        return new ArrayList<>(templates.keySet());
    }


    public Template getDefaultTemplate() {
        return templates.get(sTplName);
    }

    public void setDefTemplate(String _tpl) {
        if (isTemplate(_tpl) && !isDefault(_tpl)) {
            sTplName = _tpl;
        }
    }


    public List<Template> getTemplateAll() {
        return new ArrayList<>(templates.values());
    }


    public boolean isTemplate(String _tpl) {
        if (_tpl.isEmpty() || _tpl.equals("/")) return true;
        return templates.containsKey(_tpl);
    }


    public boolean isDefault(String _tpl) {
        if (_tpl.isEmpty() || _tpl.equals("/")) return true;
        return sTplName.equalsIgnoreCase(_tpl);
    }


    public String getTemplatePath(String _url, HttpServletRequest _request) {
        HttpSession session = _request.getSession();
        String tpl = (String) session.getAttribute(Template_Name_EL);
        if (tpl == null) {
            tpl = sTplName;
        }
        Page page = getTemplatePage(tpl, _url);
        return page.getPath();
    }


    public Page getTemplatePage(String _tpl, String _url) {
        Template template = this.getTemplate(_tpl);
        Page page = template.findPage(_url);
        if (page == null) {
            page = template.getPage(_tpl + _url);
        }
        return page;
    }

    public Collection<Page> getTemplatePages(Template _tpl) {
        Map<String, Page> tpl_pages = _tpl.getPages();
        if (tpl_pages == null || tpl_pages.isEmpty()) {
            _tpl.setPages(this._load_template_pages(_tpl.getName(), _tpl.getAssets()));
        }
        return _tpl.getPages().values();
    }

    public void setHttpSecurity(HttpSecurity _hs) throws Exception {
        _hs.authorizeRequests().antMatchers("/**/*.ico").permitAll();
        Collection<Template> tpls = this.templates.values();
        for (Template t : tpls) {
            _hs.authorizeRequests().antMatchers("/" + t.getName() + "/" + t.getAssets() + "/**").permitAll();
            if (this.isDefault(t.getName())) {
                _hs.authorizeRequests().antMatchers("/" + t.getAssets() + "/**").permitAll();
            }
        }
        String[] suffixes = CommonConfig.getArray("jees.webs.security.suffix", String.class);
        if( suffixes != null ) {
            for (String suffix : suffixes) {
                _hs.authorizeRequests().antMatchers("/**/*" + suffix).permitAll();
            }
        }
    }

    /**
     * 设置页面用的模板相关变量
     *
     */
    public void setRequestEL(HttpServletRequest _request) {
        HttpSession session = _request.getSession();

        Template template = (Template) session.getAttribute(Template_Object_EL);
        if (template == null) {
            template = this.getDefaultTemplate();
        }

        session.setAttribute(Template_Name_EL, template.getName());
        session.setAttribute(Template_Object_EL, template);
        _request.setAttribute(Template_Object_EL, template);
        _request.setAttribute(Template_Name_EL, template.getName());
        _request.setAttribute(Template_Current_EL, template.getName());
        _request.setAttribute(Assets_Current_EL, "/" + template.getName() + "/" + template.getAssets());
        if (isDefault(template.getName())) {
            _request.setAttribute(Assets_Current_EL, "/" + template.getAssets());
            _request.setAttribute(Template_Current_EL, "");
        }
    }
}
