/*
 * Author: Aiyoyoyo
 * https://github.com/aiyoyoyo/jeesjs/tree/master/src/base/Template.js
 * License: MIT license
 *
 */
// namespace:
this.MGR = this.MGR || {};
(function() {
	"use strict";
	// constructor: ===========================================================
	/**
	 * @class Roles
	 * @static
	 */
	function Menus() { throw "Menus cannot be instantiated."; };

	// private static properties: =============================================
	Menus._datas = new Map();

	// public static properties: ==============================================
    // private static methods: ================================================
    /**
     * 绘制菜单表格
     * @method _handle_load
     * @param {M extend SuperMenu} _datas
     * @static
     * @private
     */
	Menus._handle_load = function( _datas ){
	    this._datas = new Map();
        var dom_tab = $("#tab-menus tbody:last");
        var parent_group = [];
        for( var k in _datas ){
            var o = _datas[k];
            parent_group.push({ value: o.id, text: o.name });
        }
        for( var k in _datas ){
            var o = _datas[k];
            this._datas[o.id] = o;

            var e_name_id = "NAME-" + o.id;
            var e_parent_id = "PARENT-" + o.id;
            var e_parent_txt = "顶级菜单";
            for( var i in parent_group ){
                var g = parent_group[i];
                if( g.value == o.parentId && o.parentId != o.id ){
                    e_parent_txt = g.text;
                    break;
                }
            }

            var row_name = jeesjs.Editable.generateText( o.id, e_name_id, o.name, "标题" );
            var row_parent = jeesjs.Editable.generateSelect( o.id, e_parent_id, e_parent_txt );
            var row = "<tr id='DATA-" + o.id + "'>"
                    + "<td>" + o.id + "</td>"
                    + "<td>" + o.url + "</td>"
                    + "<td>" + row_name + "</td>"
                    + "<td>" + row_parent + "</td>"
                    + "<td>" + o.visible + "</td>"
                    + "<td>" + o.index + "</td>"
                    + "<td>"
                        + "<button class='btn btn-danger' onclick='MGR.Menus.onClick_Remove(" + o.id + ")'>删除</button>"
                    + "</td>"
                    + "</tr>";
            dom_tab.append( row );
            jeesjs.Editable.text( $( "#" + e_name_id ) );
            jeesjs.Editable.select( $( "#" + e_parent_id ), e_parent_txt, parent_group );
        }
    };
	// public static methods: =================================================
    /**
     * 初始化
     * @method init
     * @static
     * @public
     */
	Menus.init = function(){
	    jeesjs.Editable.init( this.onData_Change );
        MgrMenuRemote.load( (_datas)=>{ this._handle_load(_datas); } );
    };
    /**
     * 删除
     * @method onClick_Remove
     * @param {ID} _id 栏目ID
     * @static
     * @public
     */
    Menus.onClick_Remove = function( _id ){
        var o = this._datas[_id];
        jeesjs.SweetAlert.confirm( "删除确认", "确定删除栏目[" + o.name + "]吗？", "warning", ()=>{
            console.warn( "没有开放删除功能，如果需要请修改此处源码。" );
//            MgrMenuRemote.remove( o , ()=>{});
        });
    };
    /**
     * 保存数据
     * @method onData_Change
     * @param {jeesjs.Editable.Element} _e 事件对象
     * @static
     * @public
     */
    Menus.onData_Change = function( _e ){
        var o =  MGR.Menus._datas[_e.pk];
        var change = false;
        if( _e.name.indexOf( "NAME-" ) != -1 ){
            o.name = _e.value;
            change = true;
        }
        if( _e.name.indexOf( "PARENT-" ) != -1 ){
            if( _e.value != "" ){
                o.parentId = parseInt(_e.value);
                change = true;
            }
        }

        if( change ){
            MgrMenuRemote.save( o );
        }
    };

	MGR.Menus = Menus;
})();