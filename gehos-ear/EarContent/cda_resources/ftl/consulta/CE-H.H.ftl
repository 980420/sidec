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
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="HO-H.H" />
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
				<#if documento.paciente.direccionDeNacimiento??>
					<!-- Lugar de nacimiento -->
					<birthplace>
						<place>
							<addr>
								<#if documento.paciente.direccionDeNacimiento.pais??>
									<country>${documento.paciente.direccionNacimiento.pais}</country>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.estado??>
									<state>${documento.paciente.direccionNacimiento.estado}</state>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.ciudad??>
									<city>${documento.paciente.direccionNacimiento.ciudad}</city>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.pueblo??>
									<censusTract>${documento.paciente.direccionNacimiento.pueblo}</censusTract>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.tipoCalle??>
									<streetNameType>${documento.paciente.direccionNacimiento.tipoCalle}</streetNameType>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.calle??>
									<streetNameBase>${documento.paciente.direccionNacimiento.calle}</streetNameBase>
								</#if>
								<#if documento.paciente.direccionDeNacimiento.numeroCasa??>
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
	       		Datos personales
	       	</title>
           	<text>
           		<#if documento.antPersonales??>
           			<#if documento.antPersonales?size !=0 >
		           		<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="3">
		   								Antecedentes personales
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Antecedente
									</th>										
									<th>
										Fecha
									</th>										
									<th>
										Descripci&#243;n
									</th>
								</tr>
								<#list documento.antPersonales as antecedente>
									<tr>
										<td>
											${antecedente.nombre}
										</td>										
										<td>
											${fechaFormateador.format(antecedente.fecha)}
										</td>
										<td>
											<#if antecedente.descripcion??>
												${antecedente.descripcion}
											<#else>
												-
											</#if>
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
				<#if documento.antFamiliares??>
					<#if documento.antFamiliares?size !=0 >		
		              	<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="4">
		   								Antecedentes familiares
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Antecedente
									</th>										
									<th>
										Fecha
									</th>										
									<th>
										Descripci&#243;n
									</th>
									<th>
										Parentesco
									</th>
								</tr>
								<#list documento.antFamiliares as antecedente>
									<tr>
										<td>
											${antecedente.nombre}
										</td>										
										<td>
											${fechaFormateador.format(antecedente.fecha)}
										</td>
										<td>
											<#if antecedente.descripcion??>
												${antecedente.descripcion}
											<#else>
												-
											</#if>
										</td>
										<td>
											${antecedente.parentesco}
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
              	<#if documento.habitos??>
	              	<#if documento.habitos?size != 0 >
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
								<#list documento.habitos as habito>
									<tr>
										<td>
											${habito.nombre}
										</td>										
										<td>
											${fechaFormateador.format(habito.fecha)}
										</td>
										<td>
											<#if habito.descripcion??>
												${habito.descripcion}
											<#else>
												-
											</#if>										
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
				<#if documento.antPrenatObst>           	
	           		<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="4">
	   								Antecedentes prenatales y obst&#233;tricos
	   							</th>
	   						</tr>
	   						<tr>
								<td>
									Controles
								</td>										
								<td>
									<#if documento.cantcontrolesAPObst??>
										${documento.cantcontrolesAPObst}
									<#else>
										-
									</#if>
								</td>
								<td>
									Complicaci&#243;n embarazo
								</td>										
								<td>
									<#if documento.complicacionEmbarazoAPObst??>
										<#if documento.complicacionEmbarazoAPObst>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>								
								<td>
									Complicaci&#243;n Parto
								</td>										
								<td>
									<#if documento.complicacionPartoAntAPObst??>
										<#if documento.complicacionPartoAntAPObst>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
							</tr>
	   						<tr>		
								<td>
									Edad gestacional
								</td>										
								<td>
									<#if documento.edadGestacionalAPObst??>
										${documento.edadGestacionalAPObst}
									<#else>
										-
									</#if>
								</td>								
								<td>
									Tipo de edad gestacional
								</td>										
								<td>
									<#if documento.tipoEdadGestacionalAPObst??>
										${documento.tipoEdadGestacionalAPObst}
									<#else>
										-
									</#if>
								</td>
								<td>
									Tipo de parto
								</td>										
								<td>
									<#if documento.tipoEdadGestacionalAPObst??>
										${documento.tipoPartoAPObst}
									<#else>
										-
									</#if>
								</td>
							</tr>	
							<#if documento.asistenciaAPObst??>							
								<tr>
									<td>
										Asistencia
									</td>										
									<td colspan="5">
										${documento.asistenciaAPObst}
									</td>
								</tr>
							</#if>
							<#if documento.otrosAPObst??>	
								<tr>										
									<td colspan="6">
										<paragraph>
										    <caption>
										    	Otros
										    </caption>
										   	${documento.otrosAPObst}
									 	</paragraph>
									</td>
								</tr>
							</#if>
	   					</tbody>
	   				</table>
   				</#if>
   				<#if documento.periodoNeonatal>   				
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="6">
	   								Per&#237;odo neonatal
	   							</th>
	   						</tr>
	   						<tr>	   							
								<td>
									Tipo de respiraci&#243;n
								</td>										
								<td>
									<#if documento.tipoRespiracionPNeonatal??>
										${documento.tipoRespiracionPNeonatal}
									<#else>
										-
									</#if>
								</td>
								<td>
									Cianosis
								</td>										
								<td>
									<#if documento.cianosisPNeonatal??>
										<#if documento.cianosisPNeonatal>
											S&#237;
										<#else>
											No
										</#if>	
									<#else>
										-
									</#if>
								</td>
								<td>
									Malformaciones
								</td>										
								<td>
									<#if documento.malformacionesPNeonatal??>
										<#if documento.malformacionesPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>	
							</tr>
							<tr>									
								<td>
									Oftalm&#237;a
								</td>										
								<td>
									<#if documento.oftalmiaPNeonatal??>
										<#if documento.oftalmiaPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
								<td>
									Fiebre
								</td>										
								<td>
									<#if documento.fiebrePNeonatal??>
										<#if documento.fiebrePNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>	
								<td>
									Coriza
								</td>										
								<td>
									<#if documento.corizaPNeonatal??>
										<#if documento.corizaPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>									
								<td>
									Hemorragia
								</td>										
								<td>
									<#if documento.hemorragiaPNeonatal??>
										<#if documento.hemorragiaPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
								<td>
									V&#243;mitos
								</td>										
								<td>
									<#if documento.vomitosPNeonatal??>
										<#if documento.vomitosPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
								<td>
									Ictericia
								</td>										
								<td>
									<#if documento.ictericiaPNeonatal??>
										<#if documento.ictericiaPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>	
							</tr>
							<tr>									
								<td>
									Convulsiones
								</td>										
								<td colspan="5">
									<#if documento.convulsionesPNeonatal??>
										<#if documento.convulsionesPNeonatal>
											S&#237;
										<#else>
											No
										</#if>
									<#else>
										-
									</#if>
								</td>
							</tr>
							<#if documento.otrosPNeonatal??> 
								<tr>										
									<td colspan="6">
										<paragraph>
										    <caption>
										    	Otros
										    </caption>
										   	${documento.otrosPNeonatal}
									 	</paragraph>
									</td>
								</tr>
							</#if>
	   					</tbody>
	   				</table>
   				</#if>
   				<#if documento.alimentacion>   				
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="6">
	   								Alimentaci&#243;n
	   							</th>
	   						</tr>
	   						<tr>		   								   						
								<td>
									Destete
								</td>										
								<td>
									<#if documento.desteteAlimentac??>
										${documento.desteteAlimentac}
									<#else>
										-
									</#if>
								</td>									
								<td>
									Cereales
								</td>										
								<td>
									<#if documento.cerealesAlimentac??>
										${documento.cerealesAlimentac}
									<#else>
										-
									</#if>
								</td>
								<td>
									Sopas
								</td>										
								<td>
									<#if documento.sopasAlimentac??>
										${documento.sopasAlimentac}
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>										
								<td>
									Vegetales
								</td>										
								<td>
									<#if documento.vegetalesAlimentac??>
										${documento.vegetalesAlimentac}
									<#else>
										-
									</#if>
								</td>
								<td>
									Frutas
								</td>										
								<td>
									<#if documento.frutasAlimentac??>
										${documento.frutasAlimentac}
									<#else>
										-
									</#if>
								</td>
								<td>
									Huevos
								</td>										
								<td>
									<#if documento.huevosAlimentac??>
										${documento.huevosAlimentac}
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>										
								<td>
									Carne
								</td>										
								<td>
									<#if documento.carnesAlimentac??>
										${documento.carnesAlimentac}
									<#else>
										-
									</#if>
								</td>
								<td>
									Vitaminas
								</td>										
								<td>
									<#if documento.vitaminasAlimentac??>
										${documento.vitaminasAlimentac}
									<#else>
										-
									</#if>
								</td>									
								<td>
									Natural
								</td>										
								<td>										
									<#if documento.naturalAlimentac??>
										${documento.naturalAlimentac}	
									<#else>
										-
									</#if>									
								</td>
							</tr>
							<tr>	
								<td>
									Artificial
								</td>										
								<td>
									<#if documento.artificialAlimentac??>
										${documento.artificialAlimentac}
									<#else>
										-
									</#if>
								</td>								
								<td>
									Mixta
								</td>										
								<td>
									<#if documento.mixtaAlimentac??>
										${documento.mixtaAlimentac}
									<#else>
										-
									</#if>
								</td>
								<td>
									Dieta actual
								</td>										
								<td>
									<#if documento.dietaActualAlimentac??>
										${documento.dietaActualAlimentac}
									<#else>
										-
									</#if>
								</td>
							</tr>
	   					</tbody>
	   				</table>
   				</#if>
   				<#if documento.desarrollo>
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="6">
	   								Desarrollo
	   							</th>
	   						</tr>
	   						<tr>
								<td>
									Grado de escuela
								</td>										
								<td>
									<#if documento.gradoEscolarDesarr??>
										${documento.gradoEscolarDesarr}
									<#else>
										-
									</#if>
								</td>										
								<td>
									Sostuvo la cabeza a los
								</td>										
								<td>
									<#if documento.edadSostuvoCabezaDesarr??>
										${documento.edadSostuvoCabezaDesarr} meses
									<#else>
										-
									</#if>
								</td>
								<td>
									Se sent&#243; a los
								</td>										
								<td>
									<#if documento.edadPudoSentarseDesarr??>
										${documento.edadPudoSentarseDesarr} meses
									<#else>
										-
									</#if>
								</td>
							</tr>	
							<tr>										
								<td>
									Se par&#243; a los
								</td>										
								<td>
									<#if documento.edadPudoPararseDesarr??>
										${documento.edadPudoPararseDesarr} meses
									<#else>
										-
									</#if>
								</td>
								<td>
									Camin&#243; a los
								</td>										
								<td>
									<#if documento.edadPudoCaminarDesarr??>
										${documento.edadPudoCaminarDesarr} meses
									<#else>
										-
									</#if>
								</td>										
								<td>
									Control&#243; esfinter a los
								</td>										
								<td>
									<#if documento.edadControlEsfinterDesarr??>
										${documento.edadControlEsfinterDesarr} meses
									<#else>
										-
									</#if>
								</td>
							</tr>
							<tr>
								<td>
									Primer diente a los
								</td>										
								<td>
									<#if documento.primerDienteDesarr??>
										${documento.primerDienteDesarr} meses
									<#else>
										-
									</#if>
								</td>										
								<td>
									Primeras palabras a los
								</td>										
								<td colspan="3">
									<#if documento.primerasPalabrasDesarr??>
										${documento.primerasPalabrasDesarr} meses
									<#else>
										-
									</#if>
								</td>
							</tr>
							<#if documento.progresoEscolarDesarr??>
								<tr>										
									<td colspan="6">
										<paragraph>
										    <caption>
										    	Progreso en la escuela
										    </caption>
										   	${documento.progresoEscolarDesarr}
									 	</paragraph>
									</td>
								</tr>
							</#if>
							<#if documento.progresoPesoDesarr??>
								<tr>										
									<td colspan="6">
										<paragraph>
										    <caption>
										    	Progreso en el peso
										    </caption>
										   	${documento.progresoPesoDesarr}
									 	</paragraph>
									</td>
								</tr>
							</#if>
	   					</tbody>
	   				</table>
	   			</#if>	
		   	</text>
		   	<#if documento.inmunizaciones??>
			   	<#if documento.inmunizaciones?size != 0 >
				   	<component>
			    		<section>        		
			    		   	<title>Inmunizaciones</title>		    		   	
			    		   	<#list documento.inmunizaciones as inmunizacion>
				    		   	<component>
						    		<section>        		
						    		   	<title>Vacuna ${inmunizacion.nombre}</title>
								   		<text>
								   			<#if inmunizacion.fechaRecienNacido??>
												<table>
													<tbody>
														<tr>
															<th colspan="2">
																Dosis de reci&#233;n nacido
															</th>
														</tr>												
														<tr>
															<td>
																Fecha de su aplicaci&#243;n
															</td>										
															<td>
																${fechaFormateador.format(inmunizacion.fechaRecienNacido)}
															</td>
														</tr>
													</tbody>
												</table>
											</#if>
											<#if inmunizacion.fechaDosis1?? || inmunizacion.fechaDosis2?? || inmunizacion.fechaDosis3?? || inmunizacion.fechaDosis4??>
												<table>
													<tbody>
														<tr>
															<th colspan="6">
																Dosis aplicadas
															</th>
														</tr>
														<tr>
															<#if inmunizacion.fechaDosis1??>
																<td>
																	Primera
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaDosis1)}
																</td>
															</#if>											
															<#if inmunizacion.fechaDosis2??>
																<td>
																	Segunda
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaDosis2)}
																</td>
															</#if>
															<#if inmunizacion.fechaDosis3??>
																<td>
																	Tercera
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaDosis3)}
																</td>
															</#if>										
															<#if inmunizacion.fechaDosis4??>
																<td>
																	Cuarta
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaDosis4)}
																</td>
															</#if>	
														</tr>
													</tbody>
												</table>
											</#if>	
											
											<#if inmunizacion.fechaRefuerzo1?? || inmunizacion.fechaRefuerzo2?? || inmunizacion.fechaRefuerzo3?? || inmunizacion.fechaRefuerzo4??>
												<table>
													<tbody>
														<tr>
															<th colspan="6">
																Dosis de refuerzo
															</th>
														</tr>
														<tr>
															<#if inmunizacion.fechaRefuerzo1??>
																<td>
																	Primera
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaRefuerzo1)}
																</td>
															</#if>											
															<#if inmunizacion.fechaRefuerzo2??>
																<td>
																	Segunda
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaRefuerzo2)}
																</td>
															</#if>	
															<#if inmunizacion.fechaRefuerzo3??>
																<td>
																	Tercera
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaRefuerzo3)}
																</td>
															</#if>										
															<#if inmunizacion.fechaRefuerzo4??>
																<td>
																	Cuarta
																</td>										
																<td>
																	${fechaFormateador.format(inmunizacion.fechaRefuerzo4)}
																</td>
															</#if>	
														</tr>
													</tbody>
												</table>
											</#if>								
								 		</text>			        			
						    		</section>
						    	</component> 
					    	</#list>       			
			    		</section>
			    	</component>
		    	</#if>
	    	</#if>
	    	<#if documento.motivoHConsulta?? || documento.enfermedadactualHConsulta??>
	    		<component>
		    		<section>
		    			<text>
							<table>
								<tbody>
									<#if documento.motivoHConsulta??>
										<tr>
											<td>
												<paragraph>
												    <caption>
												    	Motivo de la consulta
												    </caption>
												   	${documento.motivoHConsulta}
											 	</paragraph>
											</td>
										</tr>
									</#if>
									<#if documento.enfermedadactualHConsulta??>												
										<tr>
											<td>
												<paragraph>
												    <caption>
												    	Enfermedad actual
												    </caption>
												   	${documento.enfermedadactualHConsulta}
											 	</paragraph>
											</td>
										</tr>
									</#if>
								</tbody>
							</table>
						</text>   			
		    		</section>
		    	</component>
	    	</#if>
	   	</section>
	 </component>
	 <component>
	   	<section>
	     	<title>
	     		Examen funcional
			</title>
	     	<component>
			   	<section>
			     	<title>
			     		Examen funcional
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="6">
		   								General
		   							</th>
		   						</tr>
		   						<tr>
			       					<td>
			       						Progreso de talla
			       					</td>				       					
			       					<td>
			       						<#if documento.progresoTallaG??>
			       							${documento.progresoTallaG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Aumento de peso
			       					</td>				       					
			       					<td>
			       						<#if documento.aumentoPesoG??>
			       							${documento.aumentoPesoG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						P&#233;rdida de peso	       						
			       					</td>				       					
			       					<td>
			       						<#if documento.perdidaPesoG??>
			       							${documento.perdidaPesoG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       								       					
			       					<td>
			       						Debilidad
			       					</td>				       					
			       					<td>
			       						<#if documento.debilidadG??>
			       							${documento.debilidadG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	
			       					<td>
			       						Fiebre
			       					</td>				       					
			       					<td>
			       						<#if documento.fiebreG??>
			       							${documento.fiebreG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Fatiga
			       					</td>				       					
			       					<td>
			       						<#if documento.fatigaG??>
			       							${documento.fatigaG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Temblores
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.tembloresG??>
			       							${documento.tembloresG}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
				       			<#if documento.otrosG??>
	       							<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosG}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Piel
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Cianosis
			       					</td>				       					
			       					<td>
			       						<#if documento.cianosisPiel??>
			       							${documento.cianosisPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Pigmentaciones
			       					</td>				       					
			       					<td>
			       						<#if documento.pigmentacionesPiel??>
			       							${documento.pigmentacionesPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       				 		Dermatosis				
			       					</td>				       					
			       					<td>
			       						<#if documento.dermatosisPiel??>
			       							${documento.dermatosisPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Edemas
			       					</td>				       					
			       					<td>
			       						<#if documento.edemasPiel??>
			       							${documento.edemasPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Erupciones
			       					</td>				       					
			       					<td>
			       						<#if documento.erupcionesPiel??>
			       							${documento.erupcionesPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Prurito
			       					</td>				       					
			       					<td>
			       						<#if documento.pruritoPiel??>
			       							${documento.pruritoPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Ictericia
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.ictericiaPiel??>
			       							${documento.ictericiaPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosPiel??>			       								
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosPiel}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Cabeza
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Ca&#237;das del cabello
			       					</td>				       					
			       					<td>
			       						<#if documento.caidaCabellosC??>
			       							${documento.caidaCabellosC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						S&#237;ncope
			       					</td>				       					
			       					<td>
			       						<#if documento.sincopeC??>
			       							${documento.sincopeC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Cefaleas		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.cefaleasC??>
			       							${documento.cefaleasC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Traumas
			       					</td>				       					
			       					<td>
			       						<#if documento.traumasC??>
			       							${documento.traumasC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Mareos
			       					</td>				       					
			       					<td>
			       						<#if documento.mareosC??>
			       							${documento.mareosC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorC??>
			       							${documento.dolorC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
				       			<#if documento.otrosC??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosC}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Ojos
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Amaurosis
			       					</td>				       					
			       					<td>
			       						<#if documento.amaurosisOjos??>
		       								${documento.amaurosisOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Diplop&#237;a
			       					</td>				       					
			       					<td>
			       						<#if documento.diplopiaOjos??>
		       								${documento.diplopiaOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Anteojos		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.anteojosOjos??>
		       								${documento.anteojosOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Fotofobia
			       					</td>				       					
			       					<td>
			       						<#if documento.fotofobiaOjos??>
		       								${documento.fotofobiaOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Cansancio ocular
			       					</td>				       					
			       					<td>
			       						<#if documento.cansancioOcularOjos??>
		       								${documento.cansancioOcularOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Lagrimeo
			       					</td>				       					
			       					<td>
			       						<#if documento.lagrimeoOjos??>
		       								${documento.lagrimeoOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorOjos??>
		       								${documento.dolorOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Nistagmos
			       					</td>				       					
			       					<td>
			       						<#if documento.nistagmosOjos??>
		       								${documento.nistagmosOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosOjos??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosOjos}
										 	</paragraph>
										</td>
									</tr>
								</#if>	
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>
	    	
	    	<component>
			   	<section>
			     	<title>
			     		O&#237;dos
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorO??>
		       								${documento.dolorO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Tinitus
			       					</td>				       					
			       					<td>
			       						<#if documento.tinitusO??>
		       								${documento.tinitusO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Secreciones       						
			       					</td>				       					
			       					<td>
			       						<#if documento.secrecionesO??>
		       								${documento.secrecionesO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						V&#233;rtigo
			       					</td>				       					
			       					<td>
			       						<#if documento.vertigoO??>
		       								${documento.vertigoO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Sordera
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.sorderaO??>
		       								${documento.sorderaO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosO??>	
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosO}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Nariz
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Catarros
			       					</td>				       					
			       					<td>
			       						<#if documento.catarrosN??>
		       								${documento.catarrosN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Secreci&#243;n nasal
			       					</td>				       					
			       					<td>
			       						<#if documento.secrecionNasalN??>
		       								${documento.secrecionNasalN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Epistaxis		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.epistaxisN??>
		       								${documento.epistaxisN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Sinusitis
			       					</td>				       					
			       					<td>
			       						<#if documento.sinusitisN??>
		       								${documento.sinusitisN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Obstrucciones
			       					</td>				       					
			       					<td>
			       						<#if documento.obstruccionesN??>
		       								${documento.obstruccionesN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Halitosis nasales
			       					</td>				       					
			       					<td>
			       						<#if documento.halitosisNasalesN??>
		       								${documento.halitosisNasalesN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosN??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosN}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Boca
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Dientes
			       					</td>				       					
			       					<td>
			       						<#if documento.dientesB??>
		       								${documento.dientesB}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Mucosa
			       					</td>				       					
			       					<td>
			       						<#if documento.mucosaB??>
		       								${documento.mucosaB}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						 Halitosis      						
			       					</td>				       					
			       					<td>
			       						<#if documento.halitosisB??>
		       								${documento.halitosisB}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Enc&#237;as
			       					</td>				       					
			       					<td>
			       						<#if documento.enciasB??>
		       								${documento.enciasB}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosB??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosB}
										 	</paragraph>
										</td>
									</tr>	
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Garganta
			     	</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Disfagia
			       					</td>				       					
			       					<td>
			       						<#if documento.disfagiaGg??>
		       								${documento.disfagiaGg}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Ronquera
			       					</td>				       					
			       					<td>
			       						<#if documento.ronqueraGg??>
		       								${documento.ronqueraGg}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Dolor	       						
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorGg??>
		       								${documento.dolorGg}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosGg??>	
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosGg}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Respiratorio
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Disnea
			       					</td>				       					
			       					<td>
			       						<#if documento.disneaR??>
		       								${documento.disneaR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Hem&#243;ptisis
			       					</td>				       					
			       					<td>
			       						<#if documento.hemoptisis??>
		       								${documento.hemoptisis}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Dolor tor&#225;cico		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorToracicoR??>
		       								${documento.dolorToracicoR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Silbidos y roncus
			       					</td>				       					
			       					<td>
			       						<#if documento.sibidosRoncusR??>
		       								${documento.sibidosRoncusR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Expectoraci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.expectoracionR??>
		       								${documento.expectoracionR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Estridor
			       					</td>				       					
			       					<td>
			       						<#if documento.estridorR??>
		       								${documento.estridorR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Tos
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.tosR??>
		       								${documento.tosR}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosR??>	
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosR}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Osteomuscular
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Astralgias
			       					</td>				       					
			       					<td>
			       						<#if documento.astralgiasOsteo??>
		       								${documento.astralgiasOsteo}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Deformidades
			       					</td>				       					
			       					<td>
			       						<#if documento.deformidadesOsteo??>
		       								${documento.deformidadesOsteo}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Debilidad		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.debilidadOsteo??>
		       								${documento.debilidadOsteo}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       					
			       					<td>
			       						Fracturas
			       					</td>				       					
			       					<td>
			       						<#if documento.fracturasOsteo??>
		       								${documento.fracturasOsteo}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	
			       					<td>
			       						Dolores &#243;seos
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.doloresOseosOsteo??>
		       								${documento.doloresOseosOsteo}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosOsteo??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosOsteo}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Cardiovascular
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Angustias
			       					</td>				       					
			       					<td>
			       						<#if documento.angustiasCardio??>
		       								${documento.angustiasCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Taquicardias
			       					</td>				       					
			       					<td>
			       						<#if documento.disneaCardio??>
		       								${documento.taquicardiasCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Disnea
			       					</td>				       					
			       					<td>
			       						<#if documento.disneaCardio??>
		       								${documento.disneaCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						V&#233;rtigos
			       					</td>				       					
			       					<td>
			       						<#if documento.vertigosCardio??>
		       								${documento.vertigosCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorCardio??>
		       								${documento.dolorCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Claudicaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.claudicacionCardio??>
		       								${documento.claudicacionCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Trastornos parest&#233;sicos
			       					</td>				       					
			       					<td>
			       						<#if documento.trastornosParestesicosCardio??>
		       								${documento.trastornosParestesicosCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Varicosidades
			       					</td>				       					
			       					<td>
			       						<#if documento.varicosidadesCardio??>
		       								${documento.varicosidadesCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Palpitaciones
			       					</td>				       					
			       					<td>
			       						<#if documento.palpitacionesCardio??>
		       								${documento.palpitacionesCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	
			       				</tr>				       				
			       				<tr>			       					
			       					<td>
			       						Desmayos
			       					</td>				       					
			       					<td>
			       						<#if documento.desmayosCardio??>
		       								${documento.desmayosCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Edema
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.edemaCardio??>
		       								${documento.edemaCardio}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosCardio??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosCardio}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>			        			
	    		</section>
	    	</component>	    	
	    	<component>
			   	<section>
			     	<title>
			     		Gastrointestinal
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="6">
		   								Gastrointestinal
		   							</th>
		   						</tr>
		   						<tr>
			       					<td>
			       						Apetito
			       					</td>				       					
			       					<td>
			       						<#if documento.apetitoGastroI??>
		       								${documento.apetitoGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Constipaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.constipacionGastroI??>
		       								${documento.constipacionGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	
			       					<td>
			       						Diarrea		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.diarreaGastroI??>
		       								${documento.diarreaGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorGastroI??>
		       								${documento.dolorGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Eructos
			       					</td>				       					
			       					<td>
			       						<#if documento.eructosGastroI??>
		       								${documento.eructosGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Flatulencia
			       					</td>				       					
			       					<td>
			       						<#if documento.flatulenciaGastroI??>
		       								${documento.flatulenciaGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Hemorroides
			       					</td>				       					
			       					<td>
			       						<#if documento.hemorroidesGastroI??>
		       								${documento.hemorroidesGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						F&#237;stula ano-rectal
			       					</td>				       					
			       					<td>
			       						<#if documento.fistulaAnoRectalGastroI??>
		       								${documento.fistulaAnoRectalGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>     					
			       					<td>
			       						Hernias
			       					</td>				       					
			       					<td>
			       						<#if documento.herniasGastroI??>
		       								${documento.herniasGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	
			       				</tr>
			       				<tr>	  
			       					<td>
			       						Malestar
			       					</td>
			       					<td>
			       						<#if documento.malestarGastroI??>
		       								${documento.malestarGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>       					
			       					<td>
			       						Nauseas
			       					</td>
			       					<td>
			       						<#if documento.nauseasGastroI??>
		       								${documento.nauseasGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Par&#225;sitos
			       					</td>
			       					<td>
			       						<#if documento.parasitosGastroI??>
		       								${documento.parasitosGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>	
			       				<tr>	       					
			       					<td>
			       						Pirosis
			       					</td>
			       					<td>
			       						<#if documento.pirosisGastroI??>
		       								${documento.pirosisGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
		       							V&#243;mitos
			       					</td>
			       					<td>
			       						<#if documento.vomitosGastroI??>
			       							${documento.vomitosGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>     					
			       					<td>
			       						Prolapso Rectal
			       					</td>
			       					<td>
			       						<#if documento.prolapsorectalGastroI??>
		       								${documento.prolapsorectalGastroI}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosGastroI??>
				       				<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosGastroI}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
	   				</text>	   				
					<component>
					   	<section>
					     	<title>
					     		Heces
							</title>
							<text>
				   				<table>
				   					<tbody>			       				
					       				<tr>
					       					<td>
					       						Tipo
					       					</td>				       					
					       					<td>
					       						<#if documento.tipoHecesGastroI??>
					       							${documento.tipoHecesGastroI}
					       						<#else>
					       							-
				       							</#if>
					       					</td>
					       					<td>
					       						Color
					       					</td>				       					
					       					<td>
					       						<#if documento.colorHecesGastroI??>
					       							${documento.colorHecesGastroI}
					       						<#else>
					       							-
				       							</#if>
					       					</td>
					       					<td>
					       						Mucosidad
					       					</td>				       					
					       					<td>
					       						<#if documento.mucosidadHecesGastroI??>
					       							${documento.mucosidadHecesGastroI}
					       						<#else>
					       							-
				       							</#if>
					       					</td>
					       				</tr>	
					       				<tr>
					       					<td>
					       						Sangre
					       					</td>
					       					<td colspan="5">	       						
					       						<#if documento.sangrehecesGastroI??>
						       						<#if documento.sangrehecesGastroI>
						       							S&#237;
						       						<#else>
						       							No
						       						</#if>
					       						<#else>
					       							-
				       							</#if>	       					
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
			     		Genitourinario
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorGeniU??>
			       							${documento.dolorGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Disuria
			       					</td>				       					
			       					<td>
			       						<#if documento.disuriaGeniU??>
			       							${documento.disuriaGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Nicturia		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.nicturiaGeniU??>
			       							${documento.nicturiaGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       					
			       					<td>
			       						Enuresis
			       					</td>				       					
			       					<td>
			       						<#if documento.enuresisGeniU??>
			       							${documento.enuresisGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Piuria
			       					</td>				       					
			       					<td>
			       						<#if documento.piuriaGeniU??>
			       							${documento.piuriaGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Hematuria
			       					</td>				       					
			       					<td>
			       						<#if documento.piuriaGeniU??>
			       							${documento.hematuriaGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Secreciones
			       					</td>				       					
			       					<td>
			       						<#if documento.secrecionesGeniU??>
			       							${documento.secrecionesGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Incontinencia
			       					</td>				       					
			       					<td>
			       						<#if documento.incontinenciaGeniU??>
			       							${documento.incontinenciaGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						&#218;lceras
			       					</td>				       					
			       					<td>
			       						<#if documento.ulcerasGeniU??>
			       							${documento.ulcerasGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>				       				
			       				<tr>				       					
			       					<td>
			       						Micciones
			       					</td>				       					
			       					<td>
			       						<#if documento.miccionesGeniU??>
			       							${documento.miccionesGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Flujo
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.flujoGeniU??>
			       							${documento.flujoGeniU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosGeniU??>
	       							<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosGeniU}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>	
				</section>
			</component>	    	
	    	<#if documento.paciente.genero=="f" || documento.paciente.genero=="F">
				<component>
				   	<section>
				     	<title>
				     		Ginecol&#243;gico
						</title>
						<text>
			   				<table>
			   					<tbody>
			   						<tr>
				       					<td>
				       						Dispauren&#237;a
				       					</td>				       					
				       					<td>
				       						<#if documento.dispaurenciaGinec??>
			       								${documento.dispaurenciaGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Flujo
				       					</td>				       					
				       					<td>
				       						<#if documento.flujoGinec??>
			       								${documento.flujoGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Reglas		       						
				       					</td>				       					
				       					<td>
				       						<#if documento.reglasGinec??>
			       								${documento.reglasGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       				</tr>				       							       				
									<tr>			       					
				       					<td>
				       						Tipo
				       					</td>				       					
				       					<td>
				       						<#if documento.tipoGinec??>
			       								${documento.tipoGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
										<td>
				       						Frigidez
				       					</td>				       					
				       					<td>
				       						<#if documento.frigidezGinec??>
			       								${documento.frigidezGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>	
				       					<td>
				       						Dolor Regla
				       					</td>				       					
				       					<td>
				       						<#if documento.dolorreglaGinec??>
			       								${documento.dolorreglaGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>	  						       					
				       				</tr>
				       				<tr>
				       					<td>
				       						Cantidad de Regla
				       					</td>				       					
				       					<td>
				       						<#if documento.cantidadreglaGinec??>
			       								${documento.cantidadreglaGinec}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						&#218;ltima regla
				       					</td>				       					
				       					<td colspan="3">
				       						<#if documento.fechaUltimaReglaGinec??>
			       								${fechaFormateador.format(documento.fechaUltimaReglaGinec)}
				       						<#else>
				       							-
				       						</#if>
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
			     	<title>
			     		Nervioso y mental
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Convulsiones
			       					</td>				       					
			       					<td>
			       						<#if documento.tembloresNM??>
		       								${documento.convulsionesNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Temblores
			       					</td>				       					
			       					<td>
			       						<#if documento.tembloresNM??>
		       								${documento.tembloresNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Est&#225;tica		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.estaticaNM??>
		       								${documento.estaticaNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Tics
			       					</td>				       					
			       					<td>
			       						<#if documento.ticsNM??>
		       								${documento.ticsNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Estado emocional
			       					</td>				       					
			       					<td>
			       						<#if documento.estadoEmocionalNM??>
		       								${documento.estadoEmocionalNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Esfera intelectual
			       					</td>				       					
			       					<td>
			       						<#if documento.esferaIntelectualNM??>
		       								${documento.esferaIntelectualNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Tipo de personalidad
			       					</td>				       					
			       					<td>
			       						<#if documento.personalidadNM??>
		       								${documento.personalidadNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Marcha
			       					</td>				       					
			       					<td>
			       						<#if documento.marchaNM??>
		       								${documento.marchaNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Par&#225;lisis
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.paralisisNM??>
		       								${documento.paralisisNM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosNM??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosNM}
										 	</paragraph>
										</td>
									</tr>
								</#if>
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
	     		Signos vitales
			</title>
			<component>
			   	<section>
			     	<title>
			     		Presi&#243;n arterial
					</title>
					<text>
		   				<table>
		   					<tbody>			       							       				
								<tr>
			       					<td>
			       						Diast&#243;lica       						
			       					</td>				       					
			       					<td>
			       						<#if documento.diastolicaPA??>
		       								${documento.diastolicaPA}mmHg
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Sist&#243;lica       						
			       					</td>	       								       					
			       					<td>
			       						<#if documento.sistolicaPA??>
		       								${documento.sistolicaPA}mmHg
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Media       						
			       					</td>				       					
			       					<td>
			       						<#if documento.mediaPA??>
		       								${documento.mediaPA}mmHg
			       						<#else>
			       							-
		       							</#if>
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
			     		Pulso
					</title>
					<text>
			       		<table>
		   					<tbody>		       							       				
								<tr>
			       					<td>
			       						Valor   						
			       					</td>				       					
			       					<td>
			       						<#if documento.valorPulso??>
		       								${documento.valorPulso}ppm
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Caracter&#237;sticas    						
			       					</td>	       								       					
			       					<td>
			       						<#if documento.caracteristicasPulso??>
		       								${documento.caracteristicasPulso}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Ubicaci&#243;n       						
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.ubicacionPulso??>
		       								${documento.ubicacionPulso}
			       						<#else>
			       							-
		       							</#if>
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
			     		Frecuencia respiratoria
					</title>
					<text>
			       		<table>
		   					<tbody>		       							       				
								<tr>
			       					<td>
			       						Valor   						
			       					</td>				       					
			       					<td>
			       						<#if documento.valorFrecRes??>
		       								${documento.valorFrecRes}rpm
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Caracter&#237;sticas    						
			       					</td>	       								       					
			       					<td>
			       						<#if documento.caracteristicasFrecRes??>
		       								${documento.caracteristicasFrecRes}
			       						<#else>
			       							-
		       							</#if>
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
			     		Otros signos vitales
					</title>
					<text>
			       		<table>
		   					<tbody>			       							       				
								<tr>
			       					<td>
			       						Temperatura					
			       					</td>				       					
			       					<td>
			       						<#if documento.temperaturaOSV??>
		       								${documento.temperaturaOSV}&#176;C
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Peso					
			       					</td>	       								       					
			       					<td>
			       						<#if documento.pesoOSV??>
		       								${documento.pesoOSV}Kg
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Talla			
			       					</td>				       					
			       					<td>
			       						<#if documento.tallaOSV??>
		       								${documento.tallaOSV}cm
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<tr>
			       					<td>
			       						P/T			
			       					</td>	       								       					
			       					<td>
			       						<#if documento.ptOSV??>
		       								${documento.ptOSV}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						PIT	
			       					</td>				       					
			       					<td>
			       						<#if documento.pitOSV??>
		       								${documento.pitOSV}Kg
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						PP				
			       					</td>	       								       					
			       					<td>
			       						<#if documento.ppOSV??>
		       								${documento.ppOSV}ml
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<tr>
			       					<td>
			       						ETC	
			       					</td>				       					
			       					<td>
			       						<#if documento.etcOSV??>
		       								${documento.etcOSV}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						IMC				
			       					</td>	       								       					
			       					<td>
			       						<#if documento.imcOSV??>
		       								${documento.imcOSV}Kg
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						PIMC			
			       					</td>				       					
			       					<td>
			       						<#if documento.pimcOSV??>
		       								${documento.pimcOSV}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<tr>
			       					<td>
			       						Exceso de peso			
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.excesoPesoOSV??>
		       								<#if documento.excesoPesoOSV>
				       							S&#237;
				       						<#else>
				       							No
				       						</#if>
			       						<#else>
			       							-
		       							</#if>
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
	     		Examen f&#237;sico
			</title>
			<component>
			   	<section>
			     	<title>
			     		Piel
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Color
			       					</td>				       					
			       					<td>
			       						<#if documento.colorFPiel??>
		       								${documento.colorFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Erupci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.erupcionFPiel??>
		       								${documento.erupcionFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       				 		Humedad				
			       					</td>				       					
			       					<td>
			       						<#if documento.humedadFPiel??>
		       								${documento.humedadFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						U&#241;as
			       					</td>				       					
			       					<td>
			       						<#if documento.unnasFPiel??>
		       								${documento.unnasFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Contextura
			       					</td>				       					
			       					<td>
			       						<#if documento.contexturaFPiel??>
		       								${documento.contexturaFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						N&#243;dulos
			       					</td>				       					
			       					<td>
			       						<#if documento.nodulosFPiel??>
		       								${documento.nodulosFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Temperatura
			       					</td>				       					
			       					<td>
			       						<#if documento.temperaturaFPiel??>
		       								${documento.temperaturaFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Vascularizaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.vascularizacionFPiel??>
		       								${documento.vascularizacionFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Pigmentaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.pigmentacionFPiel??>
		       								${documento.pigmentacionFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<tr>				       					
			       					<td>
			       						Cicatrices
			       					</td>				       					
			       					<td>
			       						<#if documento.cicatricesFPiel??>
		       								${documento.cicatricesFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       				 		Equimosis				
			       					</td>				       					
			       					<td>
			       						<#if documento.equimosisFPiel??>
		       								${documento.equimosisFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						F&#237;stulas
			       					</td>				       					
			       					<td>
			       						<#if documento.fistulasFPiel??>
		       								${documento.fistulasFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       							       				
								<tr>
			       					<td>
			       						Cianosis
			       					</td>				       					
			       					<td>
			       						<#if documento.cianosisFPiel??>
		       								${documento.cianosisFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						&#218;lceras
			       					</td>				       					
			       					<td>
			       						<#if documento.ulcerasFPiel??>
		       								${documento.ulcerasFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Petequias
			       					</td>				       					
			       					<td>
			       						<#if documento.petequiasFPiel??>
		       								${documento.petequiasFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>				       					
			       					<td>
			       						Pelos
			       					</td>				       					
			       					<td>
			       						<#if documento.pelosFPiel??>
		       								${documento.pelosFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Turgor
			       					</td>				       					
			       					<td>
			       						<#if documento.tugorFPiel??>
		       								${documento.tugorFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Edema
			       					</td>				       					
			       					<td>
			       						<#if documento.edemaFPiel??>
		       								${documento.edemaFPiel}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFPiel??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFPiel}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Cabeza
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Configuracion
			       					</td>				       					
			       					<td>
			       						<#if documento.configuracionFC??>
		       								${documento.configuracionFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorFC??>
		       								${documento.dolorFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Fontanelas  						
			       					</td>				       					
			       					<td>
			       						<#if documento.fontanelasFC??>
		       								${documento.fontanelasFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Craneotabes
			       					</td>				       					
			       					<td>
			       						<#if documento.craneotabesFC??>
		       								${documento.craneotabesFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Reblandecimiento
			       					</td>				       					
			       					<td>
			       						<#if documento.reblandecimientoFC??>
		       								${documento.reblandecimientoFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Cabellos
			       					</td>				       					
			       					<td>
			       						<#if documento.cabellosFC??>
		       								${documento.cabellosFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Circunferencia
			       					</td>				       					
			       					<td>
			       						<#if documento.circunferenciaFC??>
		       								${documento.circunferenciaFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Suturas
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.suturasFC??>
		       								${documento.suturasFC}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFC??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFC}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Ojos
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Conjuntiva
			       					</td>				       					
			       					<td>
			       						<#if documento.conjuntivaFOjos??>
		       								${documento.conjuntivaFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Nistagmus
			       					</td>				       					
			       					<td>
			       						<#if documento.nistagmusFOjos??>
		       								${documento.nistagmusFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Escler&#243;tica		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.ecleroticaFOjos??>
		       								${documento.ecleroticaFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Ptosis
			       					</td>				       					
			       					<td>
			       						<#if documento.ptosisFOjos??>
		       								${documento.ptosisFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						C&#243;rnea
			       					</td>				       					
			       					<td>
			       						<#if documento.ptosisFOjos??>
		       								${documento.corneaFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Exoftalmos
			       					</td>				       					
			       					<td>
			       						<#if documento.exoftalmoFOjos??>
		       								${documento.exoftalmoFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Pupilas
			       					</td>				       					
			       					<td>
			       						<#if documento.pupilasFOjos??>
		       								${documento.pupilasFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Agudeza visual
			       					</td>				       					
			       					<td>
			       						<#if documento.agudezaVisualFOjos??>
		       								${documento.agudezaVisualFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Movimientos
			       					</td>				       					
			       					<td>
			       						<#if documento.movimientosFOjos??>
		       								${documento.movimientosFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Signos oftalmosc&#243;picos
			       					</td>				       					
			       					<td>
			       						<#if documento.signosOftFOjos??>
		       								${documento.signosOftFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Desviaciones
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.desviacionesFOjos??>
		       								${documento.desviacionesFOjos}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFOjos??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFOjos}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		O&#237;dos
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Pabell&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.pabellosnFO??>
		       								${documento.pabellosnFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Secreciones
			       					</td>				       					
			       					<td>
			       						<#if documento.secrecionesFO??>
		       								${documento.secrecionesFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Conducto externo      						
			       					</td>				       					
			       					<td>
			       						<#if documento.conductoExternoFO??>
		       								${documento.conductoExternoFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Mastoides
			       					</td>				       					
			       					<td>
			       						<#if documento.mastoidesFO??>
		       								${documento.mastoidesFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						T&#237;mpano
			       					</td>				       					
			       					<td>
			       						<#if documento.timpanoFO??>
		       								${documento.timpanoFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorFO??>
		       								${documento.dolorFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>	
			       				<tr>
			       					<td>
			       						Audici&#243;n
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.audicionFO??>
		       								${documento.audicionFO}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>	
			       				<#if documento.otrosFO??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFO}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Nariz
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Fosas nasales
			       					</td>				       					
			       					<td>
			       						<#if documento.fosasNasalesFN??>
		       								${documento.fosasNasalesFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Diafanoscopia
			       					</td>				       					
			       					<td>
			       						<#if documento.diafanoscopiaFN??>
		       								${documento.diafanoscopiaFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Mucosa		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.mucosaFN??>
		       								${documento.mucosaFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Sensibilidad de los senos
			       					</td>				       					
			       					<td>
			       						<#if documento.sensibilidadSenosFN??>
		       								${documento.sensibilidadSenosFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Tabique
			       					</td>				       					
			       					<td>
			       						<#if documento.tabiqueFN??>
		       								${documento.tabiqueFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Secreci&#243;n nasal
			       					</td>				       					
			       					<td>
			       						<#if documento.secrecionNasalFN??>
		       								${documento.secrecionNasalFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>	
			       				<tr>
			       					<td>
			       						Meatos
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.meatosFN??>
		       								${documento.meatosFN}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>	
			       				<#if documento.otrosFN??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFN}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Boca
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Aliento
			       					</td>				       					
			       					<td>
			       						<#if documento.alientoFBoca??>
		       								${documento.alientoFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Lengua
			       					</td>				       					
			       					<td>
			       						<#if documento.lenguaFBoca??>
		       								${documento.lenguaFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Labios 						
			       					</td>				       					
			       					<td>
			       						<#if documento.labiosFBoca??>
		       								${documento.labiosFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Conductos salivares
			       					</td>				       					
			       					<td>
			       						<#if documento.conductosSalivaresFBoca??>
		       								${documento.conductosSalivaresFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Dientes       						
			       					</td>				       					
			       					<td>
			       						<#if documento.dientesFBoca??>
		       								${documento.dientesFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Par&#225;lisis y trismo
			       					</td>				       					
			       					<td>
			       						<#if documento.paralisisTrismoFBoca??>
		       								${documento.paralisisTrismoFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<tr>
			       					<td>
			       						Enc&#237;as       						
			       					</td>				       					
			       					<td>
			       						<#if documento.enciasFBoca??>
		       								${documento.enciasFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Mucosas
			       					</td>				       					
			       					<td>
			       						<#if documento.mucosaFBoca??>
		       								${documento.mucosaFBoca}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFBoca??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFBoca}
										 	</paragraph>
										</td>
									</tr>
								</#if>	
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>	
   				
			<component>
			   	<section>
			     	<title>
			     		Faringe
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Am&#237;gdalas
			       					</td>				       					
			       					<td>
			       						<#if documento.amigdalasFF??>
		       								${documento.amigdalasFF}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Disfagia
			       					</td>				       					
			       					<td>
			       						<#if documento.disfagiaFF??>
		       								${documento.disfagiaFF}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Adenoides	       						
			       					</td>				       					
			       					<td>
			       						<#if documento.adenoidesFF??>
		       								${documento.adenoidesFF}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td>
			       						<#if documento.dolorFF??>
		       								${documento.dolorFF}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Rinofaringe	       						
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.rinofaringeFF??>
		       								${documento.rinofaringeFF}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosFF??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFF}
										 	</paragraph>
										</td>
									</tr>
								</#if>	
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Cuello
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Per&#237;metro
			       					</td>				       					
			       					<td>
			       						<#if documento.perimetroFCuello??>
		       								${documento.perimetroFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Movilidad
			       					</td>				       					
			       					<td>
			       						<#if documento.movilidadFCuello??>
		       								${documento.movilidadFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Vasos       						
			       					</td>				       					
			       					<td>
			       						<#if documento.vasosFCuello??>
		       								${documento.vasosFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>	       					
			       					<td>
			       						Ganglios
			       					</td>				       					
			       					<td>
			       						<#if documento.gangliosFCuello??>
		       								${documento.gangliosFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Tr&#225;quea
			       					</td>				       					
			       					<td>
			       						<#if documento.traqueaFCuello??>
		       								${documento.traqueaFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Tiroides
			       					</td>				       					
			       					<td>
			       						<#if documento.tiroidesFCuello??>
		       								${documento.tiroidesFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Tumoraciones
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.tumoracionesFCuello??>
		       								${documento.tumoracionesFCuello}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>	
			       				<#if documento.otrosFCuello??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFCuello}
										 	</paragraph>
										</td>
									</tr>
								</#if>	
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Ganglios linf&#225;ticos
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Cervicales
			       					</td>				       					
			       					<td>
			       						<#if documento.cervicalesFGL??>
		       								${documento.cervicalesFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Epitrocleares
			       					</td>				       					
			       					<td>
			       						<#if documento.epitoclearesFGL??>
		       								${documento.epitoclearesFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Occipitales		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.occipitalesFGL??>
		       								${documento.occipitalesFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       					
			       					<td>
			       						Inguinales
			       					</td>				       					
			       					<td>
			       						<#if documento.inguinalesFGL??>
		       								${documento.inguinalesFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Supraclaviculares
			       					</td>				       					
			       					<td>
			       						<#if documento.supraclavicularesFGL??>
		       								${documento.supraclavicularesFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Axilares
			       					</td>				       					
			       					<td>
			       						<#if documento.axilaresFGL??>
		       								${documento.axilaresFGL}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFGL??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFGL}
										 	</paragraph>
										</td>
									</tr>	
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		T&#243;rax
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Forma
			       					</td>				       					
			       					<td>
			       						<#if documento.formaFT??>
		       								${documento.formaFT}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Palpaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.palpacionFT??>
		       								${documento.palpacionFT}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Simetr&#237;a
			       					</td>				       					
			       					<td>
			       						<#if documento.simetriaFT??>
		       								${documento.simetriaFT}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Respiraci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.respiracionFT??>
		       								${documento.respiracionFT}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Expansi&#243;n
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.expansionFT??>
		       								${documento.expansionFT}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFT??>
       								<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFT}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>			
			<#if documento.paciente.genero!="m" && documento.paciente.genero!="M">
				<component>
				   	<section>
				     	<title>
				     		Senos
						</title>
				     	<text>
			   				<table>
			   					<tbody>
			   						<tr>
				       					<td>
				       						N&#243;dulos
				       					</td>				       					
				       					<td>
				       						<#if documento.nodulosFS??>
		       									${documento.nodulosFS}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Pezones
				       					</td>				       					
				       					<td>
				       						<#if documento.pezonesFS??>
		       									${documento.pezonesFS}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Secreciones
				       					</td>				       					
				       					<td colspan="3">
				       						<#if documento.secrecionesFS??>
		       									${documento.secrecionesFS}
				       						<#else>
				       							-
			       							</#if>
				       					</td>	       					
				       				</tr>
				       				<#if documento.otrosFS??>
       									<tr>										
											<td colspan="6">
												<paragraph>
												    <caption>
												    	Otros
												    </caption>
												   	${documento.otrosFS}
											 	</paragraph>
											</td>
										</tr>
									</#if>
			   					</tbody>
			   				</table>
	   					</text>
					</section>
				</component>
			</#if>			
			<component>
			   	<section>
			     	<title>
			     		Pulmones
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Fr&#233;mito
			       					</td>				       					
			       					<td>
			       						<#if documento.fremitoFP??>
	       									${documento.fremitoFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Pectoriloquia &#225;fona
			       					</td>				       					
			       					<td>
			       						<#if documento.pectoriloquiaAfonaFP??>
	       									${documento.pectoriloquiaAfonaFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Percusi&#243;n		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.percusionFP??>
	       									${documento.percusionFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Broncofon&#237;a
			       					</td>				       					
			       					<td>
			       						<#if documento.broncofoniaFP??>
	       									${documento.broncofoniaFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Auscultaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.auscultacionFP??>
	       									${documento.auscultacionFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Ruidos adventicios
			       					</td>				       					
			       					<td>
			       						<#if documento.ruidosAdventiciosFP??>
	       									${documento.ruidosAdventiciosFP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFP??>
   									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFP}
										 	</paragraph>
										</td>
									</tr>
								</#if>	
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Coraz&#243;n
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Latido de la punta
			       					</td>				       					
			       					<td>
			       						<#if documento.latidoPuntaFCz??>
	       									${documento.latidoPuntaFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Ruidos
			       					</td>				       					
			       					<td>
			       						<#if documento.ruidosFCz??>
	       									${documento.ruidosFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Thrill		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.thrillFCz??>
	       									${documento.thrillFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Galope
			       					</td>				       					
			       					<td>
			       						<#if documento.galopeFCz??>
	       									${documento.galopeFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Pulsaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.pulsacionFCz??>
	       									${documento.pulsacionFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Frotes
			       					</td>				       					
			       					<td>
			       						<#if documento.frotesFCz??>
	       									${documento.frotesFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Ritmo
			       					</td>				       					
			       					<td>
			       						<#if documento.ritmoFCz??>
	       									${documento.ritmoFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Soplos
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.soplosFCz??>
	       									${documento.soplosFCz}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFCz??>
   									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFCz}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Vasos sangu&#237;neos
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Pulso
			       					</td>				       					
			       					<td>
			       						<#if documento.pulsoFVS??>
	       									${documento.pulsoFVS}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Caracteres
			       					</td>				       					
			       					<td>
			       						<#if documento.caracteresFVS??>
	       									${documento.caracteresFVS}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Paredes vasculares		       						
			       					</td>				       					
			       					<td>
			       						<#if documento.paredesVascularesFVS??>
	       									${documento.paredesVascularesFVS}
			       						<#else>
			       							-
		       							</#if>
			       					</td>	       					
			       				</tr>
			       				<#if documento.otrosFVS??>
   									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFVS}
										 	</paragraph>
										</td>									
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Abdomen
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Aspecto
			       					</td>				       					
			       					<td>
			       						<#if documento.aspectoFA??>
	       									${documento.aspectoFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Tumoraciones
			       					</td>				       					
			       					<td>
			       						<#if documento.tumoracionesFA??>
	       									${documento.tumoracionesFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Circunferencia					
			       					</td>				       					
			       					<td>
			       						<#if documento.circunferenciaFA??>
	       									${documento.circunferenciaFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       					
			       					<td>
			       						Ascitis
			       					</td>				       					
			       					<td>
			       						<#if documento.ascitisFA??>
	       									${documento.ascitisFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Peristalsis
			       					</td>				       					
			       					<td>
			       						<#if documento.peristalsisFA??>
	       									${documento.peristalsisFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						H&#237;gado
			       					</td>				       					
			       					<td>
			       						<#if documento.higadoFA??>
	       									${documento.higadoFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Cicatrices
			       					</td>				       					
			       					<td>
			       						<#if documento.cicatricesFA??>
	       									${documento.cicatricesFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Ri&#241;ones
			       					</td>				       					
			       					<td>
			       						<#if documento.rinnonesFA??>
	       									${documento.rinnonesFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Defensa
			       					</td>				       					
			       					<td>
			       						<#if documento.defensaFA??>
	       									${documento.defensaFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>				       				
			       				<tr>				       					
			       					<td>
			       						Bazo
			       					</td>				       					
			       					<td>
			       						<#if documento.bazoFA??>
	       									${documento.bazoFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Sensibilidad
			       					</td>				       					
			       					<td>
			       						<#if documento.sensibilidadFA??>
	       									${documento.sensibilidadFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Hernias
			       					</td>				       					
			       					<td>
			       						<#if documento.hernias??>
	       									${documento.hernias}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<tr>
			       					<td>
			       						Contractura
			       					</td>				       					
			       					<td>
			       						<#if documento.contracturaFA??>
	       									${documento.contracturaFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Dolor
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.dolorFA??>
	       									${documento.dolorFA}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFA??>
   									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFA}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Urinario
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Ri&#241;ones
			       					</td>				       					
			       					<td>
			       						<#if documento.rinnonesFU??>
	       									${documento.rinnonesFU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Puntos dolorosos
			       					</td>				       					
			       					<td>
			       						<#if documento.ptosDolorososFU??>
	       									${documento.ptosDolorososFU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Micci&#243;n					
			       					</td>				       					
			       					<td>
			       						<#if documento.miccionFU??>
	       									${documento.miccionFU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       				</tr>				       							       				
								<tr>			       					
			       					<td>
			       						Orina
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.orinaFU??>
	       									${documento.orinaFU}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFU??>
   									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFU}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		<#if documento.paciente.genero=="m" && documento.paciente.genero=="M">
			     			Genitales masculinos
			     		<#elseif documento.paciente.genero=="f" && documento.paciente.genero=="F">
			     			Genitales femeninos
			     		</#if>
					</title>
					<text>
						<#if documento.paciente.genero=="m" && documento.paciente.genero=="M">
			   				<table>
			   					<tbody>
			   						<tr>
				       					<td>
				       						Cicatrices
				       					</td>				       					
				       					<td>
				       						<#if documento.cicatricesFGM??>
	       										${documento.cicatricesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Lesiones
				       					</td>				       					
				       					<td>
				       						<#if documento.lesionesFGM??>
	       										${documento.lesionesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Secreciones					
				       					</td>				       					
				       					<td>
				       						<#if documento.secrecionesFGM??>
	       										${documento.secrecionesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       				</tr>				       							       				
									<tr>			       					
				       					<td>
				       						Erupciones
				       					</td>				       					
				       					<td>
				       						<#if documento.erupcionesFGM??>
	       										${documento.erupcionesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Escroto
				       					</td>				       					
				       					<td>
				       						<#if documento.escrotoFGM??>
	       										${documento.escrotoFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Epid&#237;dimo
				       					</td>				       					
				       					<td>
				       						<#if documento.epididimoFGM??>
	       										${documento.epididimoFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>		       					
				       				</tr>				       				
				       				<tr>
				       					<td>
				       						Deferentes
				       					</td>				       					
				       					<td>
				       						<#if documento.deferentesFGM??>
	       										${documento.deferentesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Test&#237;culos
				       					</td>				       					
				       					<td>
				       						<#if documento.testiculosFGM??>
	       										${documento.testiculosFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Pr&#243;stata
				       					</td>				       					
				       					<td>
				       						<#if documento.prostataFGM??>
	       										${documento.prostataFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       				</tr>				       				
				       				<tr>				       					
				       					<td>
				       						Seminales
				       					</td>				       					
				       					<td>
				       						<#if documento.seminalesFGM??>
	       										${documento.seminalesFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Tanner
				       					</td>				       					
				       					<td colspan="3">
				       						<#if documento.tannerFGM??>
	       										${documento.tannerFGM}
				       						<#else>
				       							-
			       							</#if>
				       					</td>	
				       				</tr>
				       				<#if documento.otrosFGM??>
   										<tr>										
											<td colspan="6">
												<paragraph>
												    <caption>
												    	Otros
												    </caption>
												   	${documento.otrosFGM}
											 	</paragraph>
											</td>
										</tr>
									</#if>
			   					</tbody>
			   				</table>
			   			</#if>		   				
		   				<#if documento.paciente.genero=="f" && documento.paciente.genero=="F">
			   				<table>
			   					<tbody>
			   						<tr>
				       					<td>
				       						Labios
				       					</td>				       					
				       					<td>
				       						<#if documento.labiosFGF??>
	       										${documento.labiosFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Bartholino
				       					</td>				       					
				       					<td>
				       						<#if documento.bartholinoFGF??>
	       										${documento.bartholinoFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Circunferencia					
				       					</td>				       					
				       					<td>
				       						<#if documento.circunferenciaFGF??>
	       										${documento.circunferenciaFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>			       					
				       				</tr>				       							       				
									<tr>				       					
				       					<td>
				       						Perin&#233;
				       					</td>				       					
				       					<td>
				       						<#if documento.perineFGF??>
	       										${documento.perineFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Vagina
				       					</td>				       					
				       					<td>
				       						<#if documento.vaginaFGF??>
	       										${documento.vaginaFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Cuello
				       					</td>				       					
				       					<td>
				       						<#if documento.cuelloFGF??>
	       										${documento.cuelloFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>		       					
				       				</tr>				       				
				       				<tr>
				       					<td>
				       						&#218;tero
				       					</td>				       					
				       					<td>
				       						<#if documento.uteroFGF??>
	       										${documento.uteroFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>				       					
				       					<td>
				       						Anexos
				       					</td>				       					
				       					<td>
				       						<#if documento.anexosFGF??>
	       										${documento.anexosFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Parametrios
				       					</td>				       					
				       					<td>
				       						<#if documento.parametriosFGF??>
	       										${documento.parametriosFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       				</tr>				       				
				       				<tr>				       					
				       					<td>
				       						Douglas
				       					</td>				       					
				       					<td>
				       						<#if documento.douglasFGF??>
	       										${documento.douglasFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>
				       					<td>
				       						Tanner
				       					</td>				       					
				       					<td colspan="3">
				       						<#if documento.tannerFGF??>
	       										${documento.tannerFGF}
				       						<#else>
				       							-
			       							</#if>
				       					</td>	
				       				</tr>
				       				<#if documento.otrosFGF??>
   										<tr>										
											<td colspan="6">
												<paragraph>
												    <caption>
												    	Otros
												    </caption>
												   	${documento.otrosFGF}
											 	</paragraph>
											</td>
										</tr>
									</#if>
			   					</tbody>
			   				</table>
			   			</#if>
		   			</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Recto
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Fisuras
			       					</td>				       					
			       					<td>
			       						<#if documento.fisurasFRecto??>
       										${documento.fisurasFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						F&#237;stula
			       					</td>				       					
			       					<td>
			       						<#if documento.fistulaFRecto??>
       										${documento.fistulaFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Hemorroides					
			       					</td>				       					
			       					<td>
			       						<#if documento.hemorroidesFRecto??>
       										${documento.hemorroidesFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Esf&#237;nter
			       					</td>				       					
			       					<td>
			       						<#if documento.esfinterFRecto??>
       										${documento.esfinterFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Tumoraciones
			       					</td>				       					
			       					<td>
			       						<#if documento.tumoracionesFRecto??>
       										${documento.tumoracionesFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Prolapso
			       					</td>				       					
			       					<td>
			       						<#if documento.prolapsoFRecto??>
       										${documento.prolapsoFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Heces
			       					</td>				       					
			       					<td>
			       						<#if documento.hecesFRecto??>
       										${documento.hecesFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Eritema anal
			       					</td>				       					
			       					<td>
			       						<#if documento.eritemaAnalFRecto??>
       										${documento.eritemaAnalFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Tacto rectal
			       					</td>				       					
			       					<td>
			       						<#if documento.tactoRectalFRecto??>
       										${documento.tactoRectalFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>				       				
			       				<tr>				       					
			       					<td>
			       						Rectoscopia
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.rectoscopiaFRecto??>
       										${documento.rectoscopiaFRecto}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFRecto??>
									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFRecto}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Huesos, Articulaciones, M&#250;sculos
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Deformidades
			       					</td>				       					
			       					<td>
			       						<#if documento.deformidadesFHAM??>
       										${documento.deformidadesFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Inflamaciones
			       					</td>				       					
			       					<td>
			       						<#if documento.inflamacionesFHAM??>
       										${documento.inflamacionesFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Rubicundez					
			       					</td>				       					
			       					<td>
			       						<#if documento.rubicundezFHAM??>
       										${documento.rubicundezFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Sensibilidad
			       					</td>				       					
			       					<td>
			       						<#if documento.sensibilidadFHAM??>
       										${documento.sensibilidadFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Movimientos
			       					</td>				       					
			       					<td>
			       						<#if documento.movimientosFHAM??>
       										${documento.movimientosFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Masas musculares
			       					</td>				       					
			       					<td>
			       						<#if documento.masasMuscularesFHAM??>
       										${documento.masasMuscularesFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Epifisitis
			       					</td>				       					
			       					<td>
			       						<#if documento.epifisitisFHAM??>
       										${documento.epifisitisFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Dedos hipocr&#225;ticos
			       					</td>				       					
			       					<td colspan="3">
			       						<#if documento.dedosHipocraticosFHAM??>
       										${documento.dedosHipocraticosFHAM}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFHAM??>
									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFHAM}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Extremidades
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Color
			       					</td>				       					
			       					<td>
			       						<#if documento.colorFExt??>
       										${documento.colorFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Edemas
			       					</td>				       					
			       					<td>
			       						<#if documento.edemasFExt??>
       										${documento.edemasFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Tembores					
			       					</td>				       					
			       					<td>
			       						<#if documento.tembloresFExt??>
       										${documento.tembloresFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Deformidades
			       					</td>				       					
			       					<td>
			       						<#if documento.deformidadesFExt??>
       										${documento.deformidadesFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						&#218;lceras
			       					</td>				       					
			       					<td>
			       						<#if documento.ulcerasFExt??>
       										${documento.ulcerasFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						V&#225;rices
			       					</td>				       					
			       					<td>
			       						<#if documento.varicesFExt??>
       										${documento.varicesFExt}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>
			       				<#if documento.otrosFExt??>
									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFExt}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Neurol&#243;gico y ps&#237;quico
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Sensibilidad objetiva
			       					</td>				       					
			       					<td>
			       						<#if documento.sensibilidadObjetivaFNP??>
       										${documento.sensibilidadObjetivaFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Motilidad
			       					</td>				       					
			       					<td>
			       						<#if documento.motilidadFNP??>
       										${documento.motilidadFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Reflejos					
			       					</td>				       					
			       					<td>
			       						<#if documento.reflejosFNP??>
       										${documento.reflejosFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Escritura
			       					</td>				       					
			       					<td>
			       						<#if documento.escrituraFNP??>
       										${documento.escrituraFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Marcha
			       					</td>				       					
			       					<td>
			       						<#if documento.marchaFNP??>
       										${documento.marchaFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Orientaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.orientacionFNP??>
       										${documento.orientacionFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>		       					
			       				</tr>				       				
			       				<tr>
			       					<td>
			       						Lenguaje
			       					</td>				       					
			       					<td>
			       						<#if documento.lenguajeFNP??>
       										${documento.lenguajeFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Romberg
			       					</td>				       					
			       					<td>
			       						<#if documento.rombregFNP??>
       										${documento.rombregFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Coordinaci&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.coordinacionFNP??>
       										${documento.coordinacionFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>				       				
			       				<tr>				       					
			       					<td>
			       						Psiquismo
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.psiquismoFNP??>
       										${documento.psiquismoFNP}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       				</tr>
			       				<#if documento.otrosFNP??>
									<tr>										
										<td colspan="6">
											<paragraph>
											    <caption>
											    	Otros
											    </caption>
											   	${documento.otrosFNP}
										 	</paragraph>
										</td>
									</tr>
								</#if>
		   					</tbody>
		   				</table>
   					</text>
				</section>
			</component>
			<component>
			   	<section>
			     	<title>
			     		Sensoriales
					</title>
					<text>
		   				<table>
		   					<tbody>
		   						<tr>
			       					<td>
			       						Visi&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.visionFSens??>
       										${documento.visionFSens}
			       						<#else>
			       							-
		       							</#if>
			       					</td>				       					
			       					<td>
			       						Audici&#243;n
			       					</td>				       					
			       					<td>
			       						<#if documento.audicionFSens??>
       										${documento.audicionFSens}
			       						<#else>
			       							-
		       							</#if>
			       					</td>
			       					<td>
			       						Olor					
			       					</td>				       					
			       					<td>
			       						<#if documento.olorFSens??>
       										${documento.olorFSens}
			       						<#else>
			       							-
		       							</#if>
			       					</td>			       					
			       				</tr>				       							       				
								<tr>				       					
			       					<td>
			       						Gusto
			       					</td>				       					
			       					<td colspan="5">
			       						<#if documento.gustoFSens??>
       										${documento.gustoFSens}
			       						<#else>
			       							-
		       							</#if>
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
    		    	<#if documento.diagEnfermedades??>
	    		    	<#if documento.diagEnfermedades?size != 0 >
		    		   		<#if documento.diagFinal>
					    		Diagn&#243;stico final
					    	<#else>
					    		Impresi&#243;n diagn&#243;stica
					    	</#if>
					    <#else>
					    	Diagn&#243;stico
					    </#if>
				    <#else>
				    	Diagn&#243;stico
				    </#if>
    		   	</title>
    		    <text>	
    		    	<#if documento.diagEnfermedades??>
	    		    	<#if documento.diagEnfermedades?size != 0 >	 			
				 			<table>
			   					<tbody>
			   						<tr>
			   							<th colspan="2">
			   								Enfermedades
			   							</th>
			   						</tr>
			   						<tr>
										<th>
											C&#243;digo
										</th>									
										<th>
											Descripci&#243;n
										</th>
									</tr>
									<#list documento.diagEnfermedades as enfermedad>
										<tr>
											<td>
												${enfermedad.codigo}
											</td>
											<td>
												${enfermedad.descripcion}
											</td>
										</tr>
									</#list>
								</tbody>
							</table>
						<#else>
							<table>
								<tbody>
									<tr>
										<td>
					    					Diagn&#243;stico
					    				</td>
					    				<td>
					    					No completado.
					    				</td>
					    			</tr>
					    		</tbody>
							</table>
					    </#if>
				  	<#else>
				    	<table>
							<tbody>
								<tr>
									<td>
				    					Diagn&#243;stico
				    				</td>
				    				<td>
				    					No completado.
				    				</td>
				    			</tr>
				    		</tbody>
						</table>
				    </#if>
		 		</text>			        			
		 	</section>
	 	</component>
	<#if documento.observacionesGenerales?? || documento.conclusionesGenerales?? || documento.estadoEvolucionPaciente??>	
 		<component>
	   	<section>
	     	<title>
	     		Observaciones y conclusiones
	     	</title>
	     	<text>
	     		<#if documento.estadoEvolucionPaciente??>
					<paragraph>
					    <caption>
					    	Estado del paciente
					    </caption>
					   	${documento.estadoEvolucionPaciente}
				 	</paragraph>
			 	</#if>			 	
	     		<#if documento.observacionesGenerales??>
					<paragraph>
					    <caption>
					    	Observaciones
					    </caption>
					   	${documento.observacionesGenerales}
				 	</paragraph>
			 	</#if>
			 	<#if documento.conclusionesGenerales??>
				 	<paragraph>
					    <caption>
					    	Conclusiones
					    </caption>
					   	${documento.conclusionesGenerales}
				 	</paragraph>
				 </#if>
          	</text>
          </section>
      	</component>
     </#if>
   </structuredBody>
 </component>
</ClinicalDocument>