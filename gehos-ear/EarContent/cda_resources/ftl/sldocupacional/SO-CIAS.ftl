<#if documento.interrogatorioAparSist?? && documento.interrogatorioAparSist?size !=0>
<component>
 <section>
  <title>Interrogatorio por aparatos y sistemas</title>
  <#list documento.interrogatorioAparSist as interrAparSist>
  <#if interrAparSist.tienePalabra == true>
   <table>
    <tbody>
     <tr>
      <td><#if interrAparSist.pregunta??>${interrAparSist.pregunta}<#else>-</#if></td>
      <#if interrAparSist.tienePalabra == true>
        <td><#if interrAparSist.palabra??>${interrAparSist.palabra}<#else>-</#if></td>
      </#if>
     </tr>
       
    </tbody>
   </table>
   </#if>
    <#if interrAparSist.tienePalabra == false>
      <text>
			<obs>
				<h1>
					<#if interrAparSist.pregunta??>${interrAparSist.pregunta}:<#else>-</#if>
				</h1>
				<p>
					<#if interrAparSist.especificacion?? && interrAparSist.especificacion!="">
						${interrAparSist.especificacion}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>	
      
     </#if>
  </#list>
 </section>
</component>
</#if>