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

<component>
	  <section>
		<title>Datos generales</title>
			<component>
				  <section>
					<title>Caracter&#237;stica de la regla</title>
					<table>
						<tbody>
							<tr>
								
									<td>
										Dismenorrea
									</td>
									<td>			   
										<#if documento.dismenorrea??&&documento.dismenorrea>
											Si.
										<#else>
											No.
										</#if>
									</td>
									
									<td>
										Metrorragia
									</td>
									<td>
										<#if documento.metrorragia??&&documento.metrorragia>
											Si.
										<#else>
											No.
										</#if>
									</td>
									<td>
										Hipermenorragia
									</td>
									<td>
										<#if documento.hipermenorragia??&&documento.hipermenorragia>
											Si.
										<#else>
											No.
										</#if>
									</td>
									<td>
										Menorragia
									</td>
									<td>
										<#if documento.menorragia??&&documento.menorragia>
											Si.
										<#else>
											No.
										</#if>
									</td>
									<td>
										Oligomenorrea
									</td>
									<td>
										<#if documento.oligomenorrea??&&documento.oligomenorrea>
											Si.
										<#else>
											No.
										</#if>
									</td>
									<td>
										Polimenorrea
									</td>
									<td>
										<#if documento.polimenorrea??&&documento.polimenorrea>
											Si.
										<#else>
											No.
										</#if>
									</td>
									<td>
										Eumenorrea
									</td>
									<td>
										<#if documento.eumenorrea??&&documento.eumenorrea>
											Si.
										<#else>
											No.
										</#if>
									</td>
								
							</tr>
						</tbody>
					</table>
						<paragraph>
							<caption>
								Observaciones
							</caption>
							<#if documento.observacionesGen??>
								${documento.observacionesGen}
								<#else>
								Ninguna.
							</#if>
						</paragraph>
				  </section>
			</component>
			<#if documento.comportamiento??&&documento.comportamiento?size !=0>
			<component>
				  <section>
					<title>Comportamiento de la menstruaci&#243;n</title>
			<table>
				<tbody>
					<tr>
						<th colspan="4">Seguimiento</th>
					</tr>
					<tr>
						<th>Fecha</th>
						<th width="40px">Hiper</th>
						<th width="40px">EUM</th>
						<th width="40px">Hipo</th>
					</tr>
					<#list documento.comportamiento as it>
					<tr>
						<td>${it.fecha?string("dd/MM/yyyy")}
						</td>
						<#if it.tipo??>
							<#if it.tipo>
								<td></td>
								<td bgcolor="#EDA641"></td>
								<td></td>
							<#else>
								<td></td>
								<td></td>
								<td bgcolor="#719F55"></td>
							</#if>
						<#else>
							<td bgcolor="#7b1204"></td>
							<td></td>
							<td></td>
						</#if>
					</tr>
					</#list>
				</tbody>
			</table>
		
		<paragraph>
			<caption>
				Observaciones
			</caption>
			<#if documento.observacionesMes??>
				${documento.observacionesMes}
				<#else>
				Ninguna.
			</#if>
		</paragraph>
		</section>
	</component>
	</#if>
  </section>
</component>
<@session lista=documento.list/>



<#include "CONSULTA-COMMON.ftl" />
<#include "END.ftl" />