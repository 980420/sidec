<#macro AntecedenteAlimentacion antecedenteAlimentacion>
	<#if antecedenteAlimentacion?? >
	<component>
		<section>
		  <title>Alimentación</title>
		  <text>
			  <table>
			    <tbody>
			      <tr>
			        <td>Natural</td>
			        <td>
						<#if antecedenteAlimentacion.natural>
								Si
						<#else>
								No
						</#if>
					</td>
			        <td>Artificial</td>
			        <td>
						<#if antecedenteAlimentacion.artificial>
								Si
						<#else>
								No
						</#if>
					</td>
			        <td>Mixta</td>
			        <td>
						<#if antecedenteAlimentacion.mixta>
								Si
						<#else>
								No
						</#if>
					</td>
			      </tr>
			    </tbody>
			  </table>
		  </text>
		</section>
	 </component>
	 </#if>
 </#macro>