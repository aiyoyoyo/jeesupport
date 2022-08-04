package com.jees.webs.modals.templates.interf;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.modals.templates.struct.Page;
import com.jees.webs.modals.templates.struct.Template;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public interface ITemplateService<M extends SuperMenu> {
    Template getTemplate(String _tpl );

    List<Template> getTemplateAll();

    void loadTemplate( String _tpl, HttpServletRequest _request );

    List<String> getTemplateNames();

    Template getDefaultTemplate();

    void setDefTemplate( String _tpl );

    boolean isTemplate( String _tpl );

    boolean isDefault( String _tpl );

    List<M> loadTemplateMenus( String _tpl );

    String getTemplatePath( String _url , HttpServletRequest _request );

    Page getTemplatePage( String _tpl, String _url );

    Class<M> getMenuClass();

    Collection<Page> getTemplatePages(Template _tpl);
}
