package com.jees.common;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 以静态变量保存Spring ApplicationContext,可在任何代码任何地方任何时候中取出ApplicaitonContext.
 *
 * @author aiyoyoyo
 */
@Log4j2
@Component
public class CommonContextHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        log.debug("--初始化Spring组件：" + applicationContext);
        CommonContextHolder.applicationContext = applicationContext; // NOSONAR
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     *
     * @return ApplicationContext对象
     */
    public static ApplicationContext getApplicationContext() {
        _check_application_context();
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param _name 名称
     * @param <T>   对象类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String _name) {
        _check_application_context();
        return (T) applicationContext.getBean(_name);
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param _cls 类
     * @param <T>  类型
     * @return 对象
     */
    public static <T> T getBean(Class<T> _cls) {
        _check_application_context();
        Collection<T> beans = applicationContext.getBeansOfType(_cls).values();
        if (beans.isEmpty()) {
            log.warn(_cls.getName() + "没有被注入或者缺少实现类!");
            return null;
        }
        return beans.iterator().next();
    }

    /**
     * 获取通类的所有实例
     *
     * @param _cls 类
     * @param <T>  类型
     * @return 实例集合
     */
    public static <T> Collection<T> getBeans(Class<T> _cls) {
        _check_application_context();
        Collection<T> beans;
        beans = applicationContext.getBeansOfType(_cls).values();
        return beans;
    }

    /**
     * 清除applicationContext静态变量.
     */
    protected static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 检查Spring的applicationContext对象是否正常注入
     */
    private static void _check_application_context() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入, 请检查相关配置。");
        }
    }

    public static void removeBean(String _name){
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) CommonContextHolder.getApplicationContext().getAutowireCapableBeanFactory();
        beanDefinitionRegistry.removeBeanDefinition(_name );
    }

    public static <T> void registerBean(String _name, Class<T> _cls){
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) CommonContextHolder.getApplicationContext().getAutowireCapableBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(_name, BeanDefinitionBuilder.genericBeanDefinition(_cls).getBeanDefinition());
    }
}
