package com.jees.test.entity;

import com.jees.core.database.repository.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * TabB entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "b", catalog = "testa")
@Data
@EqualsAndHashCode
public class TabB extends SuperEntity<Integer> {

    @Column(name = "num", nullable = false)
    private int num;
}
