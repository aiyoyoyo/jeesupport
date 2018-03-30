package com.jees.test.entity;

import com.jees.webs.entity.SuperMenu;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table( name = "js_menu" , catalog = "jees_common" )
public class Menu extends SuperMenu<Integer, Role> {
}
