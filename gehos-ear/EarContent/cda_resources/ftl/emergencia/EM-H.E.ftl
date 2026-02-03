<#include "BEGIN.ftl" />
	<component>
    	<section>
		    <title>Datos de la hoja de emergencias</title>
		    <table>
            	<tbody>
                	<tr>
                		<td>Fecha de entrada</td>
                    	<td>
	                  		<#if documento.fechaEntrada??>
	                       		${documento.fechaEntrada?string('dd/MM/yyyy')}
	                        <#else>
	                       		-
	                        </#if>
	                    </td>
	                    <td>Hora de entrada</td>
	                    <td>
	                   		<#if documento.horaEntrada??>
	                       		${documento.horaEntrada?string('hh:mm a')}
	                   		<#else>
	                       		-
	                       	</#if>
	                   	</td>         
	           		</tr>
	           		<tr>
                		<td>Fecha de salida</td>
                    	<td>
	                  		<#if documento.fechaSalida??>
	                       		${documento.fechaSalida?string('dd/MM/yyyy')}
	                        <#else>
	                       		-
	                        </#if>
	                    </td>
	                    <td>Hora de salida</td>
	                    <td>
	                   		<#if documento.horaSalida??>
	                       		${documento.horaSalida?string('hh:mm a')}
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