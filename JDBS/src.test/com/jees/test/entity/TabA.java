package com.jees.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * A entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table( name = "a" , catalog = "testa" )
public class TabA implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8191860597764392455L;
	private Integer				id;
	private String				str;

	// Constructors

	/** default constructor */
	public TabA() {
	}

	// Property accessors
	@GenericGenerator( name = "generator" , strategy = "identity" )
	@Id
	@GeneratedValue( generator = "generator" )

	@Column( name = "id" , unique = true , nullable = false )

	public Integer getId() {
		return this.id;
	}

	public void setId( Integer id ) {
		this.id = id;
	}

	@Column( name = "str" )

	public String getStr() {
		return str;
	}

	public void setStr( String str ) {
		this.str = str;
	}

}
