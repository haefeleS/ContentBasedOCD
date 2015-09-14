package i5.las2peer.services.servicePackage.entities;

import i5.las2peer.services.servicePackage.preprocessing.TextProcessor;

public class LinkedNode {
	
	private int nodeID;
	private int sender;
	private int receiver;
	private Graph graph;
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
	  
	  public int getSender() {
		    return sender;
		  }

	  public void setSender(int send) {
		    this.sender = send;
		  }
	  
	  public int getReceiver(){
		  return receiver;
	  }
	  
	  public void setReceiver(int rec){
		  this.receiver = rec;
	  }
		
	  public String getContent() {
		    return content;
		  }

	  public void setContent(String content) {
		  //this.content = content;
		  this.content = textproc.preprocText(content);
		  }
}
