package com.jees.tool.utils;

public class UrlUtil{
    /**
     * 将访问路径截取成根路径
     * eg: /xxx/yyy/zzz->"xxx"
     * eg: /->""
     * @param _uri
     * @return
     */
    public static String uri2mapping( String _uri ){
        if( !_uri.equals("/") && _uri.startsWith( "/" ) ) _uri = _uri.substring( 1 );
        int idx = _uri.indexOf( "/" );
        if( idx != -1 ) _uri = _uri.substring( 0, idx );
        return _uri;
    }
}
