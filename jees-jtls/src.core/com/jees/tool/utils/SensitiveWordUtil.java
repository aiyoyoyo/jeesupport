package com.jees.tool.utils;

import lombok.extern.log4j.Log4j2;

import java.io.File;
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

    private void _hash_words(){
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

    private void _load_txt_file( String _path ){
        File file = new File( _path ); //读取文件

        if( file.isDirectory() ){
            String[] file_list = file.list();
            for( String p : file_list ){
                File txt = new File( _path + "/" + p );

                if( txt.isFile() ){
                    FileUtil.ReadFile( txt.getAbsolutePath(), s->{
                        if( !s.isEmpty() ){
                            wordMap.put( s, new HashMap<>() );
                        }
                    });
                }
            }
        }else{
            FileUtil.ReadFile( _path, s->{
                if( !s.isEmpty() ){
                    wordMap.put( s, new HashMap<>() );
                }
            });
        }

        log.info( "已加载基础词典数量：WORDS[" + wordMap.size() + "]" );
    }

    public void initialize( String _path ){
        // 读取目录下的txt文件作为词典
        _load_txt_file( _path );

        _hash_words();
    }

    protected int check( String _text, int _idx, int _match, boolean _min ) {
        boolean flag = false;
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
                    flag = true;
                    if ( _min ) {
                        break;
                    }
                }
            } else {
                //不存在，直接返回
                break;
            }
        }
        if ( match < _match || !flag ) {  //长度必须大于等于1，为词
            match = 0;
        }
        return match;
    }

    /**
     * 检查是否包含敏感词，_min为false时，会进行词典的全匹配
     * @param _text 内容
     * @param _match 最小匹配长度
     * @param _min 是否最小匹配
     * @return
     */
    public Set< String > check( String _text, int _match, boolean _min ){
        Set<String> sets = new HashSet<>();

        for(int i = 0 ; i < _text.length() ; i++){
            int length = check( _text, i, _match, _min );    //判断是否包含敏感字符
            if( length > 0 ){    //存在,加入list中
                sets.add( _text.substring(i, i+length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        return sets;
    }

    /**
     * 替换包含敏感词的内容
     * @param _text
     * @param _replace
     * @param _match
     * @param _min
     * @return
     */
    public String replace( String _text, String _replace, int _match, boolean _min ){
        String str = _text;

        Set<String> sets = check( _text, _match, _min );

        for( String sw : sets ){
            str = str.replaceAll( sw, _replace );
        }

        return str;
    }
}
