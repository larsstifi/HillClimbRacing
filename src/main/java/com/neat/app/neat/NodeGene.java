package neat;

import java.util.*;

public class NodeGene {
	
	enum TYPE{
		INPUT,
		HIDDEN,
		OUTPUT,
		;
	}
	private TYPE type;
	private int id;
	
	
	
	public NodeGene(TYPE type, int id) {
		this.type = type;
		this.id = id;
	}
	public TYPE getType() {
		return type;
	}
	public void setType(TYPE type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public NodeGene copy() {
		return new NodeGene(type, id);
	}
	
}
