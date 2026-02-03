<#include "BEGIN.ftl" />
      <component>
        <section>
			<title>
           		Datos de la solicitud
           	</title>	
           	
             <table>
               <tbody>	  
               <tr>
               		<td>
               			Revisada               			
               		</td>
               		<td>
               			<#if documento.revisada??>
               				S&#237;
               			<#else>
               				No
               			</#if>            			
               		</td>
               </tr>
                </tbody>
            </table>	
              
             <component>
		        <section>
		           	<title>
		           		Listado de ex&#225;menes
		           	</title>
		           	 <#if documento.secciones?? && documento.secciones!?size &gt;0> 
		           	 	<#list documento.secciones as seccion>
			                	<list>  
			                    	<caption>${seccion.nombre}</caption>                      
			                        <#list seccion.examenes as examen>
			                        	<item>${examen}</item>		
			                        </#list>
			                    </list>		   								    		
			              </#list>
			     		  <#else>
							 <text>
								 No existe informaci&#243;n a mostrar.
							 </text>
		                </#if>	
		         </section>
    	    </component>
	        
					 <component>
					        <section>
					           	<title>
					           		Aspectos de la solicitud
					           	</title>	
					         	<#if documento.grupos?? &&documento.grupos!?size &gt;0>
					             	<#list documento.grupos as grupo>
										<table>
											<tbody>
												<tr>
													<th colspan='3'>
														${grupo.nombreDeGrupo}
													</th>
												</tr>
												
													<tr>
														<th>Aspectos</th>
														<th>Resultados</th>
														<th>Unidad de medida</th>									
													</tr>
												
												<#list grupo.resultados as aspecto>
													<tr>
														<td>
															${aspecto.nombre}
														</td>
														<td>
															${aspecto.resultado}
														</td>
														<td>
															<#if aspecto.unidadDeMedida??>
																${aspecto.unidadDeMedida}
															</#if>
														</td>									
													</tr>
												</#list>							
											</tbody>
										</table>
									</#list>
								<#else>
								 <text>
									 No existe informaci&#243;n a mostrar.
								 </text>
								</#if>
					
					        </section>
					      </component>
        </section>
      </component>
      
  
<#include "END.ftl" />