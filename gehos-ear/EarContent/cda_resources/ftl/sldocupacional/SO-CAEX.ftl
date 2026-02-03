<#if (documento.sustanciaExposicion?? && documento.sustanciaExposicion?size !=0) || (documento.cuestionarioAntExposicion?? && documento.cuestionarioAntExposicion?size !=0)>
<component>
 <section>
  <title>Antecedentes de exposici&#243;n</title>
   <#if documento.sustanciaExposicion?? && documento.sustanciaExposicion?size !=0>
    <table>
					<tbody>
						<tr>
							<th colspan="2">
								Exposici&#243;n a sustancias, elementos o condiciones
							</th>
						</tr>
						<tr>
							<th>Sustancia</th>
							<th>Descripci&#243;n</th>
							
						</tr>
						<#list documento.sustanciaExposicion as sustancia>
							<tr>
							    <#if sustancia.sustancia??>
								<td>${sustancia.sustancia}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if sustancia.descripcion??>
								<td>${sustancia.descripcion}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								
							</tr>
						</#list>
					</tbody>
				</table>
   <#else>
    <text>
	  No existe informaci&#243;n a mostrar.
	</text>
   </#if>
   <!--Aqui comienza el cuestionario de antecedentes de exposicion-->
   <#if documento.cuestionarioAntExposicion?? && documento.cuestionarioAntExposicion?size !=0> 
     <#list documento.cuestionarioAntExposicion as cuestionario>
      <component>
       <section>
       
       <title><#if cuestionario.pregunta??>${cuestionario.pregunta}<#else>-</#if></title>
       <table>
        <tbody>
         <tr>
          <td>Especificaci&#243;n</td>
          <td>
          <#if cuestionario.especificacion??>${cuestionario.especificacion}
          <#else>
          -
          </#if>
          </td>
          
         </tr>
         <#if cuestionario.adjuntos?? && cuestionario.adjuntos?size !=0>
          <#list cuestionario.adjuntos as adjunto>
           <tr>
            Adjunto(s)
           </tr>
           <tr>
            <td><#if adjunto.nombreAdjunto??>${adjunto.nombreAdjunto}
            <#else>
            -
            </#if>
            </td>
           </tr>
          </#list>
         <#else>
         </#if>
        </tbody>
       </table>
      </section>
     </component>
    </#list>
    
  </#if>
 </section>
</component>
</#if>