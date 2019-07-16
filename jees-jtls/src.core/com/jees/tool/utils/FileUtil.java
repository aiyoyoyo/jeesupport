package com.jees.tool.utils;

import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import java.io.*;
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
        if( file == null ){
            file = _try_absolute_loader( _path );
        }
        if( file == null ){
            file = _try_canonical_loader( _path );
        }
        if( file == null ){
            String msg = "文件没有找到：FILE=[" + _path + "]";
            log.warn( msg );
            throw new FileNotFoundException( msg );
        }
        return file;
    }

    /**
     * 读取文件内容转为字符串
     *
     * @param _filepath
     * @return
     */
    public static String read ( String _filepath, boolean _thr ) throws IOException{
        File   file   = load( _filepath, _thr );
        return read( file );
    }

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
     * @param _conent
     * @param _file
     */
    public static void write ( String _conent, String _file, boolean _thr ) throws FileNotFoundException{
        File file = load( _file, _thr );

        write( _conent, file );
    }

    public static void write( String _conent, @Nullable File _file ){
        if ( !_file.exists() ) {
            _file.getParentFile().mkdirs();
        }

        try{
            @Cleanup FileOutputStream fos = new FileOutputStream( _file );
            fos.write( _conent.getBytes( FILE_ENCODING ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件内容，并进行自定义处理
     *
     * @param _file
     * @param _consumer
     */
    public static void read ( String _file, Consumer< ? super String > _consumer ) {
        int read_count = 0;
        String read_line;

        try {
            @Cleanup BufferedReader buff_read = new BufferedReader( new FileReader( _file ) );
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
        if ( _path.startsWith( "classpath:" ) ){
            try{
                file = ResourceUtils.getFile( _path );
            }catch( FileNotFoundException e ){
            }
        }

        return file;
    }

    private static File _try_absolute_loader( String _path ){
        if( _path.startsWith("classpath:") ){
            _path = _path.replaceFirst( "classpath:", "" );
        }

        File file = new File( _path );
        return file;
    }

    private static File _try_canonical_loader( String _path ){
        if( _path.startsWith("classpath:") ){
            _path = _path.replaceFirst( "classpath:", "" );
        }

        File file = new File( _path );
        return file;
    }
}
