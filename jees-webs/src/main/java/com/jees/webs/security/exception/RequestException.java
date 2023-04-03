package com.jees.webs.security.exception;

import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.struct.ServerMessage;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.exception
 * @ClassName: RequestException
 * @Author: 刘甜
 * @Date: 2023/3/30 9:48
 * @Version: 1.0
 */
public class RequestException extends AuthenticationException {

    @Getter
    ServerMessage serverMessage;
    public RequestException(String _msg, Throwable _thr) {
        super(_msg, _thr);
        serverMessage = new ServerMessage();
        serverMessage.setCode(ICodeDefine.ErrorCode);
        serverMessage.setDesc(_msg);
    }

    public RequestException( String _msg ){
        super(_msg);
        serverMessage = new ServerMessage();
        serverMessage.setCode(ICodeDefine.ErrorCode);
        serverMessage.setDesc(_msg);
    }
    public RequestException(ServerMessage _msg){
        super(_msg.getDesc());
        serverMessage = _msg;
    }

    public RequestException(int _code){
        super(new ServerMessage(_code).getDesc());
        serverMessage = new ServerMessage();
        serverMessage.setCode(_code);
    }
}
