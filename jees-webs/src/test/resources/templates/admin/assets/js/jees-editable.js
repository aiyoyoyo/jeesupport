/*
 * Author: Aiyoyoyo
 * https://github.com/aiyoyoyo/jeesjs/tree/master/src/base/Template.js
 * License: MIT license
 *
 */
// namespace:
this.jeesjs = this.jeesjs || {};

(function() {
	"use strict";
	// constructor: ===========================================================
	/**
	 * @class Editable
	 * @static
	 */
	function Editable() { throw "Editable cannot be instantiated."; };

	// private static properties: =============================================
	// public static properties: ==============================================
	Editable.options = {
	    mode : "inline",
	    inputclass : "form-control input-sm",
	};
	Editable.TYPE = {
	    TEXT : "text",
	    SELECT : "select",
	    TAGS : "select2",
	    PASSWORD : "password",
	    CHECK : "checklist",
	};
    // private static methods: ================================================
	// public static methods: =================================================
	/**
	 * 初始化
     * @method init
     * @param {Function} _handle
     * @static
     * @public
     */
	Editable.init = function( _handle ){
	    $.fn.editable.defaults.mode = this.options.mode;
        $.fn.editable.defaults.inputclass = this.options.inputclass;
        $.fn.editable.defaults.url = _handle;
	};
    /**
     * @method text
     * @param {Dom} _e
     * @static
     * @public
     */
	Editable.text = function( _e ){
        _e.editable();
    };
    /**
     * @method password
     * @param {Dom} _e
     * @static
     * @public
     */
    Editable.password = function( _e ){
        _e.editable();
    };
    /**
     * @method select
     * @param {Dom} _e
     * @param {String} _v
     * @param {Array} _g
     * @static
     * @public
     */
    Editable.select = function( _e, _v, _g ){
        _e.editable({
            prepend: _v,
            source: _g,
        });
    };
    /**
     * @method tags
     * @param {Dom} _e
     * @param {Array} _s
     * @static
     * @public
     */
    Editable.tags = function( _e, _s ){
        _e.editable({
            inputclass: 'form-control',
            select2: {
                tags: _s,
                tokenSeparators: [",", " "]
            }
        });
    };
    /**
     * @method check
     * @param {Dom} _e
     * @static
     * @public
     */
    Editable.check = function( _e, _s ){
        _e.editable({
            source: _s
        });
    };
    /**
     * @method generate
     * @param {Number|String} _p
     * @param {jeesjs.TYPE|String} _t0
     * @param {String} _i id
     * @param {String} _v value
     * @param {String} _t1 title
     * @static
     * @public
     */
    Editable.generate = function( _p, _t0, _i, _v, _t1 ) {
         if( _v == undefined ) _v = "";
         if( _t1 == undefined ) _t1 = "";
         if( _t0 == Editable.TYPE.CHECK ){
            return '<a href="javascript:;" id="' + _i + '" data-pk="' + _p + '" data-type="'+ _t0 +'" data-title="' + _t1 + '" data-value="' + _v + '"></a>';
         }
         return '<a href="javascript:;" id="' + _i + '" data-pk="' + _p + '" data-type="'+ _t0 +'" data-title="' + _t1 + '">' + _v + '</a>';
    };
    /**
     * @method generateText
     * @param {Number|String} _p
     * @param {String} _i id
     * @param {String} _v value
     * @param {String} _t title
     * @static
     * @public
     */
    Editable.generateText = function( _p, _i, _v, _t ){
        if( _v == undefined ) _v = "";
        if( _t == undefined ) _t = "";
        return '<a href="javascript:;" id="' + _i
            + '" data-pk="' + _p
            + '" data-type="' + this.TYPE.TEXT
            + '" data-title="' + _t + '" data-value="' + _v + '"></a>';
    };
    /**
     * @method generate
     * @param {Number|String} _p
     * @param {String} _i id
     * @param {String} _v value
     * @param {String} _t title
     * @static
     * @public
     */
    Editable.generateSelect = function( _p, _i, _v, _t ) {
        if( _v == undefined ) _v = "";
        if( _t == undefined ) _t = "";
        return '<a href="javascript:;" id="' + _i
            + '" data-pk="' + _p
            + '" data-type="'+ this.TYPE.SELECT
            + '" data-title="' + _t + '">' + _v + '</a>';
    };
    /**
     * @method generateTags
     * @param {Number|String} _p
     * @param {String} _i id
     * @param {String} _v value
     * @param {String} _t title
     * @static
     * @public
     */
    Editable.generateTags = function( _p, _i, _v, _t ){
        if( _v == undefined ) _v = "";
        if( _t == undefined ) _t = "";
        return '<a href="javascript:;" id="' + _i
            + '" data-pk="' + _p
            + '" data-type="' + this.TYPE.TAGS
            + '" data-title="' + _t + '" data-value="' + _v + '"></a>';
    };

    /**
     * @method generatePassword
     * @param {Number|String} _p
     * @param {String} _i id
     * @param {String} _v value
     * @param {String} _t title
     * @static
     * @public
     */
    Editable.generatePassword = function( _p, _i, _v, _t ){
        if( _v == undefined ) _v = "";
        if( _t == undefined ) _t = "";
        return '<a href="javascript:;" id="' + _i
            + '" data-pk="' + _p
            + '" data-type="' + this.TYPE.PASSWORD
            + '" data-title="' + _t + '" data-value="' + _v + '"></a>';
    };
	jeesjs.Editable = Editable;
})();