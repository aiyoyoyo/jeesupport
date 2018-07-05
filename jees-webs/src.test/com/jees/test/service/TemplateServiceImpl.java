package com.jees.test.service;

import com.jees.webs.entity.SuperMenu;
import com.jees.webs.support.AbsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Configuration
public class TemplateServiceImpl extends AbsTemplateService{
    @Autowired
    DaoServiceImpl      daoService;

    @Override
    @Transactional
    public List<SuperMenu> loadTemplateMenus( String _tpl ) {
        List<SuperMenu> list = daoService.selectTemplateMenus( _tpl );

        // 加载页面所需要的权限
        list.forEach( m -> m.getRoles().size() );
        return list;
    }
}
