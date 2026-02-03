<#if documento.examenFisCuestionario?? && documento.examenFisCuestionario?size !=0>
<component>
 <section>
  <title>Examen físico</title>
  <#list documento.examenFisCuestionario as examenFisico>
  <component>
   <section>
     <title><#if examenFisico.pregunta??>${examenFisico.pregunta}<#else>-</#if></title>
	  <#if examenFisico.tienePalabra == true>
	   <table>
	    <tbody>
	     <tr>
	      <td>Estado</td>
	      <td><#if examenFisico.palabra??>${examenFisico.palabra}<#else>-</#if></td>
	     </tr>
	    
	    </tbody>
	   </table>
	  </#if> 
	   <#if examenFisico.especificacion??>
		<text>
			<obs>
				<h1>
					Especificación:
				</h1>
				<p>
					<#if examenFisico.especificacion?? && examenFisico.especificacion!="">
						${examenFisico.especificacion}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>										
		
	
	  </#if> 
	 </section>
	</component>   
  </#list>
 </section>
</component>
</#if>