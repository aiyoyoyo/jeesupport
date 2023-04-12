package com.jees.server.support;

import com.jees.server.interf.ISuperUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端连接管理器
 *
 * @param <ID>  用户ID
 * @param <NET> 网络对象
 * @author aiyoyoyo
 */
@Service
@Log4j2
@SuppressWarnings("unchecked")
public class Session<ID, NET> {
    Map<NET, ISuperUser<ID, NET>> net2usr = new ConcurrentHashMap<>();
    // 是否有效用户，以下数据为准
    Map<ID, NET> id2net = new ConcurrentHashMap<>();

    /**
     * 根据用户ID查找用户
     *
     * @param _id
     * @param <T>
     * @return
     */
    public <T extends ISuperUser<ID, NET>> T findById(ID _id) {
        NET net = id2net.getOrDefault(_id, null);
        return (T) net2usr.getOrDefault(net, null);
    }

    /**
     * 根据网络对象查找用户
     *
     * @param _net
     * @param <T>
     * @return
     */
    public <T extends ISuperUser<ID, NET>> T findByNet(NET _net) {
        return (T) net2usr.getOrDefault(_net, null);
    }

    // 客户端行为方法

    /**
     * 用户进入并连接
     *
     * @param _user
     * @param <T>
     */
    public <T extends ISuperUser<ID, NET>> void enter(T _user) {
        NET net = _user.getNet();
        net2usr.put(net, _user);
    }

    public <T extends ISuperUser<ID, NET>> void isValid(T _user) {
        ID id = _user.getId();
        NET net = _user.getNet();

        // 通过ID识别同一个用户发生网络变换
        if (id2net.containsKey(id)) {
            T user = (T) net2usr.get(id2net.get(id));
            user.switchover(net);
        } else {
            id2net.put(id, net);
        }
    }

    public <T extends ISuperUser<ID, NET>> void leave(T _user) {
        ID id = _user.getId();
        NET net = _user.getNet();
        if (net2usr.containsKey(net)) {
            net2usr.remove(net);
        }
        if (id != null && id2net.containsKey(id)) {
            id2net.remove(id);
        }
    }
}
