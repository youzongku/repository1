require.config({
    baseUrl : "../js/",
    paths : {
        "jquery" : "lib/jquery-1.11.3.min",
        "treecore" : "lib/jquery.ztree.core-3.5",
        "excheck" : "lib/jquery.ztree.excheck-3.5"
    },
    shim : {
        "treecore" : {
            exports : "",
            deps : ["jquery"]
        },
        "excheck" : {
            exports : "",
            deps : ["jquery","treecore"]
        }
    }

});

require(["jquery", "treecore", "excheck"], function ($, treecore, excheck) {
    var zTree;
    var setting = {
        checkable : true,
        view: {
            dblClickExpand: false,
            showLine: true,
            selectedMulti: false,
            showIcon: showIconForTree
        },
        check: {
            enable: true
        },
        checkType : { "Y": "p", "N": "s" },
        async: {
            enable: true,
            url:"/member/getTree",
            autoParam:["id"]
        },
        callback: {
            beforeClick: function(treeId, treeNode) {
                var zTree = $.fn.zTree.getZTreeObj("tree");
                if (treeNode.isParent) {
                    zTree.expandNode(treeNode);
                    return false;
                }
            }
        }
    };
    var zNodes = [
        {id:0, name:"所有栏目和按钮",isParent:true}
    ];
    $.fn.zTree.init($("#tree"), setting, zNodes);

});
function showIconForTree(treeId, treeNode) {
    return !treeNode.isParent;
};