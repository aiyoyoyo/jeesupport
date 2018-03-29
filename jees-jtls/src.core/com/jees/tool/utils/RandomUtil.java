package com.jees.tool.utils;

import java.util.Random;

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

    /**
     * 生成对应长度的随机字符串，字符范围[A-Z][a~z][0-9]
     * @param _length
     * @return
     */
    public static String  s_random_string( int _length ){
        int num_char_min = 48, num_char_max = 57; //0~9
        int ltl_char_min = 65, ltl_char_max = 90; //A~Z
        int ltu_char_min = 97, ltu_char_max = 122;//a~z

        String str = "";
        while( str.length() < _length ){
            int r = RandomUtil.s_random_integer( 48, 122 );

            // 跳过特殊字符
            if( ( r > num_char_max && r < ltl_char_min ) || ( r > ltl_char_max && r < ltu_char_min ) ) continue;

            str += ( char ) r;
        }

        return str;
    }
}
