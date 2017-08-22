<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>SIMBank管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			rules: {
				simbankid: {remote: {
                           url: "${ctx}/mifi/simbankip/isRankExists",     //后台处理程序
                           type: "get",               //数据发送方式
                           dataType: "text",           //接受数据格式   
                           data: {                     //要传递的数据
                        	   simbankid: function() {
                                   return $("#simbankid").val();
                               },
                               id: $("#id").val(),
                           }
                       }
				}
			},
			submitHandler: function(form){
				loading('正在提交，请稍等...');
				form.submit();
			}
		});
		
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/mifi/simbankip">SIMBank管理</a></li>
		<li class="active"><a href="${ctx}/mifi/simbankip/form?id=${simbankip.id}">SIMBank${not empty simbankip.id?'修改':'添加'}</a></li>
	</ul>
	<br />

	<tags:message content="${message}" />

	<form:form id="inputForm" modelAttribute="simBankIp"
		action="${ctx}/mifi/simbankip/save" method="post"
		class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label" for="simbankid">simbankid:</label>
			<div class="controls">
				<form:input path="simbankid" htmlEscape="false" maxlength="11"
				class=" digits required"/>
			<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="onlineip">onlineip:</label>
			<div class="controls">
				<form:input path="onlineip" htmlEscape="false" maxlength="30"
					class="  required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="defaultip">defaultip:</label>
			<div class="controls">
				<form:input path="defaultip" htmlEscape="false" maxlength="30"
					class=" required" />
				<span class="help-inline">例如 192.168.0.1.3306(IP地址.端口号)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="backupip">backupip:</label>
			<div class="controls">
				<form:input path="backupip" htmlEscape="false" maxlength="30"
					class=" required" />
				<span class="help-inline">例如 192.168.0.1.3306(IP地址.端口号)</span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit"
				value="保 存" />&nbsp; <input id="btnCancel" class="btn" type="button"
				value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>