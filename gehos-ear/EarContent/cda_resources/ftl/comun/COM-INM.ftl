<#macro Inmunizacion inmunizacion>
   <#if inmunizacion.inmunizaciones && inmunizacion.inmunizaciones?size !=0??>
    <component>
      <section>
        <title>Inmunizaciones</title>
        <text>
		    <table>
			    <tbody>
			      <tr>
			        <th colspan="10">Vacunas</th>
			      </tr>
			      <tr>
					<th></th>
			        <th>Dosis recién nacido</th>
			        <th>Primera dosis</th>
			        <th>Segunda dosis</th>
			        <th>Tercera dosis</th>
			        <th>Cuarta dosis</th>
			        <th>Primer refuerzo</th>
			        <th>Segundo refuerzo</th>
			        <th>Tercer refuerzo</th>
			        <th>Cuarto refuerzo</th>
			      </tr>
			      <#list inmunizacion.inmunizaciones as vacuna>
			      <tr>
			       
					<#if vacuna.nombre??>
					 <td bgcolor="#F4F4F4">${vacuna.nombre}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaRecienNacido??>
					 <td>${vacuna.fechaRecienNacido?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaDosis1??>
					 <td>${vacuna.fechaDosis1?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaDosis2??>
					 <td>${vacuna.fechaDosis2?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaDosis3??>
					 <td>${vacuna.fechaDosis3?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaDosis4??>
					 <td>${vacuna.fechaDosis4?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaRefuerzo1??>
					 <td>${vacuna.fechaRefuerzo1?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaRefuerzo2??>
					 <td>${vacuna.fechaRefuerzo2?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaRefuerzo3??>
					 <td>${vacuna.fechaRefuerzo3?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					<#if vacuna.fechaRefuerzo4??>
					 <td>${vacuna.fechaRefuerzo4?string("dd/MM/yyyy")}</td>	
					<#else>
					 <td bgcolor="#E3E4E4"></td>
					</#if>
					 </tr>
			      </#list>
		      </tbody>
	    	</table>
	    </text>
      </section>
    </component>
   </#if>
 </#macro>