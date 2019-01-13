 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function (response) {
				$scope.entity=response;
				// 给富文本赋值
                editor.html($scope.entity.goodsDesc.introduction);
                //将数据库查出来的图片字符串转换为json
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//将数据库查出来的扩展属性字符串转换为json
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //将数据库查出来的规格属性字符串转换为json
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //将数据库查出来的sku列表 遍历转换成json
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec)
                }
            }
		);
	};
		//根据规格名称和选项名称 返回是否被勾选
	$scope.checkAttributeValue=function (specName, optionName) {
		var items=$scope.entity.goodsDesc.specificationItems;
		var object=$scope.searchObjectByKey(items,'attributeName',specName);

		if(object==null){
			return false;
		}else {
			if(object.attributeValue.indexOf(optionName)>=0){
				return true;
			}else {
				return false;
			}
		}
    };

	
	//保存 
	$scope.save=function(){
        $scope.entity.goodsDesc.introduction=editor.html();//提取富文本框的值
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					location.href("admin/goods.html");
				}else{
					alert(response.message);
				}
			}		
		);				
	}


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

//上传文件
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if(response.success){
                    $scope.image_entity.url=response.message;//设置文件地址
				}else {
					alert(response.message)
				}
            }
		);
    }


    //图片列表
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};


    $scope.add_image_entity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片列表

    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //一级下拉列表
	$scope.selectItemCat1List=function () {
    	itemCatService.findByParentId(0).success(
    		function (response) {
    			$scope.itemCat1List=response;
            }
		);
    };
	//二级下拉列表
	$scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List=response;
            }
		);
    });

    //三级下拉列表
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List=response;
            }
        );
    });

    //三级列表后，读取模板id
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
            }
		);
    });

    //读取模板id后，更新品牌列表
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
       	typeTemplateService.findOne(newValue).success(
       		function (response) {
				$scope.typeTemplate=response;
				//将字符串转换成json 在html遍历
				$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);

				if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);

                }
            }
		);
        //根据模板id查询规格选项
            typeTemplateService.findSpecList(newValue).success(
                function (response) {
                    $scope.specList=response;
                }
            );
    });

    //将选中规格 加入GoodsVo.goodsDesc.specificationItems
	$scope.updateSpecAttribute=function ($event, name, value) {
		var object = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		alert(object);
		if(object!=null){
			if($event.target.checked){
				object.attributeValue.push(value);
			}else {
				object.attributeValue.splice(object.attributeValue.indexOf(value));

				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice(
						$scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}else {
			$scope.entity.goodsDesc.specificationItems.push(
				{'attributeName':name,'attributeValue':[value]}
			);
		}
    }


    //创建sku列表
    $scope.createItemList=function(){
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
        var items=  $scope.entity.goodsDesc.specificationItems;
        for(var i=0;i< items.length;i++){
            $scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );
        }
    }

    addColumn=function(list,columnName,columnValues){
        var newList=[];//新的集合
        for(var i=0;i<list.length;i++){
            var oldRow= list[i];
            for(var j=0;j<columnValues.length;j++){
                var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }
        return newList;
    }


    $scope.statusList=['未审核','已审核','未通过','关闭'];
	//分类展示
	$scope.itemCatList=[];
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(
			function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id]=response[i].name;
                }

            }
		);
    }



});	
