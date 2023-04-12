package com.jees.server.abs;

import com.jees.common.CommonConfig;
import com.jees.common.CommonContextHolder;
import com.jees.server.interf.IChannelHandler;
import com.jees.server.interf.ISuperUser;
import com.jees.server.support.Sender;
import com.jees.server.support.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 当前服务器的连接对象
 *
 * @param <ID> 用户ID
 * @author aiyoyoyo
 */
@Getter
@Setter
@Log4j2
public abstract class AbsSuperUser<ID, NET extends ChannelHandlerContext> implements ISuperUser<ID, NET>, Serializable {
    protected ID id;
    protected NET net;
    protected boolean webSocket = false;

    private long standTime = 0L;
    private int standCount = 0;

    protected Sender sender;
    @Autowired
    Session<ID, NET> session;
    @Autowired
    IChannelHandler<NET> handler;

    @Override
    public void enter() {
        session.enter(this);
        sender = CommonContextHolder.getBean(Sender.class);
    }

    @Override
    public void leave() {
        session.leave(this);
    }

    @Override
    public void standby() {
        standCount++;
        log.debug("--" + this.net.toString() + "进入待机第" + standCount + "次。");
        if (standCount >= CommonConfig.getLong("jees.jsts.socket.standMax", 3)) {
            log.debug(this.net.toString() + "待机超时， 断开客户端。");
            this.leave();
        }
    }

    @Override
    public void recovery() {
        log.debug("--" + this.net.toString() + "待机恢复.");
        standCount = 0;
        standTime = 0L;
    }

    @Override
    public void trigger(Object _obj) {
        if (CommonConfig.getBoolean("jees.jsts.socket.standEnable", false)) {
            if (_obj instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) _obj;
                if (event.state() == IdleState.READER_IDLE) {
                    long now = DateTime.now().getMillis();
                    long lost = now - standTime;
                    log.debug("--" + net.toString() + "待机时长:" + lost);
                    if (standTime == 0L) {
                        standTime = now;
                    } else if (lost >= CommonConfig.getLong("jees.jsts.socket.standTime", 30000)) {
                        this.standby();
                    }
                } else if (standCount > 0 && event.state() == IdleState.WRITER_IDLE) {
                    this.recovery();
                }
            } else {
                log.warn(net.toString() + "连接状态丢失");
            }
        }
    }

    @Override
    public void sender(Object _obj) {
        sender.insert(_obj);
    }

    @Override
    public void flush() {
        sender.flush((_o) -> {
            handler.response(_o, this.net);
        });
    }
}
