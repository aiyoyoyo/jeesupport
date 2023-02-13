package com.jees.test.controllers;

import com.atomikos.icatch.config.Configuration;
import com.atomikos.util.IntraVmObjectRegistry;
import com.jees.common.CommonContextHolder;
import com.jees.core.database.config.SessionFactoryRegistry;
import com.jees.core.database.support.ISupportDao;
import com.jees.test.entity.DS_DATA;
import com.jees.test.entity.Test;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NameNotFoundException;
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
//            List<DS_DATA> list = dao.select(DS_DATA.class, 0, 1);
            DS_DATA data = dao.selectById( DS_DATA.class, "00778f4a-9f58-4449-98fb-a9e48c8c88d8" );
//            data = list.get(0);
            return "查询结果：" + data;
        } catch (RuntimeException e) {
            return "运行错误：" + e.getMessage();
        } catch (Exception e) {
            return "错误：" + e.getMessage();
        }
    }

    @RequestMapping( "/test2")
    @ResponseBody
    @Transactional
    public String test2(){
        SessionFactoryRegistry.reRegisterSessionFactory( dao.getDefaultDB() );
        return "测试页面";
    }

    @RequestMapping( "/test2/{_test}")
    @ResponseBody
    public String test2( @PathVariable String _test ){
        return "测试页面2";
    }
}
