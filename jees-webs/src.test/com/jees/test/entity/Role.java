package com.jees.test.entity;

import com.jees.webs.entity.SuperRole;
import org.directwebremoting.annotations.DataTransferObject;

import javax.persistence.Entity;
import javax.persistence.Table;

@DataTransferObject
@Entity
@Table( name = "js_role" , catalog = "jees_common" )
public class Role extends SuperRole < Integer, Menu, User > {
    @Override
    public SuperRole build() {
        return new Role();
    }
}
