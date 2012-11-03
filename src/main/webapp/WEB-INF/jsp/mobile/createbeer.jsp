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
	<script src="<c:url value="/resources/scripts/jquery.cookie.js"/>"></script>
	<title>${result}</title>
</head>
<body>
	<div data-role="page" id="createBeerPage">	
		<script>
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
			
			jspArgs.set('name')('<c:out value="${bnm}" />');
			jspArgs.set('abv')('<c:out value="${babv}" />');
			jspArgs.set('desc')('<c:out value="${bdesc}" />');
			jspArgs.set('brewery')('<c:out value="${brid}" />');
			
			$.mobile.pushStateEnabled=false;
			$('#createBeerPage').live('pageinit', function(page_e) {
				$.getScript('<c:url value="/resources/scripts/nav.js"/>', function() {
					initNav(
						$('#createHeaderLoginBtn'), 
						$('#createHeaderLogoutBtn'),
						$('#createHeaderCrtAcctBtn'), 
						'<c:out value="${li}" />'
					);
				});
				
				
					doInit();
			});
			
			function doInit() {
				if (jspArgs.get('name') !== null) {
					$('#nameTextField').val(jspArgs.get('name'));
				}
				
				if (jspArgs.get('abv') !== null) {
					$('#abvTextField').val(jspArgs.get('abv'));
				}
				
				if (jspArgs.get('desc') !== null) {
					$('#descTextArea').val(jspArgs.get('desc'));
				}
				
				if (jspArgs.get('brewery') !== null && jspArgs.get('brewery') > 0) {
					var breweryId = jspArgs.get('brewery');
					$.ajax({
						type: 'POST',
						url: 'findbrewerybyid',
						data: {'id': breweryId},
						dataType: 'json',
						success: function(data) {
							$('#breweryPrgh').text(data.name);
						},
						error: function(jqXHR, textStatus, errorThrown) {
							alert("Error occurred:" + errorThrown);
						}
					});
				}
				
				$('#createBeerBrewerySrchBtn').click(function(click_e) {
					var encodedName = encodeURIComponent($('#nameTextField').val());
					var encodedAbv = encodeURIComponent($('#abvTextField').val());
					var encodedDesc = encodeURIComponent($('#descTextArea').val());
					
					var gotoHref = 'selectbrewery?' +
							'bnm=' + encodedName + '&' +
							'babv=' + encodedAbv + '&' +
							'bdesc=' + encodedDesc + '&' +
							'to=createbeer&paramNm=brid';
					
					window.location.href = gotoHref;
					
					return false;
				});
			}
		</script>
		
		<div data-role="header" id="createPageNavHeader">
			<a id="createHeaderLoginBtn" href="logindialog?to=main" data-icon="home" class="ui-btn-left" data-rel="dialog">Login</a>
			<h1>Create Beer</h1>
			<a id="createHeaderCrtAcctBtn" href="createacct" data-icon="plus" class="ui-btn-right" rel="external">Create Account</a>
			<a id="createHeaderLogoutBtn" href="#" data-icon="minus" class="ui-btn-right">Logout</a>
		</div>
		<div data-role="content" class="ui-body ui-body-a">
			<table class="center">
				<tr>
					<td class="alignRight alignTop"><b>Name:</b></td>
					<td colspan="2"><input type="text" name="nNameTextField" id="nameTextField" value=""  /></td>
				</tr>
				<tr>
					<td class="alignRight"><b>Brewery:</b></td>
					<td><p id='breweryPrgh'>No Brewery Selected</p></td>
					<td><a href="selectbrewery" id="createBeerBrewerySrchBtn" data-role="button">Select Brewery<a/></td>
				</tr>
				<tr>
					<td class="alignRight alignTop"><b>ABV:</b></td>
					<td colspan="2"><input type="text" name="nAbvTextField" id="abvTextField" value=""  /></td>
				</tr>
				<tr>
					<td class="alignRight alignTop"><b>Description:</b></td>
					<td colspan="2"><textarea name="nDescTextArea" id="descTextArea"></textarea></td>
				</tr>			
			</table>

			<input type="button" id="createBeerCreateBeerBtn" value="Create Beer"/>
		</div>
		
	</div>
</body>
</html>