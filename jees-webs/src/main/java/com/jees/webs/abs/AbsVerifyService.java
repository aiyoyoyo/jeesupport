package com.jees.webs.abs;

import com.jees.tool.utils.FileUtil;
import com.jees.tool.utils.ReflectUtils;
import com.jees.webs.entity.Page;
import com.jees.webs.entity.SuperMenu;
import com.jees.webs.entity.SuperRole;
import com.jees.webs.entity.SuperUser;
import com.jees.webs.support.ISupportEL;
import com.jees.webs.support.IVerifyService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * AbsVerifyService 抽象实现类，实现了操作权限配置文件verify.cfg的一些方法。
 */
@Log4j2
public abstract class AbsVerifyService<M extends SuperMenu, U extends SuperUser, R extends SuperRole> implements IVerifyService, ISupportEL {

    // 配置文件中的菜单，角色，用户信息，可与AbsSuperService中合并
    @Getter
    @Setter
    Map<String, M> menus = new HashMap<>();
    @Getter
    @Setter
    Map<Integer, R> roles = new HashMap<>();
    @Getter
    @Setter
    Map<String, U> users = new HashMap<>();

    // 配置文件中的黑名单列表、角色列表、页面元素列表
    @Getter
    @Setter
    Map<String, String[]> blackList = new HashMap<>();
    @Getter
    @Setter
    Map<String, String[]> roleList = new HashMap<>();
    @Getter
    @Setter
    Map<String, Object> elementList = new HashMap<>();

    // 存放用户名称-菜单ID关联信息
    private Map<String, List<String>> userMenuMap = new HashMap<>();

    // 自增主键，配置文件中的数据默认从101开始递增
    AtomicInteger userIdx = new AtomicInteger(100);
    AtomicInteger roleIdx = new AtomicInteger(100);
    AtomicInteger menuIdx = new AtomicInteger(100);

    @Autowired
    AbsSuperService ASS;
    @Autowired
    AbsTemplateService TS;

    @Override
    public void initialize() {
        this._load_config();
    }

    /**
     * 通过配置文件登录
     */
    @Override
    public U findUserByUsername(String _username) {
        if (this.users.containsKey(_username)) {
            return this.users.get(_username);
        }
        log.debug("--配置文件中查找登录用户信息：U=[" + _username + "]");
        throw new UsernameNotFoundException("配置中用户不存在");
    }

    /**
     * 加载用户的菜单信息
     */
    @Override
    public void loadUserMenus(HttpServletRequest _request) {
        HttpSession session = _request.getSession();
        U user = (U) session.getAttribute(Session_User_EL);
        if (user == null) return;
        Set<M> menu_list = this.users.get(user.getUsername()).getMenus();
        Map<String, M> menu_map = menu_list.stream().collect(Collectors.toMap(
                M::getUrl, m -> m, (k1, k2) -> k2
        ));
        // 菜单排序
        //Set<M> sort_list = new TreeSet<>(Comparator.comparing(M::getIndex));
        //sort_list.addAll(menu_list);
        List<Map.Entry<String, M>> list = new LinkedList<>(menu_map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, M>>() {
            @Override
            public int compare(Map.Entry<String, M> _m1, Map.Entry<String, M> _m2) {
                return _m2.getValue().getId() - _m1.getValue().getId();
            }
        });
        Map<String, M> res_map = new LinkedHashMap<>();
        for (Map.Entry<String, M> entry : list) {
            res_map.put(entry.getKey(), entry.getValue());
        }
        session.setAttribute(Session_Menus_EL, res_map);
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
        if (!special_map.isEmpty()) {
            if (special_map.containsKey(VERIFY_USER_EL))
                this._handle_users(special_map.get(VERIFY_USER_EL));
            if (special_map.containsKey(VERIFY_ROLE_EL))
                this._handle_roles(special_map.get(VERIFY_ROLE_EL));
            if (special_map.containsKey(VERIFY_BLACK_EL))
                this._handle_black(special_map.get(VERIFY_BLACK_EL));
        }
        log.debug("--配置文件中用户：U=[" + this.users.keySet() + "]");
        log.debug("--配置文件中角色：R=[" + this.roles.keySet() + "]");
        log.debug("--配置文件中菜单：M=[" + this.menus.keySet() + "]");
        // 再存入路径等基础配置
        if (!base_map.isEmpty()) this._handle_path(base_map);
        // 再存入路径页面对应的元素配置
        if (!element_map.isEmpty()) this._load_element(element_map);
        // 给用户设置对应的菜单信息
        if (!userMenuMap.isEmpty()) this._set_user_menu();
    }

    /**
     * 从配置文件加载基础配置，形成【用户-路径】数据
     */
    private void _handle_path(Map<String, List<String>> _map) {
        Map<String, Page> tpl_menus = TS.getDefaultTemplate().getPages();
        for (String path : tpl_menus.keySet()) {
            List<M> menu_list = null;
            M menu = null;
            M sub_menu = null;
            try {
                menu = this.build(menuClass());
                sub_menu = this.build(menuClass());
            } catch (IllegalAccessException | InstantiationException e) {
                log.error("--构建后台菜单实例错误：" + e.getMessage());
            }
            // 先存储父级菜单，通过判断路径起始字符完成
            for (String key : _map.keySet()) {
                List<String> val = _map.get(key);
                if (path.startsWith(key) && !key.equals(path) && !key.equals("/")) {
                    menu.setUrl(key);
                    this._handle_path_info(val, menu);
                    if (!this.menus.containsKey(key)) {
                        this.menus.put(menu.getUrl(), menu);
                    }
                }
            }
            // 再存储子菜单，通过判断模版中的Page对象的parent元素来决定父级菜单
            for (String key : _map.keySet()) {
                List<String> val = _map.get(key);
                if (key.equals(path)) {
                    Page page = tpl_menus.get(path);
                    sub_menu.setUrl(page.getUrl());
                    sub_menu.setTpl(page.getTpl());
                    this._handle_path_info(val, sub_menu);
                    String parent_url = page.getParent();
                    if (!parent_url.equals("")) {
                        if (this.menus.containsKey(parent_url)) {
                            M parent_menu = this.menus.get(page.getParent());
                            menu_list = parent_menu.getMenus();
                            if (!menu_list.contains(sub_menu))
                                menu_list.add(sub_menu);
                            parent_menu.setMenus(menu_list);
                            this.menus.put(parent_menu.getUrl(), parent_menu);
                        }
                    } else {
                        this.menus.put(sub_menu.getUrl(), sub_menu);
                    }
                }
            }
        }
//            this._handle_path_fields(val, current_menu);
//            this._handle_path_custom(val, current_menu);
//
//            // 暂时不使用自增ID
//            // this.menus.put(menuIdx.get(), m);
//            if (current_menu.getId() != current_menu.getParentId()) {
//                if (this.menus.containsKey(current_menu.getParentId())) {
//                    parent_menu = this.menus.get(current_menu.getParentId());
//                    parent_menu.getMenus().add(current_menu);
//                } else {
//                    parent_menu.setId(current_menu.getParentId());
//                    parent_menu.getMenus().add(current_menu);
//                }
//                this.menus.put(current_menu.getParentId(), parent_menu);
//            } else {
//                if (this.menus.containsKey(current_menu.getId())) {
//                    List<M> menus = this.menus.get(current_menu.getId()).getMenus();
//                    current_menu.setMenus(menus);
//                }
//                this.menus.put(current_menu.getId(), current_menu);
//            }
//            // menuIdx.getAndIncrement();
//
//            this._handle_path_user(val, current_menu);
//            this._handle_path_role(val, current_menu);
    }

    /**
     * 处理配置文件中的菜单信息
     * name：菜单名称Key
     * user：可授权的用户
     * role：可授权的角色
     *
     * @param _rows 菜单信息数组
     * @param _menu 需要封装的菜单对象
     */
    private void _handle_path_info(List<String> _rows, M _menu) {
        _rows.forEach(_row -> {
            String[] row_arr = _row.split("=", 2);
            if (row_arr.length < 2) return;
            String row_key = row_arr[0].trim();
            String row_val = row_arr[1].trim();
            _menu.setId(menuIdx.get());
            if (row_key.equals("name")) {
                _menu.setName(row_val);
                _menu.setVisible(1);
            } else if (row_key.equals("user")) {
                String[] user_arr = null;
                if (row_val.equals("*")) {
                    user_arr = this.users.keySet().toArray(new String[this.users.keySet().size()]);
                } else {
                    user_arr = row_val.split(",");
                }
                this._set_user_menu_map(user_arr, _menu.getUrl());
            } else if (row_key.equals("role")) {
                for (String role : row_val.split(",")) {
                    if (this.roleList.containsKey(role)) {
                        String[] user_arr = this.roleList.get(role);
                        this._set_user_menu_map(user_arr, _menu.getUrl());
                    }
                }
            } else {
                // 自定义属性
                try {
                    String field_name = row_key;
                    String field_value = row_val;
                    ReflectUtils.invokeSet(_menu, field_name, field_value);
                } catch (Exception e) {
                    log.warn("菜单设值错误：" + e.getMessage() + " [ " + row_key + " ]");
                }
            }
            menuIdx.incrementAndGet();
        });
    }


    /**
     * 给用户-菜单ID关联信息设置值
     */
    private void _set_user_menu_map(String[] _users, String _menu_url) {
        for (String name : _users) {
            if (this.userMenuMap.containsKey(name)) {
                if (!this.userMenuMap.get(name).contains(_menu_url))
                    this.userMenuMap.get(name).add(_menu_url);
            } else {
                this.userMenuMap.put(name, new ArrayList<String>() {{
                    add(_menu_url);
                }});
            }
        }
    }

    /**
     * 处理配置文件中的用户行数据
     */
    private void _handle_users(List<String> _users) {
        _users.forEach(_row -> {
            String[] row_arr = _row.split("=", 2);
            if (row_arr.length < 2) return;
            String row_key = row_arr[0].trim();
            String row_val = row_arr[1].trim();
            U u = (U) new SuperUser();
            String[] user_props = row_val.split(":");
            u.setId(userIdx.get());
            u.setUsername(row_key);
            u.setPassword(user_props[0]);
            u.setEnabled(user_props[1].equals("1"));
            u.setLocked(user_props[2].equals("1"));
            this.users.put(row_key, u);
            userIdx.getAndIncrement();
        });
    }

    /**
     * 处理配置文件中的角色行数据
     */
    private void _handle_roles(List<String> _roles) {
        _roles.forEach(_row -> {
            String[] row_arr = _row.split("=", 2);
            if (row_arr.length < 2) return;
            String row_key = row_arr[0].trim();
            String row_val = row_arr[1].trim();
            R r = (R) new SuperRole();
            r.setId(roleIdx.get());
            r.setName(row_key);
            this.roles.put(roleIdx.get(), r);
            // 将角色添加至对应的用户属性中
            this._set_user_role(row_val.split(","), roleIdx.get());
            // 将角色名称与角色下的用户名存入roleList
            this.roleList.put(row_key, row_val.split(","));
            roleIdx.getAndIncrement();
        });
    }

    /**
     * 处理配置文件中的黑名单行数据
     */
    private void _handle_black(List<String> _black) {
        _black.forEach(_row -> {
            String[] row_arr = _row.split("=", 2);
            if (row_arr.length < 2) return;
            String row_key = row_arr[0].trim();
            String row_val = row_arr[1].trim();
            String[] black = row_val.split(",");
            this.blackList.put(row_key, black);
        });
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
     *
     * @param _username 用户名
     * @return 用户SuperUser实例
     */
    private U _get_user(String _username) {
        if (!this.users.containsKey(_username)) {
            U su = (U) new SuperUser();
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
                U su = this._get_user(name);
                su.setRoles(new HashSet<Integer>() {{
                    add(_role);
                }});
                this.users.put(name, su);
            }
        }
    }

    /**
     * 通过用户列表(users)、菜单列表(menus)、用户-菜单关联列表(userMenu)来设置用户的菜单信息
     */
    private void _set_user_menu() {
        if (!this.users.isEmpty()) {
            for (String name : this.userMenuMap.keySet()) {
                List<String> menu_urls = this.userMenuMap.get(name);
                if (!menu_urls.isEmpty()) {
                    menu_urls.forEach(_url -> {
                        if (this.users.containsKey(name) && this.menus.containsKey(_url)) {
                            M m = this.menus.get(_url);
                            U u = this.users.get(name);
                            Set<M> user_menus = u.getMenus();
                            user_menus.add(m);
                        }
                    });
                }
            }
        }
    }

    /**
     * 暂时不使用
     * <p>
     * 原因：文件验证方式暂时采用无权限则不返回菜单信息而不是去验证菜单路径
     * 作用：给用户设置可认证通过的请求路径
     *
     * @param _users 用户
     * @param _auth  请求路径
     */
    private void _set_user_auth(String[] _users, String _auth) {
        if (_users.length > 0 && !_auth.equals("")) {
            for (String name : _users) {
                U su = this._get_user(name);
                Set<SimpleGrantedAuthority> auths = su.getAuthorities();
                auths.add(new SimpleGrantedAuthority(_auth));
                su.setAuthorities(auths);
                this.users.put(name, su);
            }
        }
    }

    public <T> T build(Class<T> _cls) throws IllegalAccessException, InstantiationException {
        T t = _cls.newInstance();
        return t;
    }

    protected abstract Class<U> userClass();

    protected abstract Class<R> roleClass();

    protected abstract Class<M> menuClass();

}
