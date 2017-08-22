<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>MIFI订单备货查询</title>
<meta name="decorator" content="default" />
<style type="text/css">
		#tableDiv {overflow:auto;}
		table th {white-space: nowrap;}
		table td {white-space: nowrap;}
	}
</style>
<script type="text/javascript">
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
		<li class="active"><a href="${ctx}/mifi/mifiOrderStock/">MIFI订单备货列表</a></li>
	</ul>
	<form:form id="searchForm" action="${ctx}/mifi/mifiOrderStock/"
		method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}" />
		<input id="pageSize" name="pageSize" type="hidden"
			value="${page.pageSize}" />
		<input id="initTag" name="initTag" type="hidden" value="${initTag}" />
		<div>
			<label>代理商：</label> <select id="sourceType" name="sourceType" class="input-small">
				<option value="">--请选择--</option>
				<c:forEach
					items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}"
					var="sourceTypeValue">
					<option value="${sourceTypeValue.channel_name_en}"
						<c:if test="${sourceTypeValue.channel_name_en==sourceType}">selected</c:if>>${sourceTypeValue.channel_name}</option>
				</c:forEach>
			</select> <label>备货状态：</label> <select id="stockStatus" name="stockStatus"
				class="input-small">
				<option value="">--请选择--</option>
				<c:forEach items="${fns:getDictList('order_stock_status')}"
					var="orderStockStatus">
					<option value="${orderStockStatus.value}"
						<c:if test="${orderStockStatus.value==stockStatus}">selected</c:if>>${orderStockStatus.label}</option>
				</c:forEach>
			</select> <label>行程开始日期：</label> <input id="startDate" name="startDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${startDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" /> 
				到
				<input id="endDate" name="endDate" type="text"
				readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${endDate}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});" />
			&nbsp;&nbsp; 
			<input id="btnSubmit" class="btn btn-primary"
				type="submit" value="查询" />
		</div>
	</form:form>
	<tags:message content="${message}" />
	<div id="tableDiv">
		<table id="contentTable"
			class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>代理商</th>
					<th>地区</th>
					<th>备货状态</th>
					<th>SIM卡数量</th>
					<th>地区中文名</th>
					<th>地区英文名</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.list}" var="a">
					<tr>
						<td>${a.source_type}</td>
						<td>${a.allowed_mcc}</td>
						<td>${a.stock_status}|${fns:getDictLabel(a.stock_status, 'order_stock_status', '未知状态')}</td>
						<td>${a.equipment_cnt}</td>
						<td>${a.allowed_mcc_cn}</td>
						<td>${a.allowed_mcc_en}</td>
						<td><c:if test="${a.stock_status!='1'}">
								<a
									href="${ctx}/mifi/mifiOrderStock/stockSimCard?allowed_mcc=${a.allowed_mcc}&source_type=${a.source_type}&startDate=${startDate}&endDate=${endDate}&sourceType=${sourceType}&stockStatus=${stockStatus}"
									onclick="return confirmx('确认[已备SIM卡]？', this.href)">确认已备SIM卡</a>
							</c:if></td>
					</tr>
				</c:forEach>
			</tbody>
			</table>
		</div>
	<div class="pagination">${page}</div>
</body>
</html>