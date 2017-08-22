<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备监控详细信息管理</title>
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
		<li><a href="${ctx}/mifi/deviceMonitor/">设备监控主体信息列表</a></li>
		<li class="active"><a href="${ctx}/mifi/deviceMonitor/detailList">设备监控详细信息列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="deviceMonitorDetailCondition" action="${ctx}/mifi/deviceMonitor/detailList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:input path="eqDeviceMonitorId" type="hidden"/>
		<label>设备编号：</label>
		<form:input path="likeImei" htmlEscape="false" maxlength="32" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>设备编码</th><th>上一记录(状态[RSSI_9215,RSSI_6200])</th><th>上一记录发生时间</th><th>下一记录(状态[RSSI_9215,RSSI_6200])</th><th>下一记录发生时间</th><th>国家中文名</th><th>国家英文名</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="detail">
			<tr>
				<td>${detail.imei}</td>
				<td>${detail.preStatus}</td>
				<td><fmt:formatDate value="${detail.preHappenDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${detail.nextStatus}</td>
				<td><fmt:formatDate value="${detail.nextHappenDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${detail.countryName}</td>
				<td>${detail.countryNameEn}</td>
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