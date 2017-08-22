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
			// 确认付款
			$(function() {
				$('#pay').click(function() {
					var url='${pageContext.request.contextPath}/f/mifi/use/pay.json';
					var sn = $('#sn').val();
					var mcc = $('#mcc').val();
					$.post(url, {sn:sn, mcc:mcc}, function(data){
						if(data.code == 'ps1'){// 支付成功
							window.location.href="http://www.wuyoumob.com";
						} else {
							showMessage(data.msg);
						}
					});
				});				
			});
			
			function showMessage(o){
				$("#message").html(o).show(100).delay(1500).hide(100); 
			}
		</script>
	</head>
	<body>
		<input type="hidden" value="${code }" />
		<div class="wrap">
			<input id="sn" type="hidden" value="${sn }" />
			<input id="mcc" type="hidden" value="${mcc }" />
			<p><img src="${ctxStaticFront }/wsd/images/wsd_logo.png" width="58"/></p>
			<h2>欢迎来到${empty countryName ? '未知世界' : countryName }</h2>
			<form>
				<input id="pay" type='button' value="点击联网"/>
			</form>
		</div>
		
		<div id="message"></div>
	</body>
</html>
