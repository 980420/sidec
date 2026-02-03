<#if documento.observaciones??||documento.conclusiones??>
<component>
  <section>
	<title>Observaciones y conclusiones</title>
		<text>
			<obs>
				<h1>
					Observaciones:
				</h1>
				<p>
					<#if documento.observaciones?? && documento.observaciones!="">
						${documento.observaciones}
					<#else>
					    -
					</#if>
				</p>
			</obs>
			<obs>
				<h1>
					Conclusiones:
				</h1>
				<p>
					<#if documento.conclusiones?? && documento.conclusiones!="">
						${documento.conclusiones}
					<#else>
						-
					</#if>
				</p>
			</obs>
		</text>
  </section>
</component>
</#if>
