<#if documento.evaluacionDiscapacidad.evalDiscapacidad??>
<component>
 <section>
  <title>
   Descripción de la discapacidad
  </title>
  <table>
   <tbody>
    <tr>
     <td>
      Causas de la discapacidad
     </td>
     <td>
      ${documento.evaluacionDiscapacidad.evalDiscapacidad.causaDiscapacidad}
     </td>
    </tr>
   </tbody>
  </table> 
  <#if documento.evaluacionDiscapacidad.evalDiscapacidad.inpsasel??>
  <component>
   <section>
    <title>Tipo de discapacidad</title>
    <table>
     <tbody>
      <tr>
       <td>
        INPSASEL
       </td>
       <td>
        ${documento.evaluacionDiscapacidad.evalDiscapacidad.inpsasel}
       </td>
      </tr>
     </tbody>
    </table> 
    <table>
     <tbody>
      <tr>       
		<th colspan="2">
			Instituto Venezolano de Seguro Social (IVSS)
		</th>						
      </tr>
      <tr>
       <th>
        Clasificación
       </th>
       <th>
        Grado
       </th>
       </tr>
       <tr>
       <td>
        ${documento.evaluacionDiscapacidad.evalDiscapacidad.clasifIVSS}
       </td>
       
       <td>
        ${documento.evaluacionDiscapacidad.evalDiscapacidad.gradoIVSS}
       </td>
      </tr>
     </tbody>
    </table> 
    <#if documento.evaluacionDiscapacidad.evalDiscapacidad.pasdisConapdis?? && documento.evaluacionDiscapacidad.evalDiscapacidad.pasdisConapdis?size !=0>
     <table>
      <tbody>
       <tr>
        <th>PASDIS/CONAPDIS</th>
        <th>Grado</th>
       </tr>
       <#list documento.evaluacionDiscapacidad.evalDiscapacidad.pasdisConapdis as pasdisConapdis>
        <tr>
         <td>${pasdisConapdis.clasificacion}</td>
         <td>${pasdisConapdis.monto}</td>
        </tr>
       </#list>
      </tbody>
     </table>
    </#if>
   </section>
  </component>
  </#if>
 </section>
</component> 
 
</#if>