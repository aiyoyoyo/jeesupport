package com.jees.test.controller;

import com.jees.common.CommonContextHolder;
import com.jees.webs.controller.PathController;
import com.jees.webs.entity.SuperMenu;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
public class MainController extends PathController {

    public MainController(){
        super();
    }
}
