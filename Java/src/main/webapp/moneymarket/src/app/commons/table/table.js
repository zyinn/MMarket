(function (angular) {
    'use strict';
    angular.module('moneyMarketApp')
        .controller('tableController', tableController);
    
    tableController.$inject = ['$scope', 'commonService', 'tableService', 'offerService', 'financingType', 'enumConfig', '$timeout', 'userinfoService', '$window', 'qbService', '$cookies'];
    
    function tableController($scope, commonService, service, offerService, financingType, enumConfig, $timeout, userinfo, $window, qbService, $cookies) {
        var vm = this;

        //判断同业理财还是线下资金，修改service里的请求URL
        vm.financingType = financingType;
        if (financingType === 'inner') {
            service.setFinancingType('inner');
        } else if (financingType === 'offline') {
            service.setFinancingType('offline');
        }

        //初始化服务里各项参数
        service.initOptions();
        
        vm.state = 'market';
        vm.isSelf = 0;
        //QB报价页数初始化和市场报价页数初始化
        var initPage = function () {
            vm.qbPage = 1, vm.marketPage = 1;
        };
        initPage();

        service.isInit = true;

        var quoteDetails = [
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T1D", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T7D", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T14D", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T1M", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T2M", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T3M", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T6M", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T9M", "priceDetails": []},
            {"multipeRecords": false, "priceDisplayString": "-", "quoteTimePeriod": "T1Y", "priceDetails": []}
        ];

        function setPeriod (item) {
            if (!item || !item.quoteDetails) return;

            var quoteDetail = angular.copy(quoteDetails);
            if (!item.quoteDetails instanceof Array) return;
            if (item.quoteDetails.length === 0) return item.quoteDetails = quoteDetail;
            item.quoteDetails.forEach(function (e) {
                quoteDetail.forEach(function (ee) {
                    if (!e.quoteTimePeriod || !ee.quoteTimePeriod) return;

                    if (e.quoteTimePeriod === ee.quoteTimePeriod) {
                        ee.multipeRecords = e.multipeRecords;
                        if (e.priceDisplayString === null) {
                            ee.priceDisplayString = '-';
                        } else {
                            ee.priceDisplayString = e.priceDisplayString;
                        }
                        ee.priceDetails = e.priceDetails;
                    }
                })
            })
            item.quoteDetails = quoteDetail;
        }
        //初始化数据
        vm.initData = function (type) {
            if (!type) type = vm.state;
            initPage();
            clearGridData(vm.state);
            vm.isLoading = true;
            
            var initQbList = function () {
                vm.state = 'qb';
                service.getQbOfferList(1).success(function (res) {


                    res.result.forEach(function (item) {
                        setPeriod(item);
                    });

                    vm.qbOfferList = res.result;

                    vm.isLoading = false;
                });
            };
            
            var initMarketList = function () {
                vm.state = 'market';
                service.getMarketOfferList(1).success(function (res) {

                    res.result.forEach(function (item) {
                        setPeriod(item);
                    });

                    vm.marketOfferList = res.result;

                    vm.isLoading = false;
                });
            };
            
            switch (type) {
                case 'qb':
                    initQbList();
                    break;
                case 'market':
                    initMarketList();
                    break;
            }
        };

        vm.initData(vm.state);


        //切换QB精品和市场报价
        vm.switchOfferType = function (type) {
            if (type == vm.state) return;

            service.filter.orderByPeriod = null;
            service.filter.orderSeq = '';
            vm.initData(type);
        };
        
        
        //searchtype中文和枚举映射
        var searchTypeEnum = {
            "机构": "INSTITUTE",
            "QB用户": "CONTACT",
            "QQ用户": "CONTACT",
            "备注": "MEMO"
        };
        
        vm.clickSearchInput = function () {
            vm.openDropdown = !vm.openDropdown;
        };
        
        vm.clickSearchClose = function () {
            vm.initFilter('mySearch');
            vm.searchKeyword = '';
            searchBoxEnter();
        };
        
        //执行输入搜索
        vm.memoType = "备注";
        var searchKeyword = _.debounce(function () {
            if (!vm.searchKeyword) return;
            
            service.searchKeyword(vm.searchKeyword).success(function (response) {
                var res = response.result;
                vm.orgList = res[0].list;
                vm.orgType = res[0].type;
                vm.qbUserList = res[1].list;
                vm.qbUserType = res[1].type;
                vm.qqUserList = res[2].list;
                vm.qqUserType = res[2].type;
                vm.memoWord = vm.searchKeyword;
                vm.openDropdown = true;
            });
            $scope.$apply();

        }, 300);
        
        //备注搜索
        vm.searchMemo = function () {
            vm.initFilter('mySearch');
            vm.openDropdown = false;
            
            service.searchType = searchTypeEnum[vm.memoType];
            service.keyword = vm.searchKeyword;
            
            vm.initData(vm.state);
        };

        //回车
        var searchData = function (type, key) {
            vm.initFilter('mySearch');
            vm.openDropdown = false;
            
            service.searchType = searchTypeEnum[type];
            service.keyword = key;
            
            vm.initData(vm.state);
        };
        
        //检索框回车
        var searchBoxEnter = function () {
            service.searchKeyword(vm.searchKeyword).success(function (response) {
                var res = response.result;
                var type;
                var key;
                var binggo = false;
                for (var i = 0; i <= 2; i++) {
                    if (res[i].list.length > 0) {
                        type = res[i].type;
                        key = res[i].list[0].id;
                        binggo = true;
                        vm.searchKeyword = res[i].list[0].name;
                        break;
                    }
                }
                
                if (!binggo) {
                    key = vm.searchKeyword;
                    type = vm.memoType;
                }

                searchData(type, key);
            });
        };
        
        //输入搜索间隔时间控制
        vm.searchKeyup = function (event) {
            
            if (event.ctrlKey === true && event.altKey && event.keyCode === 120) {
                userinfo.logout();
                return;
            }
            
            if (event.keyCode == 13) {
                searchBoxEnter();
                enterBury();
            } else {
                if (vm.searchKeyword && vm.searchKeyword.length > 1) {
                    searchKeyword();
                } else {
                    
                    vm.openDropdown = false;
                    
                    if (vm.searchKeyword !== undefined && vm.searchKeyword.length <= 0 && event.keyCode === 8) {
                        vm.searchMemo();
                    }
                }
            }
        };

        //搜索功能enter键埋点功能
        function enterBury () {
            var para;
            if (financingType === 'inner') {
                para = "MMFI.Search=" + vm.searchKeyword;
            } else if (financingType === 'offline') {
                para = "MMCD.Search=" + vm.searchKeyword;
            }
            console.log("埋点数据：" + para);
            qbService.sendBurying(para);
        }
        
        //机构和联系人选定某项
        vm.selectSearch = function (event, item, type) {
            vm.initFilter('mySearch');
            $(event.target).parents('.dropdown-menu').prev().focus();
            vm.searchKeyword = item.name;
            service.keyword = item.id;
            service.searchType = searchTypeEnum[type];
            vm.initData(vm.state);
        };
        
        //初始化过滤参数
        vm.initFilter = function (data) {
            $scope.$emit('initFilter', data);
            if (data === 'mySearch') {//我的搜索吧我的报价置为空
                vm.isSelf = 0;
            } else if(data === 'myPrice') {//我的报价吧搜索置空
                service.searchType = '';
                service.keyword = "";
                vm.searchKeyword = '';
            }
        }
        
        //获取QB数据方法
        vm.loadQbData = function (callback) {
            vm.qbPage++;
            service.getQbOfferList(vm.qbPage).success(function (res) {
                if (!res.result.length) {
                    console.log("没有数据了");
                    return;
                }
                res.result.forEach(function (item) {
                    setPeriod(item);
                });
                vm.qbOfferList = vm.qbOfferList.concat(res.result);
                if (typeof callback == 'function') callback();
            });
        };
        
        //获取market数据
        vm.loadMarketData = function (callback) {
            vm.marketPage++;
            service.getMarketOfferList(vm.marketPage).success(function (res) {
                if (!res.result.length) {
                    console.log("没有数据了");
                    return;
                }
                res.result.forEach(function (item) {
                    setPeriod(item);
                });
                vm.marketOfferList = vm.marketOfferList.concat(res.result);
                if (typeof callback == 'function') callback();
            });
        };
        
        //点击我要报价
        vm.manageOffer = function () {
            
            var direction = { 'inner': 'OUT', 'offline': 'IN' }[financingType];
            
            offerService.openModal(function (res) {});

        };
        //点击市场分析
        vm.marketAnalysis = function () {
            if ((vm.openWindow !== undefined) && !vm.openWindow.closed) {
                vm.openWindow.focus();
                return;
            }
            vm.openWindow = $window.open('market-analysis.html?tab=false','market-analysis','width=1000,height=700,left=300,top=100,titlebar=no,toolbar=no,menubar=no,scrollbars=auto,resizable=no,location=no,status=no');
        };
        //第一次打开网页弹出市场分析页面
        var firstMarket = $cookies.get('firstMarket');
        if (firstMarket === undefined) {
            vm.marketAnalysis();
            $cookies.put('firstMarket', 'false', {'expires': new Date('January 6, 2119') });
        }
        
        //有效报价和我的报价筛选
        vm.changeFilter = function () {
            vm.initFilter('myPrice');
            service.isSelf = vm.isSelf;
            if (vm.isSelf) {
                service.searchType = "CONTACT";
                service.keyword = userinfo.getUserId();
            }
            vm.initData(vm.state);
        };
        
        //响应广播事件
        $scope.$on('refreshList', function (event, data) {

            vm.initData(vm.state);

        });
        
        //刷新前清空grid数据
        function clearGridData(state) {
            if (state == 'qb') {
                vm.qbOfferList = [];
            } else if (state == 'market') {
                vm.marketOfferList = [];
            }
        }
        
        
        //websocket操作
        var ws = service.connectWebSocket();
        ws.onopen = function () {
            console.log('open');
        };
        
        ws.onmessage = function (e) {
            if (e.data) {
                // 使用反向代理时，接受ping frame用以保持连接。
                if (e.data === "ping") {
                    console.log("Received ping for keeping wsandlocalmessage connection.");
                    return;
                }
                
                var item = JSON.parse(e.data);
                console.log(item);
                if (item.messageType === "QUOTES_CHANGE") {
                    var list = item.message;
                    list.isNew = true;

                    handerPush(list);

                    $timeout(function () {
                        list.isNew = false;
                    }, 500);

                }
            }
            
            // $scope.$apply();
            commonService.safeApply($scope);

        };
        
        ws.onclose = function (e) {
            console.log('close');
        };
        
        var filterSearchOption = function (quote) {
            if (service.keyword) {
                switch (service.searchType) {
                    case 'INSTITUTE':
                        if (quote.institutionId !== service.keyword) {
                            return false;
                        }
                        break;
                    case 'CONTACT':
                        if (service.isSelf) {
                            if (quote.quoteUserId !== service.keyword && quote.quoteOperatorId !== service.keyword) {
                                return false;
                            }    
                        }
                        else{
                            if (quote.quoteUserId !== service.keyword) {
                            return false;
                            }
                        }
                        break;
                    case 'MEMO':
                        if (quote.memo.indexOf(service.keyword) === -1) {
                            return false;
                        }
                        break;
                    default:
                        return false;
                        break;
                }
            }
            
            return true;
        };

        //判断推送数据是否显示
        function handerPush(quote) {
            // 报价方向不匹配时不添加
            if (service.filter.direction !== quote.direction) {
                var list = vm[vm.state + 'OfferList'];
                if (list) {
                    if (quote.quoteId) {
                        vm[vm.state + 'OfferList'] = list.findWhere(function (e) {
                            return e.quoteId !== quote.quoteId;
                        });
                    }
                }
                return;
            }
            // console.log(service.filter.direction)
            //同业理财类型不对不添加
            if (financingType === 'inner' && service.filter.quote_type && service.filter.quote_type.indexOf(quote.quoteType) === -1) return;

            //线下资金托管不对不添加
            var trustTypeEnum = enumConfig.trustType;
            if (financingType === 'offline') {
                if ('IBD' !== quote.quoteType || (service.filter.trust_type === true && quote.custodianQualification === false)) return;
            }
            // console.log(service.filter.quote_type)
            //地区不对不添加
            if ((service.filter.province.length && service.filter.province.indexOf(quote.province) === -1) || (quote.province === null && service.filter.province instanceof Array && service.filter.province.length !== 0)) return;
            // console.log(service.filter.province)
            //机构类型不对不添加
            if (!quote.bankNature || (service.filter.bankNatures.length && service.filter.bankNatures.indexOf(quote.bankNature) === -1)) return;
            // console.log(service.filter.bankNatures)
            //银行规模不对不添加
            if ((quote.fundSizeEnum && service.filter.fundSizes.length !==0 && service.filter.fundSizes.indexOf(quote.fundSizeEnum) === -1) || (quote.fundSizeEnum === null && service.filter.fundSizes instanceof Array && service.filter.fundSizes.length !== 0)) return;
            // console.log(service.filter.fundSizes)
            //产品特征不对不添加
            quote.tags = angular.copy(service.filter.tagNames).findWhere(function (item) {
                return quote.memo.indexOf(item) !== -1;
            });
            if (quote.memo && service.filter.tagNames.length !== 0 && quote.tags.length === 0) return;
            //我的报价过滤
            if (service.isSelf && userinfo.getUserId() !== quote.quoteOperatorId && userinfo.getUserId() != quote.quoteUserId) return;
            //检索条件过滤
            if (!filterSearchOption(quote)) return;


            if (vm.state === "qb" && quote.quoteSource === 'PRIME_QB') {
                pushQuotOper(quote);
            } else if (vm.state === "market") {
                pushQuotOper(quote);
            }

        }
        
        
        var pushQuotOper = function (quote) {
            var list = vm[vm.state + 'OfferList'];

            if (!list || !list instanceof Array || !quote.quoteDetails instanceof Array || !quote.quoteId || !quote.expiredDate) {
                return;
            }
            var id = quote.quoteId;
            var expiredDate = Number(quote.expiredDate);
            var originLength = list.length;
            var newQuote = angular.copy(quoteDetails);
            var isUpdate = list.indexOfItem(function (item) {
                if (item.quoteId) {
                    return item.quoteId === id;
                } else {
                    return false;
                }
            });

            if (isUpdate >= 0) {
                if (expiredDate < new Date().getTime()){
                     list = list.findWhere(function (item) {
                         if (item.quoteId) {
                             return item.quoteId !== id;
                         }
                     });
                    vm[vm.state + 'OfferList'] = list;
                } else {
                    if (service.filter.orderByPeriod === null) {
                        quote.quoteDetails.forEach(function (item) {
                            newQuote.forEach(function (e) {
                                if (!e.quoteTimePeriod || !item.quoteTimePeriod) return;
                                if (item.quoteTimePeriod === e.quoteTimePeriod) {
                                    e.priceDisplayString = item.priceDisplayString;
                                    e.multipeRecords = item.multipeRecords;
                                    e.priceDetails = item.priceDetails;
                                }
                            })
                        })
                        quote.quoteDetails = newQuote;
                        list = list.findWhere(function (item) {
                            if (item.quoteId) {
                                return item.quoteId !== id;
                            }
                        });
                        list.unshift(quote);
                        if (list.length > originLength && list.length > 200) {
                            list.splice(list.length - 1, 1);
                        }
                        vm[vm.state + 'OfferList'] = list;
                    }
                    $scope.$broadcast('hasNewData');
                }
            } else {
                if (expiredDate < new Date().getTime()) {
                    return;
                } else {
                    if (service.filter.orderByPeriod === null) {
                        quote.quoteDetails.forEach(function (item) {
                            newQuote.forEach(function (e) {
                                if (!e.quoteTimePeriod || !item.quoteTimePeriod) return;
                                if (item.quoteTimePeriod === e.quoteTimePeriod) {
                                    e.priceDisplayString = item.priceDisplayString;
                                    e.multipeRecords = item.multipeRecords;
                                    e.priceDetails = item.priceDetails;
                                }
                            })
                        })
                        quote.quoteDetails = newQuote;
                        list = list.findWhere(function (item) {
                            if (item.quoteId) {
                                return item.quoteId !== id;
                            }
                        });
                        list.unshift(quote);
                        if (list.length > originLength && list.length > 200) {
                            list.splice(list.length - 1, 1);
                        }
                        vm[vm.state + 'OfferList'] = list;
                    }
                    $scope.$broadcast('hasNewData');
                }
            }
        };

        //处理QM状态更新推送
        function handerQMStatus(result) {
            if (!result) {
                return;
            }
            //更新状态
            var updateStatus = function (offerItem, userItem) {
                if (offerItem.source == 'QQ') return;
                
                if (!offerItem.quote_user_list) return;
                
                offerItem.quote_user_list.forEach(function (quoteUserItem) {
                    if (quoteUserItem.qb_id == userItem.qb_id) {
                        quoteUserItem.status = userItem.new_state;
                    }
                });

            };
            
            //遍历推送的结果和offer列表里每一项
            result.forEach(function (userItem) {
                
                if (vm.state == 'qb') {
                    if (vm.qbOfferList) {
                        vm.qbOfferList.forEach(function (offerItem) {
                            updateStatus(offerItem, userItem);
                        });
                    }
                }
                else if (vm.state == 'market') {
                    if (vm.marketOfferListmarketOfferList) {
                        vm.marketOfferList.forEach(function (offerItem) {
                            updateStatus(offerItem, userItem);
                        });
                    }
                }
            });

        }
        
        //$scope销毁时关闭websocket
        $scope.$on('$destroy', function () {
            ws.close();
        });
        
        //查看本机构报价
        $scope.$on('toTableInstitution', function (event, data) {
            vm.searchKeyword = data.name;
            service.keyword = data.id;
            service.searchType = searchTypeEnum["机构"];
            
            vm.isSelf = false;
            service.isSelf = false;
            
            $scope.$emit('filterChanged');

        });

    }

})(window.angular);