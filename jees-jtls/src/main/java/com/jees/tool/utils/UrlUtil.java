package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;

@Log4j2
public class UrlUtil{
    /**
     * 将访问路径截取成根路径
     * eg: /xxx/yyy/zzz 转为 "xxx"
     * eg: / 转为 ""
     * @param _uri 地址
     * @return 结果
     */
    public static String uri2root( String _uri ){
        if( !_uri.equals("/") && _uri.startsWith( "/" ) ) _uri = _uri.substring( 1 );
        int idx = _uri.indexOf( "/" );
        if( idx != -1 ) _uri = _uri.substring( 0, idx );
        return _uri;
    }

    /**
     * 获取文件路径，转换url相对路径
     * eg: classpath:static/ 转为 static
     * @param _path 路径
     * @param _make 是否创建文件夹
     * @return 路径
     */
    public static String path2url( String _path, boolean _make ){
        try {
            File cls_file = FileUtil.load( "classpath:" , false );
            String cls_path = cls_file.getCanonicalPath();
            File file = FileUtil.load( _path , false );
            String path = file.getAbsolutePath();
            path = path.replace( cls_path, "" );
            if( path.startsWith("/") || path.startsWith("\\") ) path = path.
                    replace( "/", "" ).replace("\\", "" );
            return path;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 根据url相对路径，转换成文件路径
     * @param _url 地址
     * @param _make 是否创建文件夹
     * @return 路径
     */
    public static String url2path( String _url, boolean _make ){
        return FileUtil.path( "classpath:" + _url, _make );
    }
}
