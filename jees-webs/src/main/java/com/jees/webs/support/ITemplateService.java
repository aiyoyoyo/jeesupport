package com.jees.webs.support;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.Template;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ITemplateService<M extends SuperMenu> {
    Template getTemplate(String _tpl );

    List<Template> getTemplateAll();

    void loadTemplate( String _tpl, HttpServletRequest _request );

    List<String> getTemplateNames();

    Template getDefaultTemplate();

    boolean isTemplate( String _tpl );

    boolean isDefault( String _tpl );

    List<M> loadTemplateMenus( String tpl );

    String getTemplatePath( String _path , HttpServletRequest _request );

    Class<M> getMenuClass();
}
