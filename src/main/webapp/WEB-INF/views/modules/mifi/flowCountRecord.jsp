<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域流量查询</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<style type="text/css">
		#tableDiv {overflow:auto;}
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
			$('.saveCount').click(function() {
				var $saveCountA = $(this);
				var recordId = $(this).attr('data-recordId');
				top.$.jBox.confirm("此操作需要进行大量查询和统计,耗时很长是否执行?", "系统提示", function(v, h, f) {
					if (v == "ok") {
						var url = '${ctx}/mifi/flowCount/changeStatus.json';
						$.post(url, {recordId:recordId}, function(data) {
							if ('success' == data.status) {
								var url = '${ctx}/mifi/flowCount/save.json';
								$.post(url, {recordId:recordId}, function(data) {
								});
								window.location.href = '${ctx}/mifi/flowCount?queryData=yes';
							} else {
								if (!isEmpty(data.message)) {
									top.$.jBox.info(data.message, '系统提示');
									top.$('.jbox-body .jbox-icon').css('top', '55px');
								}
							}
						});
					}
				}, {
					buttonsFocus : 1
				});
				top.$('.jbox-body .jbox-icon').css('top', '55px');
			});
			
			// 导入数据
			$('.exportData').click(function() {
				var id = $(this).attr('data-recordId');
				top.$.jBox.confirm("确认要导出卡数据吗？", "系统提示", function(v, h, f) {
					if (v == "ok") {
						$("#eqId").val(id);
						$("#searchForm").attr("action", "${ctx}/mifi/flowCount/export").submit();
						$("#eqId").val('');
						$("#searchForm").attr("action", "${ctx}/mifi/flowCount?queryData=yes");
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
		<li class="active"><a href="${ctx}/mifi/flowCount/">流量统计任务列表</a></li>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="flowCountRecordCondition" action="${ctx}/mifi/flowCount/" method="post" class="breadcrumb form-search">
		<input id="queryData" name="queryData" type="hidden" value="yes"/>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="eqId" name="eqId" type="hidden" value="${flowCountRecordCondition.eqId}"/>
		<label>区域：</label> 
		<form:select path="eqRegionId" class="input-medium required">
        	<form:option value="" label="请选择"/>
        	<form:options items="${fns:getRegionList()}" itemLabel="name" itemValue="id" htmlEscape="false"/>
        </form:select>
		<label>统计开始时间：</label>
		<form:input id="geStartDate" path="geStartDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leStartDate" path="leStartDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		<label>统计结束时间：</label>
		<form:input id="geEndDate" path="geEndDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		到
		<form:input id="leEndDate" path="leEndDate" type="text" readonly="true" maxlength="20" class="input-medium Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<shiro:hasPermission name="mifi:flowCount:count">
		&nbsp;&nbsp;<input id="btnCount" class="btn btn-primary" type="button" value="生成任务"/>
		</shiro:hasPermission>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	
	<div id="tableDiv">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>统计区域</th>
					<th>开始时间</th>
					<th>结束时间</th>
					<th>创建时间</th>
					<th>执行时间</th>
					<th>结束时间</th>
					<th>状态</th>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${page.list}" var="record">
				<tr>
					<td>${record.region.name }</td>
					<td><fmt:formatDate value="${record.startDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${record.endDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${record.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td id="execute_${record.id }"><fmt:formatDate value="${record.executeDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${record.finishDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td id="status_${record.id }">${fns:getDictLabel(record.status, 'flow_count_record_status', '未知类型')}</td>
					<td>
						<c:if test="${record.status == 'NEW' }">
						<a class="saveCount" data-recordId="${record.id }" href="javascript:void(0);">执行</a>
						<a href="${ctx}/mifi/flowCount/delete?id=${record.id}">删除</a>
						</c:if>
						<c:if test="${record.status == 'ENDED' }">
						<shiro:hasPermission name="mifi:flowCount:view">
						<a href="${ctx}/mifi/flowCount/dataList?id=${record.id}">查看</a>
						<a class="exportData" data-recordId="${record.id }" href="javascript:void(0);">导出</a>
						</shiro:hasPermission>
						</c:if>
						<c:if test="${record.status == 'FAIL' }">
						<shiro:hasPermission name="mifi:flowCount:del">
						<a href="${ctx}/mifi/flowCount/delete?id=${record.id}">删除</a>
						</shiro:hasPermission>
						</c:if>
					</td>
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