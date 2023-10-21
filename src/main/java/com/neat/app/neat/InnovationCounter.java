package neat;

public class InnovationCounter {
	static int countCon = 0;
	static int countNode= 0;
	
	public static int getNextCon() {
		return countCon++;
	}
	public static int getNextNode() {
		return countNode++;
	}
}
