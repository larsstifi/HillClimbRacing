package neat;

import java.util.Random;


import neat.GenerationManager.FitnessGenome;

public class Test {
	
	public static void main(String[] args) {
		int populationSize = 150;
		GenerationManager manager = new GenerationManager(populationSize, 2, 1);

		FitnessGenome[] currentGenomes = manager.getCurrentGeneration();

		float[] scores = new float[populationSize];
		while(true) {
			scores = evaluateGenoms(currentGenomes);
			currentGenomes =  manager.getNextGeneration(scores);
			System.out.println(manager.species.size());
			float maxScore = 0;
			float minScore = 100f;
			float totalScore = 0;
			for (int i = 0; i < scores.length; i++) {
				if(scores[i]>maxScore) maxScore = scores[i];
				if(scores[i]< minScore) minScore = scores[i];
				totalScore += scores[i];
			}
			System.out.printf("Min: %f Max: %f Avg: %f", minScore, maxScore, totalScore/populationSize);
		}
	}
	
	private static float[] evaluateGenoms(FitnessGenome[] genomes) {
		Random random = new Random();
		float[] fitness = new float[genomes.length];
		for (int i = 0; i < genomes.length; i++) {
			for (int j = 0; j < 20; j++) {
				int in1 = random.nextInt(2);
				int in2 = random.nextInt(2);
				float[] out = genomes[i].genome.evaluate(new float[]{in1, in2});
				
				if((out[0]>0f) == ((in1 == 1 && in2 == 0)||(in1 == 0 && in2 == 1))) {
					//System.out.println(out[0] +" " + in1 + " " + in2);
					fitness[i] += 1f;
				}else {
					fitness[i] += 0f;
				}
			}
		}
		return fitness;
	}
}
