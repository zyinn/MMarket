(function () {
    'use strict';
    //tableService定义
    angular.module('moneyMarketApp')
		.service('tableService', tableService);
    
    tableService.$inject = ['$http', 'appConfig'];

    //同业理财和线下资金
    var financingType;

    //方向
    var direct;

    var quoteType;

    function tableService($http, appConfig) {
        var service = this;
        
        var pageSize = 200;
        
        //初始化参数信息
        var initOptions = function () {
            
            //过滤选项
            service.filter = { direction: direct, quote_type: quoteType, trust_type: false, province: [],bankNatures: [],fundSizes: [],tagCodes:[],orderByPeriod:null};
            service.filter.tagNames = [];
            //初始化有效报价和我的报价
            service.isSelf = false;
            
            //搜索数据类型和搜索数据
            service.keyword = "";
            service.searchType = "";

        };
        
        initOptions();
        
        //设置过滤数据
        var setFilter = function (quoteTypes, areas, direction, bankNatures, fundSizes, tagCodes, tagNames) {

            if (financingType == 'inner') {
                service.filter.quote_type = quoteTypes;
            } else if (financingType == 'offline') {
                service.filter.trust_type = quoteTypes;
                service.filter.quote_type = ["IBD"];
            }
            service.filter.province = areas;

            service.filter.direction = direction;

            service.filter.bankNatures = bankNatures;

            service.filter.fundSizes = fundSizes;

            service.filter.tagCodes = tagCodes;

            service.filter.tagNames = tagNames;

        };

        var setOrderByPeriod = function (sort) {
            service.filter.orderByPeriod = sort;
        }

        var setOrderSeq = function (seq) {
            service.filter.orderSeq = seq;
        }
        
        var setOptions = function (displayType, page ,sort) {

            if(service.isSelf == 0){
                service.isSelf = false;
            } else if(service.isSelf == 1){
                service.isSelf = true;
            }

            if(service.filter.trust_type == 0){
                service.filter.trust_type = false;
            } else if(service.filter.trust_type == 1){
                service.filter.trust_type = true;
            }

            var options = {
                areas : service.filter.province,
                primeQuotesOnly : displayType,
                pageNumber : page,
                pageSize : pageSize,
                privateQuotesOnly : service.isSelf,
                direction: service.filter.direction,
                bankNatures: service.filter.bankNatures,
                fundSizes: service.filter.fundSizes,
                tagCodes: service.filter.tagCodes,
                institutionIdList:[],
                quoteUserId:"",
                memo:"",
                orderByPeriod: service.filter.orderByPeriod,
                orderSeq: service.filter.orderSeq
            };
            
            if (financingType == 'inner') {
                options.quoteTypes = service.filter.quote_type;
            } else if (financingType == 'offline') {
                options.quoteTypes = ['IBD'];
                options.custodianQualification = service.filter.trust_type;
            }

            if (service.searchType === "INSTITUTE"){
                options.institutionIdList[0] = service.keyword;
            } else if(service.searchType === "CONTACT"){
                options.quoteUserId = service.keyword;
            } else if(service.searchType === "MEMO"){
                options.memo = service.keyword;
            } else if (service.searchType === '') {
                options.institutionIdList = [];
                options.quoteUserId = '';
                options.memo = '';
            }
            
            return options;

        };
        
        
        //搜索关键字
        function searchKeyword(keyword) {
            return $http.post(financingType + '/search_preview', {
                keyword : keyword
            });
        }
        
        function setFinancingType(type) {
            financingType = type;
            service.financingType = type;
            if (type === 'inner'){
                direct = 'OUT';
                quoteType = ["GTF","UR2","UR3"];
            } else if (type === 'offline'){
                direct = 'IN';
                quoteType = ["IBD"];
            }
        }
        
        function getQbOfferList(page) {
            
            var options = setOptions(true, page);
            
            return $http.post(financingType + '/queryMmQuotes', options);
        }
        
        function getMarketOfferList(page) {
            
            var options = setOptions(false, page);
            
            return $http.post(financingType + '/queryMmQuotes', options);
        }
        
        //webSocket连接
        function connectWebSocket() {
            
            var url = appConfig.ws_root + '/money_market/websck/mm';

            // var url = "http://" + host + socketPrefix +  '/sockjs/websck/mm';
            return new WebSocket(url);
        }
        
        service.setFilter = setFilter;
        service.initOptions = initOptions;
        service.setOrderByPeriod = setOrderByPeriod;
        service.setOrderSeq = setOrderSeq;
        service.setFinancingType = setFinancingType;
        service.searchKeyword = searchKeyword;
        service.getQbOfferList = getQbOfferList;
        service.getMarketOfferList = getMarketOfferList;
        service.connectWebSocket = connectWebSocket;
    };

})();