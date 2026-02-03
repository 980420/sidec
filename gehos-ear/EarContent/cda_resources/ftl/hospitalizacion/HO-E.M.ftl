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
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="HO-E.M" />

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
   	   <#if documento.antPersonales??>
   	   		<#if documento.antPersonales?size !=0 >
		      <component>
		        <section>
		        	<title>
			       		Antecedentes personales
			       	</title>
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
			   	</section>
			 </component>
		 </#if>
	 </#if>
	 
	 <#if documento.antFamiliares??>
		 <#if documento.antFamiliares?size !=0 >
			 <component>
			   	<section>
			     	<title>
			     		Antecedentes familiares
			     	</title>
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
			   	</section>
			 </component>
		 </#if>	
	 </#if>			
	 
	 <#if documento.habitos??>
	 	<#if documento.habitos?size != 0 >
			 <component>
			   	<section>
			     	<title>
			     		H&#225;bitos psicobiol&#243;gicos
			     	</title>
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
			   	</section>
			 </component>
		 </#if>
	 </#if>
	 
	 <#if documento.antPrenatObst>
		 <component>
		   	<section>
		     	<title>
		     		Antecedentes prenatales y obst&#233;tricos
		     	</title>
	           		<table>
	   					<tbody>
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
		   	</section>
		 </component>
	 </#if>
	 
	 <#if documento.periodoNeonatal>
		 <component>
		   	<section>
		     	<title>
		     		Per&#237;odo neonatal
		     	</title>
	   				<table>
	   					<tbody>
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
		   	</section>
		 </component>
	 </#if>
	 
	 <#if documento.alimentacion>  
		 <component>
		   	<section>
		     	<title>
		     		Alimentaci&#243;n
		     	</title>
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
		   	</section>
		 </component>
	 </#if>
	 
	 <#if documento.desarrollo>
		 <component>
		   	<section>
		     	<title>
		     		Desarrollo
		     	</title>
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
		   	</section>
		 </component>
	 </#if>
	 
	 <#if documento.inmunizaciones??>
		 <#if documento.inmunizaciones?size != 0 >
		   	<component>
	    		<section>        		
	    		   	<title>Inmunizaciones</title>		    		   	
	    		   	<#list documento.inmunizaciones as inmunizacion>
		    		   	<component>
				    		<section>        		
				    		   	<title>Vacuna ${inmunizacion.nombre}</title>
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
				    		</section>
				    	</component> 
			    	</#list>       			
	    		</section>
	    	</component>
		</#if>
	</#if>
	 
	<#if documento.estadoEvolucionPaciente?? || documento.observaciones??> 	
		 <component>
		   	<section>
		     	<title>
		     		Datos de la evoluci&#243;n
		     	</title>
		     		<#if documento.estadoEvolucionPaciente??>
					 	<paragraph>
						    <caption>
						    	Estado del paciente
						    </caption>
						   	${documento.estadoEvolucionPaciente}
					 	</paragraph>
				 	</#if>
				 	
				 	<#if documento.observaciones??>
					 	<paragraph>
						    <caption>
						    	Observaciones
						    </caption>
						   	${documento.observaciones}
					 	</paragraph>
				 	</#if>
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
					    	Diagn&#243;stico no completado.
					    </#if>
				    <#else>
				    	Diagn&#243;stico no completado.
				    </#if>
		 	</section>
	 	</component>
   </structuredBody>
 </component>
</ClinicalDocument>