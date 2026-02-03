	    	var cid;

			function check(id)
			{
				alert("cid"+id);
				cid=id;
  				var aux=readCookie();
  				alert(aux);
  				if(aux=="t")
  				{
  	  				expand();
  	  				}
  				else
  				{
  	  				collapse();
  	  				}
			
			}
	    	function saveCookie(val)
	    	{
	    		alert("cid"+cid);
	    		document.cookie=cid+"="+val;
		    }
		    function readCookie()
		    {
				var val=new String(document.cookie);
				alert("cookie:"+val);
				try{
				val=val.split(cid+"=")[1].split(";")[0];
				alert(val);
				}
				catch (e) {
					val="f";
					saveCookie(val);
					
				}
				alert("leer:"+val);
				return val;
			}
			function expand()
			{
				saveCookie("t");
				document.getElementById('buscarPacienteA').style.display = 'block';
				document.getElementById('buscarPacienteN').style.display = 'none';
			}
			function collapse()
			{
				saveCookie("f");
				document.getElementById('buscarPacienteA').style.display = 'none';
				document.getElementById('buscarPacienteN').style.display = 'block';
			}