<#macro SignosVitales signosVitales>
<#if signosVitales?? >
	<#if signosVitales.peso??||
	signosVitales.talla??||
	signosVitales.DASupCorporal??||
	signosVitales.DAPesoTalla??||
	signosVitales.DAPesoIdealTalla??||
	signosVitales.DAPorcentilPeso??||
	signosVitales.DAEdadTalla??||
	signosVitales.DAIndiceMasaCorporal??||
	signosVitales.circunferenciaCefalica??||
	signosVitales.DACircunferenciaAbdominal??||
	signosVitales.DACircunferenciaCadera??||
	signosVitales.DAIndiceCinturaCadera??||
	signosVitales.diuresisHoraria??||
	signosVitales.presionVenosaCentral??||
	signosVitales.PASistolica??||
	signosVitales.PADiastolica??||
	signosVitales.PAMedia??||
	signosVitales.posturaTensionArterial??||
	signosVitales.ubicacionTensionArterial??||
	signosVitales.PValor??||
	signosVitales.caracteristicaPulso??||
	signosVitales.ubicacionPulso??||
	signosVitales.caracteristicaFR??||
	signosVitales.FRValor??||
	signosVitales.temperatura??||
	signosVitales.localizacionTemperatura??||
	signosVitales.FCValor??||
	signosVitales.saturacionO2??  >
		<component>
		      <section>
		        <title>Signos Vitales</title>
		         <component>
				      <section>
				        <title>Datos antropom&#233;tricos</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Peso</td>
										<td>
										<#if signosVitales.peso??>
											#{signosVitales.peso} kg
											<#else>
											-
										</#if></td>
										<td>Talla</td>
										<td><#if signosVitales.talla??>
											#{signosVitales.talla} cm
											<#else>
											-
										</#if></td>
										<td>Superficie corporal</td>
										<td><#if signosVitales.DASupCorporal??>
											#{signosVitales.DASupCorporal}
											<#else>
											-
										</#if></td>
									</tr>
									<tr>
										<td>Peso/Talla </td>
										<td><#if signosVitales.DAPesoTalla??>
											#{signosVitales.DAPesoTalla} kg
											<#else>
											-
										</#if></td>
										<td>Peso ideal para la talla</td>
										<td><#if signosVitales.DAPesoIdealTalla??>
											#{signosVitales.DAPesoIdealTalla} kg
											<#else>
											-
										</#if></td>
										<td>Percentil de peso</td>
										<td><#if signosVitales.DAPorcentilPeso??>
											#{signosVitales.DAPorcentilPeso}
											<#else>
											-
										</#if></td>
									</tr>
									<tr>
										<td>Edad/Talla</td>
										<td><#if signosVitales.DAEdadTalla??>
											#{signosVitales.DAEdadTalla}
											<#else>
											-
										</#if></td>
										<td>&#205;ndice de masa corporal</td>
										<td><#if signosVitales.DAIndiceMasaCorporal??>
											#{signosVitales.DAIndiceMasaCorporal} cm
											<#else>
											-
										</#if></td>
										<td>Circunferencia cef&#225;lica</td>
										<td><#if signosVitales.circunferenciaCefalica??>
											#{signosVitales.circunferenciaCefalica} cm
											<#else>
											-
										</#if></td>
									</tr>
									<tr>
										<td>Circunferencia abdominal</td>
										<td><#if signosVitales.DACircunferenciaAbdominal??>
											#{signosVitales.DACircunferenciaAbdominal} cm
											<#else>
											-
										</#if></td>
										<td>Circunferencia de cadera</td>
										<td><#if signosVitales.DACircunferenciaCadera??>
											#{signosVitales.DACircunferenciaCadera} cm
											<#else>
											-
										</#if></td>
										<td>&#205;ndice de cintura cadera </td>
										<td><#if signosVitales.DAIndiceCinturaCadera??>
											#{signosVitales.DAIndiceCinturaCadera}
											<#else>
											-
										</#if></td>
									</tr>
									<tr>
										<td>Diuresis horaria</td>
										<td><#if signosVitales.diuresisHoraria??>
											#{signosVitales.diuresisHoraria} mL/24h
											<#else>
											-
										</#if></td>
										<td>Presi&#243;n venosa central </td>
										<td colspan="3"><#if signosVitales.presionVenosaCentral??>
											#{signosVitales.presionVenosaCentral} mmHg
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
				 
				 
				 <component>
				      <section>
				        <title>Tensi&#243;n arterial</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Sist&#243;lica</td>
										<td><#if signosVitales.PASistolica??>
											#{signosVitales.PASistolica} mmHg
											<#else>
											-
										</#if></td>
										<td>Diast&#243;lica</td>
										<td><#if signosVitales.PADiastolica??>
											#{signosVitales.PADiastolica} mmHg
											<#else>
											-
										</#if></td>
										<td>Media</td>
										<td><#if signosVitales.PAMedia??>
											#{signosVitales.PAMedia}
											<#else>
											-
										</#if></td>
									</tr>
									<tr>
										<td>Postura </td>
										<td><#if signosVitales.posturaTensionArterial??>
											${signosVitales.posturaTensionArterial}
											<#else>
											-
										</#if></td>
										<td>Ubicaci&#243;n</td>
										<td colspan="3"><#if signosVitales.ubicacionTensionArterial??>
											${signosVitales.ubicacionTensionArterial}
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
					  	</text>
				      </section>
				 </component>
				 
				  <component>
				      <section>
				        <title>Pulso</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Valor</td>
										<td><#if signosVitales.PValor??>
											#{signosVitales.PValor} ppm
											<#else>
											-
										</#if></td>
										<td>Caracter&#237;sticas</td>
										<td><#if signosVitales.caracteristicaPulso??>
											${signosVitales.caracteristicaPulso}
											<#else>
											-
										</#if></td>
										<td>Ubicaci&#243;n</td>
										<td><#if signosVitales.ubicacionPulso??>
											${signosVitales.ubicacionPulso}
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
				 
				 <component>
				      <section>
				        <title>Frecuencia respiratoria</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Caracter&#237;sticas</td>
										<td><#if signosVitales.caracteristicaFR??>
											${signosVitales.caracteristicaFR}
											<#else>
											-
										</#if></td>
										<td>Valor</td>
										<td><#if signosVitales.FRValor??>
											#{signosVitales.FRValor} rpm
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
				 
				 <component>
				      <section>
				        <title>Temperatura</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Temperatura</td>
										<td><#if signosVitales.temperatura??>
											#{signosVitales.temperatura}&#176;C
											<#else>
											-
										</#if></td>
										<td>Localizaci&#243;n</td>
										<td><#if signosVitales.localizacionTemperatura??>
											${signosVitales.localizacionTemperatura}
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
				 
				 <component>
				      <section>
				        <title>Frecuencia cardiaca</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Valor</td>
										<td><#if signosVitales.FCValor??>
											#{signosVitales.FCValor} lpm
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
				 
				 <component>
				      <section>
				        <title>Saturaci&#243;n de O2</title>
				        <text>
					        <table>
								<tbody>
									<tr>
										<td>Valor</td>
										<td><#if signosVitales.saturacionO2??>
											#{signosVitales.saturacionO2}&#37;
											<#else>
											-
										</#if></td>
									</tr>
								</tbody>
					        </table>
				  		</text>
				      </section>
				 </component>
		      </section>
		 </component>
 	</#if>
 </#if>
 </#macro>