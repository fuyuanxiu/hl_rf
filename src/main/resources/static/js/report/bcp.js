/**
 * 半成品条码库存明细
 */
var pageCurr;
$(function() {

    layui.use(['table', 'laydate'], function(){
        var table = layui.table
            ,form = layui.form,element = layui.element,laydate = layui.laydate;

        tableIns=table.render({
            elem: '#unitList'
            ,url:context+'/report/getBCPList'
            ,method: 'get' //默认：get请求
            ,cellMinWidth: 80
            ,limit:20
            ,page: true,
            request: {
                pageName: 'page' //页码的参数名称，默认：page
                ,limitName: 'rows' //每页数据量的参数名，默认：limit
            },
            parseData: function (res) {
                // 可进行数据操作
                return {
                    "count": res.data.total,
                    "msg":res.msg,
                    "data":res.data.rows,
                    "code": res.status //code值为200表示成功
                }
            },
            cols: [[
                {type:'numbers'}
                ,{field:'ITEM_BARCODE', title:'批次条码', width:'20%', align:'center'}
                ,{field:'ITEM_NO', title:'产品编码', width:'12%', align:'center'}
                ,{field:'LOT_NO', title:'批次', width:'12%', align:'center'}
                ,{field:'QUANTITY', title:'条码数量', width:'8%', align:'center'}
                ,{field:'OLDQTY', title:'剩余数量', width:'8%', align:'center'}
                ,{field:'MOCODE', title:'状态', width:'8%', align:'center'}
                ,{field:'LIFNO', title:'投料单', width:'12%', align:'center'}
                ,{field:'PROC_NO', title:'工序', width:'8%', align:'center'}
                ,{field:'LINE_NO', title:'线体', width:'8%', align:'center'}
                ,{field:'FBZ', title:'备注', width:'8%', align:'center'}
                ,{field:'ITEM_NAME', title:'产品名称', width:'8%', align:'center'}
                ,{field:'ITEM_MODEL', title:'产品规格', width:'15%', align:'center'}
                ,{field:'CREATE_DATE', title:'入库日期', width:'15%', align:'center'}
                ,{field:'PROC_NAME', title:'工序名称', width:'8%', align:'center'}
                ,{field:'HH', title:'在线时长', width:'8%', align:'center'}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
            }
        });

        //监听搜索框
        form.on('submit(doSearch)', function(data){
            //alert(data.elem.getAttribute('data-url'));
            //重新加载table
            load(data);
            return false;
        });

//         //开始日期
//         var insStart = laydate.render({
//             elem: '#test-laydate-start'
//             ,type: 'datetime'
//             ,format: 'yyyy-MM-dd HH:mm:ss'
//             ,done: function(value, date){
//                 //更新结束日期的最小日期
// //            insEnd.config.min = lay.extend({}, date, {
// //              month: date.month - 1
// //            });
//
//                 //自动弹出结束日期的选择器
//                 insEnd.config.elem[0].focus();
//             }
//         });
//
//         //结束日期
//         var insEnd = laydate.render({
//             elem: '#test-laydate-end'
//             ,type: 'datetime'
//             ,format: 'yyyy-MM-dd HH:mm:ss'
//             ,done: function(value, date){
//                 //更新开始日期的最大日期
// //            insStart.config.max = lay.extend({}, date, {
// //              month: date.month - 1
// //            });
//             }
//         });
//
//         //监听工具条
//         table.on('tool(unitTable)', function(obj){
//             var data = obj.data;
//             if(obj.event === 'approval'){
//                 //审核
//                 layer.alert('审核',function(){
//                     layer.closeAll();
//                 });
//             } else if(obj.event === 'edit'){
//                 //编辑
//                 editWork(data,data.id);
//             } else if(obj.event === 'file'){
//                 //附件
//                 layer.alert('附件',function(){
//                     layer.closeAll();
//                 });
//             } else if(obj.event === 'del'){
//                 //删除
//                 // delUser(data,data.id,data.bsName);
//                 delWork(data,data.id,data.bsName);
//             }
//         });

    });
});


//重新加载表格（搜索）
function load(obj){
    //重新加载table
    tableIns.reload({
        where: {
            keyword:obj.field.keyword
        }
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

//重新加载表格（全部）
function loadAll(){
    //重新加载table
    tableIns.reload({
        page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function indexOf(arr, str){
    // 如果可以的话，调用原生方法
    if(arr && arr.indexOf){
        return arr.indexOf(str);
    }

    var len = arr.length;
    for(var i = 0; i < len; i++){
        // 定位该元素位置
        if(arr[i] == str){
            return i;
        }
    }

    // 数组中不存在该元素
    return -1;
}