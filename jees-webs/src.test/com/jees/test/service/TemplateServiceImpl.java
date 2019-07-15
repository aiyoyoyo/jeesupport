package com.jees.test.service;

import com.jees.test.entity.Menu;
import com.jees.webs.abs.AbsTemplateService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class TemplateServiceImpl extends AbsTemplateService< Menu >{
    @Override
    public Class<Menu> getMenuClass(){
        return Menu.class;
    }
}
