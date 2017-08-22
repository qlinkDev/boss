<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
	<title>设备定位</title>
	<meta name="decorator" content="default" />
    <style>
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
      #map {
        height: 88%;
        margin-top: 10px;
      }
    </style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDFNeSWdCvVfgWYYqjmb6RpLRIORGqM2L8&signed_in=true"></script>
	<script type="text/javascript">
	    
	    $(function() {
	    	// 时间戳点击事件
	    	$('#btnSubmit').click(function() {
	    		var lat = $('#lat').val();
	    		var lng = $('#lng').val();
	        	if (isEmpty(lat)) {
					top.$.jBox.info('请输入经度!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	if (isEmpty(lng)) {
					top.$.jBox.info('请输入纬度!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
		        LoadMap(lat, lng);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(glat, glng) {
	       var mapOptions = {
	            center: new google.maps.LatLng(glat, glng),
	            zoom: 12,
	            mapTypeId: google.maps.MapTypeId.ROADMAP,
	            scaleControl: true
	        };
	        var map = new google.maps.Map(document.getElementById("map"), mapOptions);
	        var myLatlng = new google.maps.LatLng(glat, glng);
            var marker = new google.maps.Marker({
                position: myLatlng,
                map: map
            });
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMap">联网分布图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/deviceLineMap">设备线路图</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/devicePosition">设备定位</a></li>
	</ul>
	<!-- tab e -->
	
	<form:form id="searchForm" class="breadcrumb form-search">
		<div>
			<label>经度：</label>
			<input id="lat" name="lat" type="text" maxlength="50" class="input-small required" /> 
			<label>纬度：</label>
			<input id="lng" name="lng" type="text" maxlength="50" class="input-small required" /> 
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看" />
		</div>
	</form:form>
	
	<div id="map"></div>
</body>
</html>