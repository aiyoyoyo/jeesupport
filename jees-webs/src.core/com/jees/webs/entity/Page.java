package com.jees.webs.entity;

import com.jees.tool.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Page {
    private String url;
    private String file;
    private String tpl;

    @Override
    public String toString(){
        return JsonUtil.toString( this );
    }
}
