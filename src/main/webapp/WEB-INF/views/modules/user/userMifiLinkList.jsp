<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户设备绑定记录查询</title>
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
		<li class="active"><a href="${ctx}/user/userMifiLink/">用户设备绑定列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/user/userMifiLink/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>用户标识：</label><input id="userId" name="userId" type="text" maxlength="100" class="input-small required" value="${userId}" /> 
		<label>用户名称：</label><input id="userName" name="userName" type="text" maxlength="100" class="input-small required" value="${userName}" /> 
		<label>MIFI标识：</label><input id="mifiId" name="mifiId" type="text" maxlength="100" class="input-small required" value="${mifiId}" /> 
		<label>起始日期：</label>
	    <input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到
		<input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp; 
	    
	    <input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>							   
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed"">
		<thead><tr><th>用户标识</th><th>用户姓名</th><th>设备编号</th><th>绑定时间</th></tr></thead>
		<tbody>
		
		<c:forEach items="${page.list}" var="a">
			<tr>
				<td><a href="${ctx}/user/userBasic/list?userId=${a.id}">${a.id}</a></td>
				<td>${a.user_name}</td>
				<td><a href="${ctx}/user/dayPass/list?likeImei=${a.mifi_id}">${a.mifi_id}</a></td>
				<td><fmt:formatDate value="${a.link_time}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
