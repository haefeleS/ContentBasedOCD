package i5.las2peer.services.servicePackage.entities;

import java.util.List;

import javax.persistence.*;

@Entity
public class Graph {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int graphID;
	private String origin; 		//data set the graph is based on
	@OneToMany(mappedBy = "graph")
	private List<Node> nodes;
	@OneToMany(mappedBy = "graph")
	private List<LinkedNode> linkedNodes;
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public int getGraphID(){
		return this.graphID;
	}
	
	public void setGraphID(int graphID){
		this.graphID = graphID;
	}
	
	public String getOrigin(){
		return this.origin;
	}
	
	public void setOrigin(String origin){
		this.origin = origin;
	}
	
	
	public List<Node> getNodes(){
		return this.nodes;
	}
	
	public void setNodes(List<Node> nodes){
		this.nodes = nodes;
	}
	
	public List<LinkedNode> getLinkedNodes(){
		return this.linkedNodes;
	}
	
	public void setLinkedNodes(List<LinkedNode> linkedNodes){
		this.linkedNodes = linkedNodes;
	}
	
}
