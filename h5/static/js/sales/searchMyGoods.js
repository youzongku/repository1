require(['../lib/common'], function (common) {
    require(['vue', 'zepto', 'dropload', 'iscroll', 'layer', 'picLazyload', 'urlPram',"frozen"], function (Vue, picLazyload, IScroll) {

        // 获取链接参数
        isLogin(function (email) {});
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
                    window.location.href = this.searchUrl + '?sc='+encodeURIComponent(encodeURIComponent(vm.searchCondtion));
                }
            }
        });
        vm.searchCondtion=decodeURIComponent(searchCondtion);
        vm.gobackUrl = 'step_2.html?sc='+encodeURIComponent(encodeURIComponent(vm.searchCondtion));
        vm.searchUrl = 'step_2.html';
    });
})

