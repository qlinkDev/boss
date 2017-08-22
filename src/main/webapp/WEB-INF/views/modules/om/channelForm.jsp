<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>渠道管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#channelName").focus();
			$("#inputForm").validate({
				rules: {
					channelName: {remote: "${ctx}/om/channel/isChannelNameUnique?channelId=" + "${channel.id}"},
					channelNameEn: {remote: "${ctx}/om/channel/isChannelNameEnUnique?channelId=" + "${channel.id}"}
				},
				messages: {
					channelName: {remote: "渠道名称已存在"},
					channelNameEn: {remote: "渠道名称已存在"}
				},
				errorPlacement: function(error, element) {
					if("text" == element.attr("type")){
						element.after(error);
						return;
					}
					if("radio" == element.attr("type")){
						element.parent().parent().find("span").last().after(error);
						return;
					}
					error.appendTo(element.parent());
				}
			});
			
			$("#regionAndPriceDiv input:checkbox").change(function() {
				var regionId = this.id.replace(/inputCheckbox/g,"");
				if(this.checked){
					$("#priceDiv" + regionId).show();
				}else{
					$("#priceDiv" + regionId).hide();
				}
		    });
			
			$("#regionAndPriceDiv input:text").change(function() {
		    	var regionId = this.id.replace(/inputText/g,"");
				var inputCheckboxObj = document.getElementById("inputCheckbox" + regionId);
				var value = inputCheckboxObj.value;
				var values;
				if('0' == value){
					value = '|' + regionId + '|' + this.value + '|' + 'true';
					inputCheckboxObj.value = value;
				    if(!inputCheckboxObj.getAttribute("checked")){
				    	inputCheckboxObj.setAttribute("checked","true");
				    }
					this.setAttribute("defaulPrice", this.value);
					return;
				}else{
					values = value.split("|");
				}
				if(!this.value){
					var defaulPrice = this.getAttribute("defaulPrice");
					inputCheckboxObj.value = values[0] + '|' + regionId + '|' + defaulPrice + '|' + values[3];
					inputCheckboxObj.removeAttribute("checked");
					return;
				}
				inputCheckboxObj.value = values[0] + '|' + regionId + '|' + this.value + '|' + values[3];
				if(!inputCheckboxObj.getAttribute("checked")){
					inputCheckboxObj.setAttribute("checked","true");
				}
		    });
			
			if("0" == $("input[checked]:radio").val()){
				$("#divBalance").show();
				$("#balance").addClass("required");
			}else{
				$("#divBalance").hide();
				$("#balance").removeClass("required");
			}
			$("input:radio").change(function() {
				if("0" == this.value){
					$("#divBalance").show();
					$("#balance").addClass("required");
				}else{
					$("#divBalance").hide();
					$("#balance").removeClass("required");
				}
		    });
		});
		
	</script>
	<style type="text/css">
	    #regionAndPriceDiv span{
			display: inline-block;
			width: 80px;
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/om/channel/">渠道列表</a></li>
		<li class="active"><a href="${ctx}/om/channel/form?id=${channel.id}">渠道<shiro:hasPermission name="om:channel:edit">${not empty channel.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="om:channel:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	
	<form:form id="inputForm" modelAttribute="channel" action="${ctx}/om/channel/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label" for="channelName">渠道名称:</label>
			<div class="controls">
				<form:input path="channelName" htmlEscape="false" maxlength="50" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="channelNameEn">渠道名称（英文）:</label>
			<div class="controls">
				<c:if test="${empty channel.channelNameEn }" var="isCreate">
				<form:input path="channelNameEn" htmlEscape="false" maxlength="50" class="required abc"/>
				(提交后将不能修改)
				</c:if>
				<c:if test="${!isCreate }">
				<form:input path="channelNameEn" htmlEscape="false" disabled="true" maxlength="50" class="required abc"/>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="region.id">归属区域:</label>
			<div id="regionAndPriceDiv" class="controls" style="vetical-align:top;">
			    <!--0_id, 1_channel_id, 2_region_id, 3_price, 10_region_name, 11_need_create-->
				<c:forEach var="price" items="${priceList}">
				    <input id="inputCheckbox${price[2]}" type="checkbox" name="priceInfos" value="${price[0]}|${price[2]}|${price[3]}|${price[11]}" style="display:none"/><span>${price[10]}</span>&nbsp;&nbsp;&nbsp;默认价格:<span style="width:60px">${price[3]}</span>
					<label>设置价格:</label><input id="inputText${price[2]}" type="text" name="inputText${price[2]}" class="number" defaulPrice="${price[3]}"></input><br/>
				</c:forEach>
				<c:forEach var="region" items="${regionList}">
				    <input id="inputCheckbox${region.id}" type="checkbox" name="priceInfos" class="required" checked="true" value="0" style="display:none"/><span>${region.name}</span>&nbsp;&nbsp;&nbsp;
					<label>设置价格:</label><input id="inputText${region.id}" type="text" name="inputText${region.id}" class="number required"></input><br/>
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="backPoint">所在国家:</label>
			<div class="controls">
				<select name="mcces" class="input-large">
					<!--0_mcces, 1_country_name_cn-->
					<c:forEach items="${mccList}" var="mcc">
					    <c:if test="${channel.mcces == mcc[0]}" var="needSelect">
						    <option value="${mcc[0]}" selected="true">${mcc[1]}</option>
						</c:if>
						<c:if test="${!needSelect}">
						    <option value="${mcc[0]}">${mcc[1]}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="backPoint">返点:</label>
			<div class="controls">
				<form:input path="backPoint" htmlEscape="false" maxlength="10" class="number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="payType">付费类型:</label>
			<div class="controls">
				<form:radiobuttons path="payType" items="${fns:getDictList('pay_type')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div id="divBalance" class="control-group" style="display:none">
			<label class="control-label" for="balance">信用额度:</label>
			<div class="controls">
				<form:input path="balance" htmlEscape="false" maxlength="10" class="number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="model">渠道模式:</label>
			<div class="controls">
				<form:radiobuttons path="model" items="${fns:getDictList('channel_model')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="createCsvFile">生成CSV文件:</label>
			<div class="controls">
				<form:radiobuttons path="createCsvFile" items="${fns:getDictList('channel_create_csv')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" for="customized">是否定制:</label>
			<div class="controls">
				<form:radiobuttons path="customized" items="${fns:getDictList('channel_customized')}" itemLabel="label" itemValue="value" htmlEscape="false" class="required"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="om:channel:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>