<#include "BEGIN.ftl" />
<component>
 <section>
  <title>Antecedentes laborales</title>
  <#if documento.listAntecedentes?? && documento.listAntecedentes?size !=0> 
  <table>
   <tbody>
    <tr>
     <th colspan="5">Listado de antecedentes</th>
    </tr>
    <tr>
     <th>Empleador/Empresa</th>
     <th>Ocupación</th>
     <th>Fecha Inicio</th>
     <th>Fecha Fin</th>
     <th>Tipo de jornada o trabajo por turnos</th>
    </tr>
    <#list documento.listAntecedentes as antecedente>
     <tr>
      <#if antecedente.empleador??>
      <td>${antecedente.empleador}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if antecedente.ocupacion??>
      <td>${antecedente.ocupacion}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if antecedente.fechaInicio??>
      <td>${antecedente.fechaInicio}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if antecedente.fechaFin??>
      <td>${antecedente.fechaFin}</td>
      <#else>
       <td bgcolor="#E3E4E4"></td>
      </#if>
      <#if antecedente.tipoJornada??>
      <td>${antecedente.tipoJornada}</td>
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