package com.jees.jsts.server.abs;

import com.jees.jsts.server.interf.IConnectroHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 用作服务器通讯客户端的连接器，需要自行处理相关事件
 */
public abstract class AbsConnectorHandler extends ChannelInboundHandlerAdapter implements IConnectroHandler {
}
