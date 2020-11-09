package com.jees.test.utils;

import com.jees.tool.utils.DataUtil;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@Log4j2
public class DataTest {
    @Test
    public void test() throws UnsupportedEncodingException {
//        log.debug( "int 2 bytes:" + Arrays.toString( DataUtil.int2bytes( 1 ) ) ); //1->0001
//        log.debug( "bytes 2 int:" + DataUtil.bytes2int( new byte[]{ 0, 0, 0, 1 } ) );  //0001->1
//        log.debug( "str 2 bytes:" + Arrays.toString( DataUtil.str2bytes( "1", "UTF-8" ) )  );  //1->49
//        log.debug( "bytes 2 str:" + DataUtil.bytes2str( new byte[]{ 49 }, "UTF-8" ) );  //49->1
//        log.debug( "warp high 2 low 2 int:" + DataUtil.warpHL( 1 ) );  //1->16777216
//        log.debug( "warp high 2 low 2 long:" + DataUtil.warpHL( 1L ) );  //1->72057594037927936
//        log.debug( "sub bytes 2 bytes:" + Arrays.toString( DataUtil.subBytes( new byte[]{ 0, 0, 0, 1 }, 2, 4 ) ) );  //0,0,0,1->0,1
//        log.debug( "long 2 bytes:" + Arrays.toString( DataUtil.long2bytes( 1L ) ) );  //1->00000001
//        log.debug( "bytes 2 long:" + DataUtil.bytes2long( new byte[]{ 0,0,0,0, 0, 0, 0, 1 } ) );  //00000001->1

        log.debug( DataUtil.bytes_2_hex( new byte[]{48}));
    }

    @Test
    public void test2(){
        log.debug( "10-->2---------------------------");
        log.debug(Integer.toBinaryString(0));
        log.debug(Integer.toBinaryString(1));
        log.debug(Integer.toBinaryString(2));
        log.debug(Integer.toBinaryString(8));
        log.debug(Integer.toBinaryString(10));
        log.debug(Integer.toBinaryString(16));
        log.debug( "10-->8---------------------------");
        log.debug(Integer.toOctalString(0));
        log.debug(Integer.toOctalString(1));
        log.debug(Integer.toOctalString(2));
        log.debug(Integer.toOctalString(8));
        log.debug(Integer.toOctalString(10));
        log.debug(Integer.toOctalString(16));
        log.debug( "10-->16---------------------------");
        log.debug(Integer.toHexString(0));
        log.debug(Integer.toHexString(1));
        log.debug(Integer.toHexString(2));
        log.debug(Integer.toHexString(8));
        log.debug(Integer.toHexString(10));
        log.debug(Integer.toHexString(16));
        log.debug( "2-->10---------------------------");
        log.debug(Integer.parseInt( "0", 2 ) );
        log.debug(Integer.parseInt( "1", 2 ) );
        log.debug(Integer.parseInt( "10", 2 ) );
        log.debug(Integer.parseInt( "1000", 2 ) );
        log.debug(Integer.parseInt( "1010", 2 ) );
        log.debug(Integer.parseInt( "10000", 2 ) );
    }
}
