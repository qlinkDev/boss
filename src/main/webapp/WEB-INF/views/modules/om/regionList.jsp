<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
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
		<li class="active"><a href="${ctx}/om/region/">区域列表</a></li>
		<shiro:hasPermission name="om:region:edit"><li><a href="${ctx}/om/region/form">区域添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="regionCondition" action="${ctx}/om/region/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>名称：</label><form:input path="likeName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>MCC：</label><form:input path="likeMcc" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>名称</th><th>编码</th><th style="width:400px;">国家</th><th>默认价格</th><th style="width:400px;">说明</th><shiro:hasPermission name="om:region:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="region">
			<tr>
				<td>${region.name}</td>
				<td>${region.code}</td>
				<td>${region.countryNames}</td>
				<td>${region.defaultPrice}</td>
				<td>${region.remarks}</td>
				<shiro:hasPermission name="om:region:edit"><td>
    				<a href="${ctx}/om/region/form?id=${region.id}">修改</a>
					<a href="${ctx}/om/region/delete?id=${region.id}" onclick="return confirmx('确认要删除该区域吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
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