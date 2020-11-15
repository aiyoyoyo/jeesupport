package com.jees.test.controller;

import com.jees.common.CommonConfig;
import com.jees.tool.utils.FileUtil;
import com.jees.tool.utils.UrlUtil;
import com.jees.webs.abs.AbsController;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;

@Controller
@Log4j2
@PropertySource("classpath:config/other.properties")
public class ExControllerConfig extends AbsController {

    @SneakyThrows
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
//        log.info(UrlUtil.uri2root("classpath:config") );
//        log.info(UrlUtil.path2url("classpath:static", false ) );
        log.info(UrlUtil.url2path("test", false ) );
        log.info( "user.dir->" + CommonConfig.get( "user.dir") );

        return CommonConfig.get( "other.test" );
    }
}
