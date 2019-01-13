app.controller("brandController",function ($scope,brandService,$controller) {

    $controller("baseController",{$scope:$scope});

    $scope.findAll=function () {
        //查询所有
        brandService.findAll().success(
            function (response) {
                $scope.list=response;
            }
        );
    };




    //分页查询
    $scope.findPage=function (page,size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        );
    };
    // 点击修改 回显操作
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    };


    //保存
    $scope.save=function () {
        var object = null;
        if($scope.entity.id!=null){
            object= brandService.update($scope.entity);
        }else {
            object=brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else {
                    alert(response.message)
                }
            }
        );
    };

    //删除功能
    $scope.del=function () {
        brandService.del($scope.selectIds).success(
            function (response) {
                if(response.success){
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            }
        )
    };

    // 条件查询+分页
    $scope.searchEntity={};
    $scope.search=function (page,size) {
        brandService.searchEntity(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        );
    }


});