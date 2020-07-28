/**
 * 注液结果查询
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
            //,url:context+'/report/getComList'
            	,data:[] 
            ,method: 'get' //默认：get请求
            ,where:{prc_name:'APP_ZY_DATA',
            	pname:'',
            	stime:sdate,
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
                ,{field:'BARCODE', title:'电池条码', width:190, align:'center'}
                ,{field:'SCANTIME', title:'扫描时间', width:150, align:'center'}
                ,{field:'RESULT', title:'注液结果', width:100, align:'center'}
                ,{field:'FWEIGHT', title:'前称重',width:100, align:'center'}
                ,{field:'FW_TIME', title:'前称重时间', width:150, align:'center'}
                ,{field:'FW_RESULT', title:'前称重结果', width:100, align:'center'}
                ,{field:'LWEIGHT', title:'后称重',width:80, align:'center'}
                ,{field:'LW_TIME', title:'后称重时间', width:150, align:'center'}
                ,{field:'FILLINGNUM_PV', title:'参考注液量', width:80, align:'center'}
                ,{field:'FILLINGNUM_SV', title:'实际注液量', width:80, align:'center'}
                ,{field:'DEVIATION_PV', title:'参考偏差量', width:80, align:'center'}
                ,{field:'DEVIATION_SV', title:'实际偏差量', width:100, align:'center'}
                ,{field:'LOTNO', title:'生产批次', width:80, align:'center'}
                ,{field:'LINENO', title:'生产线', width:80, align:'center'}
                ,{field:'TAG', title:'电子标签', width:80, align:'center'}
                ,{field:'ROWNUMS', title:'行计数', width:80, align:'center'}
                ,{field:'COLUMNNUMS', title:'列计数', width:80, align:'center'}
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

    });
});


//重新加载表格（搜索）
function load(obj){
    //重新加载table
    tableIns.reload({
    	url:context+'/report/getComList',
        where: {
        	prc_name:'APP_ZY_DATA',
        	pname:obj.field.pname,
            stime:obj.field.stime,
            etime:obj.field.etime
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


