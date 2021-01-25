/**
 * 公司看板
 */
var interval_do = null;// 页面定时器
$(function() {
	dealData();
	//console.log(rotime.data[0].REFRESHTIME)
	var cycle=parseFloat(rotime.data[0].REFRESHTIME)
	interval_do = setInterval(getKanBanList,cycle * 1000); // -执行轮播  1s=1*1000
});

function dealData() {
	var xAxis=[];var yAxis=[];
	//用户在线比例-Result
	var pieData = kanbanData.data.Result
	getUsrOnline(pieData[0].ONLINEBL, pieData[0].OUTLINEBL, pieData[0].ONLINEBL, "", '#33CCCC', 'echarts_t2');// 用户在线比例-饼图
	
	//xAxis=['1', '2', '3', '4', '5', '6', '7','8','9','10','11','12']
	// --客户端累计PPM-result1-line
	var result1=kanbanData.data.Result1//未填写
	for(var i=0;i<result1.length;i++){
		yAxis.push(result1[i].数量==null?'0':result1[i].数量)
		xAxis.push(result1[i].年份+"-"+result1[i].月份)
	}
	//yAxis=[3820, 1932, 1301, 2934, 2290, 1330, 1320,1820, 1932, 2901, 1934, 2290]//未填写
	setLineChart(xAxis,yAxis,'#CC00FF','客户端累计PPM','echarts_t1','个','line')//未填写
	
	//--包装车间生产产量-result2-bar
	var result2=kanbanData.data.Result2
	yAxis=[];xAxis=[];
	for(var i=0;i<result2.length;i++){
		yAxis.push(result2[i].BZQTYS)
		xAxis.push(result2[i].MONTHS)
	}
	setLineChart(xAxis,yAxis,'#EA5026','月产量','echarts_b1','个','bar')
	
	//--毛坯车间生产产量-result3-bar
	var result3=kanbanData.data.Result3
	yAxis=[];xAxis=[];
	for(var i=0;i<result3.length;i++){
		yAxis.push(result3[i].BZQTYS)
		xAxis.push(result3[i].MONTHS)
	}
	setLineChart(xAxis,yAxis,'#64c4d2','月产量','echarts_b2','个','bar')
	
	//--表面处理不合格率-result4-line
	var result4=kanbanData.data.Result4
	yAxis=[];xAxis=[];
	for(var i=0;i<result4.length;i++){
		yAxis.push(result4[i].BMPPM)
		xAxis.push(result4[i].MONTHS)
	}
	setLineChart(xAxis,yAxis,'#9999CC','表面处理不合格率','echarts_c2','%','line')
	
	//原料毛胚过程PPM result5-line
	var result5=kanbanData.data.Result5
	yAxis=[];xAxis=[];
	for(var i=0;i<result5.length;i++){
		yAxis.push(result5[i].MPPPM)
		xAxis.push(result5[i].MONTHS)
	}
	setLineChart(xAxis,yAxis,'#64c4d2','原料毛胚过程PPM','echarts_c1','个','line')
	
	//外协过程PPM result6-line
	var result6=kanbanData.data.Result6
	yAxis=[];xAxis=[];
	for(var i=0;i<result6.length;i++){
		yAxis.push(result6[i].WXPPM)
		xAxis.push(result6[i].MONTHS)
	}
	setLineChart(xAxis,yAxis,'#CCCC33','外协过程PPM','echarts_c3','个','line')
}
/*
function getObjectKeys(object) {//获取数组键值
    var keys = [];
    for (var property in object)
        keys.push(property);
    return keys;
}*/

function getUsrOnline(item_now, item_off, onRate, titleName, color, chartId) {
		var option = {
			color : [ color, '#999999' ],
			title : {
				text : onRate + "%",
				left : "center",
				top : "50%",
				textStyle : {
					color : "#ffffff",
					fontSize : 18,
					align : "center"
				},
			},
			/*graphic : {
				type : "text",
				left : "center",
				top : "50%",
				style : {
					text : titleName,
					textAlign : "center",
					fill : "#ffffff",
					fontSize : 11,
					fontWeight : 500
				}
			},*/
			series : [ {
				type : 'pie',
				radius : [ '55%', '70%' ],
				center : [ '50%', '50%' ],
				avoidLabelOverlap : false,
				label : {
					show : false,
					position : 'center'
				},
				data : [ {
					value : item_now,
				}, {
					value : item_off
				}, ]
			} ]
		};
		var myCharts1 = echarts.init(document.getElementById(chartId));
		myCharts1.setOption(option, true)
}
function setLineChart(xAxis,yAxis,color,titleName,chartId,unit,type){	
	option = {
			color : [ color ],
			grid : {
				x : 50,// 左边距
				y : 40,// 上边距
				x2 : 30,// 右边距
				y2 : 45,// 下边距
				borderWidth : 10
			},
			legend : {
				x : 'center', // 可设定图例在左、右、居中
				bottom : 5,
				data : [ titleName ],
				textStyle : {
					// fontSize : 22,// 字体大小
					color : '#ffffff'// 字体颜色
				},
			},
		    xAxis: {
		        type: 'category',
		        data: xAxis,
		        axisPointer : {
					type : 'shadow'
				},
				axisLabel : {
					show : true,
					textStyle : {
						color : '#ffffff',
					},
				},
				axisLine : {
					lineStyle : {
						color : '#FFFFFF'
					}
				},
		    },
		    yAxis: {
		        type: 'value',
		        name : unit,
				splitLine : {
					show : false
				}, // 去除网格线
				min : 0,
				axisLabel : {
					// formatter : '{value} ',
					textStyle : {
						color : '#ffffff',
					}
				},
				axisLine : {
					lineStyle : {
						color : '#FFFFFF'
					}
				},
		    },
		    series: [{
		    	name : titleName,
		        data: yAxis,
		        type: type,
		        label : {
					show : true,
					position : 'top',
					textStyle : {
						fontSize : 14,// 字体大小
					}
				},
		    }]
		};
	
	
	var myCharts1 = echarts.init(document.getElementById(chartId));
	myCharts1.setOption(option, true)
}

function getKanBanList(){
	$.ajax({
        url: context+ '/kanban/getCompanyList',
        cache: false,
        async: true ,
        data: {},
        type: "GET",
        contentType:  'application/json; charset=UTF-8',
        dataType: "json",
        beforeSend: function(request) {

        },
        success: function (res) {
            console.log(res)
            if(res.result){
            	kanbanData = res
            	dealData()
            }else{
				//clearInterval(interval_do);// 错误-关闭定时器
           	    //alert(res.msg)
            }
        },
        error:function (XMLHttpRequest, textStatus, errorThrown) {
       	 //alert("服务器好像出了点问题！请稍后试试");
        }
    });
}