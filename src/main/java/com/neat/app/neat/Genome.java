package neat;

import java.util.*;

import Game.GameSettings;
import neat.NodeGene.TYPE;

public class Genome {
	
	private static List<Integer> tmpList1 = new ArrayList<Integer>();
	private static List<Integer> tmpList2 = new ArrayList<Integer>();
	
	
	private Map<Integer, NodeGene> nodes;
	private Map<Integer, ConnectionGene> connections;
	private Map<Integer, List<ConnectionGene>> inEdges;
	private Map<Integer, List<ConnectionGene>> outEdges;
	
	NodeGene[] inputNodes;
	NodeGene[] outputNodes;
	LinkedList<Integer> topoSort;

	//MOVED TO GAMESETTINGS class
//	final float PROBABLILITY_PRETURBING = 0.9f;
//	final float PROBABILITY_REACTIVATION = 0.25f;
//	final float WEIGHTS_MUTATION_RATE = 0.5f;
//	final float ADD_CONNECTION_RATE = 0.05f;
//	final float ADD_NODE_RATE = 0.02f;
	

	public Genome() {
		this.nodes = new HashMap<Integer, NodeGene>();
		this.connections = new HashMap<Integer, ConnectionGene>();
		this.inEdges = new HashMap<Integer, List<ConnectionGene>>();
		this.outEdges = new HashMap<Integer, List<ConnectionGene>>();
	}
	
	/**Evaluates the genome
	 * @param inputs inputs to neural network, must match size
	 * @return output of neural network
	 */
	public float[] evaluate(float[] inputs) {
		HashMap<Integer, Float> activations = new HashMap<Integer, Float>();
		for(int i = 0; i < inputs.length; i++) {
			activations.put(i, inputs[i]);
		}
		//iterate over nodes in topological order
		for(Integer node : topoSort) {
			//iterate over all outEdges of current node
			if(outEdges.containsKey(node)) {
				for (ConnectionGene con : outEdges.get(node)) {
					int currentOutNode = con.getOutNode();
					//add out node to activations map
					if(!activations.containsKey(currentOutNode)) {
						activations.put(currentOutNode, 0f);
					}
					float currentActivation = activations.get(currentOutNode);
					currentActivation += activations.containsKey(node)?Math.tanh(activations.get(node))*con.getWeight():0;
					activations.put(currentOutNode, currentActivation);
				}
			}
		}
		float[] out = new float[outputNodes.length];
		for (int i = 0; i < outputNodes.length; i++) {
			out[i] = (float) (activations.containsKey(outputNodes[i].getId())?Math.tanh(activations.get(outputNodes[i].getId())):0);
		}
		
		return out;
	}
	
	/**
	 * updates the topological sorting of network
	 */
	public void updateTopoSort() {
		LinkedList<Integer> topoSortIntegers = new LinkedList<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		for (int i = 0; i < inputNodes.length; i++) {
			queue.add(inputNodes[i].getId());
		}
		while(!queue.isEmpty()) {
			Integer currentNode = queue.removeFirst();
			boolean hasInEdges = false;
			if(inEdges.containsKey(currentNode)) {
				for (ConnectionGene connection : inEdges.get(currentNode)) {
					if(!topoSortIntegers.contains(connection.getInNode())) {
						hasInEdges = true;
						break;
					}
				}
			}
			
			if(hasInEdges) {
				continue;
			}
			if(outEdges.containsKey(currentNode)) {
				for (ConnectionGene connection : outEdges.get(currentNode)) {
					if(!queue.contains(connection.getOutNode())) {
						queue.add(connection.getOutNode());
					}
				}
			}
			topoSortIntegers.addLast(currentNode);
		}
		this.topoSort = topoSortIntegers;
	}
	
	/**
	 * performs dfs algorithm and returns order
	 */
	private LinkedList<Integer> getBFS(Integer startNode) {
		LinkedList<Integer> order = new LinkedList<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		queue.add(startNode);
		visited.add(startNode);
		order.add(startNode);
		while(!queue.isEmpty()) {
			Integer currentNode = queue.removeFirst();
				if(outEdges.containsKey(currentNode)) {
				for (ConnectionGene connectionGene : outEdges.get(currentNode)) {
					if(!visited.contains(connectionGene.getOutNode())) {
						queue.addLast(connectionGene.getOutNode());
						visited.add(connectionGene.getOutNode());
						order.add(connectionGene.getOutNode());
					}
				}
			}
		}
		return order;
	}
	
	/**
	 * performs dfs algorithm backwards and returns order
	 */
	private LinkedList<Integer> getBFSBackwards(Integer startNode) {
		LinkedList<Integer> order = new LinkedList<Integer>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		
		queue.add(startNode);
		visited.add(startNode);
		order.add(startNode);
		while(!queue.isEmpty()) {
			Integer currentNode = queue.removeFirst();
			if(inEdges.containsKey(currentNode)) {
				for (ConnectionGene connectionGene : inEdges.get(currentNode)) {
					if(!visited.contains(connectionGene.getInNode())) {
						queue.addLast(connectionGene.getInNode());
						visited.add(connectionGene.getInNode());
						order.add(connectionGene.getInNode());
					}
				}
			}
		}
		return order;
	}
	
	/**
	 * clears old edges and create new ones from connectionGenes
	 */
	public void updateEdges() {
		
		this.inEdges.clear();
		this.outEdges.clear();
		
		for (ConnectionGene connectionGene : connections.values()) {
			if(connectionGene.isExpressed()) {
				addEdge(connectionGene);
			}
		}
	}
	/**
	 * @param connectionGene an expressed {@link ConnectionGene}
	 * 
	 */
	private void addEdge(ConnectionGene connectionGene) {
		Integer start = connectionGene.getInNode();
		Integer end = connectionGene.getOutNode();
		//add in Edge
		if(!inEdges.containsKey(end)) {
			inEdges.put(end, new LinkedList<ConnectionGene>());
		}
		inEdges.get(end).add(connectionGene);
		//add out Edge
		if(!outEdges.containsKey(start)) {
			outEdges.put(start, new LinkedList<ConnectionGene>());
		}
		outEdges.get(start).add(connectionGene);
	}
	
	private void removeEdge(ConnectionGene connectionGene) {
		Integer start = connectionGene.getInNode();
		Integer end = connectionGene.getOutNode();
		//remove in Edge
		if(inEdges.containsKey(end)) {
			inEdges.get(end).remove(connectionGene);
		}
		//remove out Edge
		if(outEdges.containsKey(start)) {
			outEdges.get(start).remove(connectionGene);
		}
	}
	
	/**
	 * @param inAmount input nodes amount
	 * @param outAmount output nodes amount
	 * @param mutationAmount amount of connection and node mutations applies
	 * @param r random number generator
	 */
	public void initialize(NodeGene[] inputNodes, NodeGene[] outputNodes, int mutationAmount, Random r) {
		nodes.clear();
		connections.clear();
		this.inputNodes = inputNodes;
		this.outputNodes = outputNodes;
		for (int i = 0; i < inputNodes.length; i++) {
			nodes.put(inputNodes[i].getId(), inputNodes[i]);
		}
		for (int i = 0; i < outputNodes.length; i++) {
			nodes.put(outputNodes[i].getId(), outputNodes[i]);
		}
		for (int j = 0; j < mutationAmount; j++) {
			this.addConnectionMutation(r);
			this.addNodeMutation(r);
			this.mutateWeight(r);
		}
		this.updateTopoSort();
	}
	
	public void mutate(Random r) {
		if(r.nextFloat() < GameSettings.WEIGHTS_MUTATION_RATE) {
			mutateWeight(r);
		}
		if(r.nextFloat() < GameSettings.ADD_CONNECTION_RATE) {
			addConnectionMutation(r);
		}
		if(r.nextFloat() < GameSettings.ADD_NODE_RATE) {
			addNodeMutation(r);
		}
	}

	private void mutateWeight(Random r) {
		for (ConnectionGene con : connections.values()) {
			if (r.nextFloat() < GameSettings.PROBABLILITY_PRETURBING) {
				con.setWeight(con.getWeight() * (r.nextFloat() * 4f - 2f));
			} else {
				con.setWeight(r.nextFloat() * 4f - 2f);
			}
		}
	}

	public void addConnectionMutation(Random r) {
		NodeGene node1 = (NodeGene) nodes.values().toArray()[(r.nextInt(nodes.size()))];
		while(node1.getType() == TYPE.OUTPUT) {
			node1 = (NodeGene) nodes.values().toArray()[(r.nextInt(nodes.size()))];
		}
		NodeGene node2 = (NodeGene) nodes.values().toArray()[(r.nextInt(nodes.size()))];
		while (node2 == node1 || node2.getType() == TYPE.INPUT) {
			node2 = (NodeGene) nodes.values().toArray()[(r.nextInt(nodes.size()))];
		}

		boolean reversed = false;
		if ((node1.getType() == NodeGene.TYPE.HIDDEN && node2.getType() == NodeGene.TYPE.INPUT)
				|| (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.HIDDEN)
				|| (node1.getType() == NodeGene.TYPE.OUTPUT && node2.getType() == NodeGene.TYPE.INPUT)) {
			reversed = true;
		}

		
		ConnectionGene newCon = new ConnectionGene(reversed ? node2.getId() : node1.getId(),
				reversed ? node1.getId() : node2.getId(), r.nextFloat() * 2f - 1f, true, InnovationCounter.getNextCon());
		this.addConnection(newCon);
		

	}

	public void addNodeMutation(Random r) {
		if(connections.size()>0) {
			ConnectionGene randomCon = (ConnectionGene) connections.values().toArray()[r.nextInt(connections.size())];
			NodeGene inNode = nodes.get(randomCon.getInNode());
			NodeGene outNode = nodes.get(randomCon.getOutNode());

			randomCon.setExpressed(false);
			this.removeEdge(randomCon);

			NodeGene newNode = new NodeGene(TYPE.HIDDEN, InnovationCounter.getNextNode());
			ConnectionGene inToNew = new ConnectionGene(inNode.getId(), newNode.getId(), 1f, true,
					InnovationCounter.getNextCon());
			ConnectionGene newToOut = new ConnectionGene(newNode.getId(), outNode.getId(), randomCon.getWeight(), true,
					InnovationCounter.getNextCon());

			nodes.put(newNode.getId(), newNode);
			connections.put(inToNew.getInnovationNumber(), inToNew);
			connections.put(newToOut.getInnovationNumber(), newToOut);
		}
	}

	/**
	 * @param parent1 More fit parent
	 * @param parent2 less fit parent
	 * @return Child of Parents
	 */
	public Genome crossover(Genome parent1, Genome parent2, Random r) {
		Genome child = new Genome();
		child.inputNodes = parent1.inputNodes;
		child.outputNodes = parent1.outputNodes;

		Map<Integer, NodeGene> nodes1 = parent1.getNodes();
		Map<Integer, NodeGene> nodes2 = parent2.getNodes();
		for (NodeGene node1 : parent1.getNodes().values()) {
			child.addNode(node1.copy());
		}


		Map<Integer, ConnectionGene> cons1 = parent1.getConnections();
		Map<Integer, ConnectionGene> cons2 = parent2.getConnections();
		for (ConnectionGene con1 : cons1.values()) {
			if (cons2.containsKey(con1.getInnovationNumber())) { // matching
				ConnectionGene con2 = cons2.get(con1.getInnovationNumber());
				ConnectionGene newCon = r.nextBoolean() ? con1.copy() : con2.copy();
				if(!con1.isExpressed() || !con2.isExpressed()) {
					newCon.setExpressed(r.nextFloat()<GameSettings.PROBABILITY_REACTIVATION);
				}
				child.addConnection(newCon);
			} else {
				child.addConnection(con1.copy());
			}
		}
		return child;
	}
	

	public Map<Integer, NodeGene> getNodes() {
		return nodes;
	}

	public void setNodes(Map<Integer, NodeGene> nodes) {
		this.nodes = nodes;
	}

	public Map<Integer, ConnectionGene> getConnections() {
		return connections;
	}

	public void setConnections(Map<Integer, ConnectionGene> connections) {
		this.connections = connections;
	}

	public void addNode(NodeGene newNode) {
		this.nodes.put(newNode.getId(), newNode);
	}
	
	public int getSize() {
		return connections.size() + nodes.size();
	}

	public void addConnection(ConnectionGene newCon) {
		//check if out edge is input node
		boolean outIsInputNode = false;
		for (int i = 0; i < inputNodes.length; i++) {
			if(inputNodes[i].getId() == newCon.getOutNode()) {
				outIsInputNode = true;
				break;
			}
		}
		
		//check if connection already exists
		boolean connectionExists = false;
		for (ConnectionGene con : connections.values()) {
			if ((con.getInNode() == newCon.getInNode() && con.getOutNode() == newCon.getOutNode())
					|| (con.getInNode() == newCon.getOutNode() && con.getOutNode() == newCon.getInNode())) {
				connectionExists = true;
				break;
			}
		}
		//check if new connection would create a cycle
		boolean createsCycle = false;
		if(this.getBFSBackwards(newCon.getInNode()).contains(newCon.getOutNode())) {
			createsCycle = true;
		}
		if(!createsCycle && !connectionExists  && !outIsInputNode) {
			this.connections.put(newCon.getInnovationNumber(), newCon);
			if(newCon.isExpressed()) {
				this.addEdge(newCon);
			}
		}
		
	}
	
	
	
	public static float compatibilityDistance(Genome genome1, Genome genome2, float c1, float c2, float c3) {

		int excessGenes = 0;
		int disjointGenes = 0;
		float avgWeightDiff = 0;
		float weightDifference = 0;
		int matchingGenes = 0;

		// nodes
		List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet(), tmpList1);
		List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet(), tmpList2);

		int highestInnovation1 = nodeKeys1.get(nodeKeys1.size() - 1);
		int highestInnovation2 = nodeKeys2.get(nodeKeys2.size() - 1);
		int indices = Math.max(highestInnovation1, highestInnovation2);
		for (int i = 0; i <= indices; i++) {
			NodeGene node1 = genome1.getNodes().get(i);
			NodeGene node2 = genome2.getNodes().get(i);
			if (node1 != null && node2 == null) {
				if (highestInnovation2 < i) {
					excessGenes++;
				} else {
					disjointGenes++;
				}
			} else if (node1 == null && node2 != null) {
				if (highestInnovation1 < i) {
					excessGenes++;
				} else {
					disjointGenes++;
				}
			}
		}

		// connections
		List<Integer> conKeys1 = asSortedList(genome1.getConnections().keySet(), tmpList1);
		List<Integer> conKeys2 = asSortedList(genome2.getConnections().keySet(), tmpList2);

		highestInnovation1 = conKeys1.get(conKeys1.size() - 1);
		highestInnovation2 = conKeys2.get(conKeys2.size() - 1);
		indices = Math.max(highestInnovation1, highestInnovation2);

		for (int i = 0; i <= indices; i++) {
			ConnectionGene connection1 = genome1.getConnections().get(i);
			ConnectionGene connection2 = genome2.getConnections().get(i);
			if (connection1 != null) {
				if (connection2 != null) {
					matchingGenes++;
					weightDifference += Math.abs(connection1.getWeight() - connection2.getWeight());
				} else if (highestInnovation2 < i) {
					excessGenes++;
				} else {
					disjointGenes++;
				}
			} else if (connection2 != null) {
				if (highestInnovation1 < i) {
					excessGenes++;
				} else {
					disjointGenes++;
				}
			}

		}

		avgWeightDiff = weightDifference / matchingGenes;

		int n = Math.max(genome1.getNodes().size(), genome2.getNodes().size());

		if (n < 20) {
			n = 1;
		}

		return (excessGenes * c1) / n + (disjointGenes * c2) / n + avgWeightDiff * c3;

	}

	private static List<Integer> asSortedList(Collection<Integer> c, List<Integer> list) {
		list.clear();
		list.addAll(c);
		java.util.Collections.sort(list);
		return list;
	}
	
}
