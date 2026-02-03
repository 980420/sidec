<#if (documento.cambioWrapper??) || (documento.datosLabCuestionario?? && documento.datosLabCuestionario?size !=0)>
<component>
 <section>
  <title>Datos laborales complementarios </title>
   <#if documento.cambioWrapper?? && documento.cambioWrapper.cambios?? && documento.cambioWrapper.cambios?size !=0>
   <component>
    <section>
    <title>Listado de cambios</title>
    <table>
					<tbody>
						
						<tr>
							<th>Fecha</th>
							<th>Categoría</th>
							<th>Lugar del cambio</th>
							<th>Notificación</th>
							<th>Descripción de la notificación</th>
						</tr>
						<#list documento.cambioWrapper.cambios as cambio>
							<tr>
							    <#if cambio.fecha??>
								<td>${cambio.fecha}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if cambio.categoria??>
								<td>${cambio.categoria}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if cambio.lugarCambio??>
								<td>${cambio.lugarCambio}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if cambio.notificacion??>
								<td>${cambio.notificacion}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if cambio.descipcion??>
								<td>${cambio.descipcion}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
							</tr>
						</#list>
					</tbody>
				</table>
			<#if documento.cambioWrapper.adjunto??>
			 <table>
			  <tbody>
			   <tr>
			    <td>Adjunto</td>
			    <td>${documento.cambioWrapper.adjunto}</td>
			   </tr>
			  </tbody>
			 </table>
			 
			</#if>	
			</section>
		</component>
   
   </#if>
   <!--Aqui comienza el cuestionario de antecedentes de exposicion-->
   <#if documento.datosLabCuestionario?? && documento.datosLabCuestionario?size !=0> 
     <#list documento.datosLabCuestionario as cuestionario>
      <component>
       <section>
       
       <title><#if cuestionario.pregunta??>${cuestionario.pregunta}<#else>-</#if></title>
       <#if cuestionario.adjunto?? || cuestionario.fecha??>
	     <table>
	      <tbody>
	         <tr>
	          <td>Fecha</td>
	          <#if cuestionario.fecha??>
	          <td>${cuestionario.fecha}</td>
	          <#else>
	          <td bgcolor="#E3E4E4"></td>
	          </#if>
	          
	          <td>Adjunto</td>
	          <#if cuestionario.adjunto??>
	          <td>${cuestionario.adjunto}</td>
	          <#else>
	          <td bgcolor="#E3E4E4"></td>
	          </#if>
	         </tr>
	      </tbody>
	     </table>
	     <#if cuestionario.especificacion??>
	     <text>
			<obs>
				<h1>
					Especificación:
				</h1>
				<p>
					<#if cuestionario.especificacion?? && cuestionario.especificacion!="">
						${cuestionario.especificacion}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>	
	     
        </#if>
        </#if>
       <#if cuestionario.tieneRadio == false && cuestionario.especificacion??>
       <text>
			<obs>
				<h1>
					Especificación:
				</h1>
				<p>
					<#if cuestionario.especificacion?? && cuestionario.especificacion!="">
						${cuestionario.especificacion}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>	
        
        <#else>
        <table>
         <tbody>
          <tr>
          <#if cuestionario.palabra??>
           <td>Respuesta</td>
           <td>
            ${cuestionario.palabra}
           </td>
           <#else>
           <td bgcolor="#E3E4E4"></td>
           </#if>
          </tr>
         </tbody>
        </table>
        <#if cuestionario.especificacion??>
         <text>
			<obs>
				<h1>
					Especificación:
				</h1>
				<p>
					<#if cuestionario.especificacion?? && cuestionario.especificacion!="">
						${cuestionario.especificacion}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>	
        </#if>
       </#if>
       
       
       
      </section>
     </component>
    </#list>
    
  </#if>
 </section>
</component>
</#if>