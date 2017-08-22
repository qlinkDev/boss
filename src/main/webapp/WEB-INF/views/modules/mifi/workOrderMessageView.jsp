<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>工单详情</title>
<base href="${pageContext.request.contextPath}">
<meta name="decorator" content="default" />
<script type="text/javascript">
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/wOrder/list">工单列表</a></li>
		<li class="active"><a href="#">工单会话详情</a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<c:forEach items="${messages}" var="item">
	<table id="contentTable" class="table table-striped table-bordered table-condensed"  style="width: 30%">
		<tr>
			<td style="width: 16%;">发送者</td>
			<td>${item.userName}</td>
		</tr>
		<tr>
			<td>发送时间</td>
			<td><fmt:formatDate value="${ item.createTime}" type="both"/></td>
		</tr>
		<tr>
			<td>消息类型</td>
			<td>${item.messageType == 1 ? '客户' : '同事'}</td>
		</tr>
		<tr>
			<td >消息内容</td>
			<td>${item.content}</td>
		</tr>
		<c:if test="${not empty item.attachPath}">
			<tr>
				<td >附件</td>
				<td><a href="http://www.youyoumob.com${item.attachPath}" target="_blank">点击预览</a></td>
			</tr>
		</c:if>
		<tr>
			<td>消息状态</td>
			<td style="color: ${item.isRead == 1 ? '' : 'red'}">${item.isRead == 1 ? '已读' : '未读'}</td>
		</tr>
	</table>
	</c:forEach>	
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)" />
		</div>
</body>
</html>