/*
Template Name: Color Admin - Responsive Admin Dashboard Template build with Twitter Bootstrap 3 & 4
Version: 4.1.0
Author: Sean Ngu
Website: http://www.seantheme.com/color-admin-v4.1/admin/
    ----------------------------
        APPS CONTENT TABLE
    ----------------------------
    
    <!-- ======== GLOBAL SCRIPT SETTING ======== -->
    01. Handle Scrollbar
	02. Handle Sidebar - Menu
	03. Handle Sidebar - Mobile View Toggle
	04. Handle Sidebar - Minify / Expand
	05. Handle Page Load - Fade in
	06. Handle Panel - Remove / Reload / Collapse / Expand
	07. Handle Panel - Draggable
	08. Handle Tooltip & Popover Activation
	09. Handle Scroll to Top Button Activation
	    
    <!-- ======== Added in V1.2 ======== -->
    10. Handle Theme & Page Structure Configuration - added in V1.2
	11. Handle Theme Panel Expand - added in V1.2
	12. Handle After Page Load Add Class Function - added in V1.2
    
    <!-- ======== Added in V1.5 ======== -->
    13. Handle Save Panel Position Function - added in V1.5
	14. Handle Draggable Panel Local Storage Function - added in V1.5
	15. Handle Reset Local Storage - added in V1.5
    
    <!-- ======== Added in V1.6 ======== -->
    16. Handle IE Full Height Page Compatibility - added in V1.6
	17. Handle Unlimited Nav Tabs - added in V1.6
    
    <!-- ======== Added in V1.9 ======== -->
    18. Handle Top Menu - Unlimited Top Menu Render - added in V1.9
	19. Handle Top Menu - Sub Menu Toggle - added in V1.9
	20. Handle Top Menu - Mobile Sub Menu Toggle - added in V1.9
	21. Handle Top Menu - Mobile Top Menu Toggle - added in V1.9
	22. Handle Clear Sidebar Selection & Hide Mobile Menu - added in V1.9
    
    <!-- ======== Added in V4.0 ======== -->
    23. Handle Check Bootstrap Version - added in V4.0
	24. Handle Page Scroll Class - added in V4.0
	25. Handle Toggle Navbar Profile - added in V4.0
	26. Handle Sidebar Scroll Memory - added in V4.0
	27. Handle Sidebar Minify Sub Menu - added in V4.0
	28. Handle Ajax Mode - added in V4.0
	29. Handle Float Navbar Search - added in V4.0

    <!-- ======== APPLICATION SETTING ======== -->
    Application Controller
*/



/* 01. Handle Scrollbar
------------------------------------------------ */
var handleSlimScroll = function() {
    "use strict";
    $('[data-scrollbar=true]').each( function() {
        generateSlimScroll($(this));
    });
};
var generateSlimScroll = function(element) {
    if ($(element).attr('data-init')) {
        return;
    }
    var dataHeight = $(element).attr('data-height');
        dataHeight = (!dataHeight) ? $(element).height() : dataHeight;
    
    var scrollBarOption = {
        height: dataHeight
    };
    if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
        $(element).css('height', dataHeight);
        $(element).css('overflow-x','scroll');
    } else {
        $(element).slimScroll(scrollBarOption);
    }
    $(element).attr('data-init', true);
    $('.slimScrollBar').hide();
};


/* 02. Handle Sidebar - Menu
------------------------------------------------ */
var handleSidebarMenu = function() {
    "use strict";
    
    var expandTime = ($('.sidebar').attr('data-disable-slide-animation')) ? 0 : 250;
    $('.sidebar .nav > .has-sub > a').click(function() {
        var target = $(this).next('.sub-menu');
        var otherMenu = $('.sidebar .nav > li.has-sub > .sub-menu').not(target);
    
        if ($('.page-sidebar-minified').length === 0) {
        	$(otherMenu).closest('li').addClass('closing');
            $(otherMenu).slideUp(expandTime, function() {
                $(otherMenu).closest('li').addClass('closed').removeClass('expand closing');
            });
            if ($(target).is(':visible')) {
            	$(target).closest('li').addClass('closing').removeClass('expand');
            } else {
            	$(target).closest('li').addClass('expanding').removeClass('closed');
            }
            $(target).slideToggle(expandTime, function() {
                var targetLi = $(this).closest('li');
                if (!$(target).is(':visible')) {
                    $(targetLi).addClass('closed');
                    $(targetLi).removeClass('expand');
                } else {
                    $(targetLi).addClass('expand');
                    $(targetLi).removeClass('closed');
                }
                $(targetLi).removeClass('expanding closing');
            });
        }
    });
    $('.sidebar .nav > .has-sub .sub-menu li.has-sub > a').click(function() {
        if ($('.page-sidebar-minified').length === 0) {
            var target = $(this).next('.sub-menu');
            if ($(target).is(':visible')) {
            	$(target).closest('li').addClass('closing').removeClass('expand');
            } else {
            	$(target).closest('li').addClass('expanding').removeClass('closed');
            }
            $(target).slideToggle(expandTime, function() {
                var targetLi = $(this).closest('li');
                if (!$(target).is(':visible')) {
                    $(targetLi).addClass('closed');
                    $(targetLi).removeClass('expand');
                } else {
                    $(targetLi).addClass('expand');
                    $(targetLi).removeClass('closed');
                }
                $(targetLi).removeClass('expanding closing');
            });
        }
    });
};


/* 03. Handle Sidebar - Mobile View Toggle
------------------------------------------------ */
var handleMobileSidebarToggle = function() {
	var sidebarProgress = false;
	
    $('.sidebar').bind('click touchstart', function(e) {
        if ($(e.target).closest('.sidebar').length !== 0) {
            sidebarProgress = true;
        } else {
            sidebarProgress = false;
            e.stopPropagation();
        }
    });
    
    $(document).bind('click touchstart', function(e) {
        if ($(e.target).closest('.sidebar').length === 0) {
            sidebarProgress = false;
        }
        if ($(e.target).closest('#float-sub-menu').length !== 0) {
            sidebarProgress = true;
        }
        
        if (!e.isPropagationStopped() && sidebarProgress !== true) {
            if ($('#page-container').hasClass('page-sidebar-toggled')) {
                sidebarProgress = true;
                $('#page-container').removeClass('page-sidebar-toggled');
            }
            if ($(window).width() <= 767) {
                if ($('#page-container').hasClass('page-right-sidebar-toggled')) {
                    sidebarProgress = true;
                    $('#page-container').removeClass('page-right-sidebar-toggled');
                }
            }
        }
    });
    
    $('[data-click=right-sidebar-toggled]').click(function(e) {
        e.stopPropagation();
        var targetContainer = '#page-container';
        var targetClass = 'page-right-sidebar-collapsed';
            targetClass = ($(window).width() < 979) ? 'page-right-sidebar-toggled' : targetClass;
        if ($(targetContainer).hasClass(targetClass)) {
            $(targetContainer).removeClass(targetClass);
        } else if (sidebarProgress !== true) {
            $(targetContainer).addClass(targetClass);
        } else {
            sidebarProgress = false;
        }
        if ($(window).width() < 480) {
            $('#page-container').removeClass('page-sidebar-toggled');
        }
        $(window).trigger('resize');
    });
    
    $('[data-click=sidebar-toggled]').click(function(e) {
        e.stopPropagation();
        var sidebarClass = 'page-sidebar-toggled';
        var targetContainer = '#page-container';

        if ($(targetContainer).hasClass(sidebarClass)) {
            $(targetContainer).removeClass(sidebarClass);
        } else if (sidebarProgress !== true) {
            $(targetContainer).addClass(sidebarClass);
        } else {
            sidebarProgress = false;
        }
        if ($(window).width() < 480) {
            $('#page-container').removeClass('page-right-sidebar-toggled');
        }
    });
};


/* 04. Handle Sidebar - Minify / Expand
------------------------------------------------ */
var handleSidebarMinify = function() {
    $(document).on('click', '[data-click=sidebar-minify]', function(e) {
        e.preventDefault();
        var sidebarClass = 'page-sidebar-minified';
        var targetContainer = '#page-container';
        
        if ($(targetContainer).hasClass(sidebarClass)) {
            $(targetContainer).removeClass(sidebarClass);
        } else {
            $(targetContainer).addClass(sidebarClass);
    
            if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
                $('#sidebar [data-scrollbar="true"]').css('margin-top','0');
                $('#sidebar [data-scrollbar="true"]').css('overflow-x', 'scroll');
            }
        }
        $(window).trigger('resize');
    });
};


/* 05. Handle Page Load - Fade in
------------------------------------------------ */
var handlePageContentView = function() {
    "use strict";
    
    var hideClass = '';
    var showClass = '';
    var removeClass = '';
    var bootstrapVersion = handleCheckBootstrapVersion();
    
    if (bootstrapVersion >= 3 && bootstrapVersion < 4) {
    	hideClass = 'hide';
    	showClass = 'in';
	} else if (bootstrapVersion >= 4 && bootstrapVersion < 5) {
		hideClass = 'd-none';
    	showClass = 'show';
	}
	$(window).on('load', function() {
		$.when($('#page-loader').addClass(hideClass)).done(function() {
			$('#page-container').addClass(showClass);
		});
	});
};


/* 06. Handle Panel - Remove / Reload / Collapse / Expand
------------------------------------------------ */
var panelActionRunning = false;
var handlePanelAction = function() {
    "use strict";
    
    if (panelActionRunning) {
        return false;
    }
    panelActionRunning = true;
    
    // remove
    $(document).on('hover', '[data-click=panel-remove]', function(e) {
        if (!$(this).attr('data-init')) {
            $(this).tooltip({
                title: 'Remove',
                placement: 'bottom',
                trigger: 'hover',
                container: 'body'
            });
            $(this).tooltip('show');
            $(this).attr('data-init', true);
        }
    });
    $(document).on('click', '[data-click=panel-remove]', function(e) {
        e.preventDefault();
        var bootstrapVersion = handleCheckBootstrapVersion();
        
        if (bootstrapVersion >= 4 && bootstrapVersion < 5) {
        	$(this).tooltip('dispose');
        } else {
        	$(this).tooltip('destroy');
        }
        $(this).closest('.panel').remove();
    });
    
    // collapse
    $(document).on('hover', '[data-click=panel-collapse]', function(e) {
        if (!$(this).attr('data-init')) {
            $(this).tooltip({
                title: 'Collapse / Expand',
                placement: 'bottom',
                trigger: 'hover',
                container: 'body'
            });
            $(this).tooltip('show');
            $(this).attr('data-init', true);
        }
    });
    $(document).on('click', '[data-click=panel-collapse]', function(e) {
        e.preventDefault();
        $(this).closest('.panel').find('.panel-body').slideToggle();
    });
    
    // reload
    $(document).on('hover', '[data-click=panel-reload]', function(e) {
        if (!$(this).attr('data-init')) {
            $(this).tooltip({
                title: 'Reload',
                placement: 'bottom',
                trigger: 'hover',
                container: 'body'
            });
            $(this).tooltip('show');
            $(this).attr('data-init', true);
        }
    });
    $(document).on('click', '[data-click=panel-reload]', function(e) {
        e.preventDefault();
        var target = $(this).closest('.panel');
        if (!$(target).hasClass('panel-loading')) {
            var targetBody = $(target).find('.panel-body');
            var spinnerHtml = '<div class="panel-loader"><span class="spinner-small"></span></div>';
            $(target).addClass('panel-loading');
            $(targetBody).prepend(spinnerHtml);
            setTimeout(function() {
                $(target).removeClass('panel-loading');
                $(target).find('.panel-loader').remove();
            }, 2000);
        }
    });
    
    // expand
    $(document).on('hover', '[data-click=panel-expand]', function(e) {
        if (!$(this).attr('data-init')) {
            $(this).tooltip({
                title: 'Expand / Compress',
                placement: 'bottom',
                trigger: 'hover',
                container: 'body'
            });
            $(this).tooltip('show');
            $(this).attr('data-init', true);
        }
    });
    $(document).on('click', '[data-click=panel-expand]', function(e) {
        e.preventDefault();
        var target = $(this).closest('.panel');
        var targetBody = $(target).find('.panel-body');
        var targetTop = 40;
        if ($(targetBody).length !== 0) {
            var targetOffsetTop = $(target).offset().top;
            var targetBodyOffsetTop = $(targetBody).offset().top;
            targetTop = targetBodyOffsetTop - targetOffsetTop;
        }
        
        if ($('body').hasClass('panel-expand') && $(target).hasClass('panel-expand')) {
            $('body, .panel').removeClass('panel-expand');
            $('.panel').removeAttr('style');
            $(targetBody).removeAttr('style');
        } else {
            $('body').addClass('panel-expand');
            $(this).closest('.panel').addClass('panel-expand');
            
            if ($(targetBody).length !== 0 && targetTop != 40) {
                var finalHeight = 40;
                $(target).find(' > *').each(function() {
                    var targetClass = $(this).attr('class');
                    
                    if (targetClass != 'panel-heading' && targetClass != 'panel-body') {
                        finalHeight += $(this).height() + 30;
                    }
                });
                if (finalHeight != 40) {
                    $(targetBody).css('top', finalHeight + 'px');
                }
            }
        }
        $(window).trigger('resize');
    });
};


/* 07. Handle Panel - Draggable
------------------------------------------------ */
var handleDraggablePanel = function() {
    "use strict";
    var target = $('.panel:not([data-sortable="false"])').parent('[class*=col]');
    var targetHandle = '.panel-heading';
    var connectedTarget = '.row > [class*=col]';
    
    $(target).sortable({
        handle: targetHandle,
        connectWith: connectedTarget,
        stop: function(event, ui) {
            ui.item.find('.panel-title').append('<i class="fa fa-refresh fa-spin m-l-5" data-id="title-spinner"></i>');
            handleSavePanelPosition(ui.item);
        }
    });
};


/* 08. Handle Tooltip & Popover Activation
------------------------------------------------ */
var handelTooltipPopoverActivation = function() {
    "use strict";
    if ($('[data-toggle="tooltip"]').length !== 0) {
        $('[data-toggle=tooltip]').tooltip();
    }
    if ($('[data-toggle="popover"]').length !== 0) {
        $('[data-toggle=popover]').popover();
    }
};


/* 09. Handle Scroll to Top Button Activation
------------------------------------------------ */
var handleScrollToTopButton = function() {
    "use strict";
    var bootstrapVersion = handleCheckBootstrapVersion();
    var showClass = '';
    
    if (bootstrapVersion >= 3 && bootstrapVersion < 4) {
    	showClass = 'in';
	} else if (bootstrapVersion >= 4 && bootstrapVersion < 5) {
    	showClass = 'show';
	}
    $(document).scroll( function() {
        var totalScroll = $(document).scrollTop();

        if (totalScroll >= 200) {
            $('[data-click=scroll-top]').addClass(showClass);
        } else {
            $('[data-click=scroll-top]').removeClass(showClass);
        }
    });

    $('[data-click=scroll-top]').click(function(e) {
        e.preventDefault();
        $('html, body').animate({
            scrollTop: $("body").offset().top
        }, 500);
    });
};


/* 10. Handle Theme & Page Structure Configuration - added in V1.2
------------------------------------------------ */
var handleThemePageStructureControl = function() {
    
    // THEME - theme selection
    $(document).on('click', '.theme-list [data-theme]', function() {
        var cssFileSrc = $(this).attr('data-theme-file');
        $('#theme').attr('href', cssFileSrc);
        $('.theme-list [data-theme]').not(this).closest('li').removeClass('active');
        $(this).closest('li').addClass('active');
        Cookies.set('theme', $(this).attr('data-theme'));
    });
    
    // COOKIE - theme selection
    if (Cookies && Cookies.get('theme')) {
        if ($('.theme-list [data-theme]').length !== 0) {
            $('.theme-list [data-theme="'+ Cookies.get('theme') +'"]').trigger('click');
        }
    }
    
    // HEADER - header styling selection
    $(document).on('change', '.theme-panel [name=header-styling]', function() {
        var targetClassAdd = ($(this).val() == 1) ? 'navbar-default' : 'navbar-inverse';
        var targetClassRemove = ($(this).val() == 1) ? 'navbar-inverse' : 'navbar-default';
        $('#header').removeClass(targetClassRemove).addClass(targetClassAdd);
        Cookies.set('header-styling', $(this).val());
    });
    
    // COOKIE - header styling selection
    if (Cookies && Cookies.get('header-styling')) {
    	var targetElm = '.theme-panel [name="header-styling"]';
    	if ($(targetElm).length !== 0) {
            $(targetElm + ' option[value="'+ Cookies.get('header-styling') +'"]').prop('selected', true);
            $(targetElm).trigger('change');
    	}
    }
    
    // SIDEBAR - sidebar styling selection
    $(document).on('change', '.theme-panel [name=sidebar-styling]', function() {
        if ($(this).val() == 2) {
            $('#sidebar').addClass('sidebar-grid');
        	Cookies.set('sidebar-styling', $(this).val());
        } else {
            $('#sidebar').removeClass('sidebar-grid');
        	Cookies.set('sidebar-styling', $(this).val());
        }
    });
    
    // COOKIE - sidebar styling selection
    if (Cookies && Cookies.get('sidebar-styling')) {
    	var targetElm = '.theme-panel [name="sidebar-styling"]';
    	if ($(targetElm).length !== 0) {
            $(targetElm + ' option[value="'+ Cookies.get('sidebar-styling') +'"]').prop('selected', true);
            $(targetElm).trigger('change');
        }
    }
    
    // CONTENT - content gradient selection
    $(document).on('change', '.theme-panel [name=content-gradient]', function() {
        if ($(this).val() == 2) {
            $('#page-container').addClass('gradient-enabled');
        	Cookies.set('content-gradient', $(this).val());
        } else {
            $('#page-container').removeClass('gradient-enabled');
        	Cookies.set('content-gradient', $(this).val());
        }
    });
    
    // COOKIE - content gradient selection
    if (Cookies && Cookies.get('content-gradient')) {
    	var targetElm = '.theme-panel [name="content-gradient"]';
    	if ($(targetElm).length !== 0) {
            $(targetElm + ' option[value="'+ Cookies.get('content-gradient') +'"]').prop('selected', true);
            $(targetElm).trigger('change');
        }
    }
    
    // CONTENT - content styling selection
    $(document).on('change', '.theme-panel [name=content-styling]', function() {
        if ($(this).val() == 2) {
            $('body').addClass('flat-black');
        	Cookies.set('content-styling', $(this).val());
        } else {
            $('body').removeClass('flat-black');
        	Cookies.set('content-styling', $(this).val());
        }
    });
    
    // COOKIE - content styling selection
    if (Cookies && Cookies.get('content-styling')) {
    	var targetElm = '.theme-panel [name="content-styling"]';
    	if ($(targetElm).length !== 0) {
            $(targetElm + ' option[value="'+ Cookies.get('content-styling') +'"]').prop('selected', true);
            $(targetElm).trigger('change');
        }
    }
    
    // DIRECTION - direction selection
    $(document).on('change', '.theme-panel [name=direction]', function() {
        if ($(this).val() == 2) {
            $('body').addClass('rtl-mode');
        	Cookies.set('direction', $(this).val());
        } else {
            $('body').removeClass('rtl-mode');
        	Cookies.set('direction', $(this).val());
        }
    });
    
    // COOKIE - direction selection
    if (Cookies && Cookies.get('direction')) {
    	var targetElm = '.theme-panel [name="direction"]';
    	if ($(targetElm).length !== 0) {
            $(targetElm + ' option[value="'+ Cookies.get('direction') +'"]').prop('selected', true);
            $(targetElm).trigger('change');
        }
    }
    
    // SIDEBAR - sidebar fixed selection
    $(document).on('change', '.theme-panel [name=sidebar-fixed]', function() {
        if ($(this).val() == 1) {
            if ($('.theme-panel [name=header-fixed]').val() == 2) {
                alert('Default Header with Fixed Sidebar option is not supported. Proceed with Fixed Header with Fixed Sidebar.');
                $('.theme-panel [name=header-fixed] option[value="1"]').prop('selected', true);
                $('#page-container').addClass('page-header-fixed');
            }
            $('#page-container').addClass('page-sidebar-fixed');
            if (!$('#page-container').hasClass('page-sidebar-minified')) {
                generateSlimScroll($('.sidebar [data-scrollbar="true"]'));
            }
        } else {
            $('#page-container').removeClass('page-sidebar-fixed');
            if ($('.sidebar .slimScrollDiv').length !== 0) {
                if ($(window).width() <= 979) {
                    $('.sidebar').each(function() {
                        if (!($('#page-container').hasClass('page-with-two-sidebar') && $(this).hasClass('sidebar-right'))) {
                            $(this).find('.slimScrollBar').remove();
                            $(this).find('.slimScrollRail').remove();
                            $(this).find('[data-scrollbar="true"]').removeAttr('style');
                            var targetElement = $(this).find('[data-scrollbar="true"]').parent();
                            var targetHtml = $(targetElement).html();
                            $(targetElement).replaceWith(targetHtml);
                        }
                    });
                } else if ($(window).width() > 979) {
                    $('.sidebar [data-scrollbar="true"]').slimScroll({destroy: true});
                    $('.sidebar [data-scrollbar="true"]').removeAttr('style');
                    $('.sidebar [data-scrollbar="true"]').removeAttr('data-init');
                }
            }
            if ($('#page-container .sidebar-bg').length === 0) {
                $('#page-container').append('<div class="sidebar-bg"></div>');
            }
        }
    });
    
    // HEADER - fixed or default
    $(document).on('change', '.theme-panel [name=header-fixed]', function() {
        if ($(this).val() == 1) {
            $('#header').addClass('navbar-fixed-top');
            $('#page-container').addClass('page-header-fixed');
        	Cookies.set('header-fixed', true);
        } else {
            if ($('.theme-panel [name=sidebar-fixed]').val() == 1) {
                alert('Default Header with Fixed Sidebar option is not supported. Proceed with Default Header with Default Sidebar.');
                $('.theme-panel [name=sidebar-fixed] option[value="2"]').prop('selected', true);
                $('.theme-panel [name=sidebar-fixed]').trigger('change');
                if ($('#page-container .sidebar-bg').length === 0) {
                    $('#page-container').append('<div class="sidebar-bg"></div>');
                }
            }
            $('#header').removeClass('navbar-fixed-top');
            $('#page-container').removeClass('page-header-fixed');
        	Cookies.set('header-fixed', false);
        }
    });
};


/* 11. Handle Theme Panel Expand - added in V1.2
------------------------------------------------ */
var handleThemePanelExpand = function() {
    $(document).on('click', '[data-click="theme-panel-expand"]', function() {
        var targetContainer = '.theme-panel';
        var targetClass = 'active';
        if ($(targetContainer).hasClass(targetClass)) {
            $(targetContainer).removeClass(targetClass);
        } else {
            $(targetContainer).addClass(targetClass);
        }
    });
};


/* 12. Handle After Page Load Add Class Function - added in V1.2
------------------------------------------------ */
var handleAfterPageLoadAddClass = function() {
    if ($('[data-pageload-addclass]').length !== 0) {
        $(window).on('load', function() {
            $('[data-pageload-addclass]').each(function() {
                var targetClass = $(this).attr('data-pageload-addclass');
                $(this).addClass(targetClass);
            });
        });
    }
};


/* 13. Handle Save Panel Position Function - added in V1.5
------------------------------------------------ */
var handleSavePanelPosition = function(element) {
    "use strict";
    if ($('.ui-sortable').length !== 0) {
        var newValue = [];
        var index = 0;
        $.when($('.ui-sortable').each(function() {
            var panelSortableElement = $(this).find('[data-sortable-id]');
            if (panelSortableElement.length !== 0) {
                var columnValue = [];
                $(panelSortableElement).each(function() {
                    var targetSortId = $(this).attr('data-sortable-id');
                    columnValue.push({id: targetSortId});
                });
                newValue.push(columnValue);
            } else {
                newValue.push([]);
            }
            index++;
        })).done(function() {
            var targetPage = window.location.href;
                targetPage = targetPage.split('?');
                targetPage = targetPage[0];
            localStorage.setItem(targetPage, JSON.stringify(newValue));
            $(element).find('[data-id="title-spinner"]').delay(500).fadeOut(500, function() {
                $(this).remove();
            });
        });
    }
};


/* 14. Handle Draggable Panel Local Storage Function - added in V1.5
------------------------------------------------ */
var handleLocalStorage = function() {
    "use strict";
    if (typeof(Storage) !== 'undefined' && typeof(localStorage) !== 'undefined') {
        var targetPage = window.location.href;
            targetPage = targetPage.split('?');
            targetPage = targetPage[0];
        var panelPositionData = localStorage.getItem(targetPage);
        
        if (panelPositionData) {
            panelPositionData = JSON.parse(panelPositionData);
            var i = 0;
            $.when($('.panel:not([data-sortable="false"])').parent('[class*="col-"]').each(function() {
                var storageData = panelPositionData[i]; 
                var targetColumn = $(this);
                if (storageData) {
                    $.each(storageData, function(index, data) {
                        var targetId = $('[data-sortable-id="'+ data.id +'"]').not('[data-init="true"]');
                        if ($(targetId).length !== 0) {
                            var targetHtml = $(targetId).clone();
                            $(targetId).remove();
                            $(targetColumn).append(targetHtml);
                            $('[data-sortable-id="'+ data.id +'"]').attr('data-init','true');
                        }
                    });
                }
                i++;
            })).done(function() {
            	window.dispatchEvent(new CustomEvent('localstorage-position-loaded'));
            });
        }
    } else {
        alert('Your browser is not supported with the local storage'); 
    }
};


/* 15. Handle Reset Local Storage - added in V1.5
------------------------------------------------ */
var handleResetLocalStorage = function() {
    "use strict";
    $(document).on('click', '[data-click=reset-local-storage]', function(e) {
        e.preventDefault();
        
        var targetModalHtml = ''+
        '<div class="modal fade" data-modal-id="reset-local-storage-confirmation">'+
        '    <div class="modal-dialog">'+
        '        <div class="modal-content">'+
        '            <div class="modal-header">'+
        '                <h4 class="modal-title"><i class="fa fa-redo m-r-5"></i> Reset Local Storage Confirmation</h4>'+
        '                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>'+
        '            </div>'+
        '            <div class="modal-body">'+
        '                <div class="alert alert-info m-b-0">Would you like to RESET all your saved widgets and clear Local Storage?</div>'+
        '            </div>'+
        '            <div class="modal-footer">'+
        '                <a href="javascript:;" class="btn btn-sm btn-default" data-dismiss="modal"><i class="fa fa-times"></i> No</a>'+
        '                <a href="javascript:;" class="btn btn-sm btn-inverse" data-click="confirm-reset-local-storage"><i class="fa fa-check"></i> Yes</a>'+
        '            </div>'+
        '        </div>'+
        '    </div>'+
        '</div>';
        
        $('body').append(targetModalHtml);
        $('[data-modal-id="reset-local-storage-confirmation"]').modal('show');
    });
    $(document).on('hidden.bs.modal', '[data-modal-id="reset-local-storage-confirmation"]', function(e) {
        $('[data-modal-id="reset-local-storage-confirmation"]').remove();
    });
    $(document).on('click', '[data-click=confirm-reset-local-storage]', function(e) {
        e.preventDefault();
        var localStorageName = window.location.href;
            localStorageName = localStorageName.split('?');
            localStorageName = localStorageName[0];
        localStorage.removeItem(localStorageName);
        
        location.reload();
    });
};


/* 16. Handle IE Full Height Page Compatibility - added in V1.6
------------------------------------------------ */
var handleIEFullHeightContent = function() {
    var userAgent = window.navigator.userAgent;
    var msie = userAgent.indexOf("MSIE ");

    if (msie > 0) {
        $('.vertical-box-row [data-scrollbar="true"][data-height="100%"]').each(function() {
            var targetRow = $(this).closest('.vertical-box-row');
            var targetHeight = $(targetRow).height();
            $(targetRow).find('.vertical-box-cell').height(targetHeight);
        });
    }
};


/* 17. Handle Unlimited Nav Tabs - added in V1.6
------------------------------------------------ */
var handleUnlimitedTabsRender = function() {
    
    // function handle tab overflow scroll width 
    function handleTabOverflowScrollWidth(obj, animationSpeed) {
    	var targetElm = 'li.active';
    	
    	if ($(obj).find('li').first().hasClass('nav-item')) {
    		targetElm = $(obj).find('.nav-item .active').closest('li');
    	}
    	var targetCss = ($('body').hasClass('rtl-mode')) ? 'margin-right' : 'margin-left';
        var marginLeft = parseInt($(obj).css(targetCss));  
        var viewWidth = $(obj).width();
        var prevWidth = $(obj).find(targetElm).width();
        var speed = (animationSpeed > -1) ? animationSpeed : 150;
        var fullWidth = 0;

        $(obj).find(targetElm).prevAll().each(function() {
            prevWidth += $(this).width();
        });

        $(obj).find('li').each(function() {
            fullWidth += $(this).width();
        });

        if (prevWidth >= viewWidth) {
            var finalScrollWidth = prevWidth - viewWidth;
            if (fullWidth != prevWidth) {
                finalScrollWidth += 40;
            }
            if ($('body').hasClass('rtl-mode')) {
            	$(obj).find('.nav.nav-tabs').animate({ marginRight: '-' + finalScrollWidth + 'px'}, speed);
            } else {
            	$(obj).find('.nav.nav-tabs').animate({ marginLeft: '-' + finalScrollWidth + 'px'}, speed);
            }
        }

        if (prevWidth != fullWidth && fullWidth >= viewWidth) {
            $(obj).addClass('overflow-right');
        } else {
            $(obj).removeClass('overflow-right');
        }

        if (prevWidth >= viewWidth && fullWidth >= viewWidth) {
            $(obj).addClass('overflow-left');
        } else {
            $(obj).removeClass('overflow-left');
        }
    }
    
    // function handle tab button action - next / prev
    function handleTabButtonAction(element, direction) {
        var obj = $(element).closest('.tab-overflow');
        var targetCss = ($('body').hasClass('rtl-mode')) ? 'margin-right' : 'margin-left';
        var marginLeft = parseInt($(obj).find('.nav.nav-tabs').css(targetCss));  
        var containerWidth = $(obj).width();
        var totalWidth = 0;
        var finalScrollWidth = 0;

        $(obj).find('li').each(function() {
            if (!$(this).hasClass('next-button') && !$(this).hasClass('prev-button')) {
                totalWidth += $(this).width();
            }
        });
    
        switch (direction) {
            case 'next':
                var widthLeft = totalWidth + marginLeft - containerWidth;
                if (widthLeft <= containerWidth) {
                    finalScrollWidth = widthLeft - marginLeft;
                    setTimeout(function() {
                        $(obj).removeClass('overflow-right');
                    }, 150);
                } else {
                    finalScrollWidth = containerWidth - marginLeft - 80;
                }

                if (finalScrollWidth !== 0) {
                	if (!$('body').hasClass('rtl-mode')) {
						$(obj).find('.nav.nav-tabs').animate({ marginLeft: '-' + finalScrollWidth + 'px'}, 150, function() {
							$(obj).addClass('overflow-left');
						});
					} else {
						$(obj).find('.nav.nav-tabs').animate({ marginRight: '-' + finalScrollWidth + 'px'}, 150, function() {
							$(obj).addClass('overflow-left');
						});
					}
                }
                break;
            case 'prev':
                var widthLeft = -marginLeft;
            
                if (widthLeft <= containerWidth) {
                    $(obj).removeClass('overflow-left');
                    finalScrollWidth = 0;
                } else {
                    finalScrollWidth = widthLeft - containerWidth + 80;
                }
                if (!$('body').hasClass('rtl-mode')) {
					$(obj).find('.nav.nav-tabs').animate({ marginLeft: '-' + finalScrollWidth + 'px'}, 150, function() {
						$(obj).addClass('overflow-right');
					});
				} else {
					$(obj).find('.nav.nav-tabs').animate({ marginRight: '-' + finalScrollWidth + 'px'}, 150, function() {
						$(obj).addClass('overflow-right');
					});
				}
                break;
        }
    }

    // handle page load active tab focus
    function handlePageLoadTabFocus() {
        $('.tab-overflow').each(function() {
            handleTabOverflowScrollWidth(this, 0);
        });
    }
    
    // handle tab next button click action
    $('[data-click="next-tab"]').click(function(e) {
        e.preventDefault();
        handleTabButtonAction(this,'next');
    });
    
    // handle tab prev button click action
    $('[data-click="prev-tab"]').click(function(e) {
        e.preventDefault();
        handleTabButtonAction(this,'prev');

    });
    
    // handle unlimited tabs responsive setting
    $(window).resize(function() {
        $('.tab-overflow .nav.nav-tabs').removeAttr('style');
        handlePageLoadTabFocus();
    });
    
    handlePageLoadTabFocus();
};


/* 18. Handle Top Menu - Unlimited Top Menu Render - added in V1.9
------------------------------------------------ */
var handleUnlimitedTopMenuRender = function() {
    "use strict";
    // function handle menu button action - next / prev
    function handleMenuButtonAction(element, direction) {
        var obj = $(element).closest('.nav');
        var targetCss = ($('body').hasClass('rtl-mode')) ? 'margin-right' : 'margin-left';
        var marginLeft = parseInt($(obj).css(targetCss));  
        var containerWidth = $('.top-menu').width() - 88;
        var totalWidth = 0;
        var finalScrollWidth = 0;

        $(obj).find('li').each(function() {
            if (!$(this).hasClass('menu-control')) {
                totalWidth += $(this).width();
            }
        });
        
        switch (direction) {
            case 'next':
                var widthLeft = totalWidth + marginLeft - containerWidth;
                if (widthLeft <= containerWidth) {
                    finalScrollWidth = widthLeft - marginLeft + 128;
                    setTimeout(function() {
                        $(obj).find('.menu-control.menu-control-right').removeClass('show');
                    }, 150);
                } else {
                    finalScrollWidth = containerWidth - marginLeft - 128;
                }

                if (finalScrollWidth !== 0) {
            		if (!$('body').hasClass('rtl-mode')) { 
						$(obj).animate({ marginLeft: '-' + finalScrollWidth + 'px'}, 150, function() {
							$(obj).find('.menu-control.menu-control-left').addClass('show');
						});
					} else {
						$(obj).animate({ marginRight: '-' + finalScrollWidth + 'px'}, 150, function() {
							$(obj).find('.menu-control.menu-control-left').addClass('show');
						});
					}
                }
                break;
            case 'prev':
                var widthLeft = -marginLeft;
    
                if (widthLeft <= containerWidth) {
                    $(obj).find('.menu-control.menu-control-left').removeClass('show');
                    finalScrollWidth = 0;
                } else {
                    finalScrollWidth = widthLeft - containerWidth + 88;
                }
            	if (!$('body').hasClass('rtl-mode')) { 
					$(obj).animate({ marginLeft: '-' + finalScrollWidth + 'px'}, 150, function() {
						$(obj).find('.menu-control.menu-control-right').addClass('show');
					});
				} else {
					$(obj).animate({ marginRight: '-' + finalScrollWidth + 'px'}, 150, function() {
						$(obj).find('.menu-control.menu-control-right').addClass('show');
					});
				}
                break;
        }
    }

    // handle page load active menu focus
    function handlePageLoadMenuFocus() {
        var targetMenu = $('.top-menu .nav');
        var targetList = $('.top-menu .nav > li');
        var targetActiveList = $('.top-menu .nav > li.active');
        var targetContainer = $('.top-menu');
        var targetCss = ($('body').hasClass('rtl-mode')) ? 'margin-right' : 'margin-left';
        var marginLeft = parseInt($(targetMenu).css(targetCss));  
        var viewWidth = $(targetContainer).width() - 128;
        var prevWidth = $('.top-menu .nav > li.active').width();
        var speed = 0;
        var fullWidth = 0;
        
        $(targetActiveList).prevAll().each(function() {
            prevWidth += $(this).width();
        });

        $(targetList).each(function() {
            if (!$(this).hasClass('menu-control')) {
                fullWidth += $(this).width();
            }
        });

        if (prevWidth >= viewWidth) {
            var finalScrollWidth = prevWidth - viewWidth + 128;
            if (!$('body').hasClass('rtl-mode')) { 
            	$(targetMenu).animate({ marginLeft: '-' + finalScrollWidth + 'px'}, speed);
            } else {
            	$(targetMenu).animate({ marginRight: '-' + finalScrollWidth + 'px'}, speed);
            }
        }
        
        if (prevWidth != fullWidth && fullWidth >= viewWidth) {
            $(targetMenu).find('.menu-control.menu-control-right').addClass('show');
        } else {
            $(targetMenu).find('.menu-control.menu-control-right').removeClass('show');
        }

        if (prevWidth >= viewWidth && fullWidth >= viewWidth) {
            $(targetMenu).find('.menu-control.menu-control-left').addClass('show');
        } else {
            $(targetMenu).find('.menu-control.menu-control-left').removeClass('show');
        }
    }

    // handle menu next button click action
    $('[data-click="next-menu"]').click(function(e) {
        e.preventDefault();
        handleMenuButtonAction(this,'next');
    });

    // handle menu prev button click action
    $('[data-click="prev-menu"]').click(function(e) {
        e.preventDefault();
        handleMenuButtonAction(this,'prev');

    });

    // handle unlimited menu responsive setting
    $(window).resize(function() {
        $('.top-menu .nav').removeAttr('style');
        handlePageLoadMenuFocus();
    });

    handlePageLoadMenuFocus();
};


/* 19. Handle Top Menu - Sub Menu Toggle - added in V1.9
------------------------------------------------ */
var handleTopMenuSubMenu = function() {
    "use strict";
    $('.top-menu .sub-menu .has-sub > a').click(function() {
        var target = $(this).closest('li').find('.sub-menu').first();
        var otherMenu = $(this).closest('ul').find('.sub-menu').not(target);
        $(otherMenu).not(target).slideUp(250, function() {
            $(this).closest('li').removeClass('expand');
        });
        $(target).slideToggle(250, function() {
            var targetLi = $(this).closest('li');
            if ($(targetLi).hasClass('expand')) {
                $(targetLi).removeClass('expand');
            } else {
                $(targetLi).addClass('expand');
            }
        });
    });
};


/* 20. Handle Top Menu - Mobile Sub Menu Toggle - added in V1.9
------------------------------------------------ */
var handleMobileTopMenuSubMenu = function() {
    "use strict";
    $('.top-menu .nav > li.has-sub > a').click(function() {
        if ($(window).width() <= 767) {
            var target = $(this).closest('li').find('.sub-menu').first();
            var otherMenu = $(this).closest('ul').find('.sub-menu').not(target);
            $(otherMenu).not(target).slideUp(250, function() {
                $(this).closest('li').removeClass('expand');
            });
            $(target).slideToggle(250, function() {
                var targetLi = $(this).closest('li');
                if ($(targetLi).hasClass('expand')) {
                    $(targetLi).removeClass('expand');
                } else {
                    $(targetLi).addClass('expand');
                }
            });
        }
    });
};


/* 21. Handle Top Menu - Mobile Top Menu Toggle - added in V1.9
------------------------------------------------ */
var handleTopMenuMobileToggle = function() {
    "use strict";
    $('[data-click="top-menu-toggled"]').click(function() {
        $('.top-menu').slideToggle(250);
    });
};


/* 22. Handle Clear Sidebar Selection & Hide Mobile Menu - added in V1.9
------------------------------------------------ */
var handleClearSidebarSelection = function() {
    $('.sidebar .nav > li, .sidebar .nav .sub-menu').removeClass('expand').removeAttr('style');
};
var handleClearSidebarMobileSelection = function() {
    $('#page-container').removeClass('page-sidebar-toggled');
};


/* 23. Handle Check Bootstrap Version - added in V4.0
------------------------------------------------ */
var handleCheckBootstrapVersion = function() {
	return parseInt($.fn.tooltip.Constructor.VERSION);
};


/* 24. Handle Page Scroll Class - added in V4.0
------------------------------------------------ */
var handleCheckScrollClass = function() {
	if ($(window).scrollTop() > 0) {
		$('#page-container').addClass('has-scroll');
	} else {
		$('#page-container').removeClass('has-scroll');
	}
};
var handlePageScrollClass = function() {
	$(window).on('scroll', function() {
		handleCheckScrollClass();
	});
	handleCheckScrollClass();
};


/* 25. Handle Toggle Navbar Profile - added in V4.0
------------------------------------------------ */
var handleToggleNavProfile = function() {
    var expandTime = ($('.sidebar').attr('data-disable-slide-animation')) ? 0 : 250;
    
    $('[data-toggle="nav-profile"]').click(function(e) {
        e.preventDefault();
        
        var targetLi = $(this).closest('li');
        var targetProfile = $('.sidebar .nav.nav-profile');
        var targetClass = 'active';
        var targetExpandingClass = 'expanding';
        var targetExpandClass = 'expand';
        var targetClosingClass = 'closing';
        var targetClosedClass = 'closed';
        
        if ($(targetProfile).is(':visible')) {
            $(targetLi).removeClass(targetClass);
            $(targetProfile).removeClass(targetClosingClass);
        } else {
            $(targetLi).addClass(targetClass);
            $(targetProfile).addClass(targetExpandingClass);
        }
        $(targetProfile).slideToggle(expandTime, function() {
        	if (!$(targetProfile).is(':visible')) {
				$(targetProfile).addClass(targetClosedClass);
				$(targetProfile).removeClass(targetExpandClass);
			} else {
				$(targetProfile).addClass(targetExpandClass);
				$(targetProfile).removeClass(targetClosedClass);
			}
			$(targetProfile).removeClass(targetExpandingClass + ' ' + targetClosingClass);
        });
    });
};


/* 26. Handle Sidebar Scroll Memory - added in V4.0
------------------------------------------------ */
var handleSidebarScrollMemory = function() {
	if (!(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent))) {
		$('.sidebar [data-scrollbar="true"]').slimScroll().bind('slimscrolling', function(e, pos) {
			localStorage.setItem('sidebarScrollPosition', pos + 'px');
		});
	
		var defaultScroll = localStorage.getItem('sidebarScrollPosition');
		if (defaultScroll) {
			$('.sidebar [data-scrollbar="true"]').slimScroll({ scrollTo: defaultScroll });
		}
	}
};


/* 27. Handle Sidebar Minify Sub Menu - added in V4.0
------------------------------------------------ */
var floatSubMenuTimeout;
var targetFloatMenu;
var handleMouseoverFloatSubMenu = function(elm) {
	clearTimeout(floatSubMenuTimeout);
};
var handleMouseoutFloatSubMenu = function(elm) {
	floatSubMenuTimeout = setTimeout(function() {
		$('#float-sub-menu').remove();
	}, 150);
};
var handleSidebarMinifyFloatMenu = function() {
	$(document).on('click', '#float-sub-menu li.has-sub > a', function(e) {
		var target = $(this).next('.sub-menu');
		var targetLi = $(target).closest('li');
		var close = false;
		var expand = false;
		if ($(target).is(':visible')) {
			$(targetLi).addClass('closing');
			close = true;
		} else {
			$(targetLi).addClass('expanding');
			expand = true;
		}
		$(target).slideToggle({
			duration: 250,
			progress: function() {
				var targetMenu = $('#float-sub-menu');
				var targetHeight = $(targetMenu).height();
				var targetOffset = $(targetMenu).offset();
				var targetOriTop = $(targetMenu).attr('data-offset-top');
				var targetMenuTop = $(targetMenu).attr('data-menu-offset-top');
				var targetTop 	 = targetOffset.top - $(window).scrollTop();
				var windowHeight = $(window).height();
				if (close) {
					if (targetTop > targetOriTop) {
						targetTop = (targetTop > targetOriTop) ? targetOriTop : targetTop;
						$('#float-sub-menu').css({ 'top': targetTop + 'px', 'bottom': 'auto' });
						$('#float-sub-menu-arrow').css({ 'top': '20px', 'bottom': 'auto' });
						$('#float-sub-menu-line').css({ 'top': '20px', 'bottom': 'auto' });
					}
				}
				if (expand) {
					if ((windowHeight - targetTop) < targetHeight) {
						var arrowBottom = (windowHeight - targetMenuTop) - 22;
						$('#float-sub-menu').css({ 'top': 'auto', 'bottom': 0 });
						$('#float-sub-menu-arrow').css({ 'top': 'auto', 'bottom': arrowBottom + 'px' });
						$('#float-sub-menu-line').css({ 'top': '20px', 'bottom': arrowBottom + 'px' });
					}
				}
			},
			complete: function() {
				if ($(target).is(':visible')) {
					$(targetLi).addClass('expand');
				} else {
					$(targetLi).addClass('closed');
				}
				$(targetLi).removeClass('closing expanding');
			}
		});
	});
	$('.sidebar .nav > li.has-sub > a').hover(function() {
		if ($('#page-container').hasClass('page-sidebar-minified')) {
			clearTimeout(floatSubMenuTimeout);

			var targetMenu = $(this).closest('li').find('.sub-menu').first();
			if (targetFloatMenu == this && $('#float-sub-menu').length !== 0) {
				return;
			} else {
				targetFloatMenu = this;
			}
			var targetMenuHtml = $(targetMenu).html();
			if (targetMenuHtml) {
				var sidebarOffset = $('#sidebar').offset();
				var sidebarX = (!$('#page-container').hasClass('page-with-right-sidebar')) ? (sidebarOffset.left + 60) : ($(window).width() - sidebarOffset.left);
				var targetHeight = $(targetMenu).height();
				var targetOffset = $(this).offset();
				var targetTop = targetOffset.top - $(window).scrollTop();
				var targetLeft = (!$('#page-container').hasClass('page-with-right-sidebar')) ? sidebarX : 'auto';
				var targetRight = (!$('#page-container').hasClass('page-with-right-sidebar')) ? 'auto' : sidebarX;
				var windowHeight = $(window).height();
	
				if ($('#float-sub-menu').length === 0) {
					targetMenuHtml = ''+
					'<div class="float-sub-menu-container" id="float-sub-menu" data-offset-top="'+ targetTop +'" data-menu-offset-top="'+ targetTop +'" onmouseover="handleMouseoverFloatSubMenu(this)" onmouseout="handleMouseoutFloatSubMenu(this)">'+
					'	<div class="float-sub-menu-arrow" id="float-sub-menu-arrow"></div>'+
					'	<div class="float-sub-menu-line" id="float-sub-menu-line"></div>'+
					'	<ul class="float-sub-menu">'+ targetMenuHtml + '</ul>'+
					'</div>';
					$('#page-container').append(targetMenuHtml);
				} else {
					$('#float-sub-menu').attr('data-offset-top', targetTop);
					$('#float-sub-menu').attr('data-menu-offset-top', targetTop);
					$('.float-sub-menu').html(targetMenuHtml);
				}
				
				if ((windowHeight - targetTop) > targetHeight) {
					$('#float-sub-menu').css({
						'top': targetTop,
						'left': targetLeft,
						'bottom': 'auto',
						'right': targetRight
					});
					$('#float-sub-menu-arrow').css({ 'top': '20px', 'bottom': 'auto' });
					$('#float-sub-menu-line').css({ 'top': '20px', 'bottom': 'auto' });
				} else {
					$('#float-sub-menu').css({
						'bottom': 0,
						'top': 'auto',
						'left': targetLeft,
						'right': targetRight
					});
					var arrowBottom = (windowHeight - targetTop) - 21;
					$('#float-sub-menu-arrow').css({ 'top': 'auto', 'bottom': arrowBottom + 'px' });
					$('#float-sub-menu-line').css({ 'top': '20px', 'bottom': arrowBottom + 'px' });
				}
			} else {
				$('#float-sub-menu').remove();
				targetFloatMenu = '';
			}
		}
	}, function() {
		if ($('#page-container').hasClass('page-sidebar-minified')) {
			floatSubMenuTimeout = setTimeout(function() {
				$('#float-sub-menu').remove();
				targetFloatMenu = '';
			}, 250);
		}
	});
};


/* 28. Handle Ajax Mode - added in V4.0
------------------------------------------------ */
var CLEAR_OPTION = '';
var handleAjaxMode = function(setting) {
	var emptyHtml = (setting.emptyHtml) ?  setting.emptyHtml : '<div class="p-t-40 p-b-40 text-center f-s-20 content"><i class="fa fa-warning fa-lg text-muted m-r-5"></i> <span class="f-w-600 text-inverse">Error 404! Page not found.</span></div>';
	var defaultUrl = (setting.ajaxDefaultUrl) ? setting.ajaxDefaultUrl : '';
		defaultUrl = (window.location.hash) ? window.location.hash : defaultUrl;
	
    if (defaultUrl === '') {
        $('#content').html(emptyHtml);
    } else {
        renderAjax(defaultUrl, '', true);
    }
    
	function clearElement() {
		$('.jvectormap-label, .jvector-label, .AutoFill_border ,#gritter-notice-wrapper, .ui-autocomplete, .colorpicker, .FixedHeader_Header, .FixedHeader_Cloned .lightboxOverlay, .lightbox, .introjs-hints, .nvtooltip, #float-sub-menu').remove();
		if ($.fn.DataTable) {
			$('.dataTable').DataTable().destroy();
		}
		if ($('#page-container').hasClass('page-sidebar-toggled')) {
			$('#page-container').removeClass('page-sidebar-toggled');
		}
	}
	
	function checkSidebarActive(url) {
		var targetElm = '#sidebar [data-toggle="ajax"][href="'+ url +'"]';
		if ($(targetElm).length !== 0) {
			$('#sidebar li').removeClass('active');
			$(targetElm).closest('li').addClass('active');
			$(targetElm).parents().addClass('active');
		}
	}
	
	function checkPushState(url) {
    	var targetUrl = url.replace('#','');
		var targetUserAgent = window.navigator.userAgent;
		var isIE = targetUserAgent.indexOf('MSIE ');
	
		if (isIE && (isIE > 0 && isIE < 9)) {
			window.location.href = targetUrl;
		} else {
			history.pushState('', '', '#' + targetUrl);
		}
	}
	
	function checkClearOption() {
		if (CLEAR_OPTION) {
			App.clearPageOption(CLEAR_OPTION);
			CLEAR_OPTION = '';
		}
	}
	
	function checkLoading(load) {
		if (!load) {
			if ($('#page-content-loader').length === 0) {
				$('body').addClass('page-content-loading');
				$('#content').append('<div id="page-content-loader"><span class="spinner"></span></div>');
			}
		} else {
			$('#page-content-loader').remove();
			$('body').removeClass('page-content-loading');
		}
	}
	
	function renderAjax(url, elm, disablePushState) {
		Pace.restart();
		
		checkLoading(false);
		clearElement();
		checkSidebarActive(url);
		checkClearOption();
		if (!disablePushState) {
			checkPushState(url);
		}
    
		var targetContainer= '#content';
		var targetUrl 	   = url.replace('#','');
		var targetType 	   = (setting.ajaxType) ? setting.ajaxType : 'GET';
		var targetDataType = (setting.ajaxDataType) ? setting.ajaxDataType : 'html';
		if (elm) {
			targetDataType = ($(elm).attr('data-type')) ? $(elm).attr('data-type') : targetDataType;
			targetDataDataType = ($(elm).attr('data-data-type')) ? $(elm).attr('data-data-type') : targetDataType;
		}
		
		$.ajax({
			url: targetUrl,
			type: targetType,
			dataType: targetDataType,
			success: function(data) {
				$(targetContainer).html(data);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$(targetContainer).html(emptyHtml);
			}
		}).done(function() {
			checkLoading(true);
			$('html, body').animate({ scrollTop: 0 }, 0);
			App.initComponent();
		});
	}
	
	$(window).on('hashchange', function() {
		if (window.location.hash) {
			renderAjax(window.location.hash, '', true);
		}
	});
	
	$(document).on('click', '[data-toggle="ajax"]', function(e) {
		e.preventDefault();
		renderAjax($(this).attr('href'), this);
	});
};
var handleSetPageOption = function(option) {
	if (option.pageContentFullHeight) {
		$('#page-container').addClass('page-content-full-height');
	}
	if (option.pageSidebarLight) {
		$('#page-container').addClass('page-with-light-sidebar');
	}
	if (option.pageSidebarRight) {
		$('#page-container').addClass('page-with-right-sidebar');
	}
	if (option.pageSidebarWide) {
		$('#page-container').addClass('page-with-wide-sidebar');
	}
	if (option.pageSidebarMinified) {
		$('#page-container').addClass('page-sidebar-minified');
	}
	if (option.pageSidebarTransparent) {
		$('#sidebar').addClass('sidebar-transparent');
	}
	if (option.pageContentFullWidth) {
		$('#content').addClass('content-full-width');
	}
	if (option.pageContentInverseMode) {
		$('#content').addClass('content-inverse-mode');
	}
	if (option.pageBoxedLayout) {
		$('body').addClass('boxed-layout');
	}
	if (option.clearOptionOnLeave) {
		CLEAR_OPTION = option;
	}
};
var handleClearPageOption = function(option) {
	if (option.pageContentFullHeight) {
		$('#page-container').removeClass('page-content-full-height');
	}
	if (option.pageSidebarLight) {
		$('#page-container').removeClass('page-with-light-sidebar');
	}
	if (option.pageSidebarRight) {
		$('#page-container').removeClass('page-with-right-sidebar');
	}
	if (option.pageSidebarWide) {
		$('#page-container').removeClass('page-with-wide-sidebar');
	}
	if (option.pageSidebarMinified) {
		$('#page-container').removeClass('page-sidebar-minified');
	}
	if (option.pageSidebarTransparent) {
		$('#sidebar').removeClass('sidebar-transparent');
	}
	if (option.pageContentFullWidth) {
		$('#content').removeClass('content-full-width');
	}
	if (option.pageContentInverseMode) {
		$('#content').removeClass('content-inverse-mode');
	}
	if (option.pageBoxedLayout) {
		$('body').removeClass('boxed-layout');
	}
};


/* 29. Handle Float Navbar Search - added in V4.0
------------------------------------------------ */
var handleToggleNavbarSearch = function() {
    $('[data-toggle="navbar-search"]').click(function(e) {
        e.preventDefault();
        $('.header').addClass('header-search-toggled');
    });
    
    $('[data-dismiss="navbar-search"]').click(function(e) {
        e.preventDefault();
        $('.header').removeClass('header-search-toggled');
    });
};


/* Application Controller
------------------------------------------------ */
var App = function () {
	"use strict";
	
	var setting;
	
	return {
		//main function
		init: function (option) {
			if (option) {
				setting = option;
			}
		    this.initLocalStorage();
		    this.initSidebar();
		    this.initTopMenu();
		    this.initComponent();
		    this.initThemePanel();
		    this.initPageLoad();
		    $(window).trigger('load');
		    
		    if (setting && setting.ajaxMode) {
		    	this.initAjax();
		    }
		},
		initSidebar: function() {
			handleSidebarMenu();
			handleMobileSidebarToggle();
			handleSidebarMinify();
			handleSidebarMinifyFloatMenu();
			handleToggleNavProfile();
			handleToggleNavbarSearch();
			
			if (!setting || (setting && !setting.disableSidebarScrollMemory)) {
				handleSidebarScrollMemory();
			}
		},
		initSidebarSelection: function() {
		    handleClearSidebarSelection();
		},
		initSidebarMobileSelection: function() {
		    handleClearSidebarMobileSelection();
		},
		initTopMenu: function() {
			handleUnlimitedTopMenuRender();
			handleTopMenuSubMenu();
			handleMobileTopMenuSubMenu();
			handleTopMenuMobileToggle();
		},
		initPageLoad: function() {
			handlePageContentView();
		},
		initComponent: function() {
			if (!setting || (setting && !setting.disableDraggablePanel)) {
				handleDraggablePanel();
			}
			handleIEFullHeightContent();
			handleSlimScroll();
			handleUnlimitedTabsRender();
			handlePanelAction();
			handelTooltipPopoverActivation();
			handleScrollToTopButton();
			handleAfterPageLoadAddClass();
			handlePageScrollClass();
		},
		initLocalStorage: function() {
		    handleLocalStorage();
		},
		initThemePanel: function() {
			handleThemePageStructureControl();
			handleThemePanelExpand();
		    handleResetLocalStorage();
		},
		initAjax: function() {
			handleAjaxMode(setting);
			$.ajaxSetup({
                cache: true
            });
		},
		setPageTitle: function(pageTitle) {
		    document.title = pageTitle;
		},
		setPageOption: function(option) {
			handleSetPageOption(option);
		},
		clearPageOption: function(option) {
			handleClearPageOption(option);
		},
		restartGlobalFunction: function() {
		    this.initLocalStorage();
		    this.initTopMenu();
		    this.initComponent();
		},
		scrollTop: function() {
            $('html, body').animate({
                scrollTop: $('body').offset().top
            }, 0);
		}
  };
}();