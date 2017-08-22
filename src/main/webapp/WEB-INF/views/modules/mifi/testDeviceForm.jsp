<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>测试设备管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#imei").focus();
			$("#inputForm").validate();
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/testDevice/">测试设备列表</a></li>
		<li class="active"><a href="${ctx}/mifi/testDevice/form?id=${device.id}">测试设备<shiro:hasPermission name="mifi:test:edit">${not empty device.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mifi:test:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="testDevice" action="${ctx}/mifi/testDevice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label" for="imei">设备编号:</label>
			<div class="controls">
				<form:input path="imei" htmlEscape="false" maxlength="32" class="required abc"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="lendUserName">借出人:</label>
			<div class="controls">
				<form:input path="lendUserName" htmlEscape="false" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="remarks">说明:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mifi:test:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>