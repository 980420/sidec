<#include "BEGIN.ftl" />
<component>
 <section>
  <title>Evaluación de riesgos</title>
  <component>
   <section>
    <title>Datos de la evaluación de riesgos</title>
    <table>
     <tbody>
      <tr>
       <td>Causa de notificación</td>
       <#if documento.causaNotificacion??>
        <td>${documento.causaNotificacion}</td>
       <#else>
        <td bgcolor="#E3E4E4"></td>
       </#if> 
       <td>Adjunto</td>
       <#if documento.adjunto??>
        <td>${documento.adjunto}</td>
       <#else>
        <td>-</td>
       </#if> 
      </tr>
     </tbody>
    </table>
   </section>
  </component>
  <#if (documento.agentesBiol?? && documento.agentesBiol?size !=0) || (documento.agentesDiserg?? && documento.agentesDiserg?size !=0) || (documento.agentesFisicos?? && documento.agentesFisicos?size !=0) || (documento.agentesPsico?? && documento.agentesPsico?size !=0) || (documento.agentesQuim?? && documento.agentesQuim?size !=0)>
  <component>
   <section>
    <title>Listado de riesgos ocupacionales</title>
    <#if documento.agentesBiol?? && documento.agentesBiol?size !=0>
     <table>
      <tbody>
       <tr>
        <th colspan="4">Agentes biológicos</th>
       </tr>
       <tr>
        <th>Nombre</th>
        <th>Tipo de notificación</th>
        <th>Grupo del agente biológico</th>
        <th>Caracterización de exposición</th>
       </tr>
       <#list documento.agentesBiol as agente>
        <tr>
         <#if agente.nombre??>
          <td>${agente.nombre}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.tipoNotificacion??>
          <td>${agente.tipoNotificacion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.grupoAgentBiol??>
          <td>${agente.grupoAgentBiol}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.caractExposicion??>
          <td>${agente.caractExposicion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if> 
        </tr>
       </#list>
      </tbody>
     </table>
    </#if>
    <#if documento.agentesDiserg?? && documento.agentesDiserg?size !=0>
     <table>
      <tbody>
       <tr>
        <th colspan="3">Agentes disergonómicos</th>
       </tr>
       <tr>
        <th>Nombre</th>
        <th>Tipo de notificación</th>
        
        <th>Caracterización de exposición</th>
       </tr>
       <#list documento.agentesDiserg as agente>
        <tr>
         <#if agente.nombre??>
          <td>${agente.nombre}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.tipoNotificacion??>
          <td>${agente.tipoNotificacion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         <#if agente.caractExposicion??>
          <td>${agente.caractExposicion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if> 
        </tr>
       </#list>
      </tbody>
     </table>
    </#if>
    <#if documento.agentesFisicos?? && documento.agentesFisicos?size !=0>
     <table>
      <tbody>
       <tr>
        <th colspan="5">Agentes físicos</th>
       </tr>
       <tr>
        <th>Nombre</th>
        <th>Tipo de notificación</th>
        <th>División</th>
        <th>Unidad organizativa</th>
        <th>Caracterización de exposición</th>
       </tr>
       <#list documento.agentesFisicos as agente>
        <tr>
         <#if agente.nombre??>
          <td>${agente.nombre}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         <#if agente.tipoNotificacion??>
          <td>${agente.tipoNotificacion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.division??>
          <td>${agente.division}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.unidadOrganizativa??>
          <td>${agente.unidadOrganizativa}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         <#if agente.caractExposicion??>
          <td>${agente.caractExposicion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if> 
        </tr>
       </#list>
      </tbody>
     </table>
    </#if>
    <#if documento.agentesPsico?? && documento.agentesPsico?size !=0>
     <table>
      <tbody>
       <tr>
        <th colspan="3">Agentes psicosociales</th>
       </tr>
       <tr>
        <th>Nombre</th>
        <th>Tipo de notificación</th>
        
        <th>Caracterización de exposición</th>
       </tr>
       <#list documento.agentesPsico as agente>
        <tr>
         <#if agente.nombre??>
          <td>${agente.nombre}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         <#if agente.tipoNotificacion??>
          <td>${agente.tipoNotificacion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         <#if agente.caractExposicion??>
          <td>${agente.caractExposicion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if> 
        </tr>
       </#list>
      </tbody>
     </table>
    </#if>
    <#if documento.agentesQuim?? && documento.agentesQuim?size !=0>
     <table>
      <tbody>
       <tr>
        <th colspan="3">Agentes químicos</th>
       </tr>
       <tr>
        <th>Nombre</th>
        <th>Tipo de notificación</th>
        
        <th>Caracterización de exposición</th>
       </tr>
       <#list documento.agentesQuim as agente>
        <tr>
         <#if agente.nombre??>
          <td>${agente.nombre}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         <#if agente.tipoNotificacion??>
          <td>${agente.tipoNotificacion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if>
         
         
         <#if agente.caractExposicion??>
          <td>${agente.caractExposicion}</td>
         <#else>
          <td bgcolor="#E3E4E4"></td>
         </#if> 
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
<#include "END.ftl" />