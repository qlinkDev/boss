<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>同步SIM卡</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript">
	$(document).ready(
		function() {
			$("#batchSyncCardInfo").click(
					function() {
						top.$.jBox.confirm("确认批量同步SIM卡？", "系统提示", function(
								v, h, f) {
							if (v == "ok") {
								$("#searchForm").attr("action",
										"${ctx}/mifi/syncCardInfo/batchSyncCardInfo")
										.submit();
								$("#searchForm").attr("action",
										"${ctx}/mifi/syncCardInfo/list");
							}
						}, {
							buttonsFocus : 1
						});
						top.$('.jbox-body .jbox-icon').css('top', '55px');
					});
		}
	);
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/syncCardInfo/init">待同步SIM卡列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/syncCardInfo/list"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<div>
			<label>卡号：</label><input id="iccid" name="iccid"
				type="text" maxlength="50" class="input-medium"
				value="${iccid}" /> 
			<label>插卡日期：</label> <input id="startDate" name="startDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${startDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			到 <input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${endDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			&nbsp;&nbsp; <input id="btnSubmit" class="btn btn-primary"
				type="submit" value="查询" />
			 &nbsp;<input id="batchSyncCardInfo"
				class="btn btn-primary" type="button" value="同步" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>卡号</th>
				<th>卡槽编号</th>
				<th>卡槽位置</th>
				<th>插卡时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.ICCID}</td>
					<td>${a.SIMBANKID}</td>
					<td>${a.SIMID}</td>
					<td>${a.stamp_updated}</td>
					<td><a href="${ctx}/mifi/syncCardInfo/syncCardInfo?sn=${a.ICCID}"
								onclick="return confirmx('确认同步SIM卡[${a.ICCID}]？', this.href)">同步SIM卡[${a.ICCID}]</a>
						</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>