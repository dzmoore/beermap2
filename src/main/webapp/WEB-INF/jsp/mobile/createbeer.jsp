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
	<div data-role="page" id="createBeerPage">	
		<script>
			$.mobile.pushStateEnabled=false;
			$('#createBeerPage').live('pageinit', function(page_e) {
					
			});
			
		</script>
		<div data-role="content" class="ui-body ui-body-a">
			<h2>Create New Beer</h2>
			<div class="ui-grid-a">
				<div class="ui-block-a"><div class="ui-body" >
					<p><strong>Brewery: </strong></p>
				</div></div>
				<div class="ui-block-b"><div class="ui-body ui-body-a" >
					<div class="ui-grid-a">
						<div class="ui-block-a"><div>
 							<p id="createBeerBreweryTxtField">Select a Brewery</p>
						</div></div>
						<div class="ui-block-b"><div>
 							<input type="button" id="createBeerBrewerySrchBtn" value="Select Brewery"/>
 						</div></div>
					</div>
				</div></div>
				
				
				<div class="ui-block-a"><div class="ui-body" >
					<p><strong>ABV: </strong></p>
				</div></div>
				<div class="ui-block-b"><div class="ui-body ui-body-a" >
					<input type="text" name="nCreateBeerAbvTextField" id="createBeerAbvTextField" value=""  />
				</div></div>
				
				<div class="ui-block-a"><div class="ui-body" >
					<p><strong>Description: </strong></p>
				</div></div>
				<div class="ui-block-b"><div class="ui-body ui-body-a" >
					<textarea name="nCreateBeerDescTextArea" id="createBeerDescTextArea"></textarea>
				</div></div>
				
			</div>

			<input type="button" id="createBeerCreateBeerBtn" value="Create Beer"/>
		</div>
		
	</div>
</body>
</html>