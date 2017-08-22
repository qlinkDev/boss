<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备流量统计图</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic }/echarts-2.2.7/dist/echarts.js"></script>
	<script type="text/javascript">
		 var countryName;
	 	 var actualY;
	 	var vaildY;
	
	 	//条件查询
		$(function(){
			$('#btnExport').click(
					function() {
						var begin =$('#beginDate').val();
						var end = $('#endDate').val();
						var countryCode = $('#countryCode').val();
						var country =$("#s2id_countryCode a span").text();
						$("#countryName").val(country);
						if(countryCode == ""){
							var msg = "请选择国家";
							$.jBox.alert(msg, '提示');
							return false;
						}
						if(begin == "" ||  end == ""){
							var msg = begin == "" ? "请选择开始时间" : "请选择结束时间";
							$.jBox.alert(msg, '提示');
							return false;
						}
						top.$.jBox.confirm("确认导出？", "操作提示", function(v, h, f) {
							if (v == "ok") {
								$("#searchForm").attr('action',
										"${ctx}/mifi/usedDateInfoStat/export2");
								$("#searchForm").submit();
								$("#searchForm").attr('action',
										"${ctx}/mifi/usedDateInfoStat/");
							}
						}, {
							buttonsFocus : 1
						});
						top.$('.jbox-body .jbox-icon').css('top', '55px');
						top.$('.jbox').css('top', '180px');
						
					});
			$('#btnSubmit').click(function(){
				var begin =$('#beginDate').val();
				var end = $('#endDate').val();
				var countryCode = $('#countryCode').val();
				var countryName =$("#s2id_countryCode a span").text();
				if(countryCode == ""){
					var msg = "请选择国家";
					$.jBox.alert(msg, '提示');
					return false;
				}
				if(begin == "" ||  end == ""){
					var msg = begin == "" ? "请选择开始时间" : "请选择结束时间";
					$.jBox.alert(msg, '提示');
					return false;
				}
				 $.ajax({
						type: 'post',
						url: '${ctx}/mifi/usedDateInfoStat/statByMonth',
						data: $('#searchForm').serialize(),
						dataType: 'json',
						success: function(data){
							if(data.code == "-1"){
								$.jBox.alert(data.msg, '提示');
							} else {
								init(data, countryName)
							}
						}
					});
			});
		});
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/usedDateInfoStat/mcc">按国家统计</a></li>
		<li><a href="${ctx}/mifi/usedDateInfoStat/year">按年统计</a></li>
		<li class="active"><a href="${ctx}/mifi/usedDateInfoStat/month">按月统计</a></li>
		<li><a href="${ctx}/mifi/usedDateInfoStat/day">按日统计</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm"  class="breadcrumb form-search">
		<label>国家：</label> 
		<select id="countryCode" name="countryCode" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${countrys}" var="country">
					<option value="${country.key}">${country.value}</option>
			</c:forEach>
		</select>
		 <input  type="hidden" id="countryName" name="countryName" >
		<label>代理商：</label> 
		<select id="sourceType" name="sourceType" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach
				items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}"
				var="sourceTypeValue">
				<option value="${sourceTypeValue.channel_name_en}"
					<c:if test="${sourceTypeValue.channel_name_en==condition.sourceType}">selected</c:if>>${sourceTypeValue.channel_name}</option>
			</c:forEach>
		</select>
		<label>统计月份：</label>
		<input id="beginDate" name="beginDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${begin}"
				onFocus="var endDate = $dp.$('endDate');  WdatePicker({dateFmt:'yyyy-MM', onpicked:function(){endDate.focus();},minDate:'2014-01' ,maxDate: '%y-%M'})" />&nbsp;到
		<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${end}"
				onFocus="WdatePicker({dateFmt:'yyyy-MM', minDate:'#F{$dp.$D(\'beginDate\')}', maxDate: '%y-%M'});" />		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="统计" />
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button"
			value="导出" />
	</form:form>
		<tags:message content="${message}" />
	
	<!-- 查询 E -->
	<div id="main" style="height:600px;border:1px solid #ccc;padding:10px;"></div>
	<!-- 查询 S -->
	
	 <script type="text/javascript">
    	function init(data, title){
			countryName = data.x;
			actualY = data.actualY;
			vaildY = data.vaildY;
			// Step:3 conifg ECharts's path, link to echarts.js from current page.
		    // Step:3 为模块加载器配置echarts的路径，从当前页面链接到echarts.js，定义所需图表路径
		    require.config({
		        paths:{ 
		        	 echarts:'${ctxStatic}/echarts-2.2.7/dist',
		        }
		    });
		    
		    // Step:4 require echarts and use it in the callback.
		    // Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
		    require(
		        [
		            'echarts',
		            'echarts/chart/bar',
		            'echarts/chart/line'
		        ],
		        function(ec) {
		            var myChart = ec.init(document.getElementById('main'));
		            var option = {
	            		title : {
		            		show : true,
		            		text :'[' + title + ']设备使用平均流量统计'	
		            	},
		                tooltip : {
		                    trigger: 'axis',
		                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		                         type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		                     }
		                },
		                legend: {
		                    data:['实际平均值','有效平均值']
		                },
		                toolbox: {
		                	show : true,
		                    orient: 'horizontal',
		                    x: 'right',  
		                    y: 'top',  
		                    color : ['#1e90ff','#22bb22','#4b0082'],
		                    showTitle: true,
		                    feature : {
		                    	 magicType: {
		                             show : true,
		                             title : {
		                                 line : '折线图',
		                                 bar : '柱形图'
		                             },
		                             type : ['line', 'bar']
		                         },
		                         saveAsImage : {
		                             show : true,
		                             title : '保存为图片',
		                             type : 'jpeg',
		                             lang : ['点击本地保存'] 
		                         },
		                    }
		                },
		                calculable : true,
		                xAxis : [
		                    {
		                    	show:true,
		                    	name : '日期',
		                        type : 'category',
		                        splitLine : false,
		                        data : countryName,
		                        axisLabel : { 
		                            interval:0,
		                            rotate:45,
		                            margin:2,
		                            textStyle:{
		                                color:"#000000"
		                            }
		                        },
		                    }
		                ],
		                grid: { // 控制图的大小，调整下面这些值就可以，
		                    x: 100,
		                    x2: 100,
		                    y2: 150,// y2可以控制 X轴跟Zoom控件之间的间隔，避免以为倾斜后造成 label重叠到zoom上
		                },
		                yAxis : [
		                    {
		                        type : 'value',
		                        name:'流量值（M / 天）',
		                        axisLabel : {
		                            formatter: '{value}'
		                        }
		                    }
		                ],
		                series : [
		                    {
		                        name:'实际平均值',
		                        type:'bar',
		                        data:actualY
		                    },
		                    {
		                    	name : '有效平均值',
		                    	type: 'bar',
		                    	data:vaildY
		                    }
		                ]
		            };
		            myChart.setOption(option);
		        }
		    );
    	}
    	
    	//初始化没数据图表
    	function initNull(){
		    require.config({
		        paths:{ 
		        	 echarts:'${ctxStatic}/echarts-2.2.7/dist',
		        }
		    });
		    require(
		        [
		            'echarts',
		            'echarts/chart/bar',
		            'echarts/chart/line'
		        ],
		        function(ec) {
		            var myChart = ec.init(document.getElementById('main'));
		            var ecConfig = require('echarts/config');
		            var option = {
		                xAxis : [
		                    {
		                        type : 'category',
		                        data : [],
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        name : '使用记录数'
		                    }
		                ],
		                series : [
		                    {
		                        name:'使用记录',
		                        type:'bar',
		                        data:[]
		                    }
		                ]
		            };
		            myChart.setOption(option);
		        }
		    );
    	}
    </script>
</body>
</html>