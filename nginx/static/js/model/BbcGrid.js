// define(['jquery','../js/jqGrid/js/jquery.jqGrid.min','../js/jqGrid/plugins/bootstrap','css!/js/jqGrid/plugins/bootstrap.min.css','css!/js/jqGrid/css/ui.jqgrid.css'], function () {
define('BbcGrid',['jquery','/js/lib/jqGrid/js/jquery.jqGrid.min.js'], function () {
    function BbcGrid() {
    }
    BbcGrid.prototype = {
        initTable : function(obj,setting) {
            obj.jqGrid(setting);
        }
    }
    return BbcGrid;
});


