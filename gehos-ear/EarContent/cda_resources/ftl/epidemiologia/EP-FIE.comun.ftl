<#include "BEGIN.ftl" />

<#macro datosFicha datos>
 	<#if datos??> 	  
 		<component>
		  <section>
			<title>Datos de la ficha</title>
			<table>				
				<tbody>
					<tr>
						<td>
							Número de ficha
						</td>
						<td>
							<#if datos.numeroFicha??>
								${datos.numeroFicha}
							<#else>
						    	-
							</#if>
						</td>
						<td>
							Semana epidemiológica
						</td>
						<td>
							<#if datos.semana??>
								${datos.semana}
							<#else>
						    	-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Fecha de elaboración
						</td>
						<td>
							<#if datos.fecha??>
								${datos.fecha?string('dd/MM/yyyy')}
							<#else>
						    	-
							</#if>
						</td>
						<td>
							Distrito/municipio sanitario
						</td>
						<td>
							<#if datos.distritoMunSanitario??>
								${datos.distritoMunSanitario}
							<#else>
						    	-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Establecimiento
						</td>
						<td>
							<#if datos.establecimiento??>
								${datos.establecimiento}
							<#else>
						    	-
							</#if>
						</td>
						<td>
							Estado
						</td>
						<td>
							<#if datos.estado??>
								${datos.estado}
							<#else>
						    	-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Municipio
						</td>
						<td>
							<#if datos.municipio??>
								${datos.municipio}
							<#else>
						    	-
							</#if>
						</td>
						<td>
							Parroquia
						</td>
						<td>
							<#if datos.parroquia??>
								${datos.parroquia}
							<#else>
						    	-
							</#if>
						</td>
					</tr>
				</tbody>
			</table>
		  </section>
		</component> 		
 	</#if>
</#macro>

<#macro session lista> 
 	<#if lista??&&lista?size !=0>
 	   <#list lista as it>
 		<component>
		  <section>
			<title>${it.etiqueta}</title>
			<#if it.valor??>
				<text>
					${it.valor}
				</text>
			<#elseif it.table??>
					${it.table}				
			</#if>			
			<@session lista=it.contenido/>
		  </section>
		</component>
 		 </#list>
 	</#if> 
</#macro>

<#macro clasificacion fie> 
	<#if fie??>
    	<component>
			<section>
				<title>Clasificación del caso</title>
				<table>				
					<tbody>
						<tr>
							<td>
								Clasificación
							</td>
							<td>
								<#if fie.clasificacion??>
									${fie.clasificacion}
								<#else>
							    	-
								</#if>
							</td>
							<td>
								Base confirmación
							</td>
							<td>
								<#if fie.baseConfirmacion??>
									${fie.baseConfirmacion}
								<#else>
							    	-
								</#if>
							</td>
						</tr>
					</tbody>
				</table>
			</section>
		</component>
 	</#if> 
</#macro>

<@datosFicha datos=documento.datosFicha/>
<@session lista=documento.list/>
<@clasificacion fie=documento/>

<#include "END.ftl" />
