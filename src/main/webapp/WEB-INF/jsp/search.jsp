<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search</title>
</head>
<body>
	<form method="post">
		Beer Name 
		<input type="text" name="beerName" value="${beerName}" /> 
		<input type="submit" value="Search" />
	</form>

	<table border="1">
		<tr>
			<th>Beer Name</th>
			<th>Abv</th>
			<th>Description</th>
		</tr>
		<c:forEach items="${beers}" var="eaBeer">
			<tr>
				<td>${eaBeer.name}</td>
				<td>${eaBeer.abv}</td>
				<td>${eaBeer.descript}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>