<#include "BEGIN.ftl" />
<component>
  <section>
	<title>Estudio imagenol&#243;gico</title>
	<component>
	  <section>
		<title>Datos del estudio</title>
		<table>
		  <tbody>
			<tr>
			  <td>Estaci&#243;n</td>
			  <td>
				  <#if documento.nombreEstacion??>
					${documento.nombreEstacion}
				  <#else>-
				  </#if>
			  </td>
			  <td>Fecha de estudio</td>
			  <td>
				  <#if documento.fechaEstudio??>
					${documento.fechaEstudio?string('dd/mm/yyyy')}
				  <#else>-
				  </#if>
			  </td>
			  <td>Fecha de reporte</td>
			  <td>
				<#if documento.fechaReporte??>
					${documento.fechaReporte?string('dd/mm/yyyy')}
				<#else>-
				</#if>
			  </td>
			</tr>
			<tr>
			  <td>M&#233;dico solicitante</td>
			  <td>
			    <#if documento.medicoSolicitante??>
					  <#if documento.medicoSolicitante.nombre??>
							${documento.medicoSolicitante.nombre} ${documento.medicoSolicitante.apellido1} ${documento.medicoSolicitante.apellido2}
					  <#else>-
					  </#if>
				  <#else>
				  -
				  </#if>
			  </td>
			  <td>M&#233;dico diagnisticante</td>
			  <td>
				  <#if documento.medicoDiagnosticante??>
						<#if documento.medicoDiagnosticante.nombre??>
							${documento.medicoDiagnosticante.nombre} ${documento.medicoDiagnosticante.apellido1} ${documento.medicoDiagnosticante.apellido2}
						<#else>-
						</#if>
					<#else>-
				  </#if>
			   </td>
			  <td>Tipo de estudio</td>
			  <td>
				<#if documento.tipoEstudio??>
					${documento.tipoEstudio}
				<#else>-
				</#if>
			  </td>
			</tr>
		  </tbody>
		</table>
		
		<paragraph>
		  <caption>Tratamiento</caption>
		  <#if documento.tratamiento??>
			  ${documento.tratamiento}
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
		</paragraph>
		
		<paragraph>
		  <caption>Comentarios</caption>
		
		  <#if documento.comentariosEstudio??>
			   ${documento.comentariosEstudio}
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
		</paragraph>
		
		<paragraph>
		  <caption>Recomendaciones</caption>
		    <#if documento.recomendaciones??>
			   ${documento.recomendaciones}
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
		</paragraph>
		
	  </section>
	</component>
	<component>
	  <section>
		<title>Alergias</title>
		  <#if documento.alergiasConocidas??>
			   <table>
				  <tr>
					<th colspan="2">Antecedentes</th>
				  </tr>
				  <tr>
					<th>Nombre</th>
					<th>Descripci&#243;n</th>
				  </tr>
				  <#list documento.alergiasConocidas as alergia>
					  <tr>
						<td>
							<#if alergia.nombre??>${alergia.nombre}<#else>-</#if>
						</td>
						<td>
							<#if alergia.descripcion??>${alergia.descripcion}<#else>-</#if>
						</td>
					  </tr>
				  </#list>
				</table>
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
		
	  </section>
	</component>
	
	<component>
	  <section>
		<title>Antecedentes familiares</title>
		<#if documento.antecedentesFamiliares??>
			   <table>
				  <tr>
					<th colspan="2">Antecedentes</th>
				  </tr>
				  <tr>
					<th>Nombre</th>
					<th>Descripci&#243;n</th>
				  </tr>
				  <#list documento.antecedentesFamiliares as antecedente>
				  <tr>
					<td>
						<#if antecedente.nombre??>${antecedente.nombre}<#else>-</#if>
					</td>
					<td>
						<#if antecedente.descripcion??>${antecedente.descripcion}<#else>-</#if>
					</td>
				  </tr>
				  </#list>
				</table>
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
		
	  </section>
	</component>
	
	<component>
	  <section>
		<title>Medidas</title>
		<#if documento.medidas??>
			   <table>
				  <tr>
					<th colspan="2">Mediciones</th>
				  </tr>
				  <tr>
					<th>Medici&#243;n</th>
					<th>Valor</th>
					<th>Unidad de medida</th>
				  </tr>
				  <#list documento.medidas as medida>
				  <tr>
					<td><#if medida.valor??>${medida.medicion}<#else>-</#if></td>
					<td><#if medida.valor??>#{medida.valor}<#else>-</#if></td>
					<td><#if medida.unidadMedida??>${medida.unidadMedida}<#else>-</#if></td>
				  </tr>
				  </#list>
				</table>
			  <#else><text>No existe informaci&#243;n a mostrar.</text>
		  </#if>
	  </section>
	</component>
	
	<component>
	  <section>
		<title>Im&#225;genes del estudio</title>
		<#if documento.imagenes??>
			<list listType="definition">
			  <caption>Im&#225;genes</caption>
			  <#list documento.imagenes as imagen>
				  <item>
					<renderMultiMedia referencedObject="risei1111974043.f06a9c3e-d0d7-415a-9960-be06df3345d4${imagen_index}"/>
				  </item>
			  </#list>
		</list>
		<#else>
			<text> No existe informaci&#243;n a mostrar.</text>
		</#if>
			<#if documento.imagenes??>
				<#list documento.imagenes as imagen>
					<entry>
					  <observationMedia classCode="OBS" moodCode="EVN" ID="risei1111974043.f06a9c3e-d0d7-415a-9960-be06df3345d4${imagen_index}">
						<id root="10.23.4567.345" />
						<value mediaType="image/png">${imagen}</value>
					  </observationMedia>
					</entry>
				</#list>
			</#if>
		  </section>
		</component>
	  </section>
	</component>
	
<#include "END.ftl" />
