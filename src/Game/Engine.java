package Game;

import gameObjects.Car;
import gameObjects.Terrain;
import neat.GenerationManager;
import neat.StatisticsTracker;
import neat.GenerationManager.FitnessGenome;

import java.io.File;
import java.util.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;


public class Engine implements Runnable{
	GameWindow gameWindow;
	Terrain terrain;
	World world;
	Vec2 gravity;
	GenerationManager generationManager;
	FitnessGenome[] generation;
	
	File savedGenerationFile;
	//cars
	int distanceCarLeft = 10;
	int carsAmount = 300;//400;
	volatile List<Car> activeCars;
	
	public int currentGeneration = 0;
	public int currentBatch = 0;
	int batchSize = 100;
	
	float[] fitness;
	
	public Engine(GameWindow gameWindow) {
		this.savedGenerationFile = new File("GenerationSave.txt");
		this.generationManager = new GenerationManager(carsAmount, 5, 2);
		this.generationManager.createInitalPopulation();
		//this.generationManager.loadGenerationFromFile(savedGenerationFile);
		this.gameWindow = gameWindow;
		terrain = gameWindow.terrain;
		activeCars = new LinkedList<Car>();

		
		gravity = new Vec2(0f, -9.81f);
		world = new World(gravity);
		terrain.createBody(world);
		createCars();
	}
	@Override
	public void run() {
		//main loop
		long loopCounter = 0;
		generationManager.saveGenerationToFile(savedGenerationFile);
		while(gameWindow.isEnabled()) {
			long start = System.currentTimeMillis();
			world.step(1f/60f, 20, 20);
			timeStepCars();
			if(loopCounter%2==0) {
				gameWindow.repaint();
			}
			if(StatisticsTracker.secSinceLastImprovement()>5f) {
				nextBatch();
			}
			
			readKeyInputs();
			
			
			loopCounter++;
			
			float currentTime = System.currentTimeMillis()-start;
			if(currentTime>16.6) {
				currentTime = 16.6f;
			}
			try {
				if (GameSettings.drawGame) {
					Thread.sleep((long) (16.6f-currentTime));
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			gameWindow.fpsEngine = 1000/(System.currentTimeMillis()-start+1);
		}
		
	}
	
	private void timeStepCars() {
		 for (Iterator iterator = activeCars.iterator(); iterator.hasNext();) {
			Car current = (Car) iterator.next();
			current.timeStep();
		}
	}
	
	public void createCars() {
		fitness=new float[carsAmount];
		generation = generationManager.getCurrentGeneration();
		for(int i = 0; i < batchSize; i++) {
			Car current = new Car(distanceCarLeft+1, 7, gameWindow, generation[i]);
			if(i==0) gameWindow.followedCar = current;
			current.createBody(world);
			activeCars.add(current);
		}
	}
	
	public void nextBatch() {
		if(currentBatch != -1) {
			for (int i = 0; i < batchSize; i++) {
				fitness[currentBatch*batchSize + i] = activeCars.get(i).currentScore;
			}
		}
		currentBatch++;
		if(batchSize * currentBatch >= carsAmount) {
			currentGeneration++;
			currentBatch = 0;
			nextGeneration();
		}
		for (int i = 0; i < batchSize; i++) {
			activeCars.get(i).destroyBody();
			Car currentCar = new Car(distanceCarLeft+1, 7, gameWindow, generation[i + currentBatch*batchSize]);
			currentCar.createBody(world);
			activeCars.set(i, currentCar);
		}
		StatisticsTracker.nextBatch();
		
	}
	
	public void nextGeneration() {
//		fitness = new float[carsAmount];
//		for (int i = 0; i < fitness.length; i++) {
//			fitness[i] = activeCars.get(i).currentScore;
//		}
		generation = generationManager.getNextGeneration(fitness);
		generationManager.saveGenerationToFile(savedGenerationFile);
		if(GameSettings.UpdateTerrain) {
			terrain.newBody(world);
		}
//		for (int i = 0; i < carsAmount; i++) {
//			activeCars.get(i).destroyBody();
//			Car currentCar = new Car(distanceCarLeft+1, 6, gameWindow, generation[i]);
//			currentCar.createBody(world);
//			activeCars.set(i, currentCar);
//		}
		StatisticsTracker.nextGeneration(fitness);
	}
	
	private void readKeyInputs() {
		if(!gameWindow.followedCar.isDead) {
			if(gameWindow.pressedKeys.contains('W') && gameWindow.pressedKeys.contains('S')) {
				gameWindow.followedCar.applyTorque = 0;
			}else if(gameWindow.pressedKeys.contains('W')){
				gameWindow.followedCar.applyTorque = 1;
			}else if(gameWindow.pressedKeys.contains('S')){
				gameWindow.followedCar.applyTorque = -1;
			}else {
				gameWindow.followedCar.applyTorque = 0;
			}
		}
	}
}
