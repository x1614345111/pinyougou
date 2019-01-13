app.service("cartService",function ($http) {

    this.findCartList=function () {
        return $http.get("../cart/findCartList.do");
    };


    this.addGoodsToCartList=function (itemId,num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    };

    this.sum=function (cartList) {
        var totalValue = {"totalNum":0,"totalMoney":0};
        for (var cart in cartList) {
            for (var index in cartList[cart].orderItemList){
                var orderItem = cartList[cart].orderItemList[index];
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;

            }
        }
        return totalValue;
    };


    this.findListByLoginUser=function () {
       return $http.get("../address/findListByLoginUser.do");
    };


    this.submitOrder=function (order) {
        return $http.post("../order/add.do",order);
    }
});