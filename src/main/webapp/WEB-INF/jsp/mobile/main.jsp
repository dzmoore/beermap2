<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mobile.css"/>" />
	<script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>
	<title>${result}</title>
</head>
<body>
	<div data-role="page" id="mobileMainPage">	
		<script>
			
			$('#mobileMainPage').live('pageinit', function(page_e) {
				$.mobile.pushStateEnabled=false;
				var isLoggedIn = '<c:out value="${li}"/>' !== null && '<c:out value="${li}"/>' === '1';
				
				$.getScript('<c:url value="/resources/scripts/nav.js"/>', function() {
					initNav(
				 		$('#mainHeaderLoginBtn'), 
				 		$('#mainHeaderLogoutBtn'),
				 		$('#mainHeaderCrtAcctBtn'), 
				 		'<c:out value="${li}" />'
				 	);
				});
				
				$('#mobileMainSearchForm').submit(function() {
					window.location.href = "search?q=" + encodeURIComponent($("#mobileMainSearchTxtField").val());
					
					return false;
				});
				
				$('#createBeerBtn').click(function(click_e) {
					window.location.href = "createbeer";
				});
				
				var btnRow = $('#createBeerBtnRow');
				
				$('#mainHeaderLogoutBtn').click(function(click_e) {
					btnRow.hide();
				});
								
				if (isLoggedIn) { 
					btnRow.show();
					
				} else {
					btnRow.hide();
				}
			});
		</script>
		
		<div data-role="header" id="mainPageNavHeader">
				<a id="mainHeaderLoginBtn" href="logindialog?to=main" data-icon="home" class="ui-btn-left" data-rel="dialog">Login</a>
				<h1>BeerMap</h1>
				<a id="mainHeaderCrtAcctBtn" href="createacct" data-icon="plus" class="ui-btn-right" rel="external">Create Account</a>
				<a id="mainHeaderLogoutBtn" href="#" data-icon="minus" class="ui-btn-right">Logout</a>
		</div>
		<div data-role="content" class="ui-body ui-body-a">			
			
			<form id="mobileMainSearchForm">
			<table class="center">
				<tr>
					<td class="alignRight">Search:</td>
					<td><input type="search" name="nMobileMainSearchTxtField" id="mobileMainSearchTxtField" value="" /></td>
				</tr>
				<tr>
					<td id="createBeerBtnRow" colspan="2">
					<input type="button" name="nCreateBeerBtn" id="createBeerBtn" value="Create New Beer" />
					<td>
				</tr>
			</table>
			</form>
			
			
	</div>
</body>
</html>