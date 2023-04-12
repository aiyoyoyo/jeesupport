package com.jees.webs.core.interf;

/**
 * 功能模块接口
 */
public interface ISupportModel {
    void initialize();

    <T> T getModelConfig(String _key, T _def);

    boolean isEnable();
}
