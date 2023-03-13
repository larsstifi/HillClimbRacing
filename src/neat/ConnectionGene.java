package neat;

public class ConnectionGene {
	private int inNode;
	private int outNode;
	private float weight;
	private boolean expressed;
	private int innovationNumber;
	
	
	
	public ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovationNumber) {
		this.inNode = inNode;
		this.outNode = outNode;
		this.weight = weight;
		this.expressed = expressed;
		this.innovationNumber = innovationNumber;
	}
	public int getInnovationNumber() {
		return innovationNumber;
	}
	public void setInnovationNumber(int innovationNumber) {
		this.innovationNumber = innovationNumber;
	}
	public int getInNode() {
		return inNode;
	}
	public void setInNode(int inNode) {
		this.inNode = inNode;
	}
	public int getOutNode() {
		return outNode;
	}
	public void setOutNode(int outNode) {
		this.outNode = outNode;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	public boolean isExpressed() {
		return expressed;
	}
	public void setExpressed(boolean expressed) {
		this.expressed = expressed;
	}
	
	public ConnectionGene copy() {
		return new ConnectionGene(inNode, outNode, weight, expressed, innovationNumber);
	}

	
}
