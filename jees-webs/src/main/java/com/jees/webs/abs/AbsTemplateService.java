package com.jees.webs.abs;

import com.jees.common.CommonConfig;
import com.jees.core.database.support.IRedisDao;
import com.jees.core.database.support.ISupportDao;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.Template;
import com.jees.webs.support.ISupportEL;
import com.jees.webs.support.ITemplateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模版和主题管理类， 模版代表不同的可访问应用，主题代表应用展示样式。
 * @author aiyoyoyo
 */
@Log4j2
public abstract class AbsTemplateService<M extends SuperMenu> implements ITemplateService, ISupportEL{
    Map< String, Template > templates = new HashMap<>();
    Template                defTemplate;
    @Autowired
    IRedisDao rDao;
    @Autowired
    ISupportDao sDao;

    public AbsTemplateService(){
        // 加载配置中的模版和主题
        StringTokenizer tpl_st = CommonConfig.getStringTokenizer( "jees.webs.templates" );
        while( tpl_st.hasMoreTokens() ){
            String   tpl    = tpl_st.nextToken().trim().toLowerCase();
            Template tplCfg = _get_template_config( tpl );
            templates.put( tpl, tplCfg );
        }

        if( templates.size() == 0 ){
            String   tpl    = "default";
            Template tplCfg = _get_template_config( tpl );
            templates.put( tpl, tplCfg );
            log.warn( "未找到有效模版，将使用默认模版路径：" + tpl );
        }

        log.info( "已加载模版：" + Arrays.toString( templates.keySet().toArray() ) );
    }

    /**
     * 加载模版配置
     *
     * @param _tpl
     * @return
     */
    private Template _get_template_config( String _tpl ){
        String tpl = _tpl;

        Template tplCfg = new Template();
        tplCfg.setName( tpl );
        tplCfg.setAssets( CommonConfig.getString( "jees.webs." + tpl + ".assets", "assets" ) );

        if( defTemplate == null ) defTemplate = tplCfg;
        return tplCfg;
    }

    /**
     * 通过模版，设置用户Session信息，用于页面显示
     * @param _tpl 模版名
     * @param _request HttpServletRequest
     */
    @Override
    public void loadTemplate( String _tpl, HttpServletRequest _request ){
        String app_path = CommonConfig.getString( "server.servlet.context-path", "/" );
        if( app_path.endsWith( "/" ) ){
            app_path = app_path.substring( 0, app_path.length() - 1 );
        }
        log.debug( "Load template el: app=[" + app_path + "]" );
        HttpSession session = _request.getSession();

        Template template = ( Template ) session.getAttribute( Template_Object_EL );
        if( template == null ){
            templates.values().forEach( t->{
                session.setAttribute( Template_EL + t.getName(), t );
                session.setAttribute( Template_Assets_EL + t.getName(), t.getName() + "/" + t.getAssets() );
            } );
        }

        template = getTemplate( _tpl );
        session.setAttribute( Template_Object_EL, template );
        _request.setAttribute( Template_Object_EL, template );
        _request.setAttribute( Assets_Current_EL, app_path + "/" + template.getName() + "/" + template.getAssets() );
        _request.setAttribute( Template_Current_EL, template.getName() );

        session.setAttribute( Session_Templates_EL, templates.values() );
        _request.setAttribute( App_EL, app_path );
    }

    @Override
    public Template getTemplate( String _tpl ){
        return templates.getOrDefault( _tpl, defTemplate );
    }

    @Override
    public List< String > getTemplateNames(){
        return templates.keySet().stream().collect( Collectors.toList() );
    }

    @Override
    public Template getDefaultTemplate(){
        return defTemplate;
    }

    @Override
    public void setDefTemplate( String _tpl){
        if( isTemplate( _tpl ) && !isDefault( _tpl) ) {
            defTemplate = getTemplate( _tpl );
        }
    }

    @Override
    public List< Template > getTemplateAll(){
        return templates.values().stream().collect( Collectors.toList() );
    }

    @Override
    public boolean isTemplate( String _tpl ){
        if( _tpl.isEmpty() || _tpl.equals( "/" ) ) return true;
        return templates.containsKey( _tpl );
    }

    @Override
    public boolean isDefault( String _tpl ){
        if( _tpl.isEmpty() || _tpl.equals( "/" ) ) return true;
        return defTemplate.getName().equalsIgnoreCase( _tpl );
    }

    @Override
    public String getTemplatePath( String _path, HttpServletRequest _request ){
        HttpSession session  = _request.getSession();
        Template    template = ( Template ) session.getAttribute( Template_Object_EL );

        if( _path.endsWith( "/" ) ) _path += "index";
        return template.getName() + "/" + _path;
    }

    @Transactional
    @Override
    public List< M > loadTemplateMenus( String _tpl ){
        List< M > list;
        if( CommonConfig.getString( "jees.webs.verify.mode", "local" ).equalsIgnoreCase( "local") ){
            // TODO 从文件加载菜单
            list = new ArrayList<>();
        }else{
            if( CommonConfig.getBoolean( "jees.jdbs.enable", false ) ){
                list = sDao.select( sDao.getDefaultDB(), getMenuClass() );
            }else if( CommonConfig.getBoolean( "jees.redis.enable", false ) ){
                list = rDao.findAll( getMenuClass() );
            }else{
                // TODO 从文件加载菜单
                list = new ArrayList<>();
            }
        }
        return list;
    }
}
