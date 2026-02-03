<#include "BEGIN.ftl" />
	<#if documento.especialidades??>
		<component>
	    	<section>
			    <title>Listado de especialidades</title>
			    <table>
					<tbody>
						<tr>
							<th colspan="3">Especialidades seleccionadas</th>
						</tr>
						<tr>
							<th>Nombre</th>
							<th>Tipo</th>
						</tr>
						<#list documento.especialidades as esp>
							<tr>
							    <#if esp.nombre??>
									<td>${esp.nombre}</td>
								<#else>
									<td bgcolor="#E3E4E4"></td>
								</#if>
								<#if esp.tipo??>
									<td>${esp.tipo}</td>
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
	<component>
    	<section>
		    <title>Interrogatorio</title>
		    <text>
	            <obs>
					<h1>Enfermedad Actual</h1>
					<#if documento.enfermedadActual??>
						<p>${documento.enfermedadActual}</p>
					<#else>
						<p>-</p>
					</#if>
				</obs>
			</text>
			<table>
            	<tbody>
                	<tr>
                    	<td>Embarazada</td> 
	                    <td>
	                    	<#if documento.embarazada??>
	                       		${documento.embarazada?string("Si", "No")}
	                    	<#else>
	                           -
	                        </#if>
	                    </td>
	                    <td>Ultima menstruación</td>
	                    <td>
	                  		<#if documento.fechaUltimaMenstruacionEmbarazada??>
	                       		${documento.fechaUltimaMenstruacionEmbarazada?string('dd/MM/yyyy')}
	                        <#else>
	                       		-
	                        </#if>
	                    </td>
	                    <td>Fecha probable del parto</td>
	                    <td>
	                   		<#if documento.fechaProbPartoEmbarazada??>
	                       		${documento.fechaProbPartoEmbarazada?string('dd/MM/yyyy')}
	                   		<#else>
	                       		-
	                       	</#if>
	                   	</td>         
	           		</tr>
	                <tr>
	                	<td>Edad gestacional</td>
	                   	<td>
	                   		<#if documento.edadGestacionalEmbarazada??>
	                      		${documento.edadGestacionalEmbarazada}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                   	<td>Paridad</td>
	                   	<td>
	                   		<#if documento.paridadEmbarazada??>
	                      		${documento.paridadEmbarazada}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                   	<td>Controles de embarazo</td>
	                   	<td>
	                   		<#if documento.cantidadControlesEmbarazada??>
	                      		${documento.cantidadControlesEmbarazada}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	                </tr>
	                <tr>
	                	<td>Riesgo</td>
	                   	<td>
	                   		<#if documento.riesgoEmbarazo??>
	                      		${documento.riesgoEmbarazo?string("Si", "No")}
	                       	<#else>
	                       		-
	                       	</#if>
	                   	</td>
	               	</tr>
       			</tbody>
   			</table>
   			
   			<#if documento.medicamentosHabituales?? && documento.medicamentosHabituales?size !=0>
	    	<component>
	      	<section>
	  			<title>Medicamentos habituales</title>
				<table>
					<tbody>
						<tr>
							<th colspan="6">Listado de presentaciones de medicamentos</th>
						</tr>
						<tr>
							<th>C&#243;digo</th>
							<th>Principio activo</th>
							<th>Concentraci&#243;n</th>
							<th>Forma farmac&#233;utica</th>
							<th>V&#237;as administraci&#243;n</th>
							<th>Dosificaci&#243;n</th>
						</tr>
					
				 		<#list documento.medicamentosHabituales as obj>
						<tr>
							<td>
								<#list obj.ppiosActivos as ppio>
							 		<#if ppio.codigoAtc??>
										${ppio.codigoAtc + "\\n"}  
							  		</#if>
					 			</#list>
							</td>
							<td>
								<#list obj.ppiosActivos as ppio>
							 		<#if ppio.ppioActivo??>
										${ppio.ppioActivo + "\\n"}
							  		</#if>
						  		</#list>
							</td>
							<td>
								<#list obj.ppiosActivos as ppio>
							 		<#if ppio.concentracion??>
										${ppio.concentracion + "\\n"}
							  		</#if>
					  			</#list>
							</td>
							<td>
								 <#if obj.formaFarmaceutica??>
									${obj.formaFarmaceutica}
							  	</#if>
							</td>
							<td>
								<#list obj.viasAdministracion as via>
							 		<#if via??>
										${via + "\\n"}
							  		</#if>
					  			</#list>
							</td>
							<td>
						 		<#if obj.dosificacion??>
									${obj.dosificacion}
						  		</#if>
							</td>
						</tr>
				 		</#list>
					</tbody>
				</table>
			</section>
	    	</component>
    		</#if>
   		</section>
	</component>
	<component>
    	<section>
		    <title>Exámen físico</title>
		    <table>
				<tbody>
					<tr>
						<th colspan="3">Hallazgos positivos</th>
					</tr>
					<tr>
						<th>Nombre</th>
						<th>Descripción</th>
					</tr>
					<#list documento.partesLesionadas as partes>
						<tr>
						    <#if partes.nombre??>
								<td>${partes.nombre}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
							<#if partes.descripcion??>
								<td>${partes.descripcion}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
						</tr>
					</#list>
					</tbody>
				</table>
		</section>
	</component>
	<component>
    	<section>
		    <title>Exámen ginecológico</title>
		    <text>
			    <obs>
					<h1>Genitales externos</h1>
					<#if documento.genitalesExternosMujer??>
						<p>${documento.genitalesExternosMujer}</p>
					<#else>
						<p>-</p>
					</#if>
				</obs>
			</text>
			<component>
    			<section>
		    		<title>Vagina</title>
		    		<table>
		            	<tbody>
		                	<tr>
		                    	<td>Tono</td> 
			                    <td>
			                    	<#if documento.tonoVaginaMujer??>
			                       		${documento.tonoVaginaMujer}
			                    	<#else>
			                           -
			                        </#if>
			                    </td>
			                    <td>Temperatura</td>
			                    <td>
			                  		<#if documento.temperaturaVaginaMujer??>
			                       		${documento.temperaturaVaginaMujer}
			                        <#else>
			                       		-
			                        </#if>
			                    </td>			                          
			           		</tr>
			           	</tbody>
		          	</table>
		    	</section>
		    </component>
		    <component>
    			<section>
		    		<title>Cuello del útero</title>
		    		<table>
		            	<tbody>
		                	<tr>
		                    	<td>Posición</td> 
			                    <td>
			                    	<#if documento.posicionCUteroMujer??>
			                       		${documento.posicionCUteroMujer}
			                    	<#else>
			                           -
			                        </#if>
			                    </td>
			                    <td>Longitud</td>
			                    <td>
			                  		<#if documento.longitudCUteroMujer??>
			                       		${documento.longitudCUteroMujer}
			                        <#else>
			                       		-
			                        </#if>
			                    </td>	
		                     	<td>Dilatación</td>
			                    <td>
			                  		<#if documento.dilatacionCUteroMujer??>
			                       		${documento.dilatacionCUteroMujer}
			                        <#else>
			                       		-
			                        </#if>
			                    </td>			                          
			           		</tr>
			           		<tr>
		                    	<td>Presentación</td> 
			                    <td>
			                    	<#if documento.presentacionCUteroMujer??>
			                       		${documento.presentacionCUteroMujer}
			                    	<#else>
			                           -
			                        </#if>
			                    </td>
			                    <td>Plano de descenso</td>
			                    <td>
			                  		<#if documento.planoDescensoCUteroMujer??>
			                       		${documento.planoDescensoCUteroMujer}
			                        <#else>
			                       		-
			                        </#if>
			                    </td>	
		                     	<td>Membranas ovulares</td>
			                    <td>
			                  		<#if documento.membranasOvularesCUteroMujer??>
			                       		${documento.membranasOvularesCUteroMujer}
			                        <#else>
			                       		-
			                        </#if>
			                    </td>			                          
			           		</tr>
			           		<tr>
		                    	<td>Características del líquido amniótico</td> 
			                    <td>
			                    	<#if documento.caractLiquidoAmnioticoCUteroMujer??>
			                       		${documento.caractLiquidoAmnioticoCUteroMujer}
			                    	<#else>
			                           -
			                        </#if>
			                    </td>
			           		</tr>
			           	</tbody>
		          	</table>
		    	</section>
		    </component>
		</section>
	</component>
	<component>
		<section>
    		<title>Diagnóstico</title>
    		<table>
				<tbody>
					<tr>
						<th colspan="3">Listado de enfermedades</th>
					</tr>
					<tr>
						<th>Código</th>
						<th>Descripción</th>
						<th>Tipo</th>
					</tr>
					<#list documento.enfermedadesImpDiag as enf>
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
    	</section>
    </component>
	
	
<#include "END.ftl" />