var app = angular.module("pinyougou",[]);

//html代码过滤器
app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);
