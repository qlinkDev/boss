<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>工单管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		//查询条件清空
		$(function(){
			$('#btnClear').click(function(){
				$("#deviceSn").val('');
				$("#channelSn").val('');
				$("#problemType option:checked").attr("selected", false);
				$("#s2id_problemType a span").text('--请选择--');
				$("#level option:checked").attr("selected", false);
				$("#s2id_level a span").text('--请选择--');
				$("#status option:checked").attr("selected", false);
				$("#s2id_status a span").text('--请选择--');
				$("#startDate").val('');
				$("#endDate").val('');
			});
			
		});
		
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/wOrder/list">工单列表</a></li>
		<shiro:hasPermission name="mifi:workOrder:view"><li><a href="${ctx}/mifi/wOrder/formWo">工单添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="workOrderCondition" action="${ctx}/mifi/wOrder/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="initTag" name="initTag" type="hidden" value="1" />
		<label>设备编号：</label><form:input id="deviceSn" path="deviceSn" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>问题类型：</label>
		<select id="problemType" name="problemType" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item" items="${fns:getDictList('work_order_problem_type') }">
				<option value="${item.value}" ${item.value == condition.problemType ? 'selected':'' }>${item.label }</option>
			</c:forEach>
		</select>
		<label>优先级：</label>
		<select id="level" name="level" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item" items="${fns:getDictList('work_order_level') }">
				<option value="${item.value }" ${item.value == condition.level ? 'selected':'' }>${item.label }</option>
			</c:forEach>
		</select>
		<label>处理状态：</label>
		<select id="status" name="status" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach var="item" items="${fns:getDictList('work_order_status') }">
				<option value="${item.value }" ${item.value == condition.status ? 'selected':'' }>${item.label }</option>
			</c:forEach>
		</select>
		<label>创建时间：</label>
		<input id="startDate" name="startDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.startDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endDate\')||\'%y-%M-%d\'}'});" />&nbsp;到
		<input id="endDate" name="endDate"
				type="text" readonly="readonly" maxlength="20"
				class="input-small Wdate required" value="${condition.endDate}"
				onclick="WdatePicker({skin:'twoer',dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startDate\')}',maxDate:'%y-%M-%d'});" />		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;
		<input id="btnClear" class="btn btn-primary" type="button" value="清空"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>工单ID</th>
				<th>设备编号</th>
				<th>问题描述</th>
				<th>问题类型</th>
				<th>处理优先级</th>
				<th>处理状态</th>
				<th>创建时间</th>
				<th>创建者</th>
				<th>诊断结果</th>
				<shiro:hasPermission name="mifi:workOrder:view">
					<th>操作</th>
				</shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="workOrder">
			<tr>
				<td>${workOrder.wid}</td>
				<td>${workOrder.deviceSn}</td>
				<td width="40%;">${workOrder.problemDesc}</td>
				<td>${fns:getDictLabel(workOrder.problemType,'work_order_problem_type','')}</td>
				<td>${fns:getDictLabel(workOrder.level,'work_order_level','')}</td>
				<td>${fns:getDictLabel(workOrder.status,'work_order_status','')}</td>
				<td>${workOrder.createTime}</td>
				<td>${workOrder.createBy.loginName}</td>
				<td>${fns:getDictLabel(workOrder.pDiagnosisType,'work_order_problem_diagnosis_type','')}</td>
				<shiro:hasPermission name="mifi:workOrder:view">
					<td>
    					<a href="${ctx}/mifi/wOrder/view?wid=${workOrder.wid}">查看详情</a>
    					<c:if test="${workOrder.status != 3 }">
    					<a href="${ctx}/mifi/wOrder/formMe?wid=${workOrder.wid}">添加会话</a>
    					</c:if>
    					<%-- <a href="${ctx}/mifi/wOrder/viewMsg?wid=${workOrder.wid}">查看会话</a> --%>
    					<c:if test="${workOrder.status != 3 }">
    					<a href=" ${ctx}/mifi/wOrder/closeForm?wid=${workOrder.wid}" >关闭工单</a>
						</c:if>
					</td>
				</shiro:hasPermission>
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