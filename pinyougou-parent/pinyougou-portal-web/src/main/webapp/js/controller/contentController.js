app.controller('contentController',function ($scope, contentService) {

    //$controller('baseController',{$scope:$scope});//继承
    $scope.contentList=[];

    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        );
    }



    //转发到搜索页面
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});