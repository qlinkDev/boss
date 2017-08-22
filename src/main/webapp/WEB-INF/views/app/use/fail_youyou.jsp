<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName() + path;
	if(request.getServerPort()!=80){
		basePath = request.getScheme()+"://"+request.getServerName()+":" + request.getServerPort() + path;
	}
%>
<c:set var="ctx" value="${pageContext.request.contextPath}${fns:getFrontPath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<c:set var="ctxStaticFront" value="${ctxStatic}/modules/mifi"/>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width, minimal-ui">
		<title>游友移动</title>
		<link rel="stylesheet" href="${ctxStaticFront }/youyou/css/style.css" />
		<link rel="stylesheet" href="${ctxStaticFront }/youyou/css/global.css" />
		<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			// 充值
			function recharge() {
				$('#rechargeForm').submit();
			}
		</script>
	</head>
	<body>
		<input type="hidden" value="${code }" />
		<c:choose>
		<c:when test="${code eq '26' }">	
		<div class="wrap">
			<!-- 充值表单 -->
			<form id="rechargeForm" action="${pageContext.request.contextPath}/f/om/recharge" method="post">
		       	<input type="hidden" name="userId" value="${user.userId }" />
		       	<input type="hidden" name="phone" value="${user.phone }" />
		       	<input type="hidden" name="returnUri" value="<%=basePath %>/f/mifi/use/check?sn=${sn }&mcc=${mcc }" />
			</form>
			
			<p><img src='${ctxStaticFront }/youyou/images/logo.png' width="131"/></p>
			<div class="content">
				<h3>${empty user.userName ? '亲' : user.userName }，欢迎来到${empty countryName ? '未知世界' : countryName }！</h3>
				<ul class="inCont">
					<li>
						<span class="infoname">账户余额</span>
						<span class="infodetails">${user.balance }元</span>
					</li>
					<li>
						<span class="infoname">上网费用</span>
						<span class="infodetails">${payMoney }元/天</span>
					</li>
					<li>
						${msg }
					</li>
				</ul>
			</div>
			<p  class="Btn"><a href="javascript:void(0);" onclick="javascript:recharge();">充 值</a></p>
			<p  class="Btn"><a href="#" style="background: #DCE0DA">关 闭</a></p>
		</div>
		</c:when>
		<c:otherwise>
		<div class="wrap">
			<p><img src='${ctxStaticFront }/youyou/images/logo.png' width="131"/></p>
			<div class="content">
				<h3>${empty user.userName ? '亲' : user.userName }，欢迎来到${empty countryName ? '未知世界' : countryName }！</h3>
				<ul class="inCont">
					<li style="text-align: center;">
						${msg }
					</li>
				</ul>
			</div>
			<p  class="Btn"><a href="#" style="background: #DCE0DA">关 闭</a></p>
		</div>
		</c:otherwise>
		</c:choose>
	</body>
</html>
