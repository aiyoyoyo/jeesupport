package com.jees.core.database.repository;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class SuperEntity<ID extends Serializable> {
    @GenericGenerator(name = "generator", strategy = "identity")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private ID id;
}
