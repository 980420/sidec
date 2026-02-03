<#include "../comun/BEGIN.ftl" />
<#if documento.paciente.representante??>
<component>
	<section>
		<title>
			Datos del representante
		</title>
		<text>
			<table>
				<tbody>
					<tr>
						<td>
							Nombre
						</td>
						<td>
							<#if documento.paciente.representante.nombres??>
								${documento.paciente.representante.nombres}
							<#else>
							-
							</#if>
						</td>
						<td>
							Primer apellido
						</td>
						<td>
							<#if documento.paciente.representante.apellido1??>
								${documento.paciente.representante.apellido1}
							<#else>
							-
							</#if>
						</td>
						<td>
							Segundo apellido
						</td>
						<td>
							<#if documento.paciente.representante.apellido2??>
								${documento.paciente.representante.apellido2}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							C&#233;dula
						</td>
						<td>
							<#if documento.paciente.representante.cedula??>
								${documento.paciente.representante.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Fecha de nacimiento
						</td>
						<td>
							<#if documento.paciente.representante.fechaNacimiento??>
								${documento.paciente.representante.fechaNacimiento?string('dd/MM/yyyy')}
							<#else>
							-
							</#if>
						</td>
						<td>
							Raza
						</td>
						<td>
							<#if documento.paciente.representante.raza??>
								${documento.paciente.representante.raza}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Etnia
						</td>
						<td>
							<#if documento.paciente.representante.cedula??>
								${documento.paciente.representante.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Estado civil
						</td>
						<td>
							<#if documento.paciente.representante.estadoCivil??>
								${documento.paciente.representante.estadoCivil}
							<#else>
							-
							</#if>
						</td>
						<td>
							Pa&#237;s
						</td>
						<td>
							<#if documento.paciente.representante.pais??>
								${documento.paciente.representante.pais}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Tel&#233;fono fijo
						</td>
						<td>
							<#if documento.paciente.representante.telefonoFijo??>
								${documento.paciente.representante.telefonoFijo}
							<#else>
							-
							</#if>
						</td>
						<td>
							Tel&#233;fono celular
						</td>
						<td>
							<#if documento.paciente.representante.telefonoCelular??>
								${documento.paciente.representante.telefonoCelular}
							<#else>
							-
							</#if>
						</td>
						<td>
							Correo electr&#243;nico
						</td>
						<td>
							<#if documento.paciente.representante.correoElectronico??>
								${documento.paciente.representante.correoElectronico}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td >
							Sexo
						</td>
						<td colspan="4">
							<#if documento.paciente.representante.genero??>
								${documento.paciente.representante.genero}
							<#else>
							-
							</#if>
						</td>
						
					</tr>
				</tbody>
			</table>
		<text>
	</section>
</component>
</#if>

<#if documento.paciente.supervisor??>
<component>
	<section>
		<title>
			Datos del supervisor
		</title>
		<text>
			<table>
				<tbody>
					<tr>
						<td>
							Nombre
						</td>
						<td>
							<#if documento.paciente.supervisor.nombres??>
								${documento.paciente.supervisor.nombres}
							<#else>
							-
							</#if>
						</td>
						<td>
							Primer apellido
						</td>
						<td>
							<#if documento.paciente.supervisor.apellido1??>
								${documento.paciente.supervisor.apellido1}
							<#else>
							-
							</#if>
						</td>
						<td>
							Segundo apellido
						</td>
						<td>
							<#if documento.paciente.supervisor.apellido2??>
								${documento.paciente.supervisor.apellido2}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							C&#233;dula
						</td>
						<td>
							<#if documento.paciente.supervisor.cedula??>
								${documento.paciente.supervisor.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Fecha de nacimiento
						</td>
						<td>
							<#if documento.paciente.supervisor.fechaNacimiento??>
								${documento.paciente.supervisor.fechaNacimiento?string('dd/MM/yyyy')}
							<#else>
							-
							</#if>
						</td>
						<td>
							Raza
						</td>
						<td>
							<#if documento.paciente.supervisor.raza??>
								${documento.paciente.supervisor.raza}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Etnia
						</td>
						<td>
							<#if documento.paciente.supervisor.cedula??>
								${documento.paciente.supervisor.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Estado civil
						</td>
						<td>
							<#if documento.paciente.supervisor.estadoCivil??>
								${documento.paciente.supervisor.estadoCivil}
							<#else>
							-
							</#if>
						</td>
						<td>
							Pa&#237;s
						</td>
						<td>
							<#if documento.paciente.supervisor.pais??>
								${documento.paciente.supervisor.pais}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Tel&#233;fono fijo
						</td>
						<td>
							<#if documento.paciente.supervisor.telefonoFijo??>
								${documento.paciente.supervisor.telefonoFijo}
							<#else>
							-
							</#if>
						</td>
						<td>
							Tel&#233;fono celular
						</td>
						<td>
							<#if documento.paciente.supervisor.telefonoCelular??>
								${documento.paciente.supervisor.telefonoCelular}
							<#else>
							-
							</#if>
						</td>
						<td>
							Correo electr&#243;nico
						</td>
						<td>
							<#if documento.paciente.supervisor.correoElectronico??>
								${documento.paciente.supervisor.correoElectronico}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Sexo
						</td>
						<td colspan="4">
							<#if documento.paciente.supervisor.genero??>
								${documento.paciente.supervisor.genero}
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

<#if documento.personaAvisarEmergencia??>
<component>
	<section>
		<title>
			Avisar en caso de emergencia
		</title>
		<text>
			<table>
				<tbody>
					<tr>
						<td>
							Nombre
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.nombres??>
								${documento.personaAvisarEmergencia.nombres}
							<#else>
							-
							</#if>
						</td>
						<td>
							Primer apellido
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.apellido1??>
								${documento.personaAvisarEmergencia.apellido1}
							<#else>
							-
							</#if>
						</td>
						<td>
							Segundo apellido
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.apellido2??>
								${documento.personaAvisarEmergencia.apellido2}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							C&#233;dula
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.cedula??>
								${documento.personaAvisarEmergencia.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Fecha de nacimiento
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.fechaNacimiento??>
								${documento.personaAvisarEmergencia.fechaNacimiento?string('dd/MM/yyyy')}
							<#else>
							-
							</#if>
						</td>
						<td>
							Raza
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.raza??>
								${documento.personaAvisarEmergencia.raza}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Etnia
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.cedula??>
								${documento.personaAvisarEmergencia.cedula}
							<#else>
							-
							</#if>
						</td>
						<td>
							Estado civil
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.estadoCivil??>
								${documento.personaAvisarEmergencia.estadoCivil}
							<#else>
							-
							</#if>
						</td>
						<td>
							Pa&#237;s
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.pais??>
								${documento.personaAvisarEmergencia.pais}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td>
							Tel&#233;fono fijo
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.telefonoFijo??>
								${documento.personaAvisarEmergencia.telefonoFijo}
							<#else>
							-
							</#if>
						</td>
						<td>
							Tel&#233;fono celular
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.telefonoCelular??>
								${documento.personaAvisarEmergencia.telefonoCelular}
							<#else>
							-
							</#if>
						</td>
						<td>
							Correo electr&#243;nico
						</td>
						<td>
							<#if documento.personaAvisarEmergencia.correoElectronico??>
								${documento.personaAvisarEmergencia.correoElectronico}
							<#else>
							-
							</#if>
						</td>
					</tr>
					<tr>
						<td >
							Sexo
						</td>
						<td colspan="4">
							<#if documento.personaAvisarEmergencia.genero??>
								${documento.personaAvisarEmergencia.genero}
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

<#if documento.descripcion??>
<component>
	<section>
		<title>
			Descripci&#243;n
		</title>
			<text>
			<#if documento.descripcion??>
				${documento.descripcion}
			</#if>		
			</text>
	</section>
</component>
</#if>		
<#include "../comun/END.ftl" />