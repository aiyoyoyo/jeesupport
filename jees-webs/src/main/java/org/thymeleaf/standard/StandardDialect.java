//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.thymeleaf.standard;

import com.jees.webs.security.processor.StandardVerifyProcessor;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.expression.*;
import org.thymeleaf.standard.processor.*;
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardCSSSerializer;
import org.thymeleaf.standard.serializer.StandardJavaScriptSerializer;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class StandardDialect extends AbstractProcessorDialect implements IExecutionAttributeDialect, IExpressionObjectDialect {
    public static final String NAME = "Standard";
    public static final String PREFIX = "th";
    public static final int PROCESSOR_PRECEDENCE = 1000;
    protected IStandardVariableExpressionEvaluator variableExpressionEvaluator = null;
    protected IStandardExpressionParser expressionParser = null;
    protected IStandardConversionService conversionService = null;
    protected IStandardJavaScriptSerializer javaScriptSerializer = null;
    protected IStandardCSSSerializer cssSerializer = null;
    protected IExpressionObjectFactory expressionObjectFactory = null;

    public StandardDialect() {
        super("Standard", "th", 1000);
    }

    protected StandardDialect(String name, String prefix, int processorPrecedence) {
        super(name, prefix, processorPrecedence);
    }

    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        if (this.variableExpressionEvaluator == null) {
            this.variableExpressionEvaluator = new OGNLVariableExpressionEvaluator(true);
        }

        return this.variableExpressionEvaluator;
    }

    public void setVariableExpressionEvaluator(IStandardVariableExpressionEvaluator variableExpressionEvaluator) {
        Validate.notNull(variableExpressionEvaluator, "Standard Variable Expression Evaluator cannot be null");
        this.variableExpressionEvaluator = variableExpressionEvaluator;
    }

    public IStandardExpressionParser getExpressionParser() {
        if (this.expressionParser == null) {
            this.expressionParser = new StandardExpressionParser();
        }

        return this.expressionParser;
    }

    public void setExpressionParser(IStandardExpressionParser expressionParser) {
        Validate.notNull(expressionParser, "Standard Expression Parser cannot be null");
        this.expressionParser = expressionParser;
    }

    public IStandardConversionService getConversionService() {
        if (this.conversionService == null) {
            this.conversionService = new StandardConversionService();
        }

        return this.conversionService;
    }

    public void setConversionService(IStandardConversionService conversionService) {
        Validate.notNull(conversionService, "Standard Conversion Service cannot be null");
        this.conversionService = conversionService;
    }

    public IStandardJavaScriptSerializer getJavaScriptSerializer() {
        if (this.javaScriptSerializer == null) {
            this.javaScriptSerializer = new StandardJavaScriptSerializer(true);
        }

        return this.javaScriptSerializer;
    }

    public void setJavaScriptSerializer(IStandardJavaScriptSerializer javaScriptSerializer) {
        Validate.notNull(javaScriptSerializer, "Standard JavaScript Serializer cannot be null");
        this.javaScriptSerializer = javaScriptSerializer;
    }

    public IStandardCSSSerializer getCSSSerializer() {
        if (this.cssSerializer == null) {
            this.cssSerializer = new StandardCSSSerializer();
        }

        return this.cssSerializer;
    }

    public void setCSSSerializer(IStandardCSSSerializer cssSerializer) {
        Validate.notNull(cssSerializer, "Standard CSS Serializer cannot be null");
        this.cssSerializer = cssSerializer;
    }

    public Map<String, Object> getExecutionAttributes() {
        Map<String, Object> executionAttributes = new HashMap(5, 1.0F);
        executionAttributes.put("StandardVariableExpressionEvaluator", this.getVariableExpressionEvaluator());
        executionAttributes.put("StandardExpressionParser", this.getExpressionParser());
        executionAttributes.put("StandardConversionService", this.getConversionService());
        executionAttributes.put("StandardJavaScriptSerializer", this.getJavaScriptSerializer());
        executionAttributes.put("StandardCSSSerializer", this.getCSSSerializer());
        return executionAttributes;
    }

    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new StandardExpressionObjectFactory();
        }

        return this.expressionObjectFactory;
    }

    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return createStandardProcessorsSet(dialectPrefix);
    }

    public static Set<IProcessor> createStandardProcessorsSet(String dialectPrefix) {
        Set<IProcessor> processors = new LinkedHashSet();
        processors.add(new StandardActionTagProcessor(dialectPrefix));
        processors.add(new StandardAltTitleTagProcessor(dialectPrefix));
        processors.add(new StandardAssertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardClassappendTagProcessor(dialectPrefix));
        String[] var2 = StandardConditionalFixedValueTagProcessor.ATTR_NAMES;
        int var3 = var2.length;

        int var4;
        String attrName;
        for (var4 = 0; var4 < var3; ++var4) {
            attrName = var2[var4];
            processors.add(new StandardConditionalFixedValueTagProcessor(dialectPrefix, attrName));
        }

        var2 = StandardDOMEventAttributeTagProcessor.ATTR_NAMES;
        var3 = var2.length;

        for (var4 = 0; var4 < var3; ++var4) {
            attrName = var2[var4];
            processors.add(new StandardDOMEventAttributeTagProcessor(dialectPrefix, attrName));
        }

        processors.add(new StandardEachTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardHrefTagProcessor(dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardVerifyProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardInlineHTMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardLangXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardMethodTagProcessor(dialectPrefix));
        var2 = StandardNonRemovableAttributeTagProcessor.ATTR_NAMES;
        var3 = var2.length;

        for (var4 = 0; var4 < var3; ++var4) {
            attrName = var2[var4];
            processors.add(new StandardNonRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }

        processors.add(new StandardObjectTagProcessor(TemplateMode.HTML, dialectPrefix));
        var2 = StandardRemovableAttributeTagProcessor.ATTR_NAMES;
        var3 = var2.length;

        for (var4 = 0; var4 < var3; ++var4) {
            attrName = var2[var4];
            processors.add(new StandardRemovableAttributeTagProcessor(dialectPrefix, attrName));
        }

        processors.add(new StandardRemoveTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new StandardStyleappendTagProcessor(dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardValueTagProcessor(dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardXmlBaseTagProcessor(dialectPrefix));
        processors.add(new StandardXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardXmlSpaceTagProcessor(dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.HTML, dialectPrefix, "block"));
        processors.add(new StandardInliningTextProcessor(TemplateMode.HTML));
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.HTML));
        processors.add(new StandardTranslationDocTypeProcessor());
        processors.add(new StandardInliningCommentProcessor(TemplateMode.HTML));
        processors.add(new StandardConditionalCommentProcessor());
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.HTML));
        processors.add(new StandardAssertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardInlineXMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.XML, dialectPrefix, "block"));
        processors.add(new StandardInliningTextProcessor(TemplateMode.XML));
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.XML));
        processors.add(new StandardInliningCommentProcessor(TemplateMode.XML));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.XML));
        processors.add(new StandardAssertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, dialectPrefix, "block"));
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, (String) null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.TEXT));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.TEXT));
        processors.add(new StandardAssertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix, "block"));
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, (String) null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.JAVASCRIPT));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.JAVASCRIPT));
        processors.add(new StandardAssertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, dialectPrefix, "block"));
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, (String) null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.CSS));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.CSS));
        return processors;
    }
}
