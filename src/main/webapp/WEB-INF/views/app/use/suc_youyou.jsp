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
		<title>游友移动</title>
		<link rel="stylesheet" href="${ctxStaticFront }/youyou/css/style.css" />
		<link rel="stylesheet" href="${ctxStaticFront }/youyou/css/global.css" />
		<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			// 点击连网
			$(function() {
				$('#internet').click(function() {
					window.location.href="http://www.wuyoumob.com";
				});				
			});
		</script>
	</head>
	<body>
		<input type="hidden" value="${code }" />
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
			<p  class="Btn"><a id="internet" href="javascript:void(0);">点击联网</a></p>
		</div>
	</body>
</html>
