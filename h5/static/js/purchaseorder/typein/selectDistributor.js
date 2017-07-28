/**
 * 选择分销商
 * Created by Administrator on 2016/9/12.
 */
require(['../../lib/common'], function (common) {
    require(['vue','zepto','dropload',"frozen"], function (Vue){

        // 获取链接参数
        var inputId = getUrlParam("ii");
        console.log('inputId='+inputId);

        var vm = new Vue({
            el:'#body_ele',
            data:{
                searchInputText:'',
                email:'',
                currPage:1,
                nextPage:0,
                totalPage:0,
                distributors:[],
                inputId:'',
                pid:'',
                disAccount:'',
                disMode:'',
                comsumerType:'',
                nick:''
            },
            filters:{
                setStyle:function(email,nick){
                    if(email == this.disAccount){
                        this.nick = nick
                        return 'background-color:#ececec'
                    }else{
                        return '';
                    }
                }
            },
            methods:{
                changeSelectStyle : function(oldPid,newPid) {
                    if(isnotnull(oldPid)){
                        $("#"+oldPid).css("backgroundColor","")
                    }
                    $("#"+newPid).css("backgroundColor","#ececec")
                },
                selectDistributor:function(pid,email,disMode,comsumerType,nick){
                    console.log(pid+","+email+","+disMode+","+comsumerType+","+nick)
                    this.changeSelectStyle(this.pid,pid);
                    this.pid=pid;
                    this.disAccount=email;
                    this.disMode=disMode;
                    this.comsumerType=comsumerType;
                    this.nick=nick
                },
                goStep2: function(){
                    if(isnull(this.disAccount)){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择分销商！',
                            button: ["确认"]
                        });
                        return
                    }

                    // 保存数据
                    var params = {
                        disAccount : this.disAccount,
                        // inputUser: this.email,
                        disMode: this.disMode,
                        comsumerType:this.comsumerType
                    };
                    if(!isnull(this.inputId)){
                        params['inputId'] = this.inputId;
                    }
                    console.log(params);
                    ajax_post("/purchase/ti/main/addUpdt", JSON.stringify(params), "application/json",
                        function(data) {
                            // 用户未登录
                            if(data.code == 101){
                                var dia = $.dialog({
                                    title: '温馨提示',
                                    content: data.msg,
                                    button: ["确认"]
                                });
                                dia.on("dialog:action",function(e){
                                    window.location.href = "/login.html";
                                });
                                return
                            }
                            this.inputId = data.inputId;
                            //分销商 模式(1,电商 2，经销商 3 ,商超 4，进口专营)
                            window.location.href='step_2.html?ii='+this.inputId;
                        },
                        function(xhr, status) { console.log("error--->" + status); }
                    );
                }
            }
        });
        // 监视输入查询条件
        vm.$watch('searchInputText', function (val) {
            vm.currPage = 1;
            vm.nextPage = 0;
            vm.totalPage = 0;
            $("#ditributors_section").find('div').remove();
            initLoad();
        });

        // 登录用户
        isLogin(function (email) {
            vm.email = email;
        });

        // 检查是否有之前的输入数据
        function checkInputBefore(){
            var url = "/purchase/ti/Inputorder/simpleinput";
            // var param = {"inputUser":vm.email};
            ajax_post(url, null, null,
                function(response) {
                    // 用户未登录
                    if(response.code == 101){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: response.msg,
                            button: ["确认"]
                        });
                        dia.on("dialog:action",function(e){
                            window.location.href = "/login.html";
                        });
                        return
                    }
                    var input = response.input;
                    if(isnotnull(input)){
                        vm.inputId = input.id;
                        vm.disAccount = input.disAccount;
                        vm.disMode = input.disMode;
                        vm.comsumerType = input.disType;
                        vm.inputUser = input.inputUser;
                    }
                }
            );
        }

        checkInputBefore();
        initLoad();

        // 初始化加载
        function initLoad(){
            vm.distributors=[];
            var params = getSearchParams();
            ajax_post('/member/relatedMember',JSON.stringify(params),"application/json",
                function(res) {
                    if (!res.suc) {
                        // 登录失败
                    }
                    bindData(res.data);
                    loadMore();
                },function(xhr, type) {
                    // 即使加载出错，也得重置
                    me.resetload();
                }
            );
        }

        // 下拉加载
        function loadMore(){
            var isload = false;
            if (vm.currPage < vm.totalPage) {
                isload = true;
            }
            if (isload) {
                console.log("----------load  more  data -------------");
                $('#ditributors_section').dropload({
                    scrollArea: window,
                    loadDownFn: function(me) {
                        var params = getSearchParams();
                        ajax_post('/member/relatedMember',JSON.stringify(params),"application/json",
                            function(res) {
                                if (!res.suc) {
                                    // 登录失败
                                }
                                bindData(res.data);

                                if (vm.currPage == vm.totalPage) {
                                    // 锁定
                                    me.isLockDown = true;
                                    // 无数据
                                    me.noData();
                                }
                                // 每次数据加载完，必须重置
                                me.resetload();
                            },function(xhr, type) {
                                // 即使加载出错，也得重置
                                me.resetload();
                            }
                        );
                    }
                });
            }
        }

        // 获取查询参数
        function getSearchParams(){
            vm.nextPage = vm.nextPage+1;
            return {
                role: 2,
                currPage: vm.nextPage,
                pageSize: 10,
                search: trim(vm.searchInputText),
                email:vm.email
            };
        }

        // 绑定数据
        function bindData(data){
            vm.currPage = data.currPage;
            vm.totalPage = data.totalPage;
            var list = data.list;
            // 追加数据
            for(var i= 0,length=list.length;i<length;i++){
                vm.distributors.push(list[i]);
            }
        }

    });
});
