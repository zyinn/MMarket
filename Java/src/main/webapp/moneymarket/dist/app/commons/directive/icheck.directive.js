!function(){"use strict";angular.module("directives").directive("iCheck",["$timeout",function(e){return{require:"ngModel",link:function(e,i,t,n){var r;return r=t.value,e.$watch(t.ngModel,function(e){$(i).iCheck("update")}),$(i).iCheck({checkboxClass:"icheckbox_minimal-aero",radioClass:"iradio_minimal-aero"}).on("ifChanged",function(a){if("checkbox"===$(i).attr("type")&&t.ngModel&&e.$apply(function(){return n.$setViewValue(a.target.checked)}),"radio"===$(i).attr("type")&&t.ngModel)return e.$apply(function(){return n.$setViewValue(r)})})}}}])}();