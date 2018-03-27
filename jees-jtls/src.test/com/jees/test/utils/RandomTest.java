package com.jees.test.utils;

import com.jees.common.CommonLogger;
import com.jees.tool.utils.RandomUtil;
import org.junit.Test;

public class RandomTest {

    @Test
    public void testRandomInteger(){
        for( int i = 0; i < 10; i ++)
            CommonLogger.getLogger().debug( "--随机范围0/1/10/100/1000结果：" +
                RandomUtil.s_random_integer( 0 ) + "," +
                RandomUtil.s_random_integer( 1 ) + "," +
                RandomUtil.s_random_integer( 10 ) + "," +
                RandomUtil.s_random_integer( 100 ) + "," +
                RandomUtil.s_random_integer( 1000 ) );
    }

    @Test
    public void testRandomIntegerMM(){
        for( int i = 0; i < 10; i ++)
            CommonLogger.getLogger().debug( "--随机范围0~0/0~1/0~10/99~100/900~1000结果：" +
                RandomUtil.s_random_integer( 0,0 ) + "," +
                RandomUtil.s_random_integer( 1,2 ) + "," +
                RandomUtil.s_random_integer( 10,0 ) + "," +
                RandomUtil.s_random_integer( 99,100 ) + "," +
                RandomUtil.s_random_integer( 900,1000 ) );
    }

    private int _random_probability_count( int _pro, int _range ){
        int count = 0;
        for( int i = 0; i < _range; i ++)
            if( RandomUtil.s_random_probability( _pro, _range ) )
                count ++;

        return count;
    }

    private int _random_probability_count( float _pro ){
        int count = 0;
        for( int i = 0; i < 10000; i ++)
            if( RandomUtil.s_random_probability( _pro ) )
                count ++;

        return count;
    }

    @Test
    public void testRandomProbability(){
        for( int i = 0; i < 10; i ++)
            CommonLogger.getLogger().debug( "--随机范围次数概率0%/15%/75%/120%/0.01%结果：" +
                _random_probability_count( 0,100 ) + "," +
                _random_probability_count( 15,100 ) + "," +
                _random_probability_count( 75,100 ) + "," +
                _random_probability_count( 120,100 ) + "," +
                _random_probability_count( 1,10000 ) );

        for( int i = 0; i < 10; i ++)
            CommonLogger.getLogger().debug( "--随机1万次概率1.0/0.5/0.25/0.01/0.0001结果：" +
                    _random_probability_count( 1.F ) + "," +
                    _random_probability_count( 0.5F ) + "," +
                    _random_probability_count( 0.25F ) + "," +
                    _random_probability_count( 0.01F ) + "," +
                    _random_probability_count( 0.0001F ) );
    }

    @Test
    public void testRandomString(){
        int num_char_min = 48, num_char_max = 57; //0~9
        int ltl_char_min = 65, ltl_char_max = 90; //A~Z
        int ltu_char_min = 97, ltu_char_max = 122;//a~z

        int len = 10;

        String str = "";
        while( str.length() < len ){
            int r = RandomUtil.s_random_integer( 48, 122 );
            if( ( r > 57 && r < 65 ) || ( r > 90 && r < 97 ) ) continue;

            str += ( char ) r;
        }


    }
}
