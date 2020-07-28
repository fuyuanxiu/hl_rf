/**
 * 应急实施
 */
var pageCurr;
$(function() {
	var titles = ['物料信息','品质信息','人员设备信息','工装夹具信息'];
    var urls = ['materials','train','inspection','troubleshoot','rectify','overhaul'];
    var lis = '';
    for(var i = 0; i < titles.length; i++){
    	if(i== 0){
    		lis += '<li lay-id="'+urls[i]+'" class="layui-this">'+titles[i]+'</li>';
    	}else{
    		lis += '<li lay-id="'+urls[i]+'" >'+titles[i]+'</li>';
    	}
    }
    $('#ul').append(lis);
    
	layui.use('table', function(){
        var table = layui.table
            ,form = layui.form,element = layui.element;

        tableIns=table.render({
            elem: '#unitList'
            ,url:context+'/report/getMaterialsList'
            ,method: 'get' //默认：get请求
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
                        "msg": "无数据",
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
                ,{field:'PROC_NO', title:'工序编号', width:80}
                ,{field:'PROC_NAME', title:'工序', width:110,align:'center'}
                ,{field:'ITEM_BARCODE', title:'物料条码', width:210}
                ,{field:'ITEM_NO', title:'物料编号', width:120}
                ,{field:'ITEM_NAME', title:'物料名称',width:130}
                ,{field:'TR_LOTNO', title:'物料批次', width:100}
                ,{field:'SUPP_NO', title:'供应商', width:100,align:'center'}
                ,{field:'QUANTITY', title:'物料用量', width:90}
                ,{field:'UNIT', title:'单位', width:80}
                ,{field:'LOT_DATE', title:'来料日期', width:100}
                ,{field:'PLAN_QTY', title:'工单计划数量', width:100}
                ,{field:'FPUT_QTY', title:'本次生产数量', width:100}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
            }
        });
        
        tableIns2=table.render({
            elem: '#list2'
            ,url:context+'/report/getQualityList'
            ,method: 'get' //默认：get请求
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
                        "msg": "无数据",
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
                ,{field:'LOT_NO', title:'批次号', width:170,align:'center'}
                ,{field:'PROC_NO', title:'工序编号', width:100}
                ,{field:'PROC_NAME', title:'工序', width:100}
                ,{field:'FCHECK_ITEM', title:'首件项目',width:100}
                ,{field:'FSTAND', title:'标准值', width:100}
                ,{field:'FDOWN_ALLOW', title:'下公差', width:100}
                ,{field:'FUP_ALLOW', title:'上公差',width:100}
                ,{field:'FSPEC_REQU', title:'单位',width:100}
                ,{field:'VCHECK_RESU', title:'检验值', width:90}
                ,{field:'FSECOND_RESU', title:'检验结果', width:90,align:'center'}
                ,{field:'FCHECK_BY', title:'检验人员', width:80,align:'center'}
                ,{field:'FCHECK_DATE', title:'检验时间', width:145,align:'center'}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
            }
        });
        
        tableIns3=table.render({
            elem: '#list3'
            ,url:context+'/report/getDeviceList'
            ,method: 'get' //默认：get请求
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
                        "msg": "无数据",
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
                ,{field:'PROC_NAME', title:'工序', width:150,align:'center'}
                ,{field:'PROC_NO', title:'工序编号', width:100}
                ,{field:'MACHINE_CODE', title:'设备编号', width:130}
                ,{field:'EQ_NAME', title:'设备名称', width:160}
                ,{field:'CREATE_BY', title:'作业员', width:130}
                ,{field:'CREATE_DATE', title:'生产时间',width:150}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
            }
        });
        
        tableIns4=table.render({
            elem: '#list4'
            ,url:context+'/report/getFixtureList'
            ,method: 'get' //默认：get请求
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
                        "msg": "无数据",
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
                ,{field:'CREATE_DATE', title:'上机时间',width:140}
                ,{field:'CREATE_BY', title:'上机操作人',width:100}
                ,{field:'M_CODE', title:'设备编号', width:100}
                ,{field:'EQ_NAME', title:'设备名称', width:100}
                ,{field:'S_CODE', title:'工装编号',width:100}
                ,{field:'CUT_NAME', title:'工装名称',width:100}
                ,{field:'DOWN_DATE', title:'下机时间',width:100}
                ,{field:'DOWN_USER_BY', title:'下机操作人',width:100}
                ,{field:'NOW_TIME', title:'本次使用次数/米数',width:100}
                ,{field:'AVAILABLE_TIME', title:'可用次/米数',width:100}
                ,{field:'TOTAL_TIME', title:'总使用次/米数',width:100}
                ,{field:'LOTNO', title:'批次号',width:100}
                ,{field:'PROC_NO', title:'工序编号',width:100}
                ,{field:'PROC_NAME', title:'工序名称',width:100}
            ]]
            ,  done: function(res, curr, count){
                //如果是异步请求数据方式，res即为你接口返回的信息。
                //如果是直接赋值的方式，res即为：{data: [], count: 99} data为当前页数据、count为数据总长度
                pageCurr=curr;
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
        //监听提交
        form.on('submit(add)', function(data){
            //新增
            var url = data.elem.getAttribute('data-url');
            
            layer.open({
                type: 2,
                title:titles[indexOf(url)],
                area: ['600px', '500px'],
                fixed: false,
                maxmin: true,
                content: '../../views/security/'+url+'.html',
                success: function (layero, index) {
                	 // 获取子页面的iframe
                    var iframe = window['layui-layer-iframe' + index];
                    // 向子页面的全局函数child传参
                    iframe.child("UNIT",data.id);
                }
              });
            
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
        form.on('submit(doDownload)', function (data) {
            //导出
            window.location = context + '/report/getExcel?keyword=' + data.field.keyword;
        })

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
    tableIns2.reload({
        where: {
        	keyword:obj.field.keyword
        }
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
    tableIns3.reload({
        where: {
        	keyword:obj.field.keyword
        }
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
    tableIns4.reload({
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