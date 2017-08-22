<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>渠道管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			tableSort({callBack : page});
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/om/channel/").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/channel/">渠道列表</a></li>
		<shiro:hasPermission name="om:channel:edit"><li><a href="${ctx}/om/channel/form">渠道添加</a></li></shiro:hasPermission>
	</ul>
	
	<form:form id="searchForm" modelAttribute="channel" action="${ctx}/om/channel/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div>
			<label>渠道名称：</label><form:input path="channelName" htmlEscape="false" maxlength="50" class="input-small"/>
			<label>渠道名称（英文）：</label><form:input path="channelNameEn" htmlEscape="false" maxlength="50" class="input-small"/>
			<label>付费类型：</label>
		    <form:select path="payType" class="input-small">
		    	<form:option value="" label="请选择"/>
		    	<form:options items="${fns:getDictList('pay_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
		    </form:select>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
		</div>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th class="sort channelName">渠道名称</th><th class="sort channelNameEn">渠道名称（英文）</th><th class="sort payType">付费类型</th><th>渠道模式</th><th class="sort backPoint">返点</th><shiro:hasPermission name="om:channel:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="channel">
			<tr>
				<td>${channel.channelName}</td>
				<td>${channel.channelNameEn}</td>
				<td>${fns:getDictLabel(channel.payType,"pay_type","未知")}</td>
				<td>${fns:getDictLabel(channel.model,"channel_model","未知")}</td>
				<td>${channel.backPoint}</td>
				<shiro:hasPermission name="om:channel:edit"><td>
    				<a href="${ctx}/om/channel/form?id=${channel.id}">修改</a>
					<a href="${ctx}/om/channel/delete?id=${channel.id}" onclick="return confirmx('确认要删除该渠道吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>