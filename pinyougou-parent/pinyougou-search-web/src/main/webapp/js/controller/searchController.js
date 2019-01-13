app.controller('searchController',function (searchService, $scope,$location) {


    //定义搜索对象
    $scope.searchMap={"keywords":"","category":"","brand":"","spec":{}
    ,"price":"","sort":"","sortField":"","pageNo":1,"pageSize":40};


    $scope.search=function () {
        $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo) ;
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
                buildPageLabel();
            }
        );
    };

    //构建分页标签(totalPages为总页数)
    buildPageLabel=function(){
        $scope.pageLabel=[];//新增分页栏属性
        var maxPageNo= $scope.resultMap.totalPages;//得到最后页码
        var firstPage=1;//开始页码
        var lastPage=maxPageNo;//截止页码

        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        if($scope.resultMap.totalPages> 5){  //如果总页数大于5页,显示部分页码
            if($scope.searchMap.pageNo<=3){//如果当前页小于等于3
                lastPage=5; //前5页

                $scope.firstDot=false;//前面没点
            }else if( $scope.searchMap.pageNo>=lastPage-2  ){//如果当前页大于等于最大页码-2
                firstPage= maxPageNo-4;		 //后5页

                $scope.lastDot=false;//后边没点
            }else{ //显示当前页为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点

        }
        //循环产生页码标签
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    };


    //根据页码查询
    $scope.queryByPage=function(pageNo){
        //页码验证
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    };

    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    };

    //判断当前页是否未最后一页
    $scope.isEndPage=function(){
        if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    };





    //添加面包屑
    $scope.addSearchItem=function(key,value){
        if(key=='category' || key=='brand' || key=='price'){    //如果点击的是分类或者是品牌
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }

        $scope.search();
    };

    //删除面包屑
    //移除复合搜索条件
    $scope.removeSearchItem=function(key){
        if(key=="category" ||  key=="brand" || key=='price'){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();
    }

    //排序
    $scope.sortSearch=function (sort, sortField) {

        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;

        $scope.search();
    }


    //隐藏品牌列表
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
        if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
            return true;
        }
        }
        return false;
    }


    //接收首页转发来的搜索参数
    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }

});