package com.jees.webs.modals.install.service;

import com.jees.common.CommonConfig;
import com.jees.webs.core.abs.AbsSupportModel;
import com.jees.webs.modals.install.interf.IInstallModel;
import com.jees.webs.modals.install.struct.InstallStep;
import com.jees.webs.modals.templates.struct.Page;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Component
public class InstallModelService extends AbsSupportModel implements IInstallModel {
    /**
     * 是否安装完成
     */
    @Getter
    boolean finish;
    /**
     * 当前安装进度
     */
    @Getter
    int index;
    /**
     * 安装进度对象
     */
    List<InstallStep> steps;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    @Override
    public void initialize() {
        this.enable = this.getModelConfig("install.enable", false);
        log.info("安装模块：" + (this.enable ? "开启" : "关闭"));
        if (this.enable) {
            this.steps = new ArrayList<>();
            if (!this.finish) {
                throw new RuntimeException("安装模块执行失败!");
            }
        } else {
            this.finish = true;
        }
    }

    /**
     * 打开页面时调用
     *
     * @return
     */
    @Override
    public InstallStep start() {
        return this._load_step();
    }

    @Override
    public InstallStep submit() {
        InstallStep step = this.steps.get(this.index);
        step.setFinish(true);
        return _build_install_step(++this.index);
    }

    @Override
    public InstallStep back() {
        InstallStep step = this.steps.get(this.index);
        step.setFinish(false);
        return _build_install_step(--this.index);
    }

    @Override
    public void finish() {
        InstallStep step = this.steps.get(this.index);
        step.setFinish(true);
        this._save_step();
        this.finish = true;
    }

    private List<Page> _load_install_pages() {
        List<Page> view_pages = new ArrayList<>();
        String root_path = CommonConfig.getString("spring.thymeleaf.prefix");
        String install_path = this.getModelConfig("install.tpl", "install");

        return view_pages;
    }

    /**
     * 配置安装页面的访问路径
     *
     * @param _registry
     */
    @Override
    public void setViewController(ViewControllerRegistry _registry) {
        if (this.enable && this.finish) {
            List<Page> list = _load_install_pages();
            list.forEach(p -> _registry.addViewController(p.getUrl()).setViewName(p.getFilepath()));

            if (list.size() > 0) {
                Page p = list.get(0);
                String install_path = this.getModelConfig("install.tpl", "install");
                _registry.addViewController("").setViewName(p.getFilepath());
                _registry.addViewController("/" + install_path).setViewName(p.getFilepath());
            }
        }
    }

    /**
     * 将安装进度保存至安装文件
     */
    private void _save_step() {
        for (InstallStep step : this.steps) {
        }
    }

    /**
     * 从安装文件中加载安装状态
     *
     * @return
     */
    private InstallStep _load_step() {
        boolean is_loaded = false;
        if (is_loaded) {
            //TODO 从安装结果文件中获取进度
            this.index = 0;
            _build_install_step(this.index);
        } else {
            this.index = 0;
        }
        return _build_install_step(this.index);
    }

    /**
     * 构建安装进度对象
     *
     * @param _idx 默认加载的安装进度对象索引
     * @return 安装进度对象
     */
    private InstallStep _build_install_step(int _idx) {
        InstallStep step;
        if (this.steps.isEmpty()) {
            step = new InstallStep();
            step.setIndex(this.steps.size());
            step.setStepInfo(new HashMap());
        } else {
            // 此处未判断上下限
            step = this.steps.get(_idx);
        }
        return step;
    }
}
