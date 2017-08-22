<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIMBank管理</title>
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
		<li class="active"><a href="${ctx}/mifi/simbankip">SIMBank管理</a></li>
		<li><a href="${ctx}/mifi/simbankip/form">SIMBank添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="simBankIp" action="${ctx}/mifi/simbankip/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>simbankid：</label><form:input path="simbankid" maxlength="50" class="input-small required" />
			<label>onlineip：</label><form:input path="onlineip" maxlength="50" class="input-small required" />
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" /> 
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>SIMBANKID</th>
				<th>ONLINEIP</th>
				<th>DEFAULTIP</th>
				<th>BACKUPIP</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.simbankid}</td>
					<td>${a.onlineip}</td>
					<td>${a.defaultip}</td>
					<td>${a.backupip}</td>
					<td><fmt:formatDate value="${a.stampCreated}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><a href="${ctx}/mifi/simbankip/form?id=${a.id}">修改</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>