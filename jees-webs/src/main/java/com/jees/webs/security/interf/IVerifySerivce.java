package com.jees.webs.security.interf;

import com.jees.webs.core.service.SecurityService;
import com.jees.webs.entity.SuperUser;
import org.thymeleaf.model.IProcessableElementTag;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: TODO
 * @Package: com.jees.webs.security.interf
 * @ClassName: IVerifySerivce
 * @Author: 刘甜
 * @Date: 2023/5/19 14:00
 * @Version: 1.0
 */
public interface IVerifySerivce {
	void initialize(SecurityService.SecurityModel _model);
	
	SuperUser findUserByUsername(String _username);
	
	boolean validateElement(HttpServletRequest _request, IProcessableElementTag _tag, String _validate);
}
