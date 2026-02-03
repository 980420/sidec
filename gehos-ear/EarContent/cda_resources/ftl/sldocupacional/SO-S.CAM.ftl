<#include "BEGIN.ftl" />
<component>
 <section>
  <title>Certificado de asistencia médica</title>
  <component>
   <section>
    <title>Datos de la consulta</title>
    <table>
     <tbody>
      <tr>
       <td>Fecha de inicio de la consulta</td>
       <#if documento.fechaInicioCons??>
       <td>${documento.fechaInicioCons}</td>
       <#else>
       <td bgcolor="#E3E4E4"></td>
       </#if>
       <td>Fecha de fin de la consulta</td>
       <#if documento.fechaFinCons??>
       <td>${documento.fechaFinCons}</td>
       <#else>
       <td bgcolor="#E3E4E4"></td>
       </#if>
      </tr>
     </tbody>
    </table>
   </section>
  </component>
  <component>
   <section>
    <title>Datos del certificado de asistencia médica</title>
    <table>
     <tbody>
      <tr>
       <td>Disposición médica</td>
       <td>
        <#if documento.dispMedica??>
         ${documento.dispMedica}
        <#else>
         -
        </#if> 
       </td>
      </tr>
     </tbody>
    </table>
    <#if documento.mostrarSubTipoAusMed == true>
    <component>
     <section>
      <title>Subtipos de ausencia médica</title>
      <table>
       <tbody>
        <tr>
         <td>Subtipos de ausencia médica</td>
         <td>
         <#if documento.subTipoAusenciaMed??>
          ${documento.subTipoAusenciaMed}
         <#else>
          -
         </#if> 
         </td>
         <#if documento.mostrarAmbulatorio == true>
          <#if documento.ambulatorio == true>
           <td>Ambulatorio</td>
          <#else>
           <td>Hospitalizado</td>
          </#if>
         </#if>
        </tr>
       </tbody>
      </table>
     </section>
    </component>
    </#if>
    <#if documento.mostarAdecTarea == true>
     <component>
      <section>
       <title>Adecuación de tareas</title>
       <table>
        <tbody>
         <tr>
          <td>Desde</td>
          <td>
          <#if documento.desde??>
           ${documento.desde}
          <#else>
           -
          </#if>  
          </td>
          <td>Hasta</td>
          <td>
          <#if documento.hasta??>
           ${documento.hasta}
          <#else>
           -
          </#if>
          </td>
          <td>Adjunto</td>
          <td>
           <#if documento.adjunto??>
            ${documento.adjunto}
           <#else>
            -
           </#if>
          </td>
         </tr>
        </tbody>
       </table>
       <#if documento.descripAdecTarea??>
        <text>
			<obs>
				<h1>
					Descripción de la adecuación de tareas:
				</h1>
				<p>
					<#if documento.descripAdecTarea?? && documento.descripAdecTarea!="">
						${documento.descripAdecTarea}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>
        
       </#if>
      </section>
     </component>
    </#if>
    <#if documento.mostrarPAM == true>
     <component>
      <section>
       <title>Período de la ausencia médica</title>
       <table>
        <tbody>
         <tr>
          <td>Desde</td>
          <td>
           <#if documento.desdePAM??>
            ${documento.desdePAM}
           <#else>
            -
           </#if>
          </td>
          <td>Hasta</td>
          <td>
           <#if documento.hastaPAM??>
            ${documento.hastaPAM}
           <#else>
            -
           </#if>
          </td>
          <td>Clase de ausencia médica</td>
          <td>
           <#if documento.claseAusMedica??>
            ${documento.claseAusMedica}
           <#else>
            -
           </#if>
          </td>
         </tr>
        </tbody>
       </table>
      </section>
     </component>
    </#if>
    <#if documento.mostrarPPPN == true>
     <component>
      <section>
       <title>Permiso pre y post-natal</title>
       <table>
        <tbody>
         <tr>
          <td>Clase de parto</td>
          <td>
           <#if documento.claseParto??>
            ${documento.claseParto}
           <#else>
            -
           </#if>
          </td>
          <td>Fecha probable de parto</td>
          <td>
           <#if documento.fechaProbParto??>
            ${documento.fechaProbParto}
           <#else>
            -
           </#if>
          </td>
          <td>Fecha real de parto</td>
          <td>
           <#if documento.fechaRealParto??>
            ${documento.fechaRealParto}
           <#else>
            -
           </#if>
          </td>
         </tr>
         <tr>
          <td>Fecha de inicio del permiso</td>
          <td>
           <#if documento.fechaIniPermiso??>
            ${documento.fechaIniPermiso}
           <#else>
            -
           </#if>
          </td>
          <td>Fecha de fin del permiso</td>
          <td>
           <#if documento.fechaFinPermiso??>
            ${documento.fechaFinPermiso}
           <#else>
            -
           </#if>
          </td>
         </tr>
        </tbody>
       </table>
      </section>
     </component>
    </#if>
    <#if documento.mostrarPLM == true>
     <component>
      <section>
       <title>Permiso de lactancia materna</title>
       <table>
        <tbody>
         <tr>
          <td>Desde</td>
          <td>
           <#if documento.fechaDesde??>
            ${documento.fechaDesde}
           <#else>
            -
           </#if>
          </td>
          <td>Hasta</td>
          <td>
           <#if documento.fechaFin??>
            ${documento.fechaFin}
           <#else>
            -
           </#if>
          </td>
          
         </tr>
        
        </tbody>
       </table>
      </section>
     </component>
    </#if>
    <#if documento.mostrarReub == true>
     <component>
      <section>
       <title>Reubicación</title>
       <table>
        <tbody>
         <tr>
          <td>Desde</td>
          <td>
          <#if documento.fechaIniReu??>
           ${documento.fechaIniReu}
          <#else>
           -
          </#if>  
          </td>
          <td>Hasta</td>
          <td>
          <#if documento.fechaFinReu??>
           ${documento.fechaFinReu}
          <#else>
           -
          </#if>
          </td>
          <td>Adjunto</td>
          <td>
           <#if documento.adjuntoReu??>
            ${documento.adjuntoReu}
           <#else>
            -
           </#if>
          </td>
         </tr>
        </tbody>
       </table>
       <#if documento.descripReub??>
        <text>
			<obs>
				<h1>
					Descripción de la reubicación:
				</h1>
				<p>
					<#if documento.descripReub?? && documento.descripReub!="">
						${documento.descripReub}
					<#else>
					    -
					</#if>
				</p>
			</obs>
		 </text>
        
       </#if>
      </section>
     </component>
    </#if>
    <#if documento.observaciones??>
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
		 </text>
        
       </#if>
   </section>
  </component>
 </section>
</component>
<#include "END.ftl" />