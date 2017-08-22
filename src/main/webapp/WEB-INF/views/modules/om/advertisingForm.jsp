<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.url {margin-top: 10px;}
		.baiduAd{margin-top: 10px;}
		.checkBoxSpan {
			display:-moz-inline-box;
			display:inline-block;
			width:180px; 
			margin-top: 5px;
		}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#imei").focus();
			$("#inputForm").validate();
			

			// 国家'ALL'和单个国家不能同时存在
			$('#containCountryALL').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('.containCountryOther').attr('checked', false);
				}
			})
			$('.containCountryOther').click(function() {
				if ($(this).attr('checked') == 'checked') {
					$('#containCountryALL').attr('checked', false);
				}
			})
			
			// 保存
			$('#btnSubmit').click(function() {
				
				// 渠道判断
				var sourceTypes = '';
				var channelNames = '';
				$('.containChannel').each(function(i) {
					if ($(this).attr('checked') == 'checked') {
						sourceTypes += ',' + $(this).val();
						channelNames += ',' + $(this).attr('data-name');
					} 
				});
				if (sourceTypes == '') {
					top.$.jBox.info('请选择所属渠道', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				} else {
					$('#sourceTypes').val(sourceTypes.substring(1));
					$('#channelNames').val(channelNames.substring(1));
				}
				
				// 投放国家判断
				var countryCodes = '';
				var countryNames = '';
				if ($('#containCountryALL').attr('checked') == 'checked') {
					countryCodes = ',ALL';
					countryNames = ',全球';
				} else {
					$('.containCountryOther').each(function(i) {
						if ($(this).attr('checked') == 'checked') {
							countryCodes += ',' + $(this).val();
							countryNames += ',' + $(this).attr('data-name');
						} 
					});
				}
				if (countryCodes == '') {
					top.$.jBox.info('请选择投放国家', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				} else {
					$('#countryCodes').val(countryCodes.substring(1));
					$('#countryNames').val(countryNames.substring(1));
				}

				var itemStrs = '';
				var flag = '1';
				// 广告位不能为空
				$('.imgPath').each(function(i) {
					var path = $(this).val();
					if (isEmpty(path)) {
						top.$.jBox.info($(this).attr('data-position') + '图片未上传', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
						flag = '0';
						return false;
					}
					itemStrs = itemStrs + '#@' + path + ',@' + $('#url' + i).val() + ',@' + $(this).attr('data-sequence') + ',@' + $('input[name="baiduAd'+i+'"]:checked').val();		
				});
				if ('1' == flag) {
					$('#itemStrs').val(itemStrs.substring(2));
					$('#inputForm').submit();
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/om/goods/">广告列表</a></li>
		<li class="active">
			<a href="${ctx}/om/goods/form?id=${goods.id}">广告<shiro:hasPermission name="om:goods:edit">${not empty region.id?'修改':'添加'}</shiro:hasPermission></a>
		</li>
	</ul><br/>
	
	<tags:message content="${message}"/>
	
	<form:form id="inputForm" modelAttribute="goods" action="${ctx}/om/goods/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="sourceTypes"/>
		<form:hidden path="channelNames"/>
		<form:hidden path="countryCodes"/>
		<form:hidden path="countryNames"/>
		<form:hidden path="itemStrs"/>
		<div class="control-group">
			<label class="control-label" for="imei">广告名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" minlength="1" maxlength="50" class="required"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="type">类型:</label>
			<div class="controls">
				<select id="type" name="type" class="input-small">
					<c:forEach items="${fns:getDictList('om_advertising_type')}" var="type">
						<option value="${type.value}" <c:if test="${type.value==goods.type}">selected</c:if>>${type.label}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="channel">所属渠道:</label>
			<div class="controls">
				<c:forEach items="${channelList}" var="channel">
					<span class="checkBoxSpan">
						<label>
							<input class="containChannel" type="checkbox" value="${channel[0] }" data-name="${channel[1] }" <c:if test="${channel[2] eq '1' }">checked="checked"</c:if> />${channel[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label" for="channel">投放国家:</label>
			<div class="controls">
				<span class="checkBoxSpan">
					<label>
						<input id="containCountryALL" type="checkbox" value="ALL" <c:if test="${goods.countryCodes eq 'ALL' }">checked="checked"</c:if>/>ALL
					</label>
				</span>
				<c:forEach items="${countryList}" var="country">
					<span class="checkBoxSpan">
						<label>
							<input class="containCountryOther" type="checkbox" value="${country[0] }" data-name="${country[1] }" <c:if test="${country[2] eq '1' }">checked="checked"</c:if>/>${country[1] }
						</label>
					</span>
				</c:forEach>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">广告位一:</label>
			<div class="controls">
				<input class="imgPath" id="imgPath1" type="hidden" value="${goods.itemList[0].imgPath }" data-sequence="1" data-position="广告位一" />
				<tags:ckfinder input="imgPath1" type="images" uploadPath="/goods" selectMultiple="false" />
				<span class="help-inline">图片文件，jpg、png等支持的图片格式</span><br />
				<input type="text" id="url0" class="url" value="${goods.itemList[0].url }" placeholder="跳转地址" /><br />
				<label class="baiduAd"><input type="radio"  name="baiduAd0"  value="0" <c:if test="${goods.itemList[0].showBaiduAd eq '0' }">checked="checked"</c:if>/>不显示百度广告</label>&nbsp;
				<label class="baiduAd"><input type="radio"  name="baiduAd0"  value="1" <c:if test="${goods.itemList[0].showBaiduAd eq '1' }">checked="checked"</c:if>/>显示百度广告</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">广告位二:</label>
			<div class="controls">
				<input class="imgPath" id="imgPath2" type="hidden" value="${goods.itemList[1].imgPath }" data-sequence="2" data-position="广告位二" />
				<tags:ckfinder input="imgPath2" type="images" uploadPath="/goods" selectMultiple="false" />
				<span class="help-inline">图片文件，jpg、png等支持的图片格式</span><br />
				<input type="text" id="url1" class="url" value="${goods.itemList[1].url }" placeholder="跳转地址" /><br />
				<label class="baiduAd"><input type="radio"  name="baiduAd1"  value="0" <c:if test="${goods.itemList[1].showBaiduAd eq '0' }">checked="checked"</c:if>/>不显示百度广告</label>&nbsp;
				<label class="baiduAd"><input type="radio"  name="baiduAd1"  value="1" <c:if test="${goods.itemList[1].showBaiduAd eq '1' }">checked="checked"</c:if>/>显示百度广告</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">广告位三:</label>
			<div class="controls">
				<input class="imgPath" id="imgPath3" type="hidden" value="${goods.itemList[2].imgPath }" data-sequence="3" data-position="广告位三" />
				<tags:ckfinder input="imgPath3" type="images" uploadPath="/goods" selectMultiple="false" />
				<span class="help-inline">图片文件，jpg、png等支持的图片格式</span><br />
				<input type="text" id="url2" class="url" value="${goods.itemList[2].url }" placeholder="跳转地址" /><br />
				<label class="baiduAd"><input type="radio"  name="baiduAd2"  value="0" <c:if test="${goods.itemList[2].showBaiduAd eq '0' }">checked="checked"</c:if>/>不显示百度广告</label>&nbsp;
				<label class="baiduAd"><input type="radio"  name="baiduAd2"  value="1" <c:if test="${goods.itemList[2].showBaiduAd eq '1' }">checked="checked"</c:if>/>显示百度广告</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">广告位四:</label>
			<div class="controls">
				<input class="imgPath" id="imgPath4" type="hidden" value="${goods.itemList[3].imgPath }" data-sequence="4" data-position="广告位四" />
				<tags:ckfinder input="imgPath4" type="images" uploadPath="/goods" selectMultiple="false" />
				<span class="help-inline">图片文件，jpg、png等支持的图片格式</span><br />
				<input type="text" id="url3" class="url" value="${goods.itemList[3].url }" placeholder="跳转地址" /><br />
				<label class="baiduAd"><input type="radio"  name="baiduAd3"  value="0" <c:if test="${goods.itemList[3].showBaiduAd eq '0' }">checked="checked"</c:if>/>不显示百度广告</label>&nbsp;
				<label class="baiduAd"><input type="radio"  name="baiduAd3"  value="1" <c:if test="${goods.itemList[3].showBaiduAd eq '1' }">checked="checked"</c:if>/>显示百度广告</label>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="om:goods:edit">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
</body>
</html>