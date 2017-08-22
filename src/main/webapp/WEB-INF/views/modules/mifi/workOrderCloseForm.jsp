<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>工单管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(function(){
		$("#closeDesc").focus();
		$("#inputForm").validate();
		$("#isRefund").change(function(){
			var val = $("#isRefund").val();
			if(val == 1){
				$("#refundDesc").css('display','block');
			}else {
				$("#refundDesc").css('display','none');
			}
		});
		$("#btnSubmit").click(function(){
			$("#inputForm").submit();
		});
		
		  /**
	     * 字符数统计
	     */
	    $('.limited').on("input",function(){
	         var limit = $(this).attr('maxlength') - $(this).val().length;
	         if($(this).val().length > Number($(this).attr('maxlength'))){
	             $(this).parent().find('.limit-text').addClass("has-error");
	             $(this).parent().find('.limit-text').html("您已经超出了"+Math.abs(limit)+" 字符");
	         }else{
	             $(this).parent().find('.limit-text').removeClass("has-error");
	             $(this).parent().find('.limit-text').html("还可以输入"+limit+" 字符");

	         }
	    });
		  
	    $('.refundDesc').on("input",function(){
	         var limit = $(this).attr('maxlength') - $(this).val().length;
	         if($(this).val().length > Number($(this).attr('maxlength'))){
	             $(this).parent().find('.limit-text').addClass("has-error");
	             $(this).parent().find('.limit-text').html("您已经超出了"+Math.abs(limit)+" 字符");
	         }else{
	             $(this).parent().find('.limit-text').removeClass("has-error");
	             $(this).parent().find('.limit-text').html("还可以输入"+limit+" 字符");

	         }
	    });
	})
		
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/wOrder/list">工单列表</a></li>
		<li class="active"><a href="${ctx}/mifi/wOrder/closeForm">工单关闭</a></li>
	</ul>
	<br />
	<tags:message content="${message}"/>

	<form:form id="inputForm" modelAttribute="workOrder" action="${ctx}/mifi/wOrder/close" method="post" enctype="multipart/form-data" class="form-horizontal">
		<input type="hidden"  name="wid"  value="${ workOrder.wid }" />
		<div class="control-group">
			<label class="control-label">关闭说明:</label>
			<div class="controls">
				<form:textarea path="closeDesc" htmlEscape="false"  rows="10" maxlength="500" class="required input-xlarge limited"  style="margin: 0px;width: 415px;height: 208px;" placeholder="请输入文字"/>
				<form:errors path="closeDesc"  cssClass="error" style="color:red"/>
				<span class="help-block content-help-block limit-text">建议不超过500字符</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">问题是否解决:</label>
			<div class="controls">
				<select id="isResolve" name="isResolve" class="input-small required">
						<option value="">--请选择--</option>
						<option value="0" >未解决</option>
						<option value="1" >已解决</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否退款:</label>
			<div class="controls">
				<select id="isRefund" name="isRefund" class="input-small">
						<option value="">--请选择--</option>
						<option value="0" >否</option>
						<option value="1" >是</option>
				</select>
			</div>
		</div>
		<div class="control-group"  id="refundDesc" style="display: none;">
			<label class="control-label">退款详情:</label>
			<div class="controls">
				<form:textarea path="refundDesc" htmlEscape="false" maxlength="500" class="required input-xlarge refundDesc"  style="margin: 0px;width: 415px;height: 208px;" />
				<span class="help-block content-help-block limit-text">建议不超过500字符</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否产生损失:</label>
			<div class="controls">
				<select id="isLoss" name="isLoss" class="input-small">
						<option value="">--请选择--</option>
						<option value="0" >否</option>
						<option value="1" >是</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">插头是否损坏:</label>
			<div class="controls">
				<select id="lossPlug" name="lossPlug" class="input-small">
						<option value="">--请选择--</option>
						<option value="0" >否</option>
						<option value="1" >是</option>
				</select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">数据线是否损坏:</label>
			<div class="controls">
				<select id="lossDataLine" name="lossDataLine" class="input-small">
						<option value="">--请选择--</option>
						<option value="0" >否</option>
						<option value="1" >是</option>
				</select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">贴纸密码错误:</label>
			<div class="controls">
				<select id="lossPassword" name="lossPassword" class="input-small">
						<option value="">--请选择--</option>
						<option value="0" >否</option>
						<option value="1" >是</option>
				</select>
			</div>
		</div>	
		<div class="form-actions">
			<shiro:hasPermission name="mifi:workOrder:edit">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="关闭"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
	</form:form>
</body>
</html>