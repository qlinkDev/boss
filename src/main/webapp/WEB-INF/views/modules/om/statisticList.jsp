<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单统计</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.labelStyle {width: 80px; text-align: right;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出订单数据吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						var pageSizeDef = $("#pageSize").val();
						$("#pageSize").val(-1);
						$("#searchForm").attr("action","${ctx}/om/statistic/export").submit();
						$("#searchForm").attr("action","${ctx}/om/statistic/list");
						$("#pageSize").val(pageSizeDef);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnBalance").click(function(){
				top.$.jBox.confirm("确认要结算当前查询条件下的所有订单吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						if(!!top.$.jBox.tip.mess){
							top.$.jBox.tip.mess='';
						}
						$("#searchForm").attr("action","${ctx}/om/statistic/balance").submit();
						$("#searchForm").attr("action","${ctx}/om/statistic/list");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/om/statistic/list").submit();
	    	return false;
	    }
	</script>
	<style type="text/css">
	    .colorRed {
			color:red;
		}
	</style>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/statistic/">订单统计</a></li>
	</ul>
	<!-- tab e -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="consumeRecordCondition" action="${ctx}/om/statistic/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div>
		    <label class="labelStyle">渠道：</label>
		    <form:select id="eqChannelId" path="eqChannelId" class="input-small">
		    	<form:option value="" label="请选择"/>
		    	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="id" htmlEscape="false"/>
		    </form:select>
		    <label class="labelStyle">类型：</label>
		    <form:select id="eqRecordType" path="eqRecordType" class="input-small">
		    	<form:option value="" label="请选择"/>
				<form:options items="${fns:getDictList('record_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		    </form:select>
		    <label class="labelStyle">状态：</label>
		    <form:select id="eqStatus" path="eqStatus" class="input-small">
		    	<form:option value="" label="请选择"/>
		    	<form:options items="${fns:getDictList('consume_order_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		    </form:select>
			<label class="labelStyle">开始日期：</label><form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		    <label class="labelStyle">结束日期：</label><form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		</div>
		<div style="margin-top:8px;">
		    
		    <label class="labelStyle">国家：</label>
		    <form:select id="eqCountryCode" path="eqCountryCode" class="input-small">
		    	<form:option value="" label="请选择"/>
				<c:forEach items="${mccList}" var="mcc">
					<c:if test="${consumeRecordCondition.eqCountryCode == mcc[2]}" var="needSelect">
					    <option value="${mcc[2]}" selected="true">${mcc[1]}</option>
					</c:if>
					<c:if test="${!needSelect}">
					    <option value="${mcc[2]}">${mcc[1]}</option>
					</c:if>
				</c:forEach>
		    </form:select>
			<label class="labelStyle">结算状态：</label>
		    <form:select id="eqBalanceStatus" path="eqBalanceStatus" class="input-small">
		    	<form:option value="" label="请选择"/>
		    	<form:options items="${fns:getDictList('balance_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		    </form:select>
			<label class="labelStyle">设备编号：</label><form:input path="eqSn" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			&nbsp;<input id="btnBalance" class="btn btn-primary" type="button" value="结算"/>
		</div>
		<div style="margin-top:8px;">
		    <label class="labelStyle">总计订单数：</label><!--${fn:length(list)}--><span class="colorRed">${page.count}</span>&nbsp;&nbsp;
			<label class="labelStyle">总计金额：</label><span class="colorRed">${sumMoney}</span>&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>商品名称</th><th>设备编号</th><th>SSID</th><th>渠道名称</th><th>国家</th><th>类型</th><th>订单状态</th><th>金额</th><th>日期</th><th>结算状态</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="record">
			<tr>
				<td>${record.targetName}</td>
				<td>${record.sn}</td>
				<td>${record.ssid}</td>
				<td>${record.channel.channelName}</td>
				<td>${record.countryName}</td>
				<td>${record.recordType.name}</td>
				<td>${record.status.name}</td>
				<td>${record.money}</td>
				<td><fmt:formatDate value="${record.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${fns:getDictLabel(record.balanceStatus,"balance_status","未知")}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
	
</body>
</html>