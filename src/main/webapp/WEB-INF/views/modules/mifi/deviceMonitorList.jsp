<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备监控主体信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/deviceMonitor/">设备监控主体信息列表</a></li>
		<li><a href="${ctx}/mifi/deviceMonitor/detailList">设备监控详细信息列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="deviceMonitorCondition" action="${ctx}/mifi/deviceMonitor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>监控编号：</label>
		<form:input path="likeCode" htmlEscape="false" maxlength="10" class="input-small"/>
		<label>执行开始日期：</label>
		<form:input id="geStartDate" path="geStartDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leStartDate" path="leStartDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>监控编码</th><th>设备数量</th><th>状态记录数量</th><th>异常数量</th><th>执行开始时间</th><th>执行结束时间</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="monitor">
			<tr>
				<td>${monitor.code}</td>
				<td>${monitor.deviceCount}</td>
				<td>${monitor.statusRecordCount}</td>
				<td>${monitor.resultCount}</td>
				<td><fmt:formatDate value="${monitor.startDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${monitor.endDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
    				<a href="${ctx}/mifi/deviceMonitor/detailList?eqDeviceMonitorId=${monitor.id}">详细</a>
				</td>
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