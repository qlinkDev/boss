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
		<shiro:hasPermission name="om:price:edit"><li><a href="${ctx}/om/price/form">价格添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="priceCondition" action="${ctx}/om/price/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>渠道：</label>
		<form:select id="eqChannelId" path="eqChannelId" class="input-small">
			<form:option value="" label="请选择"/>
			<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="id" htmlEscape="false"/>
		</form:select>
		<label>区域：</label>
		<form:select id="eqRegionId" path="eqRegionId" class="input-small">
			<form:option value="" label="请选择"/>
			<form:options items="${fns:getRegionList()}" itemLabel="name" itemValue="id" htmlEscape="false"/>
		</form:select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>渠道名称</th><th>区域名称</th><th>价格</th><th>上下架</th><th>说明</th><shiro:hasPermission name="om:price:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="price">
			<tr>
				<td>${price.channel.channelName}</td>
				<td>${price.region.name}</td>
				<td>${price.price}</td>
				<td>${price.downShelf=='0' ? '上架' : '下架' }</td>
				<td>${price.remarks}</td>
				<shiro:hasPermission name="om:price:edit"><td>
    				<a href="${ctx}/om/price/form?id=${price.id}">修改</a>
					<a href="${ctx}/om/price/delete?id=${price.id}" onclick="return confirmx('确认要删除该价格吗？', this.href)">删除</a>
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