require(['./lib/common'], function (common) {
    require(['vue','zepto','zepto_cookie','session'], function (Vue,Zepto){
        $.session.remove("email");
        $.session.remove("model");
        $.session.remove("distributorType");
        var vm = new Vue({
            el:'#login-form',
            data:{
                username:'',// 用户名
                pwd:'',// 密码
                loginSuccess:true,// 是否显示登录信息
                loginMsg:'',// 登录信息
                loginMsgStyle:'color: red;text-align: center; width: 100%'// 登录信息样式
            },
            methods:{
                // 登录方法
                login:function(){
                    var username = this.username;
                    var pwd = this.pwd;
                    if(isnull(trim(username))){
                        this.loginSuccess = false;
                        this.loginMsg = '用户名不能为空';
                        return;
                    }
                    if(isnull(trim(pwd))){
                        this.loginSuccess = false;
                        this.loginMsg = '密码不能为空';
                        return;
                    }
                    ajax_post('/member/adminlogin',JSON.stringify({user:username, cipher:pwd}),"application/json",
                        function(res) {
                            console.log(res);
                            var suc = res.success == 'false' ? false : true;
                            vm.loginSuccess = suc;
                            if(!suc){
                                vm.loginMsg = '用户名或密码错误';
                                return;
                            }
                            //记住密码
                            if($("#rem-psd").prop("checked")){
                                //30代表天数
                                $.fn.cookie("username",username,{expires: 30});
                                $.fn.cookie("pwd",pwd);
                            }
                            window.location.href = "/user/index/index.html";
                        }
                    );
                }
            }
        });
        if($.fn.cookie("username")){
            vm.username = $.fn.cookie("username");
            vm.pwd = $.fn.cookie("pwd");
        }
        ajax_get("/member/isaulogin?" + (new Date()).getTime(),null,null,
            function(res) {
                if(res.suc){
                    window.location.href = "/user/index/index.html";
                }
            }
        );
    });    
});
