package com.jees.jsts.server.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息协议格式，注意属性的顺序会影响消息内容
 * 
 * @author aiyoyoyo
 *
 */
@Getter
@Setter
public class Message {
	public static final String	DELIM_STR		= ";";
	public static final int		TYPE_SOCKET 	= 0;
	public static final int		TYPE_WEBSOCKET	= 1;
	public static final int		TYPE_BYTES		= 2;

	private int					id;					// 协议号

	private int					userId;

	private List< Integer >		intData;

	private List< String >		strData;

	private List< Boolean >		booData;

	private List< Float >		floData;

	private List< Double >		dblData;

	private List< Long >		lonData;

	private List< byte[] >		bytData;

	private int					type;

	public Message() {
		intData = new ArrayList<>();
		strData = new ArrayList<>();
		booData = new ArrayList<>();
		floData = new ArrayList<>();
		dblData = new ArrayList<>();
		lonData = new ArrayList<>();
		bytData = new ArrayList<>();

		type = TYPE_SOCKET;
	}

	public void add( Integer _obj ) {
		this.intData.add( _obj );
	}

	public void add( String _obj ) {
		this.strData.add( _obj );
	}

	public void add( Float _obj ) {
		this.floData.add( _obj );
	}

	public void add( Boolean _obj ) {
		this.booData.add( _obj );
	}

	public void add( Double _obj ) {
		this.dblData.add( _obj );
	}

	public void add( Long _obj ) {
		this.lonData.add( _obj );
	}

	public void add( byte[] _obj ) {
		this.bytData.add( _obj );
	}

	public void addInteger( Integer _obj ) {
		this.intData.add( _obj );
	}

	public void addString( String _obj ) {
		this.strData.add( _obj );
	}

	public void addFloat( Float _obj ) {
		this.floData.add( _obj );
	}

	public void addBoolean( Boolean _obj ) {
		this.booData.add( _obj );
	}

	public void addDouble( Double _obj ) {
		this.dblData.add( _obj );
	}

	public void addLong( Long _obj ) {
		this.lonData.add( _obj );
	}

	public void addBytes( byte[] _obj ) {
		this.bytData.add( _obj );
	}

	public Integer getInteger( int _idx ) {
		if ( _idx >= this.intData.size() ) return 0;
		return this.intData.get( _idx );
	}

	public String getString( int _idx ) {
		if ( _idx >= this.strData.size() ) return null;
		return this.strData.get( _idx );
	}

	public Float getFloat( int _idx ) {
		if ( _idx >= this.floData.size() ) return 0F;
		return this.floData.get( _idx );
	}

	public Boolean getBoolean( int _idx ) {
		if ( _idx >= this.booData.size() ) return null;
		return this.booData.get( _idx );
	}

	public Double getDouble( int _idx ) {
		if ( _idx >= this.booData.size() ) return 0D;
		return this.dblData.get( _idx );
	}

	public Long getLong( int _idx ) {
		if ( _idx >= this.lonData.size() ) return 0L;
		return this.lonData.get( _idx );
	}

	public byte[] getBytes( int _idx ) {
		if ( _idx >= this.bytData.size() ) return null;
		return this.bytData.get( _idx );
	}
}
