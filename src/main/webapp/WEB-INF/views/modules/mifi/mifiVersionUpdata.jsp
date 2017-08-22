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
			 $.ajax({
				type: 'post',
				url: '${ctx}/mifi/version/updataspeedlimitflag',
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
	
	<form:form id="inputForm" modelAttribute="mifiVersion" action="${ctx}/mifi/version/updataspeedlimitflag" method="post" >
			<label>speedlimitflag:</label>
			<div class="control-group" align="center">
				<input id="speedlimitflag" name="speedlimitflag" maxlength="2" type="text" digits="true"  class="input-small required"  > 
				<span id="ss"></span>
			</div>
			<input id="btnSubmit" class="btn btn-primary" type="button" value="保 存"/>&nbsp;
		</form:form>
	</div>
</body>
</html>