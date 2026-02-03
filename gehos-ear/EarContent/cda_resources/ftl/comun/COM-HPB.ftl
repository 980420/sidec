<#macro HabitoPsDoc habitoPs>
 <#if habitoPs.antecedentes?? && habitoPs.antecedentes?size !=0??>
 <component>
	<section>
		<title>H&#225;bitos psicobiol&#243;gicos</title>
		<text>
			<table>
				<tbody>
				  <tr>
					<th colspan="4">Listado de h&#225;bitos</th>
				  </tr>
				  <tr>
					<th>H&#225;bito</th>
					<th>Fecha inicio</th>
					<th>Fecha fin</th>
					<th>Descripci&#243;n</th>
				  </tr>
		       <#list habitoPs.antecedentes as antecedente>
				  <tr>
					<#if antecedente.nombre??>
					   <td>${antecedente.nombre}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.desde??>
					   <td>${antecedente.desde?string("dd/MM/yyyy")}</td>
					<#else>
						<td bgcolor="#E3E4E4"></td>
					</#if>
					<#if antecedente.hasta??>
					   <td>${antecedente.hasta?string("dd/MM/yyyy")}</td>
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