package i5.las2peer.services.servicePackage.entities;

import java.util.LinkedList;

public class Cover {
	private int coverId;
	private double cost;
	private LinkedList<Community> communities;
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public void setCoverId(int id){
		this.coverId = id;
	}
	
	public int getCoverId(){
		return coverId;
	}
	
	public void setCosts(double c){
		this.cost = c;
	}
	
	public double getCosts(){
		return cost;
	}
	
	public void setCommunties(LinkedList<Community> c){
		this.communities = c;
	}
	
	public LinkedList<Community> getCommunties(){
		return communities;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	public void addComm(Community comm){
		this.communities.add(comm);
	}
}
