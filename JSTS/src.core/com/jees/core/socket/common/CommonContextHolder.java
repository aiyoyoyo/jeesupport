package com.jees.core.socket.common ;

import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager ;
import org.apache.logging.log4j.Logger ;
import org.springframework.context.ApplicationContext ;
import org.springframework.context.ApplicationContextAware ;
import org.springframework.stereotype.Component ;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 * 
 * @author aiyoyoyo
 */
@Component
public class CommonContextHolder implements ApplicationContextAware {
	private static Logger				logger	= LogManager.getLogger( CommonContextHolder.class ) ;
	private static ApplicationContext	applicationContext ;

	/**
	 * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
	 */
	public void setApplicationContext( ApplicationContext applicationContext ) {
		CommonContextHolder.applicationContext = applicationContext ; // NOSONAR
		logger.info( "所有注入的Spring组件：{" + Arrays.toString( applicationContext.getBeanDefinitionNames() ) + "}" ) ;
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		_check_application_context() ;
		return applicationContext ;
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings( "unchecked" )
	public static < T > T getBean( String _name ) {
		_check_application_context() ;
		return ( T ) applicationContext.getBean( _name ) ;
	}

	/**
	 * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static < T > T getBean( Class< T > _cls ) {
		_check_application_context() ;
		Collection< T > beans = applicationContext.getBeansOfType( _cls ).values();
		if( beans.size() != 1 ) throw new RuntimeException( _cls.getName() + "没有注入或者注入了多个对象。" );
		return beans.iterator().next() ;
	}

	/**
	 * 清除applicationContext静态变量.
	 */
	public static void cleanApplicationContext() {
		applicationContext = null ;
	}

	private static void _check_application_context() {
		if ( applicationContext == null ) { throw new IllegalStateException(
						"applicaitonContext未注入,请在" + ICommonConfig.CFG_DEFAULT
										+ "中定义加入：<context:component-scan /> 以扫描相关类。" ) ; }
	}
}
