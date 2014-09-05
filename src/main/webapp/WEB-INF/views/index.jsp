<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="./include/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>影视网站</title>
<%@ include file="./include/includeCss.jsp" %>
<style>
h1, h2 {
	text-align: center;
	font-family: 微软雅黑;
}
.website-btn {
	width: 170px;
	margin: 10px;
	height: 50px;
	margin: 20px auto;
	border: 5px solid #bbb;
	text-align: center;
	padding-top: 0px;
	cursor: pointer;
	color: #555;
}

.website-btn:hover {
	color: #058;
	border-color: #08c;
}

.website-btn h2 {
	margin-top: 3px;
	color: inherit;
}
</style>
</head>
<body>
<div class="wrapper">
	<div style="width: 800px; margin: 100px auto 50px;">
		<h1 class="title">影视网站</h1>
		<div style="width: 800px; margin-top: 60px;">
		<c:forEach var="website" items="${websiteList}">
			<div class="website-btn" data-list-page="${ctx_path}/movie/${website.name}/list">
				<h2>${website.displayName}</h2>
			</div>
		</c:forEach>
		</div>
	</div>
</div>

</body>
<%@ include file="./include/includeJs.jsp" %>
<script>
$(function(){
	initWebsiteBtn();
});

function initWebsiteBtn() {
	$('div.website-btn').on('click', function(){
		window.open($(this).data('list-page'), '_blank');
	});
}
</script>
</html>