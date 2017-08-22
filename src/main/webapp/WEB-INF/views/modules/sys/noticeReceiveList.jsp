<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通知接收管理</title>
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
		<li class="active"><a href="${ctx}/sys/noticeReceive/">通知接收列表</a></li>
		<shiro:hasPermission name="sys:noticeReceive:edit"><li><a href="${ctx}/sys/noticeReceive/form">通知接收添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="noticeReceiveCondition" action="${ctx}/sys/noticeReceive/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>名称：</label><form:input path="likeName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>类型：</label>
	    <form:select path="eqType" class="input-small">
	    	<form:option value="" label="请选择"/>
	    	<form:options items="${fns:getDictList('notice_receive_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>类型</th>
				<th>代理商</th>
				<th>异常编码</th>
				<th>手机号码</th>
				<th>邮箱</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="receive">
			<tr>
				<td>${receive.name}</td>
				<td>${fns:getDictLabel(receive.type,"notice_receive_type","未知")}</td>
				<td>${receive.sourceType}</td>
				<td>${receive.faultCodes}</td>
				<td style="word-wrap: break-word;width: 400px;"><div style="width: 400px;">${receive.phones}</div></td>
				<td style="word-wrap: break-word;width: 600px;"><div style="width: 600px;">${receive.emails}</div></td>
				<td>
					<shiro:hasPermission name="sys:noticeReceive:edit">
    				<a href="${ctx}/sys/noticeReceive/form?id=${receive.id}">修改</a>
					<a href="${ctx}/sys/noticeReceive/delete?id=${receive.id}" onclick="return confirmx('确认要删除该通知接收吗？', this.href)">删除</a>
					</shiro:hasPermission>
				</td>
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