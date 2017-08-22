<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>MIFI设备状态变更历史查询</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出设备状态数据吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/export").submit();
						$("#searchForm").attr("action","${ctx}/mifi/mifiStatus/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li class="active"><a href="${ctx}/mifi/mifiStatus/">MIFI设备状态变化列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiStatus/"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<input id="initTag" name="initTag" type="hidden" value="${initTag}" />
		<div>
			<label>设备序列号：</label><input id="imei" name="imei" type="text"
				maxlength="50" class="input-small required" value="${imei}" />
			&nbsp; <label>开始日期：</label><input id="beginDate" name="beginDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${beginDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			<label>结束日期：</label><input id="endDate" name="endDate" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${endDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary"
				type="submit" value="查询" /> &nbsp;<input id="btnExport"
				class="btn btn-primary" type="button" value="导出" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>设备序列号</th>
				<th>设备状态</th>
				<th>时间</th>
				<th>所在地区中文名</th>
				<th>所在地区英文名</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="mifiStatus">
				<tr>
					<td>${mifiStatus.imei}</td>
					<td>${mifiStatus.uestatus}|${fns:getDictLabel(mifiStatus.uestatus, 'mifi_uestatus', '未配置状态')}</td>
					<td>${mifiStatus.stamp_created}</td>
					<td>${mifiStatus.country_name_cn}</td>
					<td>${mifiStatus.country_name_en}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>