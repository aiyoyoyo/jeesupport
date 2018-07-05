package com.jees.test.entity;

import com.jees.webs.entity.SuperRole;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

@DataTransferObject
@Entity
@Table( name = "js_role" , catalog = "jees_common" )
public class Role extends SuperRole < Integer, Menu, User > {
    @Override
    public SuperRole build() {
        return new Role();
    }

//    @RemoteProperty
//    public Map<Serializable, Menu> getMenus(){
//        return this.menus;
//    }
}
