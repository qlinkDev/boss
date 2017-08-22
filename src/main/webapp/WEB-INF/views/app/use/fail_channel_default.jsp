<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<c:set var="ctx" value="${pageContext.request.contextPath}${fns:getFrontPath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<c:set var="ctxStaticFront" value="${ctxStatic}/modules/mifi"/>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width, minimal-ui">
		<title>${channel.channelName }</title>
		<link rel="stylesheet" href="${ctxStaticFront }/wsd/css/style.css" />
		<link rel="stylesheet" href="${ctxStaticFront }/wsd/css/global.css" />
		<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			// 设备关闭
			function shut() {
				window.location.href="http://www.baidu.com";
			}
		</script>
	</head>
	<body>
		<input id="code" type="hidden" value="${code }" />
		<c:choose>
		<c:when test="${code eq '18' }">
		<div class="wrap">
			<p><img src="${ctxStaticFront }/wsd/images/wsd_logo.png" width="58"/></p>
			<h2>欢迎来到${empty countryName ? '未知世界' : countryName }</h2>
			<form>
				<p>
				${msg }
				</p>
				<input type='button' onclick="javascript:shut();" value="开始测试"/>
			</form>
		</div>
		</c:when>
		<c:otherwise>
		<div class="wrap">
			<p><img src="${ctxStaticFront }/wsd/images/wsd_logo.png" width="58"/></p>
			<h2>欢迎来到${empty countryName ? '未知世界' : countryName }</h2>
			<form>
				<p>
				${msg }
				</p>
				<input type='button' value="关 闭"/>
			</form>
		</div>
		</c:otherwise>
		</c:choose>
	</body>
</html>
