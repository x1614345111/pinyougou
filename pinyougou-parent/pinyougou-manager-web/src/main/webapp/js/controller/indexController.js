app.controller("loginController",function ($scope,loginService) {


    $scope.showName=function () {
        loginService.name().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        );
    }
});