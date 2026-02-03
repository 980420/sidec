package gehos.comun.reportes.entity;

import java.awt.image.BufferedImage;

public class ChartSource<A> {
	private String chartAgrup;
	private String chartName;
	private A orderingValue;
	
	
	public ChartSource(String chartAgrup, String chartName,
			BufferedImage chartImage) {
		super();
		this.chartAgrup = chartAgrup;
		this.chartName = chartName;
		this.chartImage = chartImage;
	}
	private BufferedImage chartImage;
	public String getChartAgrup() {
		return chartAgrup;
	}
	public void setChartAgrup(String chartAgrup) {
		this.chartAgrup = chartAgrup;
	}
	public String getChartName() {
		return chartName;
	}
	public void setChartName(String chartName) {
		this.chartName = chartName;
	}
	public BufferedImage getChartImage() {
		return chartImage;
	}
	public void setChartImage(BufferedImage chartImage) {
		this.chartImage = chartImage;
	}
	public A getOrderingValue() {
		return orderingValue;
	}
	public void setOrderingValue(A orderingValue) {
		this.orderingValue = orderingValue;
	}
	
	
}
