package com.jees.webs.core.abs;

import com.jees.common.CommonContextHolder;
import com.jees.webs.core.interf.ISupport;
import com.jees.webs.core.interf.ISupportModel;

import java.util.Collection;

public abstract class AbsSupport implements ISupport {
    @Override
    public void initialize(){
        Collection<ISupportModel> models = CommonContextHolder.getBeans(ISupportModel.class);
        for( ISupportModel model : models ){
            // 如果初始化失败，则停止服务器
            model.initialize();
        }
    }
}
