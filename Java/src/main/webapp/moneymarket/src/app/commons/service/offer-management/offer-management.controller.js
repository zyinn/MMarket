(function (angular) {
    'use strict';
    angular.module('services')
        .controller('offerManageController', offerManageController);
    
    //offerManagementController定义
    
    offerManageController.$inject = ['$uibModalInstance', '$scope', '$http', '$timeout', 'commonService', 'offerService', 'tabNavigatorService', 'appConfig', 'offerConfirm', 'quoteQuery', 'tableService'];
    
    function offerManageController($uibModalInstance, $scope, $http, $timeout, commonService, offerService, tabNavigatorService, appConfig, offerConfirm, quoteQuery, tableService) {
        var vm = this;
        
        //业务类型初始化
        vm.typeList = [
            { "name": "同存", "enum": 'IBD', "direction": 'IN' },
            { "name": "保本", "enum": 'GTF', "direction": 'OUT' },
            { "name": "非保本R2", "enum": 'UR2', "direction": 'OUT' },
            { "name": "非保本R3", "enum": 'UR3', "direction": 'OUT' }
        ];
        
        //期限枚举
        vm.periodList = [
            { displayOrder: 1, "period": "1D", "enum": "T1D" },
            { displayOrder: 2, "period": "7D", "enum": "T7D" },
            { displayOrder: 3, "period": "14D", "enum": "T14D" },
            { displayOrder: 4, "period": "1M", "enum": "T1M" },
            { displayOrder: 5, "period": "2M", "enum": "T2M" },
            { displayOrder: 6, "period": "3M", "enum": "T3M" },
            { displayOrder: 7, "period": "6M", "enum": "T6M" },
            { displayOrder: 8, "period": "9M", "enum": "T9M" },
            { displayOrder: 9, "period": "1Y", "enum": "T1Y" }
        ];
        vm.lockedPeriodList = angular.copy(vm.periodList);

        vm.featureList = offerService.featureList.slice(1);

        vm.selectedTypeList = [];
        
        vm.boardDirectionList = offerService.dataDefine.boardDirection;

        //过期时间计算和格式化
        function getExpiredDateString(value) {
            if (!value) value = 7;
            
            var expiredDate = new Date();
            
            var now = new Date();
            
            expiredDate.setTime(now.getTime() + (value - 1) * 24 * 3600 * 1000);
            expiredDate.setHours(23);
            expiredDate.setMinutes(0);
            expiredDate.setSeconds(0);
            
            return expiredDate.format("yyyy-MM-dd HH:mm:ss");
        }

        //产生报价
        function boardFactory(type) {
            return {
                type: angular.copy(type),
                periodList: [],
                featureList: angular.copy(vm.featureList),
                tagName: [],
                spaceList: angular.copy(vm.periodList),
                remark: undefined,
                active: offerService.dataDefine.activeDefine.true,
                expiredDate: 7,
                expiredDateString: getExpiredDateString(7),
                boardDir: vm.boardDirectionList.findItem(function (e) {
                    return e.value === type.direction;
                })
            };
        }

        //设置Tabindex
        function setTabIndex() {
            if (!vm.selectedTypeList) return;
            
            vm.selectedTypeList.forEach(function (item, index) {
                if (!item.periodList) return;
                
                item.periodList.forEach(function (item1, index1) {
                    item1.tabIndex = index1 * 2;
                });
            });
            
            commonService.safeApply($scope);
        }

        //初始化数据
        var originOffer;
        function initData(res) {
            
            originOffer = res;
            
            res.forEach(function (item, index) {
                var board = {
                    id: item.id,
                    seqNo: item.sequence,
                    
                    type: vm.typeList.findItem(function (e) {
                        return e.enum === item.quoteType;
                    }),
                    
                    // 取得的有效报价active均为1
                    active: offerService.dataDefine.activeDefine.true,
                    
                    //memo
                    remark: item.memo,
                    
                    // 默认有效时间7天
                    expiredDate: item.valideDays ? item.valideDays : 7
                };
                
                board.expiredDateString = getExpiredDateString(board.expiredDate);
                
                board.boardDir = vm.boardDirectionList.findItem(function (e) {
                    return e.value === item.direction;
                });
                
                //type
                //for (var j in vm.typeList) {
                //    if (vm.typeList[j].enum == item.quoteType) {
                //        selectedType.type = vm.typeList[j];
                //        vm.typeList[j].selected = true;
                //        break;
                //    }
                //}
                
                //quoteDetailsDtos
                var periodList = angular.copy(vm.periodList);
                var featurelist = angular.copy(vm.featureList);

                board.periodList = [];
                board.spaceList = [];
                board.featureList = [];
                board.tagName = [];
                if (item.tags) {
                    featurelist.forEach(function (e) {
                        item.tags.forEach(function (ee) {
                            if (e.tagCode === ee.tagCode) {
                                e.selected = true;
                            }
                        })
                    });
                }

                if (featurelist) {
                    board.featureList = featurelist;
                    board.tagName = board.featureList.findWhere(function (e) {
                        return e.selected === true;
                    }).sort(function (x, y) {
                        return x.displayOrder - y.displayOrder;
                    });
                }

                for (var j in periodList) {
                    if (typeof periodList[j] === 'function') continue;
                    
                    var exist = false;
                    var resDetailList = item.quoteDetailsDtos;
                    
                    for (var k in resDetailList) {
                        if (typeof resDetailList[k] === 'function') continue;
                        
                        if (periodList[j].enum == resDetailList[k].limitType) {
                            exist = true;
                            
                            periodList[j].value = resDetailList[k].price;
                            if (resDetailList[k].quantity) {
                                periodList[j].amount = resDetailList[k].quantity;
                                board.showAmount = true;
                            }
                            
                            periodList[j].id = resDetailList[k].id;
                            
                            board.periodList.push(periodList[j]);
                        }
                    }
                    if (!exist) {
                        board.spaceList.push(periodList[j]);
                    }
                }
                
                // console.log("selectedType: " + JSON.stringify(selectedType));
                if (board.type.enum !== 'IBD') {
                    board.spaceList = board.spaceList.findWhere(function (item) {
                        return item.enum !== "T7D" && item.enum !== "T14D";
                    });
                }
                vm.selectedTypeList.push(board);
                
                commonService.safeApply($scope);
            });

        };

        //TAB键切换tabindex
        vm.onKeydownTable = function (event) {
            if (!event || !event.target) return;
            
            if (event.target.nodeName !== "BUTTON" || event.target.className.indexOf("badge") < 0) return;
            
            if (event.keyCode === 9) {
                event.cancelBubble = true;
                tabNavigatorService.findNextTd(event, function (nextElem) {
                    var ctrl = angular.element(nextElem).controller('editable');
                    
                    if (!ctrl) {
                        $scope.$emit("onTabNavigating", nextElem);
                        return;
                    }
                    
                    ctrl.isEditing = true;
                    $timeout(function () {
                        $(nextElem).addClass('is-editing');
                        $(nextElem).find('input').focus();
                    });
                    ctrl.textChanged();
                });
            }
        };

        // 选择报价方向
        vm.onClickBoardDirection = function (event, board) {
            // console.log("onClickBoardDirection");
            
            if (!event || !event.target) return;
            if (event.target.nodeName === "BUTTON") {
                var index = vm.boardDirectionList.indexOfItem(function (item) {
                    return item.displayName === event.target.innerHTML;
                });
                
                board.boardDir = vm.boardDirectionList[index];
            }
        };
        
        //选定业务类型
        vm.onClickQuoteType = function (event,item) {
            if (!event || !event.target) return;
            
            if (event.target.nodeName === "A") {
                var scope = angular.element(event.target).scope();
                
                if (scope) vm.selectType = scope.item;
            }

            if (item.enum !== 'IBD') {
                vm.periodList = angular.copy(vm.lockedPeriodList);
                vm.periodList.splice(1,2);
            } else {
                vm.periodList = angular.copy(vm.lockedPeriodList);
            }
        };

        //报价单类型Filter
        vm.boardListFilter = function (item, index, list) {
            return vm.selectType.enum === item.type.enum && item.active !== offerService.dataDefine.activeDefine.false;
        };

        //报价单有效性Filter
        vm.periodFilter = function (item, index, list) {
            return item.active !== offerService.dataDefine.activeDefine.false;
        };
        
        //删除业务表格
        vm.deleteType = function (board) {
            //board.type.selected = false;
            //vm.selectedTypeList.splice(index, 1);
            if (board.id) {
                board.active = offerService.dataDefine.activeDefine.false;
                board.periodList.forEach(function (e) {
                    e.active = offerService.dataDefine.activeDefine.false;
                });
            } else {
                vm.selectedTypeList = vm.selectedTypeList.findWhere(function (e) {
                    return e.$$hashKey !== board.$$hashKey;
                });
            }
            setTabIndex();
        };
        
        //删除期限
        vm.deletePeriod = function (board, period) {
            period.active = offerService.dataDefine.activeDefine.false;
            
            period.value = null;
            period.amount = null;
            
            board.spaceList.push(period);
            board.spaceList.sort(function (x, y) {
                return x.displayOrder - y.displayOrder;
            });
            setTabIndex();
        };
        
        //添加期限
        vm.addPeriod = function (board, period) {
            var targetIndex = board.spaceList.findIndex(function (e) {
                return e.enum === period.enum;
            });
            board.spaceList.splice(targetIndex, 1);
            
            var target = board.periodList.findItem(function (e) {
                return e.enum === period.enum;
            });
            
            if (target) target.active = offerService.dataDefine.activeDefine.true;
            else board.periodList.push(period);
            
            board.periodList.sort(function (x, y) {
                return x.displayOrder - y.displayOrder;
            });
            setTabIndex();
        };
        
        //添加一组报价
        vm.onClickAddQuote = function () {
            var addingBoard = boardFactory(vm.selectType);
            
            var seqNoTemp = 0;
            
            vm.selectedTypeList.forEach(function (item, index) {
                if (item.seqNo > seqNoTemp) seqNoTemp = item.seqNo;
            });
            
            addingBoard.seqNo = seqNoTemp + 1;
            
            // console.log("addingBoard: " + JSON.stringify(addingBoard));
            
            vm.selectedTypeList.push(addingBoard);
            
            setTabIndex();
        };
        
        //显示数量
        vm.showAmount = function (board) {
            board.showAmount = true;
        };
        
        //隐藏数量
        vm.hideAmount = function (board) {
            board.showAmount = false;
            board.periodList.forEach(function (item, index) {
                delete item.amount;
            });
        };
        
        // 显示具体撤销时间
        vm.onChangedExpiredDate = function (event, item) {
            if (item.expiredDate <= 0 || item.expiredDate > 30) {
                item.expiredDate = '';
            };
            item.expiredDateString = getExpiredDateString(item.expiredDate);
        };

        //选特征
        vm.setFeature = function (item,board) {
            item.selected = !item.selected;
            board.tagName = board.featureList.findWhere(function (e) {
                return e.selected === true;
            }).sort(function (x, y) {
                return x.displayOrder - y.displayOrder;
            });
        };
        
        //保存
        vm.save = function () {
            
            vm.selectedTypeList.forEach(function(item, index) {
                item.periodList = item.periodList.findWhere(function(e) {
                    return e.active !== offerService.dataDefine.activeDefine.false;
                });
            });

            vm.selectedTypeList = vm.selectedTypeList.findWhere(function (e) {
                return e.id || e.periodList.length !== 0;
            })
            
            offerConfirm.confirm(vm.selectedTypeList.findWhere(function (e) {
                return e.active !== offerService.dataDefine.activeDefine.false;
            }), function () {
                
                var data = vm.selectedTypeList.map(function (item) {
                    var tagCodes = [];
                    item.featureList.findWhere(function (e) {
                        return e.selected === true;
                    }).map(function (e) {
                        return e.tagCode;
                    }).forEach(function (item) {
                        var a = new Object();
                        a.tagCode = item;
                        tagCodes.push(a);
                    });

                    return {
                        id: item.id,
                        quoteType: item.type.enum,
                        // direction : item.type.direction,
                        direction: item.boardDir.value,
                        memo: item.remark,
                        tags: tagCodes,
                        quoteDetailsDtos: item.periodList.map(function (item1) {
                            var result = {
                                id: item1.id,
                                limitType: item1.enum,
                                price: parseFloat(item1.value),
                                quantity: item1.amount,
                                active: item.active
                            };
                            
                            if (item1.active === offerService.dataDefine.activeDefine.false) {
                                result.active = item1.active;
                            }
                            
                            return result;
                        }),
                        sequence: item.seqNo,
                        source : "QB",
                        valideDays: item.expiredDate
                        
                    }
                });
                
                for (var i = 0; i < originOffer.length; i++) {
                    var stillExist = false;
                    for (var j = 0; j < data.length; j++) {
                        if (originOffer[i].quoteType == data[j].quoteType) {
                            stillExist = true;
                            break;
                        }
                    }
                    if (!stillExist) {
                        delete originOffer[i].quoteUserId;
                        originOffer[i].quoteDetailsDtos = [];
                        data.push(originOffer[i]);
                    }
                }

                if (data instanceof Array && data.length === 0) {
                    vm.cancel();
                } else {
                    saveQuote(data).success(function (response) {
                        $uibModalInstance.close(data);
                    });
                }

            });
        };
        
        //取消
        vm.cancel = function () {
            $uibModalInstance.dismiss();
        };

        //初始化页面
        var initView = function () {
            //已选择业务类型初始化
            if (quoteQuery.result && quoteQuery.result.length > 0) {
                var offer_data = quoteQuery.result[0].offer_data;
                //如果没有报价单返回，初始化出四种空白报价类型各一条
                if (offer_data.length === 0) {
                    vm.typeList.forEach(function (item) {
                        vm.selectType = item;
                        if (vm.selectType.enum !== 'IBD') {
                            vm.periodList = angular.copy(vm.lockedPeriodList);
                            vm.periodList.splice(1,2);
                        } else {
                            vm.periodList = angular.copy(vm.lockedPeriodList);
                        }
                        vm.onClickAddQuote();
                    });
                }
                initData(offer_data);
            }

            vm.selectType = vm.typeList[0];

            setTabIndex();

            //debugger;
            //console.log(vm.selectedTypeList.toString());
        }();
        
        //获取报价单
        function getQuote() {
            return $http.post('quote/get');
        }
        
        //保存报价单
        function saveQuote(data) {
            return $http.post('quote/save', {
                offer_data: data
            });
        }

    }

})(angular);