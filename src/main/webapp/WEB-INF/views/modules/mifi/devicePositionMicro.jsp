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
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
    <script src="https://cn.bing.com/mapspreview/sdk/mapcontrol?branch=release"></script>
	<script type="text/javascript">
	    var map = null;
		var pinLocation = null;
		var pin = null;
		var pinInfobox = null;
		
	    $(function() {
	    	// 时间戳点击事件
	    	$('#btnSubmit').click(function() {
	    		var lat = $('#lat').val();
	    		var lng = $('#lng').val();
	        	if (isEmpty(lat)) {
					top.$.jBox.info('请输入纬度!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
	        	if (isEmpty(lng)) {
					top.$.jBox.info('请输入经度!', '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
					return false;
	        	}
				top.$.jBox.tip("加载中...", 'loading', {persistent: true});
				$('#btnSubmit').attr("disabled","true");
		        LoadMap(lat, lng);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(glat, glng) {
			if(!!map){
				map.dispose();
			}
			//大头针位置
			pinLocation = new Microsoft.Maps.Location(glat, glng);//正轴北纬,正轴东经
			map = new Microsoft.Maps.Map(document.getElementById("mapDiv"),{
				credentials:"Au7Uc88PlfdjoQdvJxMHamlX-v3hitjEZfWHDmNMRQ4VYaNbSz_HJIPcFrFE83nI",
				showScalebar:false, //比例尺
				showCopyright:false, //版权
				showDashboard:true, //工具栏
				showLocateMeButton:false,
				showMapTypeSelector:false,
				showZoomButtons:true,
				zoom: 3, 
				mapTypeId:Microsoft.Maps.MapTypeId.road,
				center:pinLocation
			});
			//大头针标记
			pin = new Microsoft.Maps.Pushpin(pinLocation);
			map.entities.push(pin);
			
			top.$.jBox.closeTip();
			$('#btnSubmit').removeAttr("disabled");
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMapMicro">联网分布图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/deviceLineMapMicro">设备线路图</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/devicePositionMicro">设备定位</a></li>
	</ul>
	<!-- tab e -->
	
	<form:form id="searchForm" class="breadcrumb form-search">
		<div>
			<label>纬度：</label>
			<input id="lat" name="lat" type="text" maxlength="50" class="input-small required" /> 
			<label>经度：</label>
			<input id="lng" name="lng" type="text" maxlength="50" class="input-small required" /> 
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查 看" />
		</div>
	</form:form>
	
	<div id="mapDiv"></div>
</body>
</html>