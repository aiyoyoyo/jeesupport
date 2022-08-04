package com.jees.webs.verify;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 自定义标签TAG处理程序
 */
public class JeesTagProcessor extends AbstractAttributeTagProcessor {

    // thymeleaf自定义标签名 th:auth 中的 auth
    private static final String TAG_NAME = "auth";
    // 优先级
    private static final int PRECEDENCE = 10000;
    // 存放所有页面按钮ID或CLASS元素
    private Map<String, Object> elements;

    public JeesTagProcessor(String _prefix, Map<String, Object> _map) {
        super(TemplateMode.HTML, _prefix, null, false, TAG_NAME, true, PRECEDENCE, true);
        this.elements = _map;
    }

    @Override
    protected void doProcess(ITemplateContext _context, IProcessableElementTag _tag, AttributeName _name, String s, IElementTagStructureHandler _handler) {
        String id = _tag.getAttributeValue("id");
        String clazz = _tag.getAttributeValue("class");
        List<String> attr = new ArrayList<>();
        if (!id.equals("")) {
            id = "#" + id;
            attr.add(id);
        }
        if (!clazz.equals("")) {
            String[] clazz_arr = clazz.split(" ");
            for (String cla : clazz_arr) {
                attr.add("." + cla);
            }
        }
        if (attr.size() > 0) {
//            WebEngineContext context = (WebEngineContext) _context;
//            HttpServletRequest request = context.getRequest();
//            HttpSession session = context.getSession();
//            String uri = request.getRequestURI();
//            SuperUser su = (SuperUser) session.getAttribute("USER");
//            if (su == null) return;
//            Set<SimpleGrantedAuthority> auths = su.getAuthorities();
//            for (SimpleGrantedAuthority auth : auths) {
//                if (auth.equals(new SimpleGrantedAuthority(uri)) && elements.containsKey(uri)) {
//                    Map<String, Object> ele_map = (Map<String, Object>) elements.get(uri);
//                    attr.forEach(a -> {
//                        if (ele_map.containsKey(a)) {
//                            List<String> users = (List<String>) ele_map.get(a);
//                            if (!users.isEmpty()) {
//                                users.forEach(user -> {
//                                    if (!user.equals(su.getUsername())) {
//                                        _handler.removeElement();
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//            IModelFactory factory = _context.getModelFactory();
//            IModel model = factory.createModel();
//            model.add(factory.createOpenElementTag("span style='display:none;'"));
//            model.add(factory.createCloseElementTag("span"));
        }
    }
}
