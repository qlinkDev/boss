<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>卡流量清零</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.control-group {
			margin-bottom: 20px;
			text-align: center;
		}
		.control-label {
		    width: 120px;
	    	text-align: right;
	        margin-right: 20px;
		}
	</style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			// 修改
			$("#btnSubmit").click(function(){
				var cardType = $('#cardType option:selected').val();
				if (isEmpty(cardType)) {
					top.$.jBox.info('请选择卡类型', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
				}
				top.$.jBox.confirm("确认清零卡流量吗？","系统提示",function(v,h,f){
					if(v == "ok"){
						top.$.jBox.tip("正在清空卡流量 ...", 'loading');
						var url = '${ctx}/mifi/simNode/cardFlowClear.json';
						$.post(url, {cardType:cardType}, function(data) {
							if ('success' == data.status)
								top.$.jBox.tip(data.message, 'success'); 
							else
								top.$.jBox.tip(data.message, 'error'); 
						});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/simNode/cardFlowClearPage">卡流量清零</a></li>
	</ul>
	<!-- tab e -->
	
	<tags:message content="${message}" />
	
	<div style="border: 1px solid #ddd; margin-top: 200px; margin-left: auto; margin-right: auto; width: 600px; height: 200px;">
		<div style="margin-top: 60px;">
			<div class="control-group">
				<label class="control-label" for="status">卡类型：</label>
				<select id="cardType" name="cardType" class="input-medium" style="width: 220px;">
					<option value="">--请选择--</option>
					<c:forEach items="${fns:getListByTable('sim_card_type','card_type','card_type_name')}" var="cardTypeValue">
						<option value="${cardTypeValue.card_type}">${cardTypeValue.card_type_name}</option>
					</c:forEach>
				</select>
			</div>
			<div class="form-actions" style="padding-left: 240px;">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="清  零"/>&nbsp;
			</div>
		</div>
	</div>
</body>
</html>