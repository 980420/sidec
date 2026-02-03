package gehos.comun.reportes.session.customLabelGenerator;

import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

public class PieCustomLabelGeneratorNumber implements PieSectionLabelGenerator{
	 public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
         String result = null;    
         if (dataset != null) {
        	 result="";
        	Double valor = dataset.getValue(key).doubleValue();
            NumberFormat nf  = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            result+=  nf.format(valor) ;
           
         }
         return result;
     }

	public AttributedString generateAttributedSectionLabel(PieDataset arg0,
			Comparable arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
