app.controller("baseController",function ($scope) {

    //重新加载
    $scope.reloadList=function () {

        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    //分页插件
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    // 复选框选中的行
    $scope.selectIds=[];

    $scope.updateSelection=function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else {
            var number = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(number);
        }
    };


    // 提取json属性值，返回字符串
    $scope.jsonToString=function (jsonString,key) {

        var json = JSON.parse(jsonString);

        var value = "";

        for (var i = 0; i < json.length; i++) {
            if(i>0){
                value+=",";
            }
            value+=json[i][key];
        }
        return value;
    };


    //从集合中按照key查询对象
    $scope.searchObjectByKey=function (list, key, keyValue) {

        for (var i = 0; i < list.length; i++) {
            if(list[i][key]==keyValue){
                return list[i];
            }
        }

        return null;
    }
});