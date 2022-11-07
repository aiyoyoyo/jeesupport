package com.jees.webs.security.processor;

import com.jees.common.CommonContextHolder;
import com.jees.webs.security.service.VerifyModelService;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;

/**
 * 增加了Thymeleaf标签支持： th:verify
 * 可以对页面某个元素独立授权，支持jquery的ID、CLASS标记
 */
public final class StandardVerifyProcessor extends AbstractAttributeTagProcessor{
    public static final int PRECEDENCE = 300;
    public static final String ATTR_NAME = "verify";

    public StandardVerifyProcessor(final TemplateMode templateMode, final String dialectPrefix) {
        super(templateMode, dialectPrefix, (String)null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    protected boolean isVisible(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression expression = expressionParser.parseExpression(context, attributeValue);
        Object value = expression.execute(context);

        boolean check_result = false;
        if( value.toString().equalsIgnoreCase( "true" ) ){
            // 判断拿到的是什么标记，仅支持 id, class

            check_result = true;
        }
        return EvaluationUtils.evaluateAsBoolean(check_result);
    }

    @Override
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        VerifyModelService vms = CommonContextHolder.getBean( VerifyModelService.class );
        boolean visible = vms.validateElement( ((WebEngineContext)context).getRequest(), tag, attributeValue );
        if (!visible) {
            structureHandler.removeElement();
        }

    }
}
