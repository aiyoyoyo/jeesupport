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
	Editable.TYPE = {
	    TEXT : "text",
	    SELECT : "select",
	    TAGS : "select2",
	}
    // private static methods: ================================================
	// public static methods: =================================================
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
         return '<a href="javascript:;" id="' + _i + '" data-pk="' + _p + '" data-type="'+ _t0 +'" data-title="' + _t1 + '">' + _v + '</a>';
    };
	jeesjs.Editable = Editable;
})();