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
	function Roles() { throw "Roles cannot be instantiated."; };

	// private static properties: =============================================
	Roles._menus = new Map();
	Roles._datas = new Map();
	Roles._group = [];

	// public static properties: ==============================================
    // private static methods: ================================================
    /**
     * 先加载菜单
     * @method _handle_menu
     * @param {M extend SuperMenu} _datas
     * @static
     * @private
     */
    Roles._handle_menu = function( _datas ){
        this._group = [];
        for( var k in _datas ){
            var o = _datas[k];
            this._menus[o.id] = o;
            this._group.push( o.name );
        }
        MgrRoleRemote.load( (_datas)=>{ this._handle_load(_datas); } );
    }
    /**
     * 绘制菜单表格
     * @method _handle_load
     * @param {M extend SuperRole} _datas
     * @static
     * @private
     */
	Roles._handle_load = function( _datas ){
	    this._datas = new Map();
        for( var k in _datas ){
            var o = _datas[k];
            this._datas[o.id] = o;
            this._handle_add( o );
        }
    };
    /**
     * 绘制菜单表格
     * @method _handle_add
     * @param {M extend SuperRole} _data
     * @static
     * @private
     */
    Roles._handle_add = function( _data ){
        var dom_tab = $("#tab-data tbody:last");
        var o = _data;
        var e_name_id = "NAME-" + o.id;
        var e_menu_id = "MENU-" + o.id;
        var e_menu_txt = "";
        for( var i in o.menus ){
            var id = o.menus[i];
            var m = this._menus[id];
            e_menu_txt += m.name + ", ";
        }
        if ( e_menu_txt != "" ) e_menu_txt = e_menu_txt.substring( 0, e_menu_txt.length - 2 );

        var row_name = jeesjs.Editable.generateText( o.id, e_name_id, o.name, "标题" );
        var row_tags = jeesjs.Editable.generateTags( o.id, e_menu_id, e_menu_txt ) ;
        var row_remove = "<button class='btn btn-danger' onclick='MGR.Roles.onClick_Remove( " + o.id + " )'>删除</button>";
        if( o.id == 0 ){
            row_remove = "<button class='btn btn-default'>删除</button>";
            row_name = "<a href='javascript:;'>" + o.name + "<a>";
            row_tags = "<a href='javascript:;'>" + e_menu_txt + "<a>";
        }
        var row = "<tr id='E-" + o.id + "'>"
                + "<td>" + o.id + "</td>"
                + "<td>" + row_name + "</td>"
                + "<td>" + row_tags + "</td>"
                + "<td>" + row_remove + "</td>"
                + "</tr>";
        dom_tab.append( row );
        jeesjs.Editable.text( $( "#" + e_name_id ) );
        jeesjs.Editable.tags( $( "#" + e_menu_id ), this._group );
    };

    /**
     * 绘制菜单表格
     * @method _handle_add
     * @param {M extend SuperRole} _data
     * @static
     * @private
     */
    Roles._handle_remove = function( _id ){
        $("#E-" + _id).remove();
    };
	// public static methods: =================================================
    /**
     * 初始化
     * @method init
     * @static
     * @public
     */
	Roles.init = function(){
	    jeesjs.Editable.init( this.onData_Change );
	    MgrMenuRemote.load( (_datas)=>{ this._handle_menu( _datas ); } );
    };
    /**
     * 删除
     * @method onClick_Remove
     * @static
     * @public
     */
    Roles.onClick_Remove = function( _id ){
        var o = this._datas[_id];
        var title = "确定删除角色 [" + o.id + "][" + o.name + "]吗？\n(注意：没有任何角色时会造成系统无法访问。)";
        var tips = "warning";

        jeesjs.SweetAlert.confirm( "删除确认", title, tips, ()=>{
            MgrRoleRemote.remove( o , ()=>{ this._handle_remove( _id ); });
        });
    };
    /**
     * 保存数据
     * @method onData_Change
     * @param {jeesjs.Editable.Element} _e 事件对象
     * @static
     * @public
     */
    Roles.onData_Change = function( _e ){
        var o =  MGR.Roles._datas[_e.pk];
        var change = false;

        if( _e.name.indexOf( "NAME-" ) != -1 ){
            o.name = _e.value;
            change = true;
        }
        if( _e.name.indexOf( "MENU-" ) != -1 ){
            var ms = _e.value;
            o.menus = [];
            for( var idx in ms ){
                var name = ms[idx];
                for( var id in MGR.Roles._menus ){
                    var m = MGR.Roles._menus[id];
                    if( m.name == name ){
                        o.menus.push(m.id);
                        break;
                    }
                }
            }
            change = true;
        }

        if( change ){
            MgrRoleRemote.save( o );
        }
    };
    /**
     * 新增角色
     * @method onClick_Add
     * @static
     * @public
     */
    Roles.onClick_Add = function(){
        MgrRoleRemote.add( "新角色", (_data)=>{ MGR.Roles._handle_add( _data ); } );
    };

	MGR.Roles = Roles;
})();