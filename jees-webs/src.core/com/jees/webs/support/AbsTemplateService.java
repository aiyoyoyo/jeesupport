package com.jees.webs.support;

import com.jees.common.CommonConfig;
import com.jees.common.CommonLogger;
import com.jees.webs.config.TemplateConfig;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模版和主题管理类， 模版代表不同的可访问应用，主题代表应用展示样式。
 * @author aiyoyoyo
 */
public abstract class AbsTemplateService implements ITemplateService, ISupportEL{

    private Map<String, TemplateConfig> templates;
    private TemplateConfig              defTemplate;
    public AbsTemplateService(){
        // 加载配置中的模版和主题
        templates = new HashMap<>();

        StringTokenizer tpl_st = CommonConfig.getStringTokenizer( "jees.webs.templates" );
        while( tpl_st.hasMoreTokens() ){
            String tpl = tpl_st.nextToken().trim().toLowerCase();

            TemplateConfig tplCfg = _get_template_config( tpl );

            templates.put( tpl, tplCfg );
        }

        if( templates.size() == 0 ){
            String tpl = "default";

            TemplateConfig tplCfg = _get_template_config( tpl );

            templates.put( tpl, tplCfg );

            CommonLogger.getLogger( this.getClass() ).warn( "未找到有效模版，将使用默认模版路径：" + tpl );
        }

        CommonLogger.getLogger( this.getClass() ).info( "已加载模版：" + Arrays.toString( templates.keySet().toArray() ) );
    }

    /**
     * 加载模版配置
     * @param _tpl
     * @return
     */
    private TemplateConfig _get_template_config( String _tpl ){
        String tpl = _tpl;

        TemplateConfig tplCfg = new TemplateConfig();
        tplCfg.setName( tpl );
        tplCfg.setAssets( CommonConfig.getString( "jees.webs." + tpl + ".assets", "assets" ) );

        StringTokenizer the_st = CommonConfig.getStringTokenizer( "jees.webs." + tpl + ".themes" );
        Set<String> thes = new HashSet<>();

        while( the_st.hasMoreTokens() ){
            String the = the_st.nextToken().trim().toLowerCase();
            thes.add( the );
        }

        if( thes.size() == 0 ){
            thes.add( "default" );

            CommonLogger.getLogger( this.getClass() ).warn( "--模版[" + tpl + "]未找到有效主题，将使用默认主题路径：default" );
        }

        tplCfg.setThemes( thes );

        if( defTemplate == null ) defTemplate = tplCfg;

        CommonLogger.getLogger( this.getClass() ).info( "--模版[" + tpl + "]已加载主题：" + Arrays.toString( thes.toArray() ) );
        return tplCfg;
    }

    /**
     * 通过模版，设置用户Session信息，用于页面显示
     * @param _tpl
     * @param _request
     * @return
     */
    public String getTemplateAndTheme( String _tpl, HttpServletRequest _request ){
        HttpSession session = _request.getSession();

        TemplateConfig template = (TemplateConfig) session.getAttribute( Template_Current_EL );
        if( template == null ){
            templates.values().forEach( t -> {
                session.setAttribute( Template_EL + t.getName(), t.getName() );
                session.setAttribute( Template_Theme_EL + t.getName(), t.getName() + "/" + t.getTheme() );
                session.setAttribute( Template_Assets_EL + t.getName(), t.getName() + "/" + t.getAssets() );
            } );
        }

        template = getemplate( _tpl );
        session.setAttribute( Template_Current_EL, template );
        _request.setAttribute( Template_Current_EL, template );

        String theme = getTheme( template, session );
        _request.setAttribute( Theme_Current_EL, theme );

        String assets = getAssets( template, session );
        _request.setAttribute( Assets_Current_EL, assets );

        String ret_tpl_theme = template.getName() + "/" + theme;
        _request.setAttribute( Template_Theme_Current_EL, ret_tpl_theme );
        return ret_tpl_theme;
    }

    public TemplateConfig getemplate( String _tpl ){
        return templates.getOrDefault( _tpl, defTemplate );
    }

    public List<String>  getTemplateNames() {
        return templates.keySet().stream().collect(Collectors.toList());
    }

    public TemplateConfig getDefaultTemplate(){
        return defTemplate;
    }

    public boolean isTemplate( String _tpl ){
        return templates.containsKey( _tpl );
    }

    public String getTheme( TemplateConfig _tpl, HttpSession _session ){
        String theme = (String) _session.getAttribute( Theme_Current_EL );
        if( theme == null ){
            theme = _tpl.getTheme();
            _session.setAttribute( Theme_Current_EL, theme );
        }

        return theme;
    }

    public String getAssets( TemplateConfig _tpl, HttpSession _session ){
        String assets = (String) _session.getAttribute( Assets_Current_EL );
        if( assets == null ){
            assets = _tpl.getAssets();
            _session.setAttribute( Assets_Current_EL, _tpl.getName() + "/" + assets );
        }

        return assets;
    }
}
