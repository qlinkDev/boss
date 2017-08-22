<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通知接收管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			$("#name").focus();
			$("#inputForm").validate();
			
			// 初始化：类型选择‘卡监控’时显示faultCodes
			var type = $('#type').find('option:selected').val();
			if('CARD_MONITOR' == type) {
				$('#faultCodesDiv').show();
			} else {
				$('#faultCodesDiv').hide();
			}
			// 类型change事件
			$('#type').change(function() {
				var type = $('#type').find('option:selected').val();
				if('CARD_MONITOR' == type) {
					$('#faultCodesDiv').show();
				} else {
					$('#faultCodesDiv').hide();
				}
			});
			// 所有类型和单个类型不能同时存在
			$('#faultCodesALL').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('.faultCodesOther').attr('checked', false);
				}
			})
			$('.faultCodesOther').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('#faultCodesALL').attr('checked', false);
				}
				// 如果故障编码全部被选中，则改成选中'ALL'
				if ($('.faultCodesOther:checked').length == $('.faultCodesOther').length) {
					$('.faultCodesOther').attr('checked', false);
					$('#faultCodesALL').attr('checked', true);
				}
			})
			
			// 保存
			$('#btnSubmit').click(function() {
				// 手机号码和邮箱不能同时为空
				var phones = $('#phones').val();
				var emails = $('#emails').val();
				if (isEmpty(phones) && isEmpty(emails)) {
					top.$.jBox.info('手机号码和邮箱不能同时为空', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				// 如果类型选择的'卡监控',则必须选择故障编码
				var type = $('#type').find('option:selected').val();
				if('CARD_MONITOR' == type) {
					var faultCodes = '';
					if ($('#faultCodesALL').attr('checked') == 'checked') {
						faultCodes = ',ALL';
					} else {
						$('.faultCodesOther').each(function(i) {
							if ($(this).attr('checked') == 'checked') {
								faultCodes += ',' + $(this).val();
							} 
						});
					}
					if (faultCodes == '') {
						top.$.jBox.info('请选择需要监控的故障编码!', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					} else {
						faultCodes = faultCodes.substring(1);
						$('#faultCodes').val(faultCodes);
					}
				} else {
					$('#faultCodes').val('');
				}
				$('#inputForm').submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/noticeReceive/">通知接收列表</a></li>
		<li class="active"><a href="${ctx}/sys/noticeReceive/form?id=${noticeReceive.id}">通知接收<shiro:hasPermission name="sys:noticeReceive:edit">${not empty noticeReceive.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sys:noticeReceive:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="noticeReceive" action="${ctx}/sys/noticeReceive/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="faultCodes"/>
		<div class="control-group">
			<label class="control-label" for="name">名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" minlength="1" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="type">类别:</label>
			<div class="controls">
				<form:select path="type">
					<form:options items="${fns:getDictList('notice_receive_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="sourceType">所属渠道:</label>
			<div class="controls">
				<select id="sourceType" name="sourceType">
					<c:forEach items="${fns:getListByTableAndWhere('om_channel','channel_name_en','channel_name',' and del_flag = 0 ')}" var="sourceTypeValue">
						<option value="${sourceTypeValue.channel_name_en}"
							<c:if test="${sourceTypeValue.channel_name_en==noticeReceive.sourceType}">selected</c:if>>${sourceTypeValue.channel_name}
						</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group" id="faultCodesDiv">
			<label class="control-label" for="faultCodes">故障编码:</label>
			<div class="controls">
				<span class="dictSpan">
					<label>
						<input id="faultCodesALL" type="checkbox" value="ALL" <c:if test="${noticeReceive.faultCodes eq 'ALL' }">checked="checked"</c:if>/>所有
					</label>
				</span>
				<c:forEach items="${dictList }" var="dict">
					<span class="dictSpan">
						<label>
							<input class="faultCodesOther" type="checkbox" value="${dict[0] }" <c:if test="${dict[2] eq '1' }">checked="checked"</c:if> />${dict[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="phones">手机号码:</label>
			<div class="controls">
				<form:input path="phones" htmlEscape="false" minlength="0" maxlength="255" class="mobiles"/>
				(多个号码以','分隔)
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="emails">邮箱:</label>
			<div class="controls">
				<form:textarea path="emails" rows="10" htmlEscape="false" minlength="1" maxlength="512" class="emails"/>
				(多个邮箱以','分隔)
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:noticeReceive:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>