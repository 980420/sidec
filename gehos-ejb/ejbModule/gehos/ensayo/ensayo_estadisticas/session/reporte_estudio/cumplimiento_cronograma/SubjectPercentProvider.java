package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma;

import gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma.wrapper.SubjectPercentage;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;

@Name("subjectPercentProvider")
public class SubjectPercentProvider
{
	EntityManager entityManager;

	public SubjectPercentProvider(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<SubjectPercentage> getAll(int idCenter)
	{
		List<SubjectPercentage> list = new ArrayList<SubjectPercentage>();
		List<Estudio_ensayo> ensayos = (List<Estudio_ensayo>)entityManager.createQuery("select t from Estudio_ensayo t").getResultList();
		for (int i = 0; i < ensayos.size(); i++) 
		{	
			Estudio_ensayo estudio = ensayos.get(i);

			List<GrupoSujetos_ensayo> grupos = new ArrayList<GrupoSujetos_ensayo>(estudio.getGrupoSujetoses());
			for (int j = 0; j < grupos.size(); j++) 
			{				
				GrupoSujetos_ensayo g = grupos.get(j);				
				List<Sujeto_ensayo> sujetos = new ArrayList<Sujeto_ensayo>(g.getSujetos());
				for (Sujeto_ensayo s : sujetos) 
				{
					String name = s.getCodigoPaciente();
					String study = estudio.getNombre();

					String datestart="-";
					if(estudio.getFechaInicio()!=null)
						datestart = estudio.getFechaInicio().toString();

					String dateend = "-";
					if(estudio.getFechaFin()!=null)
						dateend = estudio.getFechaFin().toString();

					Cronograma_ensayo[] cronogramas = g.getCronogramas().toArray(new Cronograma_ensayo[0]);
					if(cronogramas.length>0)
					{
						Cronograma_ensayo cronograma = cronogramas[0];

						String momentcount = String.valueOf(cronograma.getMomentoSeguimientoGenerals().size());

						List<MomentoSeguimientoEspecifico_ensayo> msEspecificos = (List<MomentoSeguimientoEspecifico_ensayo>)entityManager.createQuery("select m from MomentoSeguimientoEspecifico_ensayo m where m.sujeto.id=:pid")
								.setParameter("pid", s.getId()).getResultList();

						int msCompletedCount = 0;
						for (int k = 0; k < msEspecificos.size(); k++) 
						{
							MomentoSeguimientoEspecifico_ensayo mse = msEspecificos.get(k);						
							if(mse.getEstadoMomentoSeguimiento().getId()==3)
								msCompletedCount++;
						}
						double iPercent=0; 
						if(msEspecificos.size()!=0)
						  iPercent = (msCompletedCount*100)/msEspecificos.size();

						String percent = String.valueOf(iPercent);	

						SubjectPercentage sp = new SubjectPercentage(name, study, datestart, dateend, momentcount, percent);
						list.add(sp);
					}
				}
			}
		}
		return list;
	}
}
