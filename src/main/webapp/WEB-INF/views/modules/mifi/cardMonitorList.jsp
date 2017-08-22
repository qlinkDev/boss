<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>卡监控信息管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	    $(document).ready(function() {
			$('#btnSubmit').click(function() {
				$('#btnSubmit').attr("disabled", "");
				$('#searchForm').submit();
			});
			
			$('#btnExport').click(function() {
				top.$.jBox.confirm("确认导出？", "操作提示", function(v, h, f) {
					if (v == "ok") {
						$("#searchForm").attr('action', "${ctx}/mifi/cardMonitor/export");
						$("#searchForm").submit();
						$("#searchForm").attr('action', "${ctx}/mifi/cardMonitor/");
					}
				}, {buttonsFocus : 1});
				top.$('.jbox-body .jbox-icon').css('top', '55px');
				top.$('.jbox').css('top', '180px');
			});
		});
		
		function updata(id){
	    	$.jBox("iframe:${ctx}//mifi/cardMonitor/form?id="+id,{title:"操作说明", width:400,height :350, persistent:true, buttons: { '关闭': 'ok' }});
	    }
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
		<li class="active"><a href="${ctx}/mifi/cardMonitor/">卡监控信息列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="cardMonitorCondition" action="${ctx}/mifi/cardMonitor/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label><form:input path="likeImei" htmlEscape="false" maxlength="32" class="input-small"/>
	    <label>状态：</label>
	    <form:select path="eqStatus" class="input-small">
	    	<form:option value="" label="请选择"/>
	    	<form:options items="${fns:getDictList('card_monitor_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
	    <label>类型：</label>
	    <form:select path="eqType" class="input-small">
	    	<form:option value="" label="请选择"/>
	    	<form:options items="${fns:getDictList('card_monitor_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
	&nbsp; <label>国家:</label>
			<select id="mcc" name="mcc"  class="input-small">
				<option value="">--请选择--</option>
				<c:forEach var="item"  items="${mccList }">
					<option value="${item[0] }" <c:if test="${item[0] eq condtion.mcc }">selected</c:if> >${item[1] }</option>
				</c:forEach>
			</select>
	    <label>故障：</label>
	    <form:select path="eqFaultCode" class="input-large">
	    	<form:option value="" label="请选择"/>
	    	<form:options items="${fns:getDictList('card_monitor_fault_code')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
	    </form:select>
		<label>发生时间从：</label><form:input id="geCreateDate" path="geCreateDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:00:00',isShowClear:true});"/>
		<label>到：</label><form:input id="leCreateDate" path="leCreateDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:59:59',isShowClear:true});"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出" />
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>设备编号</th><th>国家</th><th>状态</th><th>类型</th><th>故障</th><th>发生时间</th><th>处理人</th><th>处理时间</th><th>说明</th><shiro:hasPermission name="mifi:cardMonitor:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="monitor">
			<tr>
				<td>${monitor.imei}</td>
				<td>
					<c:if test="${not empty monitor.countryName}">
						${monitor.countryName}
					</c:if>
					<c:if test="${empty monitor.countryName && not empty monitor.mcc }" var="countryIsEmpty">
						${monitor.mcc}<a href="${ctx}/mifi/cardMonitor/getCountry?id=${monitor.id}" onclick="return confirmx('确认获取该记录获取国家信息？', this.href)">(获取国家信息)</a>
					</c:if>
				</td>
				<td>${fns:getDictLabel(monitor.status, "card_monitor_status", "未知")}</td>
				<td>${fns:getDictLabel(monitor.type, "card_monitor_type", "未知")}</td>
				<td>${fns:getDictLabel(monitor.faultCode, "card_monitor_fault_code", "未知")}</td>
				<td><fmt:formatDate value="${monitor.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${monitor.handleBy.name }</td>
				<td><fmt:formatDate value="${monitor.handleDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>${monitor.remarks }</td>
				<td>
				    <shiro:hasPermission name="mifi:cardMonitor:edit">
				    	<c:if test="${monitor.status eq 'NEW' || monitor.status eq 'SENT_MSG'}">
				    		<a href="javascript:void(0);" id="updatas"  onclick="updata('${monitor.id}')">已处理 </a>
				    	</c:if>
				    	<c:if test="${monitor.status eq 'HANDLED'}">
				    		<a href="javascript:void(0);" id="updatas"  onclick="updata('${monitor.id}')">修改 </a>
				    	</c:if>
				    	
				    	<a href="${ctx}/mifi/cardMonitor/delete?id=${monitor.id}" onclick="return confirmx('确认删除该记录？', this.href)">删除</a>
				    	
				    </shiro:hasPermission>
				</td>
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