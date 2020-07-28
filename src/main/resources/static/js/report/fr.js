/**
 * 应急实施
 */
var pageCurr;
$(function() {
    
	layui.use(['table', 'laydate'], function(){
        var table = layui.table
            ,form = layui.form,element = layui.element,laydate = layui.laydate;

        var sdate = getNextDate(-1);
        var edate = getCurDate(0);
        tableIns=table.render({
            elem: '#unitList'
            ,url:context+'/report/getFRList'
            ,method: 'get' //默认：get请求
            	,where:{stime:sdate,ptype:'FR',
                    etime:edate}
            ,cellMinWidth: 80,limit:20
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
                ,{field:'BARCODE', title:'条码', width:180,align:'center'}
                ,{field:'CAPACITY', title:'容量', width:80}
                ,{field:'OPENV', title:'开路电压', width:90}
                ,{field:'AVGV', title:'平均电压', width:90}
                ,{field:'ENDV', title:'终止电压',width:90}
                ,{field:'STARTDATE', title:'开始时间', width:150,align:'center'}
                ,{field:'ENDDATE', title:'结束时间', width:150,align:'center'}
                ,{field:'PASSAGEWAY', title:'通道号', width:100}
                ,{field:'FIP', title:'设备IP', width:110}
                ,{fixed:'right', title:'操作',width:70,align:'center', toolbar:'#optBar'}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
             // 隐藏列
                /*if($('#IsPurchased input[name="ptype"]:checked ').val() == 'HC'){
                	$("[data-field='CAPACITY']").css('display','none');
                }*/
                
            }
        });

      //开始日期
        var insStart = laydate.render({
          elem: '#test-laydate-start'
          ,type: 'datetime'
        	  ,value:sdate
              ,format: 'yyyy-MM-dd HH:mm:ss'
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
        	  ,value:edate
              ,format: 'yyyy-MM-dd HH:mm:ss'
          ,done: function(value, date){
            //更新开始日期的最大日期
//            insStart.config.max = lay.extend({}, date, {
//              month: date.month - 1
//            });
          }
        });
        
        //监听工具条
        table.on('tool(unitTable)', function(obj){
            var data = obj.data;
            if(obj.event === 'detail'){
//            	var url = context+'/report/toHcfr?keyword='+data.BARCODE+'&ptype='+$('#IsPurchased input[name="ptype"]:checked ').val();
//            	top.layui.index.openTabsPage(url, "明细");  //完成页面跳转
            	//var url = context+'/views/hcfr.html';
            	var url = context+'/report/toHcfr';
            	var index = layer.open({
                    type: 2,
                    title:'明细',
                    area: ['600px', '500px'],
                    fixed: false,
                    maxmin: true,
                    content: url,
                    success: function (layero, index) {
                    	 // 获取子页面的iframe
                        var iframe = window['layui-layer-iframe' + index];
                        // 向子页面的全局函数child传参
                        //iframe.child(data.BARCODE,$("input[name='ptype']:checked").val());
                        iframe.child(data.BARCODE,'FR');
                    }
                  });
            	layer.full(index);	
            } 
        });
        //监听提交
        form.on('submit(add)', function(data){
            //新增
           
            
            return false;
        });
        form.on('submit(workSubmit)', function(data){
            //编辑
            editData(data);
            return false;
        });
        form.on('submit(passwordSubmit)', function(data){
            // TODO 校验
            doSetPass(data);
            return false;
        });
      //监听搜索框
        form.on('submit(doSearch)', function(data){
        	//alert(data.elem.getAttribute('data-url'));
            //重新加载table
            load(data);
            return false;
        });
    });

});


//重新加载表格（搜索）
function load(obj){
    //重新加载table
    tableIns.reload({
        where: {
        	keyword:obj.field.keyword,
        	stime:obj.field.stime,
        	etime:obj.field.etime,
        	ptype:'FR'//obj.field.ptype
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

//清空新增表单数据
function cleanUser(){
    $("#id").val("");
    $("#bsName").val("");
    $("#mobile").val("");
    $("#email").val("");
    $("#bsPassword").val("");
}

//清空修改密码表单数据
function cleanPassword(){
    $("#userId").val("");
    $("#password").val("");
    $("#rePassword").val("");
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