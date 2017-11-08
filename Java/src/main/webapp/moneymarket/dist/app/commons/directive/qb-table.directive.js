!function(){"use strict";angular.module("directives").directive("qbTable",function(){function e(e,t,i,o,l,r,p,n,a,s,c,d,m,u){e.window=c,e.location=d;var g=this;g.state="QB精选",g.isSort=!1,g.bury="Qbchosen.Sequence";var y=new Date;y.setHours(0),y.setMinutes(0),y.setSeconds(0),y.setMilliseconds(0),g.today=y.getTime(),g.enumConfig=l,g.showToTop=!1,g.hasNewData=!1,e.$watch("vm.list",function(e,t){e&&(g.gridOptions.data=e)},!0),g.getDataDown=function(){var e=t.defer();return g.loadData({callback:function(){g.gridApi.infiniteScroll.saveScrollPercentage(),g.gridApi.infiniteScroll.dataLoaded(!0,!0).then(function(){e.resolve()})}}),e.promise},g.scrollingTop=function(){g.hasNewData=!1,g.showToTop=!1;var e=t.defer();return g.gridApi.infiniteScroll.saveScrollPercentage(),g.gridApi.infiniteScroll.dataLoaded(!0,!0).then(function(){e.resolve()}),e.promise},g.getArray=function(e){if(e)return e=e.split(/,|;/),e.findWhere(function(e){return e})},g.clickCopyQuoteId=function(e){e&&(document.oncopy=function(t){t.clipboardData.setData("text/plain",e.quoteId),t.preventDefault()},document.execCommand("copy"))},g.openQM=function(e){quote&&quote.quoteUserId&&n.openQM(quote.quoteUserId)},g.clickOrg=function(e){e&&e.quoteUserId&&n.openQM(e.quoteUserId)},e.$watch("vm.gridApi.grid.isScrollingVertically",function(e){e&&(g.showToTop=!0)}),e.$on("hasNewData",function(){jQuery(".ui-grid-viewport").scrollTop()<100||(g.isSort?(g.showToTop=!0,g.hasNewData=!0):g.showToTop&&(g.hasNewData=!0))}),g.scrollToTop=function(){g.gridApi.core.scrollTo(g.gridOptions.data[0]),g.showToTop=!1,g.hasNewData=!1},g.quoteOnmouseOver=function(e,t,i){i&&i.quoteDetails&&i.quoteDetails[t].priceDetails&&(g.periodTooltip=i.quoteDetails[t].priceDetails)},g.isToday=function(e){if(e)return parseInt(e)>g.today},g.sortPeriod=function(t){if(t&&"string"==typeof t)if("T"===t.charAt(0)){var i;g.period===t&&"DESC"===g.order?i="ASC":g.period!==t?i="DESC":g.period===t&&"ASC"===g.order&&(i="DESC"),m.setOrderByPeriod(t),m.setOrderSeq(i),e.$emit("filterChanged"),g.period=t,g.order=i,g.isSort=!0}else"new"===t?(m.setOrderByPeriod(null),m.setOrderSeq(""),e.$emit("filterChanged"),g.period="",g.order="",g.isSort=!1,g.gridApi.infiniteScroll.dataRemovedTop(),g.hasNewData=!1,g.showToTop=!1,jQuery(".ui-grid-cell-contents i.ui-grid-icon-down-dir").removeClass("ui-grid-icon-down-dir").addClass("ui-grid-icon-blank"),jQuery(".lastUpdate .ui-grid-cell-contents i.ui-grid-icon-blank").addClass("ui-grid-icon-down-dir").removeClass("ui-grid-icon-blank"),jQuery(".lastUpdate .ui-grid-cell-contents span.ui-grid-invisible").removeClass("ui-grid-invisible")):g.isSort=!0},g.gridOptions={enableHorizontalScrollbar:r.scrollbars.ALWAYS,rowHeight:30,infiniteScrollRowsFromEnd:20,infiniteScrollDown:!0,infiniteScrollUp:!0,onRegisterApi:function(t){t.infiniteScroll.on.needLoadMoreData(e,g.getDataDown),t.infiniteScroll.on.needLoadMoreDataTop(e,g.scrollingTop),g.gridApi=t,g.reload=!1,e.window.onresize=function(){var t=e.window.innerHeight;2===t?g.reload=!0:g.reload&&g.gridApi.grid.refresh();var i=setInterval(function(){var t=e.window.innerHeight;if(t>100){var o=jQuery(".operation-line").offset().top,l=jQuery(".operation-line").height(),r=t-l-o;jQuery(".ui-grid").css("top",jQuery(".operation-line").offset().top+jQuery(".operation-line").height()+"px"),jQuery(".ui-grid").css("bottom","-7px"),jQuery(".ui-grid").css("right","0px"),jQuery(".ui-grid").css("left","0px"),jQuery(".ui-grid-viewport").height(r-38),jQuery(".ui-grid-viewport").scrollTop(jQuery(".ui-grid-viewport").scrollTop()+1),jQuery(".ui-grid-viewport").scrollLeft(jQuery(".ui-grid-viewport").scrollLeft()+1),clearInterval(i)}},10)}},rowTemplate:'<div ng-repeat="col in colContainer.renderedColumns track by col.colDef.displayName" ng-class="{warning:row.entity.isNew, inactive:row.entity.active==0}" class="ui-grid-cell" ui-grid-cell></div>',columnDefs:[{name:"机构",displayName:"　机构",width:"10%",minWidth:220,headerCellTemplate:u.get("header-cell-template.html"),field:"primeListInstitutionName",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div class="ui-grid-cell-contents pointer ui-grid-cell-contents-firstcol">                                            <div burying="Qbchosen.ClickInstitutions,{{row.entity.quoteId}}" class="name" uib-tooltip="{{row.entity.primeListInstitutionName}}" tooltip-append-to-body="true" tooltip-animation="false" tooltip-placement="top-left auto">{{row.entity.primeListInstitutionName}}</div>                                       </div>'},{name:"联系人",displayName:"联系人",width:"5%",minWidth:100,headerCellTemplate:u.get("header-cell-template.html"),field:"primeListContacts",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents cell-user-list" uib-dropdown ng-class="{show:status.isopen}" is-open="status.isopen" ng-if="row.entity.primeListContacts[0].name" uib-tooltip="{{row.entity.primeListContacts[0].name}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                                            <span>{{row.entity.primeListContacts[0].name}}</span>                                    </div>'},{name:"联系方式",displayName:"联系方式",width:"5%",minWidth:100,field:"primeListInstitutionTelephone",enableSorting:!1,cellTemplate:'<div uib-dropdown is-open="status.isopen" ng-class="{show:status.isopen}" class="ui-grid-cell-contents cell-contact">                                            <img burying="Qbchosen.ContactsQM,{{row.entity.quoteId}}" src="app/commons/img/qmlogo.png" class="qm-logo pointer active" ng-class="{active : row.entity.primeListContacts[0].status}" ng-click="grid.appScope.vm.clickOrg(row.entity)">                                            <span burying="Qbchosen.ClickTelephone,{{row.entity.quoteId}}" uib-dropdown-toggle ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)"><i class="glyphicon glyphicon-earphone"></i></span>                                            <ul class="dropdown-menu dropdown-menu-left" ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)" uib-dropdown-menu>                                                <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>                                                <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>                                            </ul>                                            <span uib-dropdown-toggle class="no-pointer" ng-if="(row.entity.quoteSource==\'PRIME_QB\' && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile)) || (row.entity.quoteSource!==\'PRIME_QB\'&&!grid.appScope.vm.getArray(row.entity.marketListQuoterContacts))"><i class="glyphicon glyphicon-earphone inactive"></i></span>                                       </div>'},{name:"方向",displayName:"方向",width:"4%",maxWidth:80,minWidth:50,field:"direction",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                                            <div class="badge" ng-class="{\'IN\':\'warning\',\'OUT\':\'info\'}[row.entity.direction]">                                            {{row.entity.direction=="IN" ? "收" : "出"}}</div>                                        </div>'},{name:"类型",displayName:"类型",width:"6%",minWidth:70,field:"quote_type",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                                            {{grid.appScope.vm.enumConfig["quoteType"][row.entity.quoteType]}}                                        </div>'},{name:"T1D",displayName:"1D(T+0)",width:"5%",minWidth:90,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'},{name:"T1D",displayName:"1D",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'},{name:"T7D",displayName:"7D",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,1,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[1].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[1].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[1].multipeRecords && row.entity.quoteDetails[1].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[1].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[1].priceDisplayString}}</div>'},{name:"T14D",displayName:"14D",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,2,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[2].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[2].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[2].multipeRecords && row.entity.quoteDetails[2].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[2].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[2].priceDisplayString}}</div>'},{name:"T1M",displayName:"1M",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,3,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[3].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[3].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[3].multipeRecords && row.entity.quoteDetails[3].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[3].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[3].priceDisplayString}}</div>'},{name:"T2M",displayName:"2M",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,4,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[4].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[4].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[4].multipeRecords && row.entity.quoteDetails[4].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[4].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[4].priceDisplayString}}</div>'},{name:"T3M",displayName:"3M",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,5,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[5].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[5].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[5].multipeRecords && row.entity.quoteDetails[5].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[5].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[5].priceDisplayString}}</div>'},{name:"T6M",displayName:"6M",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,6,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[6].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[6].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[6].multipeRecords && row.entity.quoteDetails[6].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[6].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[6].priceDisplayString}}</div>'},{name:"T9M",displayName:"9M",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,7,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[7].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[7].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[7].multipeRecords && row.entity.quoteDetails[7].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[7].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[7].priceDisplayString}}</div>'},{name:"T1Y",displayName:"1Y",width:"5%",minWidth:60,headerCellTemplate:u.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,8,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[8].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[8].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[8].multipeRecords && row.entity.quoteDetails[8].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[8].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[8].priceDisplayString}}</div>'},{name:"备注",displayName:"备注",width:"*",minWidth:180,field:"memo",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                        <div class="text" uib-tooltip="{{row.entity.memo}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                            {{row.entity.memo}}                        </div></div>'},{name:"new",displayName:"最后更新",width:160,headerCellTemplate:u.get("header-cell-template.html"),headerCellClass:"lastUpdate",sortDirectionCycle:[r.DESC],sort:{direction:r.DESC,priority:1},cellTemplate:'<div ng-dblclick="grid.appScope.vm.clickCopyQuoteId(row.entity)" class="ui-grid-cell-contents"><span ng-if="grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: "HH:mm:ss"}}</span><span ng-if="!grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: " yyyy-MM-dd HH:mm:ss"}}</span></div>'}]},"offline"==g.type&&(g.gridOptions.columnDefs.splice(4,1),g.gridOptions.columnDefs.splice(4,1)),"inner"==g.type&&(g.gridOptions.columnDefs.splice(6,1),g.gridOptions.columnDefs.splice(6,1),g.gridOptions.columnDefs.splice(6,1));var v=setInterval(function(){var t=jQuery(".filter-view").height();t&&(e.window.onresize(),clearInterval(v))},10)}return e.$inject=["$scope","$q","$http","appConfig","enumConfig","uiGridConstants","contactService","qbService","offerService","sortService","$window","$location","tableService","$templateCache"],{restrict:"AE",template:'<div class="back-to-top" ng-click="vm.sortPeriod(\'new\')" ng-if="vm.showToTop && vm.hasNewData">出现新报价 点击查看</div>                        <div id="qb" ui-grid="vm.gridOptions" class="grid" ui-grid-infinite-scroll></div>',scope:{list:"=",page:"=",loadData:"&",type:"="},controller:e,bindToController:!0,controllerAs:"vm"}})}();