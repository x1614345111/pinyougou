app.controller("cartController",function ($scope,cartService) {

    $scope.cartList=[{"orderItemList":[{"goodsId":"","num":""}]}];

    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;
                $scope.totalValue=cartService.sum($scope.cartList);
            }
        );
    };


    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();
                }else {
                    alert(response.message);
                }
            }
        );
    };


    //根据当前登录用户名 获取收货地址信息
    $scope.findAddressList=function () {
        cartService.findListByLoginUser().success(
            function (response) {
                $scope.addressList=response;

                for (var i = 0; i < $scope.addressList.length; i++) {
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        return;
                    }
                }
            }
        );
    };

    //收货地址选中效果
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };

    $scope.isSelectAddress=function (address) {
        if(address==$scope.address){
            return true;
        }else {
            return false;
        }
    };//收货地址选中效果结束



    // 选择支付方式，1：微信 2：货到付款
    $scope.order={paymentType:'1'};

    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }


    //提交订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else {
                    alert(response.message);
                }
            }
        );
    }

});