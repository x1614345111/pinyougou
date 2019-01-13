app.controller('itemController', function ($scope,$http) {

    $scope.num = 1;
    //数量操作
    $scope.addNum = function (x) {
        $scope.num = parseInt($scope.num);
        $scope.num += parseInt(x);
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }


    $scope.specificationItems = {};//记录用户选择的规格

    //用户选择规则
    $scope.selectSpecification = function (name, value) {
        $scope.specificationItems[name] = value;

        searchSku();//更新sku
    }

    //判断规格是否被选中
    $scope.isSelected = function (name, value) {
        if ($scope.specificationItems[name] == value) {
            return true;
        }
        return false;
    }

    //加载默认sku
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    }

    //匹配两个对象是否相等
    matchObject = function (map1, map2) {

        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }


        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }
        return true;
    }


    //根据用户所选规格更新sku
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];

                return;
            }
        }
    }


    //加入购物车
    $scope.addToCart = function () {
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id + '&num=' + $scope.num, {'withCredentials': true}).success(
            function (response) {
                if (response.success) {
                    location.href = 'http://localhost:9107/cart.html';//跳转到购物车页面
                } else {
                    alert(response.message);
                }
            });
    }
});