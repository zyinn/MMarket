/**
 * Created by jiannan.niu on 2016/8/31.
 */
(function (angular) {
    "use strict";

    angular.module("directives").directive("burying", ["qbService", "$state", function (qbService, $state) {

        return {
            restrict: "AC",
            link: function (scope, ele, attrs) {

                ele.on("click", function () {
                    //通过路由判断当前处于的状态
                    var state = $state.current.name;
                    var page;
                    if (state === "app.innerFinancing.index") {
                        page = "MMFI.";
                    } else if (state === "app.offlineFinancing.index") {
                        page = "MMCD.";
                    } else {
                        page = '';
                    }

                    //字符串的截取和拼接
                    var burying = attrs.burying.split(',');
                    var para;
                    if (burying[0]) {
                        para = burying[0] + '';
                    }
                    if (burying[1]) {
                        para += '=' + burying[1];
                    }

                    para = page + para;

                    // console.log("埋点数据：" + para);
                    function onSuccess (data) {
                        // console.log(data)
                    }
                    function onFailure (data) {
                        // console.log(data)
                    }
                    qbService.sendBurying(para, onSuccess, onFailure);
                })

            }
        }
    }])
})(angular);