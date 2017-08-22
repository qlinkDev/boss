<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>DayPassRecord管理</title>
	<meta name="decorator" content="default"/>
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
		<li class="active"><a href="${ctx}/user/dayPass/">DayPassRecord列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="dayPassRecordCondition" action="${ctx}/user/dayPass/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label>
		<form:input path="likeImei" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>用户名：</label>
		<form:input path="likeLoginName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>类型：</label>
		<select id="eqType" name="eqType" class="input-small">
			<option value="">--请选择--</option>
			<option value="RECHARGE" <c:if test="${'RECHARGE'==dayPassRecordCondition.eqType}">selected</c:if>>充值</option>
			<option value="CONSUME" <c:if test="${'CONSUME'==dayPassRecordCondition.eqType}">selected</c:if>>消费</option>
		</select>
		<label>日期：</label>
		<form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名</th>
				<th>类型</th>
				<th>天数</th>
				<th>设备编号</th>
				<th>MCC</th>
				<th>时间</th>
				<th>描述</th>
				<th>回调状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="record">
			<tr>
				<td>${record.loginName }</td>
				<td>${record.type=='RECHARGE' ? '充值' : '消费' }</td>
				<td>${record.days }</td>
				<td>${record.imei }</td>
				<td>${record.mcc }</td>
				<td><fmt:formatDate value="${record.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${record.remarks }</td>
				<c:if test="${record.type=='CONSUME' }">
					<td>${record.status=='FAIL' ? '失败' : '成功' }</td>
				</c:if>
				<c:if test="${record.type=='RECHARGE' }">
					<td>不需回调</td>
				</c:if>
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