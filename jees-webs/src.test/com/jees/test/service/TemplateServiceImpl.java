package com.jees.test.service;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.support.AbsTemplateService;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Configuration
public class TemplateServiceImpl extends AbsTemplateService{
    @Override
    public List<SuperMenu> loadTemplateMenus(String tpl) {
        return new ArrayList<>();
    }
}
