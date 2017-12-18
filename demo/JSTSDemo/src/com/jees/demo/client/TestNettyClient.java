package com.jees.demo.client ;

import io.netty.bootstrap.Bootstrap ;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture ;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption ;
import io.netty.channel.EventLoopGroup ;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup ;
import io.netty.channel.socket.nio.NioSocketChannel ;

public class TestNettyClient {
	private EventLoopGroup	worker ;
	private Bootstrap		booter ;
	private ChannelFuture	future ;

	public TestNettyClient( int _cmd ) {
		try {
			String host = "localhost" ;
			int port = 1000 ;
			worker = new NioEventLoopGroup() ;
			booter = new Bootstrap() ;
			booter.group( worker ).channel( NioSocketChannel.class ).option( ChannelOption.SO_KEEPALIVE , true ).handler( new SimpleChannelInboundHandler< ByteBuf >() {
				@Override
				protected void channelRead0( ChannelHandlerContext ctx , ByteBuf buf ) throws Exception {
					System.out.println( "TestNettyClient read sever msg: MSG=[" + buf.readInt() + "]" );
				}
			} );
			future = booter.connect( host , port ).sync() ;
			
			Channel chl = future.channel();
			ByteBuf buf = chl.alloc().buffer();
			
			buf.writeInt( _cmd );
			if( _cmd == 1 ){
				buf.writeInt( 1 );
			}
			chl.writeAndFlush( buf );
			
			future.channel().closeFuture().sync() ;
		} catch ( Exception e ) {
			e.printStackTrace() ;
		} finally {
			worker.shutdownGracefully() ;
		}
	}

	public static void main( String[] args ) {
		new TestNettyClient( 1 );
	}
}
