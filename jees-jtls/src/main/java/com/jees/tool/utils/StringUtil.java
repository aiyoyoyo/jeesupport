package com.jees.tool.utils;

public class StringUtil {

    public static <T> T cover(String _val, Class<T> _t) {
        return cover( _val, _t, null );
    }

    public static <T> T cover(String _val, Class<T> _t, T _def) {
        if( _t.equals( Integer.class ) ){
            return (T) cover2int( _val, (Integer) _def);
        }else if( _t.equals( Float.class ) ){
            return (T) cover2float( _val, (Float) _def);
        }else if( _t.equals( Boolean.class ) ){
            return (T) cover2boolean( _val, (Boolean) _def);
        }else if( _t.equals( Long.class ) ){
            return (T) cover2long( _val, (Long) _def);
        }else if( _t.equals( Byte.class ) ){
            return (T) cover2byte( _val, (Byte) _def);
        }else if( _t.equals( Double.class ) ){
            return (T) cover2double( _val, (Double) _def);
        }else{
            return (T) _val;
        }
    }

    public static Integer cover2int( String _val, Integer _def ){
        try{
            return Integer.parseInt( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static Float cover2float( String _val, Float _def ){
        try{
            return Float.parseFloat( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static Boolean cover2boolean( String _val, Boolean _def ){
        try{
            return Boolean.parseBoolean( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static Double cover2double(String _val, Double _def) {
        try{
            return Double.parseDouble( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static Byte cover2byte(String _val, Byte _def) {
        try{
            return Byte.parseByte( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static Long cover2long(String _val, Long _def) {
        try{
            return Long.parseLong( _val );
        }catch ( Exception e ){
            return _def;
        }
    }

    public static boolean isEmpty(String _val){
        if( _val == null || _val.trim().isEmpty() ){
            return true;
        }
        return false;
    }

    public static boolean isNotEmpty(String _val){
        return !isEmpty(_val);
    }

    public static boolean isNull(String _val){
        if( _val == null ){
            return true;
        }else{
            String val = _val.trim();
            if( val.isEmpty() || val.equalsIgnoreCase( "NULL" ) ){
                return true;
            }
        }
        return false;
    }

    public static boolean isNotNull(String _val){
        return !isNull(_val);
    }
}
