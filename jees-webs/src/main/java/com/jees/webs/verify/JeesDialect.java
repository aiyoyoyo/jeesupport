package com.jees.webs.verify;

import com.jees.common.CommonConfig;
import com.jees.webs.abs.AbsVerifyService;
import com.jees.webs.support.ISupportEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashSet;
import java.util.Set;

/**
 * Thymeleaf自定义标签，th:auth权限来匹配按钮显示
 */
@Component
public class JeesDialect extends AbstractProcessorDialect implements ISupportEL {

    @Autowired
    AbsVerifyService verifyService;

    public JeesDialect() {
        super(DIALECT_NAME, "th", StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String _prefix) {
        Set<IProcessor> processors = new HashSet<IProcessor>();
        if (CommonConfig.getBoolean("jees.webs.verify.enable", false)) {
            processors.add(new JeesTagProcessor(_prefix, verifyService.getElementList()));
        }
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, _prefix));
        return processors;
    }
}
