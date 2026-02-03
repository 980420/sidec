
//Validaciones para numeros y cadenas
jQuery(".entradaNumerica").keypress(function(e){return validaEntero(e);});
jQuery(".entradaCadena").keypress(function(e){return validaCadena(e);});
jQuery(".entradaCadenaConEspacio").keypress(function(e){return validaCadenaConEspacio(e);});
jQuery(".entradaCedula").keypress(function(e){return validaCedula(e);});

function validaEntero(e){
	var evt = e || window.event;
    var charCode = evt.which || evt.keyCode;
    if(charCode==13){						    	
	    return false;
	}
    var charStr = String.fromCharCode(charCode);
    if (isCharacterKeyPress(evt) && !(/\d/.test(charStr))) {
        return false;
    }
 }
  
function validaCadena(e){
	var evt = e || window.event;
	var charCode = evt.which || evt.keyCode;
	if(charCode==13){						    	
	    return false;
	}
	var charStr = String.fromCharCode(charCode);
	if (isCharacterKeyPress(evt) && !(/[A-Za-záéíóúÁÉÍÓÚñÑüÜ-]/.test(charStr))) {
	        return false;
	    }
}

function validaCadenaConEspacio(e){
	var evt = e || window.event;
	var charCode = evt.which || evt.keyCode;
	if(charCode==13){						    	
	    return false;
	}
	var charStr = String.fromCharCode(charCode);
	if (isCharacterKeyPress(evt) && !(/[A-Za-záéíóúÁÉÍÓÚñÑüÜ -]/.test(charStr))) {
	        return false;
	    }
}

function validaCedula(e){
	var evt = e || window.event;
	var charCode = evt.which || evt.keyCode;
	if(charCode==13){						    	
	    return false;
	}
	var charStr = String.fromCharCode(charCode);
	if (isCharacterKeyPress(evt) && !(/[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]/.test(charStr))) {
	        return false;
	    }
}

function isCharacterKeyPress(evt) {
	if (typeof evt.which == "undefined") {
		return true;
	} else if (typeof evt.which == "number" && evt.which > 0) {
        return !evt.ctrlKey && !evt.metaKey && !evt.altKey && evt.which != 8;
    }
    return false;
}
