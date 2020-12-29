package com.jees.tool.utils;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * 基础的文件读写类
 *
 * @author aiyoyoyo
 */
@Log4j2
public class FileUtil {
    public static final String FILE_ENCODING = "UTF-8";

    static String classpath = null;
    public static String classpath(){
        if( classpath == null ){
            classpath = path( "classpath:" );
        }
        return classpath;
    }

    public static File load( String _path, boolean _make ){
        File file = null;
        boolean is_dir = _path.endsWith("/");
        try {
            log.debug( "尝试直接加载文件:" + _path );
            file = ResourceUtils.getFile( _path );
            if( !file.exists() ) file = null;
        } catch (FileNotFoundException e) {
            log.warn("尝试直接加载失败，文件不存在:" + _path );
        }
        if( file == null ){
            _path = _path.replace( "classpath:", "" );
            _path = _path.replace( "classpath*:", "" );
            try {
                log.debug( "尝试加载相对路径文件:" + _path );
                file = ResourceUtils.getFile( _path );
                if( !file.exists() ) file = null;
            } catch (FileNotFoundException e) {
                log.warn("尝试加载相对路径失败，文件不存在:" + _path );
            }
        }
        if( file == null ){
            if( _path.indexOf( ":" ) != -1 ){
            }else if( _path.startsWith("/") ){
                _path = classpath() + _path;
            }else{
                _path = classpath() + "/" + _path;
            }
            try {
                log.debug( "尝试加载绝对路径文件:" + _path );
                file = ResourceUtils.getFile( _path );
            } catch (FileNotFoundException e) {
                log.warn("尝试加载绝对路径失败，文件不存在:" + _path );
            }
        }
        if( file == null ){
            log.debug( "文件不存在，创建文件对象:" + _path );
            file = new File( _path );
        }
        if( !file.exists() && _make ) make( file, is_dir );
        return file;
    }

    /**
     * 读取文件内容转为字符串
     *
     * @param _filepath 文件路径
     * @param _thr 是否抛出异常
     * @return 文件内容
     * @throws IOException 读写异常
     */
    public static String read ( String _filepath, boolean _thr ) throws IOException{
        File   file   = load( _filepath, _thr );
        return read( file );
    }

    /**
     *
     * @param _file 文件对象
     * @return 文件内容
     * @throws IOException 读写异常
     */
    public static String read( @Nullable File _file ) throws IOException{
        Long   length = _file.length();
        byte[] bytes  = new byte[length.intValue()];

        try{
            @Cleanup FileInputStream fis = new FileInputStream( _file );
            fis.read( bytes );

            return new String( bytes, FILE_ENCODING );
        }catch( Exception e ){
            throw e;
        }
    }

    /**
     * 往文件里写入字符串
     *
     * @param _conent 写入内容
     * @param _file 待写入对象
     * @throws IOException 读写异常
     */
    public static void write ( String _conent, String _file, boolean _thr ) throws IOException {
        write( _conent.getBytes( FILE_ENCODING ), _file, _thr );
    }

    /**
     * @param _conent 待写入内容
     * @param _file 待写入文件
     * @throws IOException 读写异常
     */
    public static void write( String _conent, @Nullable File _file ) throws IOException {
        write( _conent.getBytes( FILE_ENCODING ), _file );
    }

    public static void write( byte[] _bytes, String _path, boolean _thr ) throws IOException {
        File file = load( _path , _thr );
        write( _bytes, file );
    }

    public static void write( byte[] _bytes, @Nullable File _file ) throws IOException {
        File   file   = load( _file.getCanonicalPath(), true );
        try{
            @Cleanup FileOutputStream fos = new FileOutputStream( file );
            fos.write( _bytes );
        } catch ( Exception e ) {
            throw e;
        }
    }

    /**
     * 读取文件内容，并进行自定义处理
     *
     * @param _file 待读取文件
     * @param _consumer 每行处理方法
     */
    public static void read ( String _file, Consumer< ? super String > _consumer ) {
        int read_count = 0;
        String read_line;

        try {
            File   file   = load( _file, false );
            @Cleanup BufferedReader buff_read = new BufferedReader( new FileReader( file ) );
            while ( ( read_line = buff_read.readLine() ) != null ) {
                if ( _consumer != null ) {
                    _consumer.accept( read_line );
                }
                read_count++;
            }
        } catch ( FileNotFoundException e ) {
            log.error( "文件没有找到：FILE=[" + _file + "]" );
        } catch ( IOException e ) {
            log.error( "文件内容读取失败：FILE=[" + _file + "], LINE=[" + read_count + "]" );
        }
    }

    public static void make( File _file, boolean _dir ){
        File parent_file = _file.getParentFile();
        List<File> list = new ArrayList<>();
        while (!parent_file.exists()) {
            list.add( 0, parent_file );
            parent_file = parent_file.getParentFile();
        }

        for( File f : list ){
            if( !f.exists() ) f.mkdir();
        }

        if( _dir ){
            log.debug("创建目录:" + _file.getPath() );
            _file.mkdir();
        }else {
            log.debug("创建文件:" + _file.getPath() );
            try {
                _file.createNewFile();
            } catch (IOException e) {
                log.error("创建文件失败:" + _file.getPath() );
            }
        }
    }

    public static String path( String _path ){
        try {
            File file = load( _path, false );
            return file.getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException e) {
            log.error( "文件[" + _path + "]路径获取失败:", e );
        }
        return null;
    }

    /**
     * 获取文件实际路径，/结尾的字符串将创建文件夹，否则创建文件
     * @param _path 路径
     * @param _make 是否创建
     * @return 文件地址
     */
    public static String path( String _path, boolean _make ){
        String file_path = path( _path );
        if( _make ){
            File file = load( _path, _make );
            try {
                return file.getCanonicalPath().replaceAll("\\\\", "/");
            } catch (IOException e) {
                log.error( "文件[" + _path + "]路径获取失败:", e );
            }
        }
        return file_path;
    }
}
