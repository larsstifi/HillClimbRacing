package neat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Util {
	public static float compatibilityDistance(Genome genome1, Genome genome2, float c1, float c2, float c3) {

		int excessGenes = 0;
		int disjointGenes = 0;
		float avgWeightDiff = 0;
		float weightDifference = 0;
		int matchingGenes = 0;

		// nodes
		List<Integer> nodeKeys1 = asSortedList(genome1.getNodes().keySet());
		List<Integer> nodeKeys2 = asSortedList(genome2.getNodes().keySet());

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
		List<Integer> conKeys1 = asSortedList(genome1.getConnections().keySet());
		List<Integer> conKeys2 = asSortedList(genome2.getConnections().keySet());

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

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

}
