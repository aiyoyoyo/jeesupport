package com.jees.test.controllers;

import com.jees.core.database.support.ISupportDao;
import com.jees.test.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: TODO
 * @Package: com.jees.test.controllers
 * @ClassName: TestController
 * @Author: 刘甜
 * @Date: 2022/11/23 13:18
 * @Version: 1.0
 */
@Controller
public class TestController {
    @Autowired
    ISupportDao dao;

    @RequestMapping( "/test1" )
    @ResponseBody
    @Transactional
    public String test1(HttpServletRequest _request){
        try {
            String id = _request.getParameter( "id" );
            List<Test> list = dao.select(dao.getDefaultDB(), Test.class);
            Thread.sleep( 5000 );
            System.out.println( "--------------------" + id + "->" + list.size() );
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {

        }
        return "";
    }

    @RequestMapping( "/test2")
    @ResponseBody
    public String test2(){
        return "测试页面";
    }

    @RequestMapping( "/test2/{_test}")
    @ResponseBody
    public String test2( @PathVariable String _test ){
        return "测试页面2";
    }
}
