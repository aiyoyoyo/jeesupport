package com.jees.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jees.core.database.repository.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * TabB entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table( name = "b" , catalog = "testb" )
@Data
@EqualsAndHashCode
public class TabB extends SuperEntity<Integer> {

	@Column( name = "num" , nullable = false )
	private int					num;
}
