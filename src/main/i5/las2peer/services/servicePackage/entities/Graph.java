package i5.las2peer.services.servicePackage.entities;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;


@Entity
public class Graph {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int graphID;
	private String origin; 		//data set the graph is based on
	@OneToMany(mappedBy = "graph")
	private LinkedList<Node> nodes;
	@OneToMany(mappedBy = "graph")
	private LinkedList<LinkedNode> linkedNodes;
	
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
	
	
	public LinkedList<Node> getNodes(){
		return this.nodes;
	}
	
	public void setNodes(LinkedList<Node> nodes){
		this.nodes = nodes;
	}
	
	public LinkedList<LinkedNode> getLinkedNodes(){
		return this.linkedNodes;
	}
	
	public void setLinkedNodes(LinkedList<LinkedNode> linkedNodes){
		this.linkedNodes = linkedNodes;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	public void addNodes(Node node){
		nodes.add(node);
	}
	
	public void deleteNode(int nodeid){
		Node node = new Node();
		
		for(Iterator<Node>it = this.nodes.iterator(); it.hasNext();){
			node = it.next();
			if(node.getNodeID() == nodeid){
				this.nodes.remove(node);
			}
		}
	}
	
}
