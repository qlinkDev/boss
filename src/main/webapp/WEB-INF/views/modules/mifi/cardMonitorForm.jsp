<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>a</title>
	<meta name="decorator" content="default"/>
		<%@include file="/WEB-INF/views/include/dialog.jsp"%>
	<script type="text/javascript">
	$(document).ready(function() {
		 $('#btnSubmit').click(function(){
			 $.jBox.tip("正在处理...", 'loading', {persistent: true});
				$(this).attr("disabled",true);
			 $.ajax({
				type: 'post',
				url: '${ctx}/mifi/cardMonitor/handled',
				data:  $('#inputForm').serialize(),
				dataType: 'json',
				success: function(data){
					if(data){
						window.parent.location.reload()
					}else{
						top.$.jBox.tip('输入有误','warning');
					} 
				}
			}); 
		 });

	});
	$().ready(function() {
	    $("#inputForm").validate();
	});
	</script>
	
	<style>
	    #customPriceLabel {
            padding-top: 0;
        }
		#customizedPriceName {
            width: 90px;
        }
	</style>
</head>
<body>
	<tags:message content="${message}"/>
	<div align="center" style="margin-top: 20px;">
		<form:form id="inputForm" modelAttribute="cardMonitor" action="${ctx}/mifi/cardMonitor/handled" method="post" class="breadcrumb form-search">
			<form:hidden path="id"/>
			<label>说明:</label>
			<div class="control-group" align="center">
				<textarea id="remarks" name="remarks" rows="7"
					cols="6"  class="required" ></textarea> 
			</div>
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
		</form:form>
	</div>
</body>
</html>