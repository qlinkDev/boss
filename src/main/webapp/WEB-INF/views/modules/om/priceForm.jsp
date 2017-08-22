<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>价格管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.contrySpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
			margin-top: 5px;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#price").focus();
			$("#inputForm").validate();
			
			// 初始化：渠道选择‘游友移动’时显示containChannel
			if('游友移动' == $('#channel').find('option:selected').text()) {
				$('#containChannelDiv').show();
			} else {
				$('#containChannelDiv').hide();
			}
			// 渠道change事件
			$('#channel').change(function() {
				var text = $(this).find('option:selected').text();
				if('游友移动' == text) {
					$('#containChannelDiv').show();
				} else {
					$('#containChannelDiv').hide();
					// containChannel清空
					//$('#containChannelALL').attr('checked', false);
					//$('.containChannelOther').attr('checked', false);
				}
			});
			// 所有渠道和单个渠道不能同时存在
			$('#containChannelALL').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('.containChannelOther').attr('checked', false);
				}
			})
			$('.containChannelOther').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('#containChannelALL').attr('checked', false);
				}
			})
			
			// 保存
			$('#btnSubmit').click(function() {
				var text = $('#channel').find('option:selected').text();
				if('游友移动' == text) {
					var containChannel = '';
					if ($('#containChannelALL').attr('checked') == 'checked') {
						containChannel = ',ALL';
					} else {
						$('.containChannelOther').each(function(i) {
							if ($(this).attr('checked') == 'checked') {
								containChannel += ',' + $(this).val();
							} 
						});
					}
					if (containChannel == '') {
						top.$.jBox.info('请选择允许使用该价格的渠道', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						return false;
					} else {
						containChannel = containChannel.substring(1);
						$('#containChannel').val(containChannel);
					}
				}
				
				$('#inputForm').submit();
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/om/price/">价格列表</a></li>
		<li class="active"><a href="${ctx}/om/price/form?id=${region.id}">价格<shiro:hasPermission name="om:price:edit">${not empty region.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="om:price:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="price" action="${ctx}/om/price/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="containChannel"/>
		<div class="control-group">
			<label class="control-label" for="channel">渠道:</label>
			<div class="controls">
				<select id="channel" name="channel" class="input-small">
					<c:forEach items="${fns:getChannelList()}" var="channel">
						<option <c:if test="${price.channel.id eq channel.id }">selected="selected"</c:if> value="${channel.id }">${channel.channelName }</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="region">区域:</label>
			<div class="controls">
				<select id="region" name="region" class="input-small">
					<c:forEach items="${fns:getRegionList()}" var="region">
						<option <c:if test="${price.region.id eq region.id }">selected="selected"</c:if> value="${region.id }">${region.name }</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="downShelf">上架:</label>
			<div class="controls">
				<select id="downShelf" name="downShelf" class="input-small">
					<option <c:if test="${price.downShelf == '0' }">selected="selected"</c:if> value="0">上架</option>
					<option <c:if test="${price.downShelf == '1' }">selected="selected"</c:if> value="1">下架</option>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="price">价格:</label>
			<div class="controls">
				<form:input path="price" htmlEscape="false" class="required number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="price">新价格:</label>
			<div class="controls">
				<form:input path="newPrice" htmlEscape="false" class="number" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="price">新价格开始时间:</label>
			<div class="controls">
				<form:input path="newPriceStartDate" type="text" readonly="true" maxlength="20" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH',isShowClear:true,minDate:'%y-%M-%d {%H+1}'});"/>
			  </div>
		</div>
		
		<div class="control-group" id="containChannelDiv">
			<label class="control-label" for="channel">允许使用该价格的渠道:</label>
			<div class="controls">
				<span class="contrySpan">
					<label>
						<input id="containChannelALL" type="checkbox" value="ALL" <c:if test="${price.containChannel eq 'ALL' }">checked="checked"</c:if>/>所有渠道
					</label>
				</span>
				<c:forEach items="${channelList}" var="channel">
					<span class="contrySpan">
						<label>
							<input class="containChannelOther" type="checkbox" value="${channel[0] }" <c:if test="${channel[2] eq '1' }">checked="checked"</c:if> />${channel[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="remarks">说明:</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="om:price:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>