package gehos.ensayo.ensayo_disenno.session.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoGeneralList_ensayo;












import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Scope(ScopeType.CONVERSATION)
@Name("momentoSeguimientoProgramdoCustomList")
public class MomentoSeguimientoProgramdoCustomList extends MomentoSeguimientoGeneralList_ensayo{
	
	@In EntityManager entityManager;
	@In SeguridadEstudio seguridadEstudio;
	private static final String EJBQL = "select msProgramado from MomentoSeguimientoGeneral_ensayo msProgramado "
			+ "inner join msProgramado.cronograma cronograma "
			+ "where msProgramado.eliminado <> #{momentoSeguimientoProgramdoCustomList.falso} "
			+ "and msProgramado.programado  = #{momentoSeguimientoProgramdoCustomList.programado} "
			+ "and cronograma.id = #{momentoSeguimientoProgramdoCustomList.idCronograma}";

	private static final String[] RESTRICTIONS = {
			"lower(msProgramado.nombre) like concat(lower(#{momentoSeguimientoProgramdoCustomList.nombre}),'%')"};


	
	private String nombre = "";
	
	private boolean falso = true;
	private boolean programado = true;
	private long idCronograma;
	private int pagina;
	
	
	
	public MomentoSeguimientoProgramdoCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder("msProgramado.id");
		setMaxResults(10);
	}
	
	
	
	// Metodo para saber las etapas del MS
		public String etapasMS(String listaDias) {
			boolean evaluacion = false;
			 boolean tratamiento= false;
			 boolean seguimiento= false;
			List<String> listadias = new ArrayList<String>();
			listadias = listaDias(listaDias);
			String e = "";
			String y = SeamResourceBundle.getBundle().getString("prm_y_ens");

			List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
					.createQuery(
							"select e from Etapa_ensayo e "
									+ "where e.cronograma.id = :idCrono "
									+ "order by e.id")
					.setParameter("idCrono", this.idCronograma).getResultList();
			for (int i = 0; i < listadias.size(); i++) {
				int dia = Integer.parseInt(listadias.get(i));
				if (dia == 0)
					evaluacion = true;
				else if (!tratamiento && dia >= 1
						&& dia <= etapas.get(1).getFinEtapa())
					tratamiento = true;
				else if (!seguimiento && dia >= etapas.get(2).getInicioEtapa()
						&& dia <= etapas.get(2).getFinEtapa())
					seguimiento = true;
				if (evaluacion && tratamiento && seguimiento) {
					e = etapas.get(0).getNombreEtapa() + ", "
							+ etapas.get(1).getNombreEtapa() + " " + y + " "
							+ etapas.get(2).getNombreEtapa();
					return e;
				}
			}
			if (evaluacion && !tratamiento && !seguimiento)
				e = etapas.get(0).getNombreEtapa();
			else if (evaluacion && tratamiento && !seguimiento)
				e = etapas.get(0).getNombreEtapa() + " " + y + " "
						+ etapas.get(1).getNombreEtapa();
			else if (evaluacion && !tratamiento && seguimiento)
				e = etapas.get(0).getNombreEtapa() + " " + y + " "
						+ etapas.get(2).getNombreEtapa();
			else if (!evaluacion && tratamiento && !seguimiento)
				e = etapas.get(1).getNombreEtapa();
			else if (!evaluacion && tratamiento && seguimiento)
				e = etapas.get(1).getNombreEtapa() + " " + y + " "
						+ etapas.get(2).getNombreEtapa();
			else
				e = etapas.get(2).getNombreEtapa();
			return e;
		}
		
		public List<String> listaDias(String dias){
			
			List<String> listaDias= new ArrayList<String>();
			String[] listaDiasSelecc;
			String[] listaUltDiaSelecc;
			String y = SeamResourceBundle.getBundle().getString("prm_y_ens");
			if (dias.contains(",")) {
				listaDiasSelecc = dias.split(", ");
				listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
						.split(" " + y + " ");
				for (int i = 0; i < listaDiasSelecc.length - 1; i++) {
					listaDias.add(listaDiasSelecc[i]);
					
				}
				listaDias.add(listaUltDiaSelecc[0]);
				listaDias.add(listaUltDiaSelecc[1]);
				

			} else if (dias.contains(y)) {
				listaUltDiaSelecc = dias.split(" " + y + " ");
				listaDias.add(listaUltDiaSelecc[0]);
				listaDias.add(listaUltDiaSelecc[1]);			

			} else {
				listaDias.add(dias);			

			}
			return listaDias;
		}
	/*Getters and Setters*/
	


	public String getNombre() {
		return nombre;
	}

	public boolean isFalso() {
		return falso;
	}

	public void setFalso(boolean falso) {
		this.falso = falso;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isProgramado() {
		return programado;
	}

	public void setProgramado(boolean programado) {
		this.programado = programado;
	}

	public int getPagina() {
        if(this.getNextFirstResult() != 0)
            return this.getNextFirstResult()/10;
            else
                return 1;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
       
        long num=(getResultCount()/10)+1;
        if(this.pagina>0){
        if(getResultCount()%10!=0){
            if(pagina<=num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        else{
            if(pagina<num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        }
    }



	public long getIdCronograma() {
		return idCronograma;
	}



	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}
	
	public boolean esGrupoPesquisaje(){		
		Cronograma_ensayo cronograma = entityManager.find(Cronograma_ensayo.class, this.idCronograma);
		if(cronograma.getGrupoSujetos().getNombreGrupo().equals("Grupo Pesquisaje"))
			return true;
		else
			return false;
	}
	

}
