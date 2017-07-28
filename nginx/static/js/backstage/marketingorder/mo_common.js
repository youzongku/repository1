/**
 * Created by Administrator on 2016/12/26.
 */
//详情展示
function showMOSymbol(type, e) {
    if (type == 1) {
        $(e).css("display", "none");
        $(e).next().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "table-row");
    } else {
        $(e).css("display", "none");
        $(e).prev().css("display", "inline-block");
        $(e).parent().parent().next().css("display", "none");
    }
}

/*鼠标经过tbody tr变色效果*/
function mo_onmouseover(obj){
    $(obj).css('background','#eee').siblings().css('background','#fff');
}

/**
 * 选中当前行效果
 * @param trObj
 */
function clickThisTr(trObj){
    $(trObj).click()
}