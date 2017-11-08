(function () {
    'use strict';
    angular.module('directives')
        .directive('qbTable', function () {
        
        Controller.$inject = ["$scope", "$q", "$http", "appConfig", "enumConfig", "uiGridConstants", "contactService", "qbService", "offerService", "sortService","$window","$location","tableService",'$templateCache'];
        
        function Controller($scope, $q, $http, appConfig, enumConfig, uiGridConstants, contactService, qbService, offerService, sortService,$window,$location,tableService,$templateCache) {
            $scope.window = $window;
            $scope.location = $location;

            var dataDefine = {
                quoteMethodDefine: {
                    SEF: { "displayName": "普通报价", "value": 'SEF' },
                    ALC: { "displayName": "联盟报价", "value": 'ALC' },
                    BRK: { "displayName": "代报价", "value": 'BRK' }
                }
            };

            var vm = this;
            vm.state = 'QB精选';
            vm.isSort = false;
            vm.bury = 'Qbchosen.Sequence';  //埋点功能使用
            
            var dateToday = new Date();
            dateToday.setHours(0);
            dateToday.setMinutes(0);
            dateToday.setSeconds(0);
            dateToday.setMilliseconds(0);
            vm.today = dateToday.getTime();
            vm.enumConfig = enumConfig;
            
            vm.showToTop = false; 	//滚定条非顶部
            vm.hasNewData = false;	//有新数据推送
            
            $scope.$watch('vm.list', function (newValue, oldValue) {
                if (newValue) {
                    vm.gridOptions.data = newValue;
                }
            }, true);
            
            //翻页获取数据
            vm.getDataDown = function () {
                var promise = $q.defer();
                vm.loadData({
                    callback: function () {
                        vm.gridApi.infiniteScroll.saveScrollPercentage();
                        vm.gridApi.infiniteScroll.dataLoaded(true, true).then(function () {
                            promise.resolve();
                        });
                    }
                });
                
                return promise.promise;
            };
            
            //滑到顶部事件
            vm.scrollingTop = function () {
                vm.hasNewData = false;
                vm.showToTop = false;
                var promise = $q.defer();
                vm.gridApi.infiniteScroll.saveScrollPercentage();
                vm.gridApi.infiniteScroll.dataLoaded(true, true).then(function () {
                    promise.resolve();
                });
                return promise.promise;
            };
            
            //获取联系方式数组
            vm.getArray = function (str) {
                if (str) {
                    str = str.split(/,|;/);
                    return str.findWhere(function (item) {
                        return item;
                    })
                }
            };

            //复制报价ID到剪切板
            vm.clickCopyQuoteId = function (quote) {
                if (!quote) return;
                document.oncopy = function (e) {
                    e.clipboardData.setData('text/plain', quote.quoteId);
                    e.preventDefault();
                };
                document.execCommand("copy");
            };
            
            //打开QM
            vm.openQM = function (user) {
                if (!quote || !quote.quoteUserId) return;
                qbService.openQM(quote.quoteUserId);
            };
            
            //点击机构
            vm.clickOrg = function (quote) {
                if (!quote || !quote.quoteUserId) return;
                qbService.openQM(quote.quoteUserId);
            };
            
            //滚动
            $scope.$watch('vm.gridApi.grid.isScrollingVertically', function (newValue) {
                if (newValue) {
                    vm.showToTop = true;
                }
            });
            
            //接收到新值推送
            $scope.$on('hasNewData', function () {
                if (jQuery('.ui-grid-viewport').scrollTop() < 100) return;
                if (vm.isSort) {
                    vm.showToTop = true;
                    vm.hasNewData = true;
                } else if (vm.showToTop) {
                    vm.hasNewData = true;
                }
            });
            
            //返回顶部
            vm.scrollToTop = function () {
                vm.gridApi.core.scrollTo(vm.gridOptions.data[0]);
                vm.showToTop = false;
                vm.hasNewData = false;
            };

            //通过tableService将数据传输给tooltips
            vm.quoteOnmouseOver = function (e,index,period) {
                if (!period || !period.quoteDetails || !period.quoteDetails[index].priceDetails) return;
                vm.periodTooltip = period.quoteDetails[index].priceDetails;
            };
            
            vm.isToday = function (time) {
                if (!time) return;
                return parseInt(time) > vm.today;
            };

            vm.sortPeriod = function (sort) {

                if (!sort || typeof(sort) !== 'string') return;
                if (sort.charAt(0) === 'T'){//点击期限向后台请求数据
                    var seq;
                    if (vm.period === sort && vm.order === 'DESC') {
                        seq = 'ASC';
                    } else if (vm.period !== sort){
                        seq = 'DESC';
                    } else if(vm.period === sort && vm.order === 'ASC') {
                        seq = 'DESC';
                    }

                    tableService.setOrderByPeriod(sort);
                    tableService.setOrderSeq(seq);
                    $scope.$emit('filterChanged');

                    vm.period = sort;
                    vm.order = seq;
                    vm.isSort = true;
                } else if (sort === 'new') {//点击查看新数据
                    tableService.setOrderByPeriod(null);
                    tableService.setOrderSeq('');
                    $scope.$emit('filterChanged');

                    vm.period = '';
                    vm.order = '';
                    vm.isSort = false;
                    vm.gridApi.infiniteScroll.dataRemovedTop();
                    vm.hasNewData = false;
                    vm.showToTop = false;
                    //强制最后更新出现小三角符号
                    jQuery('.ui-grid-cell-contents i.ui-grid-icon-down-dir').removeClass('ui-grid-icon-down-dir').addClass('ui-grid-icon-blank');
                    jQuery('.lastUpdate .ui-grid-cell-contents i.ui-grid-icon-blank').addClass('ui-grid-icon-down-dir').removeClass('ui-grid-icon-blank');
                    jQuery('.lastUpdate .ui-grid-cell-contents span.ui-grid-invisible').removeClass('ui-grid-invisible');
                } else {//ui-grid默认排序 设置一个标识位
                    vm.isSort = true;
                }
            };

            vm.gridOptions = {
                enableHorizontalScrollbar: uiGridConstants.scrollbars.ALWAYS,
                rowHeight: 30,
                infiniteScrollRowsFromEnd: 20,
                infiniteScrollDown: true,
                infiniteScrollUp: true,
                onRegisterApi: function (gridApi) {
                    gridApi.infiniteScroll.on.needLoadMoreData($scope, vm.getDataDown);
                    gridApi.infiniteScroll.on.needLoadMoreDataTop($scope, vm.scrollingTop);
                    vm.gridApi = gridApi;

                    //注册onresize事件
                    vm.reload = false;
                    $scope.window.onresize = function () {
                        //当窗口最小化并还原时，刷新ui-grid，其他情况下只调整ui-grid的位置和高度
                        var wh = $scope.window.innerHeight;
                        if (wh === 2) {
                            vm.reload = true;
                        } else {
                            if (vm.reload) {
                                vm.gridApi.grid.refresh();
                            }
                        }
                        //动态调整ui-grid的位置，因为filter的位置不确定，可能会引起ui-grid起始位置和高度失真
                        var tim = setInterval(function () {
                            var wh = $scope.window.innerHeight;
                            if (wh > 100) {
                                var ht = jQuery('.operation-line').offset().top;
                                var hh = jQuery('.operation-line').height();
                                var h = wh - hh - ht;
                                jQuery('.ui-grid').css('top',jQuery('.operation-line').offset().top + jQuery('.operation-line').height() + 'px');
                                jQuery('.ui-grid').css('bottom','-7px');
                                jQuery('.ui-grid').css('right','0px');
                                jQuery('.ui-grid').css('left','0px');
                                jQuery('.ui-grid-viewport').height(h-38);
                                jQuery('.ui-grid-viewport').scrollTop(jQuery('.ui-grid-viewport').scrollTop() + 1);
                                jQuery('.ui-grid-viewport').scrollLeft(jQuery('.ui-grid-viewport').scrollLeft() + 1);
                                clearInterval(tim);
                            }
                        },10);
                    };
                },
                rowTemplate: '<div ng-repeat="col in colContainer.renderedColumns track by col.colDef.displayName" ng-class="{warning:row.entity.isNew, inactive:row.entity.active==0}" class="ui-grid-cell" ui-grid-cell></div>',
                columnDefs: [
                    {
                        name: '机构',
                        displayName: '　机构',
                        width: "10%",
                        minWidth: 220,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'primeListInstitutionName',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div class="ui-grid-cell-contents pointer ui-grid-cell-contents-firstcol">\
                                            <div burying="Qbchosen.ClickInstitutions,{{row.entity.quoteId}}" class="name" uib-tooltip="{{row.entity.primeListInstitutionName}}" tooltip-append-to-body="true" tooltip-animation="false" tooltip-placement="top-left auto">{{row.entity.primeListInstitutionName}}</div>\
                                       </div>',
                    },
                    {
                        name: '联系人',
                        displayName: '联系人',
                        width: "5%",
                        minWidth: 100,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'primeListContacts',
                        enableSorting: false,
                        cellTemplate: '<div class="ui-grid-cell-contents cell-user-list" uib-dropdown ng-class="{show:status.isopen}" is-open="status.isopen" ng-if="row.entity.primeListContacts[0].name" uib-tooltip="{{row.entity.primeListContacts[0].name}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">\
                                            <span>{{row.entity.primeListContacts[0].name}}</span>\
                                    </div>'
                    },
                    {
                        name: '联系方式',
                        displayName: '联系方式',
                        width: "5%",
                        minWidth: 100,
                        field: 'primeListInstitutionTelephone',
                        enableSorting: false,
                        cellTemplate: '<div uib-dropdown is-open="status.isopen" ng-class="{show:status.isopen}" class="ui-grid-cell-contents cell-contact">\
                                            <img burying="Qbchosen.ContactsQM,{{row.entity.quoteId}}" src="app/commons/img/qmlogo.png" class="qm-logo pointer active" ng-class="{active : row.entity.primeListContacts[0].status}" ng-click="grid.appScope.vm.clickOrg(row.entity)">\
                                            <span burying="Qbchosen.ClickTelephone,{{row.entity.quoteId}}" uib-dropdown-toggle ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)"><i class="glyphicon glyphicon-earphone"></i></span>\
                                            <ul class="dropdown-menu dropdown-menu-left" ng-if="row.entity.quoteSource==\'PRIME_QB\' && (row.entity.primeListContacts[0].telephone || row.entity.primeListContacts[0].mobile)" uib-dropdown-menu>\
                                                <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>\
                                                <li role="menuitem" ng-repeat="item in grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile) track by $index"><a><i class="glyphicon glyphicon-earphone"></i> {{item | telFmt:\'3-4-4\'}}</a></li>\
                                            </ul>\
                                            <span uib-dropdown-toggle class="no-pointer" ng-if="(row.entity.quoteSource==\'PRIME_QB\' && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].telephone) && !grid.appScope.vm.getArray(row.entity.primeListContacts[0].mobile)) || (row.entity.quoteSource!==\'PRIME_QB\'&&!grid.appScope.vm.getArray(row.entity.marketListQuoterContacts))"><i class="glyphicon glyphicon-earphone inactive"></i></span>\
                                       </div>'
                    },
                    {
                        name: '方向',
                        displayName: '方向',
                        width: "4%",
                        maxWidth: 80,
                        minWidth: 50,
                        field: 'direction',
                        enableSorting: false,
                        cellTemplate: '<div class="ui-grid-cell-contents">\
                                            <div class="badge" ng-class="{\'IN\':\'warning\',\'OUT\':\'info\'}[row.entity.direction]">\
                                            {{row.entity.direction=="IN" ? "收" : "出"}}</div>\
                                        </div>'
                    },
                    {
                        name: '类型',
                        displayName: '类型',
                        width: "6%",
                        minWidth: 70,
                        field: 'quote_type',
                        enableSorting: false,
                        cellTemplate: '<div class="ui-grid-cell-contents">\
                                            {{grid.appScope.vm.enumConfig["quoteType"][row.entity.quoteType]}}\
                                        </div>'
                    },
                    {
                        name: 'T1D',
                        displayName: '1D(T+0)',
                        width: "5%",
                        minWidth: 90,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T1D',
                        displayName: '1D',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,0,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[0].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[0].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[0].multipeRecords && row.entity.quoteDetails[0].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[0].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[0].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T7D',
                        displayName: '7D',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,1,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[1].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[1].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[1].multipeRecords && row.entity.quoteDetails[1].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[1].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[1].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T14D',
                        displayName: '14D',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,2,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[2].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[2].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[2].multipeRecords && row.entity.quoteDetails[2].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[2].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[2].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T1M',
                        displayName: '1M',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,3,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[3].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[3].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[3].multipeRecords && row.entity.quoteDetails[3].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[3].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[3].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T2M',
                        displayName: '2M',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,4,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[4].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[4].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[4].multipeRecords && row.entity.quoteDetails[4].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[4].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[4].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T3M',
                        displayName: '3M',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,5,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[5].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[5].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[5].multipeRecords && row.entity.quoteDetails[5].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[5].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[5].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T6M',
                        displayName: '6M',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,6,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[6].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[6].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[6].multipeRecords && row.entity.quoteDetails[6].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[6].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[6].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T9M',
                        displayName: '9M',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,7,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[7].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[7].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[7].multipeRecords && row.entity.quoteDetails[7].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[7].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[7].priceDisplayString}}</div>'
                    },
                    {
                        name: 'T1Y',
                        displayName: '1Y',
                        width: "5%",
                        minWidth: 60,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        field: 'quote_price',
                        sortDirectionCycle: [uiGridConstants.DESC, uiGridConstants.ASC],
                        cellTemplate: '<div ng-mouseenter="grid.appScope.vm.quoteOnmouseOver($event,8,row.entity)" class="ui-grid-cell-contents ellipsis" uib-popover-template="\'app/commons/table/tooltip.html\'" popover-class="table-popover" popover-append-to-body="false" popover-enable="{{row.entity.quoteDetails[8].multipeRecords}}" popover-trigger="mouseenter" popover-placement="top auto" uib-tooltip="{{row.entity.quoteDetails[8].priceDisplayString}}" tooltip-enable="{{!row.entity.quoteDetails[8].multipeRecords && row.entity.quoteDetails[8].priceDisplayString !== \'-\'}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto"><div ng-if="row.entity.quoteDetails[8].multipeRecords" class="triangle-topright"></div>{{row.entity.quoteDetails[8].priceDisplayString}}</div>'
                    },
                    {
                        name: '备注',
                        displayName: '备注',
                        width: "*",
                        minWidth: 180,
                        field: 'memo',
                        enableSorting: false,
                        cellTemplate: '<div class="ui-grid-cell-contents">\
                        <div class="text" uib-tooltip="{{row.entity.memo}}" tooltip-append-to-body="false" tooltip-animation="false" tooltip-placement="top-left auto">\
                            {{row.entity.memo}}\
                        </div></div>'
                    },
                    {
                        name: 'new',
                        displayName: '最后更新',
                        width: 160,
                        headerCellTemplate: $templateCache.get('header-cell-template.html'),
                        headerCellClass: 'lastUpdate',
                        sortDirectionCycle: [uiGridConstants.DESC],
                        sort: {
                            direction: uiGridConstants.DESC,
                            priority: 1
                        },
                        cellTemplate: '<div ng-dblclick="grid.appScope.vm.clickCopyQuoteId(row.entity)" class="ui-grid-cell-contents"><span ng-if="grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: "HH:mm:ss"}}</span><span ng-if="!grid.appScope.vm.isToday(row.entity.lastUpdateTime)">{{row.entity.lastUpdateTime | date: " yyyy-MM-dd HH:mm:ss"}}</span></div>'
                    }]
            };
            
            //若为线下资金去掉类型列
            if (vm.type == 'offline') {
                vm.gridOptions.columnDefs.splice(4, 1);
                vm.gridOptions.columnDefs.splice(4, 1);
            }
            if (vm.type == 'inner') {
                vm.gridOptions.columnDefs.splice(6, 1);
                vm.gridOptions.columnDefs.splice(6, 1);
                vm.gridOptions.columnDefs.splice(6, 1);
            }
            //载入页面时进行ui-grid位置校正，因为filter位置不确定，可能会引起ui-grid起始位置和高度失真
            var timer = setInterval(function () {
                var hei = jQuery('.filter-view').height();
                if (hei) {
                    $scope.window.onresize();
                    clearInterval(timer);
                }
            },10);
        };
        
        
        return {
            restrict: 'AE',
            template: '<div class="back-to-top" ng-click="vm.sortPeriod(\'new\')" ng-if="vm.showToTop && vm.hasNewData">出现新报价 点击查看</div>\
                        <div id="qb" ui-grid="vm.gridOptions" class="grid" ui-grid-infinite-scroll></div>',
            scope: {
                list: '=',
                page: '=',
                loadData: '&',
                type: '='
            },
            controller: Controller,
            bindToController: true,
            controllerAs: 'vm'
        }


    });

})();