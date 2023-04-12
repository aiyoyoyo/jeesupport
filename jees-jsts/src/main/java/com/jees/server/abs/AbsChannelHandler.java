package com.jees.server.abs;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.annotation.MessageLabel;
import com.jees.server.annotation.MessageRequest;
import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.ISuperUser;
import com.jees.server.message.Message;
import com.jees.server.message.MessageCrypto;
import com.jees.server.message.MessageException;
import com.jees.server.message.MessageFile;
import com.jees.server.support.ProxyInterface;
import com.jees.server.support.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端连接的请求处理器，可以用作通用的消息发送器。
 */
@Log4j2
@SuppressWarnings("unchecked")
public abstract class AbsChannelHandler<NET extends ChannelHandlerContext, ID> implements IChannelHandler<NET> {

    private static Map<Integer, Class<?>> handlerClases = new HashMap<>();
    private static Map<Integer, Method> handlerMethod = new HashMap<>();

    private static Map<Integer, String> RequestLabels = new HashMap<>();
    private static Map<Integer, String> ResponseLabels = new HashMap<>();
    private static Map<Integer, String> ErrorLabels = new HashMap<>();

    @Autowired
    Session<ID, NET> session;

    @Override
    public void onload() {
        _load_request_labels();
        _load_response_labels();
        _load_error_labels();

        _handler_register();

        MessageCrypto.registProxy();
    }

    private void _load_request_labels() {
        if (RequestLabels.size() > 0) return;
        boolean request = CommonConfig.getBoolean("jees.jsts.message.request.enable", false);
        if (!request) return;
        String clazz_str = CommonConfig.getString("jees.jsts.message.request.clazz");

        String[] clses = clazz_str.split(",");
        for (String cls : clses) {
            try {
                Class c = Class.forName(cls.trim());

                Object ir = ProxyInterface.newInstance(new Class[]{c});
                Field[] fields = c.getDeclaredFields();
                for (Field f : fields) {
                    try {
                        RequestLabels.put(f.getInt(ir), f.getAnnotation(MessageLabel.class).value());
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                log.error(cls + "包含MessageLabel注解的接口发生错误：", e);
            }
        }
    }

    private void _load_response_labels() {
        if (ResponseLabels.size() > 0) return;
        boolean handler = CommonConfig.getBoolean("jees.jsts.message.handler.enable", false);
        if (!handler) return;
        String clazz_str = CommonConfig.getString("jees.jsts.message.handler.clazz");

        String[] clses = clazz_str.split(",");
        for (String cls : clses) {
            try {
                Class c = Class.forName(cls.trim());

                Object ir = ProxyInterface.newInstance(new Class[]{c});
                Field[] fields = c.getDeclaredFields();
                for (Field f : fields) {
                    try {
                        ResponseLabels.put(f.getInt(ir), f.getAnnotation(MessageLabel.class).value());
                    } catch (Exception e) {
                        continue;
                    }
                }
            } catch (Exception e) {
                log.error(cls + "包含MessageLabel注解的接口发生错误：", e);
            }
        }
    }

    private void _load_error_labels() {
        if (ErrorLabels.size() > 0) return;
        boolean error = CommonConfig.getBoolean("jees.jsts.message.error.enable", false);
        if (!error) return;
        String cls = CommonConfig.getString("jees.jsts.message.error.clazz");
        try {
            Class c = Class.forName(cls);

            Object ir = ProxyInterface.newInstance(new Class[]{c});
            Field[] fields = c.getDeclaredFields();

            for (Field f : fields) {
                try {
                    ErrorLabels.put(f.getInt(ir), f.getAnnotation(MessageLabel.class).value());
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            log.error("包含MessageLabel注解的接口发生错误：" + cls);
        }
    }

    private void _handler_register() {
        log.debug("@MessageRequest命令注册...");
        Collection<Object> msg_coll = CommonContextHolder.getApplicationContext().getBeansWithAnnotation(MessageRequest.class).values();
        if (msg_coll.size() == 0)
            log.debug("--未找到任何包含@MessageRequest类。");
        msg_coll.forEach(b -> {
            Method[] mths = b.getClass().getMethods();
            String cls_name = b.getClass().getName();
            for (Method m : mths) {
                MessageRequest mr = AnnotationUtils.findAnnotation(m, MessageRequest.class);
                if (mr != null) {
                    int cmd = mr.value();
                    if (handlerClases.containsKey(cmd)) {
                        String use_mth = handlerClases.get(cmd).getName() + "." + handlerMethod.get(cmd).getName();

                        log.warn("命令重复：CMD[" + cmd + "], 当前[" + cls_name + "." + m.getName() + "], 已使用[" + use_mth + "]");
                    } else {
                        handlerClases.put(cmd, b.getClass());
                        handlerMethod.put(cmd, m);
                        log.debug("--注册@MessageRequest命令: CMD[" + cmd + "], MTH=[" + m.getName() + "], CLS=[" + cls_name + "]");
                    }
                }
            }
        });
        log.debug("@MessageRequest命令注册成功：SIZE[" + handlerMethod.size() + "]");
    }

    @Override
    public void request(NET _net, Object _obj) {
        long time = System.currentTimeMillis();
        if (_obj != null) {
            this.handler(_net, _obj);
        }
        if (CommonConfig.getBoolean("jees.jsts.message.monitor", false)) {
            time = System.currentTimeMillis() - time;
            log.debug("--命令用时: TIME=[" + time + "]");
        }
    }

    @SuppressWarnings("unchecked")
    public void handler(NET _net, Object _obj) {
        ISuperUser<ID, NET> user = session.findByNet(_net);
        ID id = user.getId();
        NET net = user.getNet();

        Object msg = MessageCrypto.deserializer(_obj, user.isWebSocket());

        boolean debug = CommonConfig.getBoolean("jees.jsts.message.request.enable", false);
        boolean exception = CommonConfig.getBoolean("jees.jsts.message.request.exception", false);

        Integer cmd = null;
        if (msg instanceof Message) {
            Message<ID> m = (Message) msg;
            cmd = (m).getId();
            m.setUserId(user.getId());
            MessageFile.write(cmd, id, m, false);
        } else if (msg instanceof JSONObject) {
            JSONObject job = (JSONObject) msg;
            cmd = (job).getInteger("id");
            MessageFile.write(cmd, id, job, false);
        } else {
            // 代理类
            String json = msg.toString();
            cmd = JSON.parseObject(json).getInteger("id");
            MessageFile.write(cmd, id, json, false);
        }

        if (debug) {
            String label = RequestLabels.getOrDefault(cmd, "未注解命令");
            log.info("\n  [C][" + id + "][" + cmd + "][" + label + "]:" + msg.toString());
        }

        if (before(_net, cmd)) {
            if (handlerClases.containsKey(cmd)) {
                Method m = handlerMethod.get(cmd);
                try {
                    ReflectionUtils.invokeMethod(m, CommonContextHolder.getBean(handlerClases.get(cmd)), _net, msg);
                } catch (MessageException me) {
                    //程序异常，可以通知给客户端
                    if (exception) {
                        log.error("错误ME: U=[" + id + "] I=[" + cmd + "] M=[" + m.getName() + "]:" + me.getMessage(), me);
                    } else {
                        log.error("错误ME: U=[" + id + "] I=[" + cmd + "] M=[" + m.getName() + "]:" + me.getMessage());
                    }

                    me.getMsg().setRequestId(cmd);
                    error(_net, me);
                    // 后续两种错误不应该发生。RE为数据库操作错误,EX为程序错误
                } catch (RuntimeException re) {
                    log.error("错误RE: U=[" + id + "] I=[" + cmd + "] M=[" + m.getName() + "]", re);
                } catch (Exception ex) {
                    log.error("错误EX: U=[" + id + "] I=[" + cmd + "] M=[" + m.getName() + "]", ex);
                } finally {
                    after(_net);
                }
            } else {
                log.warn("命令没有注册：U=[" + id + "] CMD=[" + cmd + "]");
                unregist(_net, msg);
            }
        }
    }

    /**
     * 服务器写回给客户端
     *
     * @param _obj 数据内容
     * @param _net 客户端对象
     */
    @Override
    public void response(Object _obj, NET _net) {
        boolean proxy = CommonConfig.getBoolean("jees.jsts.message.proxy", true);

        boolean debug = CommonConfig.getBoolean("jees.jsts.message.handler.enable", false);
        boolean error = CommonConfig.getBoolean("jees.jsts.message.error.enable", false);

        int cmd;
        ISuperUser<ID, NET> user = session.findByNet(_net);
        ID id = user.getId();
        if (proxy) {
            Message msg = JSON.parseObject(_obj.toString(), Message.class);
            cmd = msg.getId();
            MessageFile.write(cmd, id, msg, true);
        } else {
            JSONObject obj = JSON.parseObject(_obj.toString());

            try {
                cmd = obj.getInteger("id");
            } catch (Exception e) {
                cmd = 0;
            }
            MessageFile.write(cmd, id, obj, true);
        }
        if (debug) {
            String label = ResponseLabels.getOrDefault(cmd, "未注解命令");
            if (label.equals("") && error) {
                label = ErrorLabels.getOrDefault(cmd, "未注解命令");
                label = "\n  [S][ERR][" + cmd + "][" + label + "][" + id + "]:";
            } else label = "\n  [S][" + cmd + "][" + label + "][" + id + "]:";
            log.info(label + _obj.toString());
        }

        final ByteBuf buf = _net.alloc().buffer();
        Object msg = MessageCrypto.serializer(buf, _obj, user.isWebSocket());
        _net.writeAndFlush(msg);
        if (buf.refCnt() == 1) {
            log.debug("--内存检测，判定再次释放：" + System.identityHashCode(buf));
            buf.release();
        }
    }

    @Override
    public void enter(NET _net, boolean _ws) {
        ISuperUser<ID, NET> user = CommonContextHolder.getBean(ISuperUser.class);
        user.setWebSocket(_ws);
        user.setNet(_net);
        user.enter();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void leave(NET _net) {
        ISuperUser<ID, NET> user = session.findByNet(_net);
        if (user != null) user.leave();
    }

    @Override
    public void standby(NET _net) {
        ISuperUser<ID, NET> user = session.findByNet(_net);
        if (user != null) user.standby();
    }

    @Override
    public void recovery(NET _net) {
        ISuperUser<ID, NET> user = session.findByNet(_net);
        if (user != null) user.recovery();
    }

    @Override
    public void trigger(NET _net, Object _obj) {
        ISuperUser<ID, NET> user = session.findByNet(_net);
        if (user != null) user.trigger(_obj);
    }
}
