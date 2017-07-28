/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue', 'zepto', 'dropload', 'iscroll', 'layer', 'picLazyload', 'urlPram',"frozen"], function (Vue, picLazyload, IScroll) {

        // 获取链接参数
        isLogin(function (email) {});
        var status = getUrlParam("ss");
        var searchCondtion = getUrlParam("sc");
        if(isnull(searchCondtion)) searchCondtion=''

        var vm = new Vue({
            el: '#body_ele',
            data: {
                searchUrl: '',
                gobackUrl:'',
                searchCondtion:''
            },
            methods:{
                search:function(){
                    window.location.href = this.searchUrl + '&sc='+vm.searchCondtion
                }
            }
        });
        vm.searchCondtion = searchCondtion;
        vm.gobackUrl = 'list.html?ss='+ status + "&sc="+ vm.searchCondtion;
        vm.searchUrl = 'list.html?ss='+ status;
    });
})

