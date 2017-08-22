<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIM卡类型管理</title>
<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	</style>
	<script type="text/javascript">
		document.addEventListener('DOMContentLoaded', function() {
			document.getElementById('btnSubmit').addEventListener('click', function() {
				$.jBox.tip("正在查询...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
		    	$("#searchForm").submit();
		    }, false);
		}, false);
		
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
		<li class="active"><a href="${ctx}/mifi/speedrestrit">限速管理</a></li>
		<li><a href="${ctx}/mifi/speedrestrit/form">限速添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="speedrestrit" action="${ctx}/mifi/speedrestrit/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>SPEEDRESTRITMCC：</label><form:input path="speedrestritmcc" maxlength="50" class="input-small required" />
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" /> 
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed" style="width:100%">
			<thead>
				<tr>
					<th>SPEEDRESTRITMCC</th>
					<th>国家名称</th>
					<th>FIRSTLEVELDATA</th>
					<th>FIRSTLEVELSPEED</th>
					<th>SECONDLEVELDATA</th>
					<th>SECONDLEVELSPEED</th>
					<th>OWNER_MCC</th>
					<th>SOURCE_TYPE</th>
					<th>创建时间</th>
					<th>修改时间</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td>${a.speedrestritmcc}</td>
						<td>${a.countryName}</td>
						<td>${a.firstleveldata}</td>
						<td>${a.firstlevelspeed}</td>
						<td>${a.secondleveldata}</td>
						<td>${a.secondlevelspeed}</td>
						<td>${a.ownerMcc}</td>
						<td>${a.sourceType}</td>
						<td><fmt:formatDate value="${a.stampCreated}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td><fmt:formatDate value="${a.stampUpdate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td>
						<a href="${ctx}/mifi/speedrestrit/form?id=${a.id}">修改</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>