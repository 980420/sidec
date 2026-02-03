<#include "BEGIN.ftl" />
<component>
 <section>
  <title>Asignación de programas de vigilancia</title>
  <table>
   <tbody>
    <tr>
     <th>Listado de programas de vigilancia</th>
    </tr>
    <tr>
     <th>Nombre</th>
    </tr>
    <#list documento.programasAsignados as programa>
     <tr>
      <td>${programa}</td>
     </tr>
    </#list>
   </tbody>
  </table>
 </section>
</component>
<#include "END.ftl" />