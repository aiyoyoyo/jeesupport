package com.jees.jsts.server.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ProxyInterface implements InvocationHandler {
    private Map<Object, Object> map = null;

    public static Object newInstance(Class[] interfaces) {
        return Proxy.newProxyInstance(ProxyInterface.class.getClassLoader(),
                interfaces, new ProxyInterface());
    }

    private ProxyInterface() {
        this.map = new HashMap<Object, Object>();
    }

    @Override
    public Object invoke ( Object proxy, Method method, Object[] args ) throws Throwable {
        Object result = null;
        String methodName = method.getName();
        if (methodName.startsWith("get")) {
            String name = methodName.substring(methodName.indexOf("get") + 3);
            return map.get(name);
        } else if (methodName.startsWith("set")) {
            String name = methodName.substring(methodName.indexOf("set") + 3);
            map.put(name, args[0]);
            return null;
        } else if (methodName.startsWith("is")) {
            String name = methodName.substring(methodName.indexOf("is") + 2);
            return (map.get(name));
        }
        return result;
    }
}
