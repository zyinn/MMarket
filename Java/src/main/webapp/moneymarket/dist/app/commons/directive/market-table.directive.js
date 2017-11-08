!function(e){"use strict";e.module("directives").directive("marketTable",function(){var e=["$http","$scope","$q","$uibModal","enumConfig","uiGridConstants","appConfig","offerService","qbService","contactService","sortService","$timeout","$window","$location","tableService","$templateCache",function(e,t,o,i,l,r,n,p,a,s,c,d,u,m,g,y){t.window=u,t.location=m;var v=this;v.state="市场报价",v.isSort=!1,v.showToTop=!1,v.hasNewData=!1,v.bury="MarketQuoted.Sequence";var w=new Date;w.setHours(0),w.setMinutes(0),w.setSeconds(0),w.setMilliseconds(0),v.today=w.getTime(),v.enumConfig=l,t.$watch("vm.list",function(e,t){e&&(v.gridOptions.data=e)},!0),v.getDataDown=function(){var e=o.defer();return v.loadData({callback:function(){v.gridApi.infiniteScroll.saveScrollPercentage(),v.gridApi.infiniteScroll.dataLoaded(!0,!0).then(function(){e.resolve()})}}),e.promise},v.scrollingTop=function(){v.hasNewData=!1,v.showToTop=!1;var e=o.defer();return v.gridApi.infiniteScroll.saveScrollPercentage(),v.gridApi.infiniteScroll.dataLoaded(!0,!0).then(function(){e.resolve()}),e.promise},t.$watch("vm.gridApi.grid.isScrollingVertically",function(e){e&&(v.showToTop=!0)}),t.$on("hasNewData",function(){jQuery(".ui-grid-viewport").scrollTop()<100||(v.isSort?(v.showToTop=!0,v.hasNewData=!0):v.showToTop&&(v.hasNewData=!0))}),v.scrollToTop=function(){v.gridApi.core.scrollTo(v.gridOptions.data[0]),v.showToTop=!1,v.hasNewData=!1},v.getArray=function(e){if(e)return e=e.split(/,|;/),e.findWhere(function(e){return e})};v.clickCopyQuoteId=function(e){e&&(document.oncopy=function(t){t.clipboardData.setData("text/plain",e.quoteId),t.preventDefault()},document.execCommand("copy"))};var f;v.clickQQ=function(e,t){if(t&&t.quoteUserId){document.oncopy=function(e){e.clipboardData.setData("text/plain",t.quoteUserId),e.preventDefault()},document.execCommand("copy"),v.copyqqsuccess=!0;var o=document.getElementById("qqcopy");o.style.left=+e.clientX+190+"px",o.style.top=+e.clientY-70+"px",v.qqnumber=t.quoteUserId,f&&d.cancel(f),f=d(function(){v.copyqqsuccess=!1},1e3)}},v.clickOrg=function(e){e&&e.quoteUserId&&a.openQM(e.quoteUserId)},v.openContactModal=function(e){e&&s.openModal(e,function(e){t.$emit("institutionOffer",e)})},v.openQM=function(e){e&&e.quoteUserId&&a.openQM(e.quoteUserId)},v.getContactStatus=function(e){return!0},v.isToday=function(e){if(e)return parseInt(e)>v.today},v.quoteOnmouseOver=function(e,t,o){o&&o.quoteDetails&&o.quoteDetails[t].priceDetails&&(v.periodTooltip=o.quoteDetails[t].priceDetails)},v.sortPeriod=function(e){if(e&&"string"==typeof e)if("T"===e.charAt(0)){var o;v.period===e&&"DESC"===v.order?o="ASC":v.period!==e?o="DESC":v.period===e&&"ASC"===v.order&&(o="DESC"),g.setOrderByPeriod(e),g.setOrderSeq(o),t.$emit("filterChanged"),v.period=e,v.order=o,v.isSort=!0}else"new"===e?(g.setOrderByPeriod(null),g.setOrderSeq(""),t.$emit("filterChanged"),v.period="",v.order="",v.isSort=!1,v.gridApi.infiniteScroll.dataRemovedTop(),v.hasNewData=!1,v.showToTop=!1,jQuery(".ui-grid-cell-contents i.ui-grid-icon-down-dir").removeClass("ui-grid-icon-down-dir").addClass("ui-grid-icon-blank"),jQuery(".lastUpdate .ui-grid-cell-contents i.ui-grid-icon-blank").addClass("ui-grid-icon-down-dir").removeClass("ui-grid-icon-blank"),jQuery(".lastUpdate .ui-grid-cell-contents span.ui-grid-invisible").removeClass("ui-grid-invisible")):v.isSort=!0},v.gridOptions={enableHorizontalScrollbar:r.scrollbars.ALWAYS,rowHeight:30,infiniteScrollRowsFromEnd:20,infiniteScrollDown:!0,infiniteScrollUp:!0,onRegisterApi:function(e){e.infiniteScroll.on.needLoadMoreData(t,v.getDataDown),e.infiniteScroll.on.needLoadMoreDataTop(t,v.scrollingTop),v.gridApi=e,v.reload=!1,t.window.onresize=function(){var e=t.window.innerHeight;2===e?v.reload=!0:v.reload&&v.gridApi.grid.refresh();var o=setInterval(function(){var e=t.window.innerHeight;if(e>100){var i=jQuery(".operation-line").offset().top,l=jQuery(".operation-line").height(),r=e-l-i;jQuery(".ui-grid").css("top",jQuery(".operation-line").offset().top+jQuery(".operation-line").height()+"px"),jQuery(".ui-grid").css("bottom","-7px"),jQuery(".ui-grid").css("right","0px"),jQuery(".ui-grid").css("left","0px"),jQuery(".ui-grid-viewport").height(r-38),jQuery(".ui-grid-viewport").scrollTop(jQuery(".ui-grid-viewport").scrollTop()+1),jQuery(".ui-grid-viewport").scrollLeft(jQuery(".ui-grid-viewport").scrollLeft()+1),clearInterval(o)}},10)}},rowTemplate:'<div ng-repeat="col in colContainer.renderedColumns track by col.colDef.displayName" ng-class="{warning:row.entity.isNew, inactive:row.entity.active==0}" class="ui-grid-cell" ui-grid-cell></div>',columnDefs:[{name:"报价方",displayName:"　报价方",width:"13%",minWidth:240,headerCellTemplate:y.get("header-cell-template.html"),field:"marketListQuoter",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div class="ui-grid-cell-contents pointer ui-grid-cell-contents-firstcol">                                            <div class="name" ng-if="row.entity.quoteSource==\'PRIME_QB\'" >                                                <span burying="MarketQuoted.ClickBidder,{{row.entity.quoteId}}" uib-tooltip="{{row.entity.primeListInstitutionName}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">{{row.entity.primeListInstitutionName}}-{{row.entity.primeListContacts[0].name}}</span>                                                <img src="app/commons/img/primelogo.png"/>                                            </div>                                            <div class="name" ng-if="row.entity.quoteSource==\'QB\'">                                                <span burying="MarketQuoted.ClickBidder,{{row.entity.quoteId}}" uib-tooltip="{{row.entity.marketListQuoter}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                                                    {{row.entity.marketListQuoter}}                                            </div>                                            <div burying="MarketQuoted.ClickBidder,{{row.entity.quoteId}}" class="name QQname" ng-if="row.entity.quoteSource==\'QQ\'" uib-tooltip="{{row.entity.marketListQuoter}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                                                {{row.entity.marketListQuoter}}                                            </div>                                        </div>'},{name:"联系方式",displayName:"联系方式",width:"5%",minWidth:100,field:"contact",enableSorting:!1,cellTemplate:'<div uib-dropdown is-open="status.isopen" ng-class="{show:status.isopen}" class="ui-grid-cell-contents cell-contact" uib-tooltip="" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                                                <img burying="MarketQuoted.BidderQM,{{row.entity.quoteId}}" class="qm-logo active" ng-if="row.entity.quoteUserId && row.entity.quoteSource!==\'QQ\'" src="app/commons/img/qmlogo.png" ng-click="grid.appScope.vm.clickOrg(row.entity)">                                                <span burying="MarketQuoted.CopyQQ,{{row.entity.quoteId}}" class="name QQname" ng-if="row.entity.quoteSource==\'QQ\' && row.entity.quoteUserId" ng-click="grid.appScope.vm.clickQQ($event, row.entity)"  uib-tooltip="点击复制QQ号" tooltip-append-to-body="true" tooltip-animation="false" tooltip-placement="top-left auto">                                                    <img class="qq-logo pointer" src="app/commons/img/qqlogo.png"/>                                                </span>                                                <span burying="MarketQuoted.ClickTelephone,{{row.entity.quoteId}}" uib-dropdown-toggle ng-if="row.entity.quoteSource!==\'PRIME_QB\' && row.entity.marketListQuoterContacts"><i class="glyphicon glyphicon-earphone"></i></span>                                                <ul class="dropdown-menu dropdown-menu-left" ng-if="row.entity.quoteSource!==\'PRIME_QB\' && row.entity.marketListQuoterContacts" uib-dropdown-menu>                                                    <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.marketListQuoterContacts) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>                                                </ul>                                                <span burying="MarketQuoted.ClickTelephone,{{row.entity.quoteId}}" uib-dropdown-toggle ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)"><i class="glyphicon glyphicon-earphone"></i></span>                                                <ul class="dropdown-menu dropdown-menu-left" ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)" uib-dropdown-menu>                                                    <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>                                                    <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>                                                </ul>                                                <span uib-dropdown-toggle class="no-pointer" ng-if="(row.entity.quoteSource==\'PRIME_QB\' && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile)) || (row.entity.quoteSource!==\'PRIME_QB\'&&!grid.appScope.vm.getArray(row.entity.marketListQuoterContacts))"><i class="glyphicon glyphicon-earphone inactive"></i></span>                                            </div>'},{name:"方向",displayName:"方向",width:"4%",maxWidth:80,minWidth:50,field:"direction",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                                            <div class="badge" ng-class="{\'IN\':\'warning\',\'OUT\':\'info\'}[row.entity.direction]">                                            {{row.entity.direction=="IN" ? "收" : "出"}}</div>                                        </div>'},{name:"类型",displayName:"类型",width:"6%",minWidth:70,field:"quote_type",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                                            {{grid.appScope.vm.enumConfig["quoteType"][row.entity.quoteType]}}                                        </div>'},{name:"T1D",displayName:"1D(T+0)",width:"5%",minWidth:100,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'},{name:"T1D",displayName:"1D",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'},{name:"T7D",displayName:"7D",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,1,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[1].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[1].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[1].multipeRecords && row.entity.quoteDetails[1].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[1].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[1].priceDisplayString}}</div>'},{name:"T14D",displayName:"14D",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,2,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[2].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[2].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[2].multipeRecords && row.entity.quoteDetails[2].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[2].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[2].priceDisplayString}}</div>'},{name:"T1M",displayName:"1M",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,3,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[3].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[3].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[3].multipeRecords && row.entity.quoteDetails[3].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[3].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[3].priceDisplayString}}</div>'},{name:"T2M",displayName:"2M",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,4,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[4].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[4].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[4].multipeRecords && row.entity.quoteDetails[4].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[4].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[4].priceDisplayString}}</div>'},{name:"T3M",displayName:"3M",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,5,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[5].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[5].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[5].multipeRecords && row.entity.quoteDetails[5].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[5].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[5].priceDisplayString}}</div>'},{name:"T6M",displayName:"6M",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,6,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[6].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[6].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[6].multipeRecords && row.entity.quoteDetails[6].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[6].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[6].priceDisplayString}}</div>'},{name:"T9M",displayName:"9M",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,7,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[7].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[7].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[7].multipeRecords && row.entity.quoteDetails[7].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[7].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[7].priceDisplayString}}</div>'},{name:"T1Y",displayName:"1Y",width:"5%",minWidth:60,headerCellTemplate:y.get("header-cell-template.html"),field:"quote_price",sortDirectionCycle:[r.DESC,r.ASC],cellTemplate:'<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,8,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[8].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[8].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[8].multipeRecords && row.entity.quoteDetails[8].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[8].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[8].priceDisplayString}}</div>'},{name:"备注",displayName:"备注",width:"*",minWidth:180,field:"memo",enableSorting:!1,cellTemplate:'<div class="ui-grid-cell-contents">                        <div class="text" uib-tooltip="{{row.entity.memo}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">                            {{row.entity.memo}}                        </div></div>'},{name:"new",displayName:"最后更新",width:160,minWidth:160,headerCellClass:"lastUpdate",headerCellTemplate:y.get("header-cell-template.html"),sortDirectionCycle:[r.DESC],sort:{direction:r.DESC,priority:1},cellTemplate:'<div ng-dblclick="grid.appScope.vm.clickCopyQuoteId(row.entity)" class="ui-grid-cell-contents"><span ng-if="grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: "HH:mm:ss"}}</span><span ng-if="!grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: " yyyy-MM-dd HH:mm:ss"}}</span></div>'}]},"offline"==v.type&&(v.gridOptions.columnDefs.splice(3,1),v.gridOptions.columnDefs.splice(3,1)),"inner"==v.type&&(v.gridOptions.columnDefs.splice(5,1),v.gridOptions.columnDefs.splice(5,1),v.gridOptions.columnDefs.splice(5,1));var D=setInterval(function(){var e=jQuery(".filter-view").height();e&&(t.window.onresize(),clearInterval(D))},10)}];return{restrict:"AE",template:'<div class="back-to-top" ng-click="vm.sortPeriod(\'new\')" ng-if="vm.showToTop && vm.hasNewData">出现新报价 点击查看</div>                        <div id="market" ui-grid="vm.gridOptions" class="grid" ui-grid-infinite-scroll></div>                        <div id="qqcopy" ng-show="vm.copyqqsuccess" class="copyqqsuccess"><img width="16px" height="16px" src="app/commons/img/true.png" alt=""> 您已成功复制QQ号{{vm.qqnumber}}</div>',controller:e,scope:{list:"=",page:"=",loadData:"&",type:"="},bindToController:!0,controllerAs:"vm"}})}(angular);