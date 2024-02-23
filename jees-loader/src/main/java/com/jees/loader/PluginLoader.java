package com.jees.loader;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Log4j2
public class PluginLoader extends URLClassLoader {
    static final int BUFFER_SIZE = 4096;
    final static String CLASS_SUFFIX = ".class";
    final Map<String, byte[]> classMap = new HashMap<>();

    @Getter
    final Map<String, byte[]> resourceMap = new HashMap<>();
    final Map<String, Class<?>> classLoad = new HashMap<>();
    public PluginLoader(URL url, ClassLoader parent) throws IOException {
        super(new URL[]{url}, parent);
        _parse_jar(url);
    }

    private void _parse_jar(URL url) throws IOException {
        String path = url.getPath();
        path = path.substring(5, path.length() - 2);
        JarFile jarFile = new JarFile(path);

        //解析jar包每一项
        Enumeration<JarEntry> en = jarFile.entries();
        InputStream input = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            while (en.hasMoreElements()) {
                JarEntry jarEntry = en.nextElement();
                String name = jarEntry.getName();
                input = jarFile.getInputStream(jarEntry);
                byte[] buffer = new byte[BUFFER_SIZE];
                int index;
                while ((index = input.read(buffer)) != -1) {
                    bos.write(buffer, 0, index);
                }
                byte[] file_bytes = bos.toByteArray();
                bos.reset();

                if (name.endsWith(CLASS_SUFFIX)) {
                    String className = name.replace(CLASS_SUFFIX, "").replaceAll("/", ".");
                    classMap.put(className, file_bytes);
                }else{
                    // 其他内容加载到资源对象下，仅处理文件，以.xx为文件
                    if(name.contains(".")){
                        // 动态加载资源
                        resourceMap.put( name, file_bytes );
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName() + " " + e.getMessage(), e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if(classLoad.containsKey(name)){
            return classLoad.get(name);// 防止二次加载
        }
        // 改变加载逻辑,插件规范里边的类,直接加载本地的class
        Class<?> clazz;
        try {
            clazz = _load_launched_class(name);
            classLoad.put(name, clazz);
        } catch (ClassNotFoundException ignore) {
            // 加载不了的,应该交由父类去加载
        }

        // 其余的类还是交由父类去完成
        clazz = super.loadClass(name);
        classLoad.put(name, clazz);
        return clazz;
    }
    private Class<?> _load_launched_class(String name) throws ClassNotFoundException {
        byte[] classBytes = classMap.get(name);
        if (classBytes == null) {
            throw new ClassNotFoundException(name);
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(classBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", e);
        }
    }
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        String className = name.replace(CLASS_SUFFIX, "").replaceAll("/", ".");

        byte[] bytes = classMap.get(className);
        if (bytes == null) {
            try{
                return Class.forName(className);
            }catch (Exception e){
                throw new ClassNotFoundException(name);
            }
        }

        return this.defineClass(name, bytes, 0, bytes.length);
    }
    public Set<String> getJarClassName(){
        return this.classMap.keySet();
    }
    public byte[] getClassBytes(String name) throws ClassNotFoundException {
        byte[] bytes = classMap.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }
        return bytes;
    }
}
