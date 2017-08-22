<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备与卡绑定管理</title>
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
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifitest/">设备与卡绑定列表</a></li>
		<shiro:hasPermission name="mifi:mifiTest:edit"><li><a href="${ctx}/mifi/mifitest/form">设备与卡绑定添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="mifiTestCondition" action="${ctx}/mifi/mifitest/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label><form:input path="likeImei" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>创建日期：</label>
		<form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-small Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>设备编号</th>
				<th>SIMBANKID</th>
				<th>SIMID</th>
				<th>生效时间</th>
				<th>失效时间</th>
				<th>创建时间</th>
				<th>创建人</th>
				<th>说明</th>
				<shiro:hasPermission name="mifi:mifiTest:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="mifiTest">
			<tr>
				<td>${mifiTest.imei}</td>
				<td>${mifiTest.simBankId}</td>
				<td>${mifiTest.simId}</td>
				<td><fmt:formatDate value="${mifiTest.startDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${mifiTest.endDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td><fmt:formatDate value="${mifiTest.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${mifiTest.createBy}</td>
				<td>${mifiTest.remarks}</td>
				<shiro:hasPermission name="mifi:mifiTest:edit"><td>
    				<a href="${ctx}/mifi/mifitest/form?id=${mifiTest.id}">修改</a>
					<a href="${ctx}/mifi/mifitest/delete?id=${mifiTest.id}" onclick="return confirmx('确认要删除该设备与卡绑定吗？', this.href)">删除</a>
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