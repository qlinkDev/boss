<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备检测管理</title>
	<meta name="decorator" content="default"/>
		<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
	document.addEventListener('DOMContentLoaded', function() {
		document.getElementById('btnSubmit').addEventListener('click', function() {
			$.jBox.tip("正在查询...", 'loading', {persistent: true});
			$(this).attr("disabled",true);
			$("#searchForm").submit();
		}, false);
	}, false);
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
		
		$(document).ready(function() {
			// 查看版本
			$('#viewVersion').click(function() {
				// 设备编号
				var imei = $('#likeImei').val();
				if (isEmpty(imei)) {
					top.$.jBox.info('请输入设备编号', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				
				var url = '${ctx}/mifi/mifiManage/version.json';
				$.post(
					url, 
					{
						imei:imei
					}, 
					function(data) {
						
						if (data.status == 'success') {
							top.$.jBox.info('设备' + imei + '的版本是：' + data.version, '系统提示');
						} else {
							top.$.jBox.info(data.message, '系统提示');
						}
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
				);
	        	
			});
		});
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/deviceDetection/">设备检测列表</a></li>
		<shiro:hasPermission name="mifi:detection:edit"><li><a href="${ctx}/mifi/deviceDetection/form">设备检测添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="deviceDetectionCondition" action="${ctx}/mifi/deviceDetection/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>设备编号：</label>
		<form:input path="likeImei" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>是否可用：</label> 
		<select id="eqUseFlag" name="eqUseFlag" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${fns:getDictList('yes_no')}" var="yesNo">
				<option value="${yesNo.value}" <c:if test="${yesNo.value==deviceDetectionCondition.eqUseFlag}">selected</c:if>>${yesNo.label}</option>
			</c:forEach>
		</select>
		<label>代理商：</label>
		<select id="eqSourceType" name="eqSourceType" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}" var="sourceTypeValue">
				<option value="${sourceTypeValue.channel_name_en}"
					<c:if test="${sourceTypeValue.channel_name_en==deviceDetectionCondition.eqSourceType}">selected</c:if>>${sourceTypeValue.channel_name}
				</option>
			</c:forEach>
		</select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="查询"/>
		&nbsp;&nbsp;<input id="viewVersion" class="btn btn-primary" type="button" value="查看版本"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>设备编号</th>
				<th>是否可用</th>
				<th>代理商</th>
				<c:forEach items="${fns:getDictList('device_detection_item')}" var="detectionItem">
				<th>${detectionItem.label}</th>
				</c:forEach>
				<th>创建人</th>
				<th>创建时间</th>
				<th>说明</th>
				<shiro:hasPermission name="mifi:detection:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="detection">
			<tr>
				<td>${detection.imei}</td>
				<td>${fns:getDictLabel(detection.useFlag, 'yes_no', '未配置状态')}</td>
				<td>${fns:getLabelByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ', detection.sourceType)}</td>
				
				<c:forEach items="${detection.itemList}" var="item">
					<td>${item.result}</td>
				</c:forEach>
				
				<td>${detection.createBy.loginName}</td>
				<td><fmt:formatDate value="${detection.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td style="word-wrap: break-word;width: 400px;"><div style="width: 400px;">${detection.remarks}</div></td>
				<shiro:hasPermission name="mifi:detection:edit"><td>
    				<a href="${ctx}/mifi/deviceDetection/form?id=${detection.id}">修改</a>
					<a href="${ctx}/mifi/deviceDetection/delete?id=${detection.id}" onclick="return confirmx('确认要删除该设备检测吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
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