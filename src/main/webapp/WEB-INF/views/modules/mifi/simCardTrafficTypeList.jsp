<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>卡类型流量统计查询</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
	$(document).ready(function() {
			var searchFlag = true;
			$('#btnSubmit').click(function() {
				if (searchFlag) {
					var type = $('#type').val();
					if (isEmpty(type)) {
						top.$.jBox.info('请选择卡类型', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					$.jBox.tip("正在查询...", 'loading', {persistent: true});
					$(this).attr("disabled",true);
					$('#searchForm').submit();
					searchFlag = false;
				} 
			});
		$("#btnExport").click(
				function() {
					var type = $('#type').val();
					if (isEmpty(type)) {
						top.$.jBox.info('请选择卡类型', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					top.$.jBox.confirm("确认要导卡类型流量数据吗？", "系统提示", function(
							v, h, f) {
						if (v == "ok") {
							$("#searchForm").attr("action",
									"${ctx}/mifi/simCardTrafficType/export")
									.submit();
							$.jBox.closeTip();						
							}
					}, {
						buttonsFocus : 1
					});
					top.$('.jbox-body .jbox-icon').css('top', '55px');
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
			<li class="active"><a href="${ctx}/mifi/simCardTrafficType">卡类型流量统计查询列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/simCardTrafficType" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="initTag" name="initTag" type="hidden" value="${initTag}"/>
		<div>
				<label>卡类型：</label>
			<select id="type" name="type" class="input-medium">
				<option value="">--请选择--</option>
				<option value="null" <c:if test="${type == 'null'}">selected</c:if>>未绑定卡</option>
				<c:forEach
					items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}"
					var="cardTypeValue">
					<option value="${cardTypeValue.card_type}"
						<c:if test="${cardTypeValue.card_type==type}">selected</c:if>>${cardTypeValue.card_type_name}</option>
				</c:forEach>
			</select> 
			<label>开始日期：</label><input id="beginDate" name="beginDate" type="text"  maxlength="20" class="input-small Wdate"
				value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
			&nbsp;
			<label>结束日期：</label><input id="endDate" name="endDate" type="text"  maxlength="20" class="input-small Wdate"
				value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true});"/>
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
						&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出" />
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
				<td>${a.IMSI}</td>
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