 <#macro AntecedenteFamiliar antecedenteFamiliar>
 <#if documento.antecedentes?? && documento.antecedentes?size !=0??>
 <component>
	<section>
		<title>Antecedentes familiares</title>
		<text>
			<table>
				<tbody>
				  	<tr>
						<th colspan="3">Listado de antecedentes</th>
				  	</tr>
				  	<tr>
						<th>Antecedente</th>
						<th>Parentesco</th>
						<th>Descripci&#243;n</th>
				  	</tr>
	       			<#list antecedenteFamiliar.antecedentes as antecedente>
				  	<tr>
					<#if antecedente.nombre??>
					   <td>${antecedente.nombre}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.parentesco??>
					   <td>${antecedente.parentesco}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.descripcion??>
					   <td>${antecedente.descripcion}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					 </tr>
				   	</#list>
				</tbody>
			</table>
		</text>
	</section>
</component>
</#if>
 </#macro>