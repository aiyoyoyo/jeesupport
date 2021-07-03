/*
 * Author: Aiyoyoyo
 * https://github.com/aiyoyoyo/jeesjs/tree/master/src/base/Template.js
 * License: MIT license
 *
 */
// namespace:
this.S = this.S || {};
(function() {
	"use strict";
	// constructor: ===========================================================
	/**
	 * @class Base
	 * @static
	 */
	function Base() { throw "Base cannot be instantiated."; };

	// private static properties: =============================================
    Base._locked = false;
	// public static properties: ==============================================
    // private static methods: ================================================
    /**
     * 初始化
     * @method _handle_init
     * @param {Array<Template>} _datas
     * @static
     * @private
     */
    Base._handle_init = function( _datas ){
        this._tree_init();

        var datas = [];
        for( var t of _datas ){
            var file = this._data_2_file( t );
            datas = datas.concat( file );
        }

        $('#files-tree').jstree().settings.core.data = datas;
        $('#files-tree').jstree().refresh( true );
    };

    /**
     * 初始化显示
     * @method _tree_init
     * @static
     * @private
     */
    Base._tree_init = function(){
        // 新的模版
        $('#files-tree').jstree({
            "core": {
                "themes": { "responsive": false },
                'data': []
            },
            "sort": function(a,b){
                var n0 = this.get_node( a );
                var n1 = this.get_node( b );

                if( n0.type == "default" ) return -1;
                if( n0.type == "assets" ) return -1;
                if( n1.type == "assets" ) return 1;
                if( n0.type == "folder" ) return -1;
                if( n1.type == "folder" ) return 1;
                return 0;
            },
            "types": {
                "default" : { "icon": "fa fa-folder text-primary fa-lg" },
                "folder" : { "icon": "fa fa-folder text-warning fa-lg" },
                "file" : { "icon": "fa fa-file text-success fa-lg" },
                "assets" : { "icon": "fa fa-folder text-danger fa-lg" },
            },
            "plugins": ["sort", "types"]
        });
    };
    /**
     * 重新整理页面关系
     * @method _handle_load
     * @param {Template} _t
     * @return {Object}
     * @static
     * @private
     */
    Base._data_2_file = function( _t ){
        var files = { "text": _t.name, "type": "default", "state": { "opened": true }, "children": [] };
        files.children.push( { "text": _t.assets, "type": "assets" } );

        // 循环分析目录及文件
        function _filter_files_( _node, _pages, _lv ){
            var m = new Set();
            for( var k in _pages ){
                if( k == "/" ) {
                    m.add( "index.html" );
                    continue;
                }
                var p = _pages[k];
                var f0 = k.charAt( 0 ) == "/"
                var f1 = k.charAt( k.length - 1 ) == "/";
                if( f0 ) k = k.substr( 1, k.length );
                if( f1 ) k = k.substr( 0, k.length - 1 );
                var r = k.split( "/" );
                var u = "";
                for( var i in r ){
                    if( i == _lv ){
                        u = r[i];
                        if( _lv == 0 ){
                            if( r.length - 1 > _lv ) m.add( u );
                            else{
                                if( f1 ) m.add( u );
                                else m.add( u + ".html" );
                            }
                        }else if( r.length > 1 && r[i-1] == _node.text ){
                            if( f1 ) m.add( u );
                            else m.add( u + ".html" );
                        }
                        break;
                    }
                }
            }
            for( var u of m ){
                var n = { "text": u, "children": [] };
                if( u.lastIndexOf( ".html" ) == -1 ){
                    n.type = "folder";
                    _filter_files_( n, _pages, _lv + 1 );
                }else n.type = "file";
                _node.children.push( n );
            }
        }
        _filter_files_( files, _t.pages, 0 );

        return files;
    };
	// public static methods: =================================================
    /**
     * 初始化
     * @method init
     * @static
     * @public
     */
	Base.init = function(){
	    SuperRemote.loadTemplates( ( _datas )=>{ this._handle_init( _datas ); } ); ;
    };
    /**
     * 将没有纪录的页面文件加入栏目信息
     * @method reload
     * @static
     * @public
     */
    Base.reload = function(){
        if( this._locked ) return;
        this._locked = true;

        var p = $('.panel');
        var pb = $('.panel-body');
        var html = '<div class="panel-loader"><span class="spinner-small"></span></div>';
        p.addClass( 'panel-loading' );
        pb.prepend( html );

        SuperRemote.reload( ()=>{
            p.removeClass('panel-loading');
            p.find('.panel-loader').remove();
            this._locked = false;
        } );
    };
	S.Base = Base;
})();