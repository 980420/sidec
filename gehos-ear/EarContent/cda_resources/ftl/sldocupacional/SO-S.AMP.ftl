<#include "BEGIN.ftl" />
<component>
 <section>
  <title>Asignación de medidas preventivas</title>
  <#if documento.medidasPreventivas?? && documento.medidasPreventivas?size !=0> 
  <table>
   <tbody>
    <tr>
     <th colspan="4">Listado de medidas preventivas</th>
    </tr>
    <tr>
     <th>Clasificación</th>
     <th>Categoría</th>
     <th>Medida preventiva</th>
     <th>Responsable de ejecución</th>
    </tr>
    <#list documento.medidasPreventivas as medida>
     <tr>
      <#if medida.clasificacion??>
      <td>${medida.clasificacion}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if medida.categoria??>
      <td>${medida.categoria}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if medida.medidaPrev??>
      <td>${medida.medidaPrev}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if medida.responsable??>
      <td>${medida.responsable}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
     </tr>
    </#list>
   </tbody>
  </table>
  <#else>
   No existe información a mostrar.
  </#if>
 </section>
</component>
<#include "END.ftl" />