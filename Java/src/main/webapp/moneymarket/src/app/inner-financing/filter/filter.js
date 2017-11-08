(function (angular) {
    'use strict';
    
    var mainModule = angular.module('moneyMarketApp');
    
    mainModule.controller('innerFilterController', [
        
        '$uibModal', '$scope', 'areaService', 'innerFilterService', 'tableService', 'offerService', 'enumConfig', '$cookies',
        function ($uibModal, $scope, areaService, service, tableService, offerService, enumConfig, $cookies) {
            
            var vm = this;
            vm.quoteType = [];
            vm.areas = [];
            vm.oldAreaList = [];
            //获取用户配置信息
            service.getUserArea().success(function (response) {
                var res = response.result;
                // console.log(res)
                vm.boardDirection = offerService.dataDefine.boardDirection.map(function (e) { return { name: e.displayName + "理财", value: e.value }; });
                vm.selectBoardDirection = vm.boardDirection[0];
                vm.selectBoardDirection.selected = true;

                //同业理财类型筛选项去掉同存
                vm.quoteTypeList = res[0].quote_type.slice(0, res[0].quote_type.length - 1);
                vm.quoteTypeList = [{ isAll : true, name : '全部', selected: true}].concat(vm.quoteTypeList);
                //同业理财类型默认选择全部
                // vm.quoteTypeList[1].selected = true;
                vm.quoteType = [vm.quoteTypeList[0]];
                //常用地区
                vm.areaList = [{ isAll : true, name : '全部', selected: true }].concat(res[0].region.map(function (item) {
                    return { name : item.province };
                }));
                
                //期限字典
                enumConfig.periodValue = res[0].period;
                
                // 获取用户报价权限
                enumConfig.quoteMethod = res[0].quote_method;

                //银行规模
                vm.fundSizes = res[0].fundSizes;
                vm.bankSizeList = [{ isAll : true, name : '全部', selected: true}];
                angular.copy(res[0].fundSizes).forEach(function (item,index) {
                    var change = item.name;
                    item.name = item.displayName;
                    item.displayName = change;
                    item.selected = false;
                    item.displayOrder = index + 1;
                    vm.bankSizeList.push(item);
                });

                //机构类型
                vm.calculationBankNature = res[0].calculationBankNature;
                vm.institutionTypeList = [{ isAll : true, name : '全部', selected: true}];
                angular.copy(res[0].calculationBankNature).forEach(function (item,index) {
                    var change = item.name;
                    item.name = item.displayName;
                    item.displayName = change;
                    item.selected = false;
                    item.displayOrder = index + 1;
                    if(item.displayName !== "SHIBOR"){
                        vm.institutionTypeList.push(item);
                    }
                });

                //产品特征
                vm.tags = res[0].tags;
                vm.featureList = [{ isAll : true, name : '全部', selected: true }];
                angular.copy(res[0].tags).forEach(function (item,index) {
                    item.name = item.tagName;
                    item.selected = false;
                    item.displayOrder = index + 1;
                    vm.featureList.push(item);
                });
                offerService.featureList = angular.copy(vm.featureList);

            });
            
            var operAfterSetArea = function () {
                var needReset = false;
                for (var i = 0; i < vm.oldAreaList.length; ++i) {
                    var itemOld = vm.oldAreaList[i];
                    if (itemOld.selected) {
                        var bFind = false;
                        for (var j = 0; j < vm.areaList.length; ++j) {
                            var itemNew = vm.areaList[j];
                            if (itemNew.name == itemOld.name) {
                                itemNew.selected = true;
                                bFind = true;
                                break;
                            }
                        }
                        
                        if (!bFind) {
                            needReset = true;
                            break;
                        }
                    }
                }
                
                if (needReset) {
                    vm.areaList.forEach(function (item) {
                        if (!item['isAll']) {
                            item.selected = false;
                        } else {
                            item.selected = true;
                        }
                    });
                    vm.areas = [];
                    vm.filterChanged();
                }
            };
            
            //设置常用地区
            vm.setArea = function () {
                areaService.openModal(vm.areaList, function (res) {
                    service.setUserArea(res.map(function (item) {
                        return item.name;
                    })).success(function () {
                        vm.oldAreaList = vm.areaList;
                        vm.areaList = [{
                                isAll : true,
                                name : '全部'
                            }].concat(res);
                        operAfterSetArea();
                    });
                });
            };
            
            //过滤修改事件
            vm.filterChanged = function (event) {

                vm.firstFilter = $cookies.get('firstFilter');
                if (vm.firstFilter === undefined) {
                    var firstFilter = document.getElementById('first-filter');
                    var clientX = event.clientX + 'px';
                    var clientY = event.clientY + 'px';
                    angular.element(firstFilter).css('left',clientX).css('top',clientY);
                    vm.filterTooltip = true;
                    $cookies.put('firstFilter', 'false', {'expires': new Date('January 6, 2119') });
                }


                var quoteTypes = [];
                if (vm.quoteType){
                    quoteTypes = vm.quoteType.map(function (item) {
                        return item.id;
                    });
                    if(quoteTypes.length ===1 && quoteTypes[0] === undefined){
                        quoteTypes = ["GTF","UR2","UR3"];
                    }
                }

                var areas = vm.areas.map(function (item) {
                    return item.name;
                });
                if (areas[0] == "全部") areas = [];

                var direction = '';
                if (vm.selectBoardDirection[0]) {
                    direction = vm.selectBoardDirection[0].value;
                } else {
                    direction = offerService.dataDefine.boardDirection[0].value;
                }

                var bankNatures = [];
                if(vm.institutionTypeList){
                    bankNatures = vm.institutionTypeList.findWhere(function (item) {
                        return item.selected === true;
                    }).map(function (item) {
                        return item.displayName;
                    });
                    if(bankNatures.length ===1 && bankNatures[0] === undefined){
                        bankNatures = [];
                    }
                }

                var fundSizes = [];
                if(vm.bankSizeList){
                    fundSizes = vm.bankSizeList.findWhere(function (item) {
                        return item.selected === true;
                    }).map(function (item) {
                        return item.displayName;
                    });
                    if(fundSizes.length ===1 && fundSizes[0] === undefined){
                        fundSizes = [];
                    }
                }

                var tagCodes = [];
                var tagNames = [];
                if(vm.featureList){
                    tagCodes = vm.featureList.findWhere(function (item) {
                        return item.selected === true;
                    }).map(function (item) {
                        return item.tagCode;
                    });
                    tagNames = vm.featureList.findWhere(function (item) {
                        return item.selected === true;
                    }).map(function (item) {
                        return item.tagName;
                    });
                    if(tagCodes.length ===1 && tagCodes[0] === undefined){
                        tagCodes = [];
                        tagNames = [];
                    }
                }

                tableService.setFilter(quoteTypes, areas, direction, bankNatures, fundSizes, tagCodes, tagNames);
                $scope.$emit('filterChanged', 'inner');
            };

            vm.close = function () {
                vm.filterTooltip = false;
            }
            //重置filter
            $scope.$on('filterReset', function (event, data) {

                //类型
                var quoteTypes = ["GTF","UR2","UR3"];
                vm.quoteTypeList.forEach(function (item) {
                    item.selected = false;
                });
                vm.quoteTypeList[0].selected = true;
                //常用地区
                var areas = [];
                vm.areaList.forEach(function (item) {
                    item.selected = false;
                });
                vm.areaList[0].selected = true;
                //机构类型
                var bankNatures = [];
                vm.institutionTypeList.forEach(function (item) {
                    item.selected = false;
                });
                vm.institutionTypeList[0].selected = true;
                //银行规模
                var fundSizes = [];
                vm.bankSizeList.forEach(function (item) {
                    item.selected = false;
                });
                vm.bankSizeList[0].selected = true;
                //产品特征
                var tagCodes = [];
                var tagNames = [];
                vm.featureList.forEach(function (item) {
                    item.selected = false;
                });
                vm.featureList[0].selected = true;
                tableService.setFilter(quoteTypes, areas, tableService.filter.direction, bankNatures, fundSizes, tagCodes, tagNames);

                //判断是我的报价  还是  搜索
                if (data === 'myPrice') {
                    //如果是我的报价，搜索置空
                    tableService.searchType = '';
                } else if (data === 'mySearch') {
                    //如果是搜索，我的报价置空
                    vm.isSelf = 0;
                    tableService.isSelf = false;
                }
            });
        }]);
    
    
    mainModule.service('innerFilterService', ['$http', 'appConfig', function ($http, appConfig) {
            
            var getUserArea = function () {
                return $http.post('base/init_data');
            }
            
            var setUserArea = function (data) {
                return $http.post('base/setarea', {
                    province : data
                });
            }
            
            this.getUserArea = getUserArea;
            this.setUserArea = setUserArea;
        }]);
    
    
    
    

})(angular);