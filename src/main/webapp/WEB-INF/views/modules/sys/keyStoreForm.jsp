<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>密钥管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate();
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/keyStore/">密钥列表</a></li>
		<li class="active">
			<a href="${ctx}/sys/keyStore/form?keyId=${keyStore.keyId}">密钥
				<shiro:hasPermission name="sys:keyStore:edit">${not empty keyStore.keyId?'修改':'添加'}</shiro:hasPermission>
				<shiro:lacksPermission name="sys:keyStore:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="keyStore" action="${ctx}/sys/keyStore/save" method="post" class="form-horizontal">
		<form:hidden path="keyId"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" for="keyDesc">密钥描述:</label>
			<div class="controls">
				<form:input path="keyDesc" htmlEscape="false" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="keyType">加密类型:</label>
			<div class="controls">
				<form:input path="keyType" htmlEscape="false" maxlength="10" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">来源:</label>
			<div class="controls">
				<form:input path="sourceType" htmlEscape="false" maxlength="20" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="keyValue">密钥:</label>
			<div class="controls">
				<form:input path="keyValue" htmlEscape="false" maxlength="2000" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="remarks">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:keyStore:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
