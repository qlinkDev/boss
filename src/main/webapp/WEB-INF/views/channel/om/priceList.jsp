<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>价格管理</title>
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
		<li class="active"><a href="${ctx}/om/price/">价格列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<%-- <form:form id="searchForm" modelAttribute="priceCondition" action="${ctx}/om/price/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>区域：</label>
		<form:select id="eqRegionId" path="eqRegionId" class="input-small">
			<form:option value="" label="请选择"/>
			<form:options items="${fns:getRegionList()}" itemLabel="name" itemValue="id" htmlEscape="false"/>
		</form:select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form> --%>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>区域名称</th><th>价格</th><th style="width:400px;">国家</th><th style="width:400px;">说明</th></tr></thead>
		<tbody>
		<c:forEach items="${priceList}" var="price">
			<tr>
				<td>${price.region.name}</td>
				<td>${price.price}</td>
				<td>${price.region.countryNames}</td>
				<td>${price.remarks}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
</body>
</html>