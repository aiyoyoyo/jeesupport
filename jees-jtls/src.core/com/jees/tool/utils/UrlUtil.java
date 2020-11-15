package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Log4j2
public class UrlUtil{
    /**
     * 将访问路径截取成根路径
     * eg: /xxx/yyy/zzz->"xxx"
     * eg: /->""
     * @param _uri
     * @return
     */
    public static String uri2root( String _uri ){
        if( !_uri.equals("/") && _uri.startsWith( "/" ) ) _uri = _uri.substring( 1 );
        int idx = _uri.indexOf( "/" );
        if( idx != -1 ) _uri = _uri.substring( 0, idx );
        return _uri;
    }

    /**
     * 获取文件路径，转换url相对路径
     * eg: classpath:static/ -> static
     * @param _path
     * @param _make
     * @return
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
     * @param _url
     * @param _make
     * @return
     */
    public static String url2path( String _url, boolean _make ){
        String path = null;
        File file = null;
        try {
            path = FileUtil.path( "classpath:" );
            path += "/" + _url;
            file = FileUtil.load( path, false );
        } catch (FileNotFoundException e) {
            log.error( "文件未找到!", e );
        }finally {
            if( _make && file != null ){
                file.mkdir();
            }
        }
        return path;
    }
}
