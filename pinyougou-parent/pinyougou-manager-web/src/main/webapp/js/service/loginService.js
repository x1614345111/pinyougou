app.service("loginService",function ($http) {

    this.name=function () {
        return $http.post("../login/name.do");
    }

});