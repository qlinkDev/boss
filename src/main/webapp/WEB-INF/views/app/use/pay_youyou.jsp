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
			// 确认付款
			$(function() {
				$('#pay').click(function() {
					var url='${pageContext.request.contextPath}/f/mifi/use/pay.json';
					var sn = $('#sn').val();
					var mcc = $('#mcc').val();
					$.post(url, {sn:sn, mcc:mcc}, function(data){
						if(data.code == 'ps2'){// 支付成功
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
			<p><img src='${ctxStaticFront }/youyou/images/logo.png' width="131"/></p>
			<div class="content">
				<h3>${empty user.userName ? '亲' : user.userName }，欢迎来到${countryName }！</h3>
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
						<span class="infoname">计费周期</span>
						<p class='infotime'>
							<span class="infoname mr_0">起：</span>${localDate } 00:00 
							<span class="infoname mr_0">止：</span>${localDate } 24:00 
					</li>
				</ul>
			</div>
			<p  class="Btn"><a id="pay" href="javascript:void(0);">点击联网</a></p>
		</div>
		
		<div id="message"></div>
	</body>
</html>
