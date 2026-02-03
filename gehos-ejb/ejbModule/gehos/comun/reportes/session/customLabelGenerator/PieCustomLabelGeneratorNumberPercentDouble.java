package gehos.comun.reportes.session.customLabelGenerator;

import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.data.general.PieDataset;

public class PieCustomLabelGeneratorNumberPercentDouble implements PieSectionLabelGenerator{
	 public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
         String result = null;    
         if (dataset != null) {
        	 
        	Double valor = dataset.getValue(key).doubleValue();
            
            List keysAux = dataset.getKeys();
            Double total = 0D;
            for (int i = 0; i < keysAux.size(); i++) {
				total+= dataset.getValue((Comparable)keysAux.get(i)).doubleValue();
			}
            Double porciento = valor/total * 100;
            NumberFormat nf  = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            result= nf.format(dataset.getValue(key));
            result+= "/ "  + nf.format(porciento) + " %";
           
         }
         return result;
     }

	public AttributedString generateAttributedSectionLabel(PieDataset arg0,
			Comparable arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
