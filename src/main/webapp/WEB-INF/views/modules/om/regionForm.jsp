<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>区域管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate();
			
			$('#btnSubmit').click(function() {
				var mcces = "";
				var countryCodes = "";
				var countryNames = "";
				$('.country').each(function(i) {
					if ($(this).attr('checked') == 'checked') {
						mcces = mcces + ',' + $(this).val();
						countryCodes = countryCodes + ',' + $(this).attr('data-code');
						countryNames = countryNames + ',' + $(this).attr('data-name');
					}
				});

				if (countryCodes == '') {
					top.$.jBox.info('请选择国家', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				
				if (mcces != '')
					mcces = mcces.substring(1);
				if (countryCodes != '')
					countryCodes = countryCodes.substring(1);
				if (countryNames != '')
					countryNames = countryNames.substring(1);
				
				$('#mcces').val(mcces);
				$('#countryCodes').val(countryCodes);
				$('#countryNames').val(countryNames);
				
				$('#inputForm').submit();
				
				
			});
		});
	</script>
	<style type="text/css">
		.contrySpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
			margin-top: 5px;
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/om/region/">区域列表</a></li>
		<li class="active"><a href="${ctx}/om/region/form?id=${region.id}">区域<shiro:hasPermission name="om:region:edit">${not empty region.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="om:region:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="region" action="${ctx}/om/region/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden id="mcces" path="mcces"/>
		<form:hidden id="countryCodes" path="countryCodes"/>
		<form:hidden id="countryNames" path="countryNames"/>
		<div class="control-group">
			<label class="control-label" for="name">名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" minlength="1" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="code">编码:</label>
			<div class="controls">
				<form:input path="code" htmlEscape="false" minlength="1" maxlength="20" class="required abc"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="defaultPrice">默认价格:</label>
			<div class="controls">
				<form:input path="defaultPrice" htmlEscape="false" class="required number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="remarks">说明:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="mcces">国家:</label>
			<div class="controls">
				<c:forEach items="${countryList}" var="country">
					<span class="contrySpan">
						<label>
							<input class="country" type="checkbox" value="${country[0] }" data-name="${country[1] }" data-code="${country[2] }" <c:if test="${country[3] eq '1' }">checked="checked"</c:if><c:if test="${country[4] eq '1' }">disabled="true"</c:if> />${country[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="om:region:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>