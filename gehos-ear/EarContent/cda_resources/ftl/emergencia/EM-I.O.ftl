<#include "BEGIN.ftl" />
	<component>
    	<section>
		    <title>Datos del ingreso en observación</title>
		    <table>
            	<tbody>
                	<tr>
	                	<td>Fecha de ingreso</td>
	                   	<td>
	                   		<#if documento.fechaIngreso??>
	                      		${documento.fechaIngreso?string('dd/MM/yyyy')}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                   	<td>Hora de ingreso</td>
	                   	<td>
	                   		<#if documento.horaIngreso??>
	                      		${documento.horaIngreso?string('hh:mm a')}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                </tr>
	     		</tbody>
   			</table>
		</section>
	</component>
	
<#include "END.ftl" />