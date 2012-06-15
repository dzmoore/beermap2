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
	<title>Create New Account</title>
</head>
<body>
	<div data-role="page" id="createAcctPage">	
		<script>
			$.mobile.pushStateEnabled=false;
			var MIN_UN_LEN = 3;
			var MIN_PW_LEN = 4;
			
			$('#createAcctPage').live('pageinit', function(page_e) {
				$('#createUsernameTextField').keyup(function() {
					var partialUsername = $(this).val();
					
					if (partialUsername.length === 0) {
						setFieldState($(this), "clear");
						return;
					}
										
					if (isValidUsername()) {
						$.post(
							"userexists", 
							{q: partialUsername},
							function(data) {
								var jsonResp = $.parseJSON(data);
								
								if (jsonResp.result) {
									setFieldState($('#createUsernameTextField'), "bad");
									
								} else {
									setFieldState($('#createUsernameTextField'), "good");
								}
							}
						);
					
					} else {
						setFieldState($(this), "bad");
					}
				});
				
				$('#createPwTextField').keyup(function() {
					var pw = $(this).val();
					var againPw = $('#createPwAgainTextField').val();
					
					$(this).removeClass("badInput");
					$(this).removeClass("goodInput");
					
					if (pw.length === 0) {
						return;
					}
					
					if (againPw.length === 0 && pw.length > MIN_PW_LEN) {
						setFieldState($(this), "good");
											
					} else if (pw === againPw) {
						setFieldState($(this), "good");
					
					} else {
						setFieldState($(this), "bad");
					}
				});
				
				$('#createPwAgainTextField').keyup(function() {
					var againPw = $(this).val();
					var pw = $('#createPwTextField').val();
					
					if (againPw.length === 0) {
						return;
					}
					
					if (pw === againPw) {
						setFieldState($(this), "good");
					
					} else {
						setFieldState($(this), "bad");
					}
				});
				
				$('#createAcctBtn').click(function(click_e) {
					if (isValidUsername()) {
						
										
						var un = $('#createUsernameTextField').val();
						
						$.post(
							"userexists", 
							{q: un},
							function(data) {
								var jsonResp = $.parseJSON(data);
								var userExists = jsonResp.result;
								if (userExists) {
								
									setFieldState($(this), "bad");
									
									alert("User already exists.");
									
									$('#createAcctBtn').attr("disabled", false);
									$('#createAcctBtn').val("Create Account");
									
								} else {
									postCreateUser();	
								}
							}
						);		
					}
				});	
			});
			
			function postCreateUser() {
				$.post(
					"createacct", 
					{
						un: $('#createUsernameTextField').val(),
						pw: $('#createPwTextField').val()
					},
					function(data) {
						var jsonResp = $.parseJSON(data);
						
						var createSuccess = jsonResp.result;
						if (createSuccess) {
							handleCreateSuccess();
							
						} else {
							handleCreateFail();
						}
					}
				);		
			}
			
			function handleCreateSuccess() {
				alert("Create success");
				$('#createAcctBtn').attr("disabled", false);
				$('#createAcctBtn').val("Create Account");
			}
			
			function handleCreateFail() {
				alert("Create failed");
				$('#createAcctBtn').attr("disabled", false);
				$('#createAcctBtn').val("Create Account");
			}
			
			function isValidUsername() {
				var un = $('#createUsernameTextField').val();
				var usernameRegex = new RegExp("[a-zA-Z1-9]{" + MIN_UN_LEN + ",}", "g");
				var regExdInput = usernameRegex.exec(un);
				
				return regExdInput == un;
			}
			
			function setFieldState(field, state) {
				if (state == "clear") {
					field.removeClass("badInput");
					field.removeClass("goodInput");
				
				} else if (state == "good") {
					field.addClass("goodInput");
					field.removeClass("badInput");
				
				} else {
					field.addClass("badInput");
					field.removeClass("goodInput");
				}
			}
			
		</script>
		<div data-role="content" class="ui-body ui-body-a">
			<h2>Create New Account</h2>
			<table class="center">
				<tr>
					<td class="alignRight">Username:</td>
					<td><input type="text" name="nCreateUsernameTextField" id="createUsernameTextField" value=""  /></td>
				</tr>
				<tr>
					<td class="alignRight">Password:</td>
					<td><input type="password" name="nCreatePwTextField" id="createPwTextField" value=""  /></td>
				</tr>
				<tr>
					<td class="alignRight">Again:</td>
					<td><input type="password" name="nCreatePwAgainTextField" id="createPwAgainTextField" value=""  /></td>
				</tr>
			</table>	
			<div class="centerHalf">
				<input type="button" id="createAcctBtn" value="Create Account"/>
			</div>
		</div>
		
	</div>
</body>
</html>