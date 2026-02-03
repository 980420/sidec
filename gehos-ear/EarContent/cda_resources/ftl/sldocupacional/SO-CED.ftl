<#if documento.evaluacionDiscapacidad??>
<component>
 <section>
  <title>
   Evaluación de discapacidad
  </title>
   <#if documento.evaluacionDiscapacidad.cuestionarios?? && documento.evaluacionDiscapacidad.cuestionarios?size !=0>
    <#list documento.evaluacionDiscapacidad.cuestionarios as cuestionario>
     <component>
      <section>
       <title>
        <#if cuestionario.pregunta??>${cuestionario.pregunta}<#else>-</#if>
       </title>
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
       <table>
        <tbody>
         <tr>
          <#if cuestionario.fecha??>
           <td>Fecha</td>
           <td>${cuestionario.fecha}</td>
          </#if>
          <#if cuestionario.valor??>
           <td>Monto de indemnización</td>
           <td>${cuestionario.valor}</td>
          </#if>
          <#if cuestionario.adjunto??>
           <td>Adjunto</td>
           <td>${cuestionario.adjunto}</td>
          </#if>
         </tr>
        </tbody>
       </table>
      </section>
     </component>
    </#list>
   </#if>
   <#include "SO-EvalDisc.ftl" />
 </section>
</component> 
 
</#if>