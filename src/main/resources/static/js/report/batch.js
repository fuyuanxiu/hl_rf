/**
 * 权限列表
 */
$(function() {
    //初始化treegrid 页面表格
    layui.config({
        base: context+'treegrid/'
    }).use(['laytpl', 'treegrid'], function () {
        var laytpl = layui.laytpl,
            treegrid = layui.treegrid;
        treegrid.config.render = function (viewid, data) {
            var view = document.getElementById(viewid).innerHTML;
            return laytpl(view).render(data) || '';
        };

        var treeForm=treegrid.createNew({
            elem: 'permTable',
            view: 'view',
            data: { rows: permList },
            parentid: 'parentId',
            singleSelect: false
        });
        treeForm.build();

    });


});

//关闭弹框
function close(){
    layer.closeAll();
}