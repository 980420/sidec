<#macro AntecedentePON antecedentePON>
<#if antecedentePON??>
				<component>
							<section>
								<title>Antecedentes prenatales, obstétricos y neonatales</title>

								<table>
								  <tbody>
								    <tr>
								      <td>Controles</td>
								      <td>
												<#if antecedentePON.controles??>
													#{antecedentePON.controles}
												<#else>
														-
												</#if>
											</td>
								      <td>Edad gestacional</td>
								      <td>
												<#if antecedentePON.edadGestacional??>
													#{antecedentePON.edadGestacional}
												<#else>
														-
												</#if>
											</td>
								      <td>Tipo edad gestacional</td>
								      <td>
												<#if antecedentePON.tipoEdadGestacional??>
													${antecedentePON.tipoEdadGestacional}
												<#else>
														-
												</#if>
											</td>
								    </tr>
								    <tr>
								      <td>Tipo documento</td>
								      <td>
												<#if antecedentePON.tipodocumento??>
													${antecedentePON.tipodocumento}
												<#else>
														-
												</#if>
											</td>
								      <td>Asistencia</td>
								      <td>
												<#if antecedentePON.asistencia??>
													${antecedentePON.asistencia}
												<#else>
														-
												</#if>
											</td>
								      <td>Complicación embarazo</td>
								      <td>
												<#if antecedentePON.complicacionEmbarazo>
													Si
												<#else>
													No
												</#if>
											</td>
								    </tr>
								    <tr>
											<td>Complicación parto</td>
								      <td>
												<#if antecedentePON.complicacionParto>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Grupo sanguíneo madre</td>
								      <td>
												<#if antecedentePON.grupoSanguineoMadre??>
													${antecedentePON.grupoSanguineoMadre}
												<#else>
														-
												</#if>
											</td>
								      <td>Grupo sanguíneo padre</td>
								      <td>
												<#if antecedentePON.grupoSanguineoPadre??>
													${antecedentePON.grupoSanguineoPadre}
												<#else>
														-
												</#if>
											</td>
								    </tr>
								    <tr>
								      <td>Edad gestacional bebé</td>
								      <td>
												<#if antecedentePON.tipoEdadGestacionalBebe??>
													${antecedentePON.tipoEdadGestacionalBebe}
													<#if antecedentePON.edadGestacionalSemanas??>
														,&#160;${antecedentePON.edadGestacionalSemanas}&#160;semanas
													<#else>
														-
													</#if>
												<#else>
													-
												</#if>
											</td>
								    </tr>
								  </tbody>
								</table>

								<component>
								  <section>
								    <title>Test de Apgar</title>
								    <table>
								      <tbody>
								        <tr>
								          <td>Primer minuto</td>
												  <td>
														<#if antecedentePON.primerMinuto??>
															#{antecedentePON.primerMinuto}&#160;puntos
														<#else>
																-
														</#if>
													</td>
								          <td>Segundo minuto</td>
													<td>
															<#if antecedentePON.segundoMinuto??>
																#{antecedentePON.segundoMinuto}&#160;puntos
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
								      <td>Tipo respiración</td>
											<td>
													<#if antecedentePON.tipoRespiracion??>
														${antecedentePON.tipoRespiracion}
													<#else>
															-
													</#if>
												</td>
								      <td>Test Silverman</td>
											<td>
													<#if antecedentePON.testSilverman??>
														${antecedentePON.testSilverman}&#160;
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
								      <td>Cianosis</td>
								      <td>
												<#if antecedentePON.cianosis>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Malformaciones</td>
								      <td>
												<#if antecedentePON.malformaciones>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Oftalmía</td>
								      <td>
												<#if antecedentePON.oftalmia>
													Si
												<#else>
													No
												</#if>
											</td>
								    </tr>
								    <tr>
								      <td>Fiebre</td>
								      <td>
												<#if antecedentePON.fiebre>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Coriza</td>
								      <td>
												<#if antecedentePON.coriza>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Hemorragia</td>
								      <td>
												<#if antecedentePON.hemorragia>
													Si
												<#else>
													No
												</#if>
											</td>
								    </tr>
								    <tr>
								      <td>Vómitos</td>
								      <td>
												<#if antecedentePON.vomitos>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Ictericia</td>
								      <td>
												<#if antecedentePON.ictericia>
													Si
												<#else>
													No
												</#if>
											</td>
								      <td>Convulsiones</td>

								      <td>
												<#if antecedentePON.convulsiones>
													Si
												<#else>
													No
												</#if>
											</td>
								    </tr>
								  </tbody>
								</table>

								<paragraph>
								  <caption>Otros</caption>
								  ${antecedentePON.otros}
								</paragraph>

							</section>
						</component>
		</#if>
</#macro>