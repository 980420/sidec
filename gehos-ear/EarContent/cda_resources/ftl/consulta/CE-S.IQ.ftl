<#include "BEGIN.ftl" />
<component>
	<section>
		<title>
			Datos de solicitud de intervenci&#243;n quir&#250;rgica
		</title>
		
		<table>
			<tbody>
				<tr>
					<td>
					   Servicio quir&#250;rgico
					</td>
					<td>
					  <#if documento.servicio??>
						${documento.servicio}
						<#else>
						-
					  </#if>
					</td>
					<td>
						Car&#225;cter
					</td>
					<td>
					  <#if documento.caracter??>
						${documento.caracter}
						<#else>
						-
					  </#if>
					</td>
					 <td>
					Tipo de riesgo
					</td>
					<td>
					 <#if documento.tipoRiesgo??>
						${documento.tipoRiesgo}
						<#else>
						-
					  </#if>
					</td>
				</tr>
				<tr>
				   
					
					<td>
					Riesgo de infecci&#243;n
					</td>
					<td>
					  <#if documento.riesgoInfeccion??>
						${documento.riesgoInfeccion}
						<#else>
						-
					  </#if>
					</td>
					<td>
					Tipo de anestesia
					</td>
					<td>
					<#if documento.tipoAnestesia??>
						${documento.tipoAnestesia}
						<#else>
						-
					  </#if>
					</td>
					 <td>
					Tipo de quir&#243;fano
					</td>
					<td>
					 <#if documento.tipoQuirofano??>
						${documento.tipoQuirofano}
						<#else>
						-
					  </#if>
					</td>
				</tr>
				<tr>
					
				   
					
					<td>
					Cama UCI
					</td>
					<td>
					<#if documento.camaUCI??&&documento.camaUCI>
						Si
						<#else>
						No
					  </#if>
					</td>
					<td>
					Rayos X
					</td>
					<td>
					<#if documento.rayosX??&&documento.rayosX>
						Si
						<#else>
						No
					  </#if>
					</td>
					<td>
					Biopsia congelaci&#243;n
					</td>
					<td>
					<#if documento.biopsiaCong??&&documento.biopsiaCong>
						Si
						<#else>
						No
					  </#if>
					</td>
				</tr>
				<tr>
				    

					 <td>
						Fecha
				    </td>
				    <td>
					 <#if documento.fecha??>
						${documento.fecha?string("dd/MM/yyyy")}
						<#else>
						-
					  </#if>
				    </td>
				    <td>
					Quir&#243;fano
					</td>
					<td>
					<#if documento.nombreQuirofano??>
						${documento.nombreQuirofano}
						<#else>
						-
					  </#if>
					</td>
				</tr>
			</tbody>
		</table>
		
		<component>
			<section>
				<title>
			    	Procedimiento quir&#250;rgico 
				</title>
				
				<table>
					<tbody>
						<tr>
							<td>Procedimiento</td>
							<td>
							 <#if documento.procedimiento??>
								${documento.procedimiento}
								<#else>
								-
							  </#if>
							</td>
						</tr>
					</tbody>
				</table>
				<table>
					<tbody>
						<tr>
							<td>Duraci&#243;n aproximada</td>
							<td>
							<#if documento.duracion??>
								${documento.duracion}
								<#else>
								-
							  </#if>
							</td>
						</tr>
					</tbody>
				</table>
				
			</section>
		</component>
		

	 <#if documento.cirujanos??&&documento.cirujanos?size != 0>
		<table>
			<tbody>
				<tr>
					<th colspan="3">Listado de cirujanos seleccionados</th>
				</tr>
				<tr>
					<th>Mat. MPPS</th>
					<th>M&#233;dico</th>
					<th>Mat. CM</th>
				</tr>
			 <#list documento.cirujanos as obj>
				<tr>
					<td>
						 <#if obj.matricula1??>
							${obj.matricula1}
						  </#if>
					</td>
					<td>
						 <#if obj.nombres??>
							${obj.nombres}
						  </#if>
						  <#if obj.apellido1??>
							${obj.apellido1}
						  </#if>
						  <#if obj.apellido2??>
							${obj.apellido2}
						  </#if>
					</td>
					<td>
						 <#if obj.matricula2??>
							${obj.matricula2}
						  </#if>
					</td>
				</tr>
			 </#list>
			</tbody>
		</table>
		</#if>
		 <#if documento.ayudantes??&&documento.ayudantes?size != 0>
		<table>
			<tbody>
				<tr>
					<th colspan="3">Listado de ayudantes seleccionados</th>
				</tr>
				<tr>
					<th>Mat. MPPS</th>
					<th>M&#233;dico</th>
					<th>Mat. CM</th>
				</tr>
			 <#list documento.ayudantes as obj>
				<tr>
					<td>
						 <#if obj.matricula1??>
							${obj.matricula1}
						  </#if>
					</td>
					<td>
						 <#if obj.nombres??>
							${obj.nombres}
						  </#if>
						  <#if obj.apellido1??>
							${obj.apellido1}
						  </#if>
						  <#if obj.apellido2??>
							${obj.apellido2}
						  </#if>
					</td>
					<td>
						 <#if obj.matricula2??>
							${obj.matricula2}
						  </#if>
					</td>
				</tr>
			 </#list>
			</tbody>
		</table>
		</#if>
		 <#if documento.equiposEspeciales??&&documento.equiposEspeciales?size != 0>
		<table>
			<tbody>
				<tr>
					<th colspan="2">Equipos especiales seleccionados</th>
				</tr>
				<tr>
					<th>Nombre</th>
					<th width="20px">Cantidad</th>
				</tr>
				
			 <#list documento.equiposEspeciales as obj>
				<tr>
					<td>
						 <#if obj.nombre??>
							${obj.nombre}
						  </#if>
					</td>
					<td>
						 <#if obj.cantidad??>
							#{obj.cantidad}
						  </#if>
					</td>
				</tr>
			 </#list>
			</tbody>
		</table>
		</#if>
		 <#if documento.dispositivos??&&documento.dispositivos?size != 0>
		<table>
			<tbody>
				<tr>
					<th>Dispositivos seleccionados</th>
				</tr>
			 <#list documento.dispositivos as obj>
				<tr>
					<td>
						 <#if obj??>
							${obj}
						  </#if>
					</td>
				</tr>
			 </#list>
			</tbody>
		</table>
		</#if>
		 <#if documento.presentaciones??&&documento.presentaciones?size != 0>
		<table>
			<tbody>
				<tr>
					<th colspan="7">Presentaciones de medicamentos seleccionados</th>
				</tr>
				<tr>
					<th>C&#243;digo</th>
					<th>Principio activo</th>
					<th>Concentraci&#243;n</th>
					<th>Forma farmac&#233;utica</th>
					<th>V&#237;as administraci&#243;n</th>
					<th>Dosificaci&#243;n</th>
					<th>Cantidad</th>
				</tr>
				
			 <#list documento.presentaciones as obj>
				<tr>
					<td>
						 <#if obj.codigo??>
							${obj.codigo}
						  </#if>
					</td>
					<td>
						 <#if obj.principioActivo??>
							${obj.principioActivo}
						  </#if>
					</td>
					<td>
						 <#if obj.concentracion??>
							${obj.concentracion}
						  </#if>
					</td>
						<td>
						 <#if obj.forma??>
							${obj.forma}
						  </#if>
					</td>
					<td>
						 <#if obj.via??>
							${obj.via}
						  </#if>
					</td>
					<td>
						 <#if obj.dosificacion??>
							${obj.dosificacion}
						  </#if>
					</td>
					<td>
						 <#if obj.cantidad??>
							#{obj.cantidad} ${obj.unidadMedida}
						  </#if>
						   
					</td>
				</tr>
			 </#list>
			</tbody>
		</table>
		</#if>
		
<#if documento.diagnostico??>
		<component>
		  <section>
			<title>Diagn&#243;stico</title>
			<#if documento.diagnostico.enfermedades?? && documento.diagnostico.enfermedades?size != 0 >
			  
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
			<#else>
			<text>
			 No existe informaci&#243;n a mostrar.
			 </text>
			</#if>
		  </section>
	 </component>
</#if>
	</section>
</component>
<#include "END.ftl" />      