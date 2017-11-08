(function(){
	'use strict';
	angular.module('services')
		.service('qbService',qbService);

		function qbService(){

			var openQbApi = function(requestStr,onSuccess,onFailure){

				if(window.cefQuery){
					window.cefQuery({
						request : requestStr,
						onSuccess : function(response){
							if(typeof onSuccess == 'function'){
								onSuccess(response);
							}
							console.log('调用QB成功：' + response);
						},
						onFailure : function(error_code, error_message){
							if(typeof onFailure == 'function'){
								onFailure(error_message);
							}
							console.log('调用QB失败：' + error_message);
						}
					});
				}else{
					if(typeof onFailure == 'function'){
						onFailure();
					}
				}

			};

			var openQM = function(userId, onSuccess, onFailure){
				var request = '["open_page",[{"name":"qm_window","userid":"'+ userId +'"}]]';
				openQbApi(request, onSuccess, onFailure);
			};

			var openStore = function(institutionId, onSuccess, onFailure){
				var request = '["open_page",[{"name":"flagship_store","underwriteid":"' +institutionId+ '"}]]';
				openQbApi(request, onSuccess, onFailure);
			};

			var getUserData = function(onSuccess,onFailure){
				
				 	var request = '["req_cache",[{"data":"UserInfo"}]]';
				 	openQbApi(request, onSuccess, onFailure);
			}

            var sendBurying = function(para, onSuccess, onFailure){
                var request = '["open_page",[{"name":"write_event","para":"'+ para +'"}]]';
                openQbApi(request, onSuccess, onFailure);
            };

			this.openQM = openQM;
			this.openStore = openStore;
			this.getUserData = getUserData;
            this.sendBurying = sendBurying;

		}

})();