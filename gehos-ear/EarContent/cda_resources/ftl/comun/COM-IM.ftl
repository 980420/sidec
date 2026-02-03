	<#if documento.presentacionMedicamentosIndicados?? && documento.presentacionMedicamentosIndicados?size !=0>
	    <component>
	      <section>
	  		<title>Indicación de presentaciones de medicamentos</title>
	  		<text>
				<table>
					<tbody>
						<tr>
							<th colspan="14">Listado de presentaciones de medicamentos indicados</th>
						</tr>
						<tr>
							<th>C&#243;digo</th>
							<th>Principio activo</th>
							<th>Concentraci&#243;n</th>
							<th>Forma farmac&#233;utica</th>
							<th>V&#237;as administraci&#243;n</th>
							<th>Dosificaci&#243;n</th>
							<th>Dosis</th>
							<th>Vía</th>
							<th>Frecuencia</th>
							<th>Fecha suspensión</th>
							<th>Enfermera</th>
							<th>Stat</th>
							<th>Cronicismo</th>
							<th>Observaciones</th>
						</tr>
						
					 <#list documento.presentacionMedicamentosIndicados as obj>
						<tr>
							<td>
								<#list obj.medicamento.ppiosActivos as ppio>
								 	<#if ppio.codigoAtc??>
										${ppio.codigoAtc + "\\n"}  
								  	</#if>
						 		</#list>
							</td>
							<td>
								<#list obj.medicamento.ppiosActivos as ppio>
								 	<#if ppio.ppioActivo??>
										${ppio.ppioActivo + "\\n"}
								  	</#if>
							  	</#list>
							</td>
							<td>
								<#list obj.medicamento.ppiosActivos as ppio>
								 	<#if ppio.concentracion??>
										${ppio.concentracion + "\\n"}
								  	</#if>
						  		</#list>
							</td>
								<td>
								 <#if obj.medicamento.formaFarmaceutica??>
									${obj.medicamento.formaFarmaceutica}
								  </#if>
							</td>
							<td>
								<#list obj.medicamento.viasAdministracion as via>
								 	<#if via??>
										${via}
								  	</#if>
						  		</#list>
							</td>
							<td>
							 	<#if obj.medicamento.dosificacion??>
									${obj.medicamento.dosificacion}
							  	</#if>
							</td>
							<td>
							 	<#if obj.dosis??>
									${obj.dosis}
							  	</#if>
							</td>
							<td>
							 	<#if obj.via??>
									${obj.via}
							  	</#if>
							</td>
							<td>
							 	<#if obj.frecuencia??>
									${obj.frecuencia}
							  	</#if>
							</td>
							<td>
							 	<#if obj.fechaSuspencion??>
									${obj.fechaSuspencion?string('dd/MM/yyyy')}
							  	</#if>
							</td>
							<td>
							 	<#if obj.enfermera??>
									${obj.enfermera?string('Sí','No')}
							  	</#if>
							</td>
							<td>
							 	<#if obj.stat??>
									${obj.stat?string('Sí','No')}
							  	</#if>
							</td>
							<td>
							 	<#if obj.cronicismo??>
									${obj.cronicismo?string('Sí','No')}
							  	</#if>
							</td>
							<td>
							 	<#if obj.observaciones??>
									${obj.observaciones}
							  	</#if>
							</td>
						</tr>
					 </#list>
					</tbody>
				</table>
				</text>
			</section>
	    </component>
    </#if>
    <#if documento.tratamientosMedicosIndicados?? && documento.tratamientosMedicosIndicados?size !=0>
	    <component>
	      <section>
	  		<title>Indicación de tratamientos médicos</title>
	  		<text>
				<table>
					<tbody>
						<tr>
							<th colspan="5">Listado de tratamientos médicos indicados</th>
						</tr>
						<tr>
							<th>Descripción</th>
							<th>Vía</th>
							<th>Frecuencia</th>
							<th>Fecha suspensión</th>
							<th>Enfermera</th>
						</tr>
						
					 <#list documento.tratamientosMedicosIndicados as obj>
						<tr>
							<td>
							 	<#if obj.descripcion??>
									${obj.descripcion}  
							  	</#if>
							</td>
							<td>
							 	<#if obj.via??>
									${obj.via}
							  	</#if>
							</td>
							<td>
							 	<#if obj.frecuencia??>
									${obj.frecuencia}
							  	</#if>
							</td>
							<td>
								<#if obj.fechaSuspension??>
									${obj.fechaSuspension?string('dd/MM/yyyy')}
							  	</#if>
							</td>
							<td>
								<#if obj.enfermera??>
									${obj.enfermera?string('Sí','No')}
							  	</#if>
							</td>
						</tr>
					 </#list>
					</tbody>
				</table>
			</text>
	      </section>
	    </component>
    </#if>