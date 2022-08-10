package com.jees.webs.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccessService {
    public boolean validate(HttpServletRequest request, Authentication authentication){
        Object principal = authentication.getPrincipal();
        if( principal == null || "anonymousUser".equals( principal ) ){
            return false;
        }
        return false;
    }
}
