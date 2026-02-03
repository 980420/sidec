function MostrarOrganoSistemaLesionado(parteTroco){
	if(parteTroco == 'torax'){
		window.document.getElementById('panelTorax').style.display = "inline";
		window.document.getElementById('botonTorax').style.backgroundColor = "#FFCC33";
		window.document.getElementById('botonAbdomen').style.backgroundColor = "#D0EBF7";
		window.document.getElementById('panelAbdomen').style.display = "none";
	}
	else if(parteTroco == 'abdomen'){
		window.document.getElementById('panelTorax').style.display = "none";		
		window.document.getElementById('botonAbdomen').style.backgroundColor = "#FFCC33";
		window.document.getElementById('botonTorax').style.backgroundColor = "#D0EBF7";
		window.document.getElementById('panelAbdomen').style.display = "inline";
	}
}

function MostrarVistaCuerpo(vista){
	if(vista == 'anterior'){		
			
		window.document.getElementById('panelPerfil').style.display = "none";
		window.document.getElementById('botonPerfil').style.backgroundColor = "#D0EBF7";
		window.document.getElementById('panelPosterior').style.display = "none";
		window.document.getElementById('botonPosterior').style.backgroundColor = "#D0EBF7";
		window.document.getElementById('panelCabezaGarganta').style.display = "none";
		window.document.getElementById('botonCabezaGarganta').style.backgroundColor = "#D0EBF7";
		
		window.document.getElementById('panelAnterior').style.display = "inline";
		window.document.getElementById('botonAterior').style.backgroundColor = "#FFCC33";
	}
	else if(vista == 'posterior'){
	    window.document.getElementById('panelAnterior').style.display = "none";
		window.document.getElementById('botonAnterior').style.backgroundColor = "#D0EBF7";		
		window.document.getElementById('panelPerfil').style.display = "none";
		window.document.getElementById('botonPerfil').style.backgroundColor = "#D0EBF7";
		
		window.document.getElementById('panelPosterior').style.display = "inline";
		window.document.getElementById('botonPosterior').style.backgroundColor = "#FFCC33";
		
		window.document.getElementById('panelCabezaGarganta').style.display = "none";
		window.document.getElementById('botonCabezaGarganta').style.backgroundColor = "#D0EBF7";
	}
	else if(vista == 'perfil'){
	    window.document.getElementById('panelAnterior').style.display = "none";	                   
		window.document.getElementById('botonAnterior').style.backgroundColor = "#D0EBF7";				
	
		window.document.getElementById('panelPerfil').style.display = "inline";				
		window.document.getElementById('botonPerfil').style.backgroundColor = "#FFCC33";
		
		window.document.getElementById('panelPosterior').style.display = "none";			
		window.document.getElementById('botonPosterior').style.backgroundColor = "#D0EBF7";	
		window.document.getElementById('panelCabezaGarganta').style.display = "none";		
		window.document.getElementById('botonCabezaGarganta').style.backgroundColor = "#D0EBF7";
	}
	else if(vista == 'cabeza'){
		window.document.getElementById('panelAnterior').style.display = "none";
		window.document.getElementById('botonAnterior').style.backgroundColor = "#D0EBF7";			
		window.document.getElementById('panelPerfil').style.display = "none";		
		window.document.getElementById('botonPerfil').style.backgroundColor = "#D0EBF7";		
		window.document.getElementById('panelPosterior').style.display = "none";		
		window.document.getElementById('botonPosterior').style.backgroundColor = "#D0EBF7";
		
		window.document.getElementById('panelCabezaGarganta').style.display = "inline";
		window.document.getElementById('botonCabezaGarganta').style.backgroundColor = "#FFCC33";
	}
}

function MostrarPanel(id){
	window.document.getElementById(id).style.display = 'inline';
}
function OcultarPanel(id){
	window.document.getElementById(id).style.display = 'none';
}
var textoGuardado = '';
var borrar = true;
function BorrarTexto(id){
    if(borrar && window.document.getElementById(id).value != ''){     
	 textoGuardado = window.document.getElementById(id).value;    
	 window.document.getElementById(id).value = '';
	}
}
function ComprobarTexto(id){ 
   if (window.document.getElementById(id).value == '') {
   	 window.document.getElementById(id).value = textoGuardado;
	 borrar = true;
   }
}
function NoBorrar(){  
  borrar = false;
}


function ExpandirRecogerPanel(idPanel, idImgM, idImgE){
	if ( window.document.getElementById(idPanel).style.display == "none"){	
	 window.document.getElementById(idPanel).style.display = "inline";	
	 window.document.getElementById(idImgM).src = "recojer.png";
	}
	else{
	 window.document.getElementById(idPanel).style.display = "none";
	 window.document.getElementById(idImgE).style.display  = "recojer2.png";
	}
	
}
function Probando(id){
    alert(window.screen.height - 500);
	window.document.getElementById(id).style.top = window.screen.height - 500;
}

function CambiarColor(nuevoColor){
    alert('1')
	if(nuevoColor == 'rojo'){
	
	    window.document.getElementsByName('recibirPaciente:amarillo');		
		
	    alert('2');
	  	window.document.getElementsByName('recibirPaciente:amarillo').checked =false;
	  	alert('3');		
	}	 
}

function SetVisible(){
            document.getElementById('accionesRe').style.display='none';
            document.getElementById('accionesRe2').style.display='';
       }
function SetInvisible(){
            document.getElementById('accionesRe2').style.display='none';
            document.getElementById('accionesRe').style.display='';
       }

//No acepta numeros
function validarLetras(e) { 
    tecla = (document.all) ? e.keyCode : e.which;
    if (tecla==8) return true;
    patron =/[A-Za-z\s]/;
    te = String.fromCharCode(tecla);
    return patron.test(te);
} 


//Solo acepta numeros
function validarNumeros(e) { 
    tecla = (document.all) ? e.keyCode : e.which;
    if (tecla==8) return true;
    patron =/\d/;
    te = String.fromCharCode(tecla);
    return patron.test(te);
} 

