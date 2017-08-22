<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI流量查询</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 查询
			$('#btnSubmit').click(function() {
				var imei = $('#imei').val();
				if (isEmpty(imei)) {
					top.$.jBox.info('请输入设备序列号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				$('#searchForm').attr('action', '${ctx}/mifi/mifiTraffic/');
				$.jBox.tip("正在查询...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
				$('#searchForm').submit();
			});
			// 查询今天流量
			$('#btnToday').click(function() {
				var imei = $('#imei').val();
				if (isEmpty(imei)) {
					top.$.jBox.info('请输入设备序列号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				$('#searchForm').attr('action', '${ctx}/mifi/mifiTraffic/listForToday');
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
		<li class="active"><a href="${ctx}/mifi/mifiTraffic/">设备流量列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiTraffic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="initTag" name="initTag" type="hidden" value="${initTag}"/>
		<div>
			<label>设备序列号：</label><input id="imei" name="imei" type="text" maxlength="50" class="input-small required" value="${imei}"/>
			&nbsp;
			<label>开始日期：</label><input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required"
				value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-%d\'}'});"/>
			<label>结束日期：</label><input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required"
				value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}',isShowClear:true});"/>
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
			&nbsp;&nbsp;<input id="btnToday" class="btn btn-primary" type="button" value="查询今天流量"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>所在地区中文名</th>
				<th>所在地区英文名</th>
				<th>所在地区编号</th>
				<th>日期</th>
				<th>使用流量(M)</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="mifiTraffic">
			<tr>
				<td>${mifiTraffic[1]}</td>
				<td>${mifiTraffic[2]}</td>
				<td>${mifiTraffic[0] }</td>
				<td>${mifiTraffic[3]}</td>
				<td>${mifiTraffic[4]}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>