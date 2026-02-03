function printTrigger() {
	var getMyFrame = document.getElementById('myIFrame');
	getMyFrame.contentWindow.focus();
	getMyFrame.contentWindow.print();
	/*var is_safari = navigator.userAgent.toLowerCase().indexOf('safari/') > -1;  
	if (is_safari )
	*/
	/*var is_chrome= navigator.userAgent.toLowerCase().indexOf('chrome/') > -1;  
	if (is_chrome){
		var clone=document.documentElement.cloneNode(true)
		var win=window.open('about:blank');
		win.document.replaceChild(clone, getMyFrame);
		win.print();
	}
	
	var is_firefox = navigator.userAgent.toLowerCase().indexOf('firefox/') > -1;  
	if (is_firefox )
		getMyFrame.contentWindow.print();

	if ('\v'=='v'){
		var ifWin = getMyFrame.contentWindow || getMyFrame;
		ifWin.focus();
		ifWin.printPage();	
	}*/
}