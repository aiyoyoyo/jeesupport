package com.jees.test.controller;

import com.jees.webs.config.AbsController;
import com.jees.webs.support.ITemplateService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ExControllerConfig extends AbsController {

    @Autowired
    ITemplateService    templateService;

    @RequestMapping( "/test" )
    public String test( HttpServletRequest _request ){
        return templateService.getTemplatePath( "menu2/", _request );
    }

    @RequestMapping( "/echo1" )
    @ResponseBody
    public String echo( HttpServletRequest _request ){
        System.out.println( "----echo1:" + _request.getParameter("aaa") );
        return _request.getParameter("aaa");
    }

    @RequestMapping( "/echo2" )
    @ResponseBody
    public String echo( String _str, HttpServletRequest _request ){
        System.out.println( "----echo2:" + _str );
        return _str;
    }

    @Data
    private class Temp{
        private String username;
    }

    @RequestMapping( "/echo3" )
    @ResponseBody
    public Temp echo( Temp _user, HttpServletRequest _request ){
        System.out.println( "----echo3:" + _user );
        return _user;
    }
}
