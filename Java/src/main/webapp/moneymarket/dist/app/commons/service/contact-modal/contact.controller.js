!function(){"use strict";function t(t,n,i,o){var e=this;e.quoteData=i,e.openQM=function(t){o.openQM(t.qb_id)},e.institutionOffer=function(){n.close({id:i.institutionId,name:i.primeListInstitutionName})},e.cancel=function(){n.dismiss()}}angular.module("services").controller("contactModalController",t),t.$inject=["$scope","$uibModalInstance","quoteData","qbService"]}();