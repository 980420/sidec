<#include "../comun/BEGIN.ftl" />
<#escape x as x?html>
	<#if documento.solicitud??>  
		<component>
			<section>
				<title>Datos de la solicitud</title>
				<text>
					<table>
						<tbody>
							<tr>
								<td>Número:</td>
								<td>
									<#if documento.solicitud.numero?? && documento.solicitud.numero!="">
										${documento.solicitud.numero}
									<#else>
										-
									</#if>
								</td>
								<td>Servicio:</td>
								<td>
									<#if documento.solicitud.servicio?? && documento.solicitud.servicio!="">
										${documento.solicitud.servicio}
									<#else>
										-
									</#if>
								</td>
								<td>Área:</td>
								<td>
									<#if documento.solicitud.area?? && documento.solicitud.area!="">
										${documento.solicitud.area}
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>
								<td>Fecha emisión:</td>
								<td>
									<#if documento.solicitud.fecha_emision??>
										${documento.solicitud.fecha_emision?string("dd/MM/yyyy")}
									<#else>
										-
									</#if>
								</td>
							</tr>
						</tbody>
					</table>
				</text>															
			</section>
		</component>
	</#if>
	
	<#if documento.medico_solicitante??>  
		<component>
			<section>
				<title>Médico solicitante</title>
				<text>
					<table>
						<tbody>
							<tr>
								<td>Cédula:</td>
								<td>
									<#if documento.medico_solicitante.cedula?? && documento.medico_solicitante.cedula!="">
										${documento.medico_solicitante.cedula}
									<#else>
										-
									</#if>
								</td>
								<td>Nombre:</td>
								<td>
									<#if documento.medico_solicitante.nombres?? && documento.medico_solicitante.nombres!="">
										${documento.medico_solicitante.nombres}
									<#else>
										-
									</#if>
								</td>
								<td>Primer apellido:</td>
								<td>
									<#if documento.medico_solicitante.apellido1?? && documento.medico_solicitante.apellido1!="">
										${documento.medico_solicitante.apellido1}
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>
								<td>Segundo apellido:</td>
								<td>
									<#if documento.medico_solicitante.apellido2?? && documento.medico_solicitante.apellido2!="">
										${documento.medico_solicitante.apellido2}
									<#else>
										-
									</#if>
								</td>
							</tr>
						</tbody>
					</table>
				</text>															
			</section>
		</component>
	</#if>
	
	<#if documento.listado_examenes?? && documento.listado_examenes?size != 0 >  
		<component>
			<section>        	
				<code code="30954-2" codeSystem="2.16.840.1.113883.6.1" displayName="Resultados de exámenes de laboratorio"/>
				<title>Resultados de exámenes</title>
				<#list documento.listado_responsables as responsables>
					<component>
						<section>
							<title>
								Responsable: <#if responsables.persona.nombres?? && responsables.persona.nombres != "">${responsables.persona.nombres}</#if>
								<#if responsables.persona.apellido1?? && responsables.persona.apellido1 != ""> ${responsables.persona.apellido1}</#if>
								<#if responsables.persona.apellido2?? && responsables.persona.apellido2 != ""> ${responsables.persona.apellido2}</#if>						
							</title>							
							<#if responsables.examenes?? && responsables.examenes?size != 0>
								<#list responsables.examenes as examen>
									<component>
										<section>
											<title>${examen.nombreDeExamen}</title>
											<#if examen.listado_aislammientos?? && examen.listado_aislammientos?size != 0>
												<component>
													<section>
														<title>Microorganismos aislados</title>
														<#list examen.listado_aislammientos as aislamiento>
															<component>
																<section>
																	<title>Aislamiento: ${aislamiento.aislamiento}	Microorganismo: ${aislamiento.microorganismo}	Placa: ${aislamiento.placa}</title>
																	<text>
																		<#if aislamiento.listado_antimicrobianos?? && aislamiento.listado_antimicrobianos?size != 0>
																			<table>
																				<tbody>
																					<tr>
																						<th colspan="3">Listado de antimicrobianos</th>
																					</tr>
																					<tr>
																						<th>Antimicrobiano</th>
																						<th>CIM</th>
																						<th>Sistema</th>
																					</tr>
																					<#list aislamiento.listado_antimicrobianos as antimicrobiano>
																						<tr>
																							<td>
																								<#if antimicrobiano.antimicrobiano??>
																									${antimicrobiano.antimicrobiano}
																								</#if>
																							</td>
																							<td>
																								<#if antimicrobiano.cim??>
																									${antimicrobiano.cim}
																								</#if>
																							</td>
																							<td>
																								<#if antimicrobiano.sistema??>
																									${antimicrobiano.sistema}
																								</#if>
																							</td>
																						</tr>
																					</#list>
																				</tbody>
																			</table>
																		</#if>
																	</text>
																</section>
															</component>
														</#list>
													</section>
												</component>
											</#if>
											<#if examen.resultado?? >
												<#if examen.resultado.valorReferenciaList?? && examen.resultado.valorReferenciaList?size != 0>													
													<text>
														<table>
															<tbody>
																<tr>
																	<td>Resultado: 
																		<#if examen.resultado.resultado?? && examen.resultado.resultado != "">														
																			${examen.resultado.resultado} <#if examen.resultado.unidadDeMedida?? && examen.resultado.unidadDeMedida != "">${examen.resultado.unidadDeMedida}</#if>														
																		<#else>
																			-
																		</#if>
																	</td>
																	<td>          Valor de referencia:</td>																	
																</tr>
															</tbody>
														</table>
													</text>
																	
													<text>
														<table>
															<tbody>
																<#list examen.resultado.valorReferenciaList as valoresRef>
																	<tr>
																		<td>                                      </td>						
																		<td>
																			${valoresRef}
																		</td>
																	</tr>
																</#list>
															</tbody>
														</table>
													</text>
																
												<#else>
													<text>
														<table>
															<tbody>
																<tr>
																	<td>Resultado:</td>
																	<td>
																		<#if examen.resultado.resultado?? && examen.resultado.resultado != "">														
																				${examen.resultado.resultado} <#if examen.resultado.unidadDeMedida?? && examen.resultado.unidadDeMedida != "">${examen.resultado.unidadDeMedida}</#if>														
																		<#else>
																			-
																		</#if>	
																	</td>
																	<td>Valor de referencia:</td>
																	<td>
																		<#if examen.resultado.valorReferencia?? && examen.resultado.valorReferencia != "">
																					${examen.resultado.valorReferencia}		
																		<#else>
																			-
																		</#if>										
																	</td>
																</tr>
															</tbody>
														</table>									
													</text>
												</#if>
											</#if>
											
											<#if examen.grupos?? && examen.grupos?size != 0>
												<#list examen.grupos as grupo>
													<component>
														<section>
															<title>${grupo.nombreDeGrupo}</title>
															<text>
																<#if grupo.resultados?? && grupo.resultados?size != 0>
																	<table>
																		<tbody>
																			<tr>
																				<th colspan="3">Listado de aspectos</th>
																			</tr>
																			<tr>
																				<th>Nombre</th>
																				<th>Resultado</th>
																				<th>Valor de referencia</th>
																			</tr>
																			<#list grupo.resultados as aspecto>
																				<tr>
																					<td>
																						<#if aspecto.nombre??>
																							${aspecto.nombre}
																						</#if>
																					</td>
																					<td>
																						<#if aspecto.resultado?? && aspecto.resultado != "" >
																							${aspecto.resultado} <#if aspecto.unidadDeMedida?? && aspecto.unidadDeMedida != "">${aspecto.unidadDeMedida}</#if>
																						</#if>
																					</td>
																					<td>
																						<#if aspecto.valorReferencia??>
																							${aspecto.valorReferencia}
																						</#if>
																					</td>
																				</tr>
																			</#list>
																		</tbody>
																	</table>
																</#if>
															</text>
														</section>
													</component>
												</#list>
											</#if>
										</section>
									</component>
								</#list>
							</#if>
						</section>
					</component>					
				</#list>
			</section>
		</component>
	</#if>
</#escape>
<#include "../comun/END.ftl" />