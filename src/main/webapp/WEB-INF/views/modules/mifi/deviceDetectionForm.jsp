<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备检测管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#imei").focus();
			$("#inputForm").validate();
			
			// 查看版本
			$('#viewVersion').click(function() {
				// 设备编号
				var imei = $('#imei').val();
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
			
			// 保存
			$('#btnSubmit').click(function() {
				// 拼装检查项字符串
				var itemStrs = '';
				$('.detectionItem').each(function() {
					itemStrs = itemStrs + '#' + $(this).find('input').val() + ',' + $(this).find('select').find('option:selected').text();			
				});
				if (!isEmpty(itemStrs)) {
					itemStrs = itemStrs.substring(1);
					$('#itemStrs').val(itemStrs);
				}
					
				$('#inputForm').submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/deviceDetection/">设备检测列表</a></li>
		<li class="active"><a href="${ctx}/mifi/deviceDetection/form?id=${deviceDetection.id}">设备检测<shiro:hasPermission name="mifi:detection:edit">${not empty region.id?'修改':'添加'}</shiro:hasPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="deviceDetection" action="${ctx}/mifi/deviceDetection/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="itemStrs"/>
		<div class="control-group">
			<label class="control-label" for="imei">设备编号:</label>
			<div class="controls">
				<form:input path="imei" htmlEscape="false" minlength="1" maxlength="32" class="required"/>&nbsp;&nbsp;
				<input id="viewVersion" class="btn btn-primary" type="button" value="查看版本"/>
			</div>
		</div>
		<c:forEach items="${fns:getDictList('device_detection_item')}" var="detectionItem">
			<div class="control-group detectionItem">
				<input type="hidden" value="${detectionItem.value},${detectionItem.label},${detectionItem.sort}" />
				<label class="control-label">${detectionItem.label}:</label>
				<div class="controls">
					<c:if test="${!empty detection.id }">
					<c:forEach items="${detection.itemList }" var="item">
					<c:if test="${detectionItem.value eq item.code }">
					<select class="input-small">
						<c:forEach items="${fns:getDictList('sys_grade')}" var="grade">
							<c:choose>
								<c:when test="${grade.label eq item.result }">
								<option value="${grade.value}" selected>${grade.label}</option>
								</c:when>
								<c:otherwise>
								<option value="${grade.value}">${grade.label}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
					</c:if>
					</c:forEach>
					</c:if>
					<c:if test="${empty detection.id }">
					<select class="input-small">
						<c:forEach items="${fns:getDictList('sys_grade')}" var="grade">
							<option value="${grade.value}">${grade.label}</option>
						</c:forEach>
					</select>
					</c:if>
				</div>
			</div>
		</c:forEach>
		<div class="control-group">
			<label class="control-label" for="useFlag">是否可用:</label>
			<div class="controls">
				<select id="useFlag" name="useFlag" class="input-small">
					<c:forEach items="${fns:getDictList('yes_no')}" var="yesNo">
						<option value="${yesNo.value}" <c:if test="${yesNo.value==deviceDetection.useFlag}">selected</c:if>>${yesNo.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="remarks">说明:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mifi:detection:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>