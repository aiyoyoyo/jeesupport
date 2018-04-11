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

        TemplateConfig template = (TemplateConfig) session.getAttribute( Template_Current_EL );
        if( template == null ){
            templates.values().forEach( t -> {
                session.setAttribute( Template_EL + t.getName(), t );
                session.setAttribute( Template_Assets_EL + t.getName(), t.getName() + "/" + t.getAssets() );
            } );
        }

        template = getTemplate( _tpl );
        _request.setAttribute( Template_Object_EL, template );
        _request.setAttribute( Assets_Current_EL, "/" + template.getName() + "/" + template.getAssets() );
        _request.setAttribute( Template_Current_EL, template.getName() );
    }

    @Override
    public TemplateConfig getTemplate( String _tpl ){
        return templates.getOrDefault( _tpl, defTemplate );
    }
    @Override
    public List<String>  getTemplateNames() {
        return templates.keySet().stream().collect(Collectors.toList());
    }
    @Override
    public TemplateConfig getDefaultTemplate(){
        return defTemplate;
    }
    @Override
    public List<TemplateConfig> getTemplateAll(){
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
}
