package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 敏感词过滤类，使用方法
 * SensitiveWordUtil util = new SensitiveWordUtil();
 * util.initialize( ${path} );
 *
 * String txt;
 * util.check( txt, 1,true );
 * util.replace( txt, "*", 1, true );
 *
 * 部分代码出处：https://blog.csdn.net/chenssy/article/details/26961957
 * @author aiyoyoyo
 */
@Log4j2
public class SensitiveWordUtil {

    public static final String FILE_ENCODING = "UTF-8";

    static Map<String, HashMap> wordMap = new HashMap<>();

    private static void _hash_words(){
        Set<String> sets = wordMap.keySet().stream().collect( Collectors.toSet() );

        for( String key : sets ){
            char[] chars = key.toCharArray();

            Map curr_map = wordMap;
            for( char c : chars ){
                HashMap tmp = ( HashMap ) curr_map.get( c );
                if( tmp == null ){
                    tmp = new HashMap<>();
                    curr_map.put( c, tmp );
                    curr_map = tmp;
                }else{
                    curr_map = tmp;
                }
            }
        }

        log.info( "生成词典：WORDS[" + wordMap.size() + "]" );
    }

    private static void _load_txt_file( String _path ) throws FileNotFoundException{
        File file = FileUtil.load( _path, true );

        if( file.isDirectory() ){
            String[] file_list = file.list();
            for( String p : file_list ){
                File txt = FileUtil.load( _path + "/" + p, true );

                if( txt.isFile() ){
                    FileUtil.read( txt.getAbsolutePath(), s->{
                        String tmp = s.trim();
                        if( !tmp.trim().isEmpty() ){
                            wordMap.put( tmp, new HashMap<>() );
                        }
                    });
                }
            }
        }else{
            FileUtil.read( _path, s->{
                if( !s.isEmpty() ){
                    wordMap.put( s, new HashMap<>() );
                }
            });
        }

        log.info( "已加载基础词典数量：WORDS[" + wordMap.size() + "]" );
    }

    public static void initialize( String _path ) throws FileNotFoundException{
        // 读取目录下的txt文件作为词典
        _load_txt_file( _path );
        _hash_words();
    }

    private static int _check_( String _text, int _idx, boolean _min ) {
        int match = 0;
        char c;
        Map curr_map = wordMap;
        for ( int i = _idx; i < _text.length(); i++ ) {
            c = _text.charAt( i );
            curr_map = ( Map ) curr_map.get( c );
            if ( curr_map != null ) {
                match++;
                if ( curr_map.size() == 0 ){
                    //结束标志位为true
                    if ( _min ) {
                        break;
                    }
                }
            } else {
                //不存在，直接返回
                break;
            }
        }

        return match;
    }

    /**
     * 检查是否包含敏感词，_min为false时，会进行词典的全匹配
     * @param _text 内容
     * @param _min 是否最小匹配
     * @return
     */
    public static Set< String > check( String _text, boolean _min ){
        Set<String> sets = new HashSet<>();
        int match = 0;
        for(int i = 0 ; i < _text.length() ; i++){
            int length = _check_( _text, i, _min );    //判断是否包含敏感字符
            if( length > 1 ){    //存在,加入list中
                sets.add( _text.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
                match ++;
            }
        }

        log.debug( "--匹配敏感词数量：MATCH=[" + match + "]" );
        return sets;
    }

    /**
     * 替换包含敏感词的内容
     * @param _text
     * @param _replace
     * @param _min
     * @return
     */
    public static String replace( String _text, String _replace, boolean _min ){
        String str = _text;

        Set<String> sets = check( _text, _min );

        for( String sw : sets ){
            str = str.replaceAll( sw, _replace );
        }

        return str;
    }
}
