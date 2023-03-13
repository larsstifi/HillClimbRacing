package Game;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.naming.directory.NoSuchAttributeException;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import neat.StatisticsTracker;

public class SettingsWindow extends JPanel{
	
	public SettingsWindow() {
		super();
		setupWindow();
	}
	
	private void setupWindow() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor= GridBagConstraints.NORTH;
		c.weightx  = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		this.add(getEvolutionSettingsPanel(),c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		this.add(getViewSettingsPanel(),c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		this.add(getTerrainSettingsPanel(),c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.weighty = 1;
		this.add(getSpeciesChartPanel(),c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		this.add(getScoreChartPanel(),c);
		
		
		
	}
	
	private JPanel getEvolutionSettingsPanel() {
		JPanel evolutionSettingsPanel = new JPanel();
		evolutionSettingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor= GridBagConstraints.NORTH;
		c.weightx  = 0.5;
		
		
		//Evolution Settings
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		evolutionSettingsPanel.add(new Label("Evolution Settings"),c);
		
		
		//C1
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("C1:"),c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField C1 = new JTextField(Float.toString(GameSettings.C1));
		C1.setHorizontalAlignment(JTextField.RIGHT);
		C1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.C1 = Float.parseFloat(C1.getText());
					System.out.println(GameSettings.C1);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(C1,c);

		//C2
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("C2:"),c);
		c.gridx = 3;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField C2 = new JTextField(Float.toString(GameSettings.C2));
		C2.setHorizontalAlignment(JTextField.RIGHT);
		C2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.C2 = Float.parseFloat(C2.getText());
					System.out.println(GameSettings.C2);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(C2,c);
		
		//C3
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 1;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("C3:"),c);
		c.gridx = 5;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField C3 = new JTextField(Float.toString(GameSettings.C3));
		C3.setHorizontalAlignment(JTextField.RIGHT);
		C3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.C3 = Float.parseFloat(C3.getText());
					System.out.println(GameSettings.C3);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(C3,c);
		
		//DT
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("DT:"),c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField DT = new JTextField(Float.toString(GameSettings.DT));
		DT.setHorizontalAlignment(JTextField.RIGHT);
		DT.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.DT = Float.parseFloat(DT.getText());
					System.out.println(GameSettings.DT);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(DT,c);
		
		
		
		//SIZE_PENALTY
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("Size Penalty:"),c);
		c.gridx = 3;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField SIZE_PENALTY = new JTextField(Float.toString(GameSettings.SIZE_PENALTY));
		SIZE_PENALTY.setHorizontalAlignment(JTextField.RIGHT);
		SIZE_PENALTY.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.SIZE_PENALTY = Float.parseFloat(SIZE_PENALTY.getText());
					System.out.println(GameSettings.SIZE_PENALTY);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(SIZE_PENALTY,c);
		
		//minSpeciesSizeforChampion
		c.gridwidth = 1;
		c.gridx = 4;
		c.gridy = 2;
		c.weightx = 0;
		evolutionSettingsPanel.add(new Label("Size for Champion:"),c);
		c.gridx = 5;
		c.gridy = 2;
		c.gridwidth = 1;
		c.weightx = 1;
		JTextField minSpeciesSizeforChampion = new JTextField(Integer.toString(GameSettings.minSpeciesSizeforChampion));
		minSpeciesSizeforChampion.setHorizontalAlignment(JTextField.RIGHT);
		minSpeciesSizeforChampion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GameSettings.minSpeciesSizeforChampion = Integer.parseInt(minSpeciesSizeforChampion.getText());
					System.out.println(GameSettings.minSpeciesSizeforChampion);
				} catch (Exception e2) {
			// TODO: handle exception
				}
			}
		});
		evolutionSettingsPanel.add(minSpeciesSizeforChampion,c);
		return evolutionSettingsPanel;
	}

	private ChartPanel getSpeciesChartPanel() {
		JFreeChart chart = ChartFactory.createStackedAreaChart(
	            "Species Amount",      // chart title
	            "Generation",                // domain axis label
	            "Amount",                   // range axis label
	            StatisticsTracker.speciesHistoryData,                   // data
	            PlotOrientation.VERTICAL,  // orientation
	            false,                   // include legend
	            true,
	            false);
		ChartPanel chartPanel = new ChartPanel(chart);
		NumberAxis domain = (NumberAxis) ((CategoryPlot)chart.getPlot()).getRangeAxis();
		((CategoryPlot)chart.getPlot()).getDomainAxis().setCategoryMargin(0);;
		return chartPanel;
		
	}
	
	private ChartPanel getScoreChartPanel() {
		JFreeChart chart = ChartFactory.createXYLineChart(
		         "Score" ,
		         "Generation" ,
		         "Score" ,
		         StatisticsTracker.scoreHistoryDataCollection,
		         PlotOrientation.VERTICAL ,
		         true , true , false);
		ChartPanel chartPanel = new ChartPanel(chart);
		XYPlot plot = chart.getXYPlot();
		ValueAxis xaxis = plot.getRangeAxis();
	    xaxis.setAutoRange(true);
		return chartPanel;
		
	}
	
	private JPanel getTerrainSettingsPanel() {
		JPanel terrainSettingsPanel = new JPanel();
		terrainSettingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor= GridBagConstraints.NORTH;
		
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		terrainSettingsPanel.add(new Label("Terrain Settings"),c);
		
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 1;	
		c.gridwidth = 1;
		terrainSettingsPanel.add(new Label("Update Terrain:"),c);
		
		c.weightx = 0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		Checkbox viewGame = new Checkbox("",true);
		viewGame.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				GameSettings.UpdateTerrain = e.getStateChange() == ItemEvent.SELECTED;
				
			}
		});
		terrainSettingsPanel.add(viewGame,c);
		
		c.weightx = 1;
		c.gridx = 2;
		c.gridy = 1;	
		c.gridwidth = GridBagConstraints.RELATIVE;
		terrainSettingsPanel.add(new Label(),c);
		
		return terrainSettingsPanel;
	}
	
	
	private JPanel getViewSettingsPanel() {
		JPanel viewSettingsPanel = new JPanel();
		viewSettingsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor= GridBagConstraints.NORTH;
		
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.RELATIVE;
		viewSettingsPanel.add(new Label("View Settings"),c);
		
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 1;	
		c.gridwidth = 1;
		viewSettingsPanel.add(new Label("Draw Game:"),c);
		
		c.weightx = 0;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		Checkbox viewGame = new Checkbox("",true);
		viewGame.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				GameSettings.drawGame = e.getStateChange() == ItemEvent.SELECTED;
				
			}
		});
		viewSettingsPanel.add(viewGame,c);
		
		c.weightx = 1;
		c.gridx = 2;
		c.gridy = 1;	
		c.gridwidth = GridBagConstraints.RELATIVE;
		viewSettingsPanel.add(new Label(),c);
		
		return viewSettingsPanel;
	}
	
}
