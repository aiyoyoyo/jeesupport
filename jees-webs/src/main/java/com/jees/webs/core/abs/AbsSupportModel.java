package com.jees.webs.core.abs;

import com.jees.common.CommonConfig;
import com.jees.webs.core.interf.ISupportModel;
import lombok.Getter;

public abstract class AbsSupportModel implements ISupportModel {

    @Getter
    protected boolean enable;
    /**
     * 获取模块配置，以 jees.webs.modals 开头拼接
     * @param _key
     */
    public <T> T getModelConfig( String _key, T _def ){
        return CommonConfig.get( "jees.webs.modals." + _key ,_def );
    }
}
