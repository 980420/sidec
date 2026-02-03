<#macro AntecedentePersonal antecedentePersonal>
 <#if antecedentePersonal.antecedentes?? && antecedentePersonal.antecedentes?size !=0??>
 <component>
	<section>
		<title>Antecedentes personales</title>
		<text>
			<table>
				<tbody>
				  <tr>
					<th colspan="3">Listado de antecedentes</th>
				  </tr>
				  <tr>
					<th>Antecedente</th>
					<th>Fecha</th>
					<th>Descripci&#243;n</th>
				  </tr>
		       <#list antecedentePersonal.antecedentes as antecedente>
				  <tr>
					<#if antecedente.nombre??>
					   <td>${antecedente.nombre}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.fecha??>
					   <td>${antecedente.fecha?string("dd/MM/yyyy")}</td>
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