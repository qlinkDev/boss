<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消费记录管理</title>
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
		<li class="active"><a href="${ctx}/om/consumeRecord/">消费记录列表</a></li>
	</ul>
	<!-- tab e -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="consumeRecordCondition" action="${ctx}/om/consumeRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>手机号码：</label><form:input path="likePhone" htmlEscape="false" maxlength="11" class="input-small phone"/>
		<label>平台：</label>
		<form:select id="eqSourceType" path="eqSourceType" class="input-small">
			<option value="">请选择</option>
			<form:options items="${fns:getKeyStoreList()}" itemLabel="keyDesc" itemValue="sourceType" htmlEscape="false"/>
		</form:select>
		<label>类型：</label>
		<form:select id="eqRecordType" path="eqRecordType" class="input-small">
			<form:option value="" label="请选择"/>
			<form:option value="RECHARGE" label="充值"/>
			<form:option value="BUY" label="购物"/>
			<form:option value="OTHER" label="其它"/>
		</form:select>
		<label>状态：</label>
		<form:select id="eqStatus" path="eqStatus" class="input-small">
			<form:option value="" label="请选择"/>
			<form:option value="NEW" label="新建"/>
			<form:option value="COMPLETED" label="已完成"/>
			<form:option value="OVERDUE" label="已过期"/>
		</form:select>
		<label>开始日期：</label><form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		<label>结束日期：</label><form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate"
								   onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>手机号码</th><th>商品名称</th><th>所属平台</th><th>类型</th><th>状态</th><th>金额</th><th>日期</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="record">
			<tr>
				<td>${record.phone}</td>
				<td>${record.targetName}</td>
				<td>${record.sourceType}</td>
				<td>${record.recordType.name}</td>
				<td>${record.status.name}</td>
				<td>${record.money}</td>
				<td><fmt:formatDate value="${record.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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