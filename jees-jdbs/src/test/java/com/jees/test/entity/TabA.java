package com.jees.test.entity;

import com.jees.core.database.repository.SuperEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "a", catalog = "testa")
@Getter
@Setter
@EqualsAndHashCode
public class TabA extends SuperEntity<Integer> {
    @Column(name = "str")
    private String str;
}
