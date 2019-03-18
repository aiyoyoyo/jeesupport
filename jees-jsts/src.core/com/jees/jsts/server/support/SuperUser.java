package com.jees.jsts.server.support;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @param <ID>
 * @author aiyoyoyo
 */
@Getter
@Setter
public abstract class SuperUser < ID > implements Serializable {
	protected ID id;
	protected ChannelHandlerContext net;

	public abstract void enter ( ChannelHandlerContext _net );

	public abstract void leave ();

	public abstract void switchover ( ChannelHandlerContext _net );

    public abstract void standby ();

	public abstract void recovery ();
}
