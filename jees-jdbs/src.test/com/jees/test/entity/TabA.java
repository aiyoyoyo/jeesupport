package com.jees.test.entity;

import javax.persistence.*;

import com.jees.core.database.repository.SuperEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table( name = "a" , catalog = "testa" )
@Data
@EqualsAndHashCode
public class TabA extends SuperEntity<Integer> {
	@Column( name = "str" )
	private String				str;
}
