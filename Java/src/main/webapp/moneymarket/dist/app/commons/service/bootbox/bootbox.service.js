!function(){"use strict";function o(o){var t=this,n=function(t,n,e){e||(e="");o.open({animation:!1,templateUrl:"app/commons/service/bootbox/bootbox.html",size:"md "+e,backdrop:"static",controller:"bootboxController",bindToController:!0,controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(o){return o.load(["app/commons/service/bootbox/bootbox.controller.js"])}],type:function(){return"message"},bodyText:function(){return t},bodyHtml:function(){return n}}})},e=function(t,n){var e=o.open({animation:!1,templateUrl:"app/commons/service/bootbox/bootbox.html",size:"md",backdrop:"static",controller:"bootboxController",bindToController:!0,controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(o){return o.load(["app/commons/service/bootbox/bootbox.controller.js"])}],type:function(){return"confirm"},bodyText:function(){return t},bodyHtml:function(){}}});e.result.then(function(){"function"==typeof n&&n()})};this.messageHtml=function(t,n){o.open({animation:!1,templateUrl:"app/commons/service/bootbox/bootbox.html",size:"md",backdrop:"static",controller:"bootboxController",bindToController:!0,controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(o){return o.load(["app/commons/service/bootbox/bootbox.controller.js"])}],type:function(){return"message"},bodyText:function(){return t},bodyHtml:function(){return n}}})},t.message=n,t.confirm=e}angular.module("services").service("bootbox",o),o.$inject=["$uibModal"]}();