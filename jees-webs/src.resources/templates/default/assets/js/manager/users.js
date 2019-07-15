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
	 * @class Users
	 * @static
	 */
	function Users() { throw "Users cannot be instantiated."; };

	// private static properties: =============================================
	Users._roles = new Map();
	Users._datas = new Map();
	Users._group = [];
	Users._check = [];

	// public static properties: ==============================================
    // private static methods: ================================================
    /**
     * 先加载菜单
     * @method _handle_role
     * @param {R extend SuperRole} _datas
     * @static
     * @private
     */
    Users._handle_role = function( _datas ){
        this._group = [];
        for( var k in _datas ){
            var o = _datas[k];
            this._roles[o.id] = o;
            this._group.push( o.name );
        }
        this._check = [];
        this._check.push({value: 0, text:"否"});
        this._check.push({value: 1, text:"是"});
        MgrUserRemote.load( (_datas)=>{ this._handle_load(_datas); } );
    }
    /**
     * 绘制菜单表格
     * @method _handle_load
     * @param {M extend SuperRole} _datas
     * @static
     * @private
     */
	Users._handle_load = function( _datas ){
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
    Users._handle_add = function( _data ){
        var dom_tab = $("#tab-data tbody:last");
        var o = _data;
        var e_name_id = "NAME-" + o.id;
        var e_pwd_id = "PWD-" + o.id;
        var e_role_id = "ROLE-" + o.id;
        var e_enabled_id = "ENABLED-" + o.id;
        var e_locked_id = "LOCKED-" + o.id;

        var e_role_txt = "";
        for( var i in o.roles ){
            var id = o.roles[i];
            var r = this._roles[id];
            e_role_txt += r.name + ", ";
        }
        if ( e_role_txt != "" ) e_role_txt = e_role_txt.substring( 0, e_role_txt.length - 2 );

        var row_name = jeesjs.Editable.generateText( o.id, e_name_id, o.username );
        var row_pwd = jeesjs.Editable.generatePassword( o.id, e_pwd_id, "****" );
        var row_enable = jeesjs.Editable.generateSelect( o.id, e_enabled_id, o.enabled ? "是" : "否"  );
        var row_lock = jeesjs.Editable.generateSelect( o.id, e_locked_id, o.locked ? "是" : "否" );
        var row_tags = jeesjs.Editable.generateTags( o.id, e_role_id, e_role_txt ) ;
        var row_remove = "<button class='btn btn-danger' onclick='MGR.Users.onClick_Remove( " + o.id + " )'>删除</button>";

        if( o.id == 0 ){
            row_name = "<a href='javascript:;'>" + o.username + "<a>";
            row_pwd = "<a href='javascript:;'>****<a>";
            row_enable  = "<a href='javascript:;'>是<a>";
            row_lock  = "<a href='javascript:;'>否<a>";
            row_tags = "<a href='javascript:;'>" + e_role_txt + "<a>";
            row_remove = "<button class='btn btn-default'>删除</button>";
        }

        var row = "<tr id='E-" + o.id + "'>"
                + "<td>" + o.id + "</td>"
                + "<td>" + row_name + "</td>"
                + "<td>" + row_pwd + "</td>"
                + "<td>" + row_enable + "</td>"
                + "<td>" + row_lock + "</td>"
                + "<td>" + row_tags  + "</td>"
                + "<td>" + row_remove + "</td>"
                + "</tr>";

        dom_tab.append( row );
        jeesjs.Editable.text( $( "#" + e_name_id ) );
        jeesjs.Editable.password( $( "#" + e_pwd_id ) );
        jeesjs.Editable.tags( $( "#" + e_role_id ), this._group );
        jeesjs.Editable.select( $( "#" + e_enabled_id ), o.enabled ? 1 : 0, this._check );
        jeesjs.Editable.select( $( "#" + e_locked_id ), o.locked ? 0 : 1, this._check );
    };

    /**
     * 绘制菜单表格
     * @method _handle_add
     * @param {M extend SuperRole} _data
     * @static
     * @private
     */
    Users._handle_remove = function( _id ){
        $("#E-" + _id).remove();
    };
	// public static methods: =================================================
    /**
     * 初始化
     * @method init
     * @static
     * @public
     */
	Users.init = function(){
	    jeesjs.Editable.init( this.onData_Change );
	    MgrRoleRemote.load( (_datas)=>{ this._handle_role( _datas ); } );
    };
    /**
     * 删除
     * @method onClick_Remove
     * @static
     * @public
     */
    Users.onClick_Remove = function( _id ){
        var o = this._datas[_id];
        var title = "确定删除账号 [" + o.username + "]吗？)";
        var tips = "warning";

        jeesjs.SweetAlert.confirm( "删除确认", title, tips, ()=>{
            MgrUserRemote.remove( o , ()=>{ this._handle_remove( _id ); });
        });
    };
    /**
     * 保存数据
     * @method onData_Change
     * @param {jeesjs.Editable.Element} _e 事件对象
     * @static
     * @public
     */
    Users.onData_Change = function( _e ){
        var o =  MGR.Users._datas[_e.pk];
        var change = false;
        if( _e.name.indexOf( "NAME-" ) != -1 ){
            o.username = _e.value;
            change = true;
        }
        if( _e.name.indexOf( "PWD-" ) != -1 ){
            o.password = _e.value;
            change = true;
        }
        if( _e.name.indexOf( "ENABLED-" ) != -1 ){
            o.enabled = _e.value == 1 ? true : false;
            change = true;
        }
        if( _e.name.indexOf( "LOCKED-" ) != -1 ){
            o.locked = _e.value == 1 ? true : false;
            change = true;
        }

        if( _e.name.indexOf( "ROLE-" ) != -1 ){
            var rs = _e.value;
            o.roles = [];
            for( var idx in rs ){
                var name = rs[idx];
                for( var id in MGR.Users._roles ){
                    var r = MGR.Users._roles[id];
                    if( r.name == name ){
                        o.roles.push(r.id);
                        break;
                    }
                }
            }
            change = true;
        }

        if( change ){
            MgrUserRemote.save( o );
        }
    };
    /**
     * 新增角色
     * @method onClick_Add
     * @static
     * @public
     */
    Users.onClick_Add = function(){
        MgrUserRemote.add( (_data)=>{ MGR.Users._handle_add( _data ); } );
    };

	MGR.Users = Users;
})();