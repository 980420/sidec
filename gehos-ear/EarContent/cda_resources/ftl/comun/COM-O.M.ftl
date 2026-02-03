<component>
	<section>
		<title>Datos de la orden m&#233;dica</title>
		<text>
	       	<obs>
				<h1>Observaciones</h1>
				<#if documento.observaciones??>
					<p>${documento.observaciones}</p>
				<#else>
					<p>-</p>
				</#if>
			</obs>
		</text>
		
		<component>
			<section>
				<title>Orden de dieta</title>
				<table>
        			<tbody>
            			<tr>
                			<td>Tipo de dieta</td> 
	            			<td>
	                			<#if documento.tipoDieta??>
	                    			${documento.tipoDieta}
	                    		<#else>
	                    			-
	                    		</#if>
	               			</td>
	          			</tr>
	       			</tbody>
	    		</table> 
	    		<text>
	       			<obs>
						<h1>Observaciones</h1>
						<#if documento.observacionesDieta??>
							<p>${documento.observacionesDieta}</p>
						<#else>
							<p>-</p>
						</#if>
					</obs>
				</text>
	      	</section>
		</component>		
		
		<component>
			<section>
				<title>Oxigenoterapia</title>
				<table>
        			<tbody>
            			<tr>
                			<td>Flujometr&#237;a</td> 
	            			<td>
	                			<#if documento.flujometria??>
	                    			${documento.flujometria}
	                    		<#else>
	                    			-
	                    		</#if>
	               			</td>
	               			<td>V&#237;a</td> 
	            			<td>
	                			<#if documento.viaOxigenoterapia??>
	                    			${documento.viaOxigenoterapia}
	                    		<#else>
	                    			-
	                    		</#if>
	               			</td>
	          			</tr>
	          		</tbody>
	    		</table>    
			</section>
		</component>
		
		<#if documento.hidratacionesParenterales?? && documento.hidratacionesParenterales?size !=0>
			<component>
	    		<section>
			    	<title>Hidrataci&#243;n parenteral</title>
			    	<table>
						<tbody>
							<tr>
								<th colspan="6">Listado de hidrataciones parenterales indicadas</th>
							</tr>
							<tr>
								<th>Tipo soluci&#243;n</th>
								<th>Cantidad l&#237;quido (cc)</th>
								<th>V&#237;a</th>
								<th>Frecuencia</th>
								<th>Velocidad infusi&#243;n</th>
								<th>Fecha suspensi&#243;n</th>
							</tr>
							<#list documento.hidratacionesParenterales as obj>
								<tr>
							    	<#if obj.tipoSolucion??>
										<td>${obj.tipoSolucion}</td>
									<#else>
										<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if obj.cantidadLiquido??>
										<td>${obj.cantidadLiquido}</td>
									<#else>
										<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if obj.via??>
										<td>${obj.via}</td>
									<#else>
										<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if obj.frecuencia??>
										<td>${obj.frecuencia}</td>
									<#else>
										<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if obj.velocidadInfusion??>
										<td>${obj.velocidadInfusion}</td>
									<#else>
										<td bgcolor="#E3E4E4"></td>
									</#if>
									<#if obj.fechaSuspension??>
										<td>${obj.fechaSuspension?string('dd/MM/yyyy')}</td>
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
		
		<#if documento.nutricionParenteral?? && documento.nutricionParenteral.componentes?? && documento.nutricionParenteral.componentes?size !=0>
			<component>
	    		<section>
			    	<title>Nutrici&#243;n parenteral</title>
					<component>
	    				<section>
			    			<title>Componentes de nutrici&#243;n parenteral indicados</title>
			    			<table>
								<tbody>
									<tr>
										<th colspan="3">Listado de componentes de nutrici&#243;n parenteral indicados</th>
									</tr>
									<tr>
										<th>Componente</th>
										<th>Cantidad (cc)</th>
										<th>Porciento (%)</th>
									</tr>
									<#list documento.nutricionParenteral.componentes as obj>
										<tr>
							    			<#if obj.componente??>
												<td>${obj.componente}</td>
											<#else>
												<td bgcolor="#E3E4E4"></td>
											</#if>
											<#if obj.cantidad??>
												<td>${obj.cantidad}</td>
											<#else>
												<td bgcolor="#E3E4E4"></td>
											</#if>
											<#if obj.porciento??>
												<td>${obj.porciento}</td>
											<#else>
												<td bgcolor="#E3E4E4"></td>
											</#if>
										</tr>	
									</#list>
								</tbody>
							</table>
							<table>
            					<tbody>
                					<tr>
                    					<td>Volumen total</td> 
	                    				<td>
	                    					<#if documento.nutricionParenteral.volumenTotal??>
	                       						${documento.nutricionParenteral.volumenTotal}
	                    					<#else>
	                           					-
	                        				</#if>
	                    				</td>
	                    				<td>Total de calor&#237;as</td>
	                    				<td>
	                  						<#if documento.nutricionParenteral.totalCalorias??>
	                       						${documento.nutricionParenteral.totalCalorias}
	                        				<#else>
	                       						-
	                        				</#if>
	                    				</td>
	                    				<td>Total de N2</td>
	                    				<td>
	                   						<#if documento.nutricionParenteral.totalDinitrogeno??>
	                       						${documento.nutricionParenteral.totalDinitrogeno}
	                   						<#else>
	                       						-
	                       					</#if>
	                   					</td>         
	           						</tr>
	       						</tbody>
   							</table>
   						</section>
					</component>
					<table>
            			<tbody>
                			<tr>
                    			<td>Velocidad infusi&#243;n</td> 
	                    		<td>
	                    			<#if documento.nutricionParenteral.velocidadInfusion??>
	                       				${documento.nutricionParenteral.velocidadInfusion}
	                    			<#else>
	                           			-
	                        		</#if>
	                    		</td>
	                    		<td>Frecuencia</td>
	                    		<td>
	                  				<#if documento.nutricionParenteral.frecuencia??>
	                       				${documento.nutricionParenteral.frecuencia}
	                        		<#else>
		                       			-
	                       			</#if>
	                 			</td>
	                    		<td>Fecha suspensi&#243;n</td>
	                    		<td>
		                   			<#if documento.nutricionParenteral.fechaSuspension??>
	                       				${documento.nutricionParenteral.fechaSuspension?string('dd/MM/yyyy')}
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
		
		<#if documento.presentacionMedicamentosIndicados?? && documento.presentacionMedicamentosIndicados?size !=0>
	    <component>
	      <section>
	  		<title>Indicaci&#243;n de medicamentos</title>
			<table>
				<tbody>
					<tr>
						<th colspan="12">Listado de presentaciones de medicamentos indicados</th>
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
						<th>Stat</th>
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
									${via + "\\n"}
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
						 	<#if obj.stat??>
								${obj.stat?string('Sí','No')}
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
		  </section>
	    </component>
    	</#if>
    	
    	<#if documento.formulasOficinales?? && documento.formulasOficinales?size !=0>
	    <component>
	      <section>
	  		<title>F&#243;rmulas oficinales</title>
			<table>
				<tbody>
					<tr>
						<th colspan="5">Listado de f&#243;rmulas oficinales indicadas</th>
					</tr>
					<tr>
						<th>Nombre</th>
						<th>Dosis</th>
						<th>V&#237;a administraci&#243;n</th>
						<th>Frecuencia</th>
						<th>Fecha suspensión</th>
					</tr>
					
				 	<#list documento.formulasOficinales as obj>
					<tr>
						<td>
						 	<#if obj.nombre??>
								${obj.nombre}
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
						 	<#if obj.fechaSuspension??>
								${obj.fechaSuspension?string('dd/MM/yyyy')}
						  	</#if>
						</td>
					</tr>
				 	</#list>
				</tbody>
			</table>
		  </section>
	    </component>
    	</#if>
    	
    	<#if documento.formulasMagistrales?? && documento.formulasMagistrales?size !=0>
	    <component>
	      <section>
	  		<title>F&#243;rmulas magistrales</title>
			<component>
	      		<section>
	  				<title>Listado de f&#243;rmulas magistrales indicadas</title>
					<#list documento.formulasMagistrales as obj>
						<component>
	      					<section>
	  							<title>
	  								<#if obj.nombre??>
										${obj.nombre}
						  			</#if>
						  		</title>
						  		<table>
						  			<tbody>
						  				<tr>
						  					<td>Dosis</td>
											<td>
						 						<#if obj.dosis??>
													${obj.dosis}
						  						</#if>
											</td>
											<td>V&#237;a administraci&#243;n</td>
											<td>
						 						<#if obj.via??>
													${obj.via}
						  						</#if>
											</td>
											<td>Frecuencia</td>
											<td>
						 						<#if obj.frecuencia??>
													${obj.frecuencia}
						  						</#if>
											</td>
											<td>Fecha suspensión</td>
											<td>
						 						<#if obj.fechaSuspension??>
													${obj.fechaSuspension?string('dd/MM/yyyy')}
						  						</#if>
											</td>
										</tr>
						  			</tbody>
						  		</table>
						  		<#if obj.presentaciones?? && obj.presentaciones?size !=0>
									<table>
										<tbody>
											<tr>
												<th colspan="7">Listado de presentaciones de medicamentos</th>
											</tr>
											<tr>
												<th>C&#243;digo</th>
												<th>Principio activo</th>
												<th>Concentraci&#243;n</th>
												<th>Forma farmac&#233;utica</th>
												<th>V&#237;as administraci&#243;n</th>
												<th>Dosificaci&#243;n</th>
												<th>%</th>
											</tr>
											<#list obj.presentaciones as pres>
												<tr>
													<td>
														<#list pres.presentacion.ppiosActivos as ppio>
							 								<#if ppio.codigoAtc??>
																${ppio.codigoAtc + "\\n"}  
							  								</#if>
					 									</#list>
													</td>
													<td>
														<#list pres.presentacion.ppiosActivos as ppio>
							 								<#if ppio.ppioActivo??>
																${ppio.ppioActivo + "\\n"}
							  								</#if>
						  								</#list>
													</td>
													<td>
														<#list pres.presentacion.ppiosActivos as ppio>
							 								<#if ppio.concentracion??>
																${ppio.concentracion + "\\n"}
							  								</#if>
					  									</#list>
													</td>
													<td>
							 							<#if pres.presentacion.formaFarmaceutica??>
															${pres.presentacion.formaFarmaceutica}
							  							</#if>
													</td>
													<td>
														<#list pres.presentacion.viasAdministracion as via>
							 								<#if via??>
																${via + "\\n"}
							  								</#if>
					  									</#list>
													</td>
													<td>
						 								<#if pres.presentacion.dosificacion??>
															${pres.presentacion.dosificacion}
						  								</#if>
													</td>
													<td>
						 								<#if pres.concentracion??>
															${pres.concentracion}
						  								</#if>
													</td>
												</tr>
				 							</#list>	
										</tbody>
									</table>
								</#if>
								<#if obj.concentraciones?? && obj.concentraciones?size !=0>
									<table>
										<tbody>
											<tr>
												<th colspan="4">Listado de productos</th>
											</tr>
											<tr>
												<th>C&#243;digo</th>
												<th>Nombre</th>
												<th>Presentaci&#243;n</th>
												<th>%</th>
											</tr>
											<#list obj.concentraciones as conc>
												<tr>
													<td>
														<#if conc.codigo??>
															${conc.codigo}  
							  							</#if>
					 								</td>
													<td>
														<#if conc.nombre??>
															${conc.nombre}  
							  							</#if>
					 								</td>
													<td>
														<#if conc.presentacion??>
															${conc.presentacion}  
							  							</#if>
					 								</td>
													<td>
						 								<#if conc.concentracion??>
															${conc.concentracion}
						  								</#if>
													</td>
												</tr>
				 							</#list>	
										</tbody>
									</table>
								</#if>
							</section>
	    				</component>
					</#list>
				</section>
			</component>		
		  </section>
		</component>	
		</#if>					
		
		<#if documento.tratamientosMedicosIndicados?? && documento.tratamientosMedicosIndicados?size !=0>
	    <component>
	      <section>
	  		<title>Indicación de otros tratamientos médicos</title>
			<table>
				<tbody>
					<tr>
						<th colspan="4">Listado de tratamientos médicos indicados</th>
					</tr>
					<tr>
						<th>Descripción</th>
						<th>Vía</th>
						<th>Frecuencia</th>
						<th>Fecha suspensión</th>
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
					</tr>
				 </#list>
				</tbody>
			</table>
	      </section>
	    </component>
    	</#if>
    
    	<#if documento.nebuloterapias?? && documento.nebuloterapias?size !=0>
	    <component>
	      <section>
	  		<title>Nebuloterapia</title>
			<table>
				<tbody>
					<tr>
						<th colspan="9">Listado de nebuloterapias indicadas</th>
					</tr>
					<tr>
						<th>C&#243;digo</th>
						<th>Principio activo</th>
						<th>Concentraci&#243;n</th>
						<th>Forma farmac&#233;utica</th>
						<th>V&#237;as administraci&#243;n</th>
						<th>Dosificaci&#243;n</th>
						<th>Dosis</th>
						<th>Frecuencia</th>
						<th>Fecha suspensión</th>
					</tr>
					
				 	<#list documento.nebuloterapias as obj>
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
									${via + "\\n"}
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
						 	<#if obj.frecuencia??>
								${obj.frecuencia}
						  	</#if>
						</td>
						<td>
						 	<#if obj.fechaSuspencion??>
								${obj.fechaSuspencion?string('dd/MM/yyyy')}
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