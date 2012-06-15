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
	<title>Login</title>
</head>
<body>
	<div data-role="page" id="loginPage">
		<script>
			$('#loginPage').live('pageinit', function(page_e) {
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
				
				jspArgs.set('to')('<c:out value="${to}" />');
				
				$.mobile.pushStateEnabled=false;
				
				$('#problemTextDiv').hide();
				
				$('#loginBtn').click(function(click_e) {
					$('#loginForm').submit();
					click_e.preventDefault();
				});
				
				$('#loginForm').submit(function() {
					$.post(
						"login", 
						{
							un: $('#loginUsernameTextField').val(),
							pw: $('#loginPasswordTextField').val()
						},
						function(data) {
							var jsonResult = $.parseJSON(data);
							if (jsonResult.result) {
								$('#problemTextDiv').hide();
								window.location.href = jspArgs.get('to');
								
							} else {
								$('#problemTextDiv').show();
							}
						}
					);		
					
					return false;
				});
			});
		</script>
		<div data-role="content" class="ui-body ui-body-a">
			<form id="loginForm">
				<table class="center">
					<tr>
						<td class="alignRight">Username:</td>
						<td><input type="text" name="nLoginUsernameTextField" id="loginUsernameTextField" value=""  /></td>
					</tr>
					<tr>
						<td class="alignRight">Password:</td>
						<td><input type="password" name="nLoginPasswordTextField" id="loginPasswordTextField" value=""  /></td>
					</tr>
				</table>	
				
				<div id="problemTextDiv" class="textCenter textColorException">There was a issue with the username and password.</div>
				
				<div class="centerHalf">
					<input type="button" id="loginBtn" value="Login"/>
				</div>
			</form>
		</div>
		
	</div>
</body>
</html>