<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="CDA.xsl"?>
<ClinicalDocument xmlns="urn:hl7-org" classCode="DOCCLIN" moodCode="EVN">

	<!-- Estos valores son fijos -->
	<typeId root="2.16.840.1.113883.1.3" extension="POCD_HD000040" />

	<!-- Este es un identificador unico para el documento, que deberemos generar.
	Atributo root: Número de registro maestro para documentos clínicos en la organización o institución hospitalaria.
	Atributo extension: Número generado.
	El par root-extension tiene que ser universalemente único -->
	<id root="${documento.oidDocumentoClinico}" extension="${documento.idUnico}" />

	<!-- Este es un identificador único que referencia a la plantilla del documento.
	Atributo root: Número de registro maestro para plantillas de documentos clínicos en la organización o institución hospitalaria. 
	Atributo extension: Nomenclador propio de la plantilla a la que se hace referencia-->
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="BQ-H.AE" />

	<!-- Código para este tipo de documento según el estándar LOINC.
	Atributo code: Código en cuestión. -->
	<code code="${documento.codigoLOINC}" codeSystem="2.16.840.1.113883.6.1" codeSystemName="LOINC" />

	<!-- Título del documento. -->
	<title>${documento.titulo}</title>

	<!-- Fecha y hora de creación del documento original, en formato ISO 8601 -->
	<effectiveTime value="${fechaHoraFormateador.format(documento.fechaHoraCreacion)}" />

	<!-- Nivel de confidencialidad. 
	Posibles valores del atributo code: 
	N:  Normal
	R:  Restringido  
	V:  Muy restringido -->
	<confidentialityCode code="${documento.confidencialidad}" codeSystem="2.16.840.1.113883.5.25" />

	<!-- En esta parte van los datos del paciente -->
	<recordTarget>
		<patientRole>

			<!-- El id del paciente va a ser el numero de HCE.
			Atributo root: Registro maestro para números de historias clínicas
			Atributo extension: Número propio de la HCE del paciente -->
			<id root="${documento.paciente.hcRoot}" extension="${documento.paciente.hcExtension}" />
          
			<!-- Direccion de residencia del paciente -->
			<#if documento.paciente.lugarResidencia??>      
			<addr>
				<#if documento.paciente.lugarResidencia.pais??>
					<country>${documento.paciente.lugarResidencia.pais}</country>
				</#if>
				<#if documento.paciente.lugarResidencia.estado??>
					<state>${documento.paciente.lugarResidencia.estado}</state>
				</#if>
				<#if documento.paciente.lugarResidencia.ciudad??>
					<city>${documento.paciente.lugarResidencia.ciudad}</city>
				</#if>
				<#if documento.paciente.lugarResidencia.pueblo??>
					<censusTract>${documento.paciente.lugarResidencia.pueblo}</censusTract>
				</#if>
				<#if documento.paciente.lugarResidencia.tipoDeCalle??>
					<streetNameType>${documento.paciente.lugarResidencia.tipoDeCalle}</streetNameType>
				</#if>
				<#if documento.paciente.lugarResidencia.calle??>
					<streetNameBase>${documento.paciente.lugarResidencia.calle}</streetNameBase>
				</#if>
				<#if documento.paciente.lugarResidencia.numeroCasa??>
					<houseNumber>${documento.paciente.lugarResidencia.numeroCasa}</houseNumber>
				</#if>
			</addr>
			</#if>   

      		<#if documento.paciente.telefonos??>
				<#if documento.paciente.telefonos?size != 0 >
					<#list documento.paciente.telefonos as telefono>
						<!-- Telefonos del paciente si tiene -->
						<telecom value="${telefono}" />
					</#list>
				</#if>
			</#if>

			<patient>
				
				<#if documento.paciente.nombre?? || documento.paciente.apellido1?? || documento.paciente.apellido2??>
					<!-- Nombre y apellidos del paciente -->
					<name>
						<#if documento.paciente.nombre??>
							<given>${documento.paciente.nombre}</given>
						</#if>
						<#if documento.paciente.apellido1??>
							<family>${documento.paciente.apellido1}</family>
						</#if>
						<#if documento.paciente.apellido2??>
							<family>${documento.paciente.apellido2}</family>
						</#if>
					</name>
				</#if>
	    
				<!-- Género del paciente 
				Posibles valores del atributo code: ver vocabulario HL7 v3 -->
				<administrativeGenderCode code="${documento.paciente.genero}" codeSystem="2.16.840.1.113883.5.1" />
        		
        		<#if documento.paciente.fechaNacimiento??>
					<!-- Fecha y hora de nacimiento del paciente
					en formato ISO 8601 -->
					<birthTime value="${fechaFormateador.format(documento.paciente.fechaNacimiento)}"/>
				</#if>
       
				<#if documento.paciente.estadoCivil??>
					<!-- Estado civil 
					Posibles valores del atributo code: ver vocabulario HL7 v3 --> 
					<maritalStatusCode code="${documento.paciente.estadoCivil}" codeSystem="2.16.840.1.113883.5" /> 
				</#if>

    			<#if documento.paciente.raza??>
					<!-- Codigo de raza 
					Posibles valores del atributo code: ver vocabulario HL7 v3 -->  
					<raceCode code="${documento.paciente.raza}" codeSystem="2.16.840.1.113883.5" />
    			</#if>

				<#if documento.paciente.etnia??>
					<!-- Etnia
					Posibles valores del atributo code: ver vocabulario HL7 v3 -->
					<ethnicGroupCode code="${documento.paciente.etnia}" codeSystem="2.16.840.1.113883.5" />
				</#if>        			
				
				<#if documento.paciente.direccionNacimiento??>
					<!-- Lugar de nacimiento -->
					<birthplace>
						<place>
							<addr>
								<#if documento.paciente.direccionNacimiento.pais??>
									<country>${documento.paciente.direccionNacimiento.pais}</country>
								</#if>
								<#if documento.paciente.direccionNacimiento.estado??>
									<state>${documento.paciente.direccionNacimiento.estado}</state>
								</#if>
								<#if documento.paciente.direccionNacimiento.ciudad??>
									<city>${documento.paciente.direccionNacimiento.ciudad}</city>
								</#if>
								<#if documento.paciente.direccionNacimiento.pueblo??>
									<censusTract>${documento.paciente.direccionNacimiento.pueblo}</censusTract>
								</#if>
								<#if documento.paciente.direccionNacimiento.tipoCalle??>
									<streetNameType>${documento.paciente.direccionNacimiento.tipoCalle}</streetNameType>
								</#if>
								<#if documento.paciente.direccionNacimiento.calle??>
									<streetNameBase>${documento.paciente.direccionNacimiento.calle}</streetNameBase>
								</#if>
								<#if documento.paciente.direccionNacimiento.numeroCasa??>
									<houseNumber>${documento.paciente.direccionNacimiento.numeroCasa}</houseNumber>
								</#if>
							</addr>
						</place>
					</birthplace>  
				</#if>	        
			</patient>
		</patientRole>
	</recordTarget>
	
	<#if documento.autor??>
		<!-- Datos del autor del documento -->
		<author>
	
			<!-- Fecha y hora en que lo crea, en formato ISO 8601 -->
			<time value="${fechaHoraFormateador.format(documento.fechaHoraAutoria)}" />
	
			<!-- Datos del autor del documento -->
			<assignedAuthor>
				<#if documento.autor.idRoot?? && documento.autor.idExtension??>
					<!-- Identificador del autor.
					Atributo root: Registro maestro para identificadores de médicos
					Atributo extension: Matrícula médica  -->
					<id root="${documento.autor.idRoot}" extension="${documento.autor.idExtension}" />
				</#if>
				
				<#if documento.autor.prefijo?? || documento.autor.nombre??  || documento.autor.apellido1?? || documento.autor.apellido2??>
					<!-- Nombre de médico, enfermera o técnico autor del documento -->
					<assignedPerson>
						<name>
							<#if documento.autor.prefijo??>
								<prefix>${documento.autor.prefijo}</prefix>
							</#if>
							<#if documento.autor.nombre??>
				 				<given>${documento.autor.nombre}</given>
							</#if>
				 			<#if documento.autor.apellido1??>
				 				<family>${documento.autor.apellido1}</family>
							</#if>
				 			<#if documento.autor.apellido2??>
				 				<family>${documento.autor.apellido2}</family>
							</#if>
						</name>
					</assignedPerson>
				</#if>
				
				<#if documento.hospital??>
					<!-- Identificador y nombre del hospital para el cual el médico trabaja -->
					<representedOrganization>
						<#if documento.hospital.id??>
							<id root="${documento.hospital.id}" />
						</#if>
						<#if documento.hospital.nombre??>
							<name>${documento.hospital.nombre}</name>
						</#if>
					</representedOrganization>
				</#if>
	
			</assignedAuthor>
		</author>
	</#if>
	
	<#if documento.custodio??>
		<!-- Identificador y nombre del hospital o departamento del hospital que se responsabiliza 
		con la custodia del documento -->
		<custodian>
			<assignedCustodian>
				<representedCustodianOrganization>
					<#if documento.custodio.idRoot?? && documento.custodio.idExtension??>
						<!-- Identificador del centro de custodio
						Atributo root: Registro maestro de custodios
						Atributo extension: Identificador propio del custodio -->
						<id root="${documento.custodio.idRoot}" extension="${documento.custodio.idExtension}"/>
					</#if>
					<#if documento.custodio.nombre??>
						<!-- Nombre del lugar de custodia -->
						<name>${documento.custodio.nombre}</name>
					</#if>	
				</representedCustodianOrganization>
			</assignedCustodian>
		</custodian>
	</#if>
  
  	<#if documento.firmante??>
		<!-- Datos del firmante del documento (debe ser un médico) -->
		<legalAuthenticator>
	
			<!-- Fecha y hora de firma -->
			<time value="${fechaHoraFormateador.format(documento.fechaHoraFirma)}"/>
	    
			<!-- Código de firma -->
			<signatureCode code="S"/>
	
			<!-- Datos de la entidad firmante -->
			<assignedEntity>
				<#if documento.firmante.idRoot?? && documento.firmante.idExtension??>
					<!-- Identificador del autor.
					Atributo root: Registro maestro para identificadores de médicos
					Atributo extension: Matrícula médica -->
					<id root="${documento.firmante.idRoot}" extension="${documento.firmante.idExtension}" />
				</#if>
				
				<!-- Nombre de médico -->
				<assignedPerson>
					<name>
						<#if documento.firmante.prefijo??>
							<prefix>${documento.firmante.prefijo}</prefix>
						</#if>
						<#if documento.firmante.nombre??>
							<given>${documento.firmante.nombre}</given>
						</#if>
						<#if documento.firmante.apellido1??>
							<family>${documento.firmante.apellido1}</family>
						</#if>
						<#if documento.firmante.apellido2??>
							<family>${documento.firmante.apellido2}</family>
						</#if>
					</name>
				</assignedPerson>
	
			</assignedEntity>
		</legalAuthenticator> 
	</#if> 
	
	<#if documento.paciente.padre??>
		<!-- Participant va justo antes de component - es el último de los
		elementos del header.
		Un participant para el padre, es un participante "indirecto" ya que el documento CDA R2 es
		acerca de su hijo-->	
		<participant typeCode="IND">
	
			<!-- Su participación es en el rol de 'familiar' (next of kin) -->
			<associatedEntity classCode="NOK">
				<#if documento.paciente.padre.idRoot?? && documento.paciente.padre.idExtension??>
					<!-- Su identificación - ejemplo documento de identidad-->
					<id root="${documento.paciente.padre.idRoot}" extension="${documento.paciente.padre.idExtension}" />
				</#if>
				
				<!-- El tipo de relación que tiene, en este caso es el PADRE -->
				<code code="FTH" codeSystem="2.16.840.1.113883.5.111" displayName="Padre" />
	
				<!-- Se podria incluir también dirección y teléfonos (addr y telecom)-->
				<#if documento.paciente.padre.lugarResidencia??>
					<addr>
						<#if documento.paciente.padre.lugarResidencia.pais??>
							<country>${documento.paciente.padre.lugarResidencia.pais}</country>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.estado??>
							<state>${documento.paciente.padre.lugarResidencia.estado}</state>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.ciudad??>
							<city>${documento.paciente.padre.lugarResidencia.ciudad}</city>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.pueblo??>
							<censusTract>${documento.paciente.padre.lugarResidencia.pueblo}</censusTract>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.tipoCalle??>
							<streetNameType>${documento.paciente.padre.lugarResidencia.tipoCalle}</streetNameType>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.calle??>
							<streetNameBase>${documento.paciente.padre.lugarResidencia.calle}</streetNameBase>
						</#if>
						<#if documento.paciente.padre.lugarResidencia.numeroCasa??>
							<houseNumber>${documento.paciente.padre.lugarResidencia.numeroCasa}</houseNumber>
						</#if>
					</addr> 
				</#if>    
	      				
				<#if documento.paciente.padre.telefonos??>
					<#if documento.paciente.padre.telefonos?size != 0 >
						<#list documento.paciente.padre.telefonos as telefono>
							<!-- Telefonos, si tiene -->
							<telecom value="${telefono}" />
						</#list>
					</#if>
				</#if>
	        
	        	<#if documento.paciente.padre??>
					<!-- Su(s) nombre(s) y apellido(s) -->
					<associatedPerson>
						<name>
							<#if documento.paciente.padre.nombre??>
								<given>${documento.paciente.padre.nombre}</given>
							</#if>
							<#if documento.paciente.padre.apellido1??>
								<family>${documento.paciente.padre.apellido1}</family>
							</#if>
							<#if documento.paciente.padre.apellido2??>
								<family>${documento.paciente.padre.apellido2}</family>
							</#if>
						</name>
					</associatedPerson>
				</#if>
			        
			</associatedEntity>
		</participant>
	</#if>

	<#if documento.paciente.madre??>
		<!-- Idem para la madre -->
		<participant typeCode="IND">
			<associatedEntity classCode="NOK">
				<#if documento.paciente.madre.idRoot?? && documento.paciente.madre.idExtension??>
					<!-- Su identificación - ejemplo documento de identidad-->
					<id root="${documento.paciente.madre.idRoot}" extension="${documento.paciente.madre.idExtension}" />
				</#if>
	                    
				<code code="MTH" codeSystem="2.16.840.1.113883.5.111" displayName="Madre" />
	
				<!-- Se podria incluir también dirección y teléfonos (addr y telecom)-->   
				<#if documento.paciente.madre.lugarResidencia??>
					<addr>
						<#if documento.paciente.madre.lugarResidencia.pais??>
							<country>${documento.paciente.madre.lugarResidencia.pais}</country>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.estado??>
							<state>${documento.paciente.madre.lugarResidencia.estado}</state>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.ciudad??>
							<city>${documento.paciente.madre.lugarResidencia.ciudad}</city>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.pueblo??>
							<censusTract>${documento.paciente.madre.lugarResidencia.pueblo}</censusTract>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.tipoCalle??>
							<streetNameType>${documento.paciente.madre.lugarResidencia.tipoCalle}</streetNameType>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.calle??>
							<streetNameBase>${documento.paciente.madre.lugarResidencia.calle}</streetNameBase>
						</#if>
						<#if documento.paciente.madre.lugarResidencia.numeroCasa??>
							<houseNumber>${documento.paciente.madre.lugarResidencia.numeroCasa}</houseNumber>
						</#if>
					</addr> 
				</#if>
	      
				<#if documento.paciente.madre.telefonos??>
					<#if documento.paciente.madre.telefonos?size != 0 >
						<#list documento.paciente.madre.telefonos as telefono>
							<!-- Telefonos, si tiene -->
							<telecom value="${telefono}" />
						</#list>
					</#if>
				</#if>
	        	
				<!-- Su(s) nombre(s) y apellido(s) -->
				<associatedPerson>
					<name>
						<#if documento.paciente.madre.nombre??>
							<given>${documento.paciente.madre.nombre}</given>
						</#if>
						<#if documento.paciente.madre.apellido1??>
							<family>${documento.paciente.madre.apellido1}</family>
						</#if>
						<#if documento.paciente.madre.apellido2??>
							<family>${documento.paciente.madre.apellido2}</family>
						</#if>
					</name>
				</associatedPerson>
	
			</associatedEntity>
		</participant>
	</#if>

	<#if documento.documentoRelacionado??>
		<!-- Documento relacionado al presente documento -->
		<relatedDocument typeCode="APND">
			<parentDocument classCode="DOCCLIN" moodCode="EVN">
	
				<!--Id del documento relacionado -->
				<id root="${documento.documentoRelacionado.codigoRoot}" extension="${documento.documentoRelacionado.codigoExtension}" />
	
				<!-- Título del documento relacionado -->
				<text>${documento.documentoRelacionado.titulo}</text>
			</parentDocument>
		</relatedDocument>
	</#if> 
 
 <!-- Aqui comienza el cuerpo del documento -->
 <component>
   <structuredBody>
   		<#if documento.paciente.idExtension??>
			<component>
	      		<section>
		        	<title>
			            PATIENTID
			        </title>
			        <text>
					     ${documento.paciente.idExtension}
			       	</text>
		      	</section>
			</component>
		</#if>
		<component>
	    	<section>
	        	<title>
		       		Consulta preanest&#233;sica
		       	</title>
	       		<component>
			    	<section>
			           	<text>
			           		<table>
			   					<tbody>
									<tr>
										<td>
											Diagn&#243;stico m&#233;dico
										</td>
										<td>
											${documento.diagMedicoPreA}
										</td>										
										<td>
											M&#233;dico Auxiliar
										</td>
										<td>
											${documento.medicoAuxiliarPreA.nombreCompleto()}
										</td>
									</tr>
									<tr>
										<td>
											Riesgo quir&#250;rgico
										</td>
										<td>
											${documento.riesgoQuirurgicoPreA}
										</td>										
										<td>
											Estado f&#237;sico
										</td>
										<td>
											${documento.estadoFisicoPreA}
										</td>
									</tr>
									<tr>
										<td>
											M&#233;dico Anestesi&#243;logo
										</td>
										<td>
											${documento.medicoAnesteciologoPreA.nombreCompleto()}
										</td>										
										<td>
											Peso
										</td>
										<td>
											${documento.pesoPreA}
										</td>
									</tr>
									<tr>
										<td>
											Talla
										</td>
										<td>
											${documento.tallaPreA}
										</td>										
										<td>
											Consulta externa
										</td>
										<td>
											<#if documento.consultaExternaPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
								</tbody>
							</table>
							
							<#if documento.hozpitalizado>
								<table>
									<tbody>
										<tr>
											<th colspan="4">
												Hospitalizaci&#243;n
											</th>
										</tr>
										<tr>										
											<td>
												Sala
											</td>
											<td>
												${documento.salaHospitalizacionPreA}
											</td>
										
											<td>
												Cama
											</td>
											<td>
												${documento.camaHospitalizacionPreA}
											</td>
										</tr>
									</tbody>
								</table>
							</#if>
							
							<table>
								<tbody>
									<tr>									
										<td>
											Columna
										</td>
										<td>
											${documento.columnaPreA}
										</td>
									</tr>
									<tr>										
										<td>
											Tr&#255;quea
										</td>
										<td>
											${documento.traqueaPreA}
										</td>
										<td>
											Boca
										</td>
										<td>
											${documento.bocaPreA}
										</td>
									</tr>
									<tr>
										<td>
											Esteroides
										</td>
										<td>
											${documento.esteroidesPreA}
										</td>										
										<td>
											Fosas nasales
										</td>
										<td>
											${documento.fosasNasalesPreA}
										</td>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Medicaci&#243;n dos semanas anteriores
											    </caption>
											   	${documento.medicamentoSemAnterioresPreA}
										 	</paragraph>
										</td>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Recomendaciones
											    </caption>
											   	${documento.recomendacionesPreA}
										 	</paragraph>
										</td>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Observaciones
											    </caption>
											   	${documento.observacionesPreA}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
			           	</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Examen cardiovascular
			    		</title>
				    	<#if documento.signosVitalesPreA??>
					    	<component>
						    	<section>
						    		<title>
						    			Signos vitales
						    		</title>
						           	<text>
										<table>
						   					<tbody>			   					
												<tr>
													<th colspan="6">
														Tensi&#243;n
													</th>
												</tr>
												<tr>
													<td>
														Diast&#243;lica
													</td>
													<td>
														${documento.signosVitalesCPreA.pADiastolica}
													</td>										
													<td>
														Sist&#243;lica
													</td>
													<td>
														${documento.signosVitalesCPreA.pASistolica}
													</td>
													<td>
														Media
													</td>
													<td>
														${documento.signosVitalesCPreA.pAMedia}
													</td>
												</tr>
											</tbody>
										</table>
										
										<table>
						   					<tbody>			   					
												<tr>
													<th colspan="6">
														Pulso
													</th>
												</tr>
												<tr>
													<td>
														Valor
													</td>
													<td>
														${documento.signosVitalesCPreA.pValor}
													</td>										
													<td>
														Caracter&#237;sticas
													</td>
													<td>
														${documento.signosVitalesCPreA.caracteristicaPulso}
													</td>
													<td>
														Ubicaci&#243;n
													</td>
													<td>
														${documento.signosVitalesCPreA.ubicacionPulso}
													</td>
												</tr>
											</tbody>
										</table>
									</text>
						    	</section>
					    	</component>
						</#if>
					
						<component>
					    	<section>
					           	<text>
					           		<table>
					   					<tbody>			   					
											<tr>
												<td>
													Car&#255;cter
												</td>
												<td>
													${documento.caracterPreA}
												</td>
											</tr>
											<tr>										
												<td colspan="4">
													<paragraph>
													    <caption>
													    	Observaciones de la auscultaci&#243;n
													    </caption>
													   	${documento.auscultacionECPreA}
												 	</paragraph>
												</td>
											</tr>
											<tr>										
												<td colspan="4">
													<paragraph>
													    <caption>
													    	Observaciones del electrocardiograma
													    </caption>
													   	${documento.electrocardiogramaECPreA}
												 	</paragraph>
												</td>
											</tr>
										</tbody>
									</table>
								</text>
					    	</section>
				    	</component>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Examen respiratorio
			    		</title>				    	
			           	<text>
							<table>
			   					<tbody>			   					
									<tr>
										<td>
											Frecuencia
										</td>
										<td>
											${documento.signosVitalesCPreA.fRValor}
										</td>
										<#if documento.signosVitalesCPreA.caracteristicaFR??>
											<td>
												Carcater&#237;sticas
											</td>
											<td>
												${documento.signosVitalesCPreA.caracteristicaFR}
											</td>
										</#if>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Observaciones de la auscultaci&#243;n
											    </caption>
											   	${documento.auscultacionERPreA}
										 	</paragraph>
										</td>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Observaciones sobre los rayos X
											    </caption>
											   	${documento.rayosXERPreA}
										 	</paragraph>
										</td>
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Observaciones de las pruebas funcionales
											    </caption>
											   	${documento.pruebasFuncionalesERPreA}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Interrogatorio
			    		</title>				    	
			           	<text>
							<table>
			   					<tbody>			   					
									<tr>
										<td>
											Tos
										</td>
										<td>
											<#if documento.tosIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Expectoraci&#243;n
										</td>
										<td>
											<#if documento.expectoracionIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Bronquitis
										</td>
										<td>
											<#if documento.bronquitisIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Disnea
										</td>
										<td>
											<#if documento.disneaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Asma
										</td>
										<td>
											<#if documento.asmaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Alergia
										</td>
										<td>
											<#if documento.alergiaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Edema
										</td>
										<td>
											<#if documento.edemaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Cianosis
										</td>
										<td>
											<#if documento.cianosisIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Dolor anginoso
										</td>
										<td>
											<#if documento.dolorAnginosoIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Hipertensi&#243;n Arterial
										</td>
										<td>
											<#if documento.hipertensionArterialIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											P&#233;rdida Peso
										</td>
										<td>
											<#if documento.perdidaPesoIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Ictero
										</td>
										<td>
											<#if documento.icteroIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Insuficiencia  Hep&#255;tica
										</td>
										<td>
											<#if documento.insuficienciaHepaticaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Diabetes
										</td>
										<td>
											<#if documento.diabetesIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Tiroideopat&#237;a
										</td>
										<td>
											<#if documento.tiroideopatiaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Nefropat&#237;a
										</td>
										<td>
											<#if documento.nefropatiaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Cefalea
										</td>
										<td>
											<#if documento.cefaleaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
										<td>
											Trauma
										</td>
										<td>
											<#if documento.traumaIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Convulsiones
										</td>
										<td>
											<#if documento.convulcionesIntPreA>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Estado ps&#237;quico
											    </caption>
											   	${documento.estadoPsiquicoIntPreA}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Ex&#255;menes de laboratorio
			    		</title>				    	
			           	<text>
			           		<table>
			           			<tbody>
			           				<tr>
			           					<th>
			           						Ex&#255;menes
			           					</th>
			           				</tr>
			           				<tr>
			           					<td>
			           						Hemogobina
			           					</td>
			           					<td>
			           						${documento.hemoglobinaELPreA}
			           					</td>
			           					<td>
			           						Glicemia
			           					</td>
			           					<td>
			           						${documento.glicemiaELPreA}
			           					</td>
			           				</tr>
			           				<tr>
			           					<td>
			           						Hematocrito
			           					</td>
			           					<td>
			           						${documento.hematocritoELPreA}
			           					</td>
			           					<td>
			           						Urea
			           					</td>
			           					<td>
			           						${documento.ureaELPreA}
			           					</td>
			           				</tr>
			           				<tr>
			           					<td>
			           						Serologia
			           					</td>
			           					<td>
			           						<#if documento.serologiaELPreA>
			           							Positivo
			           						<#else>
			           							Negativo
			           						</#if>
			           					</td>
			           					<td>
			           						Grupo sanguineo
			           					</td>
			           					<td>
			           						${documento.grupoSanguineoELPreA}
			           					</td>
			           				</tr>
			           			</tbody>
		           			</table>
		           			
							<table>
			   					<tbody>	
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Observaciones sobre la preanestecia
											    </caption>
											   	${documento.observacionesPreanesteciaERPreA}
										 	</paragraph>
										</td>
									</tr>
									<tr>
										<td>
			           						Anestecia indicada
			           					</td>
			           					<td>
			           						${documento.anesteciaIndicadaELPreA}
			           					</td>
			           					<td>
			           						M&#233;todo a aplicar
			           					</td>
			           					<td>
			           						${documento.metodoAplicarELPreA}
			           					</td>
			           				</tr>
			           				<tr>
										<td>
			           						Agente a aplicar
			           					</td>
			           					<td colspan="3">
			           						${documento.agenteAplicarELPreA}
			           					</td>
			           				</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
		    	<#if documento.habitosPreA??>
			    	<#if documento.habitosPreA?size != 0 >
				    	<component>
					    	<section>
					    		<title>
					    			H&#255;bitos
					    		</title>				    	
					           	<text>
					           		<table>
					   					<tbody>
					   						<tr>
					   							<th colspan="3">
					   								H&#225;bitos psicobiol&#243;gicos
					   							</th>
					   						</tr>
					   						<tr>
												<th>
													H&#225;bito
												</th>										
												<th>
													Fecha
												</th>										
												<th>
													Descripci&#243;n
												</th>
											</tr>
											<#list documento.habitosPreA as habito>
												<tr>
													<td>
														${habito.nombre}
													</td>										
													<td>
														${fechaFormateador.format(habito.fecha)}
													</td>
													<td>
														${habito.descripcion}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</text>
					    	</section>
				    	</component>
			    	</#if>
			    </#if>
	        </section>
	    </component>
	    
	    <component>
	    	<section>
	        	<title>
		       		Preoperatorio
		       	</title>
		    	<#if documento.signosVitalesPreO??>
			    	<component>
				    	<section>
				    		<title>
				    			Signos vitales
				    		</title>
				           	<text>
				           		<#if documento.signosVitalesPreO.pADiastolica?? || documento.signosVitalesPreO.pASistolica?? || documento.signosVitalesPreO.pAMedia??>
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="6">
													Tensi&#243;n
												</th>
											</tr>										
											<tr>
												<#if documento.signosVitalesPreO.pADiastolica??>
													<td>
														Diast&#243;lica
													</td>
													<td>
														${documento.signosVitalesPreO.pADiastolica}
													</td>
												</#if>	
												<#if documento.signosVitalesPreO.pASistolica??>									
													<td>
														Sist&#243;lica
													</td>
													<td>
														${documento.signosVitalesPreO.pASistolica}
													</td>
												</#if>
												<#if documento.signosVitalesPreO.pAMedia??>
													<td>
														Media
													</td>
													<td>
														${documento.signosVitalesPreO.pAMedia}
													</td>
												</#if>
											</tr>
										</tbody>
									</table>
								</#if>
								
								<#if documento.signosVitalesPreO.pValor?? || documento.signosVitalesPreO.caracteristicaPulso?? || documento.signosVitalesPreO.ubicacionPulso??>
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="6">
													Pulso
												</th>
											</tr>
											<tr>
												<#if documento.signosVitalesPreO.pValor??>
													<td>
														Valor
													</td>
													<td>
														${documento.signosVitalesPreO.pValor}
													</td>
												</#if>	
												<#if documento.signosVitalesPreO.caracteristicaPulso??>									
													<td>
														Caracter&#237;sticas
													</td>
													<td>
														${documento.signosVitalesPreO.caracteristicaPulso}
													</td>
												</#if>
												<#if documento.signosVitalesPreO.ubicacionPulso??>
													<td>
														Ubicaci&#243;n
													</td>
													<td>
														${documento.signosVitalesPreO.ubicacionPulso}
													</td>
												</#if>
											</tr>
										</tbody>
									</table>
								</#if>
								
								<#if documento.frecRespiratoriaPreO??>
									<paragraph>
									    <caption>
									    	Frecuencia respiratoria
									    </caption>
									   	${documento.frecRespiratoriaPreO}
								 	</paragraph>
								</#if>
							</text>
				    	</section>
			    	</component>
				</#if>
				
				<#if documento.medicamentosPreO??>
					<#if documento.medicamentosPreO?size != 0 >
				    	<component>
					    	<section>
					    		<title>
					    			Drogas suministradas al paciente durante el preoperatorio
					    		</title>
					           	<text>
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="4">
													Medicamentos
												</th>
											</tr>
											<tr>
												<th>
													Hora
												</th>
												<th>
													Medicamento
												</th>
												<th>
													Dosis
												</th>
												<th>
													V&#237;a
												</th>											
											</tr>
											<#list documento.medicamentosPreO as medicamento>										
												<tr>
													<td>
														${fechaHoraFormateador.format(medicamento.horario)}
													</td>
													<td>
														${medicamento.nombre}
													</td>
													<td>
														${medicamento.dosis}
													</td>
													<td>
														${medicamento.via}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</text>
					    	</section>
				    	</component>
					</#if>
				</#if>
				
				<text>
	           		<table>
	   					<tbody>
							<tr>
								<td>
									M&#233;dico
								</td>
								<td>
									${documento.medicoPreO.nombreCompleto()}
								</td>
							</tr>
							<tr>										
								<td colspan="2">
									<paragraph>
									    <caption>
									    	Resultado
									    </caption>
									   	${documento.resultadoPreO}
								 	</paragraph>
								</td>
							</tr>
						</tbody>
					</table>
				</text>
	        </section>
	    </component>
	    
	    <component>
	    	<section>
	        	<title>
		       		Transoperatorio
		       	</title>
		       	<component>
			    	<section>
			    		<title>
			    			Datos generales
			    		</title>
			           	<text>
							<table>
			   					<tbody>			   					
									<tr>
										<td>
											Codificaci&#243;n
										</td>
										<td>
											${documento.codificacionTrasO}
										</td>
										<td>
											Agente de anestesia
										</td>
										<td>
											${documento.agenteAnestesiaTransO}
										</td>
									</tr>
									<tr>																				
										<td>
											M&#233;todo anest&#233;sico
										</td>
										<td>
											${documento.metodoAnestesicoTransO}
										</td>						
									</tr>
								</tbody>
							</table>
							
							<#if documento.medicamentosTransO??>
								<#if documento.medicamentosTransO?size != 0 >
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="2">
													Medicamentos
												</th>
											</tr>
											<tr>
												<th>
													Hora
												</th>
												<th>
													Medicamento
												</th>										
											</tr>
											<#list documento.medicamentosTransO as medicamento>										
												<tr>
													<td>
														${fechaHoraFormateador.format(medicamento.horario)}
													</td>
													<td>
														${dmedicamento.nombre}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</#if>
							</#if>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Indicadores
			    		</title>
			           	<text>
							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="2">
											Anestecia
										</th>
									</tr>
									<tr>
										<td>
											Hora de comienzo
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaIniAnestecsiaTransO)}
										</td>
										<td>
											Hora de fin
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaFinAnestesiaTransO)}
										</td>
									</tr>
								</tbody>
							</table>

							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="2">
											Operaci&#243;n
										</th>
									</tr>
									<tr>								
										<td>
											Hora de comienzo
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaIniOpeTransO)}
										</td>
										<td>
											Hora de fin
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaFinOpeTransO)}
										</td>
									</tr>
								</tbody>
							</table>
							
							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="2">
											Intubaci&#243;n
										</th>
									</tr>
									<tr>
										<td>
											Hora intubaci&#243;n
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaIntubacionTransO)}
										</td>										
										<td>
											Hora extubaci&#243;n
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaExtubacionTransO)}
										</td>
									</tr>
								</tbody>
							</table>
							
							<#if documento.indicadoresSVTransO??>
								<#if documento.indicadoresSVTransO?size != 0 >
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="8">
													Signos Vitales
												</th>
											</tr>
											<tr>
												<th>
													Pulso
												</th>
												<th>
													T.A.Diast&#243;lica
												</th>
												<th>
													T.A.Sist&#243;lica
												</th>
												<th>
													P.V.C.
												</th>
												<th>
													SatO2
												</th>
												<th>
													Ventilaci&#243;n
												</th>
												<th>
													Posici&#243;n
												</th>
												<th>
													Hora
												</th>								
											</tr>
											<#list documento.indicadoresSVTransO as indicador>										
												<tr>
													<td>
														${indicador.pValor}
													</td>
													<td>
														${indicador.pADiastolica}
													</td>
													<td>
														${indicador.pASistolica}
													</td>
													<td>
														${indicador.presionVenosaCentral}
													</td>
													<td>
														${indicador.saturacionO2}
													</td>
													<td>
														${indicador.ventilacion}
													</td>
													<td>
														${indicador.posicion}
													</td>
													<td>
														${fechaHoraFormateador.format(indicador.hora)}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</#if>
							</#if>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Balance de l&#237quidos
			    		</title>
			           	<text>
							<#if documento.liquidosTransO??>
								<#if documento.liquidosTransO?size != 0 >
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="4">
													Balance de l&#237quidos
												</th>
											</tr>
											<tr>
												<th>
													L&#237;quido
												</th>
												<th>
													Ingresos
												</th>
												<th>
													P&#233;rdidas
												</th>
												<th>
													Hora
												</th>						
											</tr>
											<#list documento.liquidosTransO as liquido>										
												<tr>
													<td>
														${liquido.nombre}
													</td>
													<td>
														<#if liquido.ingresos??>
															${liquido.ingresos}
														<#else>
															---
														</#if>
													</td>
													<td>
														<#if liquido.perdidas??>
															${liquido.perdidas}
														<#else>
															---
														</#if>
													</td>
													<td>
														${fechaHoraFormateador.format(liquido.hora)}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</#if>
							</#if>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Otros datos de la anestesia
			    		</title>
			           	<text>
							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="4">
											Inducci&#243;n
										</th>
									</tr>
			   						<tr>
			   							<td>
											Agente
										</td>
										<td>
											${documento.induccionAgenteTransO}
										</td>	
										<td>
											Relajante
										</td>
										<td>
											${documento.induccionRelajanteTransO}
										</td>
									</tr>
			   						<tr>
										<td>
											M&#233;todo
										</td>
										<td colspan="3">
											${documento.induccionMetodoTransO}
										</td>
									</tr>
								</tbody>
							</table>
							
							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="4">
											Mantenimiento
										</th>
									</tr>
			   						<tr>
			   							<td>
											Agente
										</td>
										<td>
											${documento.mantenimientoAgenteTransO}
										</td>	
										<td>
											Relajante
										</td>
										<td>
											${documento.mantenimientoRelajanteTransO}
										</td>
									</tr>
			   						<tr>
										<td>
											M&#233;todo
										</td>
										<td colspan="3">
											${documento.mantenimientoMetodoTransO}
										</td>
									</tr>
								</tbody>
							</table>		
							   					
							<table>
			   					<tbody>
									<tr>	
										<td>
											V&#237;a aire
										</td>
										<td>
											${documento.viAireTransO}
										</td> 
									</tr>
								</tbody>
							</table>	 
										
							<table>
			   					<tbody>	
			   						<tr>
										<th colspan="4">
											Intubaci&#243;n
										</th>
									</tr>
			   						<tr>
			   							<td>
											Numero tubo
										</td>
										<td>
											${documento.numeroTuboTransO}
										</td>	
										<td>
											Tipo mango tubo
										</td>
										<td>
											${documento.tipoMangoTuboTransO}
										</td>
									</tr>
			   						<tr>
										<td>
											Tipo espátula
										</td>
										<td colspan="3">
											${documento.tipoEspatulaTransO}
										</td>
									</tr>
								</tbody>
							</table> 	
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			L&#237;quidos totales y estado del paciente
			    		</title>
			           	<text>
							<table>
			   					<tbody>		   					
									<tr>
										<td>
											Suero total
										</td>
										<td>
											${documento.sueroTotalTransO}cc
										</td>
										<td>
											Sangre total
										</td>
										<td>
											${documento.sangreTotalTransO}cc
										</td>	
									</tr>
									<tr>
										<td>
											Plasma total
										</td>
										<td>
											${documento.plasmaTolatTransO}cc
										</td>
										<td>
											Total administrado
										</td>
										<td>
											${documento.totalAdministradoTransO}cc
										</td>
									</tr>
									<tr>
										<td>
											Tiempo anest&#233;sico
										</td>
										<td>
											${documento.tiempoAnestesicoTransO}
										</td>
										<td>
											Tiempo quir&#250;rgico
										</td>
										<td>
											${documento.tiempoQuirurgicoTransO}
										</td>
									</tr>
									<tr>
										<td>
											Estado mucus
										</td>
										<td>
											${documento.estadoMucusTransO}
										</td>
										<td>
											Emesis
										</td>
										<td>
											<#if documento.emesisTransO>
												S&#237;
											<#else>
												No
											</#if>
										</td>										
									</tr>
									<tr>
										<td>
											Tipo reflejo
										</td>
										<td>
											${documento.tipoReflejoTransO}
										</td>
										<td>
											Excitaci&#243;n
										</td>
										<td><!--Ojo tener presente que puede cambiar-->
											<#if documento.exitacionTransO>
												S&#237;
											<#else>
												No
											</#if>
										</td>
									</tr>
									<tr>
										<td>
											Tipo conciencia
										</td>
										<td>
											${documento.tipoConscienciaTransO}
										</td>
										<td>
											Pulso
										</td>
										<td>
											${documento.pulsoTransO}
										</td>								
									</tr>
									<tr>
										<td>
											T.A. Diast&#243;lica
										</td>
										<td>
											${documento.tADiastolicaTransO}
										</td>
										<td>
											T.A. Sist&#243;lica
										</td>
										<td>
											${documento.tASistolicaTransO}
										</td>
									</tr>
									<tr>
										<td>
											T.A. Media
										</td>
										<td>
											${documento.tAMediaTransO}
										</td>
										<td>
											Respiraci&#243;n
										</td>
										<td>
											${documento.respiracionTransO}
										</td>
									</tr>
									<tr>
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Resultado
											    </caption>
											   	${documento.resultadoTransO}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
	       		<component>
			    	<section>
			    		<title>
			    			Personal
			    		</title>
			           	<text>
			           		<table>
			   					<tbody>
									<tr>
										<td>
											M&#233;dico anestesi&#243;logo
										</td>
										<td>
											${documento.medicoAnestesiaTransO.nombreCompleto()}
										</td>
									</tr>
									<tr>	
										<td>
											M&#233;dico auxiliar anestesia
										</td>
										<td>
											${documento.auxiliarAnesteciaTransO.nombreCompleto()}
										</td>											
									</tr>
									<tr>	
										<td>
											Enfermera
										</td>
										<td>
											${documento.enfermeraTransO.nombreCompleto()}
										</td>
									</tr>
									<tr>
										<td>
											Enfermera transfusionista
										</td>
										<td>
											${documento.enfermeraTransfusionTransO.nombreCompleto()}
										</td>
									</tr>
									<tr>								
										<td>
											M&#233;dico cirujano
										</td>
										<td>
											${documento.medicoCirujanoTransO.nombreCompleto()}
										</td>
									</tr>
									<tr>	
										<td>
											M&#233;dico auxiliar de cirug&#237;a
										</td>
										<td>
											${documento.auxiliarCirugiaTransO.nombreCompleto()}
										</td>										
									</tr>
									<tr>
										<td>
											Diagnostico m&#233;dico
										</td>
										<td>
											${documento.diagMedicoTransO}
										</td>
									</tr>
									<tr>
										<td>
											Intervenci&#243;n practicada
										</td>
										<td>
											${documento.tipoIntervencionaTransO}
										</td>
									</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
	       		<component>
			    	<section>
			    		<title>
			    			Anestesia regional
			    		</title>
			           	<text>
			           		<table>
			   					<tbody>
									<tr>
										<td>
											M&#233;todo anest&#233;sico
										</td>
										<td>
											${documento.metodoAnestesiaTransO}
										</td>
										<td>
											Posici&#243;n
										</td>
										<td>
											${documento.posicionAnestesiaRegTransO}
										</td>
									</tr>
									<tr>
										<td>
											Agente anest&#233;sico
										</td>
										<td>
											${documento.agenteAnesteciaTransO}
										</td>										
										<td>
											Nivel anestesia
										</td>
										<td>
											${documento.nivelAnestesiaTransO}
										</td>
									</tr>
									<tr>	
										<td>
											t&#233;cnica anest&#233;sica
										</td>
										<td>
											${documento.tecnicaAnestesiaTransO}
										</td>									
										<td>
											Via administraci&#243;n medicamento
										</td>
										<td>
											${documento.viaAdmonMedicamentoTransO}
										</td>										
									</tr>
									<tr>										
										<td colspan="4">
											<paragraph>
											    <caption>
											    	Descripci&#243;n anestesia regional
											    </caption>
											   	${documento.descripcionAnesteciaRegTransO}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
			           	</text>
			    	</section>
		    	</component>
	        </section>
	    </component>
	    
	    <component>
	    	<section>
	        	<title>
		       		Posoperatorio
		       	</title>
		       	<component>
			    	<section>
			    		<title>
			    			Indicadores
			    		</title>
			           	<text>
			           		<#if documento.indicadoresSVPosO??>
								<#if documento.indicadoresSVPosO?size != 0 >
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="8">
													
												</th>
											</tr>
											<tr>
												<th>
													Pulso
												</th>
												<th>
													T.A.Diast&#243;lica
												</th>
												<th>
													T.A.Sist&#243;lica
												</th>
												<th>
													P.V.C.
												</th>
												<th>
													SatO2
												</th>
												<th>
													Temperatura
												</th>
												<th>
													Hora
												</th>								
											</tr>
											<#list documento.indicadoresSVPosO as indicador>										
												<tr>
													<td>
														${indicador.pValor}
													</td>
													<td>
														${indicador.pADiastolica}
													</td>
													<td>
														${indicador.pASistolica}
													</td>
													<td>
														${indicador.presionVenosaCentral}
													</td>
													<td>
														${indicador.saturacionO2}
													</td>
													<td>
														${indicador.temperatura}
													</td>
													<td>
														${fechaHoraFormateador.format(indicador.hora)}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</#if>
							</#if>
						</text>
			    	</section>
		    	</component>
		    	
		       	<component>
			    	<section>
			    		<title>
			    			Recepci&#243;n
			    		</title>
			           	<text>
							<table>
			   					<tbody>			   					
									<tr>
										<td>
											Fecha de recepci&#243;n
										</td>
										<td>
											${fechaFormateador.format(documento.fechaRecepcionPosO)}
										</td>
										<td>
											Hora de recepci&#243;n
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaRecepcionPosO)}
										</td>
									</tr>
									<tr>																				
										<td>
											Tipo consciencia
										</td>
										<td>
											${documento.tipoConscienciaPosO}
										</td>	
										<td>
											Tipo reflejo
										</td>
										<td>
											${documento.tipoReflejoPosO}
										</td>						
									</tr>
								</tbody>
							</table>
							
							<table>
			   					<tbody>			   					
									<tr>
										<th colspan="6">
											Tensi&#243;n
										</th>
									</tr>
									<tr>
										<td>
											Diast&#243;lica
										</td>
										<td>
											${documento.signosVitalesPosO.pADiastolica}
										</td>										
										<td>
											Sist&#243;lica
										</td>
										<td>
											${documento.signosVitalesPosO.pASistolica}
										</td>
										<td>
											Media
										</td>
										<td>
											${documento.signosVitalesPosO.pAMedia}
										</td>
									</tr>
								</tbody>
							</table>
							
							<table>
			   					<tbody>			   					
									<tr>
										<th colspan="6">
											Pulso
										</th>
									</tr>
									<tr>
										<td>
											Valor
										</td>
										<td>
											${documento.signosVitalesPosO.pValor}
										</td>										
										<td>
											Caracter&#237;sticas
										</td>
										<td>
											${documento.signosVitalesPosO.caracteristicaPulso}
										</td>
										<td>
											Ubicaci&#243;n
										</td>
										<td>
											${documento.signosVitalesPosO.ubicacionPulso}
										</td>
									</tr>
								</tbody>
							</table>
							
							<paragraph>
							    <caption>
							    	Respiraci&#243;n
							    </caption>
							   	${documento.respiracionPosO}
						 	</paragraph>
						 	
						 	<table>
			   					<tbody>			   					
									<tr>																				
										<td>
											Tipo de secreci&#243;n
										</td>
										<td>
											${documento.tipoSecrecionPosO}
										</td>	
										<td>
											Tipo exitaci&#243;n
										</td>
										<td>
											${documento.tipoExitacionPosO}
										</td>						
									</tr>
								</tbody>
							</table>
						 </text>
			    	</section>
		    	</component>
		    	
		       	<component>
			    	<section>
			    		<title>
			    			Balance de l&#237quidos
			    		</title>
			           	<text>
			           		<#if documento.liquidosPosO??>
								<#if documento.liquidosPosO?size != 0 >
									<table>
					   					<tbody>			   					
											<tr>
												<th colspan="4">
													Balance de l&#237quidos
												</th>
											</tr>
											<tr>
												<th>
													L&#237;quido
												</th>
												<th>
													Ingresos sal&#243;n
												</th>
												<th>
													Ingresos recuperaci&#243;n
												</th>
												<th>
													P&#233;rdidas
												</th>
												<th>
													Hora
												</th>						
											</tr>
											<#list documento.liquidosPosO as liquido>										
												<tr>
													<td>
														${liquido.nombre}
													</td>
													<td>
														<#if liquido.ingresos??>
															${liquido.ingresos}
														<#else>
															---
														</#if>
													</td>
													<td>
														<#if liquido.ingresosRecuperacion??>
															${liquido.ingresosRecuperacion}
														<#else>
															---
														</#if>
													</td>												
													<td>
														<#if liquido.perdidas??>
															${liquido.perdidas}
														<#else>
															---
														</#if>
													</td>
													<td>
														${fechaHoraFormateador.format(liquido.hora)}
													</td>
												</tr>
											</#list>
										</tbody>
									</table>
								</#if>
							</#if>
						</text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			    		<title>
			    			Otros
			    		</title>
			           	<text>
							<table>
			   					<tbody>	
			   						<tr>
			   							<td colspan="4">
											<paragraph>
											    <caption>
											    	Descripci&#243;n anestesia regional
											    </caption>
											   	${documento.descripcionAnesteciaRegPosO}
										 	</paragraph>
										</td>
									</tr>
									<tr>
			   							<td colspan="4">
											<paragraph>
											    <caption>
											    	Indicaciones inmediatas
											    </caption>
											   	${documento.indicacionesInmediatasPosO}
										 	</paragraph>
										</td>
									</tr>
									<tr>
			   							<td colspan="4">
											<paragraph>
											    <caption>
											    	Aspiraci&#243;n
											    </caption>
											   	${documento.aspiracionPosO}
										 	</paragraph>
										</td>
									</tr>
									<tr>
			   							<td colspan="4">
											<paragraph>
											    <caption>
											    	Ox&#237;geno
											    </caption>
											   	${documento.oxigenoPosO}
										 	</paragraph>
										</td>
									</tr>
			   						<tr>
			   							<td colspan="4">
											<paragraph>
											    <caption>
											    	Medicaci&#243;n
											    </caption>
											   	${documento.medicacionPosO}
										 	</paragraph>
										</td>
									</tr>
								</tbody>
							</table>
						</text>
			    	</section>
		    	</component>
		    	
				<component>
			    	<section>
			    		<title>
			    			Alta
			    		</title>
			           	<text>
							<table>
			   					<tbody>			   					
									<tr>
										<td>
											Fecha de alta
										</td>
										<td>
											${fechaFormateador.format(documento.fechaAltaPosO)}
										</td>
										<td>
											Hora de alta
										</td>
										<td>
											${fechaHoraFormateador.format(documento.horaAltaPosO)}
										</td>
									</tr>
									<tr>																				
										<td>
											M&#233;dico anestesi&#243;logo
										</td>
										<td>
											${documento.medicoAnestecialtaPosO.nombreCompleto()}
										</td>						
									</tr>
								</tbody>
							</table>
						 </text>
			    	</section>
		    	</component>
		    	
		    	<component>
			    	<section>
			           	<text>
			           		<paragraph>
							    <caption>
							    	Observaciones
							    </caption>
							   	${documento.ObservacionesPosO}
						 	</paragraph>							
					 	</text>
			    	</section>
		    	</component>
	        </section>
	    </component>
   </structuredBody>
 </component>
</ClinicalDocument>