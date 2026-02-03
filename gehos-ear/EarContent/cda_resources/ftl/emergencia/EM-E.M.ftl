<#include "BEGIN.ftl" />
<component>
	<section>
	  	<title>
	   		Datos de la evoluci&#243;n
	   	</title>
	   <text>
	       <obs>
				<h1>Observaciones</h1>
				<#if documento.observaciones??>
					<p>${documento.observaciones}</p>
				<#else>
					<p>-</p>
				</#if>
			</obs>
		</text>
	        
    <#if documento.especialidad??>
		<component>
	    	<section>
			    <title>Especialidad seleccionada</title>
			    <table>
					<tbody>
						<tr>
							<th colspan="3">Especialidad seleccionada</th>
						</tr>
						<tr>
							<th>Nombre</th>
							<th>Tipo</th>
						</tr>
						<tr>
						    <#if documento.especialidad.nombre??>
								<td>${documento.especialidad.nombre}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
							<#if documento.especialidad.tipo??>
								<td>${documento.especialidad.tipo}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
						</tr>
					</tbody>
				</table>
			</section>
		</component>
	</#if>
	<component>
		<section>
    		<title>Diagnóstico</title>
    		<table>
				<tbody>
					<tr>
						<th colspan="3">Listado de enfermedades</th>
					</tr>
					<tr>
						<th>Código</th>
						<th>Descripción</th>
						<th>Tipo</th>
					</tr>
					<#list documento.enfermedadesImpDiag as enf>
						<tr>
						    <#if enf.codigo??>
								<td>${enf.codigo}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
							<#if enf.descripcion??>
								<td>${enf.descripcion}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
							<#if enf.tipo??>
								<td>${enf.tipo}</td>
							<#else>
								<td bgcolor="#E3E4E4"></td>
							</#if>
						</tr>
					</#list>
					</tbody>
				</table>
    	</section>
    </component>
	</section>
</component>	
	
<#include "END.ftl" />