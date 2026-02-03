<#macro AntecedenteDesarrollo antecedenteDesarrollo>
<#if antecedenteDesarrollo??>
<component>
	<section>
		<title>Desarrollo</title>
		<text>
		  	<table>
		    	<tbody>
		      		<tr>
				        <td>Sostuvo la cabeza a los</td>
				        <td>
				        	<#if antecedenteDesarrollo.seSostuvoCabeza??>
									${antecedenteDesarrollo.seSostuvoCabeza}
							<#else>
								-
							</#if>
						</td>
				        <td>Se sentó a los</td>
				        <td>
							<#if antecedenteDesarrollo.seSento??>
									${antecedenteDesarrollo.seSento}
							<#else>
								-
							</#if>
						</td>
				        <td>Se paró a los</td>
				        <td>
							<#if antecedenteDesarrollo.seParo??>
									${antecedenteDesarrollo.seParo}
							<#else>
								-
							</#if>
						</td>
			      	</tr>
			      	<tr>
			  			<td>Caminó a los</td>
			        	<td>
							<#if antecedenteDesarrollo.camino??>
									${antecedenteDesarrollo.camino}
							<#else>
										-
							</#if>
						</td>
			        	<td>Controló esfínter a los</td>
				        <td>
							<#if antecedenteDesarrollo.controloEsfinter??>
									${antecedenteDesarrollo.controloEsfinter}
							<#else>
										-
							</#if>
						</td>
			        	<td>Primer diente a los</td>
				        <td>
							<#if antecedenteDesarrollo.primerDiente??>
									${antecedenteDesarrollo.primerDiente}
							<#else>
										-
							</#if>
						</td>
			      	</tr>
			      	<tr>
			        	<td>Primeras palabras a los</td>
				        <td>
							<#if antecedenteDesarrollo.primerasPalabras??>
									${antecedenteDesarrollo.primerasPalabras}
							<#else>
										-
							</#if>
						</td>
				        <td>Asiste a la escuela</td>
				        <td colspan="3">
							<#if antecedenteDesarrollo.asisteEscuela>
										Si
							<#else>
										No
							</#if>
						</td>
			      	</tr>
		    	</tbody>
		  	</table>
			<#if antecedenteDesarrollo.progresoEscuela??>
		  	<paragraph>
		    	<caption>Progreso en la escuela</caption>
						${antecedenteDesarrollo.progresoEscuela}
		  	</paragraph>
			</#if>
			<#if antecedenteDesarrollo.progresoPeso??>
		  	<paragraph>
		    	<caption>Progreso en el peso</caption>
				${antecedenteDesarrollo.progresoPeso}
		  	</paragraph>
		  	</#if>
	  	</text>
	</section>
</component>
</#if>
 </#macro>