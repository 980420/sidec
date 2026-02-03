function changeCapituloFields() {
	if(document.getElementById("evolucionInsert:capInput").value.length == 0){
		document.getElementById("evolucionInsert:grupInput").value = '';
		document.getElementById("evolucionInsert:cateInput").value = '';
		document.getElementById("evolucionInsert:subcatInput").value = '';
		clearFields();
	}
}
function changeGrupoFields() {
	if(document.getElementById("evolucionInsert:grupInput").value.length == 0){
		document.getElementById("evolucionInsert:cateInput").value = '';
		document.getElementById("evolucionInsert:subcatInput").value = '';
		clearFields();
	}
}
function changeCategoriaFields() {
	if(document.getElementById("evolucionInsert:cateInput").value.length == 0){
		document.getElementById("evolucionInsert:subcatInput").value = '';
		clearFields();
	}
}

//-----------------------------------------------------------

var curDt = new Date();
var calDt='';

function disablementFunction(day){
	if (curDt.getTime() - day.date.getTime() > 0) return true; else return false;  
}

function disabledClassesProv(day){
	if (curDt.getTime() - day.date.getTime() < 0)
		return 'rich-calendar-boundary-dates';
	else 
		return '';
}

function previousDate(date){
	calDt = new Date(Date.parse(date));
}

function previousDateDB(date){
	fech = date.toString().replace("-","/");
	fech = fech.replace("-","/");
	calDt = new Date(Date.parse(fech));
}

function disablementFunctionPrevious(day){
	if ((curDt.getTime() - day.date.getTime() > 0) && (calDt.getTime() - day.date.getTime() < 0)){
		fech= calDt.getTime() - day.date.getTime();
		return true;
	}
	else 
		return false; 
}

function disabledClassesProvPrevious(day){
	if ((curDt.getTime() - day.date.getTime() < 0) || (calDt.getTime() - day.date.getTime() >= 0))
		return 'rich-calendar-boundary-dates';
	else 
		return '';
}		