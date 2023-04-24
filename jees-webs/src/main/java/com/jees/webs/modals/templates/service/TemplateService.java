package com.jees.webs.modals.templates.service;

import com.jees.common.CommonConfig;
import com.jees.core.database.support.IRedisDao;
import com.jees.core.database.support.ISupportDao;
import com.jees.webs.core.interf.ISupportEL;
import com.jees.webs.modals.templates.struct.Page;
import com.jees.webs.modals.templates.struct.Template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TemplateService<M> implements ISupportEL {
    /**
     * 模板对象
     */
    Map<String, Template> templates = new HashMap<>();

    @Autowired
    IRedisDao rDao;
    @Autowired
    ISupportDao sDao;
    String sIndexPage; // 默认首页名称
    String sRootTpl; // 模板根路径
    String sTplName; // 当前模板名称

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
     *
     * @param _tpl
     * @return
     */
    private Template _load_template_config(String _tpl) {
        String tpl = _tpl;

        Template tpl_cfg = new Template();
        tpl_cfg.setName(tpl);
        tpl_cfg.setAssets(CommonConfig.get("jees.webs.modals.templates." + tpl + ".assets", "assets"));
//        tpl_cfg.setPages( this._load_template_pages( tpl, tpl_cfg.getAssets() ) );// 这里不能在初始化时执行
        tpl_cfg.setTemplatePath(getRootTpl() + tpl);
        return tpl_cfg;
    }

    @Autowired
    ResourcePatternResolver loader;
    /**
     * 加载模板页面，排除资源路径
     * 排除以下路径: 资源路径(assets)、_[dir_name]（下划线开头文件/文件夹）
     *
     * @param _tpl   模板名即路径
     * @param _asset 模板资源文件夹名
     * @return
     */
    private Map<String, Page> _load_template_pages(String _tpl, String _asset) {
        Map<String, Page> tpl_pages = new HashMap<>();
        String res_path = getRootTpl() + _tpl + "/**/*.*";
        Resource[] rfs = new Resource[0];
        try {
//            ResourcePatternResolver loader = CommonContextHolder.getBean(ResourcePatternResolver.class);
            if( loader != null ){
                rfs = this.loader.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "templates/"+_tpl+"/**/*.*");
            }
        } catch (IOException e) {
            log.error("模板[" + _tpl + "]加载失败：", e);
        }
        for (Resource r : rfs) {
            try {
                String r_path = "";
                if (r.getURI() != null) {
                    String uri_str = r.getURI().toString();
                    if (uri_str.startsWith("jar:file")) {
                        // jar包的文件
                        uri_str = uri_str.substring(uri_str.lastIndexOf("!") + 1);
                        r_path = uri_str;
                    } else {
                        r_path = r.getURI().getPath();
                    }
                } else {
                    r_path = r.toString();
                }
                if (r_path.indexOf("/" + _asset + "/") != -1) continue;
                if (r_path.startsWith("_")) continue;
                if (r_path.indexOf("/_") != -1) continue;
                if (r_path.startsWith("/")) r_path = r_path.substring(1);
                log.debug("页面文件路径:" + r_path);

                String[] path_arr = r_path.split("/");
                String page_url = null;
                String page_file = null;
                String page_path = null;
                for (int i = 0; i < path_arr.length; i++) {
                    String path = path_arr[i];
                    if (page_url == null && path.equalsIgnoreCase("templates")) {
                        page_url = "";
                        page_path = "";
                    } else if (page_url != null) {
                        if (i == path_arr.length - 1) { // 结尾
                            String[] tmp_path = path.split("\\.");
                            if (tmp_path.length > 1) {
                                page_path += path;
                                page_url += tmp_path[0];
                            } else {
                                page_path += path + "/" + sIndexPage + ".html";
                                page_url += tmp_path[0];
                            }
                        } else {
                            page_url += path + "/";
                            page_path += path + "/";
                        }
                    }
                    page_file = path;
                }
                Page page = new Page();
                page.setTpl(_tpl);
                page.setUrl(page_url);
                page.setPath(page_path);
                page.setFilepath(r_path);
                page.setFilename(page_file);

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
        return templates.keySet().stream().collect(Collectors.toList());
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
        return templates.values().stream().collect(Collectors.toList());
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


    public Class<M> getMenuClass() {
        return null;
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
    }

    /**
     * 设置页面用的模板相关变量
     *
     * @param _request
     */
    public void setRequestEL(HttpServletRequest _request) {
        HttpSession session = _request.getSession();

        Template template = (Template) session.getAttribute(Template_Object_EL);
        if (template == null) {
            template = this.getDefaultTemplate();
//            templates.values().forEach( t->{
//                session.setAttribute( Template_EL + t.getName(), t );
//                session.setAttribute( Template_Assets_EL + t.getName(), t.getName() + "/" + t.getAssets() );
//            } );
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

    @Transactional
    public List<M> loadTemplateMenus(String _tpl) {
        List<M> list;
        if (CommonConfig.getString("jees.webs.verify.mode", "local").equalsIgnoreCase("local")) {
            // TODO 从文件加载菜单
            list = new ArrayList<>();
        } else {
            if (CommonConfig.getBoolean("jees.jdbs.enable", false)) {
                list = sDao.select(sDao.getDefaultDB(), getMenuClass());
            } else if (CommonConfig.getBoolean("jees.redis.enable", false)) {
                list = rDao.findAll(getMenuClass());
            } else {
                // TODO 从文件加载菜单
                list = new ArrayList<>();
            }
        }
        return list;
    }

}
