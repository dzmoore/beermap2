function fixQuotes(fixThisString) {
	var fixedString = "";
	if (fixThisString !== null) {
		fixedString = fixThisString.replace(/&#034;/g, "\"");
	}
	
	return fixedString;
}