<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>密钥管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
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
		<li class="active"><a href="${ctx}/sys/keyStore/">密钥列表</a></li>
		<shiro:hasPermission name="sys:keyStore:edit"><li><a href="${ctx}/sys/keyStore/form">密钥添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="YYKeyStore" action="${ctx}/sys/keyStore/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>密钥描述 ：</label><form:input path="keyDesc" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>密钥描述</th>
				<th>加密类型</th>
				<th>来源</th>
				<th>密钥</th>
				<th>创建时间</th>
				<th>更新时间</th>
				<th>备注</th>
				<shiro:hasPermission name="sys:keyStore:edit">
					<th>操作</th>
				</shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="keyStore">
			<tr>
				<td><a href="${ctx}/sys/keyStore/form?keyId=${keyStore.keyId}">${keyStore.keyDesc}</a></td>
				<td>${keyStore.keyType}</td>
				<td>${keyStore.sourceType}</td>
				<td>${keyStore.keyValue}</td>
				<td><fmt:formatDate value="${keyStore.createDate}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
				<td><fmt:formatDate value="${keyStore.updateDate}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
				<td>${keyStore.remarks}</td>
				<shiro:hasPermission name="sys:keyStore:edit"><td>
    				<a href="${ctx}/sys/keyStore/form?keyId=${keyStore.keyId}">修改</a>
					<a href="${ctx}/sys/keyStore/delete?keyId=${keyStore.keyId}" onclick="return confirmx('确认要删除该密钥吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
