package com.jees.tool.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PaginationUtils {
	public static int		TYPE_A = 0;
	private int max;
	private int index;
	private int pages;
	private int min;
	private List<Integer> indexes;
	/**
	 * 以5页为一个单位显示，0表示被隐藏的部分
	 * e.g: 1,2,3,4,5,0,10 | 1,0,3,4,5,6,7,0,10 | 1,0,6,7,8,9,10
	 * 
	 * @param _index
	 * @param _count
	 * @param _max
	 */
	public PaginationUtils( int _index, int _count, int _max ){
		typeA( _index, _count, _max );
	}
	
	public void typeA( int _index, int _count, int _max ){
		min = 8;
		max = _max;
		if( max == 0 ) max = 1;
		index = _index;
		pages = ( _count + _max - 1 ) / _max;
		if( index > pages ) index = pages;
		indexes = new ArrayList<>();

		if( pages <= min ){
			for( int i = 1; i <= pages; i ++ ){
				indexes.add( i );
			}
		}else{
			if( index < 5 ){ //1,2,3,4,5..pages
				for( int i = 1; i <= 5; i ++){
					indexes.add( i );
				}
				indexes.add( 0 );
				indexes.add( pages );
			}else if( index > pages - 4 && index != 5 ){ // 1,0,pages-5..pages
				indexes.add( 1 );
				indexes.add( 0 );
				for( int i = pages - 4; i <= pages; i ++){
					indexes.add( i );
				}
			}else{ // 1,0,index-2..index+2,0, pages
				int[] tmp = new int[5];
				tmp[0] = index - 2;
				tmp[1] = index - 1;
				tmp[2] = index;
				tmp[3] = index + 1;
				tmp[4] = index + 2;
				indexes.add( 1 );
				indexes.add( 0 );
				for( int i = 0; i < tmp.length; i ++){
					indexes.add( tmp[i] );
				}
				indexes.add( 0 );
				indexes.add( pages );
			}
		}
	}
}
