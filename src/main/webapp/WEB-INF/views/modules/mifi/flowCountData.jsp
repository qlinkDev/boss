<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域流量查询</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		.tableDiv {overflow:auto; margin-top: 10px;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			// 生成统计任务
			$('#btnCount').click(function() {
				window.location.href = "${ctx}/mifi/flowCount/form";
			});
			
			// 执行统计任务
			$('#saveCount').click(function() {
				var $saveCountA = $(this);
				var recordId = $(this).attr('data-recordId');
				top.$.jBox.confirm("是否执行当前统计任务?", "系统提示", function(v, h, f) {
					if (v == "ok") {
						var url = '${ctx}/mifi/flowCount/changeStatus.json';
						$.post(url, {recordId:recordId}, function(data) {
							if ('success' == data.status) {
								var url = '${ctx}/mifi/flowCount/save.json';
								$.post(url, {recordId:recordId}, function(data) {
									top.$.jBox.info(data.message, '系统提示');
									window.location.href = '${ctx}/mifi/flowCount?queryData=yes';
								});
								$('#record_' + recordId).text('统计中');
								$saveCountA.text('');
							} else {
								top.$.jBox.info(data.message, '系统提示');
							}
						});
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
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/flowCount/">流量统计任务列表</a></li>
		<li class="active"><a href="${ctx}/mifi/flowCount/dataList?id=${id }">数据列表</a></li>
	</ul>
	<!-- tab E -->
	
	<form:form id="searchForm" action="${ctx}/mifi/flowCount/dataList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="id" name="id" type="hidden" value="${id}"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 数据列表 S -->
	<div class="tableDiv">
		<div>统计汇总(流量[M])</div>
		<table id="contentTableSummary" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>订单总数</th>
					<th>设备总数</th>
					<th>总流量</th>
					<th>平均流量</th>
					<th>非漫游率</th>
					<c:forEach items="${countryList }" var="country">
					<th>${country }</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${summaryList}" var="summary">
				<tr>
					<td>${summary.orderTotal }</td>
					<td>${summary.deviceTotal }</td>
					<td>${summary.flowTotal }</td>
					<td>${summary.flowAverage }</td>
					<td><fmt:formatNumber type="number" value="${(summary.oneCountryDeviceTotal/summary.deviceTotal)*100 }" pattern="0.0000" maxFractionDigits="4"/>%</td>
					<c:forEach items="${countryList }" var="country">
						<td>
							<c:forEach items="${summary.itemList }" var="item">
								<c:if test="${item.countryName == country }">
									${item.flow }/<fmt:formatNumber type="number" value="${item.flow/summary.deviceTotal }" pattern="0.00" maxFractionDigits="2"/> 
								</c:if>
							</c:forEach>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	
	<div class="tableDiv">
		<div>统计详情(流量[M])</div>
		<table id="contentTableCount" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>设备编号</th>
					<th>订单编号</th>
					<th>开始时间</th>
					<th>结束时间</th>
					<th>总流量</th>
					<c:forEach items="${countryList }" var="country">
					<th>${country }</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="count">
				<tr>
					<td>${count.imei }</td>
					<td>${count.orderCode }</td>
					<td>${count.startDate }</td>
					<td>${count.endDate }</td>
					<td>${count.flowTotal }</td>
					<c:forEach items="${countryList }" var="country">
						<td>
							<c:forEach items="${count.itemList }" var="item">
								<c:if test="${item.countryName == country }">${item.flow }</c:if>
							</c:forEach>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>