package com.jees.webs.remote;

import com.jees.webs.core.interf.ISupportEL;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteProxy;

@Log4j2
@RemoteProxy
public class VerifyRemote implements ISupportEL {

//    @Autowired
//    AbsSuperService ASS;
//    @Autowired
//    AbsVerifyService AVS;
//    @Autowired
//    AccessImpl AI;

//    @RemoteMethod
//    public Map<String, Boolean> elements(String _uri) {
//        Map<String, Boolean> res = new HashMap<>();
//        if (_uri.equals("")) return res;
//        if (CommonConfig.getBoolean("jees.webs.verify.enable", true) &&
//                !CommonConfig.getBoolean("jees.jdbs.enable", false)
//        ) {
//            Map<String, Object> elements = AVS.getElementList();
//            WebContext wc = WebContextFactory.get();
//            HttpSession session = wc.getSession();
//            SuperUser user = (SuperUser) session.getAttribute("USER");
//            HttpServletRequest request = wc.getHttpServletRequest();
//            String uri = request.getRequestURI();
//            String name = user.getUsername();
//            if (!elements.isEmpty())
//                elements.forEach((String _path, Object _element) -> {
//                    if (_uri.contains(_path)) {
//                        Map<String, List<String>> eles = (Map<String, List<String>>) elements.get(_path);
//                        if (!eles.isEmpty()) {
//                            eles.forEach((String _ele, List<String> _users) -> {
//                                if (_users.contains(name)) res.put(_ele, true);
//                                else res.put(_ele, false);
//                            });
//                        }
//                    }
//                });
//            log.debug("--" + uri + "页面拥有元素：V=["+ res +"]");
//        }
//        return res;
//    }
//
//    /**
//     * 适用于离线模式下前端调用权限认证的方法
//     * @param _path 请求路径
//     * @return 认证标识：true通过 false无权限
//     */
//    @RemoteMethod
//    public Map<String, Object> check(String _path) {
//        boolean allow = true;
//        if (CommonConfig.getBoolean("jees.webs.verify.enable", true) &&
//            !CommonConfig.getBoolean("jees.jdbs.enable", false)
//        ) {
//            WebContext wc = WebContextFactory.get();
//            HttpSession session = wc.getSession();
//            HttpServletRequest request = wc.getHttpServletRequest();
//            SuperUser user = (SuperUser) session.getAttribute("USER");
//            Collection<? extends GrantedAuthority> auths = user.getAuthorities();
//            String ip = request.getRemoteAddr();
//            String name = user.getUsername();
//            Set<SuperMenu> menus = user.getMenus();
//            log.debug("--Check Url, V=["+ _path +"]");
//            boolean allow_path = AI.allowMenu(_path, menus);
//            boolean allow_ip = AI.allowIP(ip, null);
//            boolean allow_user = AI.allowUser(name, null);
//            if (!allow_path || !allow_ip || !allow_user) allow = false;
//        }
//        boolean finalAllow = allow;
//        return new HashMap<String, Object>() {
//            {
//                put("allow", finalAllow);
//            }
//        };
//    }

}
