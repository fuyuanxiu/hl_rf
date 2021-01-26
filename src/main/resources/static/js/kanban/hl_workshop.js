/**
 * 车间看板
 */
var action = true;
var interval_do = null;// 页面定时器
var MyMarhq = null;// 表格滚动定时器
$(function() {
	dealData()
	var cycle=parseFloat(rotime.data[0].REFRESHTIME)
	interval_do = setInterval(getKanBanList, cycle * 1000); // -执行轮播 1s=1*1000
});

function dealData() {
	var dataList = kanbanData.data;
	getAreaChart(dataList);// 区域信息echarts图
	setItemData(dataList)// 车间状态数据设置
	setLinerInfo(dataList)// 设置班组长信息
	setAreaWarn(dataList.warnInfo)// 区域报警信息
	setTaskTable(dataList.taskInfo)// 任务汇总表格
	/** *产品工序合格率数据处理-start-** */
	var prodOk_x = []// 横坐标
	var prodOk_y1 = []// 个数
	var prodOk_y2 = []// 百分比
	if (dataList.prodOk.length == 0) {
		prodOk_x.push('0')
		prodOk_y1.push('0')
		prodOk_y2.push('0')
	} else {
		for (var i = 0; i < dataList.prodOk.length; i++) {
			prodOk_x.push(dataList.prodOk[i].BOARD_ITEM)
			prodOk_y1.push(dataList.prodOk[i].AVERAGE)
			prodOk_y2.push(dataList.prodOk[i].PASSRATE)
		}
	}
	getProcChart(prodOk_x, prodOk_y1, prodOk_y2);// 产品工序合格率
	/** *产品工序合格率数据处理-end-** */

	/** 班组合格率数据处理-start-* */
	var classOk_x = []// 横坐标
	var classOk_y1 = []// 个数
	var classOk_y2 = []// 百分比
	if (dataList.classOk.length == 0) {
		classOk_x.push('0')
		classOk_y1.push('0')
		classOk_y2.push('0')
	} else {
		for (var i = 0; i < dataList.classOk.length; i++) {
			classOk_x.push(dataList.classOk[i].CGROUP_NAME)
			classOk_y1.push(dataList.classOk[i].AVERAGE)
			classOk_y2.push(dataList.classOk[i].PASSRATE)
		}
	}
	getClassChart(classOk_x, classOk_y1, classOk_y2);// 班组合格率图
	/** 班组合格率数据处理-end-* */

	/** 班组生产达标情况数据处理-start-* */
	var status_x = [];
	var status_y = [];
	// console.log(dataList.classStatus)
	if (dataList.classStatus.length == 0) {
		status_x.push('0')
		status_y.push('0')
	} else {
		for (var i = 0; i < dataList.classStatus.length; i++) {
			status_x.push(dataList.classStatus[i].CGROUP_NAME)
			status_y.push(dataList.classStatus[i].REACHPASSRATE)
		}
	}
	getStatusChart(status_x, status_y);// 班组生产达标情况
	/** 班组生产达标情况数据处理-end-* */
}

function setItemData(dataList) {
	$("#plan_emp").text(dataList.planEmp);
	$("#on_emp").text(dataList.onEmp);
	$("#task_count").text(dataList.taskNum);
}

function setLinerInfo(dataList) {
	var obj = dataList.linerInfo
	$("#director").html(obj[0].FNAME);// 车间经理
	$("#director_img").attr('src', "../downImages/" + obj[0].FCODE + ".png");
}

function setTaskTable(tableData) {
	var html = "";
	for (var j = 0; j < tableData.length; j++) {
		var arr = tableData[j];
		var f_time = arr.PLAN_FINISH_TIME.substring(0, arr.PLAN_FINISH_TIME.indexOf(' '));
		var isDelay = arr.ISDEFERRED == 'Y' ? '是' : '否';
		html += '<tr><td>' + arr.WORL_SINGNUM + '</td><td>' + arr.PRO_CODE + '</td><td>' + arr.PRO_NAME + '</td><td>' + arr.TASK_QTY + '</td><td>' + f_time + '</td><td>' + isDelay
				+ '</td></tr> ';
	}
	$("#unTask1").empty();
	$("#unTask1").append(html);
	$("#unTask").empty();
	$("#unTask").append(html);

	if (MyMarhq != null) {// 判断计时器是否为空-关闭
		clearInterval(MyMarhq);
		MyMarhq = null;
	}
	var item = $('.tbl-body tbody tr').length
	// console.log(item)

	if (item > 7) {
		$('.tbl-body tbody').html($('.tbl-body tbody').html() + $('.tbl-body tbody').html());
		$('.tbl-body').css('top', '0');
		var tblTop = 0;
		var speedhq = 40; // 数值越大越慢
		var outerHeight = $('.tbl-body tbody').find("tr").outerHeight();
		function Marqueehq() {
			if (tblTop <= -outerHeight * item) {
				tblTop = 0;
			} else {
				tblTop -= 1;
			}
			$('.tbl-body').css('top', tblTop + 'px');
		}

		MyMarhq = setInterval(Marqueehq, speedhq);
	} else {
		$('.tbl-body').css('top', '0');// 内容少时不滚动
	}
}

function setAreaWarn(warnData) {
	// (E:设备报警，Q:质量报警，M :欠料报警)
	var e_type = 0
	var q_type = 0
	var m_type = 0
	// 限制只显示两行报警
	for (var i = 0; i < warnData.length; i++) {
		if (warnData[i].THETYPE == "E") {
			if (e_type >= 2) {
				continue;
			} else if (e_type > 0) {
				$("#dev_warn_area2").text(warnData[i].EQ_AREANAME)
				e_type++
			} else {
				$("#dev_warn_area1").text(warnData[i].EQ_AREANAME)
				e_type++
			}
		} else if (warnData[i].THETYPE == "Q") {
			if (q_type >= 2) {
				continue;
			} else if (q_type > 0) {
				$("#qual_warn_area2").text(warnData[i].EQ_AREANAME)
				q_type++
			} else {
				$("#qual_warn_area1").text(warnData[i].EQ_AREANAME)
				q_type++
			}
		} else if (warnData[i].THETYPE == "M") {
			if (m_type >= 2) {
				continue;
			} else if (m_type > 0) {
				$("#owe_warn_area2").text(warnData[i].EQ_AREANAME)
				m_type++
			} else {
				$("#owe_warn_area1").text(warnData[i].EQ_AREANAME)
				m_type++
			}
		}
	}
}

function getAreaChart(dataList) {
	getAreaCharts(dataList.onEmpRate, getOff(dataList.onEmpRate), "员工在岗率", '#00FF99', 'area_chart1');// （在线,缺席,标题，颜色，divID）
	
	getAreaCharts(dataList.onDevRate, getOff(dataList.onDevRate), "设备使用率", '#6699FF', 'area_chart2')// （开机数，停机数，标题，颜色，divID）

	getAreaCharts(dataList.onTimeRate, getOff(dataList.onTimeRate), "报工及时率", '#33FFFF', 'area_chart3')// (按时数,不及时数,标题，颜色，divID)
}
// 设置饼图的显示
function getOff(onData) {
	return 100 - parseFloat(onData)
}

// 区域信息-charts
function getAreaCharts(item_now, item_off, titleName, color, chartId) {
	var option = {
		color : [ color, '#999999' ],
		title : {
			text : titleName+"\n"+"\n"+item_now + "%",
			left : "center",
			top : "35%",
			textStyle : {
				color : "#ffffff",
				fontSize : 12,
				align : "center"
			},
		},
		/*graphic : {
			type : "text",
			left : "center",
			top : "35%",
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
			radius : [ '60%', '75%' ],
			center : [ '50%', '50%' ],
			avoidLabelOverlap : false,
			label : {
				show : false,
				position : 'center'
			},
			data : [ {
				value : item_now,
			}, {
				value : item_off,
			}, ]
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById(chartId));
	myCharts1.setOption(option, true)
}
function getClassChart(classOk_x, classOk_y1, classOk_y2) {
	option = {
		color : [ '#CC3366', '#00FFCC' ],
		grid : {
			x : 45,// 左边距
			y : 40,// 上边距
			x2 : 35,// 右边距
			y2 : '28%',// 下边距
		},
		legend : {
			x : 'center', // 可设定图例在左、右、居中
			y : 'bottom',
			data : [ '平均报工数量', '合格率' ],
			textStyle : {
				// fontSize : 22,// 字体大小
				color : '#ffffff'// 字体颜色
			},
		},
		xAxis : [ {
			type : 'category',
			data : classOk_x,
			axisPointer : {
				type : 'shadow'
			},
			axisLabel : {
				show : true,
				textStyle : {
					color : '#ffffff',
				},
				interval : 0,
				rotate : 40
			},
			axisLine : {
				lineStyle : {
					color : '#FFFFFF'
				}
			},
		} ],
		yAxis : [ {
			type : 'value',
			name : '个',
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
		}, {
			type : 'value',
			name : '%',
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
		} ],
		series : [ {
			name : '平均报工数量',
			type : 'bar',
			data : classOk_y1,
			barMaxWidth : '50',
			label : {
				show : true,
				position : 'top',
				textStyle : {
					fontSize : 12,// 字体大小
				}
			},
		}, {
			name : '合格率',
			type : 'line',
			yAxisIndex : 1,
			data : classOk_y2,
			label : {
				show : true,
				position : 'top',
				textStyle : {
					fontSize : 12,// 字体大小
				}
			},
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById('center_chart'));
	myCharts1.setOption(option, true)
}

function getProcChart(prodOk_x, prodOk_y1, prodOk_y2) {
	option = {
		color : [ '#CC3366', '#00FFCC' ],
		grid : {
			x : 45,// 左边距
			y : 30,// 上边距
			x2 : 35,// 右边距
			y2 : '33%',// 下边距
		},
		legend : {
			x : 'center', // 可设定图例在左、右、居中
			y : 'bottom',
			data : [ '平均工序数量', '合格率' ],
			textStyle : {
				// fontSize : 22,// 字体大小
				color : '#ffffff'// 字体颜色
			},
		},
		xAxis : [ {
			type : 'category',
			data : prodOk_x,
			axisPointer : {
				type : 'shadow'
			},
			axisLabel : {
				show : true,
				showMaxLabel : true,
				showMinLabel : true,
				textStyle : {
					color : '#ffffff',
				},
				interval : 0,
				rotate : 80
			},
			axisLine : {
				lineStyle : {
					color : '#FFFFFF',
				}
			},
		} ],
		yAxis : [ {
			type : 'value',
			name : '个',
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
		}, {
			type : 'value',
			name : '%',
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
		} ],
		series : [ {
			name : '平均工序数量',
			type : 'bar',
			data : prodOk_y1,
			barMaxWidth : '50',
		}, {
			name : '合格率',
			type : 'line',
			yAxisIndex : 1,
			data : prodOk_y2,
		// label : {
		// show : true,
		// position : 'top',
		// textStyle : {
		// fontSize : 12,// 字体大小
		// }
		// },
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById('right_b_chart'));
	myCharts1.setOption(option, true)
}
function getStatusChart(xData,yData) {// xData,yData
	 var xData = [ '一班', '二班', '三班', '四班', '五班', '六班']
	// var yData = ['100', '96.01', '80.01', '72','80', '95' ]
	 var yData = ['0', '0', '1', '72','0', '5' ]
	var colorList = [ '#00CCCC', '#2a5fcf', '#0093ff', '#00deff', '#97e7ff', '#00CC99', '#00FFCC', '#CCCC66' ]
	var visualMapPiecesData = []

	for (var i = 0; i < xData.length; i++) {
		visualMapPiecesData.push({
			value : yData[i],
			label : xData[i],
			color : colorList[i]
		})
	}
	var option = {
		angleAxis : {// 角度轴
			max : 110,
			axisLine : {
				show : false
			},
			axisTick : {
				show : false
			},
			axisLabel : {
				show : false
			},
			splitLine : {
				show : false
			},
			clockwise : true//逆时针方向
		},
		radiusAxis : {
			type : 'category',
			data : xData,
			z : 100,
			axisLine : {
				show : false
			},
			axisTick : {
				show : false
			},
			axisLabel : {
				show : true,
				interval : 0,//0：全部显示
				color : '#ffffff',
				margin : 0,
				align : 'left',//显示在坐标轴右边
				formatter : function(value, index) {
					return yData[index]+"%"//坐标轴标签显示数值
				}
			},
			splitLine : {
				show : false
			}
		},

		polar : {
			center : [ '55%', '55%' ],
			radius : [ 90 ]
		// 半径大小
		},
		tooltip : {
			trigger : 'item',
			formatter : function(params, ticket, callback) {
				return params.name + ' : ' + ' (' + params.data + '%)'
			}
		},
		visualMap : {
			top : 5,
			x : 'left',
			orient : 'vertical',
			textStyle : {
				color : '#ffffff'
			},
			pieces : visualMapPiecesData,
			outOfRange : {
				color : '#ffffff'
			},
		},
		series : [ {
			type : 'bar',
			data : yData,
			center : [ '55%', '55%' ],
			coordinateSystem : 'polar',
			label : {
				normal : {
					show : true
				}
			},
			itemStyle : {
				normal : {
					// 定制显示（按顺序）
					color : function(params) {
							return colorList[params.dataIndex]										
					}
				},
			},

		} ]
	}
	var myCharts1 = echarts.init(document.getElementById('status_chart'));
	myCharts1.setOption(option, true)
}

function getKanBanList() {
	$.ajax({
		url : context +'/kanban/getWorkohopList',
		cache : false,
		async : true,
		data : {
			"workShopId" : workShopId
		},
		type : "GET",
		contentType : 'application/json; charset=UTF-8',
		dataType : "json",
		beforeSend : function(request) {

		},
		success : function(res) {
			console.log(res)
			if (res.result) {
				kanbanData = res
				action = true;
				dealData()
			} else {
				// action = false;
				// clearInterval(interval_do);// 错误-关闭定时器
				// alert(res.msg)
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			// alert("服务器好像出了点问题！请稍后试试");
		}
	});
}