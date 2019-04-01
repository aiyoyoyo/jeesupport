package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

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

    /**
     * 读取文件内容转为字符串
     * @param _file
     * @return
     * @throws IOException
     */
    public static String ReadFile( String _file ) throws IOException {
        File file = new File( _file );
        Long length = file.length();
        byte[] bytes = new byte[ length.intValue() ];

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read( bytes );
        } catch (FileNotFoundException e) {
            log.error( "文件没有找到：FILE=[" + _file + "]" );
        } catch (IOException e) {
            log.error( "文件读写错误：FILE=[" + _file + "]" );
        } finally {
            if( fis != null ) fis.close();
        }

        return new String( bytes, FILE_ENCODING );
    }

    /**
     * 往文件里写入字符串
     * @param _conent
     * @param _file
     * @throws IOException
     */
    public static void WriteFile( String _conent, String _file ) throws IOException {
        File file = new File( _file );

        if( !file.exists() ){
            file.getParentFile().mkdirs();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream( file );
            fos.write( _conent.getBytes( FILE_ENCODING ) );
        } catch ( FileNotFoundException e) {
            log.error( "文件没有找到：FILE=[" + _file + "]" );
        } catch (IOException e) {
            log.error( "文件读写错误：FILE=[" + _file + "]" );
        } finally {
            if( fos != null ) fos.close();
        }
    }

    /**
     * 读取文件内容，并进行自定义处理
     * @param _file
     * @param _consumer
     */
    public static void ReadFile( String _file, Consumer< ? super String > _consumer ){
        BufferedReader buff_read = null;

        int read_count = 0;
        String read_line;
        try {
            buff_read = new BufferedReader( new FileReader( _file ) );
            while ( ( read_line = buff_read.readLine() ) != null ) {
                if( _consumer != null ){
                    _consumer.accept( read_line );
                }
                read_count ++;
            }
        } catch ( FileNotFoundException e ) {
            log.error( "文件没有找到：FILE=[" + _file + "]" );
        } catch ( IOException e ) {
            log.error( "文件内容读取失败：FILE=[" + _file + "], LINE=[" + read_count + "]" );
        } finally {
            if( buff_read != null ) {
                try {
                    buff_read.close();
                } catch ( IOException ioe ) {
                    log.error( "关闭文件流失败：FILE=[" + _file + "]", ioe );
                }
            }
        }
    }
}
