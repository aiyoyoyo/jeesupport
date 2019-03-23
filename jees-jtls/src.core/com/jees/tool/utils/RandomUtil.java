package com.jees.tool.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
    protected static Random random = new Random( System.currentTimeMillis() );
    // 通用公共算法 ==================================================
    /**
     * 返回指定范围的随机数
     * e.g: _range = 100, return 0~99
     * @param _range
     * @return
     */
    public static int s_random_integer( int _range ){
        return (int )(random.nextDouble() * _range);
    }

    /**
     * 返回指定范围的随机数
     * e.g: _min = 1, _max = 100, return 1~100
     * @param _min
     * @param _max
     * @return
     */
    public static int s_random_integer( int _min , int _max ) {
        if( _max < _min ){
            int tmp = _max;
            _max = _min;
            _min = tmp;
        }
        if( _max == 0 ) _max = _min + 1;
        return random.nextInt( _max ) % ( _max - _min + 1 ) + _min;
    }

    /**
     * 根据概率和范围，给出随机结果是否匹配<br/>
     * eg: _pro = 10, _range = 100, return true/false
     *
     * @param _pro
     * @param _range
     * @return
     */
    public static boolean s_random_probability( int _pro , int _range ) {
        if(_pro == _range) return true;

        float n = 100.F / _range * _pro;

        return random.nextDouble() * 100.F <= n;
    }

    /**
     * 根据概率，给出结果随机是否匹配<br/>
     * eg: _percentage = 15.36 return true/false
     * @param _percentage 百分比
     * @return
     */
    public static boolean s_random_probability( float _percentage ){
        return s_random_probability( (int)( _percentage * 100 ), 10000 );
    }

    public static String[] s_random_str = new String[]{
            "0","1","2","3","4","5","6","7","8","9",
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
    };

    public static final String RandomString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 生成对应长度的随机字符串，字符范围[A-Z][a~z][0-9]
     * 每千万次，8位在千万分之1
     * @param _length
     * @return
     */
    public static String  s_random_string( int _length ){
        return RandomStringUtils.random( _length, RandomString );
    }

    /**
     * 根据权重数组，随机一个返回索引
     * @param _widgets
     * @return
     */
    public static int   s_random_widget( int[] _widgets ){
        int odds[] = new int[ _widgets.length];
        int range = 0;
        int idx = 0;

        int max_idx = 0;
        int max_widget = 0;
        for( int w : _widgets ) {
            range += w;
            if( idx == 0 ) odds[idx] = w;
            else odds[idx] = odds[idx-1] + w;

            if( w > max_widget ){
                max_widget = w;
                max_idx = idx;
            }
            idx ++;
        }

        int result = s_random_integer( range );

        for( int i = 0; i < odds.length; i ++ )
            if( result < odds[i] ) return i;

        return max_idx;
    }

    /**
     * 从set中随机取得一个元素
     * @param _sets
     * @return
     */
    public static <E> E s_random_element( Set<E> _sets ){
        int r = s_random_integer( _sets.size() );
        int i = 0;
        for ( E e : _sets ) {
            if( i == r ){
                return e;
            }
            i++;
        }
        return null;
    }
}
