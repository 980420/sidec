<#include "BEGIN.ftl" />
	<component>
		<section>
			<title>Datos de la evolución</title>
			<table>
	        	<tbody>
	           		<tr>
	               		<td>Fecha</td> 
	               		<td>
		                    <#if documento.fecha??>
		                       ${documento.fecha?string('dd/MM/yyyy')}
		                   <#else>
		                       -
		                   </#if>
	               		</td>
		               <td>Hora</td>
		               <td>
		                  <#if documento.hora??>
		                       ${documento.hora?string('hh:mm a')}
		                   <#else>
		                       -
		                   </#if>
		               </td>
		           	</tr>
		           	<tr>
	       				<td>Estado del paciente</td>
	               		<td>
		                   <#if documento.estadoPaciente??>
		                       ${documento.estadoPaciente}
		                   <#else>
		                       -
		                   </#if>
	               		</td>         
	           		</tr>
	         	</tbody>
		 	</table>
	 	</section>
	</component>
	<#if documento.subjetivo??>
		<component>
			<section>
				<title>Subjetivo</title>			
				<text>
					<obs>
						<h1>Subjetivo:</h1>
						<p>${documento.subjetivo}</p>
					</obs>
				</text>
		 	</section>
		</component>
	</#if>
	<#if documento.objetivo??>
		<component>
			<section>
				<title>Objetivo</title>			
				<text>
					<obs>
						<h1>Objetivo:</h1>
						<p>${documento.objetivo}</p>
					</obs>
				</text>
		 	</section>
		</component>
	</#if>
	<#if documento.diagnosticosNanda??&&documento.diagnosticosNanda?size !=0>
		<component>
			<section>
				<title>Planificación</title>				
				 <#list documento.diagnosticosNanda as diags>
					<component>
						<section>
							<title>${diags.diagnostico}</title>
							<table>
					        	<tbody>
						        	<tr>
										<th colspan="2">
											Listado de intervenciones
										</th>
									</tr>
									<tr>
										<th>Código</th>
										<th>Intervención</th>
									</tr>
									<#list diags.intervenciones as inter>
										<tr>
									   		<td>${inter.codigo}</td>
									   		<td>${inter.intervencion}</td>
										</tr>
									</#list>
					            </tbody>
				            </table>
				            <#list diags.intervenciones as inter>
					            <component>
									<section>
										<title>${inter.codigo}</title>
										<table>
								        	<tbody>
									        	<tr>
													<th colspan="2">
														Listado de resultados 
													</th>
												</tr>
												<tr>
													<th>Código</th>
													<th>Resultado</th>
												</tr>
												<#list inter.resultados as result>
													<tr>
												   		<td>${result.codigo}</td>
												   		<td>${result.resultado}</td>
													</tr>
												</#list>
								            </tbody>
							            </table>						            
									</section>
								</component>
							</#list>
						</section>
					</component>
				 </#list>
			</section>
		</component>
	</#if>
	<#if documento.evolucion??>
		<component>
			<section>
				<title>Evolución</title>			
				<text>
					<obs>
						<h1>Evolución:</h1>
						<p>${documento.evolucion}</p>
					</obs>
				</text>
		 	</section>
		</component>
	</#if>
 <#include "END.ftl" />