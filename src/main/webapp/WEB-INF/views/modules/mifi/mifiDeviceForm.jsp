<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>MIFI设备管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#inputForm").validate();
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/mifiDevice/init">MIFI设备列表</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/form?id=${mifiBasicInfo.id}">MIFI设备修改</a></li>
	</ul><br/>
	
	<form:form id="inputForm" modelAttribute="mifiBasicInfo" action="${ctx}/mifi/mifiDevice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" for="sn">设备序列号:</label>
			<div class="controls">
				<form:input path="sn" htmlEscape="false" readonly="true" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">所属渠道:</label>
			<div class="controls">
				<form:select path="sourceType" class="input-medium required" style="width: 220px;">
		        	<form:option value="" label="请选择"/>
		        	<form:options items="${fns:getChannelList()}" itemLabel="channelName" itemValue="channelNameEn" htmlEscape="false"/>
		        </form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="testIp">业务ip及端口:</label>
			<div class="controls">
				<form:input path="testIp" htmlEscape="false" maxlength="50"/>
				<span class="help-inline">例如 192.168.0.1.3306(IP地址.端口号)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="testUpdateIp">升级ip及端口:</label>
			<div class="controls">
				<form:input path="testUpdateIp" htmlEscape="false" maxlength="50"/>
				<span class="help-inline">例如 192.168.0.1.3306(IP地址.端口号)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="softsimType">是否软卡:</label>
			<div class="controls">
				<form:input path="softsimType" htmlEscape="false" maxlength="1" class="number required"/>
				<span class="help-inline">(0_不是软卡模式，1_是软卡模式)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="webPortalFlag">是否弹窗:</label>
			<div class="controls">
				<form:input path="webPortalFlag" htmlEscape="false" maxlength="1" class="number required"/>
				<span class="help-inline">(0_不弹窗，1_弹窗)</span>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>