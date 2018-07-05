package com.jees.webs.support;

import com.jees.common.CommonConfig;
import com.jees.webs.entity.Template;
import lombok.extern.log4j.Log4j2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模版和主题管理类， 模版代表不同的可访问应用，主题代表应用展示样式。
 * @author aiyoyoyo
 */
@Log4j2
public abstract class AbsTemplateService implements ITemplateService, ISupportEL{

    private Map<String, Template> templates;
    private Template defTemplate;
    public AbsTemplateService(){
        // 加载配置中的模版和主题
        templates = new HashMap<>();

        StringTokenizer tpl_st = CommonConfig.getStringTokenizer( "jees.webs.templates" );
        while( tpl_st.hasMoreTokens() ){
            String tpl = tpl_st.nextToken().trim().toLowerCase();

            Template tplCfg = _get_template_config( tpl );

            templates.put( tpl, tplCfg );
        }

        if( templates.size() == 0 ){
            String tpl = "default";

            Template tplCfg = _get_template_config( tpl );

            templates.put( tpl, tplCfg );

            log.warn( "未找到有效模版，将使用默认模版路径：" + tpl );
        }

        log.info( "已加载模版：" + Arrays.toString( templates.keySet().toArray() ) );
    }

    /**
     * 加载模版配置
     * @param _tpl
     * @return
     */
    private Template _get_template_config(String _tpl ){
        String tpl = _tpl;

        Template tplCfg = new Template();
        tplCfg.setName( tpl );
        tplCfg.setAssets( CommonConfig.getString( "jees.webs." + tpl + ".assets", "assets" ) );

        if( defTemplate == null ) defTemplate = tplCfg;
        return tplCfg;
    }

    /**
     * 通过模版，设置用户Session信息，用于页面显示
     * @param _request
     * @return
     */
    @Override
    public void loadTemplate( String _tpl, HttpServletRequest _request ){
        HttpSession session = _request.getSession();

        Template template = (Template) session.getAttribute( Template_Object_EL );
        if( template == null ){
            templates.values().forEach( t -> {
                session.setAttribute( Template_EL + t.getName(), t );
                session.setAttribute( Template_Assets_EL + t.getName(), t.getName() + "/" + t.getAssets() );
            } );
        }

        template = getTemplate( _tpl );
        session.setAttribute( Template_Object_EL, template );
        _request.setAttribute( Template_Object_EL, template );
        _request.setAttribute( Assets_Current_EL, "/" + template.getName() + "/" + template.getAssets() );
        _request.setAttribute( Template_Current_EL, template.getName() );

        session.setAttribute( Session_Templates_EL, templates.values() );
    }

    @Override
    public Template getTemplate(String _tpl ){
        return templates.getOrDefault( _tpl, defTemplate );
    }
    @Override
    public List<String>  getTemplateNames() {
        return templates.keySet().stream().collect(Collectors.toList());
    }
    @Override
    public Template getDefaultTemplate(){
        return defTemplate;
    }
    @Override
    public List<Template> getTemplateAll(){
        return templates.values().stream().collect( Collectors.toList() );
    }
    @Override
    public boolean isTemplate( String _tpl ){
        if( _tpl.isEmpty() || _tpl.equals("/") ) return true;
        return templates.containsKey( _tpl );
    }
    @Override
    public boolean isDefault( String _tpl ) {
        if( _tpl.isEmpty() || _tpl.equals("/") ) return true;
        return defTemplate.getName().equalsIgnoreCase( _tpl );
    }

    @Override
    public String getTemplatePath( String _path, HttpServletRequest _request ){
        HttpSession session = _request.getSession();
        Template template = (Template) session.getAttribute( Template_Object_EL );

        if( _path.endsWith( "/" ) ) _path += "index";
        return template.getName() + "/" + _path;
    }
}
