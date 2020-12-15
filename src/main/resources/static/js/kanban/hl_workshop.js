/**
 * 车间看板
 */
var time1 = 0, time2 = 0;
$(function() {
	getAreaChart();// 区域信息echarts图
	getClassChart();//班组合格率图
	getProcChart();//产品工序合格率
	getStatusChart();//班组生产达标情况
});
function getAreaChart() {
	getAreaCharts(52, 5, 91.22, "员工在岗率", '#00FF99', 'area_chart1')// （在线,缺席,比率，标题，颜色，divID）
	getAreaCharts(20, 6, 76.92, "设备使用率", '#6699FF', 'area_chart2')// （开机数，停机数，比率,标题，颜色，divID）
	getAreaCharts(102, 23, 81.6, "报工及时率", '#33FFFF', 'area_chart3')// (按时数,不及时数，比率，标题，颜色，divID)
}

// 获取员工在岗率-charts
function getAreaCharts(item_now, item_off, onRate, titleName, color, chartId) {
	var option = {
		color : [ color, '#999999' ],
		title : {
			text : onRate + "%",
			left : "center",
			top : "50%",
			textStyle : {
				color : "#ffffff",
				fontSize : 16,
				align : "center"
			},
		},
		graphic : {
			type : "text",
			left : "center",
			top : "40%",
			style : {
				text : titleName,
				textAlign : "center",
				fill : "#ffffff",
				fontSize : 11,
				fontWeight : 500
			}
		},
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
				value : item_off,
			}, ]
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById(chartId));
	myCharts1.setOption(option, true)
}
function getClassChart() {
	option = {
		color : [ '#CC00FF', '#00FFCC' ],
		grid : {
			x : 50,// 左边距
			y : 30,// 上边距
			x2 : 30,// 右边距
			y2 : 50,// 下边距
			borderWidth : 10
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
			data : [ '1班', '2班', '3班', '4班', '5班', '6班' ],
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
			data : [ 226, 159, 390, 264, 287, 177 ]
		}, {
			name : '合格率',
			type : 'line',
			yAxisIndex : 1,
			data : [ 72.0, 92.2, 83.3, 74.5, 66.3, 60.2 ]
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById('center_chart'));
	myCharts1.setOption(option, true)
}

function getProcChart() {
	option = {
		color : [ '#CC00FF', '#00FFCC' ],
		grid : {
			x : 50,// 左边距
			y : 30,// 上边距
			x2 : 30,// 右边距
			y2 : '25%',// 下边距
			borderWidth : 10
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
			data : [ '550001', '550002', '300213', '234003', '511221',
					'362331', '700213', '834003' ],
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
			data : [ 1226, 1159, 3190, 2164, 1287, 1177, 2123, 4311 ]
		}, {
			name : '合格率',
			type : 'line',
			yAxisIndex : 1,
			data : [ 72.0, 92.2, 83.3, 74.5, 66.3, 60.2, 80.9, 98.2 ]
		} ]
	};
	var myCharts1 = echarts.init(document.getElementById('right_b_chart'));
	myCharts1.setOption(option, true)
}
function getStatusChart() {
	var xData = [ '一班', '二班', '三班', '四班', '五班', '六班', '七班', '八班','九班', '十班' ]
	var yData = [ '72', '73', '76', '80', '85','90', '95', '96','97','98']
	var colorList = [ '#2a5fcf', '#0093ff','#00deff', '#97e7ff', '#00CCCC', '#00CC99', '#00FFCC','#CCCC66']
	var visualMapPiecesData = []
	// visualMap: {
	// pieces: [
	// { value: 123, label: '123（自定义特殊颜色）', color: 'grey' }
	// ]
	// }
	for (var i = 0; i < xData.length; i++) {
		visualMapPiecesData.push({
			value : yData[i],
			label : xData[i],
			color : colorList[i]
		})
	}
	var option = {
		angleAxis : {
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
			clockwise : true
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
				show : false
			},
			splitLine : {
				show : false
			}
		},
		center: ['60%','55%'],
		polar : {
	        radius: [80]//半径大小
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
			}
		},
		series : [ {
			type : 'bar',
			data : yData,
			coordinateSystem : 'polar',
			itemStyle : {
				normal : {
					// 定制显示（按顺序）
					color : function(params) {
						return colorList[params.dataIndex]
					}
				}
			}
		} ]
	}
	var myCharts1 = echarts.init(document.getElementById('status_chart'));
	myCharts1.setOption(option, true)
}