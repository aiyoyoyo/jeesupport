package com.jees.test.entity;

import com.jees.webs.entity.SuperRole;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "js_role" , catalog = "jees_common" )
public class Role extends SuperRole<Integer, User, Menu> {
}
