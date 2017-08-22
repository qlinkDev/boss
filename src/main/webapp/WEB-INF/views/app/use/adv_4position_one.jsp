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
		<title>游友移动</title>
		<meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width, minimal-ui">
	    <!-- Makes your prototype chrome-less once bookmarked to your phone's home screen -->
	    <!-- iOS中Safari允许全屏浏览 -->
	    <meta name="apple-mobile-web-app-capable" content="yes">
	    <!-- iOS中Safari顶端状态条样式 -->
	    <meta name="apple-mobile-web-app-status-bar-style" content="black">
	    <meta name="apple-mobile-web-app-title" content="游友移动">
	    <!-- 忽略将数字变为电话号码 -->
	    <meta content="telephone=no" name="format-detection">
	    <!-- 忽略自动识别邮箱账号 -->
	    <meta content="email=no" name="format-detection">
	
	    <!-- 针对手持设备优化，主要是针对一些老的不识别viewport的浏览器，比如黑莓 -->
	    <meta name="HandheldFriendly" content="true">
	    <!-- 微软的老式浏览器 -->
	    <meta name="MobileOptimized" content="320">
	    <!-- UC强制竖屏 -->
	    <meta name="screen-orientation" content="portrait">
	    <!-- QQ强制竖屏 -->
	    <meta name="x5-orientation" content="portrait">
	    <!-- UC强制全屏 -->
	    <meta name="full-screen" content="yes">
	    <!-- QQ强制全屏 -->
	    <meta name="x5-fullscreen" content="true">
	    <!-- UC应用模式 -->
	    <meta name="browsermode" content="application">
	    <!-- QQ应用模式 -->
	    <meta name="x5-page-mode" content="app">
	    <!-- windows phone 点击无高光 -->
	    <meta name="msapplication-tap-highlight" content="no">
		<link rel="stylesheet" type="text/css" href="${ctxStaticFront }/youyouadv/css/main.css"/>
		<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
		<script>
			$(function(){
				var advId = '${advId}';
				var imei = '${imei}';	
				var mcc = '${mcc}';	
				
				// 记录首页显示成功
				var url = '${ctx}/mifi/use/page_click.json';
				$.post(url, {advId:advId, imei:imei, mcc:mcc, type:'HOME_SHOW'}, function(data) {}, 'josn');
				
				var i=3;
				var timer = setInterval(function(){
					i--;
					$('.section').text(i);
					if(i<1){
						clearInterval(timer);
						location.href = '${ctx}/mifi/use/fourStepTow?advId='+advId+'&imei='+imei+'&mcc='+mcc;
					}
				}, 1000);
				
			})
		</script>
	</head>
	<body>
		<div class="onlinewrap">
			<div class='section'>3</div>
			<div class="content">
				<img src="${advUrl}" alt="" />
			</div>
		</div>
	</body>
</html>
