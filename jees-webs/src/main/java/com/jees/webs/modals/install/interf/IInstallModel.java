package com.jees.webs.modals.install.interf;

import com.jees.webs.modals.install.struct.InstallStep;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 * 安装模块功能
 */
public interface IInstallModel {
    /**
     * 安装是否结束
     * @return
     */
    boolean isFinish();
    /**
     * 开始执行
     * @return 第一步安装进度对象
     */
    InstallStep start();

    /**
     * 保存当前安装对象，进入下一个安装流程
     * @return 下一步安装进度对象
     */
    InstallStep submit();

    /**
     * 保存当前安装对象，进入上一个安装流程
     * @return 上一步安装进度对象
     */
    InstallStep back();

    /**
     * 保存当前安装对象，完成安装
     */
    void finish();

    /**
     * 如果启用了安装页面，需要先检测安装结果并配置访问路径
     * @param _registry
     */
    void setViewController( ViewControllerRegistry _registry);
}
