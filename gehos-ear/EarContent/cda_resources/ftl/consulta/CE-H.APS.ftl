<#include "BEGIN.ftl" />
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
			</#if>
			<@session lista=it.contenido/>
		  </section>
		</component>
 		 </#list>
 	</#if>
 
</#macro>

<@session lista=documento.list/>
<#if documento.subjetivo??||documento.objetivo??||documento.apreciacion??||documento.plan??>
<component>
      <section>
        <title>Evoluci&#243;n SOAP</title>
        <#if documento.subjetivo??>
        <component>
		      <section>
		        <title>Subjetivo (S)</title>
				<paragraph>
						<caption>
							Subjetivo
						</caption>

							${documento.subjetivo}
						
				</paragraph>
			  </section>
		</component>
		</#if>
		 <#if documento.objetivo??>
          <component>
		      <section>
		        <title>Objetivo (O)</title>
				<paragraph>
						<caption>
							Objetivo
						</caption>

							${documento.objetivo}
						
				</paragraph>
			  </section>
		</component>
		</#if>
		<#if documento.apreciacion??>
        <component>
		      <section>
		        <title>Apreciación (A)</title>
						<#if documento.apreciacion??>
								<component>
								  <section>
									<title>Diagn&#243;stico</title>
									<#if documento.apreciacion.enfermedades?? && documento.apreciacion.enfermedades?size != 0 >
									  
										<table>
											<tbody>
												<tr>
													<th colspan="3">
														Clasificaci&#243;n Internacional de Enfermedades (CIE)
													</th>
												</tr>
												<tr>
													<th>C&#243;digo</th>
													<th>Descripci&#243;n</th>
													<th>Tipo</th>
												</tr>
												<#list documento.apreciacion.enfermedades as enf>
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
														<#if enf.tipo??>
														<td>${enf.tipo}</td>
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
		</#if>
		<#if documento.plan??>
        <component>
		      <section>
		        <title>Plan (P)</title>
				<paragraph>
						<caption>
							Plan
						</caption>
							${documento.plan}
				</paragraph>
			  </section>
		</component>
		</#if>

	  </section>
</component>
</#if>
<#if documento.mantenimiento??>
        <component>
		      <section>
		        <title>Mantenimiento de salud</title>
				<table>
					<tbody>
						<tr>
							<th colspan="6">
								Listado de par&#225;metros
							</th>
						</tr>
						<tr>
							<th>Fecha inicio</th>
							<th>Par&#225;metro</th>
							<th>Periodicidad</th>
							<th>Plan terap&#233;utico</th>
							<th>Observaciones</th>
							<th>Fecha control</th>
						</tr>
						<#list documento.mantenimiento as mant>
							<tr>
							    <#if mant.fechai??>
								<td>${mant.fechai?string('dd/MM/yyyy')}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if mant.parametro??>
								<td>${mant.parametro}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if mant.periodicidad??>
								<td>${mant.periodicidad}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if mant.plan??>
								<td>${mant.plan}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if mant.observaciones??>
								<td>${mant.observaciones}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if mant.fechac??>
								<td>${mant.fechac}</td>
								<#else>
								<td bgcolor="#E3E4E4"></td>
								</#if>
							</tr>
						</#list>
					</tbody>
				</table>
			  </section>
		</component>
</#if>

<#include "CONSULTA-COMMON.ftl" />
<#include "END.ftl" />