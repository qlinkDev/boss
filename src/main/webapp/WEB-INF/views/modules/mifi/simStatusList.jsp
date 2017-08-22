<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>SIM卡状态变更历史查询</title>
	<meta name="decorator" content="default" />
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		var searchFlag = true;
		$(document).ready(function() {
			// 导出
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出SIM卡状态数据吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						$("#searchForm").attr("action","${ctx}/mifi/simStatus/export").submit();
						$("#searchForm").attr("action","${ctx}/mifi/simStatus/list");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			// 查询
			$('#btnSubmit').click(function() {
				if (searchFlag) {
					var iccid = $('#iccid').val();
					if (isEmpty(iccid)) {
						top.$.jBox.info('请输入卡号', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					}
					$.jBox.tip("正在查询...", 'loading', {persistent: true});
					$(this).attr("disabled",true);
					$('#searchForm').submit();
					searchFlag = false;
				} 
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
		<li class="active"><a href="${ctx}/mifi/simStatus/init">SIM卡状态变更历史列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/simStatus/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
		<div>
			<label>卡号：</label><input id="iccid" name="iccid" type="text" maxlength="50" class="input-medium required" value="${iccid}" />&nbsp; 
			<label>开始日期：</label>
			<input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${beginDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			<label>结束日期：</label>
			<input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-small Wdate required" value="${endDate}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />&nbsp;&nbsp;
			
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" />&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="导出" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>ID</th>
				<th>卡号</th>
				<th>卡状态</th>
				<th>时间</th>
				<th>所在地区中文名</th>
				<th>所在地区英文名</th>
				<th>注册国家的区域码</th>
				<th>卡箱服务器连接状态</th>
				<th>总流量(M)</th>
				<th>已用流量(M)</th>
				<th>流量增值(M)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="a">
				<tr>
					<td>${a.id}</td>
					<td>${a.iccid}</td>
					<td>${a.usimstatus}|${fns:getDictLabel(a.usimstatus, 'usimstatus', '未知状态')}</td>
					<td><fmt:formatDate value="${a.stamp_created}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${a.country_name_cn}</td>
					<td>${a.country_name_en}</td>
					<td>${a.reg_mcc}</td>
					<td>${a.nwstatus}</td>
					<td>${a.data_cap}</td>
					<td>${a.data_used}</td>
					<td>${a.data_add}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>