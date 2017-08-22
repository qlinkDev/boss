<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/common/utils.js" type="text/javascript"></script>
	<script type="text/javascript">
		// 广告上架
		function shelfUp(id) {
			top.$.jBox.confirm("确认要时行当前操作吗？", "系统提示", function(v,h,f) {
				if(v == "ok"){
					var url = '${ctx}/om/goods/shelfUp.json';
					$.post(url, {id:id}, function(data) {
						if (data.code == '1') {
							top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
							$('#searchForm').submit();
						} else {
							if (isEmpty(data.msg))
								top.$.jBox.info('广告上架失败', '系统提示');
							else
								top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}
					}, 'json');
				}
			}, {buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		// 广告下架
		function shelfDown(id) {
			top.$.jBox.confirm("确认要时行当前操作吗？", "系统提示", function(v,h,f) {
				if(v == "ok"){
					var url = '${ctx}/om/goods/shelfDown.json';
					$.post(url, {id:id}, function(data) {
						if (data.code == '1') {
							top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
							$('#searchForm').submit();
						} else {
							if (isEmpty(data.msg))
								top.$.jBox.info('广告下架失败', '系统提示');
							else
								top.$.jBox.info(data.msg, '系统提示');
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}
					}, 'json');
				}
			}, {buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
	
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<!-- tab S -->
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/om/goods/">广告列表</a></li>
		<shiro:hasPermission name="om:goods:edit"><li><a href="${ctx}/om/goods/form">广告添加</a></li></shiro:hasPermission>
	</ul>
	<!-- tab E -->
	
	<!-- 查询 S -->
	<form:form id="searchForm" modelAttribute="advertisingCondition" action="${ctx}/om/goods/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>广告名称：</label>
		<form:input path="likeName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>类型：</label> 
		<select id="eqUseFlag" name="eqUseFlag" class="input-small">
			<option value="">--请选择--</option>
			<c:forEach items="${fns:getDictList('om_advertising_type')}" var="type">
				<option value="${type.value}" <c:if test="${type.value==advertisingCondition.eqType}">selected</c:if>>${type.label}</option>
			</c:forEach>
		</select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<!-- 查询 E -->
	
	<!-- 信息提示 S -->
	<tags:message content="${message}"/>
	<!-- 信息提示 E -->
	
	<!-- 数据列表 S -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>广告名称</th>
				<th>类型</th>
				<th>上/下架</th>
				<th>代理商</th>
				<th>投放国家</th>
				<th>创建时间</th>
				<th>图片</th>
				<shiro:hasPermission name="om:goods:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="goods">
			<tr>
				<td>${goods.name}</td>
				<td>${fns:getDictLabel(goods.type, 'om_advertising_type', '未配置类型')}</td>
				<td>${fns:getDictLabel(goods.shelfUpDown, 'sys_shelf', '未配置')}</td>
				<td>${goods.channelNames}</td>
				<td>${goods.countryNames}</td>
				<td><fmt:formatDate value="${goods.createDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				<td>
				<c:forEach items="${goods.itemList}" var="item">
					<a class="fancybox" target="_blank" href="${item.imgPath}">
						<img style="max-width:60px;max-height:60px;border:0;padding:3px;" src="${item.imgPath}" onerror="javascript:this.src='${ctxStatic}/images/error.png';"/>
					</a>&nbsp;&nbsp;
				</c:forEach> 
				</td>
				<td>
					<shiro:hasPermission name="om:goods:edit">
	    				<a href="${ctx}/om/goods/form?id=${goods.id}">修改</a>
						<c:if test="${goods.shelfUpDown=='UP'}">
						<a href="javascript:void(0);" onclick="javascript:shelfDown('${goods.id}');">下架</a>
						</c:if>
						<c:if test="${goods.shelfUpDown=='DOWN'}">
						<a href="javascript:void(0);" onclick="javascript:shelfUp('${goods.id}');">上架</a>
						</c:if>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!-- 数据列表 E -->
	
	<!-- 分页 S -->
	<div class="pagination">${page}</div>
	<!-- 分页 E -->
</body>
</html>