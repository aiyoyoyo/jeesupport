package com.jees.server.interf;

/**
 * 需要由应用自定义SuperUser的继承类,注意Scope需要声明为prototype
 * @param <ID> 唯一ID
 * @param <NET> 网络对象
 */
public interface ISuperUser<ID, NET>{
    // 属性 ///////////////////
    ID getId();
    void setId( ID _id );
    NET getNet();
    void setNet( NET _net );
    boolean isWebSocket();
    void setWebSocket( boolean _ws );

    // 事件 ///////////////////
    /**
     * 建立连接
     */
    void enter ();
    /**
     * 断开连接
     */
    void leave ();
    /**
     * 网络切换
     * @param _net 网络对象
     */
    void switchover ( NET _net );
    /**
     * 超时挂起
     */
    void standby ();
    /**
     * 挂起恢复
     */
    void recovery ();
    /**
     * 事件触发
     * @param _obj 触发事件
     */
    void trigger( Object _obj );

    // 消息 ///////////////////
    void sender( Object _obj );

    void flush();
}
