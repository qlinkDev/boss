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
    .map-marker-label{
        position: absolute;
	    color: blue;
	    font-size: 16px;
	    font-weight: bold;
    }
    </style>
	<!--微软Bing Map ajax控制参考文档https://msdn.microsoft.com/en-us/library/gg427609.aspx-->
    <script src="https://cn.bing.com/mapspreview/sdk/mapcontrol?branch=release"></script>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
	    var map = null;
		var pinLocation = null;
		var pin = null;
		var pinInfobox = null;
		var centerLocation = null;
	    
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
				top.$.jBox.tip("加载中...", 'loading', {persistent: true});
				$('#btnSubmit').attr("disabled","true");
		        LoadMap(imei, startDate, endDate);
	    	});
	    });
	    
	    // 加载Map
	    function LoadMap(imei, startDate, endDate) {
	        if(!!map){
				map.dispose();
			}
			// 异步获取JSON数据
	        var url = '${ctx}/mifi/mifiDevice/lineJsonData';
	        $.post(url, {imei:imei, startDateStr:startDate, endDateStr:endDate}, function(data) {
				top.$.jBox.closeTip();
	        	if (data.code == '1') {
	        		var lines = data.lines;
	        		if (lines.length > 0) {
	        			var firstLine = lines[0];
						centerLocation = new Microsoft.Maps.Location(firstLine.glat, firstLine.glng);
						map = new Microsoft.Maps.Map(document.getElementById("mapDiv"),{
							credentials:"Au7Uc88PlfdjoQdvJxMHamlX-v3hitjEZfWHDmNMRQ4VYaNbSz_HJIPcFrFE83nI",
							showScalebar:false, //比例尺
							showCopyright:false, //版权
							showDashboard:true, //工具栏
							showLocateMeButton:false,
							showMapTypeSelector:false,
							showZoomButtons:true,
							zoom: 5, 
							mapTypeId:Microsoft.Maps.MapTypeId.road,
							center:centerLocation
						}); 
		        		
		    	        // 创建一群大头针并连线
		        		var vertices = new Array();
	        			$.each(lines, function(i, line){
							//大头针位置
							pinLocation = new Microsoft.Maps.Location(line.glat, line.glng);//正轴北纬,正轴东经
							//大头针标记
							pin = new Microsoft.Maps.Pushpin(pinLocation, {text:i + 1 + ''}); 
							pin.metadata = {
								description: "<div><label>时间：</label><span>" + line.createDate + "</span></div>"
							};
							Microsoft.Maps.Events.addHandler(pin, 'click', displayInfobox);
							map.entities.push(pin);
		    	            // 线点数据
		    	            vertices.push(pinLocation);
	        			});
	    				//添加线  
						var line = new Microsoft.Maps.Polyline(vertices);
						map.entities.push(line);
						//大头针消息盒子
						pinInfobox = new Microsoft.Maps.Infobox(centerLocation, {
							visible: false
						});
						pinInfobox.setMap(map);
						
						$('#btnSubmit').removeAttr("disabled");
	        		} else {
						top.$.jBox.info('当前时间段内设备无开机数据!', '系统提示');
						top.$('.jbox-body .jbox-icon').css('top','55px');
	        		}
	        	} else {
					top.$.jBox.info(data.msg, '系统提示');
					top.$('.jbox-body .jbox-icon').css('top','55px');
	        	}
	        });
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
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMapMicro">联网分布图</a></li>
		<li class="active"><a href="${ctx}/mifi/mifiDevice/deviceLineMapMicro">设备线路图</a></li>
		<li><a href="${ctx}/mifi/mifiDevice/devicePositionMicro">设备定位</a></li>
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
	
	<div id="mapDiv"></div>
</body>
</html>