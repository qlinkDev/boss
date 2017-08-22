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
    .map-marker-label{
        position: absolute;
	    color: blue;
	    font-size: 16px;
	    font-weight: bold;
    }
    </style>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDFNeSWdCvVfgWYYqjmb6RpLRIORGqM2L8&signed_in=true"></script>
	<script type="text/javascript">
	
		var markerSize = { x: 24, y: 40 };
	    google.maps.Marker.prototype.setLabel = function(label){
	        this.label = new MarkerLabel({
	          map: this.map,
	          marker: this,
	          text: label
	        });
	        this.label.bindTo('position', this, 'position');
	    };
	
	    var MarkerLabel = function(options) {
	        this.setValues(options);
	        this.span = document.createElement('span');
	        this.span.className = 'map-marker-label';
	    };
	
	    MarkerLabel.prototype = $.extend(new google.maps.OverlayView(), {
	        onAdd: function() {
	            this.getPanes().overlayImage.appendChild(this.span);
	            var self = this;
	            this.listeners = [
	            google.maps.event.addListener(this, 'position_changed', function() { self.draw();    })];
	        },
	        draw: function() {
	            var text = String(this.get('text'));
	            var position = this.getProjection().fromLatLngToDivPixel(this.get('position'));
	            this.span.innerHTML = text;
	            this.span.style.left = (position.x - (markerSize.x / 2)) - (text.length * 3) + 10 + 'px';
	            this.span.style.top = (position.y - markerSize.y + 40) + 'px';
	        }
	    });
	    
	    $(function() {
	    	// 时间戳点击事件
	    	$('#btnSubmit').click(function() {
	    		var imei = $('#sn').val();
	        	if (isEmpty(imei)) {
					top.$.jBox.info('请输入设备编号!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	var startDate = $('#startDateStr').val();
	        	if (isEmpty(startDate)) {
					top.$.jBox.info('请选择开始时间!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	var endDate = $('#endDateStr').val();
	        	if (isEmpty(endDate)) {
					top.$.jBox.info('请选择结束时间!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
		        LoadMap(imei, startDate, endDate);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(imei, startDate, endDate) {
	    	
	        // 异步获取JSON数据
	        var url = '${ctx}/mifi/mifiDevice/lineJsonData';
	        $.post(url, {imei:imei, startDateStr:startDate, endDateStr:endDate}, function(data) {
	        	if (data.code == '1') {
	        		var lines = data.lines;
	        		if (lines.length > 0) {
	        			var lineStamp = lines[0];
		    	        var mapOptions = {
	    		            center: new google.maps.LatLng(lineStamp.glat, lineStamp.glng),
	    		            zoom: 12,
	    		            mapTypeId: google.maps.MapTypeId.ROADMAP,
	    		            scaleControl: true
	    		        };
	    		        var map = new google.maps.Map(document.getElementById("map"), mapOptions);
		        		
		    	        // 创建信息展示窗口
		    	        var infoWindow = new google.maps.InfoWindow();
		        		var flightPlanCoordinates = new Array();
	        			$.each(lines, function(i, line){ 
	        				var k = i + 1;
		    	            var myLatlng = new google.maps.LatLng(line.glat, line.glng);
		    	            var marker = new google.maps.Marker({
		    	                map: map,
		    	                position: myLatlng,
		    	                label: k
		    	            });
		    	            // marker绑定click事件
		    	            (function (marker, line) {
		    	                google.maps.event.addListener(marker, "click", function (e) {
		    	                    // 信息窗口添加内容
		    	                    infoWindow.setContent("<div style='width:160px;min-height:20px'><label>时间：</label><span>" + line.createDate + "</span></div>");
		    	                    infoWindow.open(map, marker);
		    	                });
		    	            })(marker, line); 
		    	            
		    	            // 线点数据
		    	            flightPlanCoordinates.push(new google.maps.LatLng(line.glat, line.glng));
	        			});
	    				//定义线  
				        var flightPath = new google.maps.Polyline({  
				            path: flightPlanCoordinates,  
				            strokeColor: "#FF0000",  
				            strokeOpacity: 1.0,  
				            strokeWeight: 2  
				        });  
	    				//将线添加至地图  
	    				flightPath.setMap(map); 
	        		} else {
						top.$.jBox.info('当前时间段内设备无开机数据!', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
		    	        var mapOptions = {
	    		            center: new google.maps.LatLng(31.22, 121.48),
	    		            zoom: 12,
	    		            mapTypeId: google.maps.MapTypeId.ROADMAP,
	    		            scaleControl: true
	    		        };
	    		        var map = new google.maps.Map(document.getElementById("map"), mapOptions);
	        		}
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
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMap">联网分布图</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/deviceLineMap">设备线路图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/devicePosition">设备定位</a></li>
	</ul>
	<!-- tab e -->
	
	<form:form id="searchForm" class="breadcrumb form-search">
		<div>
			<label>设备编号：</label>
			<input id="sn" name="sn" type="text" maxlength="50" class="input-small required" /> 
			<label>时间：</label> 
			<input id="startDateStr" name="startDateStr" type="text" readonly="readonly" maxlength="20" value="${startDate }" class="input-medium Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,maxDate:'#F{$dp.$D(\'endDateStr\',{H:-1})}'});" />
			到
			<input id="endDateStr" name="endDateStr" type="text" readonly="readonly" maxlength="20" value="${endDate }" class="input-medium Wdate required" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true,maxDate:'%y-%M-%d %H:%m:%s'});" />&nbsp;&nbsp; 
			&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看" />
		</div>
	</form:form>
	
	<div id="map"></div>
</body>
</html>