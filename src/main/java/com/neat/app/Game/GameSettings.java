package Game;

public class GameSettings {
	//species settings
	public static volatile float C1 = 1f;
	public static volatile float C2 = 1f;
	public static volatile float C3 = 0.4f;
	public static volatile float DT = 5f;
	public static volatile float SIZE_PENALTY = 0.5f;
	
	public static volatile int minSpeciesSizeforChampion = 5;
	
	//mutation settings
	public static volatile float PROBABLILITY_PRETURBING = 0.9f;
	public static volatile float PROBABILITY_REACTIVATION = 0.25f;
	public static volatile float WEIGHTS_MUTATION_RATE = 0.5f;
	public static volatile float ADD_CONNECTION_RATE = 0.05f;
	public static volatile float ADD_NODE_RATE = 0.02f;
	
	//game settings
	public static volatile boolean drawGame = true;
	
	public static volatile boolean UpdateTerrain = true;
}
