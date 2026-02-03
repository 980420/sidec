<#include "BEGIN.ftl" />
<#if documento.listadoControlMedicamento?? && documento.listadoControlMedicamento?size !=0>
	<component>
		<section>
			<title>Listado de control de medicamentos</title>
			
		       	<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de control de medicamentos</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Medicamento</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoControlMedicamento as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td><#if obj.medicamento??>${obj.medicamento}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
		
		</section>
	</component>
</#if>
		
<#if documento.listadoControlOrdenDieta?? && documento.listadoControlOrdenDieta?size !=0>
	<component>
		<section>
			<title>Listado de control de orden de dieta</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de control de orden de dieta</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Tipo de dieta </th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoControlOrdenDieta as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermera??>${obj.enfermera}</#if></td>
								<td><#if obj.tipoDieta??>${obj.tipoDieta}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
	  	</section>
	</component>	
</#if>	
		
<#if documento.listadoCumplimientoOxigenoterapia?? && documento.listadoCumplimientoOxigenoterapia?size !=0>
	<component>
		<section>
			<title>Listado de control de oxigenoterapias</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="7">Listado de control de oxigenoterapias</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Flujometria</th>
							<th>Vía</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoOxigenoterapia as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermera??>${obj.enfermera}</#if></td>
								<td><#if obj.flujometria??>${obj.flujometria}</#if></td>
								<td><#if obj.via??>${obj.via}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>



<#if documento.listadoCumplimientoNutricionesParenterales?? && documento.listadoCumplimientoNutricionesParenterales?size !=0>
	<component>
		<section>
			<title>Listado de control de la nutrición parenteral</title>
			<#list documento.listadoCumplimientoNutricionesParenterales as obj>
				<component>
					<section>
						<title>Datos del control de la nutrición parenteral</title>
							<component>
								<section>
									<title>Datos de la nutrición parenteral</title>
									<component>
										<section>
											<title>Listado de componentes de la nutrición parenteral</title>
											
													<table>
														<tbody>
															<tr>
																<th colspan="3">Listado de componentes de la nutrición parenteral</th>
															</tr>
															<tr>
																<th>Componente</th>
																<th>Cantidad (cc)</th>
																<th>Porciento (%)</th>
															</tr>
															<#list obj.componentes as componente>
																<tr>
																	<td><#if componente.componente??>${componente.componente}</#if></td>
																	<td><#if componente.cantidad??>${componente.cantidad}</#if></td>
																	<td><#if componente.porciento??>${componente.porciento}</#if></td>
																</tr>
															</#list>
														</tbody>
													</table>
												
													<table>
														<tbody>
															<tr>
																<td>Volumen total</td>
																<td><#if obj.volumenTotal??>${obj.volumenTotal}</#if></td>
																<td>Total de calorías</td>
																<td><#if obj.totalCalorias??>${obj.totalCalorias}</#if></td>
																<td>Total de N2</td>
																<td><#if obj.totalDinitrogeno??>${obj.totalDinitrogeno}</#if></td>
															</tr>
														</tbody>
													</table>
										</section>
									</component>
									
												<table>
													<tbody>
														<tr>
															<td>Velocidad infusión</td>
															<td><#if obj.velocidadInfusion??>${obj.velocidadInfusion}</#if></td>
															<td>Horario</td>
															<td><#if obj.horario??>${obj.horario}</#if></td>
															<td>Fecha de suspensión</td>
															<td><#if obj.fechaSuspension??>${obj.fechaSuspension?string('dd/MM/yyyy')}</#if></td>
														</tr>
													</tbody>
												</table>
								</section>
							</component>
							<component>
								<section>
									<title>Cumplimiento</title>
									<table>
										<tbody>
											<tr>
												<td>Fecha</td>
												<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
												<td>Hora</td>
												<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
												<td>Cumplido</td>
												<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
												<td>Enfermero</td>
												<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
											</tr>
										</tbody>
									</table>
									<#if obj.causa??>
										<paragraph>
											<caption>Causa</caption>
											${obj.causa}
										</paragraph>
									</#if>
								</section>
							</component>
					</section>
				</component>
			</#list>
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoNebuloterapia?? && documento.listadoCumplimientoNebuloterapia?size !=0>
	<component>
		<section>
			<title>Listado de control de nebuloterapias</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="9">Listado de control de nebuloterapias</th>
						</tr>
						<tr>
							<th>Principios activos</th>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Vía</th>
							<th>Dosificación</th>
							<th>Dosis</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoNebuloterapia as obj>
							<tr>
								<td>
									<#if obj.ppios?? && obj.ppios?size !=0>
										<#list obj.ppios as ppio>
											${ppio + "\\n"}
										</#list>
									</#if>
								</td>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td>
									<#if obj.vias?? && obj.vias?size !=0>
										<#list obj.vias as via>
											${via + "\\n"}
										</#list>
									</#if>	
								</td>
								<td><#if obj.dosificacion??>${obj.dosificacion}</#if></td>
								<td><#if obj.dosis??>${obj.dosis}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
		
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoHidratacionesParenterales?? && documento.listadoCumplimientoHidratacionesParenterales?size !=0>
	<component>
		<section>
			<title>Listado de control de hidratación parenteral</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="9">Listado de control de hidratación parenteral</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Tipo solución</th>
							<th>Cantidad líquido</th>
							<th>Vía</th>
							<th>Velocidad infusión</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoHidratacionesParenterales as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td><#if obj.tipoSolucion??>${obj.tipoSolucion}</#if></td>
								<td><#if obj.cantidadLiquido??>${obj.cantidadLiquido}</#if></td>
								<td><#if obj.via??>${obj.via}</#if></td>
								<td><#if obj.velocidadInfusion??>${obj.velocidadInfusion}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoFormulaOficinal?? && documento.listadoCumplimientoFormulaOficinal?size !=0>
	<component>
		<section>
			<title>Listado de control de fórmulas oficinales</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de control de fórmulas oficinales</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Fórmula</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoFormulaOficinal as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td><#if obj.nombre??>${obj.nombre}</#if>	</td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoFormulaMagistral?? && documento.listadoCumplimientoFormulaMagistral?size !=0>
	<component>
		<section>
			<title>Listado de control de fórmulas magistrales</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de control de fórmulas magistrales</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Fórmula</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoFormulaMagistral as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td><#if obj.nombre??>${obj.nombre}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoTratamiento?? && documento.listadoCumplimientoTratamiento?size !=0>
	<component>
		<section>
			<title>Listado de control de tratamientos</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de control de tratamientos</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Tratamiento</th>
							<th>Causa</th>
							<th>Cumplido</th>
						</tr>
						<#list documento.listadoCumplimientoTratamiento as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermera??>${obj.enfermera}</#if></td>
								<td><#if obj.tratamiento??>${obj.tratamiento}</#if></td>
								<td><#if obj.causa??>${obj.causa}</#if></td>
								<td><#if obj.cumplido??>${obj.cumplido?string('Sí','No')}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>

<#if documento.listadoCumplimientoObservaciones?? && documento.listadoCumplimientoObservaciones?size !=0>
	<component>
		<section>
			<title>Otros datos del control de tratamiento</title>
			
				<table>
					<tbody>
						<tr>
							<th colspan="4">Otros datos del control de tratamiento</th>
						</tr>
						<tr>
							<th>Fecha</th>
							<th>Hora</th>
							<th>Enfermera(o)</th>
							<th>Observaciones</th>
						</tr>
						<#list documento.listadoCumplimientoObservaciones as obj>
							<tr>
								<td><#if obj.fecha??>${obj.fecha?string('dd/MM/yyyy')}</#if></td>
								<td><#if obj.hora??>${obj.hora?string('HH:mm a')}</#if></td>
								<td><#if obj.enfermero??>${obj.enfermero}</#if></td>
								<td><#if obj.observaciones??>${obj.observaciones}</#if></td>
							</tr>
						</#list>
					</tbody>
				</table>
			
		</section>
	</component>
</#if>
<#include "END.ftl" />