package com.jees.test.entity;

import com.jees.webs.entity.SuperUser;
import org.directwebremoting.annotations.DataTransferObject;

import javax.persistence.Entity;
import javax.persistence.Table;

@DataTransferObject
@Entity
@Table( name = "js_user" , catalog = "jees_common" )
public class User extends SuperUser< Integer, Role, Menu > {

    @Override
    public SuperUser build() {
        return new User();
    }
}
