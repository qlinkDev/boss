<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
	<head>
	<title>api登录</title>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">
	<script src="${pageContext.request.contextPath}/static/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function() {
			$('#apiLoginForm').submit();
		});
	</script>
</head>
<body>
	<div id="main">
        <form id="apiLoginForm" action="${ctx}/login" method="post">
        	<input type="hidden" name="username" value="${username }" />
        	<input type="hidden" name="password" value="${password }" />
		</form>
	</div>
</body>
</html>