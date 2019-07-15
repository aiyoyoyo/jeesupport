var handleInstallStep = function(){
    InstallRemote.step( function( _step ){
        handleInstallFinish( _step );
        if( _step.step == 0 ){
            $("#redisStatus").hide();
            $("#adminStatus").hide();
            $("#otherStatus").hide();
        }else{
            handleRedisInstall( _step );
            handleAdminInstall( _step );
            handleOtherInstall( _step );
            handleInstallSuccAlert( _step );
        }
    });
}
var handleRedisInstall = function( _step ){
    if( ( _step.step & 1 ) == 1 ){
        $("#redisStatus").show();
        $("#redisHost").val( _step.redisHost );
        $("#redisPort").val( _step.redisPort );
        $("#redisPassword").val( _step.redisPassword );
        $("#redisDatabase").val( _step.redisDatabase );
        $("#btnRedis0").hide();
        $("#btnRedis1").show();
    }else{
        $("#btnRedis0").show();
        $("#btnRedis1").hide();
    }
}
var handleAdminInstall = function( _step ){
    if( ( _step.step & 2 ) == 2 ){
        $("#adminStatus").show();
        $("#adminAccount").val( _step.adminAccount );
        $("#adminPassword").val( _step.adminPassword );
        $("#adminGroup").val( _step.adminGroup );
        $("#btnAdmin0").hide();
        $("#btnAdmin1").show();
    }else{
        $("#btnAdmin0").show();
        $("#btnAdmin1").hide();
    }
}
var handleOtherInstall = function( _step ){
    if( ( _step.step & 4 ) == 4 ){
        $("#otherStatus").show();
        $("#serverPort").val( _step.serverPort );
        $("#installPath").val( _step.installPath );
        $("#btnOther0").hide();
        $("#btnOther1").show();
    }else{
        $("#btnOther0").show();
        $("#btnOther1").hide();
    }
}
var handleInstallFinish = function( _step ){
    if( _step.step == 7 ){
        $("#btnStart").hide();
        $("#btnFinish").show();
        $("#btnRestart").hide();
    }else if( ( _step.step & 8 ) == 8 ){
        $("#btnStart").hide();
        $("#btnFinish").hide();
        $("#btnRestart").show();
    }else{
        $("#btnStart").show();
        $("#btnFinish").hide();
        $("#btnRestart").hide();
    }
}
var handleInstallSuccAlert = function( _step ){
    if( ( _step.step & 8 ) == 8 ){
        swal({
            title: '安装已完成',
            text: '现在你可以通过重启启动服务器，登录后台来做更详细的系统设置了。',
            icon: 'success',
            buttons: {
                confirm: {
                    text: '确定',
                    value: true,
                    visible: true,
                    className: 'btn btn-info',
                    closeModal: true
                }
            }
        });
    }
}
var handleInstallFailAlert = function( _tips ){
    swal({
        title: '安装失败',
        text: _tips,
        icon: 'error',
        buttons: {
            confirm: {
                text: '确定',
                value: true,
                visible: true,
                className: 'btn btn-danger',
                closeModal: true
            }
        }
    });
}
var initTips = function( _name ){
    $("#" + _name + "AlertClose").on( "click", function(){
        $("#" + _name + "Alert").hide();
    });
}
var hideTips = function( _name ){
    $("#" + _name + "Status").hide();
    $("#" + _name + "Alert").hide();
}
/* Install Controller
------------------------------------------------ */
var Install = function () {
	"use strict";
	var setting;
	var running;
	return {
	    _handle_success: function(){
	        running = false;
            handleInstallStep();
        },
        _handle_error: function( _name ){
            running = false;
            $("#" + _name + "Alert").show();
            handleInstallStep();
        },
        _handle_install: function( _tips ){
            if( _tips.length == "" ) handleInstallStep();
            else handleInstallFailAlert( _tips );
        },
		//main function
		init: function (option) {
			if (option) {
				setting = option;
			}
            running = false;

            handleLocalStorage();
            handlePageContentView();
            handlePanelAction();
			handleInstallStep();

			$(window).trigger('load');

			initTips( "redis" );
			hideTips( "redis" );
		},
        redis: function(){
            if( running ) return;
            running = true;
            hideTips( "redis" );

            var host = $("#redisHost").val();
            var port = $("#redisPort").val();
            var pass = $("#redisPassword").val();
            var db = $("#redisDatabase").val();

            InstallRemote.redis( host, port, pass, db, {
                callback: this._handle_success,
                errorHandler:()=>{this._handle_error( "redis" );}
            });
        },
        admin: function(){
            if( running ) return;
            running = true;

            var account = $("#adminAccount").val().trim();
            var password = $("#adminPassword").val().trim();
            var group = $("#adminGroup").val().trim();

            if( account.length == 0 || password.length == 0 || group.length == 0 ){
                return;
            }

            InstallRemote.admin( account, password, group, this._handle_success );
        },
        other: function(){
            if( running ) return;
            running = true;

            var port = $("#serverPort").val();
            var installPath = $("#installPath").val();

            if( installPath.trim().length == 0 || port.trim().length == 0 ){
                return;
            }

            InstallRemote.other( port, installPath, this._handle_success );
        },
        finish: function(){
            if( running ) return;
            running = true;

            InstallRemote.finish( this._handle_install );
        }
  };
}();