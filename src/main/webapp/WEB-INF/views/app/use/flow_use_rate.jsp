<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName() + path;
	if(request.getServerPort()!=80){
		basePath = request.getScheme()+"://"+request.getServerName()+":" + request.getServerPort() + path;
	}
%>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
<c:set var="ctxStaticFront" value="${ctxStatic}/modules/mifi"/>
<html>
	<head>
		<meta charset="UTF-8">
		<title>游友专车</title>
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
		<link rel="stylesheet" type="text/css" href="${ctxStaticFront }/youyouauto/css/main.css"/>
		<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
		<script type="text/javascript">
			$(function() {

				$('#mask').show();	
				
				// 点击确认按钮	
				$('.maskbtn').click(function() {
					$('#mask').hide();

					$.get('http://192.168.0.1', {}, function() {}, 'jsonp');
				});
				
			});
			
		</script>
	</head>
	<body>
	
		<div class="mask" id="mask">
			<div class="maskcon">
				<p class="masktext"><img src="${ctxStaticFront }/youyouauto/css/img/right.png" width="26" alt="" class="mr10"/>${msg }</p>
				<p class="maskbtn">确认</p>
			</div>
		</div>
		
		<div class="content">
			<div class="logo pt_4">
				<img src="${ctxStaticFront }/youyouauto/css/img/logo.png" width="150" alt="" />
			</div>
			<div class="logo pt_4">
				<img src="${ctxStaticFront }/youyouauto/css/img/name.png" width="208" alt="" />
			</div>
			<div class="logo pt_2">
				<img src="${ctxStaticFront }/youyouauto/css/img/slogan.png" width="218" alt="" />
			</div>
			<!--<div class="logo pt_4 logotext">
				<p class="conttext">可以为你提供以下服务</p>
			</div>-->
			<div class="list">
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/wifi.png" width="20" alt="" /></p>
					<p>境外WIFI</p>
				</div>
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/file.png" width="18" alt="" /></p>
					<p>游友签证</p>
				</div>
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/card.png" width="21" alt="" /></p>
					<p>目的地卡券</p>
				</div>
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/guide.png" width="18" alt="" /></p>
					<p>司导包车</p>
				</div>
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/pscar.png" width="17" alt="" /></p>
					<p>接送机</p>
				</div>
				<div class="listcon">
					<p class="cricle"><img src="${ctxStaticFront }/youyouauto/css/img/pointcar.png" width="21" alt="" /></p>
					<p>点对点用车</p>
				</div>
			</div>
			<%-- <div class="button"><span id="intbtn">${msg }</span></div> --%>
		</div>
	</body>
</html>
