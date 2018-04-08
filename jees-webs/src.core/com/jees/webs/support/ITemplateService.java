package com.jees.webs.support;

import com.jees.webs.config.TemplateConfig;
import com.jees.webs.entity.SuperMenu;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ITemplateService {
    TemplateConfig getTemplate( String _tpl );

    List<TemplateConfig> getTemplateAll();

    void loadTemplate( String _tpl, HttpServletRequest _request );

    List<String> getTemplateNames();

    TemplateConfig getDefaultTemplate();

    boolean isTemplate( String _tpl );

    boolean isDefault( String _tpl );

    List<SuperMenu> loadTemplateMenus(String tpl);
}
