<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@	page import="org.apache.commons.lang.ObjectUtils"%>
<%
	String lat = ObjectUtils.toString(request.getAttribute("lat"));
	String lng = ObjectUtils.toString(request.getAttribute("lng"));
	String accuracy = ObjectUtils.toString(request.getAttribute("accuracy"));
%>
<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
	<meta http-equiv="content-type" content="text/html; charset=gbk" />
	<title>谷歌定位</title>
	<link rel="shortcut icon" href="${ctxStatic}/favicon.ico">
    <script src="https://maps.googleapis.com/maps/api/js?sensor=false&callback=initMap" async defer></script>
    <script type="text/javascript">
	function initMap() {
		var myLatLng = {
			lat : <%=lat %>,
			lng : <%=lng %>
		};

		// Create a map object and specify the DOM element for display.
		var map = new google.maps.Map(document.getElementById('container'), {
			center : myLatLng,
			scrollwheel : true,
			zoom : 4
		});

		// Create a marker and set its position.
		var marker = new google.maps.Marker({
			map : map,
			position : myLatLng,
			title : 'Hello World!'
		});
	}
	</script>
</head>
<body>
	<div>
		<div style="border-top: 1px solid #ddd; margin-top: 10px; margin-left: auto; margin-right: auto; width: 1200px; height: 100px;">
			<label class="control-label">经度:<%=lat%></label><br /> <br /> <label
				class="control-label">纬度:<%=lng%></label><br /> <br /> <label
				class="control-label">半径:<%=accuracy%>(米)
			</label>
		</div>
		<div id="container" style="margin-left: auto; margin-right: auto; width: 1200px; height: 780px;">
		</div>
	</div>
</body>
</html>
