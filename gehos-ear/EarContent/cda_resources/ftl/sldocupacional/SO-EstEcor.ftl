<#if documento.ecorIniciado??>
<component>
 <section>
  <title>
   Estado del ECOR
  </title>
  
  <table>
   <tbody>
    <tr>
     <#if documento.ecorIniciado==true> 
     <td>
      ECOR iniciado
     </td>
     <#else>
     <td>
      ECORfinalizado
     </td>
     </#if>
     
    </tr>
   </tbody>
  </table> 
  
 </section>
</component> 
 
</#if>