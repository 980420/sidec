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
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="HO-H.P" />

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
           <id root="${documento.paciente.hcRoot}" extension="${documento.paciente.hcExtension}" />                     <!-- Direccion de residencia del paciente -->
           <#if documento.paciente.lugarResidencia??>                 <addr>
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
           </#if>               <#if documento.paciente.telefonos??>
               <#if documento.paciente.telefonos?size != 0 >
                   <#list documento.paciente.telefonos as telefono>
                       <!-- Telefonos del paciente si tiene -->
                       <telecom value="${telefono}" />
                   </#list>
               </#if>
           </#if>
           <patient>                               <#if documento.paciente.nombre?? || documento.paciente.apellido1?? || documento.paciente.apellido2??>
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
               </#if>                       <!-- Género del paciente
               Posibles valores del atributo code: ver vocabulario HL7 v3 -->
               <administrativeGenderCode code="${documento.paciente.genero}" codeSystem="2.16.840.1.113883.5.1" />                               <#if documento.paciente.fechaNacimiento??>
                   <!-- Fecha y hora de nacimiento del paciente
                   en formato ISO 8601 -->
                   <birthTime value="${fechaFormateador.format(documento.paciente.fechaNacimiento)}"/>
               </#if>                      <#if documento.paciente.estadoCivil??>
                   <!-- Estado civil
                   Posibles valores del atributo code: ver vocabulario HL7 v3 -->
                   <maritalStatusCode code="${documento.paciente.estadoCivil}" codeSystem="2.16.840.1.113883.5" />
               </#if>
               <#if documento.paciente.raza??>
                   <!-- Codigo de raza
                   Posibles valores del atributo code: ver vocabulario HL7 v3 -->                     <raceCode code="${documento.paciente.raza}" codeSystem="2.16.840.1.113883.5" />
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
                   </birthplace>                 </#if>                       </patient>
       </patientRole>
   </recordTarget>       <#if documento.autor??>
       <!-- Datos del autor del documento -->
       <author>
           <!-- Fecha y hora en que lo crea, en formato ISO 8601 -->
           <time value="${fechaHoraFormateador.format(documento.fechaHoraAutoria)}" />               <!-- Datos del autor del documento -->
           <assignedAuthor>
               <#if documento.autor.idRoot?? && documento.autor.idExtension??>
                   <!-- Identificador del autor.
                   Atributo root: Registro maestro para identificadores de médicos
                   Atributo extension: Matrícula médica  -->
                   <id root="${documento.autor.idRoot}" extension="${documento.autor.idExtension}" />
               </#if>                               <#if documento.autor.prefijo?? || documento.autor.nombre??  || documento.autor.apellido1?? || documento.autor.apellido2??>
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
               </#if>                               <#if documento.hospital??>
                   <!-- Identificador y nombre del hospital para el cual el médico trabaja -->
                   <representedOrganization>
                       <#if documento.hospital.id??>
                           <id root="${documento.hospital.id}" />
                       </#if>
                       <#if documento.hospital.nombre??>
                           <name>${documento.hospital.nombre}</name>
                       </#if>
                   </representedOrganization>
               </#if>               </assignedAuthor>
       </author>
   </#if>       <#if documento.custodio??>
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
                   </#if>                   </representedCustodianOrganization>
           </assignedCustodian>
       </custodian>
   </#if>       <#if documento.firmante??>
       <!-- Datos del firmante del documento (debe ser un médico) -->
       <legalAuthenticator>               <!-- Fecha y hora de firma -->
           <time value="${fechaHoraFormateador.format(documento.fechaHoraFirma)}"/>                   <!-- Código de firma -->
           <signatureCode code="S"/>               <!-- Datos de la entidad firmante -->
           <assignedEntity>
               <#if documento.firmante.idRoot?? && documento.firmante.idExtension??>
                   <!-- Identificador del autor.
                   Atributo root: Registro maestro para identificadores de médicos
                   Atributo extension: Matrícula médica -->
                   <id root="${documento.firmante.idRoot}" extension="${documento.firmante.idExtension}" />
               </#if>                               <!-- Nombre de médico -->
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
               </assignedPerson>               </assignedEntity>
       </legalAuthenticator>
   </#if>       <#if documento.paciente.padre??>
       <!-- Participant va justo antes de component - es el último de los
       elementos del header.
       Un participant para el padre, es un participante "indirecto" ya que el documento CDA R2 es
       acerca de su hijo-->           <participant typeCode="IND">               <!-- Su participación es en el rol de 'familiar' (next of kin) -->
           <associatedEntity classCode="NOK">
               <#if documento.paciente.padre.idRoot?? && documento.paciente.padre.idExtension??>
                   <!-- Su identificación - ejemplo documento de identidad-->
                   <id root="${documento.paciente.padre.idRoot}" extension="${documento.paciente.padre.idExtension}" />
               </#if>                               <!-- El tipo de relación que tiene, en este caso es el PADRE -->
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
               </#if>                                             <#if documento.paciente.padre.telefonos??>
                   <#if documento.paciente.padre.telefonos?size != 0 >
                       <#list documento.paciente.padre.telefonos as telefono>
                           <!-- Telefonos, si tiene -->
                           <telecom value="${telefono}" />
                       </#list>
                   </#if>
               </#if>                           <#if documento.paciente.padre??>
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
               </#if>                               </associatedEntity>
       </participant>
   </#if>
   <#if documento.paciente.madre??>
       <!-- Idem para la madre -->
       <participant typeCode="IND">
           <associatedEntity classCode="NOK">
               <#if documento.paciente.madre.idRoot?? && documento.paciente.madre.idExtension??>
                   <!-- Su identificación - ejemplo documento de identidad-->
                   <id root="${documento.paciente.madre.idRoot}" extension="${documento.paciente.madre.idExtension}" />
               </#if>                                       <code code="MTH" codeSystem="2.16.840.1.113883.5.111" displayName="Madre" />
               <!-- Se podria incluir también dirección y teléfonos (addr y telecom)-->                  <#if documento.paciente.madre.lugarResidencia??>
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
               </#if>                         <#if documento.paciente.madre.telefonos??>
                   <#if documento.paciente.madre.telefonos?size != 0 >
                       <#list documento.paciente.madre.telefonos as telefono>
                           <!-- Telefonos, si tiene -->
                           <telecom value="${telefono}" />
                       </#list>
                   </#if>
               </#if>                               <!-- Su(s) nombre(s) y apellido(s) -->
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
               </associatedPerson>               </associatedEntity>
       </participant>
   </#if>
   <#if documento.documentoRelacionado??>
       <!-- Documento relacionado al presente documento -->
       <relatedDocument typeCode="APND">
           <parentDocument classCode="DOCCLIN" moodCode="EVN">                   <!--Id del documento relacionado -->
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
				
				<#if documento.AntPrenatObst>           	
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
									${documento.cantcontrolesAPObst}
								</td>
								<td>
									Complicaci&#243;n embarazo
								</td>										
								<td>
									<#if documento.complicacionEmbarazoAPObst>
										S&#237;
									<#else>
										No
									</#if>
								</td>
							</tr>
	   						<tr>										
								<td>
									Complicaci&#243;n Parto
								</td>										
								<td>
									<#if documento.complicacionPartoAntAPObst>
										S&#237;
									<#else>
										No
									</#if>
								</td>
								<td>
									Edad gestacional
								</td>										
								<td>
									${documento.edadGestacionalAPObst}
								</td>
							</tr>								
							<tr>										
								<td>
									Tipo de edad gestacional
								</td>										
								<td>
									<#if documento.tipoEdadGestacionalAPObst??>
										${documento.tipoEdadGestacionalAPObst}
									</#if>
								</td>
								<td>
									Tipo de parto
								</td>										
								<td>
									<#if documento.tipoEdadGestacionalAPObst??>
										${documento.tipoPartoAPObst}
									</#if>
								</td>
							</tr>	
							<#if documento.asistenciaAPObst??>									
								<tr>
									<td>
										Asistencia
									</td>										
									<td colspan="3">
										${documento.asistenciaAPObst}
									</td>
								</tr>
							</#if>
							<#if documento.otrosAPObst??>	
								<tr>										
									<td colspan="4">
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
   				
   				<#if documento.PeriodoNeonatal>   				
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="4">
	   								Per&#237;odo neonatal
	   							</th>
	   						</tr>
	   						<tr>
	   							<#if documento.tipoRespiracionPNeonatal??>
									<td>
										Tipo de respiraci&#243;n
									</td>										
									<td>
										${documento.tipoRespiracionPNeonatal}
									</td>
								</#if>
								<td>
									Cianosis
								</td>										
								<td>
									<#if documento.cianosisPNeonatal>
										S&#237;
									<#else>
										No
									</#if>	
								</td>	
							</tr>
							<tr>
								<td>
									Malformaciones
								</td>										
								<td>
									<#if documento.malformacionesPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>									
								<td>
									Oftalm&#237;a
								</td>										
								<td>
									<#if documento.oftalmiaPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>
							</tr>
							<tr>
								<td>
									Fiebre
								</td>										
								<td>
									<#if documento.fiebrePNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>	
								<td>
									Coriza
								</td>										
								<td>
									<#if documento.corizaPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>
							</tr>
							<tr>									
								<td>
									Hemorragia
								</td>										
								<td>
									<#if documento.hemorragiaPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>
								<td>
									V&#243;mitos
								</td>										
								<td>
									<#if documento.vomitosPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>	
							</tr>
							<tr>
								<td>
									Ictericia
								</td>										
								<td>
									<#if documento.ictericiaPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>									
								<td>
									Convulsiones
								</td>										
								<td>
									<#if documento.convulsionesPNeonatal>
										S&#237;
									<#else>
										No
									</#if>
								</td>
							</tr>
							<#if documento.otrosPNeonatal??> 
								<tr>										
									<td colspan="4">
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
   				
   				<#if documento.Alimentacion>   				
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="4">
	   								Alimentaci&#243;n
	   							</th>
	   						</tr>
	   						<tr>		   								   						
								<td>
									Destete
								</td>										
								<td>
									${documento.desteteAlimentac}
								</td>									
								<td>
									Cereales
								</td>										
								<td>
									${documento.cerealesAlimentac}
								</td>
							</tr>
							<tr>
								<td>
									Sopas
								</td>										
								<td>
								
									${documento.sopasAlimentac}
								</td>										
								<td>
									Vegetales
								</td>										
								<td>
									${documento.vegetalesAlimentac}
								</td>
							</tr>
							<tr>
								<td>
									Frutas
								</td>										
								<td>
									${documento.frutasAlimentac}
								</td>
								<td>
									Huevos
								</td>										
								<td>
									${documento.huevosAlimentac}
								</td>
							</tr>
							<tr>										
								<td>
									Carne
								</td>										
								<td>
									${documento.carnesAlimentac}
								</td>
								<td>
									Vitaminas
								</td>										
								<td>
									${documento.vitaminasAlimentac}
								</td>
							</tr>
							<tr>										
								<td>
									Natural
								</td>										
								<td>										
									${documento.naturalAlimentac}										
								</td>
								<td>
									Artificial
								</td>										
								<td>
									${documento.artificialAlimentac}
								</td>
							</tr>
							<tr>										
								<td>
									Mixta
								</td>										
								<td>
									${documento.mixtaAlimentac}
								</td>
								<td>
									Dieta actual
								</td>										
								<td>
									${documento.dietaActualAlimentac}
								</td>
							</tr>
	   					</tbody>
	   				</table>
   				</#if>
   				
   				<#if documento.Desarrollo>
	   				<table>
	   					<tbody>
	   						<tr>
	   							<th colspan="4">
	   								Desarrollo
	   							</th>
	   						</tr>
	   						<tr>
								<td>
									Grado de escuela
								</td>										
								<td>
									${documento.gradoEscolarDesarr}
								</td>										
								<td>
									Sostuvo la cabeza a los
								</td>										
								<td>
									${documento.edadSostuvoCabezaDesarr} meses
								</td>
							</tr>	
							<tr>
								<td>
									Se sent&#243; a los
								</td>										
								<td>
									${documento.edadPudoSentarseDesarr} meses
								</td>										
								<td>
									Se par&#243; a los
								</td>										
								<td>
									${documento.edadPudoPararseDesarr} meses
								</td>
							</tr>									
							<tr>
								<td>
									Camin&#243; a los
								</td>										
								<td>
									${documento.edadPudoCaminarDesarr} meses
								</td>										
								<td>
									Control&#243; esfinter a los
								</td>										
								<td>
									${documento.edadControlEsfinterDesarr} meses
								</td>
							</tr>
							<tr>
								<td>
									Primer diente a los
								</td>										
								<td>
									${documento.primerDienteDesarr} meses
								</td>										
								<td>
									Primeras palabras a los
								</td>										
								<td>
									${documento.primerasPalabrasDesarr} meses
								</td>
							</tr>
							<tr>										
								<td colspan="4">
									<paragraph>
									    <caption>
									    	Progreso en la escuela
									    </caption>
									   	${documento.progresoEscolarDesarr}
								 	</paragraph>
								</td>
							</tr>
							<tr>										
								<td colspan="4">
									<paragraph>
									    <caption>
									    	Progreso en el peso
									    </caption>
									   	${documento.progresoPesoDesarr}
								 	</paragraph>
								</td>
							</tr>
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
															<th colspan="4">
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
															<th colspan="4">
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
	     	<text>
   				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						General
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
							Progreso de peso y talla
							</td>
							<td>
							${documento.generalProgresoPesoTalla}
							</td>
							<td>
							Sudores
							</td>
							<td>
							${documento.generalSudores}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
							Debilidad
							</td>
							<td>
							${documento.generalDebilidad}
							</td>
	       					<td>
							Fatiga
							</td>
							<td>
							${documento.generalFatiga}
							</td>		       					
	       				</tr>
						<tr>
							<td colspan="4">
								<paragraph>
								    <caption>
								    	Otros
								    </caption>
								   	${documento.generalOtros}
							 	</paragraph>
							</td>
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Piel
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
							Dermatosis
							</td><td>
							${documento.pielDermatosis}
							</td>
							<td>
							Ictericia
							</td>
							<td>
							${documento.pielIctericia}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
							Prurito
							</td>
							<td>
							${documento.pielPrurito}
							</td>
							<td>
							Edemas
							</td>
							<td>
							${documento.pielEdemas}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
							Cianosis
							</td>
							<td>
							${documento.pielCianosis}
							</td>
							<td>
							Otros
							</td>
							<td>
							${documento.pielOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Cabeza
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
								Dolor
							</td>
							<td>
								${documento.cabezaDolor}
							</td>
							<td>
								Ca&#237;da del cabello
							</td>
							<td>
								${documento.cabezaCaidaDelCabello}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Mareo
							</td>
							<td>
								${documento.cabezaMareo}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.cabezaOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Ojos
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
								Cansancio
							</td>
							<td>
								${documento.ojosCansancio}
							</td>
							<td>
								Amaurosis
							</td>
							<td>
								${documento.ojosAmaurosis}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Diplop&#237;a
							</td>
							<td>
								${documento.ojosDiplopia}
							</td>
							<td>
								Dolor
							</td>
							<td>
								${documento.ojosDolor}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Fotofobia
							</td>
							<td>
								${documento.ojosFotofobia}
							</td>
							<td>
								Anteojos
							</td>
							<td>
								${documento.ojosAnteojos}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Lagrimeo
							</td>
							<td>
								${documento.ojosLagrimeo}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.ojosOtros}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Nistagmus
							</td>
							<td>
								${documento.ojosNistagmus}
							</td>
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						O&#237;dos
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
								Sordera
							</td>
							<td>
								${documento.oidosSordera}
							</td>
							<td>
								Dolor
							</td>
							<td>
								${documento.oidosDolor}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Secreciones
							</td>
							<td>
								${documento.oidosSecreciones}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.oidosOtros}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Tinnitus
							</td>
							<td>
								${documento.oidosTinnitus}
							</td>
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Nariz
	       					</th>
	       				</tr>				       							       				
						<tr>
	       					<td>
								Epistaxis
							</td>
							<td>
								${documento.narizEpistaxis}
							</td>
							<td>
								Halitosis nasales
							</td>
							<td>
								${documento.narizHalitosisNasales}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Sinusitis
							</td>
							<td>
								${documento.narizSinusitis}
							</td>
							<td>
								Dolor senos accesorios
							</td>
							<td>
								${documento.narizDolorSenosAccesorios}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Obstrucci&#243;n
							</td>
							<td>
								${documento.narizObstruccion}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.narizOtros}
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Secreci&#243;n
							</td>
							<td>
								${documento.narizSecrecion}
							</td>
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Boca
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Mucosa
							</td>
							<td>
								${documento.bocaMucosa}
							</td>
	       					<td>
								Halitosis
							</td>
							<td>
								${documento.bocaHalitosis}
							</td>
	       				</tr>
						<tr>
	       					<td>
								Dientes
							</td>
							<td>
								${documento.bocaDientes}
							</td>			       					
							<td>
								Otros
							</td>
							<td>
								${documento.bocaOtros}
							</td>			       					
	       				</tr>
						<tr>
							<td>
								Enc&#237;as
							</td>
							<td>
								${documento.bocaEncias}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Garganta
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Dolor
							</td>
							<td>
								${documento.gargantaDolor}
							</td>
	       					<td>
								Disfagia
							</td>
							<td>
								${documento.gargantaDisfagia}
							</td>
	       				</tr>
						<tr>
							<td>
								Ronquera
							</td>
							<td>
								${documento.gargantaRonquera}
							</td>
                            <td>
								Otros
							</td>
							<td>
								${documento.gargantaOtros}
							</td>							
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Respiratorio
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Dolor tor&#225;cico
							</td>
							<td>
								${documento.respiratorioDolorToracico}
							</td>
	       					<td>
								Disnea
							</td>
							<td>
								${documento.respiratorioDisnea}
							</td>
	       				</tr>
						<tr>
	       					<td>
								Hemoptisis
							</td>
							<td>
								${documento.respiratorioHemoptisis}
							</td>
							<td>
								Silbidos y roncus
							</td>
							<td>
								${documento.respiratorioSilbidosRoncus}
							 	
							</td>			       					
	       				</tr>
						<tr>
	       					<td>
								Tos
							</td>
							<td>
								${documento.respiratorioTos}
							</td>			       					
							<td>
								Estridor
							</td>
							<td>
								${documento.respiratorioEstridor}
							</td>			       					
	       				</tr>
						<tr>
							<td>
								Expectoraci&#243;n
							</td>
							<td>
								${documento.respiratorioExpectoracion}
							</td>	
							<td>
								Otros
							</td>
							<td>
								${documento.respiratorioOtros}
							</td>
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Muscular y osteo-articular
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Debilidad
							</td>
							<td>
								${documento.muscularOsteoArticularDebilidad}
							</td>
							<td>
								Fracturas
							</td>
							<td>
								${documento.muscularOsteoArticularFracturas}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Artralgias
							</td>
							<td>
								${documento.muscularOsteoArticularArtralgias}
							</td>
							<td>
								Deformaciones
							</td>
							<td>
								${documento.muscularOsteoArticularDeformaciones}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Dolores &#243;seos
							</td>
							<td>
								${documento.muscularOsteoArticularDoloresOseos}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.muscularOsteoArticularOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Cardiovascular
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Dolor
							</td>
							<td>
								${documento.cardiovascularDolor}
							</td>
							<td>
								Desmayos
							</td>
							<td>
								${documento.cardiovascularDesmayos}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Disnea
							</td>
							<td>
								${documento.cardiovascularDisnea}
							</td>
							<td>
								Edema
							</td>
							<td>
								${documento.cardiovascularEdema}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Palpitaciones
							</td>
							<td>
								${documento.cardiovascularPalpitaciones}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.cardiovascularOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Gastrointestinal
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Apetito
							</td>
							<td>
								${documento.gastrointestinalApetito}
							</td>
							<td>
								Diarrea
							</td>
							<td>
								${documento.gastrointestinalDiarrea}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Dolor
							</td>
							<td>
								${documento.gastrointestinalDolor}
							</td>
							<td>
								Heces
							</td>
							<td>
								${documento.gastrointestinalHeces}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Malestar
							</td>
							<td>
								${documento.gastrointestinalMalestar}
							</td>
							<td>
								Par&#225;sitos
							</td>
							<td>
								${documento.gastrointestinalParasitos}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Nauseas
							</td>
							<td>
								${documento.gastrointestinalNauseas}
							</td>
							<td>
								Prolapso rectal
							</td>
							<td>
								${documento.gastrointestinalProlapsoRectal}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								V&#243;mitos
							</td>
							<td>
								${documento.gastrointestinalVomitos}
							</td>
							<td>
								F&#237;stula ano-rectal
							</td>
							<td>
								${documento.gastrointestinalFistula}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Pirosis
							</td>
							<td>
								${documento.gastrointestinalPirosis}
							</td>
							<td>
								Hemorroides
							</td>
							<td>
								${documento.gastrointestinalHemorroides}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Flatulencia
							</td>
							<td>
								${documento.gastrointestinalFlatulencia}
							</td>
							<td>
								Hernias
							</td>
							<td>
								${documento.gastrointestinalHernias}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Constipaci&#243;n
							</td>
							<td>
								${documento.gastrointestinalConstipacion}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.gastrointestinalOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Genitourinario
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Secreciones
							</td>
							<td>
								${documento.genitourinarioSecreciones}
							</td>
							<td>
								Hematuria
							</td>
							<td>
								${documento.genitourinarioHematuria}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								&#218;lceras
							</td>
							<td>
								${documento.genitourinarioUlceras}
							</td>
							<td>
								Menarqu&#237;a
							</td>
							<td>
								${documento.genitourinarioMenarquia}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Micci&#243;n
							</td>
							<td>
								${documento.genitourinarioMiccion}
							</td>
							<td>
								Menstruaci&#243;n
							</td>
							<td>
								${documento.genitourinarioMenstruacion}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Enuresis
							</td>
							<td>
								${documento.genitourinarioEnuresis}
							</td>
							<td>
								Flujos
							</td>
							<td>
								${documento.genitourinarioFlujos}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Incontinencia
							</td>
							<td>
								${documento.genitourinarioIncontinencia}
							</td>
							<td>
								Piuria
							</td>
							<td>
								${documento.genitourinarioPiuria}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Disuria
							</td>
							<td>
								${documento.genitourinarioDisuria}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.genitourinarioOtros}
							</td>			       					
	       				</tr>
   					</tbody>
   				</table>
   				
				<table>
   					<tbody>   						
   						<tr>
	       					<th colspan="4">
	       						Nervioso y mental
	       					</th>
	       				</tr>				       							       				
						<tr>
						    <td>
								Esfera afectiva
							</td>
							<td>
								${documento.nerviosoYMentalEsferaAfectiva}
							</td>
							<td>
								Est&#225;tica
							</td><td>
						    	${documento.nerviosoYMentalEstatica}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Esfera intelectual
							</td>
							<td>
								${documento.nerviosoYMentalEsferaIntelectual}
							</td>
							<td>
								Marcha
							</td>
							<td>
								${documento.nerviosoYMentalMarcha}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Esfera volitiva
							</td>
							<td>
								${documento.nerviosoYMentalEsferaVolitiva}
							</td>
							<td>
								Par&#225;lisis
							</td>
							<td>
								${documento.nerviosoYMentalParalisis}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Tics
							</td>
							<td>
								${documento.nerviosoYMentalTics}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.nerviosoYMentalOtros}
							</td>			       					
	       				</tr>
						<tr>
						    <td>
								Convulsiones
							</td>
							<td>
								${documento.nerviosoYMentalConvulsiones}
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
	     		Signos vitales
	     	</title>
	     	<text>
				<table>
					<tbody>
						<tr>
							<td>
								Edad
							</td>
							<td>							                      
			                      ${documento.edad}
			                </td>
						</tr>
						<tr>
							<th colspan="4">
								Presi&#243;n arterial
							</th>
						</tr>
						<tr>
							<td>
								Diast&#243;lica
							</td>
							<td>
								${documento.presionDiastolica} mmHg
							</td>
							<td>
								Sist&#243;lica
							</td>
							<td>
								${documento.presionSistolica} mmHg
							</td>
							<td>
								Media
							</td>
							<td>
								${documento.presionMedia} mmHg
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Otros signos vitales
							</th>
						</tr>
						<tr>
							<td>
								Temperatura
							</td>
							<td>
								${documento.temperatura} C
							</td>
							<td>
								Talla
							</td>
							<td>
								${documento.talla} cm
							</td>
							<td>
								Peso
							</td>
							<td>
								${documento.peso} kg
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
	     		Examen F&#205;sico
	     	</title>
	     	<text>
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Estado General
							</th>
						</tr>
						<tr>
							<td>
								Condiciones generales
							</td><td>
								${documento.estadoGeneralCondicionesGenerales}
							</td>
							<td>
								Nutrici&#243;n y desarrollo
							</td>
							<td>
								${documento.estadoGeneralNutricionDesarrollo}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otras
					             	</caption>
					              	${documento.estadoGeneralOtras}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Piel,ped&#237;cudo adiposo y faneras
							</th>
						</tr>
						<tr>
							<td>
								Color
							</td>
							<td>
								${documento.pielPedicudoFanerasColor}
							</td>
							<td>
								Edema
							</td>
							<td>
								${documento.pielPedicudoFanerasEdema}
							</td>
						</tr>
						<tr>
							<td>
								Humedad
							</td>
							<td>
								${documento.pielPedicudoFanerasHumedad}
							</td>
							<td>
								U&#241;as
							</td>
							<td>
								${documento.pielPedicudoFanerasUnas}
							</td>
						</tr>
						<tr>
							<td>
								Contextura
							</td>
							<td>
								${documento.pielPedicudoFanerasContextura}
							</td>
							<td>
								N&#243;dulos
							</td>
							<td>
								${documento.pielPedicudoFanerasNodulos}
							</td>
						</tr>
						<tr>
							<td>
								Pigmentaci&#243;n
							</td>
							<td>
								${documento.pielPedicudoFanerasPigmentacion}
							</td>
							<td>
								Pelos
							</td>
							<td>
								${documento.pielPedicudoFanerasPelos}
							</td>
						</tr>
						<tr>
							<td>
								Equimosis
							</td>
							<td>
								${documento.pielPedicudoFanerasEquimosis}
							</td>
							<td>
								Vascularizaci&#243;n
							</td>
							<td>
								${documento.pielPedicudoFanerasVascularizacion}
							</td>
						</tr>
						<tr>
							<td>
								Cianosis
							</td>
							<td>
								${documento.pielPedicudoFanerasCianosis}
							</td>
							<td>
								Cicatrices
							</td>
							<td>
								${documento.pielPedicudoFanerasCicatrices}
							</td>
						</tr>
						<tr>
							<td>
								Erupci&#243;n
							</td>
							<td>
								${documento.pielPedicudoFanerasErupcion}
							</td>
							<td>
								&#218;lceras
							</td>
							<td>
								${documento.pielPedicudoFanerasUlceras}
							</td>
						</tr>
						<tr>
							<td>
								Pan&#237;culo adiposo
							</td>
							<td>
								${documento.pielPedicudoFanerasPaniculoAdiposo}
							</td>
							<td>
								F&#237;stulas
							</td>
							<td>
								${documento.pielPedicudoFanerasFistulas}
							</td>
						</tr>
						<tr>
							<td>
								Turgor
							</td>
							<td>
								${documento.pielPedicudoFanerasTurgor}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.pielPedicudoFanerasOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Cabeza
							</th>
						</tr>
						<tr>
							<td>
								Configuraci&#243;n
							</td>
							<td>
								${documento.cabezaConfiguracion}
							</td>
							<td>
								Craneotabes
							</td>
							<td>
								${documento.cabezaCraneotabes}
							</td>
						</tr>
						<tr>
							<td>
								Cantonales
							</td>
							<td>
								${documento.cabezaCantonales}
							</td>
							<td>
								Cabellos
							</td>
							<td>
								${documento.cabezaCabellos}
							</td>
						</tr>
						<tr>
							<td>
								Suturas
							</td>
							<td>
								${documento.cabezaSuturas}
							</td>
							<td>
								Circunferencia
							</td>
							<td>
								${documento.cabezaCircunferencia}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.cabezaOtrosEF}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Ojos
							</th>
						</tr>
						<tr>
							<td>
								Conjuntiva
							</td>
							<td>
								${documento.ojosConjuntiva}
							</td>
							<td>
								Nistagmos
							</td>
							<td>
								${documento.ojosNistagmos}
							</td>
						</tr>
						<tr>
							<td>
								Escler&#243;tica
							</td>
							<td>
								${documento.ojosEsclerotica}
							</td>
							<td>
								Ptosis
							</td>
							<td>
								${documento.ojosPtosis}
							</td>
						</tr>
						<tr>
							<td>
								Cornea
							</td>
							<td>
								${documento.ojosCornea}
							</td>
							<td>
								Exoftalmos
							</td>
							<td>
								${documento.ojosExoftalmos}
							</td>
						</tr>
						<tr>
							<td>
								Pupila
							</td>
							<td>
								${documento.ojosPupila}
							</td>
							<td>
								Oftalmoscopios
							</td>
							<td>
								${documento.ojosOftalmoscopios}
							</td>
						</tr>
						<tr>
							<td>
								Movimientos
							</td>
							<td>
								${documento.ojosMovimientos}
							</td>
							<td>
								Desviaciones
							</td>
							<td>
								${documento.ojosDesviaciones}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.ojosOtrosEF}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								O&#237;dos
							</th>
						</tr>
						<tr>
							<td>
								Pabell&#243;n
							</td>
							<td>
								${documento.oidosPabellon}
							</td>
							<td>
								Secreciones
							</td>
							<td>
								${documento.oidosSecrecionesEF}
							</td>
						</tr>
						<tr>
							<td>
								Canal externo
							</td>
							<td>
								${documento.oidosCanalExterno}
							</td>
							<td>
								Mastoides
							</td><td>
								${documento.oidosMastoides}
							</td>
						</tr>
						<tr>
							<td>
								T&#237;mpano
							</td>
							<td>
								${documento.oidosTimpano}
							</td>
							<td>
								Dolor
						    </td>
							<td>
								${documento.oidosDolorEF}
							</td>
						</tr>
						<tr>
							<td>
								Audici&#243;n
							</td>
							<td>
								${documento.oidosAudicion}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.oidosOtrosEF}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Rinofaringe
							</th>
						</tr>
						<tr>
							<td>
								Fosas nasales
							</td>
							<td>
								${documento.rinofaringeFosasNasales}
							</td>
							<td>
								Am&#237;gdalas
							</td>
							<td>
								${documento.rinofaringeAmigdalas}
							</td>
						</tr>
						<tr>
							<td>
								Mucosas
							</td>
							<td>
								${documento.rinofaringeMucosas}
							</td>
							<td>
								Faringes
							</td>
							<td>
								${documento.rinofaringeFaringes}
							</td>
						</tr>
						<tr>
							<td>
								Secreci&#243;n nasal
							</td>
							<td>
								${documento.rinofaringeSecrecionNasal}
							</td>
							<td>
								Adenoides
							</td>
							<td>
								${documento.rinofaringeAdenoides}
							</td>
						</tr>
						<tr>
							<td>
								Tabique
							</td>
							<td>
								${documento.rinofaringeTabique}
							</td>
							<td>
								Secreci&#243;n postnasal
							</td>
							<td>
								${documento.rinofaringeSecrecionPostnasal}
							</td>
						</tr>
						<tr>
							<td>
								Senos accesorios
							</td>
							<td>
								${documento.rinofaringeSenosAccesorios}
							</td>
							<td>
								Disfagia
							</td>
							<td>
								${documento.rinofaringeDisfagia}
							</td>
						</tr>
						<tr>
							<td>
								Diafanoscop&#237;a
							</td>
							<td>
								${documento.rinofaringeDiafanoscopia}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.rinofaringeOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Boca
							</th>
						</tr>
						<tr>
							<td>
								Aliento
							</td>
							<td>
								${documento.bocaAliento}
							</td>
							<td>
								Lengua
							</td>
							<td>
								${documento.bocaLengua}
							</td>
						</tr>
						<tr>
							<td>
								Labios
							</td>
							<td>
								${documento.bocaLabios}
							</td>
							<td>
								Conductores salivares
							</td>
							<td>
						   	 	${documento.bocaConductoresSalivares}
							</td>
						</tr>
						<tr>
							<td>
								Dientes
							 </td>
							 <td>
								${documento.bocaDientesEF}
							</td>
							<td>
								Par&#225;lisis y trismo
							</td>
							<td>
								${documento.bocaParalisisYTrismo}
							</td>
						</tr>
						<tr>
							<td>
								Enc&#237;as
							</td><td>
								${documento.bocaEnciasEF}
							</td>
							<td>
								Mucosas
						    </td>
							<td>
								${documento.bocaMucosas}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.bocaOtrosEF}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Cuello
							</th>
						</tr>
						<tr>
							<td>
								Movilidad
							</td>
							<td>
								${documento.cuelloMovilidad}
							</td>
							<td>
								Vasos
							</td>
							<td>
								${documento.cuelloVasos}
							</td>
						</tr>
						<tr>
							<td>
								Tumoraci&#243;n
							</td>
							<td>
								${documento.cuelloTumoracion}
							</td>
							<td>
								Tr&#225;quea
							</td>
							<td>
								${documento.cuelloTraquea}
							</td>
						</tr>
						<tr>
							<td>
								Tiroides
							</td>
							<td>
								${documento.cuelloTiroides}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.cuelloOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								G&#225;nglios linf&#225;ticos
							</th>
						</tr>
						<tr>
							<td>
								Cervicales
							</td>
							<td>
								${documento.gangliosLinfaticosCervicales}
							</td>
							<td>
								Inguinales
							</td>
							<td>
								${documento.gangliosLinfaticosInguinales}
							</td>
						</tr>
						<tr>
							<td>
								Occipitales
							</td>
							<td>
								${documento.gangliosLinfaticosOccipitales}
							</td>
							<td>
								Epritroclares
							</td>
							<td>
								${documento.gangliosLinfaticosEpritroclares}
							</td>
						</tr>
						<tr>
							<td>
								Supraclaviculares
							</td>
							<td>
								${documento.gangliosLinfaticosSupraclaviculares}
							</td>
							<td>
								Axilares
							</td>
							<td>
								${documento.gangliosLinfaticosAxilares}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.gangliosLinfaticosOtros}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								T&#243;rax y pulmones
							</th>
						</tr>
						<tr>
							<td>
								Forma
							</td>
							<td>
								${documento.toraxPulmonesForma}
							</td>
							<td>
								Palpaci&#243;n
							</td>
							<td>
								${documento.toraxPulmonesPalpitacion}
							</td>
						</tr>
						<tr>
							<td>
								Simetr&#237;a
							</td>
							<td>
								${documento.toraxPulmonesSimetria}
							</td>
							<td>
								Respiraci&#243;n
							</td>
							<td>
						    	${documento.toraxPulmonesRespiracion}
							</td>
						</tr>
						<tr>
							<td>
								Expansi&#243;n
							</td>
							<td>
								${documento.toraxPulmonesExpansion}							 	
							</td>
							<td>
								Respiraciones por minuto
						    </td>
							<td>
								${documento.toraxPulmonesRespiracionesMinuto}
							</td>
						</tr>
						<tr>
							<td>
								Percusi&#243;n
							</td>
							<td>
								${documento.toraxPulmonesPercusion}
							</td>
							<td>
								Ruidos adventicios
							</td>
							<td>
								${documento.toraxPulmonesRuidosAdventicios}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.toraxPulmonesOtros}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Coraz&#243;n y vasos sangu&#237;neos
							</th>
						</tr>
						<tr>
							<td>
								Rengl&#243;n precordial
							</td>
							<td>
								${documento.corazonVasosSanguineosRenglonPrecordial}
							</td>
							<td>
								Soplos
							</td>
							<td>
								${documento.corazonVasosSanguineosSoplos}
							</td>
						</tr>
						<tr>
							<td>
								Latido de la punta
							</td>
							<td>
								${documento.corazonVasosSanguineosLatidoPunta}
							</td>
							<td>
								Tensi&#243;n arterial
							</td>
							<td>
								${documento.corazonVasosSanguineosTensionArterial}
							</td>
						</tr>
						<tr>
							<td>
								Thrill
							</td>
							<td>
								${documento.corazonVasosSanguineosThrill}
							</td>
							<td>
								Pulso
							</td>
							<td>
								${documento.corazonVasosSanguineosPulso}
							</td>
						</tr>
						<tr>
							<td>
								Ritmo
							</td>
							<td>
								${documento.corazonVasosSanguineosRitmo}
							</td>
							<td>
								Vasos perif&#233;ricos
							</td>
							<td>
								${documento.corazonVasosSanguineosVasosPerifericos}
							</td>
						</tr>
						<tr>
							<td>
								Ruido
							</td>
							<td>
								${documento.corazonVasosSanguineosRuido}
							</td>
							<td>
								Vascularizaci&#243;n
							</td>
							<td>
								${documento.corazonVasosSanguineosVascularizacion}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.corazonVasosSanguineosOtros}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Abdomen
							</th>
						</tr>
						<tr>
							<td>
								Circunferencia
							</td>
							<td>
								${documento.abdomenCircunferencia}
							</td>
							<td>
								Defensa
							</td>
							<td>
								${documento.abdomenDefensa}
							</td>
						</tr>
						<tr>
							<td>
								Aspecto
							</td>
							<td>
								${documento.abdomenAspecto}
							</td>
							<td>
								Tumoraciones
							</td>
							<td>
								${documento.abdomenTumoraciones}
							</td>
						</tr>
						<tr>
							<td>
								Peristesis
							</td>
							<td>
								${documento.abdomenPeristesis}
							</td>
							<td>
								Ascitis
							</td>
							<td>
								${documento.abdomenAscitis}
							</td>
						</tr>
						<tr>
							<td>
								Cicatrices
							</td>
							<td>
								${documento.abdomenCicatrices}
							</td>
							<td>
								H&#237;gado
							</td>
							<td>
								${documento.abdomenHigado}
							</td>
						</tr>
						<tr>
							<td>
								Dolor
							</td>
							<td>
								${documento.abdomenDolor}
							</td>
							<td>
								Bazo
							</td>
							<td>
								${documento.abdomenBazo}
							</td>
						</tr>
						<tr>
							<td>
								Hiperestesia
							</td>
							<td>
								${documento.abdomenHiperestesia}
							</td>
							<td>
								Hernias
							</td>
							<td>
								${documento.abdomenHernias}
							</td>
						</tr>
						<tr>
							<td>
								Contractura
							</td>
							<td>
								${documento.abdomenContractura}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.abdomenOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Urinario
							</th>
						</tr>
						<tr>
							<td>
								Ri&#241;ones
							</td>
							<td>
								${documento.urinarioRinones}
							</td>
							<td>
								Orina
							</td>
							<td>
								${documento.urinarioOrina}
							</td>
						</tr>
						<tr>
							<td>
								Puntos dolorosos
							</td>
							<td>
								${documento.urinarioPuntosDolorosos}
							</td>
							<td>
								Micci&#243;n
							</td>
							<td>
								${documento.urinarioMiccion}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.urinarioOtros}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Genitales
							</th>
						</tr>
						<tr>
							<td>
								Cicatrices
							</td>
							<td>
								${documento.genitalesCicatrices}
							</td>
							<td>
								Test&#237;culos
							</td>
							<td>
								${documento.genitalesTesticulos}
							</td>
						</tr>
						<tr>
							<td>
								Lesiones
							</td>
							<td>
							${documento.genitalesLesiones}
							</td>
							<td>
								Pr&#243;stata
							</td>
							<td>
								${documento.genitalesProstata}
							</td>
						</tr>
						<tr>
							<td>
								Secreciones
							</td>
							<td>
								${documento.genitalesSecreciones}
							</td>
							<td>
								Ves&#237;culas seminales
							</td>
							<td>
								${documento.genitalesVesiculasSeminales}
							</td>
						</tr>
						<tr>
							<td>
								Escroto
							</td>
							<td>
								${documento.genitalesEscroto}
							</td>
							<td>
								Ovarios
							</td>
							<td>
								${documento.genitalesOvarios}
							</td>
						</tr>
						<tr>
							<td>
								Epid&#237;dimo
							</td>
							<td>
								${documento.genitalesEpididimo}
							</td>
							<td>
								Externos
							</td>
							<td>
								${documento.genitalesExternos}
							</td>
						</tr>
						<tr>
							<td>
								Conducto deferente
							</td>
							<td>
								${documento.genitalesConductoDeferente}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.genitalesOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Ano y recto
							</th>
						</tr>
						<tr>
							<td>
								Fisura
							</td>
							<td>
								${documento.anoRectoFisura}
							</td>
							<td>
								Hemorroides
							</td>
							<td>
								${documento.anoRectoHemorroides}
							</td>
						</tr>
						<tr>
							<td>
								F&#237;stula
							</td>
							<td>
								${documento.anoRectoFistula}
							</td>
							<td>
								Eritema anal
							</td>
							<td>
								${documento.anoRectoEritemaAnal}
							</td>
						</tr>
						<tr>
							<td>
								Prolapso
							</td>
							<td>
								${documento.anoRectoProlapso}
							</td>
							<td>
								Tacto rectal
							</td>
							<td>
								${documento.anoRectoTactoRectal}
							</td>
						</tr>
						<tr>
							<td>
								Esf&#237;nter
							</td>
							<td>
								${documento.anoRectoEsfinter}
							</td>
							<td>
								Rectoscop&#237;a
							</td>
							<td>
								${documento.anoRectoRectoscopia}
							</td>
						</tr>
						<tr>
							<td>
								Tumoraciones
							</td>
							<td>
								${documento.anoRectoTumoraciones}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.anoRectoOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Huesos, articulaciones y m&#250;sculos
							</th>
						</tr>
						<tr>
							<td>
								Deformaciones
							</td>
							<td>
								${documento.huesosArticulacionesMusculosDeformaciones}
							</td>
							<td>
								Nistagmos
							</td>
							<td>
								${documento.huesosArticulacionesMusculosLimitacionMovimiento}
							</td>
						</tr>
						<tr>
							<td>
								Inflamaciones
							</td>
							<td>
								${documento.huesosArticulacionesMusculosInflamaciones}
							</td>
							<td>
								Masas musculares
							</td>
							<td>
								${documento.huesosArticulacionesMusculosMasasMusculares}
							</td>
						</tr>
						<tr>
							<td>
								Epifisitis
							</td>
							<td>
								${documento.huesosArticulacionesMusculosEpifisitis}
							</td>
							<td>
								Dedos hipocr&#225;ticos
							</td>
							<td>
								${documento.huesosArticulacionesMusculosDedosHipocraticos}
							</td>
						</tr>
						<tr>
							<td>
								Sensibilidad
							</td>
							<td>
								${documento.huesosArticulacionesMusculosSensibilidad}
							</td>
							<td>
								Equimosis
							</td>
							<td>
								${documento.huesosArticulacionesMusculosEquimosis}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.huesosArticulacionesMusculosOtros}
				 	           	</paragraph>
				 	       	</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Neurol&#243;gico y f&#237;sico
							</th>
						</tr>
						<tr>
							<td>
								Motilidad
							</td>
							<td>
								${documento.neurologicoFisicoMotilidad}
							</td>
							<td>
								Lenguaje
							</td>
							<td>
								${documento.neurologicoFisicoLenguaje}
							</td>
						</tr>
						<tr>
							<td>
								Reflejos
							</td>
							<td>
								${documento.neurologicoFisicoReflejos}
							</td>
							<td>
								Escritura
							</td>
							<td>
								${documento.neurologicoFisicoEscritura}
							</td>
						</tr>
						<tr>
							<td>
								Sensibilidad objetiva
							</td>
							<td>
								${documento.neurologicoFisicoSensibilidadObjetiva}
							</td>
							<td>
								Orientaci&#243;n
							</td>
							<td>
								${documento.neurologicoFisicoOrientacion}
							</td>
						</tr>
						<tr>
							<td>
								Coordinaci&#243;n
							</td>
							<td>
								${documento.neurologicoFisicoCoordinacion}
							</td>
							<td>
								Psiquismo
							</td>
							<td>
								${documento.neurologicoFisicoPsiquismo}
							</td>
						</tr>
						<tr>
							<td>
								Tr&#225;ficos
							</td>
							<td>
								${documento.neurologicoFisicoTraficos}
							</td>
							<td>
								Otros
							</td>
							<td>
								${documento.neurologicoFisicoOtros}
							</td>
						</tr>
					</tbody>
				</table>
				
				<table>
					<tbody>
						<tr>
							<th colspan="4">
								Sensoriales
							</th>
						</tr>
						<tr>
							<td>
								Visi&#243;n
							</td>
							<td>
								${documento.sensorialesVision}
							</td>
							<td>
								Gusto
							</td>
							<td>
								${documento.sensorialesGusto}
							</td>
						</tr>
						<tr>
							<td>
								Audici&#243;n
							</td>
							<td>
								${documento.sensorialesAudicion}
							</td>
							<td>
								Olor
							</td>
							<td>
								${documento.sensorialesOlor}
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<paragraph>
					              	<caption>
					    	       		Otros
					             	</caption>
					              	${documento.sensorialesOtros}
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
	     		Otros datos
	     	</title>
	     	<text>
				<table>
					<tbody>
						<tr>
							<td>
							    <paragraph>
					              	<caption>
					    	       		Resumen
					             	</caption>
					              	${documento.resumen}
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
