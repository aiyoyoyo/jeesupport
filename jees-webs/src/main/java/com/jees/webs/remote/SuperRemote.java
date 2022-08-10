package com.jees.webs.remote;

import com.jees.webs.modals.templates.service.TemplateService;
import com.jees.webs.modals.templates.struct.Template;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
@RemoteProxy
public class SuperRemote{

    @Autowired
    TemplateService templateService;

    @RemoteMethod
    public List< Template > loadTemplates(){
        return templateService.getTemplateAll();
    }

    @RemoteMethod
    public void reload() throws Exception{
        log.debug( "--reload" );
    }
}
