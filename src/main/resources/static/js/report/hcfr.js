/**
 * 应急实施
 */
var pageCurr;
$(function() {
    
	layui.use('table', function(){
        var table = layui.table
            ,form = layui.form,element = layui.element;
        setTimeout('', 1000);
        //form.render(); //更新全部
        
        tableIns=table.render({
            elem: '#unitList'
            ,url:context+'/report/getHcfrList'
            ,method: 'get' //默认：get请求
            ,cellMinWidth: 80,limit:20
            ,page: true
            ,where: {
            	keyword:$('#keyword').val(),
            	ptype:$('#ptype').val()//$("input[name='ptype']:checked").val()
            },
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
                ,{field:'STARTTIME', title:'开始时间', width:150,align:'center'}
                ,{field:'ENDTIME', title:'结束时间', width:150,align:'center'}
                ,{field:'SSMODE', title:'老化模式', width:180}
                ,{field:'DEVID', title:'设备编号', width:130}
                ,{field:'WOINDEX', title:'工步索引',width:150}
                ,{field:'WONO', title:'工步号', width:100}
                ,{field:'CYCNO', title:'循环次数', width:100,align:'center'}
                ,{field:'DEVDESC', title:'设备码', width:100}
                ,{field:'PALLETNUM', title:'托盘码', width:120}
                ,{field:'LOCATION', title:'托盘号', width:100}
                ,{field:'PASSAGEWAY', title:'通道号', width:100}
                ,{field:'OPENV', title:'开路电压', width:100}
                ,{field:'AVRV', title:'平均电压', width:100}
                ,{field:'ENDV', title:'终止电压', width:100}
                ,{field:'TVALUE', title:'时间', width:100}
                ,{field:'CVALUE', title:'容量', width:100}
                ,{field:'CHARGEI', title:'充电电流', width:100}
                ,{field:'ENDI', title:'终止电流', width:100}
                ,{field:'DISCHARGEI', title:'放电电流', width:100}
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
    });

});


//重新加载表格（搜索）
function load(obj){
    //重新加载table
    tableIns.reload({
        where: {
        	keyword:obj.field.keyword,
        	ptype:obj.field.ptype
        }
        , page: {
            curr: pageCurr //从当前页码开始
        }
    });
}

function child(keyword,ptyle) {

	$('#keyword').val(keyword)
	
     $('#ptype').val(ptyle)
     
	// $(":radio[name='ptype'][value='"+ptyle+"']").attr("checked","true");

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