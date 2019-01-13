app.service("loginService",function ($http) {

    this.loginName=function () {
        return $http.post("../loginName/name.do");
    }
});