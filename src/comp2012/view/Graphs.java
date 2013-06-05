package comp2012.view;

import java.awt.image.BufferedImage;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class Graphs {
	
	public static BufferedImage getGraph(String[] groups, String[] dates, double[][] values, String title)
	{
		if (groups.length != values.length)
		{
			throw new RuntimeException("Not enough group titles for the amount of data groups supplied");
		}
		if (dates.length != values[0].length)
		{
			throw new RuntimeException("Not enough dates for the amount of marks supplied");
		}
		
		CategoryDataset data = Graphs.createCategoryDataset(groups, dates, values);
		CategoryAxis caxis = new CategoryAxis();
		ValueAxis vaxis = new NumberAxis("Mark (%)");
		CategoryItemRenderer renderer = new BarRenderer();
		
		JFreeChart chart = new JFreeChart(title, new CategoryPlot(data, caxis, vaxis, renderer));
		return chart.createBufferedImage(600, 200);
	}

    private static CategoryDataset createCategoryDataset(String[] rowKeyPrefix,
            String[] columnKeyPrefix, double[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix[r];
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix[c];
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;

    }

}
