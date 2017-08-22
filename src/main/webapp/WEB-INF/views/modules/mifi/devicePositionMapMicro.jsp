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
      #mapDiv {
        height: 88%;
        margin-top: 10px;
      }
    </style>
	<!--微软Bing Map ajax控制参考文档https://msdn.microsoft.com/en-us/library/gg427609.aspx-->
	<!--https://cn.bing.com/mapspreview/sdk/mapcontrol/isdk#overview-->
    <script src="https://cn.bing.com/mapspreview/sdk/mapcontrol?branch=release"></script>
	<script type="text/javascript">
	    var map = null;
		var pinLocation = null;
		var pin = null;
		var pinInfobox = null;
		//https://www.bingmapsportal.com/Content/images/poi_custom.png
		var pinIcon = "http://ecn.dev.virtualearth.net/mapcontrol/v7.0/7.0.20160525132934.57/i/poi_search.png";
		var centerLocation = null;
		
		// 页面加载默认显示1小时内设备联网分布图
	    window.onload = function () {
	        centerLocation = new Microsoft.Maps.Location(0, 0);
			//LoadMap(1);
	    }
	    
	    $(function() {
	    	// 时间戳点击事件
	    	$('#btnSubmit').click(function() {
	    		top.$.jBox.tip("加载中...", 'loading', {persistent: true});
				$('#btnSubmit').attr("disabled","true");
				var timeStamp = $('#timeStamp option:selected').val();
		        LoadMap(timeStamp);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(timeStamp) {
			if(!!map){
				map.dispose();
			}
			map = new Microsoft.Maps.Map(document.getElementById("mapDiv"),{
				credentials:"Au7Uc88PlfdjoQdvJxMHamlX-v3hitjEZfWHDmNMRQ4VYaNbSz_HJIPcFrFE83nI",
				showScalebar:false, //比例尺
				showCopyright:false, //版权
				showDashboard:true, //工具栏
				showLocateMeButton:false,
				showMapTypeSelector:false,
				showZoomButtons:true,
				mapTypeId:Microsoft.Maps.MapTypeId.road,
				zoom:2,
				center: centerLocation
			});
	        
	        // 异步获取JSON数据
	        var url = '${ctx}/mifi/mifiDevice/positionJsonData';
	        $.post(url, {dateStr:timeStamp}, function(data) {
	        	if (data.code == '1') {
	    	        // 展示在线设备数
	        		var positions = data.positions;
	        		$('#onLineDeviceNum').text(positions.length);
					// 创建一群大头针
        			$.each(positions, function(i, position){
						//大头针位置
						pinLocation = new Microsoft.Maps.Location(position.glat, position.glng);//正轴北纬,正轴东经
						//大头针标记
						pin = new Microsoft.Maps.Pushpin(pinLocation);
						pin.metadata = {
							description: "<div><label>编号：</label><span>" + position.imei + "</span><br/><label>时间：</label><span>" + position.createDate + "</span></div>"
						};
                        Microsoft.Maps.Events.addHandler(pin, 'click', displayInfobox);
			            map.entities.push(pin);
        			});
					
					top.$.jBox.closeTip();
					$('#btnSubmit').removeAttr("disabled");
	        	} else {
					top.$.jBox.info(data.msg, '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
	        	}
	        });
			//大头针消息盒子
			pinInfobox = new Microsoft.Maps.Infobox(centerLocation, {
                visible: false
			});
			pinInfobox.setMap(map);
	    }
		
		function displayInfobox(e){
			pinInfobox.setOptions({
                location: e.target.getLocation(),
                description: e.target.metadata.description,
                visible: true
            });
        }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mifi/mifiDevice/devicePositionMapMicro">联网分布图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/deviceLineMapMicro">设备线路图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMicro">设备定位</a></li>
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
			</select>&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看" />
			<div style="float: right;margin-right: 100px;margin-top: 5px;">
				<label>在线设备数：</label>
		  		<span id="onLineDeviceNum" style="color: red;">0</span>
		  	</div>
		</div>
	</form:form>
	<div id="mapDiv"></div>
</body>
</html>