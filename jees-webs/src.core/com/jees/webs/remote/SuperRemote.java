package com.jees.webs.remote;

import com.jees.webs.abs.AbsInstallService;
import com.jees.webs.entity.Template;
import com.jees.webs.support.ITemplateService;
import lombok.extern.log4j.Log4j2;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
@RemoteProxy
public class SuperRemote{

    @Autowired
    ITemplateService TS;
    @Autowired
    AbsInstallService IS;

    @RemoteMethod
    public List< Template > loadTemplates(){
        return TS.getTemplateAll();
    }

    @RemoteMethod
    public void reload() throws Exception{
        log.debug( "--reload" );
        IS.refresh();
    }
}
