<#if documento.diagnostico??>
		<component>
		  <section>
			<title>Diagn&#243;stico</title>
			<#if documento.diagnostico.enfermedades?? && documento.diagnostico.enfermedades?size != 0 >
			  
				<table>
					<tbody>
						<tr>
							<th colspan="3">
								Clasificaci&#243;n Internacional de Enfermedades (CIE)
							</th>
						</tr>
						<tr>
							<th>C&#243;digo</th>
							<th>Descripci&#243;n</th>
							<th>Tipo</th>
						</tr>
						<#list documento.diagnostico.enfermedades as enf>
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
			<#else>
			<text>
			 No existe informaci&#243;n a mostrar.
			 </text>
			</#if>
		  </section>
	 </component>
</#if>