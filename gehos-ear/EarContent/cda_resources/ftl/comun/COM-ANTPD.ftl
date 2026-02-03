<#if documento.problemas?? && documento.problemas?size !=0??>
    <component>
      <section>
        <title>Problemas detectados</title>
        <text>
		    <table>
		    	<tbody>
			      <tr>
			        <th colspan="10">Listado de problemas</th>
			      </tr>
			      <tr>
					<th>Problema</th>
			        <th>Edad paciente</th>
			        <th>Fecha detecci&#243;n</th>
			        <th>Fecha resoluci&#243;n</th>
			        <th>Descripci&#243;n</th>
			      </tr>
			      <#list documento.problemas as problema>
				      <tr>
						<#if problema.nombre??>
						 <td>${problema.nombre}</td>	
						<#else>
						 <td bgcolor="#E3E4E4"></td>
						</#if>
						<#if problema.edad??>
						 <td>${problema.edad}</td>	
						<#else>
						 <td bgcolor="#E3E4E4"></td>
						</#if>
						<#if problema.fechad??>
						 <td>${problema.fechad?string("dd/MM/yyyy")}</td>	
						<#else>
						 <td bgcolor="#E3E4E4"></td>
						</#if>
						<#if problema.fechar??>
						 <td>${problema.fechar?string("dd/MM/yyyy")}</td>	
						<#else>
						 <td bgcolor="#E3E4E4"></td>
						</#if>
						<#if problema.descripcion??>
						 <td>${problema.descripcion}</td>	
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