package com.jees.core.socket.support;

/**
 * 命令的对接内容
 * @author aiyoyoyo
 *
 * @param <C>
 */
public interface ISupportCommand<C, M> {

	public String	CMD_SERVICE	= "commandService" ;
	/**
	 * 命令实现入口，通过命令在分解并利用反射来调用对应的方法
	 * @param _ctx
	 * @param _buf
	 */
	public void docommand( C _ctx , M _buf ) ;
}
