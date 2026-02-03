package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilConjuntoDatos {
	static List<VariableMomento> extraerDatosDeHQL(String hql){
		List<String> listadoIdMomentos = new ArrayList<String>();
		List<String> listadoIdVariables = new ArrayList<String>();

		List<VariableMomento> variablePorMomento= new ArrayList<VariableMomento>();
		Pattern patronVariable = Pattern.compile("variable\\.id=(\\d+)");
        Pattern patronMomento = Pattern.compile("momentoSeguimientoGeneral\\.id=(\\d+)");

        Matcher matcherVariable = patronVariable.matcher(hql);
        while (matcherVariable.find()) {
        	listadoIdVariables.add(matcherVariable.group(1));
        }

        Matcher matcherMomento = patronMomento.matcher(hql);
        while (matcherMomento.find()) {
        	listadoIdMomentos.add(matcherMomento.group(1));
        }
        
		
		for (int i = 0; i < listadoIdVariables.size(); i++) {
			variablePorMomento.add(new VariableMomento(Long.parseLong(listadoIdVariables.get(i)), Long.parseLong(listadoIdMomentos.get(i))));
		}
		
		return variablePorMomento;
	}
	
	// Retorna true si el HQL es nuevo.
	// Cuando se soluciono la incidencia que repetia las variable en los momentos, 
	// fue necesario restructurar el HQL, por tanto es necesario diferenciar los nuevos de los viejos
	static boolean esHQLNuevo(String hql){
		String regex = "\\(\\((variable\\.id=\\d+\\s+and\\s+momentoSeguimientoGeneral\\.id=\\d+)(\\s+or\\s+\\(variable\\.id=\\d+\\s+and\\s+momentoSeguimientoGeneral\\.id=\\d+\\))*\\)?\\)";
		return Pattern.compile(regex).matcher(hql).find();
	}
	
	static String declaracionHql(ConjuntoDatos_ensayo conjuntoDatos,Estudio_ensayo estudio, List<VariableConjuntoDatos> listadoVariablesSeleccionadas) {
		String hql = "select variableDato from VariableDato_ensayo variableDato "
				+ "inner join variableDato.variable variable inner join "
				+ "variableDato.crdEspecifico crdEspecifico inner join "
				+ "crdEspecifico.momentoSeguimientoEspecifico momentoSeguimientoEspecifico "
				+ "inner join momentoSeguimientoEspecifico.sujeto sujeto "
				+ "inner join momentoSeguimientoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "where crdEspecifico.estadoHojaCrd.codigo between 3 and 4 "
				+ "and crdEspecifico.hojaCrd.estudio.id = " + String.valueOf(estudio.getId()) + " " 
				+ "and sujeto.eliminado = false ";
		if(conjuntoDatos.getEntidad().getTipoEntidad().getId()!=7L)
			hql+="and sujeto.entidad.id = " + String.valueOf(conjuntoDatos.getEntidad().getId()) + " " ;
		String consultaMSVariable = "";
		for (int i = 0; i < listadoVariablesSeleccionadas.size(); i++) {
			String subConsulta = "("+"variable.id="
					+Long.valueOf(((VariableConjuntoDatos)listadoVariablesSeleccionadas.get(i)).getVariable().getId())
					+" and " +"momentoSeguimientoGeneral.id="
					+Long.valueOf(((VariableConjuntoDatos)listadoVariablesSeleccionadas.get(i)).getMomentoSeguimientoGeneral().getId()) +")";
		
			// Si es el ultimo, no lleva el or
			if(i+1 < listadoVariablesSeleccionadas.size()){
				subConsulta+=" or ";
			}
			consultaMSVariable +=  subConsulta;
		}
		return hql + "and ("+ consultaMSVariable+ ")" 
				+ " order by momentoSeguimientoEspecifico.fechaInicio, variableDato.id";
	}
}
