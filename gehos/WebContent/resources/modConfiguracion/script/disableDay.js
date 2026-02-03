var calDt=null;
var curDt = new Date();
function previousDate(date){
	calDt = date;
}
function disablementFunctionPreviousEx(day){
	if (curDt.getTime() - day.date.getTime() >= 86400000){
		return false;
	}
	if (day.date.getTime() - curDt.getTime() > 604800000){
		return false;
	}
	if(calDt == null){
		return true;
	}
	if (calDt.getTime() - day.date.getTime() <= 0){
		return true;
	}
	else 
		return false;
  }

function disabledClassesProvPreviousEx(day){
	if (curDt.getTime() - day.date.getTime() >= 86400000)
		return 'noAvailable';
	if (day.date.getTime() - curDt.getTime() > 604800000)
		return 'noAvailable';
	if(calDt == null){ 
		return '';
	}
	if (calDt.getTime() - day.date.getTime() <= 0)
		return '';
	else 
		return 'noAvailable';
}
function disablementFunctionPreviousEx1(day){
	if (curDt.getTime() - day.date.getTime() >= 86400000){
		return false;
	}
	if (day.date.getTime() - curDt.getTime() > 604800000){
		return false;
	}
	if(calDt == null){
		return true;
	}
	if (calDt.getTime() - day.date.getTime() >= 0){
		return true;
	}
	else 
		return false;
  }

function disabledClassesProvPreviousEx1(day){
	if (curDt.getTime() - day.date.getTime() >= 86400000)
		return 'noAvailable';
	if (day.date.getTime() - curDt.getTime() > 604800000)
		return 'noAvailable';
	if(calDt == null){ 
		return '';
	}
	if (calDt.getTime() - day.date.getTime() >= 0)
		return '';
	else 
		return 'noAvailable';
}