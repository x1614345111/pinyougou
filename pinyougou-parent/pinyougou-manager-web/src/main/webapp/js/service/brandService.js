app.service("brandService",function ($http) {

    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    };

    this.findPage=function (page,size) {
        return $http.get("../brand/findPage.do?page="+page+"&size="+size);
    };

    this.findOne=function (id) {
        return $http.post("../brand/findOne.do?id="+id);
    };

    this.add=function (entity) {
        return $http.post("../brand/save.do",entity);
    };

    this.update=function (entity) {
        return $http.post("../brand/update.do",entity);
    };

    this.del = function (selectList) {
        return $http.post("../brand/delete.do?ids="+selectList);
    };

    this.searchEntity = function (page,size,searchEntity) {
        return $http.post("../brand/search.do?page="+page+"&size="+size,searchEntity);
    };

    this.selectOptionList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
});