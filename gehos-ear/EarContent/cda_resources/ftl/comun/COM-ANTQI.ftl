 <#macro AntecedenteQuirurgico antecedenteQuirurgico>
 <#if antecedenteQuirurgico?? && antecedenteQuirurgico.antecedentes?? && antecedenteQuirurgico.antecedentes?size !=0??>
 <component>
	<section>
		<title>Antecedentes quir&#250;rgicos</title>
		<text>
			<table>
				<tbody>
				  <tr>
					<th colspan="3">Listado de antecedentes quir&#250;rgicos</th>
				  </tr>
				  <tr>
				    <th>Fecha intervención</th>
					<th>Procedimiento quir&#250;rgico</th>
					<th>Descripci&#243;n</th>
				  </tr>
		       <#list antecedenteQuirurgico.antecedentes as antecedente>
				  <tr>
					<#if antecedente.fecha??>
					   <td>${antecedente.fecha?string("dd/MM/yyyy")}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.procedimiento??>
					   <td>${antecedente.procedimiento}</td>
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