<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备开机管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
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
		<li class="active"><a href="${ctx}/mifi/deviceBoot/">设备开机列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="deviceBootCondition" action="${ctx}/mifi/deviceBoot/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label>
		<form:input path="likeImei" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>类型：</label>
		<select id="eqType" name="eqType" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${fns:getDictList('device_boot_type')}" var="dbType">
				<option value="${dbType.value}" <c:if test="${dbType.value==deviceBootCondition.eqType}">selected</c:if>>${dbType.label}</option>
			</c:forEach>
		</select>
		<label>广告：</label>
		<select id="eqAdvertisingId" name="eqAdvertisingId" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${advList}" var="adv">
				<option value="${adv.id}" <c:if test="${adv.id==deviceBootCondition.eqAdvertisingId}">selected</c:if>>${adv.name}</option>
			</c:forEach>
		</select>
		<label>开机日期：</label>
		<form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<label>设备数量：${total }</label>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>设备编号</th>
					<th>类型</th>
					<th>MAC</th>
					<th>MCC</th>
					<th>广告</th>
					<th>创建时间</th>
					<th>客户端</th>
					<th>代理信息</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="boot">
				<tr>
					<td>${boot.imei}</td>
					<td>${fns:getDictLabel(boot.type, 'device_boot_type', '未配置类型')}</td>
					<td>${boot.mac}</td>
					<td>${boot.mcc}</td>
					<td>${boot.advertising.name}</td>
					<td><fmt:formatDate value="${boot.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${boot.clientType}</td>
					<td>${boot.userAgent}</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>