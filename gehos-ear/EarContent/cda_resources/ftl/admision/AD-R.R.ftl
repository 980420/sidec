<#include "../comun/BEGIN.ftl" />
      <component>
	   	<section>
	     	<title>Datos de la referencia</title>
	     	<text>
				<table>
   					<tbody> 
   						<tr>
							<td>
								Entidad que refiere
							</td>										
							<td>
								<#if documento.hospitalReferente??>
							   		${documento.hospitalReferente}
						   		<#else>
						   			-
							   	</#if>
							</td>
							<td>
								Entidad a la que se refiere
							</td>
							<td>
								<#if documento.hospitalDestino??>
							   		${documento.hospitalDestino}
						   		<#else>
						   			-
							   	</#if>
							</td>							
							<td>
								Servicio
							</td>										
							<td>
								<#if documento.servicio??>
							   		${documento.servicio}
						   		<#else>
						   			-
							   	</#if>
							</td>
						</tr>  												
						<tr>
							<td>
								Especialidad
							</td>										
							<#if documento.especialidad??>
								<td>
									${documento.especialidad}
								</td>
							</#if>
														
						</tr>	
					</tbody>
				</table>
				<#if documento.motivo??>
										
						<paragraph>
					         <caption>
					         Motivo de la referencia
					         </caption>
							${documento.motivo}
					      </paragraph>

			     </#if>
			     <#if documento.resumenClinico??>
										
						<paragraph>
					         <caption>
					       Res&#250;men cl&#237;nico
					         </caption>
							${documento.resumenClinico}
					      </paragraph>
			     </#if>
			</text>
        </section>
      </component>
      
      <#if documento.tipoTraslado??&&documento.tipoDestino??&&documento.medioTraslado??>
		<component>
		  <section>
			<title>Traslado</title>
			<text>
      	     	<table>
   					<tbody> 
   						<tr>
							<td>
								Tipo de traslado
							</td>										
							<td>
								<#if documento.tipoTraslado??>
							   		${documento.tipoTraslado}
						   		<#else>
						   			-
							   	</#if>
							</td>
							<td>
								Tipo de destino
							</td>
							<td>
								<#if documento.tipoDestino??>
							   		${documento.tipoDestino}
						   		<#else>
						   			-
							   	</#if>
							</td>							
							<td>
								Medio de traslado
							</td>										
							<td>
								<#if documento.medioTraslado??>
							   		${documento.medioTraslado}
						   		<#else>
						   			-
							   	</#if>
							</td>
						</tr>  												
					</tbody>
				</table>
			<text>
        </section>
      </component>
      </#if>
      
      <#if documento.diagnostico??>
		<component>
		  <section>
			<title>Diagn&#243;stico</title>
			<#if documento.diagnostico.enfermedades?? && documento.diagnostico.enfermedades?size != 0 >
				<text>
					<table>
						<tbody>
							<tr>
								<th colspan="2">
									Clasificaci&#243;n Internacional de Enfermedades (CIE)
								</th>
							</tr>
							<tr>
								<th>C&#243;digo</th>
								<th>Descripci&#243;n</th>
							</tr>
							<#list documento.diagnostico.enfermedades as enf>
								<tr>
								    <#if enf.codigo??>
									<td>${enf.codigo}</td>
									<#else>
									<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if enf.descripcion??>
									<td>${enf.descripcion}</td>
									<#else>
									<td bgcolor="#E3E4E4"></td>
									</#if>
	
								</tr>
							</#list>
						</tbody>
					</table>
				<text>
			<#else>
			 <text>
			 	No existe informaci&#243;n a mostrar.
			 </text>
			</#if>
		  </section>
	 </component>
</#if>
 <#include "../comun/END.ftl" />