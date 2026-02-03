<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="CDA.xsl"?>
<ClinicalDocument xmlns="urn:hl7-org:v3" classCode="DOCCLIN" moodCode="EVN">
 <!-- Estos valores son fijos -->
 <typeId root="2.16.840.1.113883.1.3" extension="POCD_HD000040" />
  <!-- Este es un identificador único que referencia a la plantilla del documento.
 Atributo root: Número de registro maestro para plantillas de documentos clínicos en la organización o institución hospitalaria. 
 Atributo extension: Nomenclador propio de la plantilla a la que se hace referencia-->
 <templateId root="${documento.oidDocumentoClinicoCDA}" extension="${documento.extensionCDA}" />
 <!-- Este es un identificador unico para el documento, que deberemos generar.
 Atributo root: Número de registro maestro para documentos clínicos en la organización o institución hospitalaria.
 Atributo extension: Número generado.
 El par root-extension tiene que ser universalemente único -->
 <id root="${documento.oidDocumentoClinico}" extension="${documento.idUnico}" />
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
<setId extension="${documento.reference}" root="${documento.oidDocumentoClinico}"/>
 <versionNumber value="${documento.versionNumber}"/>
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
					<#if documento.paciente.lugarResidencia.municipio??>
						<city>${documento.paciente.lugarResidencia.municipio}</city>
					</#if>
					<#if documento.paciente.lugarResidencia.parroquia??>
						<censusTract>${documento.paciente.lugarResidencia.parroquia}</censusTract>
					</#if>
					<#if documento.paciente.lugarResidencia.tipoVia??>
						<streetNameType>${documento.paciente.lugarResidencia.tipoVia}</streetNameType>
					</#if>
					<#if documento.paciente.lugarResidencia.nombreVia??>
						<streetNameBase>${documento.paciente.lugarResidencia.nombreVia}</streetNameBase>
					</#if>
					<#if documento.paciente.lugarResidencia.numero??>
						<houseNumber>${documento.paciente.lugarResidencia.numero}</houseNumber>
					</#if>
					
					<!-- datos auxiliares no estan en el estandar -->
					<#if documento.paciente.lugarResidencia.localidad??>
						<local>${documento.paciente.lugarResidencia.localidad}</local>
					</#if>
					<#if documento.paciente.lugarResidencia.tipoZona??>
						<sectorType>${documento.paciente.lugarResidencia.tipoZona}</sectorType>
					</#if>
					<#if documento.paciente.lugarResidencia.nombreZona??>
						<sector>${documento.paciente.lugarResidencia.nombreZona}</sector>
					</#if>
					<#if documento.paciente.lugarResidencia.tipoEdificacion??>
						<buldingType>${documento.paciente.lugarResidencia.tipoEdificacion}</buldingType>
					</#if>
					<#if documento.paciente.lugarResidencia.nombreEdificacion??>
						<building>${documento.paciente.lugarResidencia.nombreEdificacion}</building>
					</#if>
					<#if documento.paciente.lugarResidencia.piso??>
						<floor>${documento.paciente.lugarResidencia.piso}</floor>
					</#if>
					<#if documento.paciente.lugarResidencia.codigoPostal??>
						<postalCode>${documento.paciente.lugarResidencia.codigoPostal}</postalCode>
					</#if>
					<#if documento.paciente.lugarResidencia.puntoReferencia??>
						<reference>${documento.paciente.lugarResidencia.puntoReferencia}</reference>
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
				<#if documento.paciente.nombres?? || documento.paciente.apellido1?? || documento.paciente.apellido2??>
					<!-- Nombre y apellidos del paciente -->
					<name>
						<#if documento.paciente.nombres??>
							<given>${documento.paciente.nombres}</given>
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
					<birthTime value="${fechaHoraFormateador.format(documento.paciente.fechaNacimiento)}"/>
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
								<#if documento.paciente.direccionNacimiento.municipio??>
									<city>${documento.paciente.direccionNacimiento.municipio}</city>
								</#if>
								<#if documento.paciente.direccionNacimiento.parroquia??>
									<censusTract>${documento.paciente.direccionNacimiento.parroquia}</censusTract>
								</#if>
								<#if documento.paciente.direccionNacimiento.tipoVia??>
									<streetNameType>${documento.paciente.direccionNacimiento.tipoVia}</streetNameType>
								</#if>
								<#if documento.paciente.direccionNacimiento.nombreVia??>
									<streetNameBase>${documento.paciente.direccionNacimiento.nombreVia}</streetNameBase>
								</#if>
								<#if documento.paciente.direccionNacimiento.numero??>
									<houseNumber>${documento.paciente.direccionNacimiento.numero}</houseNumber>
								</#if>
								
								<!-- datos auxiliares no estan en el estandar -->
								<#if documento.paciente.direccionNacimiento.localidad??>
									<local>${documento.paciente.direccionNacimiento.localidad}</local>
								</#if>
								<#if documento.paciente.direccionNacimiento.tipoZona??>
									<sectorType>${documento.paciente.direccionNacimiento.tipoZona}</sectorType>
								</#if>
								<#if documento.paciente.direccionNacimiento.nombreZona??>
									<sector>${documento.paciente.direccionNacimiento.nombreZona}</sector>
								</#if>
								<#if documento.paciente.direccionNacimiento.tipoEdificacion??>
									<buldingType>${documento.paciente.direccionNacimiento.tipoEdificacion}</buldingType>
								</#if>
								<#if documento.paciente.direccionNacimiento.nombreEdificacion??>
									<building>${documento.paciente.direccionNacimiento.nombreEdificacion}</building>
								</#if>
								<#if documento.paciente.direccionNacimiento.piso??>
									<floor>${documento.paciente.direccionNacimiento.piso}</floor>
								</#if>
								<#if documento.paciente.direccionNacimiento.codigoPostal??>
									<postalCode>${documento.paciente.direccionNacimiento.codigoPostal}</postalCode>
								</#if>
								<#if documento.paciente.direccionNacimiento.puntoReferencia??>
									<reference>${documento.paciente.direccionNacimiento.puntoReferencia}</reference>
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
					<!-- Identificador del autor.
					Atributo root: Registro maestro para identificadores de médicos
					Atributo extension: Matrícula médica  -->
					<id root="${documento.autor.idRoot}" extension="${documento.autor.idExtension}" />
				<#if documento.autor.prefijo?? || documento.autor.nombres??  || documento.autor.apellido1?? || documento.autor.apellido2??>
					<!-- Nombre de médico, enfermera o técnico autor del documento -->
					<assignedPerson>
						<name>
							<#if documento.autor.prefijo??>
								<prefix>${documento.autor.prefijo}</prefix>
							</#if>
							<#if documento.autor.nombres??>
				 				<given>${documento.autor.nombres}</given>
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
							<id extension="${documento.hospital.id}" root="${documento.hospital.idRoot}" />
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
						<!-- Identificador del centro de custodio
						Atributo root: Registro maestro de custodios
						Atributo extension: Identificador propio del custodio -->
						<id root="${documento.custodio.idRoot}" extension="${documento.custodio.idExtension}"/>
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
					<!-- Identificador del autor.
					Atributo root: Registro maestro para identificadores de médicos
					Atributo extension: Matrícula médica -->
					<id root="${documento.firmante.idRoot}" extension="${documento.firmante.idExtension}" />
				<!-- Nombre de médico -->
				<assignedPerson>
					<name>
						<#if documento.firmante.prefijo??>
							<prefix>${documento.firmante.prefijo}</prefix>
						</#if>
						<#if documento.firmante.nombres??>
							<given>${documento.firmante.nombres}</given>
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
					<!-- Su identificación - ejemplo documento de identidad-->
					<id root="${documento.paciente.padre.idRoot}" extension="${documento.paciente.padre.idExtension}" />
				<!-- El tipo de relación que tiene, en este caso es el PADRE -->
				<code code="FTH" codeSystem="2.16.840.1.113883.5.111" displayName="Padre" />
				<!-- Se podria incluir también dirección y teléfonos (addr y telecom)-->
				      
	        	<#if documento.paciente.padre??>
					<!-- Su(s) nombre(s) y apellido(s) -->
					<associatedPerson>
						<name>
							<#if documento.paciente.padre.nombres??>
								<given>${documento.paciente.padre.nombres}</given>
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
					<!-- Su identificación - ejemplo documento de identidad-->
					<id root="${documento.paciente.madre.idRoot}" extension="${documento.paciente.madre.idExtension}" />
				<code code="MTH" codeSystem="2.16.840.1.113883.5.111" displayName="Madre" />

				<!-- Su(s) nombre(s) y apellido(s) -->
				<associatedPerson>
					<name>
						<#if documento.paciente.madre.nombres??>
							<given>${documento.paciente.madre.nombres}</given>
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
		<relatedDocument typeCode="RPLC">
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
							    <#if documento.paciente.idExtension??>
									 ${documento.paciente.idExtension}
								<#else>
								-
								</#if>
					       	</text>
					       	<#if documento.paciente.fotoPaciente!?length &gt; 0>
					       	<entry>
							  <observationMedia classCode="OBS" moodCode="EVN" ID="patientphoto">
								<value mediaType="image/png" representation="B64">
									${documento.paciente.fotoPaciente}
								</value>
							  </observationMedia>
							</entry>
							</#if>
							
							<#if documento.paciente.pais??>
							<entry>
							  <observationMedia classCode="OBS" moodCode="EVN" ID="nacion">
								<value>
									<#if documento.paciente.pais??>
									${documento.paciente.pais}
									<#else>
									-
									</#if>
								</value>
								
							  </observationMedia>
							</entry>
							</#if>
							
							<#if documento.grupoSanguineo??>
							<entry>
							  <observationMedia classCode="OBS" moodCode="EVN" ID="gruposan">
								<value mediaType="text">
									<#if documento.grupoSanguineo??>
									${documento.grupoSanguineo}${documento.factorSanguineo}
									<#else>
									-
									</#if>
								</value>
							  </observationMedia>
							</entry>
							</#if>
							
						    <#if documento.numerohc??>
							<entry>
							  <observationMedia classCode="OBS" moodCode="EVN" ID="numerohc">
								<value mediaType="text">
									<#if documento.numerohc??>
									${documento.numerohc}
									<#else>
									-
									</#if>
								</value>
							  </observationMedia>
							</entry>
							</#if>						
				      	</section>
		   			</component>
		   		</#if>
