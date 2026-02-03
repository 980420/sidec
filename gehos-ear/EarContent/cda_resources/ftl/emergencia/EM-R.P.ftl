<#include "BEGIN.ftl" />
	<component>
    	<section>
		    <title>Datos de la recepción del paciente</title>
		    <table>
            	<tbody>
                	<tr>
	                	<td>Traído por</td>
	                   	<td>
	                   		<#if documento.formaLlegada??>
	                      		${documento.formaLlegada}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                   	<td>Índice de severidad</td>
	                   	<td>
	                   		<#if documento.nivelGravedad??>
	                      		${documento.nivelGravedad}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                </tr>
	     		</tbody>
   			</table>
		    <text>
				<obs>
					<h1>Motivo de consulta</h1>
					<#if documento.motivoConsulta??>
						<p>${documento.motivoConsulta}</p>
					<#else>
						<p>-</p>
					</#if>
				</obs>
				<obs>
					<h1>Observaciones</h1>
					<#if documento.observaciones??>
						<p>${documento.observaciones}</p>
					<#else>
						<p>-</p>
					</#if>
				</obs>
			</text>
		</section>
	</component>
	
<#include "END.ftl" />