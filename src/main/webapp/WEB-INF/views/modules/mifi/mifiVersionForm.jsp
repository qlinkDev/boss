<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI版本管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#inputForm").validate();
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/version">MIFI版本信息列表</a></li>
		<li class="active"><a href="${ctx}/mifi/version/form?id=${mifiVersion.id}">MIFI版本信息修改</a></li>
	</ul><br/>
	
	<form:form id="inputForm" modelAttribute="mifiVersion" action="${ctx}/mifi/version/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" for="IMEI_6200">设备序列号:</label>
			<div class="controls">
				<form:input path="IMEI_6200" htmlEscape="false" readonly="true" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sn">升级权限:</label>
			<div class="controls">
				<form:input path="UPDATEFLAG" htmlEscape="false" maxlength="1" digits="true" class="required"/>
				<span class="help-inline">(0_不升级,1_升级)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">限速权限:</label>
			<div class="controls">
				<form:input path="SPEEDLIMITFLAG" htmlEscape="false" maxlength="1" digits="true" class="required"/>
				<span class="help-inline">(0_不限速,1_限速)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="testIp">LCD上语言显示:</label>
			<div class="controls">
				<form:input path="lcd_version_Type" htmlEscape="false" maxlength="1" digits="true" class="required"/>
				<span class="help-inline">(0_英文版本,1_中文版本)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="testUpdateIp">上传log标志:</label>
			<div class="controls">
				<form:input path="log_file_Type" htmlEscape="false" maxlength="1" digits="true" class="required"/>
				<span class="help-inline">(0_不上传log,1_上传yym的log,2_上传yy_daemon的log,3_代表上传yym和yy_daemon的log)</span>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>