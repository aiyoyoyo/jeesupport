package com.jees.server.interf;

import io.netty.channel.ChannelHandlerContext;

/**
 * Socket服务的内容接口，客户端行为
 *
 * @param <NET> 客户端对象模版
 * @author aiyoyoyo
 */
public interface IChannelHandler<NET extends ChannelHandlerContext> {
    void onload();

    /**
     * 接收数据
     *
     * @param _net 客户端对象
     * @param _obj 数据内容
     */
    void request(NET _net, Object _obj);

    /**
     * 发送数据
     *
     * @param _net 客户端对象
     * @param _obj 数据内容
     */
    void response(Object _obj, NET _net);

    /**
     * 连接建立
     *
     * @param _net 客户端对象
     * @param _ws  是否WebSocket
     */
    void enter(NET _net, boolean _ws);

    /**
     * 连接断开
     *
     * @param _net 客户端对象
     */
    void leave(NET _net);

    /**
     * 连接待机
     *
     * @param _net 客户端对象
     */
    void standby(NET _net);

    /**
     * 待机恢复
     *
     * @param _net 客户端对象
     */
    void recovery(NET _net);

    /**
     * 事件触发器
     *
     * @param _net 网络对象
     * @param _obj 事件
     */
    void trigger(NET _net, Object _obj);

    /**
     * 连接过程中出现错误
     *
     * @param _net 客户端对象
     * @param _thr 错误类型
     */
    void error(NET _net, Throwable _thr);

    /**
     * 命令解析前置验证
     *
     * @param _net 网络对象
     * @param _cmd 命令
     * @return 结果
     */
    boolean before(NET _net, int _cmd);

    /**
     * 命令处理结束后置处理
     *
     * @param _net 网络对象
     */
    void after(NET _net);

    /**
     * 接受未注册命令通知
     *
     * @param _net 网络对象
     * @param _msg 消息
     */
    void unregist(NET _net, Object _msg);
}
