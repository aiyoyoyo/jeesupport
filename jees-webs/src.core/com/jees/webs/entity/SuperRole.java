package com.jees.webs.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@MappedSuperclass
public class SuperRole <ID extends Serializable, U extends SuperUser, M extends SuperMenu>{
    @Id
    @GeneratedValue( strategy = IDENTITY )
    @GenericGenerator( name = "generator" , strategy = "identity" )
    @Column( name = "id" , unique = true , nullable = false )
    int id;
    @Column( name = "name" , unique = true, nullable = false )
    String name;
}
