package neat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.*;

import Game.GameSettings;
import neat.NodeGene.TYPE;

public class GenerationManager {

	final float C1 = 1f;
	final float C2 = 1f;
	final float C3 = 0.4f;
	final float DT = 5f;
	final float SIZE_PENALTY = 0.5f;
	
	final int minSpeciesSizeforChampion = 5;

	int generationSize;
	int inAmount;
	int outAmount;
	
	NodeGene[] inputNodes;
	NodeGene[] outputNodes;

	Random r;
	FitnessGenome[] currentGeneration;
	public volatile Map<FitnessGenome, Species> speciesMap;
	List<Species> species;

	public GenerationManager(int generationSize, int inAmount, int outAmount) {
		super();
		this.generationSize = generationSize;
		this.inAmount = inAmount;
		this.outAmount = outAmount;
		this.currentGeneration = new FitnessGenome[generationSize];
		this.species = new LinkedList<Species>();
		this.speciesMap = new HashMap<FitnessGenome, Species>();
		this.r = new Random(1);
		inputNodes = new NodeGene[inAmount];
		outputNodes = new NodeGene[outAmount];
	}
	
	public void saveGenerationToFile(File file) {
		FileWriter out;
		try {
			out = new FileWriter(file);
			out.write(String.format("%d %d %d \n", generationSize, inputNodes.length, outputNodes.length));
			
			for (int i = 0; i < inputNodes.length; i++) {
				out.write(String.format("%d \n",inputNodes[i].getId()));
			}
			
			for (int i = 0; i < outputNodes.length; i++) {
				out.write(String.format("%d \n",outputNodes[i].getId()));
			}
			
			for(FitnessGenome currentGenom: currentGeneration) {
				out.write(String.format("%d %d \n", currentGenom.genome.getNodes().size() -inputNodes.length - outputNodes.length, currentGenom.genome.getConnections().size()));
				for(NodeGene currentNodeGene : currentGenom.genome.getNodes().values()) {
					if(currentNodeGene.getType() == TYPE.HIDDEN) {
						out.write(String.format("%d \n", currentNodeGene.getId()));
					}
				}
				for(ConnectionGene currentConnectionGene : currentGenom.genome.getConnections().values()) {
					out.write(String.format("%d %d %f %b %d \n", currentConnectionGene.getInNode(), currentConnectionGene.getOutNode(), currentConnectionGene.getWeight(), currentConnectionGene.isExpressed(), currentConnectionGene.getInnovationNumber()));
				}
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadGenerationFromFile(File file) {
		speciesMap.clear();
		species.clear();
		try {
			Scanner scanner = new Scanner(file);
			generationSize = scanner.nextInt();
			currentGeneration = new FitnessGenome[generationSize];
			inputNodes = new NodeGene[scanner.nextInt()];
			outputNodes = new NodeGene[scanner.nextInt()];
			
			for (int i = 0; i < inputNodes.length; i++) {
				inputNodes[i] = new NodeGene(TYPE.INPUT, scanner.nextInt());
			}
			for (int i = 0; i < outputNodes.length; i++) {
				outputNodes[i] = new NodeGene(TYPE.OUTPUT, scanner.nextInt());
			}
			
			for(int i = 0; i < generationSize; i++) {
				Genome newGenome = new Genome();
				newGenome.initialize(inputNodes, outputNodes, 0, r);
				int hiddenNodesAmount = scanner.nextInt();
				int connectionsAmount = scanner.nextInt();
				
				for (int j = 0; j < hiddenNodesAmount; j++) {
					newGenome.addNode(new NodeGene(TYPE.HIDDEN, scanner.nextInt()));
				}
				for (int j = 0; j < connectionsAmount; j++) {
					newGenome.addConnection(new ConnectionGene(scanner.nextInt(), scanner.nextInt(), scanner.nextFloat(), scanner.nextBoolean(), scanner.nextInt()));
				}
				newGenome.updateTopoSort();
				currentGeneration[i] = new FitnessGenome(newGenome, 0f);
			}
			scanner.close();
			updateSpecies();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createInitalPopulation() {
		inputNodes = new NodeGene[inAmount];
		outputNodes = new NodeGene[outAmount];
		for (int i = 0; i < inputNodes.length; i++) {
			NodeGene newInputNodeGene =  new NodeGene(TYPE.INPUT, InnovationCounter.getNextNode());
			inputNodes[i] = newInputNodeGene;
		}
		for (int i = 0; i < outputNodes.length; i++) {
			NodeGene newOutputNodeGene = new NodeGene(TYPE.OUTPUT, InnovationCounter.getNextNode());
			outputNodes[i] = newOutputNodeGene;
		}
		
		for (int i = 0; i < generationSize; i++) {
			Genome newGenome = new Genome();
			newGenome.initialize(inputNodes, outputNodes, 4, r);
			FitnessGenome newFitnessGenome = new FitnessGenome(newGenome, 0);
			currentGeneration[i] = newFitnessGenome;
		}
		updateSpecies();
		
	}

	public FitnessGenome[] getCurrentGeneration() {
		return currentGeneration;
	}
	
	public void setFitness(float[] generationFitness) {
		for (int i = 0; i < generationFitness.length; i++) {
			currentGeneration[i].fitness = generationFitness[i];
		}
	}

	public FitnessGenome[] getNextGeneration(float[] generationFitness) {
		//updates the fitness
		setFitness(generationFitness);
		//manage species and create new generation and store in currentGeneration
		generateNextGeneration(r);
		return currentGeneration;
	}
	
	private void generateNextGeneration(Random r) {
		updateSpeciesFitness();
		FitnessGenome[] nextGeneration = new FitnessGenome[generationSize];
		int indexCounter = 0;
		float totalAdjustedFitness = 0f;
		//put all champions in next generation
		for(Species s: species) {
			if(s.members.size() >= minSpeciesSizeforChampion) {
				nextGeneration[indexCounter++] = s.champion;
			}
			totalAdjustedFitness += s.adjustedFitness;
		}
		
		//add new genomes till newgeneration reached max size
		while(indexCounter < generationSize) {
			//pick random species based on fitness
			float random = r.nextFloat() * totalAdjustedFitness;
			float totalTracker = 0f;
			for(Species s: species) {
				totalTracker += s.adjustedFitness;
				if(totalTracker > random) {
					//pick 2 random parents and create child
					FitnessGenome parent1 = s.members.get(r.nextInt(s.members.size()));
					FitnessGenome parent2 = s.members.get(r.nextInt(s.members.size()));
					//make sure parent 1 is fitter
					if(parent1.fitness<parent2.fitness) {
						FitnessGenome tempFitnessGenome = parent1;
						parent1 = parent2;
						parent2 = tempFitnessGenome;
					}
					
					FitnessGenome childFitnessGenome = new FitnessGenome(parent1.genome.crossover(parent1.genome, parent2.genome, r), 0f);
					childFitnessGenome.genome.mutate(r);
					childFitnessGenome.genome.updateTopoSort();
					nextGeneration[indexCounter++] = childFitnessGenome;
					break;
				}
			}
		}		
		currentGeneration = nextGeneration;
		updateSpecies();
	}
	//gets called after a new generation was created
	private void updateSpecies() {
		
		//clear members and choose new random mascot
		for (Species s : species) {
			s.reset(r);
		}
		speciesMap.clear();
		//assign members to species
		for (int i = 0; i < currentGeneration.length; i++) {
			FitnessGenome current = currentGeneration[i];
			boolean foundSpecies = false;
			for (Species s : species) {
				if (Genome.compatibilityDistance(current.genome, s.mascot.genome, GameSettings.C1, GameSettings.C2, GameSettings.C3) < GameSettings.DT) {
					s.members.add(current);
					speciesMap.put(current, s);
					current.speciesID = s.speciesID;
					foundSpecies = true;
					break;
				}
			}
			if (!foundSpecies) {
				Species newSpecies = new Species(current);
				species.add(newSpecies);
				current.speciesID = newSpecies.speciesID;
			}
		}
		//remove empty species
		LinkedList<Species> emptySpecies = new LinkedList<Species>();
		for (Species s : species) {
			if(s.members.size() == 0) {
				emptySpecies.add(s);
			}
		}
		species.removeAll(emptySpecies);

		StatisticsTracker.addSpeciesScore(this.species);
	}
	//called before generating new generation
	private void updateSpeciesFitness(){
		for (Species s : species) {
			s.updateAdjustedFitness();
		}
	}

	public class Species {
		public FitnessGenome mascot;
		public FitnessGenome champion;
		public List<FitnessGenome> members;
		float maxFitness;
		float adjustedFitness;
		int speciesID;

		public Species(FitnessGenome mascot) {
			this.mascot = mascot;
			this.members = new LinkedList<FitnessGenome>();
			this.members.add(mascot);
			this.adjustedFitness = 0f;
			this.maxFitness = Float.NEGATIVE_INFINITY;
			this.speciesID = StatisticsTracker.speciesIDTracker++;
		}
		
		public void reset(Random r) {
			this.mascot = members.get(r.nextInt(members.size()));
			members.clear();
		}
		public void updateAdjustedFitness() {
			this.adjustedFitness = 0f;
			this.maxFitness = Float.NEGATIVE_INFINITY;
			for(FitnessGenome member : members) {
				this.adjustedFitness += (member.fitness/(GameSettings.SIZE_PENALTY * member.genome.getSize()))/members.size();
				if(member.fitness > maxFitness) {
					maxFitness = member.fitness;
					champion = member;
				}
			}
		}
	}

	public class FitnessGenome implements Comparable<FitnessGenome> {
		public Genome genome;
		public float fitness;
		public int speciesID;

		public FitnessGenome(Genome genome, float fitness) {
			this.genome = genome;
			this.fitness = fitness;
		}

		@Override
		public int compareTo(FitnessGenome o) {
			if (this.fitness > o.fitness) {
				return 1;
			} else if (this.fitness < o.fitness) {
				return -1;
			} else {
				return 0;
			}

		}
	}
}
