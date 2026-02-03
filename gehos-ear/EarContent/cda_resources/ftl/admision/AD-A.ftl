<#include "../comun/BEGIN.ftl" />
            <component>
                  <section>
                    <title>
                        Datos de la admisi&#243;n
                    </title>
                   	<text>
                       <table>
                           <tbody>
                               <tr>
                                   <td>
                                      Servicio de ubicaci&#243;n
                                   </td> 
                                   <td>
                                        <#if documento.servicioUbicacion??>
                                           ${documento.servicioUbicacion}
                                       <#else>
                                           -
                                       </#if>
                                   </td>
                                   <td>
                                       Ubicaci&#243;n
                                   </td>
                                   <td>
                                      <#if documento.ubicacion??>
                                           ${documento.ubicacion}
                                       <#else>
                                           -
                                       </#if>
                                   </td>
                                   <td>
                                       Cama
                                   </td>
                                   <td>
                                       <#if documento.cama??>
                                           ${documento.cama}
                                       <#else>
                                           -
                                       </#if>
                                   </td>         
                               </tr>
                                <tr>
                                   <td>
                                       Fecha de ingreso
                                   </td>
                                   <td>
                                       <#if documento.fechaIngreso??>
                                          ${documento.fechaIngreso?string('dd/MM/yyyy')}
                                       <#else>
                                           -
                                       </#if>
                                   </td>
                                </tr>
                               
                           </tbody>
                       </table> 
                      	<#if documento.observaciones??>
							<paragraph>
						         <caption>
						          Observaciones
						         </caption>
								${documento.observaciones}
					      	</paragraph>
					  	</#if>
					  </text>
                  </section>
            </component>
 <#include "../comun/END.ftl" />