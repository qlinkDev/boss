<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SIM卡流量查询</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 查询
			$('#btnSubmit').click(function() {
				var iccid = $('#iccid').val();
				if (isEmpty(iccid)) {
					top.$.jBox.info('请输入卡号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				$.jBox.tip("正在查询...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
				$('#searchForm').submit();
			});
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
		<li class="active"><a href="${ctx}/mifi/simCardTraffic/">SIM卡流量列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/simCardTraffic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="initTag" name="initTag" type="hidden" value="${initTag}"/>
		<div>
			<label>卡号：</label><input id="iccid" name="iccid" type="text" maxlength="50" class="input-medium" value="${iccid}" placeholder="SIM卡背面20位数字"/>
			&nbsp;
			<label>开始日期：</label><input id="beginDate" name="beginDate" type="text"  maxlength="20" class="input-medium Wdate"
				value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			&nbsp;
			<label>结束日期：</label><input id="endDate" name="endDate" type="text"  maxlength="20" class="input-medium Wdate"
				value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡号</th>
				<th>开始日期</th>
				<th>结束日期</th>
				<th>使用流量(M)</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="a">
			<tr>
				<td>${a.iccid}</td>
				<td>${beginDate}</td>
				<td>${endDate}</td>
				<td>${a.dataused}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>