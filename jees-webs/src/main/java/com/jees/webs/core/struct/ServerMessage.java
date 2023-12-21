package com.jees.webs.core.struct;

import com.jees.tool.utils.JsonUtil;
import com.jees.webs.core.interf.ICodeDefine;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ServerMessage {
    int code;
    String desc;
    String message;
    String helper;

    public ServerMessage() {
    }

    public ServerMessage(int _code) {
        this.setCode(_code);
    }

    public void setCode(int code) {
        this.code = code;
        this.desc = ICodeDefine.getCodeDesc(code);
        this.message = ICodeDefine.getCodeDesc(code);
    }

    @Override
    public String toString() {
        return JsonUtil.toString(this);
    }
}
