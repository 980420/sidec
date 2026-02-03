package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Regla_ensayo;

public class ReglaWrapper 
{
	Regla_ensayo regla;
	HojaCrd_ensayo hoja;
	MomentoSeguimientoGeneral_ensayo momento;
	GrupoSujetos_ensayo grupo;
	
	public Regla_ensayo getRegla() {
		return regla;
	}
	public void setRegla(Regla_ensayo regla) {
		this.regla = regla;
	}
	public HojaCrd_ensayo getHoja() {
		return hoja;
	}
	public void setHoja(HojaCrd_ensayo hoja) {
		this.hoja = hoja;
	}
	public MomentoSeguimientoGeneral_ensayo getMomento() {
		return momento;
	}
	public void setMomento(MomentoSeguimientoGeneral_ensayo momento) {
		this.momento = momento;
	}
	public GrupoSujetos_ensayo getGrupo() {
		return grupo;
	}
	public void setGrupo(GrupoSujetos_ensayo grupo) {
		this.grupo = grupo;
	}
	
	public String getId()
	{
		return String.valueOf(regla.getId());
	}
	
	
}
