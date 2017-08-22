<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>联系客服</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.pagetalk{width:900px;margin:150px auto;}
		.pagetalk{overflow:hidden;border:1px solid #dedede;border-radius:5px;padding:20px;}
		.pagetalk .tel,.company,.weixin{float:left;width:33%;text-align:center;}
		.pagetalk .weixin{border-left:1px dashed #dedede;border-right:1px dashed #dedede;}
		.pagetalk .lead{border-bottom:1px solid #dedede;}
	</style>
</head>
<body>
	
	<div class="pagetalk">
		<p class="lead">与我们联系</p>
		<div class="tel">
			<p >客服电话</p>
			<ul class="unstyled">
  				<li></li>
  				<li><strong style="font-size: 18px;">400-805-1110</strong></li>
  				<li style="margin-bottom:10px;">全年无休8:00-22:00</li>
  				<li></li>
  				<li>021-50435935-824</li>
			</ul>
		</div>
		<div class="weixin">
			<p>微信公众号</p>
            <span><img src="${ctxStatic }/images/wx140x140.jpg" width="140" height="140"></span>
		</div>
		<div class="company">
			<p>企业客服</p>
            <p><a href="http://wpa.qq.com/msgrd?v=3&amp;uin=2880729613&amp;site=qq&amp;menu=yes" class="dp-t15" target="_blank"><img src="http://wpa.qq.com/pa?p=1:2841528328:41" width="80"></a></p>
            <p><a href="http://wpa.qq.com/msgrd?v=3&amp;uin=2880729612&amp;site=qq&amp;menu=yes" class="dp-t15" target="_blank"><img src="http://wpa.qq.com/pa?p=1:513082694:41" width="80"></a></p>
        </div>
	</div>
	
</body>
</html>