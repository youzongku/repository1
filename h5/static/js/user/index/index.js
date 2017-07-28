/**
 * 菜单页面
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue','zepto'], function (Vue){
        //未登录不能访问
        isLogin(function (email) {});
    	var vm = new Vue({
            el:'#body_index',
            data:{
               menus:[],
               wa_count:0
            },
            methods:{
                load:function(event){
                    var target = event.target;
                    var n;
                    if(target.tagName === 'A'){
                        n = $(target).attr("position");
                    }else if(target.tagName === 'LI'){
                        n = $(target).find("a").attr("position");
                    }
                    n = parseInt(n);
                    ajax_post(
                        "/member/checkMenuAuthority",
                        JSON.stringify({"position": n}),
                        "application/json",
                        function (response) {
                            if (response.success == true) {
                                if (response.isFlag == true) {
                                    switch(n){
                                        case 2://微仓订单-微仓订单查询
                                            window.location.href = "/purchaseorder/polist.html";
                                            break;
                                        case 3://销售发货-终端订单查询
                                            window.location.href = "/sales/list.html";
                                            break;
                                        case 28://业务员-微仓订单查询
                                            window.location.href = "/purchaseorder/polist.html";
                                            break;
                                        case 29://业务员-发货单查询
                                             window.location.href = "/sales/list.html";
                                            break;     
                                        case 30 ://微仓订单-微仓订单录入
                                            window.location.href = "/purchaseorder/typein/step_1.html";
                                            break;
                                        case 31 ://业务员-微仓订单录入
                                            window.location.href = "/purchaseorder/typein/step_1.html";
                                            break; 
                                        case 32://销售发货-发货单录入
                                            window.location.href = "/sales/create/step_1.html";
                                            break;
                                        case  33://业务员-发货单录入
                                            window.location.href = "/sales/create/step_1.html";
                                            break;      
                                    }
                                   
                                }else{
                                    var dia = $.dialog({
                                        title: '温馨提示',
                                        content: '您没有该操作权限，请联系管理员',
                                        button: ["确认"]
                                    });
                                    dia.on("dialog:action",function(e){
                                       window.location.href = "/user/index/index_1.html";
                                    });
                                }
                            }else{
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: '用户未登录，请登录',
                                    button: ["确认"]
                                });
                                dia.on("dialog:action",function(e){
                                    //未登录
                                    window.location.href = "/user/index/index_1.html";
                                });
                               
                            }
                        }
                    );
                }
            	
            }
        });
        // 获取商品分类
        ajax_get('/member/getRoleMenuOfUser?tp=true',null,null,
            function(res) {
            	if(res.success){
            		vm.menus = res.roleMenus;
            	}
            }
        );
		ajax_post("/sales/manager/ctslodr", JSON.stringify({ status: 3 }), "application/json",
			function(response) {
				vm.wa_count = response;
			},
			function(xhr, status) {
				layer.msg('获取待审核的销售发货订单数出错', {icon : 2, time : 1000});
			}
		);
        vm.$nextTick(
           function(){
                (function(e){
                    for(var _obj=document.getElementById(e.id).getElementsByTagName(e.tag),i=-1,a;a=_obj[++i];){
                        a.onclick = function(){
                            var ul = this.nextSibling;
                            if(!ul){return false;}
                            ul = ul.nextSibling; if(!ul){return false;}
                            if(e.tag != 'a'){ ul = ul.nextSibling; if(!ul){return false;} }
                            for(var _li=this.parentNode.parentNode.childNodes,n=-1,li;li=_li[++n];){
                                if(li.tagName=="LI"){
                                    for(var _ul=li.childNodes,t=-1,$ul;$ul=_ul[++t];){
                                        switch($ul.tagName){
                                            case "UL":
                                                $ul.className = $ul!=ul?"" : ul.className?"":"off";
                                                break;
                                            case "A":
                                                $ul.className = $ul!=this?"" : this.className?"":"off";
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                })({id:'menu',tag:'a'});
           }
        ); 
    });
});
