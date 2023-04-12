package com.jees.webs.security.service;

import com.jees.webs.core.interf.ICodeDefine;
import com.jees.webs.core.struct.ServerMessage;
import com.jees.webs.security.exception.RequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.service
 * @ClassName: GlobalExceptionService
 * @Author: 刘甜
 * @Date: 2023/4/6 14:57
 * @Version: 1.0
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionService {

    /**
     * 参数解析失败
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ServerMessage handleServerMessageException(Exception ex) {
        log.error("参数解析失败", ex);
        ServerMessage msg = new ServerMessage();
        msg.setCode(ICodeDefine.Server_ErrorState);
        msg.setDesc(ex.getMessage());
        return msg;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RuntimeException.class})
    public ServerMessage handleServerMessageRuntimeException(RuntimeException ex) {
        log.error("参数解析失败", ex);
        ServerMessage msg = new ServerMessage();
        msg.setCode(500);
        msg.setDesc(ex.getMessage());
        return msg;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({RequestException.class})
    public ServerMessage handleServerMessageRequestException(RequestException ex) {
        log.error("参数解析失败", ex);
        return ex.getServerMessage();
    }
}
