package com.jees.webs.verify;


import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.Set;

/**
 * Thymeleaf自定义标签，th:auth权限来匹配按钮显示
 */
//@Component
public class JeesDialect extends AbstractProcessorDialect {

//    @Autowired
//    AbsVerifyService verifyService;

    static final String DIALECT_NAME                 = "TH_AUTH";
    String VERIFY_USER_EL               = "users";
    String VERIFY_ROLE_EL               = "roles";
    String VERIFY_BLACK_EL              = "black";

    public JeesDialect() {
        super(DIALECT_NAME, "th", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String _prefix) {
//        Set<IProcessor> processors = new HashSet<IProcessor>();
//        if (CommonConfig.getBoolean("jees.webs.verify.enable", false)) {
//            processors.add(new JeesTagProcessor(_prefix, verifyService.getElementList()));
//        }
//        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, _prefix));
//        return processors;
        return null;
    }
}
