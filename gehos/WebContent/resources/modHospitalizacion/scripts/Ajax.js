var url,id_contenedor, valor;
var pagina_requerida = false;

function createXMLHttpRequest() 
{
if (window.XMLHttpRequest)
    {
        // Si es Mozilla, Safari etc
        pagina_requerida = new XMLHttpRequest ();
    } else if (window.ActiveXObject)
    {
        // pero si es IE
        try
        {
            pagina_requerida = new ActiveXObject ("Msxml2.XMLHTTP");
        }
        catch (e)
        {
            // en caso que sea una versión antigua
            try
            {
                pagina_requerida = new ActiveXObject ("Microsoft.XMLHTTP");
            }
            catch (e)
            {
            }
        }
    }
    else
    return false;
    
}
function llamarasincrono (url, id_contenedor, valor)
{
    this.url=url;
    this.id_contenedor=id_contenedor;
    this.valor=valor;

    
    createXMLHttpRequest();
    pagina_requerida.onreadystatechange = function ()
    {
        // función de respuesta
        cargarpagina (pagina_requerida, id_contenedor);
    }
	
	
	
    pagina_requerida.open ('POST', url, true); 
    pagina_requerida.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
    pagina_requerida.send(valor);
    return;
	
	

}



function cargarpagina (pagina_requerida, id_contenedor)
{
	
	if (pagina_requerida.readyState==1) {
       document.getElementById (id_contenedor).innerHTML="<img src='images/saving.gif' style='margin-left:100px ; margin-top:160px'/> <br> <p align='center' style='color:#A2AAA1'>Guardando hierro....</p>";
         }
    
	
	if (pagina_requerida.readyState == 4 && (pagina_requerida.status == 200 || window.location.href.indexOf ("http") == - 1))
    //Cerrar Editor, imagen guardada satisfactoriamente.
	document.getElementById (id_contenedor).innerHTML = pagina_requerida.responseText;
	if (pagina_requerida.readyState == 4 && (pagina_requerida.status == 404 || window.location.href.indexOf ("http") == - 1))
		{
    	document.getElementById (id_contenedor).innerHTML="<img src='images/error.gif' style='margin-left:100px ; margin-top:160px'/> <br> <p align='center' style='color:#DE6565'>Servidor no encontrado...</p>";
        window.setTimeout("llamarasincrono (this.url, this.id_contenedor, this.valor)",5000)  ;
		}
}