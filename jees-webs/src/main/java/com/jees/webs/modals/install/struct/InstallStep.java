package com.jees.webs.modals.install.struct;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class InstallStep {
    /**
     * 索引
     */
    int index;
    /**
     * 是否最后一步
     */
    boolean finish;
    /**
     * 当前记录的信息
     */
    Map stepInfo;
}
