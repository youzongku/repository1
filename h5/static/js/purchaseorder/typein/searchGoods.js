/**
 * 查询正价商品或赠品的条件
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue', 'zepto', 'dropload', 'iscroll', 'layer', 'picLazyload', 'urlPram',"frozen"], function (Vue, picLazyload, IScroll) {

        // 获取链接参数
        var inputId = getUrlParam("ii");
        var type = getUrlParam("type");
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

        vm.searchCondtion=decodeURIComponent(searchCondtion);

        if(type=='pro'){
            vm.gobackUrl = 'step_2.html?ii='+inputId + '&sc='+encodeURIComponent(encodeURIComponent(vm.searchCondtion))
            vm.searchUrl = 'step_2.html?ii='+inputId
        }else if(type=='gift'){
            vm.gobackUrl = 'step_3.html?ii='+inputId + '&sc='+encodeURIComponent(encodeURIComponent(vm.searchCondtion))
            vm.searchUrl = 'step_3.html?ii='+inputId
        }
    });
})

