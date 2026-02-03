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