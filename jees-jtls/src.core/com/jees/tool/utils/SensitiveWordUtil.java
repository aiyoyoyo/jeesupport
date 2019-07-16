package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
    static Map<String, HashMap> wordMap = new HashMap<>();
    private static void _hash_words(){
        Set<String> key_set = new HashSet<>( wordMap.keySet()  );
        wordMap.clear();
        Iterator< String > key_it = key_set.iterator();
        String key;
        Map curr_map;
        Map new_map;
        while (key_it.hasNext()){
            //关键字
            key = key_it.next();
            curr_map = wordMap;
            for( int i = 0; i < key.length(); i++ ){
                //转换成char型
                char c = key.charAt( i );
                //库中获取关键字
                Object word_map = curr_map.get( c );
                //如果存在该key，直接赋值，用于下一个循环获取
                if( word_map != null ){
                    curr_map = ( Map ) word_map;
                }else{
                    //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    new_map = new HashMap<>();
                    //不是最后一个
                    new_map.put( "isEnd", "0" );
                    curr_map.put( c, new_map );
                    curr_map = new_map;
                }
                if( i == key.length() - 1 ){
                    //最后一个
                    curr_map.put( "isEnd", "1" );
                }
            }
        }

        log.info( "词典优化：WORDS[" + wordMap.size() + "]" );
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

    private static int check( String _text, int _idx, boolean _min ){
        int     match    = 0; //匹配标识数默认为0
        int     find     = 0; //查找的层级标识
        char    word;
        Map     curr_map = wordMap;
        for( int i = _idx; i < _text.length(); i++ ){
            word = _text.charAt( i );
            //获取指定key
            curr_map = ( Map ) curr_map.get( word );
            find ++;
            if( curr_map != null ){
                //存在，则判断是否为最后一个
                //找到相应key，匹配标识+1
                log.debug( match + "--Find:" + word );
                match ++;
                //如果为最后一个匹配规则,结束循环，返回匹配标识数
                if( "1".equals( curr_map.get( "isEnd" ) ) ){
                    //最小规则，直接返回,最大规则还需继续查找
                    if( _min ){
                        break;
                    }
                }
            }else{
                break;
            }
        }
        log.debug( match + "--END:" + find );
        if( match != find ){
            match = 0;
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
        Set< String > sets  = new HashSet<>();
        int           match = 0;
        for( int i = 0; i < _text.length(); i++ ){
            int length = check( _text, i, _min );    //判断是否包含敏感字符
            if( length > 0 ){    //存在,加入list中
                String find = _text.substring( i, i + length );
                log.debug( "--FIND->" + find );
                sets.add( find );
                i = i + length - 1;    //减1的原因，是因为for会自增
                match++;
            }
        }

        log.debug( "--匹配敏感词数量：MATCH=[" + match + "], MIN=[" + _min + "]" );
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
            String rep_char = "";
            for( int i = 0; i < sw.length(); i ++ ) rep_char += _replace;
            str = str.replaceAll( sw, rep_char );
        }

        return str;
    }
}
