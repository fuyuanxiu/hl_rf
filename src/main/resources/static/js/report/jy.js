/**
 * 库存条码交易明细
 */
var pageCurr;
$(function() {

    layui.use(['table', 'laydate'], function(){
        var table = layui.table
            ,form = layui.form,element = layui.element,laydate = layui.laydate;

        //var sdate = new Date(new Date().toLocaleDateString());
        var sdate = getNextDate(-1);
      
        //var edate = new Date((new Date() / 1000 + 86400 * 0) * 1000);
        var edate = getCurDate(0);

        tableIns=table.render({
            elem: '#unitList'
            ,url:context+'/report/getJYList'
            ,method: 'get' //默认：get请求
            ,where:{stime:sdate,
                etime:edate}
            ,cellMinWidth: 80
            ,limit:20
            ,page: true,
            request: {
                pageName: 'page' //页码的参数名称，默认：page
                ,limitName: 'rows' //每页数据量的参数名，默认：limit
            },
            parseData: function (res) {
            	if(res.status == 1){
            		return {
                   	 "code": res.status,//code值为200表示成功
                       "count": 0,
                       "msg":res.msg,
                       "data":[]
                   }
            	}
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
                ,{field:'BILL_NO', title:'单号', width:'15%', align:'center'}
                ,{field:'ITEM_BARCODE', title:'物料条码', width:'20%', align:'center'}
                ,{field:'ITEM_NO', title:'物料编码', width:'12%', align:'center'}
                ,{field:'ITEM_DESCRIPTION', title:'物料描述',width:'8%', align:'center'}
                ,{field:'QUANTITY', title:'物料数量', width:'8%', align:'center'}
                ,{field:'ITEM_UNIT', title:'单位',width:'6%', align:'center'}
                ,{field:'LOT_NO', title:'批次', width:'10%', align:'center'}
                ,{field:'TO_DEPOTNO', title:'入库仓库', width:'10%', align:'center'}
                ,{field:'FROM_DEPOTNO', title:'出库仓库', width:'10%', align:'center'}
                ,{field:'BILL_TYPE_NAME', title:'单据类型', width:'8%', align:'center'}
                ,{field:'TRANSACTION_NAME', title:'事务类型', width:'10%', align:'center'}
                ,{field:'CREATE_BY', title:'操作人', width:'8%', align:'center'}
                ,{field:'CREATE_DATE', title:'操作日期', width:'15%', align:'center'}
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

         //开始日期
         var insStart = laydate.render({
             elem: '#test-laydate-start'
             ,type: 'datetime'
             ,format: 'yyyy-MM-dd HH:mm:ss'
            	 ,value:sdate
             ,done: function(value, date){
                 //更新结束日期的最小日期
 //            insEnd.config.min = lay.extend({}, date, {
 //              month: date.month - 1
 //            });

                 //自动弹出结束日期的选择器
                 insEnd.config.elem[0].focus();
             }
         });

         //结束日期
         var insEnd = laydate.render({
             elem: '#test-laydate-end'
             ,type: 'datetime'
             ,format: 'yyyy-MM-dd HH:mm:ss'
            ,value: edate
             ,done: function(value, date){
                 //更新开始日期的最大日期
 //            insStart.config.max = lay.extend({}, date, {
 //              month: date.month - 1
 //            });
             }
         });
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
            keyword:obj.field.keyword,
            stime:obj.field.stime,
            etime:obj.field.etime
            ,lineno:obj.field.lineno
            ,pmodel:obj.field.pmodel
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


