
var abierto;
function Plegar()
{
 if(abierto)
 {
  
   document.getElementById('menuPle').style.display='none';
   document.getElementById('cuerpo').style.display='none';
   document.getElementById('footer').style.display='none';
   abierto=false;
   var im=document.getElementById('im');
   im.setAttribute('src', document.getElementById('hidden_root').value + 'images/expandir.gif');
   im.style.display="inline";
   im.style.cursor="pointer";
   im.onclick=expandOrCollapseMenuClient;
 }
 else
 {
  
   document.getElementById('menuPle').setAttribute("width","200px");
   document.getElementById('cuerpo').setAttribute("width","198px");
   document.getElementById('footer').setAttribute("width","198px");
   
   
   document.getElementById('menuPle').style.display='';
   document.getElementById('cuerpo').style.display='';
   document.getElementById('footer').style.display='';
   
    var im=document.getElementById('im');
	im.setAttribute('src',document.getElementById('hidden_root').value + 'images/cont.gif');
	im.style.display="none";
	im.onclick=expandOrCollapseMenuClient;
	abierto=true;
 }
   
}
function changeImage(cond,img)
{
  if(cond)
    {
     img.setAttribute('src',document.getElementById('hidden_root').value + 'images/reducir2.png');
    }
  else
  {
     img.setAttribute('src',document.getElementById('hidden_root').value + 'images/reducir.png');
  }
}

