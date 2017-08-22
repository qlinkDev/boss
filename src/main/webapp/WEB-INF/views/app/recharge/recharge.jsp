<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
	<head>
	<title>充值</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
	<script src="${pageContext.request.contextPath}/static/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			$('#rechargeForm').submit();
		});
	</script>
</head>
<body>
	<div id="main">
        <form id="rechargeForm" action="${rechargeUri }" method="post">
        	<input type="hidden" name="id" value="${id }" />
        	<input type="hidden" name="userId" value="${userId }" />
        	<input type="hidden" name="phone" value="${phone }" />
        	<input type="hidden" name="money" value="${money }" />
        	<input type="hidden" name="sourceType" value="${sourceType }" />
        	<input type="hidden" name="callbackUri" value="${callbackUri }" />
        	<input type="hidden" name="returnUri" value="${returnUri }" />
		</form>
	</div>
</body>
</html>