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
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="CE-H.HO" />
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
	   							<th colspan="6">
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
	     		Datos generales
			</title>
	     	<text>
   				<table>
   					<tbody>
   						<tr>
   							<th colspan="6">
   								Estadiaje cl&#237;nico de inicio
   							</th>
   						</tr>
   						<#if documento.estadioCliniIni??>
	   						<tr>
		       					<td colspan="6">
		       						<paragraph>
									    <caption>
									    	Estad&#237;o
									    </caption>
									   	${documento.estadioCliniIni}
								 	</paragraph>
		       					</td> 		       					
		       				</tr>
		       			</#if>				       							       				
						<tr>
	       					<td>
	       						T	       						
	       					</td>				       					
	       					<td>
	       						<#if documento.cliniIniT??>
	       							${documento.cliniIniT}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						N
	       					</td>				       					
	       					<td>
	       						<#if documento.cliniIniN??>
	       							${documento.cliniIniN}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						M
	       					</td>				       					
	       					<td>
	       						<#if documento.cliniIniM??>
	       							${documento.cliniIniM}
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
   							<th colspan="6">
   								Estadiaje patol&#243;gico de inicio
   							</th>
   						</tr>
   						<#if documento.estadioPatoIni??>
	   						<tr>
	   							<td colspan="6">
		       						<paragraph>
									    <caption>
									    	Estad&#237;o
									    </caption>
									   	${documento.estadioPatoIni}
								 	</paragraph>
		       					</td> 	       					
		       				</tr>
		       			</#if>				       							       				
						<tr>
	       					<td>
	       						T	       						
	       					</td>				       					
	       					<td>
	       						<#if documento.patoIniT??>
	       							${documento.patoIniT}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						N
	       					</td>				       					
	       					<td>
	       						<#if documento.patoIniN??>
	       							${documento.patoIniN}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						M
	       					</td>				       					
	       					<td>
	       						<#if documento.patoIniM??>
	       							${documento.patoIniM}
								<#else>
									-
								</#if>
	       					</td>	       					
	       				</tr>
	       				<#if documento.localizacionPrimaria??>
		       				<tr>			       					
		       					<td colspan="6">
		       						<paragraph>
									    <caption>
									    	Localizaci&#243;n primaria (Topogr&#225;fica)
									    </caption>
									   	${documento.localizacionPrimaria}
								 	</paragraph>
		       					</td>	       					
		       				</tr>
		       			</#if>
		       			<#if documento.tipoHistologico??>
		       				<tr>
		       					<td colspan="6">
		       						<paragraph>
									    <caption>
									    	Tipo histol&#243;gico (Morfolog&#237;a)
									    </caption>
									   	${documento.tipoHistologico}
								 	</paragraph>
		       					</td>       					
		       				</tr>
		       			</#if>	
   					</tbody>
   				</table>
   				<#if documento.extencionClinicTumoresSolidos??>
	   				<paragraph>
					    <caption>
					    	Extensi&#243;n cl&#237;nica de los tumores s&#243;lidos
					    </caption>
					   	${documento.extencionClinicTumoresSolidos}
				 	</paragraph>
				</#if>	   				
   				<table>
   					<tbody>
   						<tr>
   							<th colspan="6">
   								Clasificaci&#243;n de los tumores hematol&#243;gicos
   							</th>
   						</tr>
   						<tr>
   							<td>
	       						Leucemia linfoide aguda (FAB)	       						
	       					</td>				       					
	       					<td>
	       						<#if documento.leucemiaLinfoideAgudaFAB??>
	       							${documento.leucemiaLinfoideAgudaFAB}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Leucemia linfoide cr&#243;nica (RAI)
	       					</td>				       					
	       					<td>
	       						<#if documento.leucemiaLinfoideCronicaRAI??>
	       							${documento.leucemiaLinfoideCronicaRAI}
								<#else>
									-
								</#if>
	       					</td>
   							<td>
	       						Leucemia mieloide aguda	       						
	       					</td>				       					
	       					<td>
	       						<#if documento.leucemiaMieloideAguda??>
	       							${documento.leucemiaMieloideAguda}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Leucemia mieloide cr&#243;nica
	       					</td>				       					
	       					<td>
	       						<#if documento.leucemiaMieloideCronica??>
	       							${documento.leucemiaMieloideCronica}
								<#else>
									-
								</#if>
	       					</td>
   							<td>
	       						Mieloma m&#250;ltiple (Durie-Salmon)    						
	       					</td>				       					
	       					<td>
	       						<#if documento.mielomaMultiple??>
	       							${documento.mielomaMultiple}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Linfomas
	       					</td>				       					
	       					<td>
	       						<#if documento.linfomas??>
	       							${documento.linfomas}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<#if documento.hostiocitosis??>
		       				<tr>
		       					<td colspan="6">
								 	<paragraph>
									    <caption>
									    	Histiocitosis
									    </caption>
									   	${documento.hostiocitosis}
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
	     		Examen f&#237;sico
			</title>
	     	<text>
				<table>
   					<tbody>		       							       				
						<tr>
	       					<td>
	       						E.C.O.G.      						
	       					</td>				       					
	       					<td>
	       						<#if documento.ecog??>
	       							${documento.ecog}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Karnofsky 						
	       					</td>	       								       					
	       					<td>
	       						<#if documento.karnofsky??>
	       							${documento.karnofsky}
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
   							<th colspan="6">
   								Presi&#243;n arterial
   							</th>
   						</tr>			       							       				
						<tr>
	       					<td>
	       						Diast&#243;lica       						
	       					</td>				       					
	       					<td>
	       						<#if documento.PADiastolica??>
	       							${documento.PADiastolica} mmHg
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Sist&#243;lica       						
	       					</td>	       								       					
	       					<td>
	       						<#if documento.PASistolica??>
	       							${documento.PASistolica} mmHg
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Media       						
	       					</td>				       					
	       					<td>
	       						<#if documento.PAMedia??>
	       							${documento.PAMedia} mmHg
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
   							<th colspan="6">
   								Pulso
   							</th>
   						</tr>			       							       				
						<tr>
	       					<td>
	       						Valor   						
	       					</td>				       					
	       					<td>
	       						<#if documento.PValor??>
	       							${documento.PValor} ppm
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
	       					<td>
	       						<#if documento.ubicacionPulso??>
	       							${documento.ubicacionPulso}
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
   							<th colspan="6">
   								Frecuencia respiratoria
   							</th>
   						</tr>			       							       				
						<tr>
	       					<td>
	       						Valor   						
	       					</td>				       					
	       					<td>
	       						<#if documento.FRValor??>
	       							${documento.FRValor} rpm
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
	       		<table>
   					<tbody>
   						<tr>
   							<th colspan="6">
   								Otros signos vitales
   							</th>
   						</tr>			       							       				
						<tr>
	       					<td>
	       						Temperatura					
	       					</td>				       					
	       					<td>
	       						<#if documento.temperatura??>
	       							${documento.temperatura} &#176;C
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Peso					
	       					</td>	       								       					
	       					<td>
	       						<#if documento.peso??>
	       							${documento.peso} Kg
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Talla			
	       					</td>				       					
	       					<td>
	       						<#if documento.talla??>
	       							${documento.talla} cm
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Superficie corporal			
	       					</td>	       								       					
	       					<td colspan="5">
	       						<#if documento.superficieCorporal??>
	       							${documento.superficieCorporal} m2
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
   							<th colspan="6">
   								S&#237;ntomas
   							</th>
   						</tr>
   						<tr>
	       					<td>
	       						Piel
	       					</td>				       					
	       					<td>
	       						<#if documento.piel??>
	       							${documento.piel}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Ojos
	       					</td>				       					
	       					<td>
	       						<#if documento.ojos??>
	       							${documento.ojos}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Cavidad oral      						
	       					</td>				       					
	       					<td>
	       						<#if documento.cavidadOral??>
	       							${documento.cavidadOral}
								<#else>
									-
								</#if>
	       					</td>			       					
	       				</tr>				       							       				
						<tr>				       					
	       					<td>
	       						ORL
	       					</td>				       					
	       					<td>
	       						<#if documento.orl??>
	       							${documento.orl}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						T&#243;rax
	       					</td>				       					
	       					<td>
	       						<#if documento.torax??>
	       							${documento.torax}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Coraz&#243;n
	       					</td>				       					
	       					<td>
	       						<#if documento.corazon??>
	       							${documento.corazon}
								<#else>
									-
								</#if>
	       					</td>		       					
	       				</tr>				       				
	       				<tr>
	       					<td>
	       						Pulmones
	       					</td>				       					
	       					<td>
	       						<#if documento.pulmones??>
	       							${documento.pulmones}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Mamas
	       					</td>				       					
	       					<td>
	       						<#if documento.mamas??>
	       							${documento.mamas}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Axilas
	       					</td>				       					
	       					<td>
	       						<#if documento.axilas??>
	       							${documento.axilas}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>				       				
	       				<tr>				       					
	       					<td>
	       						Abdomen
	       					</td>				       					
	       					<td>
	       						<#if documento.abdomen??>
	       							${documento.abdomen}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Genitales exteriores
	       					</td>				       					
	       					<td>
	       						<#if documento.genitalesExteriores??>
	       							${documento.genitalesExteriores}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Vagina
	       					</td>				       					
	       					<td>
	       						<#if documento.vagina??>
	       							${documento.vagina}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Cuello uterino
	       					</td>				       					
	       					<td>
	       						<#if documento.cuelloUterino??>
	       							${documento.cuelloUterino}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Fondos de saco
	       					</td>				       					
	       					<td>
	       						<#if documento.fondosSaco??>
	       							${documento.fondosSaco}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Anexos
	       					</td>				       					
	       					<td>
	       						<#if documento.anexos??>
	       							${documento.anexos}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<tr>				       					
	       					<td>
	       						Recto
	       					</td>				       					
	       					<td>
	       						<#if documento.recto??>
	       							${documento.recto}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Pr&#243;stata
	       					</td>				       					
	       					<td>
	       						<#if documento.prostata??>
	       							${documento.prostata}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Ganglios
	       					</td>				       					
	       					<td>
	       						<#if documento.ganglios??>
	       							${documento.ganglios}
								<#else>
									-
								</#if>
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Extremidades
	       					</td>				       					
	       					<td>
	       						<#if documento.extremidades??>
	       							${documento.extremidades}
								<#else>
									-
								</#if>
	       					</td>				       					
	       					<td>
	       						Neurol&#243;gico
	       					</td>				       					
	       					<td>
	       						<#if documento.neurologico??>
	       							${documento.neurologico}
								<#else>
									-
								</#if>
	       					</td>
	       					<td>
	       						Descripci&#243;n de examen f&#237;sico
	       					</td>				       					
	       					<td>
	       						<#if documento.descripcionExamenFisico??>
	       							${documento.descripcionExamenFisico}
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
	 <#if documento.tumorPrimario??>
		 <component>
		   	<section>
		     	<title>
		     		Diagn&#243;stico inicial
		     	</title>
		     	<text>
					<paragraph>
					    <caption>
					    	Tumor primario
					    </caption>
					   	${documento.tumorPrimario}
				 	</paragraph>
			   	</text>
		   	</section>
		 </component>
	</#if> 	
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