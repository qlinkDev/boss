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
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDFNeSWdCvVfgWYYqjmb6RpLRIORGqM2L8&signed_in=true"></script>
	<script type="text/javascript">
	    // 页面加载默认显示1小时内设备联网分布图
	    window.onload = function () {
	        //LoadMap(1);
	    }
	    
	    $(function() {
	    	// 时间戳点击事件
	    	$('#btnSubmit').click(function() {
	    		var timeStamp = $('#timeStamp option:selected').val();
		        LoadMap(timeStamp);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(timeStamp) {
	        var mapOptions = {
	            center: new google.maps.LatLng(20, 20),
	            zoom: 3,
	            mapTypeId: google.maps.MapTypeId.ROADMAP,
	            scaleControl: true
	        };
	        var map = new google.maps.Map(document.getElementById("map"), mapOptions);
	        
	        // 异步获取JSON数据
	        var url = '${ctx}/mifi/mifiDevice/positionJsonData';
	        $.post(url, {dateStr:timeStamp}, function(data) {
	        	if (data.code == '1') {
	    	        // 创建信息展示窗口
	    	        var infoWindow = new google.maps.InfoWindow();
	        		var positions = data.positions;
	        		$('#onLineDeviceNum').text(positions.length);
        			$.each(positions, function(i, position){ 
	    	            var myLatlng = new google.maps.LatLng(position.glat, position.glng);
	    	            var marker = new google.maps.Marker({
	    	                position: myLatlng,
	    	                map: map
	    	            });
	    	 
	    	            // marker绑定click事件
	    	            (function (marker, position) {
	    	                google.maps.event.addListener(marker, "click", function (e) {
	    	                    // 信息窗口添加内容
	    	                    infoWindow.setContent("<div style='width:160px;min-height:40px'><label>编号：</label><span>" + position.imei + "</span><br/><label>时间：</label><span>" + position.createDate + "</span></div>");
	    	                    infoWindow.open(map, marker);
	    	                });
	    	            })(marker, position);
        			});
	        	} else {
					top.$.jBox.info(data.msg, '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
	        	}
	        });
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiDevice/devicePositionMap">联网分布图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/deviceLineMap">设备线路图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/devicePosition">设备定位</a></li>
	</ul>
	<!-- tab e -->

	<form:form id="searchForm" class="breadcrumb form-search">
		<div>
			<label>时间戳：</label>
			<select id="timeStamp" name="simBankId" class="input-medium">
				<option value="1">1小时内</option>
				<option value="4">4小时内</option>
				<option value="8">8小时内</option>
				<option value="12">12小时内</option>
				<option value="24">24小时内</option>
			</select> &nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看" />
			<div style="float: right;margin-right: 100px;margin-top: 5px;">
		  		<label>在线设备数：</label>
		  		<span id="onLineDeviceNum" style="color: red;">0</span>
		  	</div>
		</div>
	</form:form>
	<div id="map"></div>
</body>
</html>