<#include "BEGIN.ftl" />
			<#if documento.bolsasSangre?? >
					<component>
					  <section>
						<title>Unidades entregadas</title>
						<#if documento.bolsasSangre?? && documento.bolsasSangre?size != 0 >						  
							<table>
								<tbody>
									<tr>
										<th colspan="4">
											Bolsas de sangre
										</th>
									</tr>
									<tr>
										<th>Serial</th>
										<th>Grupo y factor</th>
										<th>Segmento</th>
										<th>Componente</th>
									</tr>
									<#list documento.bolsasSangre as bolsa >
										<tr>
										    <#if bolsa.serial??>
											<td>${bolsa.serial}</td>
											<#else>
											<td bgcolor="#E3E4E4"></td>
											</#if>
											<#if bolsa.grupoFactor??>
											<td>${bolsa.grupoFactor}</td>
											<#else>
											<td bgcolor="#E3E4E4"></td>
											</#if>											
											<#if bolsa.segmento??>
											<td>${bolsa.segmento}</td>
											<#else>
											<td bgcolor="#E3E4E4"></td>
											</#if>
											<#if bolsa.componente??>
											<td>${bolsa.componente}</td>
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
	        <component>
	    		<section>        		
	    		   	<title>Datos de la Transfusi&#243;n</title>		
	    		   	<#if documento.reaccionesTransfusionales?? >
	    		   	<table>
	   					<tbody> 
	   						<tr>
								<td>
									Reacciones transfusionales
								</td>
								<td>
									${documento.reaccionesTransfusionales}
								</td>
							</tr>
						</tbody>
					</table>  
					</#if> 		   		   						
				 	<paragraph>
					    <caption>
					    	Observaciones
					    </caption>
					   	${documento.observaciones}
		 			</paragraph>			 					
	    		</section>
	    	</component>
  <#include "END.ftl" />