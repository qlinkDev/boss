<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡类型管理</title>
<meta name="decorator" content="default" />
<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiip">IP管理</a></li>
		<li><a href="${ctx}/mifi/mifiip/form">IP添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="mifiIp" action="${ctx}/mifi/mifiip/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>MCC：</label><form:input path="mcc" maxlength="50" class="input-small required" />
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /> 
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>MCC</th>
				<th>DEFAULTIP</th>
				<th>BACKUPIP</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.mcc}</td>
					<td>${a.defaultip}</td>
					<td>${a.backupip}</td>
					<td><fmt:formatDate value="${a.stampCreated}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>
					<a href="${ctx}/mifi/mifiip/form?id=${a.id}">修改</a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>