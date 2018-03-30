package com.jees.test.entity;

import com.jees.webs.entity.SuperUser;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table( name = "js_user" , catalog = "jees_common" )
public class User extends SuperUser<Integer, Role> {
}
