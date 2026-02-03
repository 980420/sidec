<#include "BEGIN.ftl" />
<#macro session lista>
 
 	<#if lista??&&lista?size !=0>
 	   <#list lista as it>
 		<component>
		  <section>
			<title>${it.etiqueta}</title>
			<#if it.valor??>
				<text>
					${it.valor}
				</text>
			<#elseif it.table??>
				${it.table}				
			</#if>	
			<@session lista=it.contenido/>
		  </section>
		</component>
 		 </#list>
 	</#if>
 
</#macro>

<@session lista=documento.list/>



<#include "CONSULTA-COMMON.ftl" />
<#include "END.ftl" />