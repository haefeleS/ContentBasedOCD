package i5.las2peer.services.servicePackage.entities;

//import javax.persistence.*;

import i5.las2peer.services.servicePackage.preprocessing.TextProcessor;


public class Node {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int nodeID;
	private String userID;
	//@ManyToOne(fetch=FetchType.LAZY)
	//@JoinColumn(name = "GRAPHID")
	//private Graph graph;
	private String content;
	private TextProcessor textproc = new TextProcessor();
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public int getNodeID() {
	    return nodeID;
	  }

	  public void setNodeID(int nodeID) {
	    this.nodeID = nodeID;
	  } 
	  
	  public String getUserID() {
		    return userID;
		  }

	  public void setUserID(String userID) {
		    this.userID = userID;
		  }
		
	  public String getContent() {
		    return content;
		  }

	  public void setContent(String content) {
		  //this.content = content;
		  this.content = textproc.preprocText(content);
		  }
	  
	 /* public Graph getGraph(){
		  return graph;
	  }
	  
	  public void setGraph(Graph graph){
		  this.graph = graph;
	  }*/
}
