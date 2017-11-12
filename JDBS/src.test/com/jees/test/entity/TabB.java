package com.jees.test.entity ;

import javax.persistence.Column ;
import javax.persistence.Entity ;
import javax.persistence.GeneratedValue ;
import javax.persistence.Id ;
import javax.persistence.Table ;
import org.hibernate.annotations.GenericGenerator ;

/**
 * TabB entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table( name = "b", catalog="testb"  )
public class TabB implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6831937672938924389L ;
	private Integer	id ;

	// Constructors

	/** default constructor */
	public TabB() {
	}

	/** full constructor */

	// Property accessors
	@GenericGenerator( name = "generator" , strategy = "identity" )
	@Id
	@GeneratedValue( generator = "generator" )
	@Column( name = "id" , unique = true , nullable = false )
	public Integer getId() {
		return this.id ;
	}

	public void setId( Integer id ) {
		this.id = id ;
	}

}
