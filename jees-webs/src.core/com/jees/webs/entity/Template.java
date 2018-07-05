package com.jees.webs.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * 模版结构配置
 * @author aiyoyoyo
 */
@Getter
@Setter
@Log4j2
public class Template {
    private String                  name;
    private String                  assets;
    private Map<String , Page>      pages = new HashMap<>();

    public void addPage( Page _page ){
        if( !pages.containsKey( _page.getUrl() ) ){
            pages.put( _page.getUrl(), _page );
            log.debug( "--配置访问路径：URL=[" + _page.getUrl() + "], PATH=[" + _page.getPath() + "]" );
        }else{
            log.warn( "存在重复的访问路径：URL=[" + _page.getUrl() + "], PATH=[" + _page.getPath() + "]" );
        }
    }
}
