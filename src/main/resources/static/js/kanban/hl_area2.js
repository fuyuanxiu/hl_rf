/**
 * 区域看板
 */
var pageCurr;
$(function() {
	if(KanbanList.CN != null || KanbanList.CN != 'null'){
		getProWarn(KanbanList.CN);
	}
	
	getChart1("chart1");
	getChart2("chart2");
	getChart3(KanbanList.CE);//测
	//getKanBanList(permList,woList[0].TASK_NO,woList[0].PRO_CODE);
	
	doYCBJ();//异常报警
	
	doTask();//在制工单
	doUnTask();//生产任务信息
	
	doJi();//机
	doLiao();//料
});
/*产能预警仪表盘*/
function getProWarn(cn) {
	option1 = {
		    series: [
		        {
		            name: '业务指标',
		            type: 'gauge',
		            min : 0,
					max : 100,
					
					radius: '80%',
					axisLine : { // 坐标轴线
						lineStyle : { // 属性lineStyle控制线条样式
							width : 10
						}
					},
					 axisLabel: {            // 刻度标签。
		                    show: true,             // 是否显示标签,默认 true。
		                    distance: 1,            // 标签与刻度线的距离,默认 5。
		                    color: "#fff",          // 文字的颜色,默认 #fff。
		                    fontSize: 12,           // 文字的字体大小,默认 5。
		                    formatter: "{value}",   // 刻度标签的内容格式器，支持字符串模板和回调函数两种形式。 示例:// 使用字符串模板，模板变量为刻度默认标签 {value},如:formatter: '{value} kg'; // 使用函数模板，函数参数分别为刻度数值,如formatter: function (value) {return value + 'km/h';}
		                },
					splitLine: {           // 分隔线
                        length :10,         // 属性length控制线长
                        lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                            width:3,
                            color: '#fff',
                            shadowColor : '#fff', //默认透明
                            shadowBlur: 10
                        }
                    },
					pointer: {              // 仪表盘指针。
	                    show: true,             // 是否显示指针,默认 true。
	                    length: "70%",          // 指针长度，可以是绝对数值，也可以是相对于半径的百分比,默认 80%。
	                    width: 5,               // 指针宽度,默认 8。
	                },
	                
	                itemStyle: {            // 仪表盘指针样式。
	                    color: "auto",          // 指针颜色，默认(auto)取数值所在的区间的颜色
	                    opacity: 1,             // 图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
	                    borderWidth: 0,         // 描边线宽,默认 0。为 0 时无描边。
	                    borderType: "solid",    // 柱条的描边类型，默认为实线，支持 'solid', 'dashed', 'dotted'。
	                    borderColor: "#000",    // 图形的描边颜色,默认 "#000"。支持的颜色格式同 color，不支持回调函数。
	                    shadowBlur: 10,         // (发光效果)图形阴影的模糊大小。该属性配合 shadowColor,shadowOffsetX, shadowOffsetY 一起设置图形的阴影效果。 
	                    shadowColor: "#fff",    // 阴影颜色。支持的格式同color。
	                },
					axisTick : { // 坐标轴小标记
						length : 13, // 属性length控制线长
						distance:1,
						lineStyle : { // 属性lineStyle控制线条样式
							color : 'auto'
						}
					},
		            detail: {formatter: '{value}%'},
		            data: [{value: 0}]
		        }
		    ]
		};
	var gaugeChart = echarts.init(document.getElementById('pro_warn'));
	gaugeChart.setOption(option1);
}
//获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}
//主内容
function getChart1(chart) {
	option = {
		title : {
			text : '折线图堆叠'
		},
		tooltip : {
			trigger : 'axis',
			axisPointer : {
				type : 'cross',
				crossStyle : {
					color : '#999'
				}
			}
		},
		legend : {
			data : [ '蒸发量', '降水量', '平均温度' ]
		},
		xAxis : [ {
			type : 'category',
			data : [ '1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月',
					'9月', '10月', '11月', '12月' ],
			axisPointer : {
				type : 'shadow'
			}
		} ],
		yAxis : [ {
			type : 'value',
			name : '水量',
			min : 0,
			max : 250,
			interval : 50,
			axisLabel : {
				formatter : '{value} ml'
			}
		}, {
			type : 'value',
			name : '温度',
			min : 0,
			max : 25,
			interval : 5,
			axisLabel : {
				formatter : '{value} °C'
			}
		} ],
		series : [
				{
					name : '蒸发量',
					type : 'bar',
					data : [ 2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6,
							162.2, 32.6, 20.0, 6.4, 3.3 ]
				},
				{
					name : '降水量',
					type : 'bar',
					data : [ 2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6,
							182.2, 48.7, 18.8, 6.0, 2.3 ]
				},
				{
					name : '平均温度',
					type : 'line',
					yAxisIndex : 1,
					data : [ 2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4,
							23.0, 16.5, 12.0, 6.2 ]
				} ]
	};
	var chart1 = echarts.init(document.getElementById(chart));
	chart1.setOption(option);
}
function getChart2(chart) {
	option = {
		title : {
			text : '折线图堆叠'
		},
		tooltip : {
			trigger : 'axis',
			axisPointer : { // 坐标轴指示器，坐标轴触发有效
				type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		legend : {
			data : [ '直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎', '百度',
					'谷歌', '必应', '其他' ]
		},
		grid : {
			left : '3%',
			right : '4%',
			bottom : '3%',
			containLabel : true
		},
		xAxis : [ {
			type : 'category',
			data : [ '周一', '周二', '周三', '周四', '周五', '周六', '周日' ]
		} ],
		yAxis : [ {
			type : 'value'
		} ],
		series : [ {
			name : '直接访问',
			type : 'bar',
			data : [ 320, 332, 301, 334, 390, 330, 320 ]
		}, {
			name : '邮件营销',
			type : 'bar',
			stack : '广告',
			data : [ 120, 132, 101, 134, 90, 230, 210 ]
		}, {
			name : '联盟广告',
			type : 'bar',
			stack : '广告',
			data : [ 220, 182, 191, 234, 290, 330, 310 ]
		}, {
			name : '视频广告',
			type : 'bar',
			stack : '广告',
			data : [ 150, 232, 201, 154, 190, 330, 410 ]
		}, {
			name : '搜索引擎',
			type : 'bar',
			data : [ 862, 1018, 964, 1026, 1679, 1600, 1570 ],
			markLine : {
				lineStyle : {
					type : 'dashed'
				},
				data : [ [ {
					type : 'min'
				}, {
					type : 'max'
				} ] ]
			}
		}, {
			name : '百度',
			type : 'bar',
			barWidth : 5,
			stack : '搜索引擎',
			data : [ 620, 732, 701, 734, 1090, 1130, 1120 ]
		}, {
			name : '谷歌',
			type : 'bar',
			stack : '搜索引擎',
			data : [ 120, 132, 101, 134, 290, 230, 220 ]
		}, {
			name : '必应',
			type : 'bar',
			stack : '搜索引擎',
			data : [ 60, 72, 71, 74, 190, 130, 110 ]
		}, {
			name : '其他',
			type : 'bar',
			stack : '搜索引擎',
			data : [ 62, 82, 91, 84, 109, 110, 120 ]
		} ]
	};
	var chart = echarts.init(document.getElementById(chart));
	chart.setOption(option);
}
//测
function getChart3(list) {
	var xa = []; var da = [];var line=[];
	//定义数组
	for(var i=0;i<list.xAxis.length;i++){
		xa[i] = list.xAxis[i]
	}
	for(var i=0;i<(list.data?list.data:[]).length;i++){
		da[i] = list.data[i]
	}
	for(var i=0;i<(list.data?list.data:[]).length;i++){
		line[i] = list.data_line[i]
	}
	option = {
		    title : {
					text : '不良柏拉图'
				},
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross',
		            crossStyle: {
		                color: '#999'
		            }
		        }
		    },
		    toolbox: {
		        feature: {
		            dataView: {show: true, readOnly: false},
		            magicType: {show: true, type: ['line', 'bar']},
		            restore: {show: true},
		            saveAsImage: {show: true}
		        }
		    },
		    legend: {
		        data: []
		    },
		    xAxis: [
		        {
		            type: 'category',
		            //data: ["外观不良", "材质线", "焊接印", "台阶不良", "报废", "塞块进", "小片移位", "赛块进"],
		            data:xa,
		            axisPointer: {
		                type: 'shadow'
		            }
		        }
		    ],
		    yAxis: [
		        {
		            type: 'value',
		            name: '生产数量',
		            /*min: 0,
		            max: 250,*/
		            interval: 50,
		            axisLabel: {
		                formatter: '{value}'
		            }
		        },
		        {
		            type: 'value',
		            name: '合格率',
		            min: 0,
		            max: 100,
		            interval: 5,
		            axisLabel: {
		                formatter: '{value}%'
		            }
		        }
		    ],
		    series: [
		        {
		            name: '生产数量',
		            type: 'bar',
		            data: da
		            //data:["4672", "788", "370", "321", "216", "29", "16", "3"]
		        },
		        {
		            name: '合格率',
		            type: 'line',
		            yAxisIndex: 1,
		            data: line
		            //data: ["72.83", "85.11", "90.88", "95.88", "99.25", "99.7", "99.95", "100"]
		        }
		    ]
		};

	var chart = echarts.init(document.getElementById("chart3"));
	chart.setOption(option);
}

function getKanBanList(area,taskNo,itemNo){
	$.ajax({
        url:  '/kanban/getKanbanList',
        cache: false,
        async: true ,
        data: {"area":area,"taskNo":taskNo,"itemNo":itemNo},
        type: "GET",
        contentType:  'application/json; charset=UTF-8',
        dataType: "json",
        beforeSend: function(request) {

        },
        success: function (res) {
            console.log(res)
            if(res.result){
           	
            }else{
           	 alert(res.msg)
            }
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
       	 alert("服务器好像除了点问题！请稍后试试");
        }
    });
}
function doTask(){
	var obj=KanbanList.Task;
	if(obj[0]){
		$("#task_no").html(obj[0].TASK_NO);
		$("#xh").html(obj[0].BOARD_TYPE);
		$("#jzsj").html(obj[0].DATE_END);
		//$("#wlsm").html(obj[0].TASK_NO);????存储过程未返回
		$("#rws").html(obj[0].PLAN_QTY);
		
		$("#tlsl").html(obj[0].OUTTL_QTY);
		$("#ywcsl").html(obj[0].FISH_QTY);
		$("#zhsl").html(obj[0].PRODU_QTY);
		//$("#tksl").html(obj[0].PLAN_QTY);????存储过程未返回
		//$("#bfsl").html(obj[0].PLAN_QTY);????存储过程未返回
		//$("#bfsl").html(obj[0].PLAN_QTY);????存储过程未返回
		//$("#srksl").html(obj[0].PLAN_QTY);????存储过程未返回
	}
} 

//异常报警
function doYCBJ(){
	var obj=KanbanList.XC;	
	$("#yc_zl li").remove();
	$("#yc_sb li").remove();
	for (var i = 0; i < obj.length; i++) {	 
		if(obj[i].DATATYPE == 'Q'){
			var newRow = '';
			if(obj[i].COLOR == 'red'){
				newRow="<li><span class='layui-badge'>"+obj[i].PROBLEM_TIME+"</span></li>";	
			}else{
				newRow="<li><span class='layui-badge layui-bg-orange'>"+obj[i].PROBLEM_TIME+"</span></li>";
			}
	        $("#yc_zl").append(newRow);
		}else{
			var newRow = '';
			if(obj[i].COLOR == 'red'){
				newRow="<li><span class='layui-badge'>"+obj[i].PROBLEM_TIME+"</span></li>";	
			}else{
				newRow="<li><span class='layui-badge layui-bg-orange'>"+obj[i].PROBLEM_TIME+"</span></li>";
			}
	        $("#yc_sb").append(newRow);
		}
		
    }	
}
//生产任务信息
function doUnTask(){
	var obj=KanbanList.UnTask;	
	$("#unTask tr").remove();
	for (var i = 0; i < obj.length; i++) {	 
		var newRow="<tr ><td>"+obj[i].TASK_NO+"</td>"+
				   "<td>"+obj[i].PRO_CODE+"</td>"+
				   "<td>"+obj[i].PLAN_QTY+"</td>"+
		           "</tr>";	
	   	$("#unTask").append(newRow);
    }	
}
//机
function doJi(){
	var obj=KanbanList.JI;	var kaiji=0;var xianzhi=0;var weixiu=0;
	$("#ji li").remove();
	for (var i = 0; i < obj.length; i++) {	
		var newRow= '';
		if(obj[i].EQ_STATUS == '开机'){
			kaiji += 1;
			newRow="<li><div class='inner'><div class='disc' style='background:linear-gradient(DarkSeaGreen,whitesmoke)'>"+obj[i].EQ_NAME+"</div></div></li>";
		}else if(obj[i].EQ_STATUS == '闲置'){
			xianzhi += 1;
			newRow="<li><div class='inner'><div class='disc' style='background:linear-gradient(Yellow,whitesmoke)'>"+obj[i].EQ_NAME+"</div></div></li>";
		}else{
			weixiu += 1;
			newRow="<li><div class='inner'><div class='disc' style='background:linear-gradient(PeachPuff,whitesmoke)'>"+obj[i].EQ_NAME+"</div></div></li>";
		}	
	   	$("#ji").append(newRow);
	   	
		$("#kaiji").html(kaiji);
		$("#xianzhi").html(xianzhi);
		$("#weixiu").html(weixiu);
    }	
}
//料
function doLiao(){
	var obj=KanbanList.LIAO;	
	$("#liao tr").remove();
	for (var i = 0; i < obj.length; i++) {	 
		var newRow="<tr ><td>"+obj[i].IDNRK+"</td>"+
				   "<td>"+obj[i].ITEM_NAME+"</td>"+
				   "<td>"+obj[i].FMENGE+"</td>"+"<td>"+obj[i].XMENGE+"</td>"+
				   "<td>"+obj[i].TMENGE+"</td>"+"<td>"+obj[i].CMENGE+"</td>"+
				   "<td>"+obj[i].WMENGE+"</td>"+"<td>"+obj[i].MEINS+"</td>"+
		           "</tr>";	
	   	$("#liao").append(newRow);
    }	
}