package com.jees.loader.util;

import com.jees.loader.PluginLoader;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URL;
import java.util.function.Consumer;

/**
 * 插件的类都在这里维护
 */
@Log4j2
public class PluginUtil {
    static Map<String,Class<?>> classes = new ConcurrentHashMap<>();
    static Map<String, byte[]> classesBytes = new HashMap<>();

    public static void put(String _name, Class<?> _cls, byte[] new_class_bytes){
        classes.put(_name, _cls);
        classesBytes.put(_name, new_class_bytes);
    }

    public static Class<?> get(String _name){
        return classes.get(_name);
    }

    public static void remove(String _name){
        classes.remove(_name);
        classesBytes.remove(_name);
    }
    public static boolean contains(String _name){
        return classes.containsKey(_name);
    }
    public static void loadPlugin(String _plugin) throws IOException {
        PluginUtil.loadPlugin( _plugin, null, null );
    }
    public static void loadPlugin(String _plugin, Consumer<String> _classHandler) throws IOException {
        PluginUtil.loadPlugin( _plugin, _classHandler, null );
    }
    /**
     * @param _plugin 完整的jar包路径
     * @param _classHandler 加载Class回调，主要用于卸载SpringBean
     * @param _resourceHandler 加载资源回调，主要用于写入资源文件
     * @throws IOException 文件读取错误
     */
    public synchronized static void loadPlugin(String _plugin, Consumer<String> _classHandler, Consumer<Map<String, byte[]>> _resourceHandler) throws IOException {
        String plugin_file = _plugin + "!/";
        String plugin_file_path = "jar:file:" + plugin_file;
        log.debug("加载插件：{}", plugin_file_path);
        // 动态加载对应插件
        URL plugin_url = new URL(plugin_file_path);
        try (PluginLoader loader = new PluginLoader(plugin_url,  ClassLoader.getSystemClassLoader())) {
            // 处理class
            Set<String> clazz_set = loader.getJarClassName();
            for( String name : clazz_set ){
                log.debug("加载类：{}", name);
                byte[] new_class_bytes = loader.getClassBytes(name);
                boolean same_class = false;
                if( classesBytes.containsKey(name) ){
                    byte[] old_class_bytes = classesBytes.get(name);
                    same_class = new_class_bytes.length == old_class_bytes.length;
                    log.debug("对比加载的业务类：{}={}", new_class_bytes.length,old_class_bytes.length);
                }
                if( !same_class ){
                    Class<?> clazz = loader.loadClass(name); //
                    if( _classHandler != null && PluginUtil.contains(name) ){// 还存在旧的类
                        log.debug("尝试卸载当前业务类{}！", name);
                        _classHandler.accept(name);
                    }
                    log.debug("加载模块业务类：{}", name);
                    PluginUtil.put(name, clazz, new_class_bytes);
                }else{
                    log.debug("忽略未变化的类：{}", name);
                }
            }
            if( _resourceHandler != null ){
                // 将资源写入项目目录
                _resourceHandler.accept( loader.getResourceMap() );
            }
        }catch (Exception e) {
            log.error("加载插件失败：", e);
        }
    }

    public static Class<?> forName(String _name) throws ClassNotFoundException {
        if( PluginUtil.contains(_name) ){
            return PluginUtil.get(_name);
        }
        throw new ClassNotFoundException(_name);
    }
}
