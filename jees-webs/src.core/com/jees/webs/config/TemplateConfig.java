package com.jees.webs.config;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * 模版结构配置
 * @author aiyoyoyo
 */
@Data
public class TemplateConfig {
    private String name;
    private String assets;
    private String theme;
    private Set<String> themes;

    public String getTheme(){
        if( theme == null )
            setTheme( themes.iterator().next() );

        return theme;
    }
}
