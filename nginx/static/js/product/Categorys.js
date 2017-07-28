define("Categorys", ["jquery","common","layer","laypage"], function($, common, layer,laypage){
    function Categorys(){
    }
    Categorys.prototype = {
        show : function(param){
             Categorys.prototype.islogin(function(email){
                var distributionMode = undefined; 
                var islogin = false;
                if(email){
                    islogin = true;
                    Categorys.prototype.getMode(function(dis){
                        distributionMode = dis;
                    });
                }
                 param.model = distributionMode;
                $.ajax({
                    url: "/product/api/getSkuList",
                    type: "post",
                    contentType: "application/json",
                    data: JSON.stringify(param),
                    async: true,
                    success: function (data) {
                        $("#product-list").empty();
                        $("#pagination_product").empty();
                        $("#total").text(1);
                        if(data.result && data.result.length > 0) {
                            var htmlCode = "";
                            var disPrice = 0;
                            $.each(data.result,function(i,item){
                                disPrice = item.disPrice;
                                 if(item.isSpecial){
                                   disPrice = item.specialSale;
                                }

                                if(i % 5 == 4) {
                                    htmlCode += '<li style="margin-right: 0;">';
                                } else {
                                    htmlCode += '<li>';
                                }
                                htmlCode += '<div class="product-top"> <div>';
                                htmlCode += '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" target="_blank">';
                                htmlCode += '<img src="'+urlReplace(item.imageUrl, item.csku,"",180,180,100)+'" alt="" />';
                                htmlCode += '</a>';
                                htmlCode += '</div> <p class="bar-code">国际条码：' + item.interBarCode + '</p>';
                                htmlCode += '<input type="hidden" id="sku" value="'+ item.csku +'">';
                                htmlCode += '<input type="hidden" id="warehouseId" value="'+ item.warehouseId +'">';
                                htmlCode += '<input type="hidden" id="publicImg" value="'+ item.imageUrl +'">';
                                htmlCode += '<a href="../product/product-detail.html?sku=' + item.csku + '&warehouseId=' + item.warehouseId + '" target="_blank">';
                                htmlCode += '<p class="product-title">' + item.ctitle + '</p>';
                                htmlCode += '</a>';
                                htmlCode += '<p class="product-price">¥' + disPrice + '</p>';
                                htmlCode += '</div> <div class="product-bottom"> <span class="bor-r-org" id="buynow_new">';
                                htmlCode += '立即采购</span> <span id="add-to-cart-button_new">加入购物车</span> </div></li>';
                            });
                            $("#product-list").html(htmlCode);
                            Categorys.prototype.init_pagination(data.totalPage,data.currPage,param);
                        }
                    }
                });
            });
        },
        category : function(vcId,flagid){
            $.ajax({
                url: "/product/api/vcQuery",
                type: "post",
                contentType: "application/json",
                data: JSON.stringify({"parentId":vcId}),
                async: false,
                success: function (data) {
                    $(".brands").empty();
                    var htmlCode = "";
                    if(data && data.length > 0) {
                        $.each(data,function(i,item){
                            if (item.vcId == flagid) {
                                htmlCode += '<a class=\"selectedCategory\" data-level="'+item.level+'" href=\"javascript:void(0);\" tag="'+item.vcId+'">'+item.name+'</a>';
                            } else {
                                htmlCode += '<a data-level="'+item.level+'" href=\"javascript:void(0);\" tag="'+item.vcId+'">'+item.name+'</a>';
                            }
                        });
                    } else {
                        htmlCode += "<a href=\"javascript:void(0);\">暂无详细分类</a>";
                    }
                    $(".brands").prepend(htmlCode);
                }
            });
        },
        parentCategory : function(vcId){
            var parentvcId;
            var flagid;
             $.ajax({
                url: "/product/api/vcQueryParent?vcId="+vcId,
                type: "get",
                contentType: "application/json",
                async: false,
                success: function (data) {
                    $(".crumbs-l").empty();
                    var cateHtml = '<a href="/">首页</a>';
                    for(var i = data.length-2; i >= 0 ;i--){
                        if (data[i].level == 4) {
                            parentvcId = data[i].parentId;
                            flagid = data[i].vcId;
                            continue;
                        }
                        if (data[i].vcId == vcId){
                            cateHtml += '<i class="arrows-r-g"></i>'+
                                '<a href="javascript:void(0);" class="currCate selectedCategory" data-cid="'+ data[i].vcId+'">' + data[i].name + '</a>';
                        } else {
                            cateHtml += '<i class="arrows-r-g"></i>'+
                                '<a href="javascript:void(0);" class="currCate" data-cid="'+ data[i].vcId+ '">' + data[i].name + '</a>';
                        }
                    }
                    $(".crumbs-l").append(cateHtml);
                    if (parentvcId) {
                        Categorys.prototype.category(parentvcId,flagid);
                    }
                }
            });
        },
        cateDetail : function(vcId){
            $.ajax({
                url: "/product/api/cateDetail?vcId=" + vcId,
                type: "get",
                contentType: "application/json",
                async: false,
                success: function (data) {
                    if(data) {
                        $(".currCate").text(data.name);
                    }
                }
            });
        },
        init_pagination : function(total,currPage,param) {
            $("#pagination_product").empty();
            laypage({
                cont: 'pagination_product',
                pages: total,
                curr: currPage,
                groups: 5,
                skin: 'yahei',
                first: '首页',
                last: '尾页',
                prev: '上一页',
                next: '下一页',
                skip: true,
                jump: function(obj, first) {
                    //first一个Boolean类，检测页面是否初始加载。非常有用，可避免无限刷新。
                    if(!first){
                        param.currPage = obj.curr;
                        Categorys.prototype.show(param);
                    }
                }
            });
        },
        islogin : function(call) {
            var url = "../member/isnulogin?" + (new Date()).getTime();
            $.ajax({
                url: url,
                type: "get",
                dataType: "json",
                async: false,
                success: function (data) {
                    if (data.suc) {
                        call(data.user.email);
                    } else {
                        call("");
                    }
                }
            });
        },
        getMode : function(call){
            $.ajax({
                url: "/member/infor?" + Math.random(),
                type: "get",
                dataType: "json",
                async: false,
                success: function (response) {
                    if (response.id != null && response.id != 'null' && response.id != '' && response.id != undefined) {
                        call(response.distributionMode);
                    }
                    else {
                        call("");
                    }
                }
            });
        },
        getWarehouse: function() {
            ajax_get("/inventory/queryWarehouse", "", "application/json",
                function(data) {
                    $("#searchWarehouse").html('');
                    var content = '';
                    if (data && data.length > 0) {
                        for(var i = 0;i < data.length; i++) {
                            content += '<li warehouseName="'+ data[i].warehouseName + '" tag="'+ data[i].warehouseId + '">' + data[i].warehouseName + '</li>';
                        }
                        $("#searchWarehouse").html(content);
                    }
                },
                function(xhr, status) {

                }
            );
        }
    };
    return Categorys;

});
