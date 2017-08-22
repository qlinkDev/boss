<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>生成CSV文件</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			// 消费记录生成csv文件
			$("#btnSubmitForConsume").click(function(){
				var startDate = $('#startDate').val();
				var endDate = $('#endDate').val();
				if (startDate=='' || endDate=='') {
					top.$.jBox.info('开始时间和结束时间不能为空', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				top.$.jBox.confirm("确认要生成csv文件吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						var url = '${ctx}/om/consumeRecord/createCsv';
						$.post(url, {startDateStr:startDate, endDateStr:endDate}, function(data) {
							top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			// mifi订单生成csv文件
			$("#btnSubmitForMifiOrder").click(function(){
				var startDate = $('#startDateMifiOrder').val();
				var endDate = $('#endDateMifiOrder').val();
				if (startDate=='' || endDate=='') {
					top.$.jBox.info('开始时间和结束时间不能为空', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				top.$.jBox.confirm("确认要生成csv文件吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						var url = '${ctx}/om/consumeRecord/createMifiOrderCsv';
						$.post(url, {startDateStr:startDate, endDateStr:endDate}, function(data) {
							top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/consumeRecord/csv">生成CSV文件</a></li>
	</ul>
	<!-- tab e -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	<div style="border-bottom: 1px solid #ddd; margin-top: 10px;">
		<div><font style="color: red;">消费记录</font></div>
		<label>开始日期：</label><input id="startDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
	    <label>结束日期：</label><input id="endDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
		&nbsp;<input id="btnSubmitForConsume" class="btn btn-primary" type="button" value="生 成"/>
	</div>
	<div style="border-bottom: 1px solid #ddd; margin-top: 10px;">
		<div><font style="color: red;">mifi订单</font></div>
		<label>开始日期：</label><input id="startDateMifiOrder" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
	    <label>结束日期：</label><input id="endDateMifiOrder" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
		&nbsp;<input id="btnSubmitForMifiOrder" class="btn btn-primary" type="button" value="生 成"/>
	</div>
</body>
</html>