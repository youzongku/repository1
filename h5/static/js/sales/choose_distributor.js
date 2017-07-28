require(['../lib/common'], function (common) {
    require(['vue','zepto','dropload','session',"frozen"], function (Vue,Zepto){
        //未登录不能访问
        isLogin(function (email) {});
        var vm = new Vue({
            el:'#body_distributor',
            data:{
                searchInputText:'',
                currPage:1,
                nextPage:0,
                totalPage:0,
                notType:3,
                distributors:[],
                disAccount:'',
                disMode:'',
                comsumerType:"",
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
                        $("#"+oldPid).css("backgroundColor","");
                    }
                    $("#"+newPid).css("backgroundColor","#ececec");
                },
                selectDistributor:function(pid,email,disMode,comsumerType,nick){
                    console.log(pid+","+email+","+disMode+","+nick);
                    this.changeSelectStyle(this.pid,pid);
                    this.pid=pid;
                    this.disAccount=email;
                    this.disMode=disMode;
                    this.comsumerType=comsumerType;
                    this.nick=nick;
                    $.session.set("email",this.disAccount);
                    $.session.set("model",this.disMode);
                    $.session.set("distributorType",this.comsumerType);
                },
                goStep2: function(){
                    if(isnull(this.disAccount)){
                        var dia = $.dialog({
                            title: '温馨提示',
                            content: '请选择分销商！',
                            button: ["确认"]
                        });
                        return;
                    }
                    window.location.href='step_2.html';
                }
            }
        });
        // 监视输入查询条件
        vm.$watch('searchInputText', function (val) {
            vm.currPage = 1;
            vm.nextPage = 0;
            vm.totalPage = 0;
            $("#distributors_list").find('div').remove();
            getDistributors();
        });

        //初始化加载分销商
        getDistributors();
        function getDistributors(){
            vm.distributors=[];
            var param = getSearchParams();
            ajax_post('/member/relatedMember',JSON.stringify(param),"application/json",
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
        };

        //检查之前选择的分销商
        function checkBeforeChoose(){
            var email = $.session.get("email");
            vm.disAccount = email;
        }
        checkBeforeChoose();

        // 下拉加载
        function loadMore(){
            var isload = false;
            if (vm.currPage < vm.totalPage) {
                isload = true;
            }
            if (isload) {
                $('#distributors_list').dropload({
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
        };

        function getSearchParams(){
            vm.nextPage = vm.nextPage +1;
            return {
                currPage: vm.nextPage,
                search: trim(vm.searchInputText),
                pageSize: 10,
                notType: vm.notType
            };
        };
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
