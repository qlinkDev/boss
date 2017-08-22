<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script type="text/javascript">
	document.addEventListener('DOMContentLoaded', function() {
		document.getElementById('btnSubmit').addEventListener('click', function() {
			$.jBox.tip("正在查询...", 'loading', {persistent: true});
			$(this).attr("disabled",true);
			$("#searchForm").submit();
		}, false);
	}, false);
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/user/userBasic">用户列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="userBasicInfo" action="${ctx}/user/userBasic/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>用户编号：</label><form:input path="userId"  htmlEscape="false" maxlength="100" class="input-small required" /> 
		<label>渠道商编号：</label><form:input path="sourceType" htmlEscape="false"  maxlength="100" class="input-small required" />
		<label>邮箱：</label><form:input path="email" htmlEscape="false" maxlength="100" class="input-small required"  /> 
		<label>电话：</label><form:input path="phone" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;
	    <input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>							   
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed"">
		<thead><tr><th>用户编号</th><th>用户姓名</th><th>渠道商编号</th><th>天数</th>
		<th>电话</th><th>邮箱</th><th>balance</th><th>totalDeposit</th>
		<th>创建时间</th><th>修改时间</th>
		</tr></thead>
		<tbody>
		
		<c:forEach items="${page.list}" var="a">
			<tr>
				<td><a href="${ctx}/user/userMifiLink/list?userId=${a.userId}">${a.userId}</a></td>
				<td>${a.userName}</td>
				<td>${a.sourceType}</td>
				<td><a href="${ctx}/user/dayPass/list?likeLoginName=${a.userId}">${a.dayPass}</a></td>
				<td>${a.phone}</td>
				<td>${a.email}</td>
				<td>${a.balance}</td>
				<td>${a.totalDeposit}</td>
				<td><fmt:formatDate value="${a.createTime}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
				<td><fmt:formatDate value="${a.updateTime}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
