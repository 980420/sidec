	//Para deshabilitar en el calendario los dias mayores que hoy de rafa------------------------------------
	
	function disableMayoresHoy(day){	
		var curDt = new Date();
		if (curDt.getTime() - day.date.getTime() > 0) 
			return true; 
		else 
			return false;
	}
	function disableMayoresHoyClass(day){
		var curDt = new Date();
	   	if (curDt.getTime() - day.date.getTime() > 0) 
	   		return '';
	   	else 	   		
	   		return 'noAvailableDay';//'rich-calendar-boundary-dates';
	 }
	
	//------------------------------------------------------------------------------------------------
	
	//Para deshabilitar en el calendario los dias menores que hoy ------------------------------------
	
	function menoresHoy(day){	
		var curDt = new Date();
		resetTime(curDt);
		if (day.date.getTime() - curDt.getTime() >= 0) 
			return true; 
		else 
			return false;
	}
	function menoresHoyClass(day){
		var curDt = new Date();
		resetTime(curDt);
	   	if (day.date.getTime() - curDt.getTime() >= 0) 
	   		return '';
	   	else 	   		
	   		return 'noAvailableDay';//'rich-calendar-boundary-dates';
	 }
	
	//------------------------------------------------------------------------------------------------
	/*function previousDate(date){
		   var ano=date.substring(6,10);
		   var mes=date.substring(3,5);
		   mes--;
		   var dia=date.substring(0,2);
		   calDt=new Date(ano,mes,dia);
		  //calDt = new Date(Date.parse(date));	   	  
	}*/
	//Para mostrar procesando en el crear HC-----------------------------------------------------------
	function mostrarControles(){
		document.getElementById("procesando").style.display="none";
		document.getElementById("controles").style.display="block";
	}
	function mostrarProcesando(){		
		document.getElementById("controles").style.display="none";
		document.getElementById("procesando").style.display="block";
		return true;
	}
	//--------------------------------------------------------------------------------------------------
	
	//Para habilitar el dia de hoy y el de ayer en la pag realizar admision-----------------------------
	function hoyAyer(day){
		
		var hoy = new Date();
		resetTime(hoy);
		
		var ayer = new Date();
		ayer.setDate(ayer.getDate()-1);
		resetTime(ayer);
		 	
		if ((hoy.getTime() == day.date.getTime()) || (ayer.getTime() == day.date.getTime())) 
			return true; 
		else 
			return false;
	}
	function hoyAyerClass(day){
		var hoy = new Date();
		resetTime(hoy);
		
		var ayer = new Date();
		ayer.setDate(ayer.getDate()-1);
		resetTime(ayer);
		
	   	if ((hoy.getTime() == day.date.getTime()) || (ayer.getTime() == day.date.getTime())) 
	   		return '';
	   	else 	   		
	   		return 'noAvailableDay';//'rich-calendar-boundary-dates';
	 }
	 function resetTime(fecha){
		 fecha.setHours(0);
		 fecha.setMinutes(0);
		 fecha.setSeconds(0); 
		 fecha.setMilliseconds(0);
	 }
	 //---------------------------------------------------------------------------------------------
	 
	 //Para habilitar y deshabilitar los dias de dos calendarios cuando uno dependa de otro
	 
     var calDt=null;
    
     function previousDate(date){
     	calDt = date;
     }
    
     function disablementFunction(day){
    	 var curDt = new Date();
         if(calDt == null){
         	if (curDt.getTime() - day.date.getTime() > 0)
         		return true;
         	else
         		return false;
         }
     	else {
     		if (calDt.getTime() - day.date.getTime() >= 0 )
     			return true;
     		else
    			return false;
    	 }
     }
    
     function disabledClassesProv(day){
    	var curDt = new Date();
     	if(calDt == null){
     		if (curDt.getTime() - day.date.getTime() > 0)
     			return '';
     		else
     			return 'noAvailableDay';
     	}
     	else {
     		if (calDt.getTime() - day.date.getTime() >= 0)
     			return '';
     		else
     			return 'noAvailableDay';
     	}
     }
    
     function resetSecondDate(first,second) {
     	var firstDate = first.getSelectedDate().getTime();
     	var secondDate = second.getSelectedDate().getTime();
    
     	if(firstDate - secondDate > 0)
     		second.resetSelectedDate();
     }
    
     function disablementFunctionPreviousEx(day){
    	 var curDt = new Date();
     	if(calDt == null){
     		if (curDt.getTime() - day.date.getTime() > 0) return true; else return false;
     	}
    	if ((curDt.getTime() - day.date.getTime() > 0) && (calDt.getTime() - day.date.getTime() <= 0)){
     		return true;
     	}
     	else
     		return false;
     }
    
     function disabledClassesProvPreviousEx(day){
    	 var curDt = new Date();
     	if(calDt == null){
     		if (curDt.getTime() - day.date.getTime() < 0)
     			return 'noAvailableDay';
     		else
     			return '';
     	}
     	if ((curDt.getTime() - day.date.getTime() < 0) || (calDt.getTime() - day.date.getTime() > 0))
     		return 'noAvailableDay';
     	else
     		return '';
     }
	 
	 
	 //-------------------------------------------------------------------------------------------