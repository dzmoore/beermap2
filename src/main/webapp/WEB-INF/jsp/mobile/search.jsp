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


<div data-role="page" id="search-main">
	<script>
		$.mobile.pushStateEnabled=false;
		$('#search-main').live('pageinit', function(page_e) {
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
		
			jspArgs.set('searchQuery')('<c:out value="${q}" />');	
			
			if (jspArgs.get('searchQuery') !== null && jspArgs.get('searchQuery').length !== 0) {
				$('#the-search-field').val(jspArgs.get('searchQuery'));
				doSearch();
			}
			
			$("#the-search-btn").click(function(click_e) {
				click_e.preventDefault();
				
				$('#search-form').submit();
			});
			
			$('#search-form').submit(function() {
				doSearch();
				
				return false;
			});
			
			$.getScript('<c:url value="/resources/scripts/nav.js"/>', function() {
				initNav(
			 		$('#searchHeaderLoginBtn'), 
			 		$('#searchHeaderLogoutBtn'),
			 		$('#searchHeaderCrtAcctBtn'), 
			 		'<c:out value="${li}" />'
			 	);
			});
		});
		
		function doSearch() {
			$('#search-results-list').empty();
			$('#search-results-list').listview('refresh');
				
			$.post("beersearch", 
				{
					q: $('#the-search-field').val(), 
					_ids: "0"
				},
				function(data) {
					var results = $.parseJSON(data);
					$.each(results, function(i, eaBeer) {
						$('#search-results-list').append(
							'<li><a href="beer?id=' + 
							eaBeer.id + '" rel="external">' 
							+ eaBeer.name + "</a></li>"
						);
					});
					
					$('#search-results-list').listview('refresh');
				}
			);
		}
		
		
				
	</script>
	<div data-role="header" id="searchPageNavHeader">
		<a id="searchHeaderLoginBtn" href="logindialog?to=main" data-icon="home" class="ui-btn-left" data-rel="dialog">Login</a>
		<h1>Search</h1>
		<a id="searchHeaderCrtAcctBtn" href="createacct" data-icon="plus" class="ui-btn-right" rel="external">Create Account</a>
		<a id="searchHeaderLogoutBtn" href="#" data-icon="minus" class="ui-btn-right">Logout</a>
	</div>

	<div data-role="content" class="ui-body ui-body-a">
		<form id="search-form">
			<div data-role="fieldcontain">
				<label for="the-search-field">Search Input:</label>
				<input type="search" name="search" id="the-search-field" value="" />	
				<input type="button" id="the-search-btn" value="Search" /> 
			</div>
		</form>
		<ul data-role="listview" id="search-results-list"/>
	</div><!-- /content -->

</div><!-- /page -->

</body>
</html>