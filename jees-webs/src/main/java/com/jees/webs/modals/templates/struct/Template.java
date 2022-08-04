package com.jees.webs.modals.templates.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.DataTransferObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 模版结构配置
 * @author aiyoyoyo
 */
@Getter
@Setter
@Log4j2
@DataTransferObject
public class Template {
    /**
     * 模板名称
     */
    String                  name;
    /**
     * 资源路径
     */
    String                  assets;
    /**
     * 文件路径
     */
    private String          templatePath;
    /**
     * 模板下页面集合
     * key: 访问地址
     * value: 页面信息
     */
    Map<String , Page>      pages = new HashMap<>();

    /**
     * 添加模板页面
     * @param _page
     */
    public void addPage( Page _page ){
        if( !pages.containsKey( _page.getUrl() ) ){
            pages.put( _page.getUrl(), _page );
            log.debug( "--配置访问路径: URL=[" + _page.getUrl() + "], PATH=[" + _page.getFilepath() + "]" );
        }else{
            log.warn( "存在重复的访问路径: URL=[" + _page.getUrl() + "], PATH=[" + _page.getFilepath() + "]" );
        }
    }

    /**
     * 返回页面信息
     * @param _url
     * @return
     * @throws NullPointerException
     */
    public Page getPage( String _url ) throws NullPointerException{
        Page page = findPage( _url );
        if( page == null ){
            throw new NullPointerException( "请求页面[" + _url + "]没有找到！" );
        }
        return page;
    }

    /**
     * 返回页面信息，可能为null
     * @param _url
     * @return
     */
    public Page findPage( String _url ){
        Page page = _try_err_page( _url );
        if( page == null ){
            page = _find_page( _url );
        }
        return page;
    }

    private Page _find_page( String _url ){
        if( pages.containsKey( _url ) ){
            return pages.get( _url );
        }else{
            return null;
        }
    }

    private Page _try_err_page( String _url ){
        Page err_page = null;
        // 判断是否错误页面
        String err_name = _url;
        int len = err_name.length();
        if( err_name.length() > 3 ){
            len = 3;
            err_name = err_name.substring( 0, len );
        }
        for( int i = len; i > 1; i -- ) {
            Page page = _try_err_page(err_name, i);
            if( page != null ){
                err_page = page;
                break;
            }
        }
        return err_page;
    }

    private Page _try_err_page( String _url, int _len ){
        Page err_page = null;
        try {
            String err_name = _url.substring( 0, _len );
            int err_num = Integer.parseInt( err_name );
            if( err_num > 99 ) err_page = _find_page( err_name );
            else if( err_num > 9 ) err_page = _find_page( err_name + "x" );
            else err_page = _find_page( err_name + "xx" );
        }catch ( Exception e ){
        }
        return err_page;
    }
}
