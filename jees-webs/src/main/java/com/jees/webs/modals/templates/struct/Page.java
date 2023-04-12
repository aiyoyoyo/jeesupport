package com.jees.webs.modals.templates.struct;

import com.jees.tool.utils.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.directwebremoting.annotations.DataTransferObject;

@Getter
@Setter
@DataTransferObject
public class Page {
    /**
     * 访问url路径
     */
    private String url;
    /**
     * 访问文件路径
     */
    private String path;
    /**
     * 文件名字
     */
    private String filename;
    /**
     * 文件路径
     */
    private String filepath;
    /**
     * 所属模板
     */
    private String tpl;
    /**
     * 父页面
     */
    private String parent;
    /**
     * 错误页面
     */
    private boolean errorPage;

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }
}
