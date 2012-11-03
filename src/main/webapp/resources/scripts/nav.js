navObj = new Object();

function initNav(loginBtn, logoutBtn, crtAcctBtn, loggedIn) {
	navObj.loginBtn = loginBtn;
	navObj.logoutBtn = logoutBtn;
	navObj.crtAcctBtn = crtAcctBtn;
	
	if (loggedIn === '1') {		
		navObj.isLoggedIn = true;
		loginBtn.hide();
		crtAcctBtn.hide();
		logoutBtn.show();
	
	} else {
		navObj.isLoggedIn = false;
		loginBtn.show();
		crtAcctBtn.show();
		logoutBtn.hide();
	}	
	
	initBtns();
}

function initBtns() {
	navObj.logoutBtn.click(function(click_e) {
		if (navObj.isLoggedIn) {
			$.post("logout", {}, function(data) {
				navObj.isLoggedIn = false;
				navObj.loginBtn.show();
				navObj.crtAcctBtn.show();
				navObj.logoutBtn.hide();
			});
			return false;
		}
		
		return true;
	});
}