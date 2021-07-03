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
	function SweetAlert() { throw "SweetAlert cannot be instantiated."; };

	// private static properties: =============================================
	// public static properties: ==============================================
    SweetAlert.ALERT = {
        INFO : "info",
        SUCCESS : "success",
        WARNING : "warning",
        ERROR : "error",
    };
    // private static methods: ================================================
    /**
     * @method _dialog
     * @static
     * @private
     */
    SweetAlert._dialog = function( _t, _c, _i, _yn, _ac, _ly, _ln, _cy, _cn, _ey, _en ){
        swal({
            title: _t,
            text: _c,
            icon: _i,
            closeOnClickOutside : false,
            buttons: {
                cancel: {
                    text: _ln,
                    value: null,
                    visible: _yn,
                    className: 'btn ' + _cn,
                    closeOnConfirm : true,
                },
                confirm: {
                    text: _ly,
                    value: true,
                    visible: true,
                    className: 'btn' + _cy,
                    closeOnConfirm : true
                }
            }
        }).then((yes) => {
            if (yes) {
                if( _ey != undefined ) _ey();
            } else {
                if( _en != undefined ) _en();
            };
        });
    };
	// public static methods: =================================================
    /**
     * @method alert
     * @param {String} _t title
     * @param {String} _c context
     * @param {jeesjs.SweetAlert.ALERT|String} _i icon
     * @param {Function} _h handler
     * @static
     * @public
     */
    SweetAlert.alert = function( _t, _c, _i, _h ) {
        this._dialog( _t, _c, _i, false, false, "确定", "", "btn-primary", "", _h );
    };
    /**
     * @method confirm
     * @param {String} _t title
     * @param {String} _c context
     * @param {jeesjs.SweetAlert.ALERT|String} _i icon
     * @param {Function} _y handler
     * @param {Function} _n handler
     * @static
     * @public
     */
    SweetAlert.confirm = function( _t, _c, _i, _h ) {
        this._dialog( _t, _c, _i, true, false, "确定", "取消", "btn-warning", "btn-default", _h );
    };

	jeesjs.SweetAlert = SweetAlert;
})();