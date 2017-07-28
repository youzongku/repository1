var layer = undefined;
var laypage = undefined;


function init_attr_man(lay,layp){
	layer = lay;
	laypage = layp;
	init_func();
}

function init_func(){
	 //  添加属性-弹出窗
    $('.attributePop').click(function(){
    	create();
    });
    //  修改属性-弹出窗
    $('.modificationPop').click(function(){
        layer.open({
            type: 1,
            title: "添加属性",
            area: ['440px', '360px'],
            shadeClose: true, //点击遮罩关闭
            content: $(".attribute-pop"),
            btn: ['保存','取消'],
            scrollbar: false
        });
    });
    $('.attribute-body-del').click(function(){
        var a=$(this).siblings('.attribute-body-inp');
        if(a.val()!=''){
            a.val('');
        }
    });	
}


function create(){
	var cHtml = '<div class="attribute-pop">'+
    			'<div><span class="attribute-pop-l"><em class="red"></em>属性名称：</span>'+
            	'<span class="attribute-pop-r"><input type="text"/></span></div>'+
            	'<div><span class="attribute-pop-l"><em class="red"></em>属性类型：</span>'+
	            '<span class="attribute-pop-r">'+
                '<select name="selectType">'+
                '<option>请选择属性类型</option>'+
                '<option value="text" >文本</option>'+
                '<option value="radio" >单选</option>'+
                '<option value="checkbox" >多选</option>'+
                '<option value="date" >时间</option>'+
                '</select></span></div>'+
    			'<div><span class="attribute-pop-l"><em class="red"></em>Key值：</span>'+
            	'<span class="attribute-pop-r">'+
                '<input type="text"/></span></div>'+
    			'<div><span class="attribute-pop-l" style="vertical-align: top;">备选值：</span>'+
            	'<span class="attribute-span-type">'+
                '<span id="spanType_text" class="attribute-pop-r">'+
				'<input class="attribute-pop-inp" type="text" disabled="disabled"/>'+
				'</span>'+
				'<span id="spanType_radio" class="attribute-pop-select" style="display: none">'+
				'<p class="attribute-pop-head">'+
				'<span>设为默认</span>'+
				'<btutton class="searchButton" id="add_radio">添加</btutton>'+
				'</p>'+
				'<p>'+
				'<input type="radio" name="radio"/>'+
				'<input type="text" class="attribute-body-inp"/>'+
				'<span class="attribute-body-del">x</span>'+
				'</p>'+
				'<p>'+
				'<input type="radio" name="radio"/>'+
				'<input type="text" class="attribute-body-inp"/>'+
				'<span class="attribute-body-del">x</span>'+
				'</p>'+
				'</span>'+
				'<span id="spanType_checkbox" class="attribute-pop-select" style="display: none">'+
				'<p class="attribute-pop-head">'+
				'<btutton class="searchButton" id="add_checkbox">添加</btutton>'+
				'</p>'+
				'<p>'+
				'<input type="checkbox" name="checkbox"/>'+
				'<input type="text" class="attribute-body-inp"/>'+
				'<span class="attribute-body-del">x</span>'+
				'</p>'+
				'<p>'+
				'<input type="checkbox" name="checkbox"/>'+
				'<input type="text" class="attribute-body-inp"/>'+
				'<span class="attribute-body-del">x</span>'+
				'</p>'+
				'</span>'+
				'<span id="spanType_date" class="attribute-pop-r" style="display: none">'+
				'<input readonly="true" type="text" class="attribute-pop-inp" onclick=\'laydate({istime: true, format: "YYYY-MM-DD hh:mm:ss"})\'>'+
				'</span>'+
				'</span>'+
				'</div>'+
				'</div>';
	layer.open({
	    type: 1,
	    title: "添加属性",
	    area: ['440px', '360px'],
	    shadeClose: true, //点击遮罩关闭
	    content: cHtml,
	    btn: ['保存','取消'],
	    scrollbar: false,
	    success:function(){
	    	$(".attribute-pop").on("change","select[name='selectType']",function(){
	    		var $spanType = $("#spanType_"+$(this).val());
		        $spanType.parent().children("span").hide();
		        $spanType.show();
	    	});
	    	//属性类型查询
	    	ajax_get('/store/attr/types?'+new Date().getTime(),'','',function(res){
	    		console.log(res)
	    	});


	    },
	    yes:function(index){
	    	/*var param = {
	    		{
				  "attrName":"库存",
				  "attrKey":"stock",
				  "attrDesc":"商品库存字段",
				  "status":1,
				  "attrType":"radio",
				  "typeId":1,
				  "isNull":true,
				  "isShow":true,
				  "createUser":"admin"
				  }
	    	};
	    	ajax_post('/store/attr/create',JSON.stringify(param),'application/json',function(data){

	    	});*/

	    }
	});
}

        
