<#macro AntecedenteGO antecedenteGO>
<#if antecedenteGO??>
<component>
	<section>
		<title>Antecedentes ginecológicos y obstétricos</title>
		<text>
			<table>
				<tbody>
			  		<tr>
						<td>Menarquía</td>
						<td>
						<#if antecedenteGO.menarquia??>
								${antecedenteGO.menarquia}
						<#else>
									-
						</#if>
						</td>
						<td>Ciclo menstrual</td>
						<td>
						<#if antecedenteGO.cicloMenstrualA??>
								#{antecedenteGO.cicloMenstrualA}
						<#else>
									-
						</#if>
							/
						<#if antecedenteGO.cicloMenstrualD??>
								#{antecedenteGO.cicloMenstrualD}
						<#else>
									-
						</#if>
						</td>
						<td>Tipo regla</td>
						<td>
							<#if antecedenteGO.tipoRegla??>
									${antecedenteGO.tipoRegla}
							<#else>
										-
							</#if>
						</td>
			  		</tr>
			  		<tr>
						<td>Primera relación sexual</td>
						<td>
							<#if antecedenteGO.primeraRelacionSexual??>
									${antecedenteGO.primeraRelacionSexual}
							<#else>
										-
							</#if>
						</td>
						<td>Número parejas</td>
						<td>
							<#if antecedenteGO.numeroParejas??>
									#{antecedenteGO.numeroParejas}
							<#else>
										-
							</#if>
						</td>
						<td>Dispaurenia</td>
						<td>
							<#if antecedenteGO.dispaurenia>
									Si
							<#else>
									No
							</#if>
						</td>
			  		</tr>
			  		<tr>
						<td>Fecha última citología</td>
						<td>
							<#if antecedenteGO.ultimaCitología??>
										${antecedenteGO.ultimaCitología?string("dd/MM/yyyy")}
							<#else>
										-
							</#if>
						</td>
						<td>Usa MACO</td>
						<td>
							<#if antecedenteGO.usaMACO>
									Si
							<#else>
									No
							</#if>
						</td>
				  	</tr>
			  		<tr>
						<td>Fecha última regla (FUR)</td>
						<td>
							<#if antecedenteGO.ultimaRegla??>
										${antecedenteGO.ultimaRegla?string("dd/MM/yyyy")}
							<#else>
										-
							</#if>
						</td>
						<td>Fecha probable parto</td>
						<td>
							<#if antecedenteGO.probableParto??>
										${antecedenteGO.probableParto?string("dd/MM/yyyy")}
							<#else>
										-
							</#if>
						</td>
						<td>Número gestas</td>
						<td>
							<#if antecedenteGO.cantGestas??>
									#{antecedenteGO.cantGestas}
							<#else>
										-
							</#if>
						</td>
			  		</tr>
			  		<tr>
						<td>Número partos</td>
						<td>
							<#if antecedenteGO.cantPartos??>
									#{antecedenteGO.cantPartos}
							<#else>
										-
							</#if>
						</td>
						<td>Número cesáreas</td>
						<td>
							<#if antecedenteGO.cesareas??>
									#{antecedenteGO.cesareas}
							<#else>
										-
							</#if>
						</td>
						<td>Número hijos vivos</td>
						<td>
							<#if antecedenteGO.hijosVivos??>
									#{antecedenteGO.hijosVivos}
							<#else>
										-
							</#if>
						</td>
			  		</tr>
			  		<tr>
						<td>Número hijos varones</td>
						<td>
							<#if antecedenteGO.varones??>
									#{antecedenteGO.varones}
							<#else>
										-
							</#if>
						</td>
						<td>Número hijos hembras</td>
						<td>
							<#if antecedenteGO.hembras??>
									#{antecedenteGO.hembras}
							<#else>
										-
							</#if>
						</td>
						<td>Máximo peso fetal (PMF)</td>
						<td>
							<#if antecedenteGO.pesoFetalMax??>
									#{antecedenteGO.pesoFetalMax} gr
							<#else>
										-
							</#if>
						</td>
			  		</tr>
			  		<tr>
						<td>Fecha último parto (FUP)</td>
						<td>
							<#if antecedenteGO.ultimoParto??>
										${antecedenteGO.ultimoParto?string("dd/MM/yyyy")}
							<#else>
										-
							</#if>
						</td>
						<td>Número abortos</td>
						<td>
							<#if antecedenteGO.abortos??>
									#{antecedenteGO.abortos}
							<#else>
										-
							</#if>
						</td>
						<td>Número ectópicos</td>
						<td>
							<#if antecedenteGO.ectopicos??>
									#{antecedenteGO.ectopicos}
							<#else>
										-
							</#if>
						</td>
			  		</tr>
				</tbody>	
			</table>
	  


	  <component>
		<section>
		  <title>Partos</title>
			<#if antecedenteGO.partos??>
				<#list antecedenteGO.partos as parto>
					<component>
						<section>
							<title>${parto.fecha?string("dd/MM/yyyy")}</title>

						  <table>
							<tbody>
							  <tr>
								<td>Tipo de parto</td>
								<td>
									<#if parto.tipoParto??>
											${parto.tipoParto}
									<#else>
											-
									</#if>
								</td>
							  </tr>
							</tbody>
						  </table>


						  <table>
							<tbody>
							  <tr>
								<td>Hemorragia</td>
								<td>
									<#if parto.hemorragia>
											Si
									<#else>
											No
									</#if>
								</td>
								<td>Lesión perineal</td>
								<td>
									<#if parto.lesionPerineal>
											Si
									<#else>
											No
									</#if>
								</td>
								<td>Enfermedades hipertensivas embarazo</td>
								<td>
									<#if parto.hipertensionEmbarazo>
											Si
									<#else>
											No
									</#if>
								</td>
							  </tr>
							  <tr>
								<td>Número nacidos</td>
								<td>
									<#if parto.numeroNacidos??>
										#{parto.numeroNacidos}
									<#else>
											-
									</#if>
								</td>
							  </tr>
							</tbody>
						  </table>

						
							  <table>
							   <tbody>
								<tr>
								  <th colspan="3">Datos recién nacido</th>
								</tr>
								<tr>
								  <th>Sexo</th>
								  <th>Estado</th>
								  <th>Peso (gr)</th>
								</tr>
							   <#list parto.recienNacidos as recienNacido>
								<tr>
								  <td>${recienNacido.sexo}</td>
								  <td>${recienNacido.estado}</td>
								  <td>#{recienNacido.peso}</td>
								</tr>
								</#list>
								</tbody>
							  </table>
						

						  <table>
							<tbody>
							  <tr>
								<td>Puerperio</td>
								<td>
								<#if parto.puerperio??>
									<#if parto.puerperio>
										Complicado
									<#else>
										No complicado
									</#if>
									<#else>
									-
								</#if>
								</td>
							  </tr>
							</tbody>
						  </table>
					</section>
				  </component> 
				</#list>
			</#if>

		</section>
	  </component>   
	  	</text>
	</section>
  </component>
  </#if>
</#macro>