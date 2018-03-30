package com.jees.webs.support;

import com.jees.webs.config.TemplateConfig;
import com.jees.webs.entity.SuperMenu;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ITemplateService {
    String getTemplateAndTheme( String _tpl, HttpServletRequest _request );

    List<String> getTemplateNames();

    TemplateConfig getDefaultTemplate();

    boolean isTemplate( String _tpl );

    List<SuperMenu> loadTemplateMenus(String tpl);
}
