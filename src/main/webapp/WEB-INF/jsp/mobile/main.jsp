<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
				var loggedIn = false;
				
				var jspArgs = (function(){
					var jspArgsValues = {}; // Values stored here, protected by a closure
					var jspArgsObj = {};    // This gets augmented and assigned to jspArgs
				 
					jspArgsObj.set = function(name){
						return function(value){
							name && (jspArgsValues[name] = value);
						};
					};
				 
					jspArgsObj.get = function(name){
						return jspArgsValues[name];
					};
				 
					return jspArgsObj;
				})();
				
				jspArgs.set('li')('<c:out value="${li}" />');
				jspArgs.set('un')('<c:out value="${un}" />');
				jspArgs.set('pw')('<c:out value="${pw}" />');
				
				if (jspArgs.get('li') === '1') {
					loggedIn = true;
						
					$('#mainHeaderCrtAcctBtn').hide();
					
					$('#mainHeaderLoginBtn .ui-btn-text').text('Logout');
				}
				
				$('#mainHeaderLoginBtn').click(function(click_e) {
					
					if (loggedIn) {
						$.post("logout", {}, function(data) {
							loggedIn = false;
							$('#mainHeaderLoginBtn .ui-btn-text').text('Login');
							$('#mainHeaderCrtAcctBtn').show();
						});
						return false;
					}
					
					return true;
				});
				
				$('#mobileMainSearchForm').submit(function() {
					window.location.href = "search_params?q=" + encodeURIComponent($("#mobileMainSearchTxtField").val());
					
					return false;
				});
			});
		</script>
		
		<div data-role="content" class="ui-body ui-body-a">
			<div data-role="header" id="mainPageNavHeader">
				<a id="mainHeaderLoginBtn" href="logindialog?to=main" data-icon="home" class="ui-btn-left" data-rel="dialog">Login</a>
				<h1>BeerMap</h1>
				<a id="mainHeaderCrtAcctBtn" href="createacct" data-icon="plus" class="ui-btn-right" rel="external">Create Account</a>
			</div>
			
			<form id="mobileMainSearchForm">
			<table class="center">
				<tr>
					<td class="alignRight">Search:</td>
					<td><input type="search" name="nMobileMainSearchTxtField" id="mobileMainSearchTxtField" value="" /></td>
				</tr>
			</table>
			</form>
			
			
	</div>
</body>
</html>