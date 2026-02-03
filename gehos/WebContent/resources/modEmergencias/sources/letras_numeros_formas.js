var angle=0;
var fin;
var c;
var nombre_F;


var numeros_letras_formas=new Array();
 numeros_letras_formas[1]=new Image();
 numeros_letras_formas[1].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico1.png";
 numeros_letras_formas[2]=new Image();
 numeros_letras_formas[2].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico2.png";
 numeros_letras_formas[3]=new Image();
 numeros_letras_formas[3].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico3.png";
 numeros_letras_formas[4]=new Image();
 numeros_letras_formas[4].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico4.png";
 numeros_letras_formas[5]=new Image();
 numeros_letras_formas[5].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico5.png";
 numeros_letras_formas[6]=new Image();
 numeros_letras_formas[6].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico6.png";
 numeros_letras_formas[7]=new Image();
 numeros_letras_formas[7].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico7.png";
 numeros_letras_formas[8]=new Image();
 numeros_letras_formas[8].src="/gehos/resources/modHospitalizacion/images/partesCuerpoImages/ico8.png";


     /*   for(var i=1;i<28;i++)
        {
            numeros_letras_formas[i]=new Image();
            numeros_letras_formas[i].src="images/Letras/Letra_"+i+".png"
        }

          for(var i=28;i<51;i++)
        {
            var resta=i-27;
            numeros_letras_formas[i]=new Image();
            numeros_letras_formas[i].src="images/Numeros/"+resta+".png"
        }
          for(var i=51;i<56;i++)
        {
            var resta=i-50;
            numeros_letras_formas[i]=new Image();
            numeros_letras_formas[i].src="images/Formas/Forma_"+resta+".png"
        }  */

          

function insertar_letra_numero_forma(nombre_figura)
{ nombre_F=nombre_figura;
 Zoom_Image(3,numeros_letras_formas[nombre_figura])
 Imagen_a_pintar=numeros_letras_formas[nombre_figura];
 // document.getElementById("zoom_circulo").style.left="12px";
document.getElementById("layers").style.cursor="move";
document.getElementById("idForm:aplicar").style.visibility="visible";

}

function rotar_letra_numero_forma_derecha(evt)
{


   if(fin==true)
    {
       document.getElementById("status").innerHTML=fin;
    fin=false;
       return false;
      }
    //  document.getElementById("rotar_derecha").addEventListener("onmouseout",cancelar_rotar_letra_numero_forma_derecha,false);
    rotate_canvas(angle+=0.7);
    setTimeout("rotar_letra_numero_forma_derecha()",50);
}



function rotar_letra_numero_forma_izquierda(evt)
{


   if(fin==true)
    {
       document.getElementById("status").innerHTML=fin;
    fin=false;
       return false;
      }
     // document.getElementById("rotar_derecha").addEventListener("onmouseout",cancelar_rotar_letra_numero_forma_derecha,false);


    rotate_canvas(angle-=0.7);
    setTimeout("rotar_letra_numero_forma_izquierda()",50);
}

function cancelar_rotar_letra_numero_forma_derecha(evt)
{

document.getElementById("rotar_derecha").removeEventListener("onmousedown",rotar_letra_numero_forma_derecha,false);

 document.getElementById("status").innerHTML="cancelado derecha";
   fin=true;

}
function cancelar_rotar_letra_numero_forma_izquierda(evt)
{

document.getElementById("rotar_derecha").removeEventListener("onmouseout",cancelar_rotar_letra_numero_forma_derecha,false);

 document.getElementById("status").innerHTML=fin;
   fin=true;

}



function drag_layer(id)
{


  Imagen_a_pintar=estados[id];
     for(i=12;i>10;i--)
      Zoom_Image(i);

}

function drag_figure(e)
{
   document.getElementById("status").innerHTML=e.type  +"X" + ": "+e.clientX + "Y: "+e.clientY;
         document.getElementById(Automatic_name).onmousemove = drag_figure_move;
}

     var nume=0;

function drag_figure_move(evt)
{        document.getElementById(Automatic_name).style.cursor = "move";
         var elem = document.getElementById(Automatic_name).style.borderColor;
         var objectinit=document.getElementById("workspace");
         var coords= getxy(evt,objectinit)
         Move_horizontal(coords.x+document.body.scrollLeft+40);
         document.getElementById(Automatic_name).addEventListener("mousemove", drag_figure,false);
         Move_vertical(coords.y+document.body.scrollTop+40);
         document.getElementById(Automatic_name).addEventListener("mousemove", drag_figure,false);
         document.getElementById("status").innerHTML=evt.type  +"X" + ": "+coords.x + "Y: "+coords.y+"zomm2: "+current_zoom_level+" css: "+elem;
}

function up_figure_move(e)
{
         document.getElementById(Automatic_name).onmousemove = "";
         document.getElementById(Automatic_name).removeEventListener("mousemove", drag_figure,false);
         if(debug)
          document.getElementById("status").innerHTML=e.type  +"X" + ": "+e.clientX + "Y: "+e.clientY;
}

function Zoom_Image(zoom_level,image)
{           Imagen_a_pintar=image;
			Automatic_name=Math.random();
		  	document.getElementById("layers").innerHTML= Dynamic_element();
			var contex=document.getElementById(Automatic_name).getContext("2d");

			current_zoom_level=zoom_level;
			rotate_canvas(anglev);
}


	/* Une capas */
function Move_to_canvas()
{           //document.getElementById("td_paint").style.cursor="";
document.getElementById("workspace").style.cursor="default";
              //   document.getElementById("zoom_circulo").style.left="12px";

                          if(Imagen_a_pintar.src!="") {
		     	var cannumero=document.getElementById("canvas").getContext("2d");
		    	cannumero.save();
                  	if(anglev==0)
					 {cannumero.drawImage(Imagen_a_pintar,xx-55,yy-55, current_zoom_level*8 , current_zoom_level*8);
					 }

			     	else
				    	rotate_canvas_save(anglev,cannumero)

            cannumero.restore();
			document.getElementById("layers").innerHTML="";
            }

             anglev=0;

}

	/* Mueve las capas horizontalmente */
function Move_horizontal(X_position)
{
 if(Imagen_a_pintar.src!="") {
			xx=X_position;
            document.getElementById("layers").innerHTML=Dynamic_element() ;
			var contex=document.getElementById(Automatic_name).getContext("2d");

			rotate_canvas(anglev); }
}

	/* Mueve las capas verticalmente */
function Move_vertical(Y_position)
{       if(Imagen_a_pintar.src!="") {
		yy=Y_position;

		document.getElementById("layers").innerHTML=Dynamic_element() ;
		var contex=document.getElementById(Automatic_name).getContext("2d");

		rotate_canvas(anglev); }
}

function Dynamic_element()
{      	var canvasdinamic="<canvas id="+ Automatic_name +" onmouseup='javascript:up_figure_move(event)' onmousedown='javascript:drag_figure(event)' style='position:absolute;z-index: 10;border-width:0.3mm' width="+"241px"+" height="+"402px"+" ></canvas>"
        return canvasdinamic;
}

function Dynamic_erase()
{
        document.getElementById("layers").innerHTML="";
       // document.getElementById("td_paint").style.cursor="";
}

	/* Rota las autoformas y letras, los dibuja en su posición teniendo en cuenta  moviemientos anteriores */
function rotate_canvas(angle)
{
         if(Imagen_a_pintar) {
	    	document.getElementById("layers").innerHTML=Dynamic_element() ;
	       var contex=document.getElementById(Automatic_name).getContext("2d");
		   contex.save();
		   contex.translate(xx,yy);
	       contex.rotate(angle* Math.PI / 37.5);
           document.getElementById("status").innerHTML=fin;
           contex.drawImage(Imagen_a_pintar,-52,-52, current_zoom_level*8 , current_zoom_level*8);
           contex.restore();
	       anglev=angle;   }
}


	/* Rota las autoformas y letras, los dibuja en su posición teniendo en cuenta  moviemientos anteriores  */
function rotate_canvas_save(angle,contex)
{   if(Imagen_a_pintar.src!="") {
	    contex.translate(xx,yy);
		var current_position=-Imagen_a_pintar.width;
		contex.rotate(angle* Math.PI /  37.5);
         // alert("rotatesave");
		contex.drawImage(Imagen_a_pintar,-52,-52, current_zoom_level*8 , current_zoom_level*8);
		// contex.drawImage(estados[24],-62,-42, current_zoom_level*9 , current_zoom_level*9);
		anglev=angle; }
}