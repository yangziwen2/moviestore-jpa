<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>影视列表 [${website.displayName}]</title>
<%@ include file="../include/includeCss.jsp" %>
<style>
.title {
	text-align: center;
	font-family: 微软雅黑;
}
.list-wrapper {
	width: 100%; margin: -10px auto 20px;
}
.list-wrapper .table {
	width: 100%;
}
.list-wrapper th, .list-wrapper td {
	text-align: center;
}
#J_pageBar {
	margin-top: 10px;
	margin-bottom: 10px;
}
.query-form-wrapper {
	width: 700px; margin: 20px auto 10px;
}
.query-form-wrapper form {
	margin-bottom: 0px;
}
.query-form-wrapper table {
	width: 500px; margin: 0px auto;
}
.query-form-wrapper .label-wrapper {
	text-align: right; width: 20%;
}
.query-form-wrapper .input-wrapper {
	width: 20%;
}
.query-form-wrapper select {
	width: 165px;
}
.query-form-wrapper .btn-wrapper {
	text-align: center;
}
.query-form-wrapper .btn {
	width: 82px;
}
.btn-sep {
	width: 15px; display: inline-block;
}
.info-wrap a {
	white-space:nowrap;
}
dl {
	margin-top: 0px;
	padding-bottom: 10px;
	border-bottom: 1px solid #ccc;
}
dd {
	white-space:nowrap;
	overflow: hidden;
}
img {
	border: 1px solid #ccc;
}
</style>
</head>
<body>
<div class="wrapper">
	<div style="width: 1200px; margin: 50px auto 50px;">
		<h2 class="title">影视列表 [ <a href="${website.homePageUrl}" target="_blank">${website.displayName}</a> ]</h2>
		<div class="query-form-wrapper">
			<table style="margin-left: 80px;">
				<tbody id="J_queryTbody">
					<tr>
						<td class="label-wrapper">
							<label for="J_title">
								<strong>名称:&nbsp;</strong>
							</label>
						</td>
						<td class="input-wrapper">	
							<input id="J_title" type="text" class="input-medium" value="${param.title}" />
						</td>
						<td class="label-wrapper">
							<label for="J_actor">
								<strong>演员:&nbsp;</strong>
							</label>
						</td>
						<td class="input-wrapper">	
							<input id="J_actor" type="text" class="input-medium" value="${param.actor}" />
						</td>
					</tr>
					<tr>
						<td class="label-wrapper">
							<label for="J_category">
								<strong>类型:&nbsp;</strong>
							</label>
						</td>
						<td>
							<select id="J_category" class="input-medium">
								<option value="">全部</option>
								<c:forEach items="${categoryList}" var="category">
									<option value="${category}" <c:if test="${param.category == category}">selected</c:if>>
										${category}
									</option>
								</c:forEach>
							</select>
						</td>
						<td class="label-wrapper">
							<label for="J_subcategory">
								<strong>分类:&nbsp;</strong>
							</label>
						</td>
						<td>
							<select id="J_subcategory" disabled="disabled" class="input-medium">
								<option value="">全部</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="label-wrapper">
							<label for="J_area">
								<strong>地区:&nbsp;</strong>
							</label>
						</td>
						<td>
							<select id="J_area" class="input-medium">
								<option value="">全部</option>
								<c:forEach items="${areaList}" var="area">
									<option value="${area}" <c:if test="${param.area == area}">selected</c:if>>
										${area}
									</option>
								</c:forEach>
							</select>
						</td>
						<td class="label-wrapper">
							<label for="J_year">
								<strong>年份:&nbsp;</strong>
							</label>
						</td>
						<td>
							<select id="J_year" class="input-medium">
								<option value="">全部</option>
								<c:forEach items="${yearList}" var="year">
									<option value="${year}" <c:if test="${param.year == year}">selected</c:if>>
										${year}
									</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="btn-wrapper" colspan="4">
							<div style="margin-left: 50px;">
								<button id="J_queryBtn" class="btn btn-primary">&nbsp;查&nbsp;&nbsp;询&nbsp;</button>
								<div class="btn-sep">&nbsp;</div>
								<button id="J_clearBtn" class="btn btn-primary">清除条件</button>
								<div class="btn-sep">&nbsp;</div>
								<a style="width: 60px;" id="J_clearBtn" class="btn btn-primary" href="${ctx_path}/">返回首页</a>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<hr/>
		<div class="list-wrapper clearfix">
			<div class="row-fluid">
				<div class="span2">
				</div>
				<div class="span10">
					<div id="J_pageBarTop"></div>
				</div>
			</div>
			<div class="info-wrap clearfix">
				<c:forEach var="movieInfo" items="${page.list}">
				<dl style="float:left; width: 150px; text-align: center;">
					<dd style="overflow: hidden;" title="${movieInfo.title}">
						<strong>
							<a href="${website.getMovieUrl(movieInfo.movieId)}" target="_blank">&nbsp;${movieInfo.title}&nbsp;</a>
						</strong>
					</dd>
					<dd>
						<a href="${website.getMovieUrl(movieInfo.movieId)}" target="_blank">
							<c:choose>
								<c:when test="${website.mockPhotoReferer eq true}">
									<img class="lazy" data-original='<c:url value="${ctx_path}/photo/mockReferer">
									<c:param name="referer" value="${website.getMovieUrl(movieInfo.movieId)}" />
									<c:param name="photoUrl" value="${movieInfo.photoUrl}" />
									</c:url>' style="width: 150px; height: 195px;">
								</c:when>
								<c:otherwise>
									<img class="lazy" data-original="${movieInfo.photoUrl}" style="width: 150px; height: 195px;">
								</c:otherwise>
							</c:choose>
						</a>
					</dd>
					<dd></dd>
					<dd>地区：${movieInfo.area}</dd>
					<dd>年份：${movieInfo.year}</dd>
					<dd>类型：${movieInfo.category}</dd>
					<dd>分类：${movieInfo.subcategory}</dd>
				</dl>
				</c:forEach>
			</div>
			<div class="row-fluid">
				<div class="span2">
				</div>
				<div class="span10">
					<div id="J_pageBarBottom"></div>
				</div>
			</div>
		</div>
	</div>
</div>

</body>
<%@ include file="../include/includeJs.jsp" %>
<script type="text/javascript" src="${ctx_path}/js/bootstrap/bootstrapPageBar.js"></script>
<script type="text/javascript" src="${ctx_path}/js/jquery/jquery.lazyload.js"></script>
<script type="text/javascript">
$(function(){
	initPageBar();
	initQueryBtn();
	initClearBtn();
	initQueryFields();
	initSubcategorySel();
	loadImg();
});
function loadImg() {
	$('img.lazy').lazyload({
		threshold: 300,
		effect: 'fadeIn',
		placeholder: 'http://www.xunleicang.com/Public/images/nophoto.jpg'
	});
}
function initPageBar() {
	var start = parseInt('${page.start}'),
		limit = parseInt('${page.limit}'),
		totalCount = parseInt('${page.count}');
	buildPageBar('#J_pageBarTop, #J_pageBarBottom', start, limit, totalCount);
}
function buildPageBar(pageBarEls, start, limit, totalCount){
	pageBarEls = $(pageBarEls);
	if(pageBarEls.size() == 0) {
		return;
	}
	var curPage = Math.floor(start / limit) + 1;
	var totalPage = Math.floor(totalCount / limit) + (totalCount % limit > 0? 1: 0);
	pageBarEls.bootstrapPageBar({
		curPageNum: curPage,
		totalPageNum: totalPage,
		maxBtnNum: 10,
		pageSize: limit,
		siblingBtnNum: 2,
		paginationCls: 'pagination-right',
		click: function(i, pageNum){
			var start = (pageNum - 1) * limit;
			var url = CTX_PATH + '/movie/${website.name}/list'
				+ '?start=' + start
				+ '&limit=' + limit;
			url = appendParams(url);
			location.href = discardEmptyParams(url);
		}
	});
}

function initQueryFields() {
	$('#J_queryTbody input[type=text]').on('keyup', function(ev){
		if(ev.which == 13) {
			$('#J_queryBtn').trigger('click');
		}
	});
}

function initSubcategorySel() {
	var $category = $('#J_category'),
		$subcategory = $('#J_subcategory');
	$category.on('change', function(){
		if(!$category.val()) {
			$subcategory.val('').attr('disabled', true);
		} else {
			refreshSubcategorySel('${webste.name}', $category.val());
		}
	});
	$category.trigger('change');
}

function refreshSubcategorySel(websiteName, category) {
	var $subcategory = $('#J_subcategory');
	$subcategory.children(':gt(0)').remove();
	$.getJSON(CTX_PATH + '/movie/${website.name}/listSubcategory?category=' + encodeURIComponent(category), function(data){
		if(data.success !== true || !$.isArray(data.subcategoryList)) {
			return;
		}
		var selected = '';
		for(var i = 0, l = data.subcategoryList.length; i < l; i++) {
			$subcategory.append('<option value="' + data.subcategoryList[i] + '">' + data.subcategoryList[i] + '</option>');
			if('${param.subcategory}' == data.subcategoryList[i]) {
				selected = data.subcategoryList[i];
			}
		}
		$subcategory.attr('disabled', data.subcategoryList.length == 0).val(selected);
	});
}

function initQueryBtn() {
	$('#J_queryBtn').on('click', function(){
		var url = appendParams(CTX_PATH + '/movie/${website.name}/list');
		location.href = discardEmptyParams(url);
	});
}

function appendParams(url) {
	var firstSep = url.indexOf('?') < 0? '?': '&';
	return url 
		+ firstSep +'area=' + encodeURIComponent($('#J_area').val())
		+ '&year=' + $('#J_year').val()
		+ '&title=' + encodeURIComponent($('#J_title').val())
		+ '&category=' + encodeURIComponent($('#J_category').val())
		+ '&subcategory=' + encodeURIComponent($('#J_subcategory').val())
		+ '&actor=' + encodeURIComponent($('#J_actor').val());
}

function initClearBtn() {
	$('#J_clearBtn').on('click', function(){
		$('#J_area').val('');
		$('#J_year').val('');
		$('#J_title').val('');
		$('#J_category').val('');
		$('#J_subcategory').val('');
		$('#J_actor').val('');
		$('#J_queryBtn').trigger('click');
	});
}

function discardEmptyParams(url) {
	url = url.replace(/(?:\?|&)([^\/\?&]+?=)(?=&|$)/g, '');
	if(url.indexOf('&') >= 0 && url.indexOf('?') == -1) {
		url = url.replace('&', '?');
	}
	return url;
}
</script>
</html>