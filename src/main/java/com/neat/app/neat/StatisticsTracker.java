package neat;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gameObjects.Car;
import neat.GenerationManager.Species;
public class StatisticsTracker {
	public static Car firstCar;
	static float highestScoreAlive;
	static float highestScore;
	static long timeLastImprovement;
	public static List<Species> species;
	public volatile static int speciesIDTracker;
	public static DefaultCategoryDataset speciesHistoryData = new DefaultCategoryDataset();
	public static XYSeriesCollection scoreHistoryDataCollection = new XYSeriesCollection();
	private static XYSeries avgScoreSeries = new XYSeries("Average");
	private static XYSeries maxScoreSeries = new XYSeries("Max");
	private static XYSeries minScoreSeries = new XYSeries("Min");
	
	public static void update(Car car) {
		if(!car.isDead && (car.chassis.getPosition().x>highestScoreAlive || firstCar.isDead )) {
			highestScoreAlive = car.chassis.getPosition().x;
			firstCar = car;
		}
		if(car.currentScore>highestScore) {
			highestScore = car.currentScore;
			timeLastImprovement = System.currentTimeMillis();
		}
	}
	
	public static float secSinceLastImprovement() {
		if(timeLastImprovement != -1) {
			return (System.currentTimeMillis()-timeLastImprovement)/1000f;
		}else {
			return 0f;
		}
		
	}
	
	public static void nextGeneration(float[] fitness) {
		nextBatch();
		float total = 0;
		float minScore = Float.POSITIVE_INFINITY;
		float maxScore = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < fitness.length; i++) {
			float current = fitness[i];
			total += current;
			if(current<minScore) {
				minScore = current;
			}
			if(maxScore<current) {
				maxScore = current;
			}
		}
		updateScoreHistory(total/fitness.length, maxScore, minScore);
		System.out.printf("Avg: %f Max: %f Min: %f Amount of Species: %d\n", total/fitness.length, maxScore, minScore, species.size());
	}
	static int roundCounter = 0;
	private static void updateScoreHistory(float average, float max, float min){
		if(0 == scoreHistoryDataCollection.getSeriesCount()) {
			scoreHistoryDataCollection.addSeries(avgScoreSeries);
			scoreHistoryDataCollection.addSeries(maxScoreSeries);
			scoreHistoryDataCollection.addSeries(minScoreSeries);
		}
		avgScoreSeries.add(roundCounter, average);
		maxScoreSeries.add(roundCounter, max);
		minScoreSeries.add(roundCounter, min);
		roundCounter++;
		System.out.println(minScoreSeries.getItemCount());
	}
	
	public static void nextBatch() {
		firstCar = null;
		highestScoreAlive = -1f;
		highestScore = -1f;
		timeLastImprovement = -1;
	}
	
	public volatile static Map<Species, Integer> speciesMap = new HashMap<GenerationManager.Species, Integer>();
	private static int IdCounter = 0;
	private static Integer RoundCounter = 0;
	public static void addSpeciesScore(List<Species> species) {
		StatisticsTracker.species = species;
		for (Iterator iterator = species.iterator(); iterator.hasNext();) {
			Species s = (Species) iterator.next();
			if(!speciesMap.containsKey(s)) {
				speciesMap.put(s, IdCounter++);
			}
			speciesHistoryData.addValue(((double)s.members.size()),Integer.toString(s.speciesID),Integer.toString(RoundCounter));
		}
		RoundCounter++;
		
		
	}
}
