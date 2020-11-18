package com.jees.tool.utils;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;
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

    public static File load( String _path, boolean _thr ) throws FileNotFoundException{
        File file = _try_clspath_loader( _path );
        if( file == null ) file = _try_absolute_loader( _path );
        if( file == null ) file = _try_canonical_loader( _path );
        if( file == null && _thr ) throw new FileNotFoundException( "文件[" + _path + "]没有找到。" );

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
        if ( !_file.getParentFile().exists() ) {
            _file.getParentFile().mkdirs();
        }
        try{
            @Cleanup FileOutputStream fos = new FileOutputStream( _file );
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

    private static File _try_clspath_loader( String _path ){
        File file = null;
        if ( _path.startsWith( "classpath:" ) || _path.startsWith( "classpath*:") ){
            try{
                file = ResourceUtils.getFile( _path );
            }catch( FileNotFoundException e ){
                log.error( "环境路径文件[" + _path + "]没有找到!");
                file = null;
            }
        }
        return file;
    }

    private static File _try_absolute_loader( String _path ){
        if( _path.startsWith("classpath:") ){
            _path = _path.replaceFirst( "classpath:", "" );
        }else if( _path.startsWith("classpath*:") ){
            _path = _path.replaceFirst( "classpath*:", "" );
        }
        File file = null;
        try{
            file = ResourceUtils.getFile( _path );
            File chk_file = new File( file.toURI() );
            if( !chk_file.exists() ){
                log.error( "相对路径文件[" + _path + "]没有找到!");
                file = null;
            }
        }catch( FileNotFoundException e ){
            log.error( "相对路径文件[" + _path + "]没有找到!");
            file = null;
        }
        return file;
    }

    private static File _try_canonical_loader( String _path ){
        if( _path.startsWith("classpath:") ){
            _path = _path.replaceFirst( "classpath:", "" );
        }else if( _path.startsWith("classpath*:") ){
            _path = _path.replaceFirst( "classpath*:", "" );
        }
        File file = new File( _path );
        if( !file.exists() ){
            log.error( "绝对路径文件[" + _path + "]没有找到!");
        }
        return file;
    }

    public static String path( String _path ){
        try {
            return ResourceUtils.getFile( _path ).getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException e) {
            log.error( "文件[" + _path + "]没有找到:", e );
        }
        return null;
    }

    public static String path( String _path, boolean _make ){
        File file = null;
        try {
            file = ResourceUtils.getFile( _path );
            if( _make && !file.exists()) {
                File parent_file = file.getParentFile();
                List<File> list = new ArrayList<>();
                while (!parent_file.exists()) {
                    list.add( 0, parent_file );
                    parent_file = parent_file.getParentFile();
                }

                for( File f : list ){
                    if( !f.exists() ) f.mkdir();
                }
                file.mkdir();
            }
            return file.getCanonicalPath().replaceAll("\\\\", "/");
        } catch (IOException e) {
            if( _make ){
                if( _path.startsWith( "classpath:" ) ){
                    _path = _path.replaceAll( "classpath:", "" );
                }
                _path = path( "classpath:" )  +"/" + _path;
                _path = path( _path, true );
            }else{
                log.error( "文件[" + _path + "]没有找到:", e );
            }
        }
        return _path;
    }
}
