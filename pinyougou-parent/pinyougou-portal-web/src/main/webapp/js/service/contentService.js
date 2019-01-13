app.service('contentService',function ($http) {
    
    this.findByCategoryId=function (categoryId) {
        return $http.post('content/findByCategoryId.do?categoryId='+categoryId);
    }
});