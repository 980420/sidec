package gehos.comun.reportes.session.customLabelGenerator;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;

import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

public class CategoryCustomItemLabelGenerator  extends AbstractCategoryItemLabelGenerator
implements CategoryItemLabelGenerator, Cloneable, Serializable {

    /** The default format string. */
    public static final String DEFAULT_LABEL_FORMAT_STRING = "{0}";

    /**
     * Creates a new generator with a default number formatter.
     */
    public CategoryCustomItemLabelGenerator() {
        super(DEFAULT_LABEL_FORMAT_STRING, NumberFormat.getInstance());
    }

    /**
     * Creates a new generator with the specified number formatter.
     *
     * @param labelFormat  the label format string (<code>null</code> not
     *                     permitted).
     * @param formatter  the number formatter (<code>null</code> not permitted).
     */
    public CategoryCustomItemLabelGenerator(String labelFormat,
                                              NumberFormat formatter) {
        super(labelFormat, formatter);
    }

    /**
     * Creates a new generator with the specified number formatter.
     *
     * @param labelFormat  the label format string (<code>null</code> not
     *                     permitted).
     * @param formatter  the number formatter (<code>null</code> not permitted).
     * @param percentFormatter  the percent formatter (<code>null</code> not
     *     permitted).
     *
     * @since 1.0.2
     */
    public CategoryCustomItemLabelGenerator(String labelFormat,
            NumberFormat formatter, NumberFormat percentFormatter) {
        super(labelFormat, formatter, percentFormatter);
    }

    /**
     * Creates a new generator with the specified date formatter.
     *
     * @param labelFormat  the label format string (<code>null</code> not
     *                     permitted).
     * @param formatter  the date formatter (<code>null</code> not permitted).
     */
    public CategoryCustomItemLabelGenerator(String labelFormat,
                                              DateFormat formatter) {
        super(labelFormat, formatter);
    }

    /**
     * Generates the label for an item in a dataset.  Note: in the current
     * dataset implementation, each row is a series, and each column contains
     * values for a particular category.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The label (possibly <code>null</code>).
     */
    public String generateLabel(CategoryDataset dataset, int row, int column) {
    	NumberFormat nf = this.getNumberFormat();
    	
        return nf.format(dataset.getValue(row, column));
    }

    /**
     * Tests this generator for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return <code>true</code> if this generator is equal to
     *     <code>obj</code>, and <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryCustomItemLabelGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

}
