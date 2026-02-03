<#if documento.diagnostico??>
<table>
 <tbody>
  <tr>
   <td>
    Tipo de diagnóstico
   </td>
   <td>
    <#if documento.diagnostico.tipo??>${documento.diagnostico.tipo}
    <#else>
     -
    </#if>
   </td>
   
  </tr>
 </tbody>
</table> 
<#include "DIAGNOSTICO.FTL" />
<table>
 <tbody>
  <tr>
   <td>
    Estado
   </td>
   <td>
    <#if documento.diagnostico.estado??>${documento.diagnostico.estado}
    <#else>
     -
    </#if>
   </td>
   <#if documento.diagnostico.adjunto??>
    <td>
     Adjunto
    </td>
    <td>
     ${documento.diagnostico.adjunto}
     
    </td>
   </#if>
  </tr>
 </tbody>
</table>
</#if>

                          