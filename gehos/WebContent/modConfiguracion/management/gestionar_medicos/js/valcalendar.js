function hoyAyer(day) {

	var hoy = new Date();
	resetTime(hoy);

	var ayer = new Date();
	ayer.setDate(ayer.getDate() - 1);
	resetTime(ayer);

	if ((hoy.getTime() == day.date.getTime())
			|| (ayer.getTime() == day.date.getTime()))
		return true;
	else
		return false;
}
function hoyAyerClass(day) {
	var hoy = new Date();
	resetTime(hoy);

	var ayer = new Date();
	ayer.setDate(ayer.getDate() - 1);
	resetTime(ayer);

	if ((hoy.getTime() == day.date.getTime())
			|| (ayer.getTime() == day.date.getTime()))
		return '';
	else
		return 'noAvailableDay';// 'rich-calendar-boundary-dates';
}
function resetTime(fecha) {
	fecha.setHours(0);
	fecha.setMinutes(0);
	fecha.setSeconds(0);
	fecha.setMilliseconds(0);
}

function disableMayoresHoy(day) {
	var curDt = new Date();
	resetTime(curDt);	
	if (day == curDt)
		return false;
	if (curDt.getTime() - day.date.getTime() > 0)
		return true;
	else
		return false;
}
function disableMayoresHoyClass(day) {
	var curDt = new Date();
	resetTime(curDt);
	if (day == curDt)
		return 'noAvailableDay';
	if (curDt.getTime() - day.date.getTime() > 0)
		return '';
	else
		return 'noAvailableDay';// 'rich-calendar-boundary-dates';
}
