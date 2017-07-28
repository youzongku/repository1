/**
 * 添加正价商品
 * Created by Administrator on 2016/9/12.
 */
require(['../lib/common'], function (common) {
    require(['vue', 'zepto', 'dropload', 'iscroll', 'layer', 'picLazyload', 'urlPram'], function (Vue, picLazyload, IScroll) {

        // 获取链接参数
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
                    window.location.href = this.searchUrl + '&sc='+encodeURIComponent(encodeURIComponent(vm.searchCondtion))
                }
            }
        });
        //字符串编码
        vm.searchCondtion = decodeURIComponent(searchCondtion);
        vm.gobackUrl = 'polist.html?ss='+ status + "&sc="+ vm.searchCondtion;
        vm.searchUrl = 'polist.html?ss='+ status;
    });
})

