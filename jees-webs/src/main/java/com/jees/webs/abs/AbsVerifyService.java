package com.jees.webs.abs;

import com.jees.tool.utils.FileUtil;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.ISupportEL;
import com.jees.webs.support.IVerifyService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * AbsVerifyService 抽象实现类，实现了操作权限配置文件verify.cfg的一些方法。
 */
@Log4j2
public class AbsVerifyService implements IVerifyService, ISupportEL {
    @Getter
    @Setter
    Map<Integer, SuperMenu> menus = new HashMap<>();
    @Getter
    @Setter
    Map<Integer, SuperRole> roles = new HashMap<>();
    @Getter
    @Setter
    Map<String, SuperUser> users = new HashMap<>();
    @Getter
    @Setter
    Map<String, Object> blackList = new HashMap<>();
    @Getter
    @Setter
    Map<String, Object> roleList = new HashMap<>();
    @Getter
    @Setter
    Map<String, Object> elementList = new HashMap<>();

    @Override
    public void initialize() {
        this._load_config();
    }

    @Override
    public UserDetails findUserByUsername(String _username) {
        if (this.users.containsKey(_username)) {
            return this.users.get(_username);
        }
        log.debug("--配置文件中查找登录用户信息：U=[" + _username + "]");
        throw new UsernameNotFoundException("配置中用户不存在");
    }

    /**
     * 从verify.cfg配置文件加载配置的总入口
     */
    private void _load_config() {
        Map<String, List<String>> base_map = new HashMap<>();
        Map<String, List<String>> special_map = new HashMap<>();
        Map<String, List<String>> element_map = new HashMap<>();
        AtomicReference<String> base_key = new AtomicReference<>("");
        AtomicReference<String> special_key = new AtomicReference<>("");
        AtomicReference<String> element_key = new AtomicReference<>("");
        FileUtil.read("classpath:config/verify.cfg", (_line) -> {
            if (_line.isEmpty() || _line.trim().startsWith("#")) return;
            _line = _line.trim();
            // 基础配置
            if (_line.startsWith("[/") && _line.endsWith("]")) {
                if (_line.contains("$")) {
                    element_key.set(_line.substring(_line.indexOf("[") + 1, _line.indexOf("]")));
                    element_map.put(element_key.get(), new ArrayList<>());
                    special_key.set("");
                    base_key.set("");
                } else {
                    base_key.set(_line.substring(_line.indexOf("[") + 1, _line.indexOf("]")));
                    base_map.put(base_key.get(), new ArrayList<>());
                    special_key.set("");
                    element_key.set("");
                }
            } else if (_line.startsWith("[:") && _line.endsWith("]")) {
                special_key.set(_line.substring(_line.indexOf("[:") + 2, _line.indexOf("]")));
                special_map.put(special_key.get(), new ArrayList<>());
                element_key.set("");
                base_key.set("");
            } else {
                if (!base_key.get().equals("")) {
                    List<String> base_val = base_map.get(base_key.get());
                    base_val.add(_line);
                    base_map.put(base_key.get(), base_val);
                }
                if (!special_key.get().equals("")) {
                    List<String> special_val = special_map.get(special_key.get());
                    special_val.add(_line);
                    special_map.put(special_key.get(), special_val);
                }
                if (!element_key.get().equals("")) {
                    List<String> element_val = element_map.get(element_key.get());
                    element_val.add(_line);
                    element_map.put(element_key.get(), element_val);
                }
            }
        });
        // 先存入用户、角色、黑名单等特殊配置
        if (!special_map.isEmpty()) this._load_special(special_map);
        log.debug("--配置文件中用户：U=[" + this.users.keySet() + "]");
        // 再存入路径等基础配置
        if (!base_map.isEmpty()) this._load_base(base_map);
        // 再存入路径页面对应的元素配置
        if (!element_map.isEmpty()) this._load_element(element_map);
    }

    /**
     * 从配置文件加载基础配置，形成【用户-路径】数据
     */
    private void _load_base(Map<String, List<String>> _map) {
        AtomicReference<String> public_key = new AtomicReference<>("");
        for (String key : _map.keySet()) {
            List<String> val = _map.get(key);
            val.forEach(_str -> {
                String[] str_arr = _str.split("=");
                if (str_arr.length < 2) return;
                String str0 = str_arr[0].trim();
                String str1 = str_arr[1].trim();
                // 直接解析用户列表
                if (str0.equals("user")) {
                    if (str1.equals("*")) {
                        Set<String> user_set = this.users.keySet();
                        this._set_user_auth(user_set.toArray(new String[user_set.size()]), key);
                        return;
                    }
                    String[] user_arr = str1.split(",");
                    for (String name : user_arr) {
                        SuperUser su = this._get_user(name);
                        Set<SimpleGrantedAuthority> auths = su.getAuthorities();
                        if (!public_key.get().equals("")) {
                            auths.add(new SimpleGrantedAuthority(public_key.get()));
                        }
                        auths.add(new SimpleGrantedAuthority(key));
                        auths.add(new SimpleGrantedAuthority(key.endsWith("/") ? key + "**" : key + "/**"));
                        su.setAuthorities(auths);
                        this.users.put(name, su);
                    }
                } else if (str0.equals("role")) {
                    String[] role_arr = str1.split(",");
                    for (String role : role_arr) {
                        if (this.roleList.containsKey(role)) {
                            String[] user_arr = (String[]) this.roleList.get(role);
                            for (String name : user_arr) {
                                SuperUser su = this._get_user(name);
                                Set<SimpleGrantedAuthority> auths = su.getAuthorities();
                                if (!public_key.get().equals("")) {
                                    auths.add(new SimpleGrantedAuthority(public_key.get()));
                                }
                                auths.add(new SimpleGrantedAuthority(key));
                                auths.add(new SimpleGrantedAuthority(key.endsWith("/") ? key + "**" : key + "/**"));
                                if (key.equals("/")) auths.add(new SimpleGrantedAuthority("/index"));
                                su.setAuthorities(auths);
                                this.users.put(name, su);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 从配置文件加载特殊配置，形成【用户、角色、黑名单】数据
     */
    private void _load_special(Map<String, List<String>> _map) {
        AtomicInteger role_idx = new AtomicInteger(1);
        for (String key : _map.keySet()) {
            List<String> val = _map.get(key);
            val.forEach(_str -> {
                String[] str_arr = _str.split("=");
                if (str_arr.length < 2) return;
                String str0 = str_arr[0].trim();
                String str1 = str_arr[1].trim();
                switch (key) {
                    case "users":
                        // 构造用户信息存入users
                        SuperUser su = new SuperUser();
                        String[] user_props = str1.split(":");
                        su.setUsername(str0);
                        su.setPassword(user_props[0]);
                        su.setEnabled(user_props[1].equals("1"));
                        su.setLocked(user_props[2].equals("1"));
                        this.users.put(str0, su);
                        break;
                    case "roles":
                        // 将角色ID存入用户信息中
                        this._set_user_role(str1.split(","), role_idx.get());
                        // 构造角色信息存入roles
                        SuperRole sr = new SuperRole();
                        sr.setId(role_idx.get());
                        sr.setName(str0);
                        this.roles.put(role_idx.get(), sr);
                        // 将角色名称与角色下的用户名存入roleList
                        this.roleList.put(str0, str1.split(","));
                        role_idx.getAndIncrement();
                        break;
                    case "black":
                        // 存入黑名单信息
                        String[] black = str1.split(",");
                        this.blackList.put(str0, black);
                        break;
                }
            });
        }
    }

    /**
     * 从配置文件加载页面按钮配置，形成 路径:< 标签: [用户]> 数据
     */
    private void _load_element(Map<String, List<String>> _map) {
        for (String key : _map.keySet()) {
            Map<String, Object> ele_map = new HashMap<>();
            String path_str = key.substring(0, key.indexOf("$"));
            String ele_str = key.substring(key.indexOf("$") + 1);
            String[] ele_arr = ele_str.split(",");
            for (String ele : ele_arr) {
                List<String> user_list = new ArrayList<>();
                List<String> val = _map.get(key);
                val.forEach(_str -> {
                    String[] str_arr = _str.split("=");
                    if (str_arr.length < 2) return;
                    String str0 = str_arr[0].trim();
                    String str1 = str_arr[1].trim();
                    if (str0.equals("user")) {
                        String[] user_arr = str1.split(",");
                        for (String user : user_arr) {
                            user_list.add(user);
                        }
                    }
                });
                ele_map.put(ele, user_list);
            }
            elementList.put(path_str, ele_map);
        }
    }

    /**
     * 获取用户实例，如果用户名不存在就新增一例
     * @param _username 用户名
     * @return 用户SuperUser实例
     */
    private SuperUser _get_user(String _username) {
        if (!this.users.containsKey(_username)) {
            SuperUser su = new SuperUser();
            su.setUsername(_username);
            this.users.put(_username, su);
        }
        return this.users.get(_username);
    }

    /**
     * 给用户设置角色
     *
     * @param _users 用户数组
     * @param _role  角色ID
     */
    private void _set_user_role(String[] _users, Integer _role) {
        if (_users.length > 0 && _role != null) {
            for (String name : _users) {
                SuperUser su = this._get_user(name);
                su.setRoles(new HashSet<Integer>() {{
                    add(_role);
                }});
                this.users.put(name, su);
            }
        }
    }

    /**
     * 给用户设置可认证通过的请求路径
     *
     * @param _users 用户
     * @param _auth  请求路径
     */
    private void _set_user_auth(String[] _users, String _auth) {
        if (_users.length > 0 && !_auth.equals("")) {
            for (String name : _users) {
                SuperUser su = this._get_user(name);
                Set<SimpleGrantedAuthority> auths = su.getAuthorities();
                auths.add(new SimpleGrantedAuthority(_auth));
                su.setAuthorities(auths);
                this.users.put(name, su);
            }
        }
    }

}
