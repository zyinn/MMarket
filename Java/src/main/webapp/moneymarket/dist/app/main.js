!function(e){"use strict";e.module("directives",[]),e.module("services",[]),e.module("components",[]),e.module("moneyMarketApp",["ui.router","oc.lazyLoad","ui.bootstrap","directives","services","components","ui.grid","ui.grid.infiniteScroll","ngCookies","ngFileUpload"]);var n=e.module("moneyMarketApp").run(["userinfoService","$http","$templateCache",function(e,n,t){e.validate(),n.get("app/commons/table/header-cell-template.html").then(function(e){t.put("header-cell-template.html",e.data)},function(e){console.log("header-cell-template.html模板加载失败！")})}]).constant("enumConfig",{quoteType:{GTF:"保本",UR2:"非保本R2",UR3:"非保本R3",IBD:"同存"},period:{T7D:{min:1,max:7},T14D:{min:8,max:14},T1M:{min:15,max:30},T2M:{min:31,max:60},T3M:{min:61,max:90},T6M:{min:91,max:180},T1Y:{min:181,max:360}},periodValue:{},quoteMethod:{},bankNature:{SHIBOR:0,BIG_BANK:1,JOINT_STOCK:3,CITY_COMMERCIAL_BANK:4,RURAL_CREDIT:6,OTHERS:99},trustType:{UNLIMIT:0,LIMIT:1}});e.module("moneyMarketApp").service("refreshService",["$interval","$window",function(e,n){this.getCurrentTime=function(){t&&e.cancel(t);var t=e(function(){var e=(new Date).getHours();3===e&&n.location.reload(!0)},18e5)}}]),n.factory("userInterceptor",["$q","$injector","appConfig","qbService",function(e,n,t,r){var o=function(e){return!/(\.html|\.js|\.json|ui\-grid|validateUser|service)/.test(e)};return{request:function(r){if(o(r.url)){t.service_root&&(r.url=t.service_root+r.url);var i=n.get("userinfoService"),a=n.get("$state");i.getUserData(function(e){r.headers.username=e.UserAccount,r.headers.password=e.Password},function(){return a.go("login"),e.reject(r)})}return r.url===t.validate_user&&t.service_root&&(r.url=t.service_root+r.url),r},requestError:function(e){return e},response:function(r){if(o(r.config.url)){var i=r.data;if(r.config.url.endWith(t.get_brother_alliance))return r;if(0===i.return_code)return r;if(i.return_message){var a=n.get("bootbox");a.message(i.return_message.exceptionName+"\n"+i.return_message.exceptionMessage,i.return_message.exceptionStackTrace)}return e.reject(r)}return r},responseError:function(n){return e.reject(n)}}}]).config(["$httpProvider","$injector",function(e,n){e.interceptors.push("userInterceptor")}]),n.config(["$stateProvider","$urlRouterProvider","$locationProvider",function(e,n,t){e.state("app",{"abstract":!0,views:{menu:{templateUrl:"app/commons/menu/index.html",controller:["$scope","$http","offerService","qbService","refreshService",function(e,n,t,r,o){o.getCurrentTime();var i=void 0,a=void 0;e.allianceClick=function(){t.openReadOnlyModal(function(){},{institution_id:i,last_update:new Date})},e.allianceOpenQM=function(){r.openQM(a)}}],resolve:{}},body:{templateUrl:"app/main.html",controller:["$scope","tableService",function(e,n){e.$on("filterChanged",function(n,t){e.$broadcast("refreshList",t)}),e.$on("initFilter",function(n,t){e.$broadcast("filterReset",t)}),e.$on("institutionOffer",function(n,t){e.$broadcast("toTableInstitution",t)})}]}}}).state("login",{url:"/login",views:{login:{templateUrl:"app/login/login.html",controller:"loginController",controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(e){return e.load(["app/login/login.js"])}]}}}}).state("app.innerFinancing",{"abstract":!0,url:"/inner",views:{part1:{templateUrl:"app/inner-financing/main.html"}}}).state("app.innerFinancing.index",{url:"",views:{filter:{templateUrl:"app/inner-financing/filter/index.html",controller:"innerFilterController",controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(e){return e.load(["app/inner-financing/filter/filter.js"])}]}},table:{templateUrl:"app/commons/table/index.html",controller:"tableController",controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(e){return e.load(["app/commons/table/table.js","app/commons/table/table.service.js"])}],financingType:function(){return"inner"}}}}}).state("app.offlineFinancing",{url:"/offline","abstract":!0,views:{part1:{templateUrl:"app/inner-financing/main.html"}}}).state("app.offlineFinancing.index",{url:"",views:{filter:{templateUrl:"app/offline-financing/filter/index.html",controller:"offlineFilterController",controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(e){return e.load(["app/offline-financing/filter/filter.js"])}]}},table:{templateUrl:"app/commons/table/index.html",controller:"tableController",controllerAs:"vm",resolve:{load:["$ocLazyLoad",function(e){return e.load(["app/commons/table/table.js","app/commons/table/table.service.js"])}],financingType:function(){return"offline"}}}}})}])}(angular);