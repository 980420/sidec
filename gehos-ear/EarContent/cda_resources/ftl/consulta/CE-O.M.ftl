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
	<templateId root="${documento.oidDocumentoClinicoCDA}" extension="HO-H.UG" />

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
	     		Ubicaci&#243;n
	     	</title>
	     	<text>
				<table>
   					<tbody>   												
						<tr>
							<td>
								Servicio
							</td>										
							<td>
								${documento.servicioU}
							</td>
							<td>
								Habitaci&#243;n
							</td>
							<td>
								${documento.habitacionU}
							</td>							
						</tr>		
						<tr>
							<td>
								Cama
							</td>										
							<td colspan="3">
								${documento.camaU}
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
	       		Medicamentos y observaciones
	       	</title>
           	<text>
           		<paragraph>
				    <caption>
				    	Observaciones
				    </caption>
				   	${documento.observacionesMO}
			 	</paragraph>
           		
           		<#if documento.medicamentosindicados??>
           			<#if documento.medicamentosindicados?size != 0 >
		           		<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="4">
		   								Listado de medicamentos Indicados
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Medicamento
									</th>										
									<th>
										Dosis
									</th>	
									<th>
										V&#237;a
									</th>									
									<th>
										Horario
									</th>
								</tr>
								<#list documento.medicamentosindicados as medicamento>
									<tr>
										<td>
											${medicamento.nombre}
										</td>										
										<td>
											${medicamento.dosis}
										</td>
										<td>
											${medicamento.via}
										</td>
										<td>
											${medicamento.horario}
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
				
				<#if documento.medicamentosOpcionales??>
					<#if documento.medicamentosOpcionales?size !=0 >
		              	<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="2">
		   								Listado de medicamentos seleccionados como opcionales
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Medicamento
									</th>									
									<th>
										Descripci&#243;n
									</th>
								</tr>
								<#list documento.medicamentosOpcionales as medicamento>
									<tr>
										<td>
											${medicamento.nombre}
										</td>
										<td>
											${medicamento.descripcion}
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
	     		Orden de dieta
	     	</title>
	     	<text>
				<table>
   					<tbody>
   						<tr>
	       					<td>
	       						Tipo de dieta
	       					</td>				       					
	       					<td colspan="3">
	       						${documento.tipoDieta}
	       					</td>
	       				</tr>
   						<tr>				       					
	       					<td colspan="4">
	       						<paragraph>
								    <caption>
								    	Observaci&#243;n
								    </caption>
								   	${documento.observacionOD}
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
	     		Solicitud de nutrici&#243;n parenteral
	     	</title>
	     	<text>
		     	<#if documento.componentesNP??>
			     	<#if documento.componentesNP?size !=0 >
			       		<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="3">
		   								Listado de componentes de nutrici&#243;n parenteral indicados
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Componente
									</th>	
									<th>
										Cantidad
									</th>								
									<th>
										Porciento
									</th>
								</tr>
								<#list documento.componentesNP as componenteNP>
									<tr>
										<td>
											${componenteNP.nombre}
										</td>
										<td>
											${componenteNP.cantidad}
										</td>
										<td>
											${componenteNP.porciento}
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
				
				<table>
					<tbody>
						<tr>
	       					<td>
	       						Osmolaridad
	       					</td>				       					
	       					<td>
	       						${documento.osmolaridadNP}
	       					</td>
	       					<td>
	       						cc/hora
	       					</td>				       					
	       					<td>
	       						${documento.ccHoraNP}
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						gotas/min
	       					</td>				       					
	       					<td>
	       						${documento.gotasMinNP}
	       					</td>
	       					<td>
	       						cc/kg
	       					</td>				       					
	       					<td>
	       						${documento.ccKgNP}
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						cc Total
	       					</td>				       					
	       					<td>
	       						${documento.ccTotalNP}
	       					</td>
	       					<td>
	       						Horario
	       					</td>				       					
	       					<td>
	       						${documento.horarioNP}
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Fecha de suspensi&#243;n
	       					</td>				       					
	       					<td colspan="3">
	       						${fechaFormateador.format(documento.fechaSuspencionNP)}
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
	     		Solicitud de preparaci&#243;n intravenosa
	     	</title>
	     	<text>	     	
		     	<#if documento.medicamentosPrepIntravenosa??>
			     	<#if documento.medicamentosPrepIntravenosa?size !=0 >
			     		<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="2">
		   								Listado de medicamentos indicados para la preparaci&#243;n intravenosa
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Medicamento
									</th>	
									<th>
										Concentraci&#243;n
									</th>
								</tr>
								<#list documento.medicamentosPrepIntravenosa as medicamento>
									<tr>
										<td>
											${medicamento.nombre}
										</td>
										<td>
											${medicamento.concentracion}
										</td>
									</tr>
								</#list>
							</tbody>
						</table>
					</#if>
				</#if>
				
				<table>
					<tbody>
						<tr>
	       					<td>
	       						Soluci&#243;n
	       					</td>				       					
	       					<td>
	       						${documento.solucionPI}
	       					</td>
	       					<td>
	       						Cantidad
	       					</td>				       					
	       					<td>
	       						${documento.cantidadPI}
	       					</td>
	       				</tr>
	       				<tr>
	       					<td>
	       						Horario
	       					</td>				       					
	       					<td>
	       						${documento.horarioPI}
	       					</td>
	       					<td>
	       						Fecha de suspensi&#243;n
	       					</td>				       					
	       					<td>
	       						${fechaFormateador.format(documento.fechaSuspencionPI)}
	       					</td>
	       				</tr>
	       			</tbody>
	       		</table>
          </text>
        </section>
      </component>  
 
	<#if documento.formulasOficiales??>
     	<#if documento.formulasOficiales?size !=0 >
     		<component>
			   	<section>
			     	<title>
			     		F&#243;rmulas oficiales
			     	</title>
			     	<text>
			     		<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="6">
		   								Listado de f&#243;rmulas oficinales indicadas
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Nombre
									</th>	
									<th>
										Dosis
									</th>
									<th>
										Volumen
									</th>	
									<th>
										Cantidad
									</th>
									<th>
										Horario
									</th>	
									<th>
										Fecha de culminaci&#243;n
									</th>
								</tr>
								<#list documento.formulasOficiales as formula>
									<tr>
										<td>
											${formula.nombre}
										</td>
										<td>
											${formula.dosis}
										</td>
										<td>
											${formula.volumen}
										</td>
										<td>
											${formula.cantidad}
										</td>
										<td>
											${formula.horario}
										</td>
										<td>
											${fechaFormateador.format(formula.fechaCulminacion)}
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
  	
  	<#if documento.formulasOficiales??>
  		<#if documento.formulasOficiales?size !=0 >
		  	<component>
			   	<section>
			     	<title>
			     		F&#243;rmulas magistrales
			     	</title>	     	
					<#list documento.formulasOficiales as formula>
						<component>
						   	<section>
						     	<text>				     		
						     		<table>
					   					<tbody>
					   						<tr>
					   							<th colspan="5">
					   								${formula.nombre}
					   							</th>
					   						</tr>
					   						<tr>
												<th>
													Dosis
												</th>
												<th>
													Volumen
												</th>	
												<th>
													Cantidad
												</th>
												<th>
													Horario
												</th>	
												<th>
													Fecha de culminaci&#243;n
												</th>
											</tr>									
											<tr>
												<td>
													${formula.dosis}
												</td>
												<td>
													${formula.volumen}
												</td>
												<td>
													${formula.cantidad}
												</td>
												<td>
													${formula.horario}
												</td>
												<td>
													${fechaFormateador.format(formula.fechaCulminacion)}
												</td>
											</tr>
										</tbody>
									</table>
									
									<#if formula.productos??>
										<#if formula.productos?size !=0 >
											<table>
												<tbody>
													<tr>
														<th colspan="2">
															Concentraci&#243;n de productos
														</th>
													</tr>
													<tr>
														<th>
															Producto
														</th>
														<th>
															Concentraci&#243;n
														</th>
													</tr>
													<#list formula.productos as producto>
														<tr>
															<td>
																${producto.nombre}
															</td>
															<td>
																${producto.concentracion}
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
					</#list>
		        </section>
		  	</component>
		  </#if>
	  </#if>
 	
	 <#if documento.tratamientos??>
 		<#if documento.tratamientos?size !=0 >
     		<component>
			   	<section>
			     	<title>
			     		Indicaci&#243;n de otros tratamientos
					</title>
			     	<text>
		   				<table>
		   					<tbody>
		   						<tr>
		   							<th colspan="3">
		   								Listado de tratamientos
		   							</th>
		   						</tr>
		   						<tr>
									<th>
										Tratamiento
									</th>	
									<th>
										Horario
									</th>								
									<th>
										Fecha de suspenci&#243;n
									</th>
								</tr>
								<#list documento.tratamientos as tratamiento>
									<tr>
										<td>
											${tratamiento.nombre}
										</td>
										<td>
											${tratamiento.horario}
										</td>
										<td>
											${fechaFormateador.format(tratamiento.fechaSuspencion)}
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
   </structuredBody>
 </component>
</ClinicalDocument>