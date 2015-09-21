package i5.las2peer.services.servicePackage.entities;

import java.util.LinkedList;

public class Community {

	private int comID;
	
	private LinkedList<Node> members = new LinkedList<Node>();
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public void setCommunityID(int comID){
		this.comID = comID;
	}
	
	public int getCommunityID(){
		return comID;
	}
	
	public void setMembers(LinkedList<Node> mem){
		this.members = mem;
	}
	
	public LinkedList<Node> getMembers(){
		return members;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	//method for adding members to the community
	public void addMember(Node node){
		this.members.add(node);
	}
	
}
