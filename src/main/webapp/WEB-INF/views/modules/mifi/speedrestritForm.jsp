<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>测试设备管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {
		$("#inputForm").validate({
			rules: {
				speedrestritmcc: {remote: {
                           url: "${ctx}/mifi/speedrestrit/isRankExists",     //后台处理程序
                           type: "get",               //数据发送方式
                           dataType: "text",           //接受数据格式   
                           data: {                     //要传递的数据
                        	   speedrestritmcc: function() {
                                   return $("#speedrestritmcc").val();
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
		<li ><a href="${ctx}/mifi/speedrestrit">限速管理</a></li>
		<li class="active"><a href="${ctx}/mifi/speedrestrit/form?id=${speedrestrit.id}">限速${not empty speedrestrit.id?'修改':'添加'}</a></li>
	</ul>
	<br />

	<tags:message content="${message}" />

	<form:form id="inputForm" modelAttribute="speedrestrit"
		action="${ctx}/mifi/speedrestrit/save" method="post"
		class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
			<label class="control-label" for="speedrestritmcc">speedrestritmcc:</label>
			<div class="controls">
				<form:input path="speedrestritmcc" htmlEscape="false" maxlength="10"
				class=" digits required"	 />
			<span class="help-inline">(十六进制)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="countryName">国家名称:</label>
			<div class="controls">
				<form:input path="countryName" htmlEscape="false" maxlength="50" class="required" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="firstleveldata">firstleveldata:</label>
			<div class="controls">
				<form:input path="firstleveldata" htmlEscape="false" maxlength="10"
					class=" digits required" />
			<span class="help-inline">(十六进制)</span>
			<span class="help-inline"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="firstlevelspeed">firstlevelspeed:</label>
			<div class="controls">
				<form:input path="firstlevelspeed" htmlEscape="false" maxlength="10"
					class=" digits required" />
			<span class="help-inline">(十六进制)</span>

			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="secondleveldata">secondleveldata:</label>
			<div class="controls">
				<form:input path="secondleveldata" htmlEscape="false" maxlength="10"
					class=" digits required" />
			<span class="help-inline">(十六进制)</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="secondlevelspeed">secondlevelspeed:</label>
			<div class="controls">
				<form:input path="secondlevelspeed" htmlEscape="false"
					maxlength="10" class=" digits required" />
			<span class="help-inline">(十六进制)</span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="ownerMcc">ownerMcc:</label>
			<div class="controls">
				<form:input path="ownerMcc" htmlEscape="false" maxlength="100" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="sourceType:">sourceType:</label>
			<div class="controls">
				<form:input path="sourceType" htmlEscape="false" maxlength="50" />
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