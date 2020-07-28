/**
 * OCV3
 */
var pageCurr;var prc_name='APP_OCV3_DATA';
$(function() {
    
	layui.use(['table', 'laydate'], function(){
        var table = layui.table
            ,form = layui.form,element = layui.element,laydate = layui.laydate;

        var sdate = getNextDate(-1);
        var edate = getCurDate(0);
        
        tableIns2=table.render({
            elem: '#list2'
            ,url:context+'/report/getOCVList'
            ,method: 'get' //默认：get请求
            	 ,where:{stime:sdate,
                     etime:edate,prc_name:prc_name}
            ,cellMinWidth: 80,limit:20
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
                ,{field:'PRO_MODEL', title:'型号', width:100,align:'center'}
                ,{field:'MODEL', title:'设备号', width:120,align:'center'}
                ,{field:'SN', title:'条码', width:200,align:'center'}
                ,{field:'OCT3', title:'OCV3时间', width:150,align:'center'}
                ,{field:'OCV3RESULT', title:'OCV3结果',width:100,align:'center', templet:'<div>{{changStatus(d.OCV3RESULT)}}</div>'}
                ,{field:'OCV3', title:'OCV3',width:100,align:'center'}
                ,{field:'OCV3MIN', title:'电压下限', width:150,align:'center'}
                ,{field:'OCV3MAX', title:'电压上限',width:100,align:'center'}
                ,{field:'VOL3RESULT', title:'电压结果',width:100,align:'center', templet:'<div>{{changStatus(d.OCR3)}}</div>'}
                ,{field:'OCR3', title:'IMP3', width:100,align:'center'}
                ,{field:'IMP3MIN', title:'IMP下限', width:100,align:'center'}
                ,{field:'IMP3MAX', title:'IMP上限', width:100,align:'center'}
                ,{field:'IMP3RESULT', title:'IMP结果', width:100,align:'center', templet:'<div>{{changStatus(d.IMP3RESULT)}}</div>'}
                ,{field:'K13', title:'K值', width:100,align:'center'}
                ,{field:'K3MIN', title:'K值下限', width:100,align:'center'}
                ,{field:'K3MAX', title:'K值上限', width:100,align:'center'}
                ,{field:'K13RESULT', title:'K值结果', width:100,align:'center', templet:'<div>{{changStatus(d.K13RESULT)}}</div>'}
                ,{field:'THICKNESS3', title:'厚度', width:100,align:'center'}
                ,{field:'THICKNESS3MIN', title:'厚度下限', width:100,align:'center'}
                ,{field:'THICKNESS3MAX', title:'厚度上限', width:100,align:'center'}
                ,{field:'THICRESULT3', title:'厚度结果', width:100,align:'center', templet:'<div>{{changStatus(d.THICRESULT3)}}</div>'}
                ,{field:'LINENO', title:'线体号', width:100,align:'center'}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
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
            if(obj.event === 'approval'){
                //审核
                layer.alert('审核',function(){
                    layer.closeAll();
                });
            } else if(obj.event === 'edit'){
                //编辑
                editWork(data,data.id);
            } else if(obj.event === 'file'){
                //附件
                layer.alert('附件',function(){
                    layer.closeAll();
                });
            } else if(obj.event === 'del'){
                //删除
                // delUser(data,data.id,data.bsName);
                delWork(data,data.id,data.bsName);
            }
        });
        form.on('select(lineno)', function(data){
            $.ajax({
                type:'post',
                url:context+'/report/getLineList',
                data:{},
                dataType:'json',
                async:true,
                success:function(data){
                	if(data.result){
                		var option = '<option value="0">请选择</option>';  //默认值
                		for(var i=0;i<data.data.length;i++){
                			option += '<option value="'+data.data[i].LINE_NO+'">'+data.data[i].LINE_NO+'</option>';
                		}
                	}
                    $("#lineno").html("");
                    $("#lineno").append(option);
                    form.render('select','line');
                },
            });
        });
        
        $.ajax({
            type:'get',
            url:context+'/report/getLineList',
            data:{},
            dataType:'json',
            async:true,
            success:function(data){
            	if(data.result){
            		var option = '<option value="">请选择</option>';  //默认值
            		for(var i=0;i<data.data.length;i++){
            			option += '<option value="'+data.data[i].LINE_NO+'">'+data.data[i].LINE_NO+'</option>';
            		}
            	}
                $("#lineno").html("");
                $("#lineno").append(option);
              //重新渲染select
                form.render('select');
            },
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
   /* tableIns.reload({
        where: {
        	keyword:obj.field.keyword,
        	stime:obj.field.stime,
        	etime:obj.field.etime
        	//,lineno:obj.field.ptype
        }
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });*/
   tableIns2.reload({
        where: {
        	keyword:obj.field.keyword,
        	stime:obj.field.stime,
        	etime:obj.field.etime
        	,lineno:obj.field.lineno
        	,pmodel:obj.field.pmodel
        	,prc_name:prc_name
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