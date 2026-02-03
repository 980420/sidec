var timer;
var delay;
var cond=true;

function f()
{
	clearInterval(timer);
	clearTimeout(delay);
	if(cond)
     timer=setInterval("crecer()",2);
	else
	  timer=setInterval("decrecer()",2);
	cond=!cond;
}
function crecer()
{
	
	 var el=document.getElementById("messages");
	 el.style.display='';
	 var val=new String();
	 val=el.style.height;
	 val=val.replace("px","");
	 var valor=new Number(val);
	  valor+=3;
	  
	 el.style.height=(valor+"px");
	if(valor>=110)
     {
	   clearInterval(timer);
	   if(!arriba)
	     delay=setTimeout(f,3000);
	 }
	
	  
	 
	 
}
function decrecer()
{
	 var el=document.getElementById("messages");
	 var val=new String();
	 val=el.style.height;
	 val=val.replace("px","");
	  var valor=new Number(val);
	 valor-=1;
	 el.style.height=(valor+"px");
	if(valor<=1)
	{
	 clearInterval(timer);
	 el.style.display='none';
	}
	
	 
}
var closeImage=false;
var arriba=false;
function onMouseOver()
{
	arriba=true;
	if(cond)
	{
		clearInterval(timer);
		timer=setInterval(f,2);
	}
	
	clearTimeout(delay);
}
function onMouseOut()
{
	arriba=false;
	 if(!closeImage)
	 {
	  delay=setTimeout(f,3000);
	 }
	 else
	  closeImage=!closeImage;
}

function closeMessages()
{
	clearTimeout(delay);
	clearInterval(timer);
	cond=true;
	closeImage=true;
	var el=document.getElementById("messages");
	el.style.display='none';	
	el.style.height="1px";
}