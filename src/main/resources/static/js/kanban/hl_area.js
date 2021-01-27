
/**
 * 区域看板
 */

var taskNo="";
var area="";
var itemNo="";
var index =1;

var action = true;
var interval_do = null;// 定时器-工单轮播
var taskNo_action=true;
var interval_taskNo = null;// 定时器-更新工单

var time1=0,time2=0;

$(function() {
	
	doData()
	var cycle=parseFloat(Rotime.data[0].REFRESHTIME)//-刷新获取所有工单
	var cycle1=parseFloat(Rotime_1.data[0].REFRESHTIME)//-工单切轮播
		
	interval_do = setInterval(getKanBanList,cycle1* 1000); // 工单切轮播
	interval_taskNo=setInterval(getWoList, cycle * 1000)//-更新工单数据  1s=1*1000
});

function doData(){
	setDots(woList.length);
	
	if(woList.length>1){//至少两个工单
		if(index<woList.length){
			   //设置下个要播放的看板参数
			   taskNo=woList[index].TASK_NO;//工单
			   area=woList[index].ARE_CODE;//区域
			   itemNo=woList[index].PRO_CODE;//产品编号
			  
			   if(index==woList.length-1){
				  index=0;
			   }else{
				   index++;
			   }
			}
	}
	
	if(KanbanList.CN != null || KanbanList.CN != 'null'){
		getProWarn(KanbanList.CN);
	}
	doFa();
	//doFaImg();
	doHuan();
	getChart3(KanbanList.CE);//测
	//getKanBanList(permList,woList[0].TASK_NO,woList[0].PRO_CODE);
	
	doGLZ();//管理者照片
	
	doYCBJ();//异常报警
	
	doTask();//在制工单
	doUnTask();//生产任务信息
	
	doRen();//人
	doJi();//机
	doLiao();//料
}

function doFa(){
	var obj=KanbanList.FA;
	$("#fa tr").remove();
	for (var i = 0; i < obj.length; i++) {	 
		var newRow="<tr >"+
				   "<td>"+obj[i].PROC_ORDER+"</td>"+
				   "<td>"+obj[i].PROC_NAME+"</td>"+
				   "<td>"+(obj[i].TECHNICS_DESCRIBE ? obj[i].TECHNICS_DESCRIBE:"")+"</td>"+
		           "</tr>";	
	   	$("#fa").append(newRow);
    }
}
function doFaImg(){
	var obj=KanbanList.FA;
	if(obj){
		$("#div_li_fa_img").attr('src',"../downImages/"+obj+".png");
	}
}
function doHuan(){
	var obj=KanbanList.HUAN;
	if(obj){
		if(isHasImg("../downImages/"+obj+".png")){
			document.getElementById("div_li_huan_img").style.display="display";
			$("#div_li_huan_img").attr('src',"../downImages/"+obj+".png");
		}else{
			document.getElementById("div_li_huan_img").style.display="none";
		}
	}
}
function isHasImg(pathImg){//判断路径是否存在图片
    var ImgObj=new Image();
    ImgObj.src= pathImg;
     if(ImgObj.fileSize > 0 || (ImgObj.width > 0 && ImgObj.height > 0))
     {
       return true;
     } else {
       return false;
    }
}
/*产能预警仪表盘*/
function getProWarn(cn) {
	option1 = {
		    series: [
		        {
		            name: '业务指标',
		            type: 'gauge',
		            min : 70,
					max : 130,
					splitNumber: 6, //刻度数量
					radius: '70%',
					axisLine : { // 坐标轴线
						lineStyle : { // 属性lineStyle控制线条样式
							width : 10,
							color: [[0.16, 'red'], [0.5, '#FFFF00'], [0.84, '#008000'], [1, '#0000FF']]
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
		            data: [{value: cn}]
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
	if(!list.xAxis){
		return;
	}
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
					text : '不良柏拉图',
					textStyle:{
				        color:'#fff'
				    }
				},
		    tooltip: {
		        trigger: 'axis',
		        axisPointer: {
		            type: 'cross',
		            crossStyle: {
		                color: '#fff'
		            }
		        }
		    },
		    legend: {
		        data: [],
		        textStyle:{
                    //fontSize: 18,//字体大小
                    color: '#ffffff'//字体颜色
                },
		    },
		    xAxis: [
		        {
		            type: 'category',
		            //data: ["外观不良", "材质线", "焊接印", "台阶不良", "报废", "塞块进", "小片移位", "赛块进"],
		            data:xa,
		            axisPointer: {
		                type: 'shadow'
		            },
		            axisLabel: {
                        show: true,
                        textStyle: {
                            color: '#ffffff'
                        }
                    }
		        }
		    ],
		    yAxis: [
		        {
		            type: 'value',
		            name: '生产数量',
		            /*min: 0,
		            max: 250,*/
		            //interval: 50,
		            axisLabel: {
		                formatter: '{value}',
		                textStyle: {
                            color: '#ffffff'
                        }
		            }, splitLine:{
                        show:false
                    },nameTextStyle:{
                        color:"#fff"
                    }
		        },
		        {
		            type: 'value',
		            name: '合格率',
		            min: 0,
		            max: 100,
		            interval: 20,
		            axisLabel: {
		                formatter: '{value}%',
		                textStyle: {
                            color: '#ffffff'
                        }
		            }, splitLine:{
                        show:false
                    },nameTextStyle:{
                        color:"#fff"
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
		            data: line,
		            lineStyle:{ color:'#FFEA51' }
		            //data: ["72.83", "85.11", "90.88", "95.88", "99.25", "99.7", "99.95", "100"]
		        }
		    ]
		};
	var chart = echarts.init(document.getElementById("div_li_ce"));
	chart.setOption(option);
}

function getKanBanList(){
	console.log(area)
	console.log(taskNo)
	console.log(itemNo)
	$.ajax({
        url: context+'/kanban/getKanbanList',
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
            	KanbanList = res.data
            	action = true;
            	doData()
            }else{
            	//action = false;
				//clearInterval(interval_do);// 错误-关闭定时器
           	    //alert(res.msg)
            }
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
       	 //alert("服务器好像出了点问题！请稍后试试");
        }
    });
}

function getWoList(){
	$.ajax({
        url:  context+'/kanban/getWoList',
        cache: false,
        async: true ,
        data: {"line":permList},
        type: "GET",
        contentType:  'application/json; charset=UTF-8',
        dataType: "json",
        beforeSend: function(request) {

        },
        success: function (res) {
            console.log(res)
            if(res.result){
            	 taskNo_action = true;
            	 woList =res.data
            	 index=0;
            	 
            	 setDots(woList.length);
            }else{
             //taskNo_action = false;
             //clearInterval(interval_taskNo);// 错误-关闭定时器
           	 //alert(res.msg)
            }
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
       	 //alert("服务器好像出了点问题！请稍后试试");
        }
    });
}
function doTask(){
	var obj=KanbanList.Task;
	if(obj[0]){
		$("#task_no").html(obj[0].TASK_NO);
		$("#xh").html(obj[0].BOARD_TYPE);
		$("#jzsj").html(obj[0].DATE_END);
		$("#cpmc").html(obj[0].BOARD_NAME);//产品名称
		$("#rws").html(obj[0].PLAN_QTY);
		
		$("#tlsl").html(obj[0].OUTTL_QTY);
		$("#ywcsl").html(obj[0].FISH_QTY);
		$("#zhsl").html(obj[0].PRODU_QTY);
		$("#tuilsl").html(obj[0].INTL_QTY);//退料数量
		$("#bhgs").html(obj[0].NG_QTY);//不合格数量
		$("#rks").html(obj[0].INDEPOT_QTY);//入库数量
		
		$("#zt").html(obj[0].STATUS);//状态

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
				newRow=""+obj[i].PROBLEM_TIME+"";	
				$("#yc_zl").css("color",obj[i].COLOR); 
			}else{
				newRow=""+obj[i].PROBLEM_TIME+"";
			}
	        $("#yc_zl").html(newRow);
		}else{
			var newRow = '';
			if(obj[i].COLOR == 'red'){
				newRow=""+obj[i].PROBLEM_TIME+"";	
				$("#yc_sb").css("color",obj[i].COLOR);
			}else{
				newRow=""+obj[i].PROBLEM_TIME+"";
			}
	        $("#yc_sb").html(newRow);
		}
		
    }	
}
//生产任务信息
function doUnTask(){
	var obj=KanbanList.UnTask;	
	$("#unTask tr").remove();
	for (var i = 0; i < obj.length; i++) {	
		if(i>3){
			return;
		}
		var newRow="<tr >"+
				   "<td>"+obj[i].TASK_NO+"</td>"+
				   "<td>"+obj[i].PRO_CODE+"</td>"+
				   "<td>"+obj[i].PLAN_QTY+"</td>"+
		           "</tr>";	
	   	$("#unTask").append(newRow);
    }		
}
//人
function doRen(){
	var obj=KanbanList.REN;	var kaiji=0;var xianzhi=0;var weixiu=0;
	$("#ren tr").remove();
	$("#ren_head td").remove();
	var newRow_img,newRow_name,newRow_proc,head_td;
	for (var i = 0; i < obj.length; i++) {	
		if(i>11){
			continue;
		}
		newRow_img +="<td style='width:70px;'><img  src='../downImages/"+obj[i].FCODE+".png' class='img-glz' alt=''></td>";
		
		newRow_name +="<td>"+obj[i].FNAME+"</td>";
		newRow_proc +="<td>"+obj[i].WORPROC_NAME+"</td>";
		
		head_td += "<td><span class='speed-line'><span class='speed-num' style='background-color: "+obj[i].IS_COLOR+"; width: 80%;'></span><span class='numText'>"+obj[i].PERCENTAGE+"%</span></span></td>"
    }
	$("#ren_head").append(head_td);

	$("#ren").append("<tr >"+newRow_img+"</tr>");
	$("#ren").append("<tr >"+newRow_name+"</tr>");
	$("#ren").append("<tr >"+newRow_proc+"</tr>");
}
//机
function doJi(){
	var obj=KanbanList.JI;	var kaiji=0;var xianzhi=0;var weixiu=0;
	//console.log(obj)
	$("#ji tr").remove();
	var newRow_icon,newRow_name,newRow_code,opacity;
	for (var i = 0; i < obj.length; i++) {	
		var color = '';
		if(i>9){
			continue;
		}
		if(obj[i].EQ_STATUS == '开机'){
			color="Green";
			opacity=1;
		}else if(obj[i].EQ_STATUS == '待机'){
			color="#D3D3D3";
			opacity=0.5;
		}else if(obj[i].EQ_STATUS == '维修'){
			color="Red";
			opacity=1;
		}
		var icon = getIcon(obj[i].EQ_NAME);

		newRow_icon +="<td><i class='ji-icon  iconfont "+icon+"' style='color:"+color+";font-size:56px;opacity:"+opacity+";' ></i></td>";
		
		newRow_name +="<td>"+obj[i].EQ_NAME+"</td>";
		newRow_code +="<td>"+obj[i].EQ_CODE+"</td>";
    }

	$("#ji").append("<tr >"+newRow_icon+"</tr>");
	$("#ji").append("<tr >"+newRow_name+"</tr>");
	$("#ji").append("<tr >"+newRow_code+"</tr>");
}
//管理者照片
function doGLZ(){
	var obj = KanbanList.GLZ;
	for (var i = 0; i < obj.length; i++) {	
		if(obj[i].GENRE == '0'){
			$("#jl_name").html(obj[i].FNAME);//车间经理
			$("#jl_img").attr('src',"../downImages/"+obj[i].FCODE+".png");
		}else if(obj[i].GENRE == '1'){
			$("#zz_name").html(obj[i].FNAME);//组长
			$("#zz_img").attr('src',"../downImages/"+obj[i].FCODE+".png");
		}else if(obj[i].GENRE == '2'){
			$("#zjy_name").html(obj[i].FNAME);//质检员
			$("#zjy_img").attr('src',"../downImages/"+obj[i].FCODE+".png");
		}
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
//切换-法环测
$(".ul1 li").click(function() {
    //$(this).siblings('li').removeClass('active');  // 删除其他兄弟元素的样式
    //$(this).addClass('active');                    // 添加当前元素的样式
    chang_div1($(this).attr('id'))
});
var li_1=['li_fa','li_huan','li_ce'];
var div_1=['div_li_fa','div_li_huan','div_li_ce'];
//每隔1000ms执行一次
var test1 = setInterval(function(){
	if(time1>2){
		time1 = 0;
	}
	chang_div1(li_1[time1]);
	time1++;
},20*1000);
//清除Interval的定时器,传入变量名(创建Interval定时器时定义的变量名)
//clearInterval(test1);
function chang_div1(li_name){
	for (var i = 0; i < li_1.length; i++) {	
		if(li_1[i] == li_name){
			$("#"+li_1[i]).attr("class","active");
			
			$("#div_"+li_1[i]).attr("class","");
		}else{
			$("#"+li_1[i]).attr("class","");
			
			$("#div_"+li_1[i]).attr("class","div_none");
		}
	}
}

//切换-人机料
$(".ul2 li").click(function() {
    //$(this).siblings('li').removeClass('active');  // 删除其他兄弟元素的样式
    //$(this).addClass('active');                    // 添加当前元素的样式
    chang_div2($(this).attr('id'))
});
var li_2=['li_ren','li_ji','li_liao'];
var div_2=['div_li_ren','div_li_ji','div_li_liao'];
var test2 = setInterval(function(){
	if(time2>2){
		time2 = 0;
	}
	chang_div2(li_2[time2]);
	time2++;
},20*1000);
function chang_div2(li_name){
	for (var i = 0; i < li_2.length; i++) {	 
		if(li_2[i] == li_name){
			$("#"+li_2[i]).attr("class","active");
			
			$("#div_"+li_2[i]).attr("class","");
		}else{
			$("#"+li_2[i]).attr("class","");
			
			$("#div_"+li_2[i]).attr("class","div_none");
		}
	}
}

function getIcon(name){
	if(name == '攻丝机'){
		return 'icon-xianweijing';
	}else if(name == '金属圆锯机'){
		return 'icon-yuanju';
	}else if(name == '切管机'){
		return 'icon-qieguan';
	}else if(name == '砂带机'){
		return 'icon-shiyongdamoguodaotideshadaidamojueyuana';
	}else if(name == '台钻'){
		return 'icon-jixiediandonggongjumechanical-xiaotaizuan';
	}else if(name == '冲床'){
		return 'icon-chongchuang';
	}else if(name == '油压机'){
		return 'icon-jiyouyali';
	}else if(name == '机器人焊接设备'){
		return 'icon-jiqiren-copy-copy-copy';
	}else if(name == '氩弧焊机'){
		return 'icon-huhanicon';
	}else if(name == '烟尘净化器'){
		return 'icon-icon-test-copy';
	}else if(name == '工作台'){
		return 'icon-weibiaoti-';
	}
}

//20210127-fyx-轮播图的导航点
function setDots(length){
	$("#dots i").remove();
	for (var i = 0; i < length; i++) {	 
		var newRow="<i class='dot'></i>";	
	   	$("#dots").append(newRow);
    }
	//$('#dots i').eq((index-1)).addClass('active');
	setDotsClass();
}
function setDotsClass(){
	$("#dots i").eq((index-1)).addClass('active').siblings().removeClass("active");
}