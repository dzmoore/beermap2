<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/main.css"/>" />
<title>${result}</title>
</head>
<body>
	<span class="entry">
		<h2 class="item">${mb.name}</h2><br/>
			<table class="invis">
				<tr class="invis"><td class="invis"><b class="item">Brewery:</b></td><td class="invis"><span class="item">${mb.brewery.name}</span></td></tr>
				<tr class="invis"><td class="invis"><b class="item">ABV:</b></td><td class="invis"><span class="item">${mb.abv}</span></td></tr>
				<tr class="invis"><td class="invis"><b class="item">Description:</b></td><td class="invis"><span class="item">${mb.descript}</span></td></tr>
			</table>

	</span>
</body>
</html>