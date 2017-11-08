/**
 * Created by jiannan.niu on 2016/9/6.
 */
(function (angular) {
    'use strict';

    angular.module('moneyMarketApp').service('marketService',marketService);
    marketService.$inject = ['$http', 'appConfig', 'userInterceptor'];
    function marketService ($http, appConfig, userInterceptor) {
        var vm = this;
        //本地调试使用
        // appConfig.service_root = 'http://127.0.0.1:8888/money_market/';

        vm.getData = function (direction,quoteType) {
            return $http.post(appConfig.service_root + 'inner/queryPriceMatrix', {
                "direction": direction,
                "quoteType": quoteType
            });
        };

        vm.getChart = function (direction, quoteType, matrixBankNature, matrixFundSize, timePeriod, numberOfPreviousDays) {
            return $http.post(appConfig.service_root + 'inner/queryPriceTrend', {
                "direction": direction,
                "quoteType": quoteType,
                "matrixBankNature": matrixBankNature,
                "matrixFundSize": matrixFundSize,
                "timePeriod": timePeriod,
                "createTime": new Date().format("yyyy-MM-dd"),
                "numberOfPreviousDays": numberOfPreviousDays
            });
        };
    };

})(angular);