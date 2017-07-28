require(['./lib/common'], function (common) {
    require(['vue'], function (Vue){

        // 全局注册组件，tag 为 my-component
        Vue.component('my-component', {
            template: '<div>A custom component!</div>'
        });
        new Vue({
            el:"#example",
            data:{
                msgInComponent:'this is msgInComponent'
            }
        });


        var vm = new Vue({
            el:'#app',
            data:{
                username:'小明',
                newTodo:'',
                todos:[
                    { text: 'Learn JavaScript' },
                    { text: 'Learn Vue.js' },
                    { text: 'Build Something Awesome' }
                ],
                object:{
                    name:'小明',
                    age:20,
                    country:'中国'
                },
                message:"this is message",
                showSpan:false,
                showSpanStyle:'color:red',
                showSpan1:false,
                showSpan1Style:'color:red',
                oneCheckedVal:false,
                checkedVal:[],
                picked:'',
                selected_value:''
            },
            methods:{
                clickMe:function(){
                    alert("username is "+this.username);
                },
                addTodo: function () {
                    var text = this.newTodo.trim()
                    if (text) {
                        this.todos.push({ text: text })
                        this.newTodo = ''
                    }
                },
                removeTodo: function (index) {
                    this.todos.splice(index, 1)
                },
                reverseMessage:function(){
                    this.message = this.message.split('').reverse().join('');
                }
            }
        });
    });
});
