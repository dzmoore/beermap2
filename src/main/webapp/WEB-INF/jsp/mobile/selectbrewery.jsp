<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Mobile Web - Search</title>
	<meta name="viewport" content="width=device-width, initial-scale=1"> 
	<link rel="stylesheet" href="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.css" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/mobile.css"/>" />
	<script src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
	<script src="http://code.jquery.com/mobile/1.1.0/jquery.mobile-1.1.0.min.js"></script>
</head> 

<body> 


<div data-role="page" id="selectBreweryPage">
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
		
		jspArgs.set('goToHref')('<c:out value="${to}" />');
		jspArgs.set('paramName')('<c:out value="${paramNm}" />');
		
		$.mobile.pushStateEnabled=false;
		
		$('#selectBreweryPage').live('pageinit', function(page_e) {			
			
			$("#brewerySearchBtn").click(function(click_e) {
				click_e.preventDefault();
				
				$('#brewerySearchForm').submit();
			});
			
			$('#brewerySearchForm').submit(function() {
				doSearch();
				
				return false;
			});
			
			
		});
		
		function doSearch() {
			$('#brewerySearchResultsList').empty();
			$('#brewerySearchResultsList').listview('refresh');
				
			$.post("searchbrewery", 
				{
					q: $('#brewerySearchField').val()
				},
				function(data) {
					var results = $.parseJSON(data);
					$.each(results, function(i, eaBrewery) {
						$('#brewerySearchResultsList').append(
							'<li><a href="#" data-rel="back" id="' +
							eaBrewery.id + '">' 
							+ eaBrewery.name + "</a></li>"
						);
						
						$('#'+eaBrewery.id).click(function(click_e) {
							var newWindowHref = 
								jspArgs.get('goToHref') + '?' +
								jspArgs.get('paramName') + '=' +
								eaBrewery.id;
							
							window.location.href = newWindowHref;
							
							return false;
						});
					});
					
					$('#brewerySearchResultsList').listview('refresh');
				}
			);
		}
		
		
				
	</script>
	<div data-role="content" class="ui-body ui-body-a">
		<form id="brewerySearchForm">
			<div data-role="fieldcontain">
				<label for="brewerySearchField">Brewery Name:</label>
				<input type="search" name="search" id="brewerySearchField" value="" />	
				<input type="button" id="brewerySearchBtn" value="Search" /> 
			</div>
		</form>
		<ul data-role="listview" id="brewerySearchResultsList"/>
	</div><!-- /content -->

</div><!-- /page -->

</body>
</html>