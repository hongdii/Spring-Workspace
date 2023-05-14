<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<form action="<%= request.getContextPath()%>/hello?name=bk" method="get">
	<div>인사말 : 안녕하세요, bk</div>
</form>
</body>
</html>