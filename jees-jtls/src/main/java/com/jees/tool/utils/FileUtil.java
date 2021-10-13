package com.jees.tool.utils;

import com.jees.common.CommonConfig;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
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
            log.info( "Class Path:" + classpath );
        }
        return classpath;
    }

    public static File load( String _path, boolean _make ){
        File file = null;
        boolean is_dir = _path.endsWith("/");
        try {
            log.debug( "尝试直接加载文件: " + _path );
            file = ResourceUtils.getFile( _path );
            if( !file.exists() ) file = null;
        } catch (FileNotFoundException e) {
            log.warn("尝试直接加载失败，文件不存在: " + _path );
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
            log.debug( "File is not exist: " + _path );
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
    public static String read( File _file ) throws IOException{
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
    public static void write( String _conent, File _file ) throws IOException {
        write( _conent.getBytes( FILE_ENCODING ), _file );
    }

    public static void write( byte[] _bytes, String _path, boolean _thr ) throws IOException {
        File file = load( _path , _thr );
        write( _bytes, file );
    }

    public static void write( byte[] _bytes, File _file ) throws IOException {
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
            log.error( "文件没有找到:FILE=[" + _file + "]" );
        } catch ( IOException e ) {
            log.error( "文件内容读取失败:FILE=[" + _file + "], LINE=[" + read_count + "]" );
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

    /**
     * 获取文件的真实路径
     * @param _cls
     * @return
     */
    public static String path( Class _cls ){
        return _cls.getResource( "" ).getPath();
    }

    static String projectPath;
    static String webPath;
    static String srcPath;
    static String tmpPath;
    static boolean devWebroot;  // 普通开发
    static boolean devMaven;    // Maven开发
    static boolean depTomcat;   // Tomcat部署
    static boolean depServer;   // Server部署(SpringBoot)

    /**
     * 获取工程开发路径，如果是部署路径则返回部署路径
     * @return
     */
    public static String project() {
        if( projectPath == null ) {
            Class app = null;
            try {
                app = Class.forName( FileUtil.class.getName() );
            } catch (ClassNotFoundException e) {
            }
            String path = app.getResource("").getPath();
            StringBuffer str_sb = new StringBuffer();

            int sub_idx;

            sub_idx = path.indexOf("target");
            devMaven = sub_idx != -1;

            if (!devMaven) {
                sub_idx = path.indexOf("out");
                devWebroot = sub_idx != -1;
            }
            if (!devMaven && !devWebroot) {
                sub_idx = path.indexOf("webapps");
                depTomcat = sub_idx != -1;

                if (!depTomcat) {
                    sub_idx = path.indexOf("WEB-INF");
                    depServer = sub_idx != -1;
                }
            }

            if( !depTomcat && !depServer ){
                projectPath = path.substring(0, sub_idx);
            }else{
                projectPath = "";
                if( path.startsWith( "/" ) ){
                    String os = System.getProperty("os.name");
                    log.info( "os.name: " + os );
                    if(os.toLowerCase().startsWith("win")){
                        if( path.startsWith( "/" ) ){
                            path = path.substring( 1 );
                        }
                    }
                }
                String[] paths = path.split("/");
                for( int i = 0; i< paths.length; i ++ ){
                    String p = paths[i];
                    if( p.equalsIgnoreCase( "webapps") ){
                        projectPath += p + "/" + paths[i + 1] + "/";
                        break;
                    }else if( p.equalsIgnoreCase( "WEB-INF" ) ){
                        break;
                    }else projectPath += p + "/";
                }
            }

            log.info( "Project path:" + projectPath );
        }
        return projectPath.replaceAll("%20", " ");
    }

    /**
     * 获取源代码路径
     * @return
     */
    public static String source(){
        if( srcPath == null ) {
            String project_path = project();
            if (devMaven) {
                srcPath = project_path + "src/main/java/";
            } else if (devWebroot) {
                srcPath = project_path + "src/";
            } else if (depTomcat || depServer) {
                srcPath = project_path + "WEB-INF/classes/";
            }

            File try_file = FileUtil.load(srcPath, false);
            if (!try_file.exists()) {
                log.warn("Can't find Src Path:" + srcPath);
                srcPath = null;
            }
            log.info( "Src Path:" + srcPath );
        }
        return srcPath;
    }

    /**
     * 获取webroot的路径，没有找到正确的地址返回null
     * @return
     */
    public static String webroot(){
        if( webPath == null ) {
            String project_path = project();
            if (devMaven) {
                webPath = project_path + CommonConfig.get( "jees.webs.devWebRoot", "src/main/resources/" );
            } else if (devWebroot) {
                webPath = project_path + "WebRoot/";
            } else if (depTomcat || depServer) {
                webPath = project_path;
            }

            File try_file = FileUtil.load(webPath, false);
            if (!try_file.exists()) {
                log.warn("Can't find WebRoot Path:" + webPath);
                webPath = null;
            }
            log.info( "Web Path:" + webPath );
        }
        return webPath;
    }

    /**
     * 获取模版路径
     * @return
     */
    public static String template(){
        if( tmpPath == null ){
            String path = webroot() + "WEB-INF/";

            tmpPath = "";
            if( path.startsWith( "/" ) ){
                path = path.substring( 1 );
            }
            String[] paths = path.split("/");
            for( int i = 0; i< paths.length; i ++ ){
                String p = paths[i];
                if( p.startsWith( "icomm-") && p.endsWith(".jar") ){
                    projectPath += p + "/" + paths[i + 1] + "/";
                    break;
                }else tmpPath = path + "temp/";
            }

            log.info( "Template Path:" + tmpPath );
        }

        return tmpPath;
    }

    /**
     * 拷贝文件到指定目录
     * @param _form 源文件
     * @param _to 目标文件夹
     * @param _all 为真时，当源文件为目录时拷贝源文件夹下的所有子文件到目标文件夹
     */
    public void copy( String _form, String _to, boolean _all ){

    }

    /**
     *  读取jar包中的资源文件
     * @param _file 文件名
     * @return 文件内容
     * @throws IOException 读取错误
     */
    public static String read( String _file ) throws IOException{
        int read_count = 0;
        String read_line;
        StringBuilder buffer = new StringBuilder();
        try {
            @Cleanup BufferedReader buff_read = new BufferedReader( new InputStreamReader( FileUtil.class.getClassLoader().getResourceAsStream(_file)) );
            while ( ( read_line = buff_read.readLine() ) != null ) {
                buffer.append( new String( read_line.getBytes(), FILE_ENCODING ) + "\n" );
                read_count++;
            }
        } catch ( FileNotFoundException e ) {
            log.error( "文件没有找到:FILE=[" + _file + "]" );
        } catch ( IOException e ) {
            log.error( "文件内容读取失败:FILE=[" + _file + "], LINE=[" + read_count + "]" );
        }
        return buffer.toString();
    }
}
