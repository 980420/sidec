var canvas, c, canvastemp, ctemp, canvassel, csel, canvasundo, cundo, wsp, imgd, co, check;
var iface = { dragging:false, resizing:false, status:null, xy:null, txy:null }
var prefs = { pretty:false, controlpoints:false }
var img1 = new Image();
var img2 = new Image();
//var img3 = new Image();
img2.src="/resources/modHospitalizacion/images/partesCuerpoImages/Untitled-7.jpg";
//img3.src="http://"+location.host+"/yadiel-2/img/ico1.png";
var oCanvas2;
var direccion_destino="http://"+location.host+"/tesis/SASA_converter/convertfx.php"
var Imagen_a_pintar =new Image();
	Imagen_a_pintar.src = "";
    
 var dash=new Image();
   dash.src="/images/dashed2.gif";
var comprobar=0;
var comprobar2=0;
var Automatic_name;
var current_zoom_level =0;
var paramq=0;
var xx=200;
var yy=200;
var anglev=0;
var estados=new Array();
var letras=new Array();
var debug=true;
var coords;



var Numeros_de_estados=new Array();


