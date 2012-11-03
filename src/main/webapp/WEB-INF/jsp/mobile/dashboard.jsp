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
	<title>Dashboard</title>
</head>
<body>
	<div data-role="page" id="dashboardPage">	
		<script>
			$.mobile.pushStateEnabled=false;
			$('#singleBeerPage').live('pageinit', function(page_e) {
								
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
			
				jspArgs.set('id')('<c:out value="${mb.id}" />');
				jspArgs.set('name')('<c:out value="${mb.name}" />');
				
				handleSaveEditCancel("cancel");
				
				$("#editBtn").click(function(click_e) {
					handleSaveEditCancel("edit");
					
					click_e.preventDefault();
				});
				
				$('#cancelBtn').click(function(click_e) {
					handleSaveEditCancel("cancel");
					click_e.preventDefault();
				});
			
				$('#saveBtn').click(function(click_e) {					
					var b = {
						mb: JSON.stringify({
							id: jspArgs.get('id'), 
							name: $('#nameTextField').val(),
							abv: $('#abvTextField').val(),
							descript: $('#descTextArea').val()
						})
					};
					
					$.post("beerupdate", b, function(data){
						var results = $.parseJSON(data);
						var success = results.result;
						
						if (success) {
							handleSaveEditCancel("save");
							
						} else {
							alert("An error occurred while attempting to update this beer.");
						}
					});
					
					click_e.preventDefault();
				});
			});
			
			function handleSaveEditCancel(type) {
				if (type === "edit") {
					$('#nameTextField').val($('#nameHeader').text());
					$('#nameRow').show();
				
					// desc
					$('#descPrgrph').hide();
					
					$('#descTextArea').val($('#descPrgrph').text());
					$('#descTextArea').show();
					
					// abv
					$('#abvPrgrph').hide();
					
					$('#abvTextField').val($('#abvPrgrph').text());
					$('#abvTextField').show();
					
					// edit buttons
					$('#editDiv').hide();
					$('#saveDiv').show();
					$('#cancelDiv').show();
				
				} else if (type === "save") {
					$('#nameHeader').text($('#nameTextField').val());
					$('#nameRow').hide();
				
					$('#descPrgrph').text($('#descTextArea').val());
				
					$('#descTextArea').hide();
					$('#descPrgrph').show();
					
					$('#abvPrgrph').text($('#abvTextField').val());
					$('#abvTextField').hide();
					$('#abvPrgrph').show();
					
					$('#editDiv').show();
					$('#saveDiv').hide();
					$('#cancelDiv').hide();
				
				} else if (type === "cancel") {
					$('#nameRow').hide();
				
					$('#descTextArea').hide();
					$('#descPrgrph').show();
					
					$('#abvTextField').hide();
					$('#abvPrgrph').show();
					
					$('#editDiv').show();
					$('#saveDiv').hide();
					$('#cancelDiv').hide();
				
				}
			}
			
		</script>
	<div data-role="content" class="ui-body ui-body-a">
		<h2 id="nameHeader">${mb.name}</h2>
		<table class="center">
			<tr id="nameRow">
				<td class="alignRight alignTop"><b>Name:</b></td>
				<td><input type="text" name="nNameTextField" id="nameTextField" value=""  /></td>
			</tr>
			<tr>
				<td class="alignRight alignTop"><b>Brewery:</b></td>
				<td>${mb.brewery.name}</td>
			</tr>
			<tr>
				<td class="alignRight alignTop"><b>ABV:</b></td>
				<td>
					<div id="abvPrgrph" class="alignTop">${mb.abv}</div>
					<input type="text" name="nAbvTextField" id="abvTextField" value=""  />
				</td>
			</tr>
			<tr>
				<td class="alignRight alignTop"><b>Description:</b></td>
				<td>
					<div id="descPrgrph" class="alignTop">${mb.descript}</div>
					<textarea name="nDescTextArea" id="descTextArea"></textarea>
				</td>
			</tr>
			
		</table>
		
		<table>
			<tr><td>
				<div id="saveDiv">
					<input type="button" id="saveBtn" value="Save"/>
				</div>
			</td><td>
				<div id="cancelDiv">
					<input type="button" id="cancelBtn" value="Cancel"/>
				</div>
			</td></tr>
		</table>
		
		<div id="editDiv" class="centerHalf">
			<input type="button" id="editBtn" value="Edit"/>
		</div>
	</div>
</body>
</html>