/**
 * Created by jiannan.niu on 2016/9/6.
 */
(function (angular) {
    'use strict';

    angular.module('directives', []);
    angular.module('services', []);
    angular.module('moneyMarketApp', ['directives', 'services', 'ngCookies', 'ui.router', 'ui.bootstrap', 'oc.lazyLoad']);

    var app = angular.module('moneyMarketApp');

    app.factory('userInterceptor', ['$q', '$injector', 'appConfig', '$cookies', function ($q, $injector, appConfig, $cookies) {

        var isSeverRequest = function (url) {
            return !/(\.html|\.js|\.json|ui\-grid|validateUser|service)/.test(url);
        };
        return {
            request: function (config) {
                if (isSeverRequest(config.url)) {
                    config.headers['username'] = $cookies.get('username');
                    config.headers['password'] = $cookies.get('password');
                }

                return config;
            },
            requestError: function (config) {
                return config;
            },
            response: function (response) {
                if (isSeverRequest(response.config.url)) {
                    var resData = response.data;

                    if (response.config.url.endWith(appConfig.get_brother_alliance)) {
                        return response;
                    } else {
                        if (resData.return_code === 0) {
                            return response;
                        } else {
                            //返回错代码之后操作
                            if (resData.return_message) {
                                var bootbox = $injector.get('bootbox');
                                bootbox.message(resData.return_message.exceptionName+'\n'+resData.return_message.exceptionMessage, resData.return_message.exceptionStackTrace);
                            }
                            return $q.reject(response);
                        }
                    }
                } else {
                    return response;
                }
            },
            responseError: function (response) {
                return $q.reject(response);
            }
        }
    }
    ]).config([
        '$httpProvider', '$injector', function ($httpProvider, $injector) {

            $httpProvider.interceptors.push('userInterceptor');
        }
    ]);

    app.controller('marketController', marketController);
    marketController.$inject = ['marketService', '$interval', '$state'];
    function marketController (marketService, $interval, $state) {
        var vm = this;
        vm.state = 'inner';
        vm.isInit = false;
        vm.chartShow = false;
        vm.chartData = [];
        vm.row = 2;
        vm.col = 4;
        //埋点使用
        $state.current.name = 'app.innerFinancing.index';

        vm.innerDirection = [{"name": "出理财", "id": "OUT"}, {"name": "收理财", "id": "IN"}];
        vm.offlineDirection = [{"name": "出资金", "id": "OUT"}, {"name": "收资金", "id": "IN"}];
        vm.quoteTypeList = [{"name": "保本", "id": "GTF"}, {"name": "非保R2", "id": "UR2"}, {"name": "非保R3", "id": "UR3"}];
        vm.BankNature = [{
            "name": "SHIBOR",
            "fundSize": "NONE",
            "displayName": "Shibor"
        }, {
            "name": "BIG_BANK",
            "fundSize": "NONE",
            "displayName": "大行"
        }, {"name": "JOINT_STOCK",
            "fundSize": "NONE",
            "displayName": "股份制"
        }, {
            "name": "CITY_COMMERCIAL_BANK",
            "fundSize": "LARGER_FIVE_T",
            "displayName": "城农商(>5000亿)"
        }, {
            "name": "CITY_COMMERCIAL_BANK",
            "fundSize": "ONE_FIVE_T",
            "displayName": "城农商(1000-5000亿)"
        }, {
            "name": "CITY_COMMERCIAL_BANK",
            "fundSize": "SMALLER_ONE_T",
            "displayName": "城农商(<1000亿)"
        }, {
            "name": "OTHERS",
            "fundSize": "NONE",
            "displayName": "其他"
        }];

        vm.FIperiodList = [{"name": ""}, {"name": "1D(T+0)"}, {"name": "1M"}, {"name": "2M"}, {"name": "3M"}, {"name": "6M"}, {"name": "9M"}, {"name": "1Y"}];
        vm.CDperiodList = [{"name": ""}, {"name": "1D(T+0)"}, {"name": "7D"}, {"name": "14D"}, {"name": "1M"}, {"name": "2M"}, {"name": "3M"}, {"name": "6M"}, {"name": "9M"}, {"name": "1Y"}];

        //同业理财时默认选择方向出，同业存款时选择方向收
        vm.initFilter = function () {
            vm.boardDirection.forEach(function (item) {
                item.selected = false;
            });
            vm.quoteTypeList.forEach(function (item) {
                item.selected = false;
            });
            if (vm.state === 'inner') {
                vm.boardDirection[0].selected = true;
                vm.quoteTypeList[1].selected = true;
            } else if (vm.state === 'offline') {
                vm.boardDirection[1].selected = true;
            }
        };

        //初始化页面
        vm.initPage = function (isRefresh) {
            vm.isInit = false;
            marketService.getData(vm.direction, vm.quoteType).success(function (data) {
                vm.matrixData = angular.copy(vm.BankNature);

                if (data.result && Array.isArray(data.result)) {
                    if (data.result.length === 0) return;
                    data.result.forEach(function (item) {
                        vm.matrixData.forEach(function (e) {
                            if (item.matrixBankNature === e.name) {
                                if (item.matrixFundSize && item.matrixFundSize === e.fundSize) {
                                    e.rowDtoList = item.rowDtoList;
                                } else if (item.matrixFundSize === 'NONE') {
                                    e.rowDtoList = item.rowDtoList;
                                }
                            }
                        });
                    });

                    //清除所有的td选中效果
                    vm.initTd = function () {
                        vm.matrixData.forEach(function (item) {
                            item.rowDtoList.forEach(function (e) {
                                e.selected = false;
                            });
                        });
                    };
                    //如果是刷新矩阵，刷新后显示用户选中的位置
                    vm.initTd();

                    vm.matrixData[vm.row].rowDtoList[vm.col].isSelected = true;
                    if (isRefresh) {
                        vm.matrixClick(undefined, vm.matrixData[vm.row], vm.matrixData[vm.row].rowDtoList[vm.col]);
                    }

                }
                vm.isInit = true;
            })
        };

        //点击矩阵中的数据，选中数据方格，并且刷新charts
        vm.matrixClick = function (event, item, period) {
            //正常情况下点击矩阵设置请求条件，绘制charts
            vm.matrixBankNature = item.name;
            vm.matrixFundSize = item.fundSize;
            vm.timePeriod = period.timePeriod;
            vm.getChart();
            //矩阵显示选中效果
            vm.matrixData.forEach(function (e) {
                e.rowDtoList.forEach(function (ee) {
                    ee.isSelected = false;
                });
            });
            period.isSelected = true;
            //记录用户选中的位置，刷新矩阵后使用
            vm.row = vm.matrixData.indexOf(item);
            vm.rowName = vm.BankNature[vm.row].displayName;
            vm.col = item.rowDtoList.indexOf(period);
            if (vm.state === 'inner') {
                vm.colName = vm.FIperiodList[vm.col + 1].name;
            } else if (vm.state === 'offline') {
                vm.colName = vm.CDperiodList[vm.col + 1].name;
            }
        };

        //切换同业理财和同业存款
        vm.tabChange = function (event) {
            var target = event.target;
            if (target.nodeName !== 'LI') return;
            if (jQuery(target).hasClass('active')) return;
            if (jQuery(target).hasClass('inner')) {
                vm.state = 'inner';
                vm.row = 2;
                vm.col = 4;
                vm.boardDirection = vm.innerDirection;
                $state.current.name = 'app.innerFinancing.index';
                vm.direction = 'OUT';
                vm.quoteType = 'UR2';
            } else if (jQuery(target).hasClass('offline')) {
                vm.state = 'offline';
                vm.row = 2;
                vm.col = 6;
                vm.boardDirection = vm.offlineDirection;
                $state.current.name = 'app.offlineFinancing.index';
                vm.direction = 'IN';
                vm.quoteType = 'IBD';
            }
            vm.initFilter();
            vm.initPage(true);
            vm.refreshMatrix();
        };

        //切换方向
        vm.changeDirection = function (event, item) {
            var target = event.target;
            if (jQuery(target).hasClass('selected')) return;
            vm.boardDirection.forEach(function (e) {
                e.selected = false;
            });
            item.selected = true;
            vm.direction = item.id;
            vm.initPage(true);
            vm.refreshMatrix();
        };

        //切换类型
        vm.changeType = function (event, item) {
            var target = event.target;
            if (jQuery(target).hasClass('selected')) return;
            vm.quoteTypeList.forEach(function (e) {
                e.selected = false;
            });
            item.selected = true;
            vm.quoteType = item.id;
            vm.initPage(true);
            vm.refreshMatrix();
        };

        //自动刷新矩阵，时间一分钟
        var timer;
        vm.refreshMatrix = function () {
            if (timer) $interval.cancel(timer);
            timer = $interval(function () {
                vm.initPage(false);
            }, 1000 * 60);
        };

        //请求charts数据
        vm.getChart = function () {
            marketService.getChart(vm.direction, vm.quoteType, vm.matrixBankNature, vm.matrixFundSize, vm.timePeriod, 90)
                .success(function (data) {
                    if (data.result && Array.isArray(data.result)) {
                        if (data.result.length === 0) {
                            vm.chartShow = true;
                            vm.chartData = vm.mockData;
                        } else {
                            vm.chartShow = true;
                            data.result.forEach(function (item) {
                                if (item.createTime && typeof item.createTime === 'number') {
                                    var time = new Date();
                                    time.setTime(item.createTime);
                                    item.createTime = time.format('MM-dd');
                                }
                            });
                            vm.chartData = data.result;

                        }
                        vm.makeChart();
                    }
                });
        };

        //第一次进来时初始化页面
        vm.boardDirection = vm.innerDirection;
        vm.direction = 'OUT';
        vm.quoteType = 'UR2';
        vm.initFilter();
        vm.initPage(true);
        vm.refreshMatrix();


        //配置AmCharts
        vm.makeChart = function () {
            var chart = AmCharts.makeChart("market-charts", {
                "type": "serial",
                "theme": "black",
                "color": "#ffebc8",
                "backgroundAlpha": 0,
                "backgroundColor": "#222222",
                "plotAreaFillAlphas": 1,
                "plotAreaFillColors": "#161819",
                "marginTop": 10,
                "categoryField": "createTime",
                "mouseWheelZoomEnabled": true,
                "categoryAxis": {
                    "showFirstLabel": true,
                    "showLastLabel": true,
                    "minorGridAlpha": 0.1,
                    "minorGridEnabled": true,
                    "color": "#bacdc9",
                    "gridAlpha": 0.8,
                    "gridColor": "#494949",
                    "gridPosition": "middle",
                    "axisAlpha": 1,
                    "axisColor": "#494949"
                },
                "valueAxes": [{
                    "color": "#bacdc9",
                    "title": "收益率（%）",
                    "titleFontSize": 10,
                    "titleColor": "#878787",
                    "titleBold": false,
                    "titlePosition": "bottom",
                    "axisAlpha": 1,
                    "axisColor": "#494949",
                    "gridAlpha": 0.8,
                    "gridColor": "#494949"
                }],
                "dataProvider": vm.chartData,
                "legend": {//图例配置
                    "useGraphSettings": true,
                    "fontSize": 12,
                    "color": "#ffebc8",
                    "enabled": true,
                    "markerType": "line",
                    "markerBorderThickness": 2,
                    "valueText": "[[]]",
                    "valueWidth": 0,
                    "divId": "marketCharts"
                },
                "graphs": [
                    {//图形配置
                        "title": "下区间",
                        "balloonText": "",
                        //图形上的点
                        "bullet": "round",
                        "bulletAlpha": 1,
                        "bulletSize": 5,
                        "cursorBulletAlpha": 1,
                        //图形上的线条
                        "lineColor": "#b59e5c",
                        "lineThickness": 2,
                        "type": "line",
                        "valueField": "priceLow"
                    }, {
                        "title": "上区间",
                        "balloon": {
                            "borderAlpha": 0,
                            "fillAlpha": 1,
                            "fillColor": "#ffedcd",
                            "fontSize": 13
                        },
                        "balloonText": "<div style='text-align: center;'>[[category]]</br>收益率：<b>[[priceLow]]-[[priceHigh]]</b></div>",
                        //图形上的点
                        "bullet": "square",
                        "bulletAlpha": 1,
                        "bulletSize": 5,
                        "cursorBulletAlpha": 1,
                        //图形上的线条
                        "lineColor": "#d1655d",
                        "lineThickness": 2,
                        "type": "line",
                        "valueField": "priceHigh"
                    }
                ],
                "chartScrollbar": {//滚动条配置
                    //滚动条通用配置
                    "color": "transparent",
                    "scrollbarHeight": 20,
                    "backgroundColor": "#161819",
                    "backgroundAlpha": 1,
                    "selectedBackgroundAlpha": 1,
                    "selectedBackgroundColor": "#393939",
                    //滚动条拖动图标
                    "dragIcon": "dragIconRectSmallBlack",
                    "dragIconHeight": 27,
                    "dragIconWidth": 23,
                    //滚动条方格线
                    "gridAlpha": 0,
                    "autoGridCount": true,
                    //滚动条位置
                    "oppositeAxis": false,
                    "offset": 30
                },
                "chartCursor": {//鼠标悬浮效果通用配置
                    "cursorAlpha": 0.5,
                    "cursorColor": "#979797",
                    "valueLineEnabled": true,
                    "valueLineBalloonEnabled": false,
                    "valueLineAlpha": 0.5,
                    "valueLineColor": "#fff",
                    "categoryBalloonEnabled": false,
                    "categoryBalloonAlpha": 0.5,
                    "categoryBalloonColor": "#fff",
                    "fullWidth": false,
                    "cursorPosition": "middle",
                    //图形上的点
                    "bulletsEnabled": true,
                    "bulletSize": 8,
                    "categoryBalloonDateFormat": "MM-DD"
                }
            });
            //默认滚动条的位置，滚动条全长3个月，默认显示最近的一个月
            //当数据量大于30个时，拉动条显示最近1/3，否则显示全部
            if (vm.chartData.length > 29) {
                chart.zoomToIndexes(Math.round(vm.chartData.length * 2/3), vm.chartData.length);
            }
        };
        //时间格式化函数，参数n为今天的前n天
        vm.time = function (n) {
            var time = new Date();
            time.setDate(new Date().getDate() - n);
            time = time.format('MM-dd');
            return time;
        };
        //虚拟数据
        vm.mockData = [
            {"priceLow": 0, "priceHigh": null, "createTime": vm.time(1)},
        ];
    };

})(angular);