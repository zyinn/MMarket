(function (angular) {
    'use strict';
    
    var injectedFun = ['$scope', '$timeout', 'tabNavigatorService', 'commonService',
        function ($scope, $timeout, tabNavigatorService, commonService) {
            
            var $ctrl = this;
            var timer;
            
            var dataDefine = {
                tooltip: {
                    remark: "",
                    price: "请输入您的价格，不确定价格时可不输入",
                    amount: "请输入您的数量，不确定数量时可不输入"
                }
            };
            
            function cancelShow() {
                if ($ctrl.keepOpenTooltip !== "true") $ctrl.showTooltip = false;
                $timeout.cancel(timer);
            };
            
            // Add by Wei Lai on 2016/06/07
            // 迁移 tab or enter
            $ctrl.keydown = function (e) {
                if (!e) return;
                
                function doInputNavigating() {
                    $ctrl.onBlur(e);
                    
                    tabNavigatorService.findNextTd(e, function (nextElem) {
                        var ctrl = angular.element(nextElem).find('editable-input').controller('editableInput');
                        
                        if (!ctrl) {
                            var oldCtrl = angular.element(nextElem).controller('editable');
                            
                            if (oldCtrl && oldCtrl.beginEdit) {
                                $ctrl.isEditing = false;
                                oldCtrl.beginEdit({ target: nextElem[0] });
                                return;
                            } else {
                                $scope.$emit("onTabNavigating", nextElem);
                                return;
                            }
                        }
                        
                        $ctrl.isEditing = false;
                        ctrl.beginEdit({ target: nextElem[0] });
                    });
                };
                
                function rollbackEditing() {
                    $ctrl.isEditing = false;
                    // rollback editing
                    $ctrl.editingValue = $ctrl.value;
                    cancelShow();
                    return;
                };
                
                switch (e.keyCode) {
                    // Tab
                    case 9:
                        e.cancelBubble = true;
                        doInputNavigating();
                        break;
                    // Enter
                    case 13:
                        e.cancelBubble = true;
                        // commit editing
                        $ctrl.value = $ctrl.editingValue;
                        doInputNavigating();
                        break;
                    // Esc
                    case 27:
                        e.cancelBubble = true;
                        rollbackEditing();
                        break;
                    // Up
                    case 38:
                        e.cancelBubble = true;
                        //联盟报价时按箭头上可输入OFR，箭头下输入BID
                        if ($ctrl.isalliance === 'true' && $ctrl.direction === 'OUT') {
                            e.cancelBubble = true;
                            $ctrl.editingValue = 'OFR';
                        } else if ($ctrl.isalliance === 'true' && $ctrl.direction === 'IN') {
                            e.cancelBubble = true;
                            $ctrl.editingValue = 'BID';
                        }
                        break;
                    default:
                        break;
                };
            };
            
            $ctrl.onBlur = function (event) {
                
                console.log('onBlur');
                
                // commit editing
                $ctrl.value = $ctrl.editingValue;
                
                $ctrl.isEditing = false;
                cancelShow();
            };
            
            $ctrl.beginEdit = function (event) {
                $ctrl.isEditing = true;
                
                var component = $(event.target).parent();
                $timeout(function () {
                    if (component) {
                        component.find('input').focus();
                    }
                });

                // showTooltip();
                $ctrl.showTooltip = true;
            };
            
            $ctrl.$onInit = function () {
                if ($ctrl.valueType) $ctrl.toolTipText = dataDefine.tooltip[$ctrl.valueType];
                
                $ctrl.__defineGetter__('editingValue', function () { return this.val });
                $ctrl.__defineSetter__('editingValue', function (data) {
                    
                    if (!data) {
                        this.val = data;
                        return;
                    }
                    
                    var abs = undefined;
                    
                    switch ($ctrl.vaildRule) {
                        case 'length':
                            if (data.length > 200) {
                                this.val = (data + "").substring(0, 200);
                            } else {
                                this.val = data;
                            }
                            break;
                        case 'int':
                            abs = Math.abs(data);
                            if (!/^\d+$/.test(data) || abs > 99999999 || abs === 0) {
                                data = (data + "").substring(0, data.length - 1);
                                this.val = data;
                            } else {
                                this.val = data;
                            }
                            break;
                        case 'number':
                            if ((data + "") === 'OFR' || (data + "") === 'BID') {
                                this.val = data + "";
                                return;
                            }
                            
                            var reg = /^\-?\d*(\.\d{0,4})?$/;
                            abs = Math.abs(data);
                            if (!reg.test(data) || abs > 100 || (abs === 0 && data.length >= 3 && (data + "").indexOf('.') === -1)) {
                                this.val = (data + "").substring(0, data.length - 1);
                            } else if ((data + "").charAt(0) === '0' && (data + "").charAt(1) === '0') {
                                this.val = (data + "").substring(1, data.length);
                            } else if ((data + "").charAt(0) === '-' && (data + "").charAt(1) === '0' && (data + "").charAt(2) === '0') {
                                this.val = '-' + (data + "").substring(2, data.length);
                            } else if ((data + "").charAt(0) === '.') {
                                this.val = '0' + (data + "").substring(0, data.length);
                            } else if ((data + "").charAt(0) === '-' && (data + "").charAt(1) === '.') {
                                this.val = '-0.' + (data + "").substring(2, data.length);
                            } else {
                                this.val = data;
                            }
                            break;
                        default:
                            this.val = data;
                            break;
                    }
                });
                
                if ($ctrl.keepOpenTooltip === "true") $ctrl.showTooltip = true;
                $ctrl.editingValue = $ctrl.value;
            };

        }];
    
    
    //angular.module('directives').directive('editable', function () {
    //    return {
    //        restrict : 'C',
    //        templateUrl : 'app/commons/directive/editable.directive.html',
    //        scope : {
    //            value : "=ngModel",
    //            remark : "@",
    //            price : "@",
    //            amount : "@",
    //            isbeginediting: "@",
    //            isalliance: "@",
    //            direction: "="
    //        },
    //        controller : injectedFun,
    //        bindToController : true,
    //        controllerAs : 'vm'
    //    };
    //});
    
    
    angular.module('components').component('editableInput', {
        templateUrl: 'app/commons/component/editable-input.template.html',
        transclude: true,
        bindings: {
            value : "<",
            
            //remark : "@",
            //price : "@",
            //amount : "@",
            valueType: "@",
            vaildRule: "@",
            keepOpenTooltip: "@",
            
            isalliance: "@",
            direction: "@"
        },
        controller : injectedFun
        
    });

})(window.angular);
