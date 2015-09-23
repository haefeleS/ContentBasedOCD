package i5.las2peer.services.servicePackage.ocd;

import java.util.LinkedList;

public class Clustering {
	private double costs;
	private LinkedList<Cluster> cluster;
	
	///////////////////
	////Constructor////
	///////////////////
		
	public Clustering(){
		cluster = new LinkedList<Cluster>();
	}
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public void setCosts(double c){
		this.costs = c;
	}
	
	public double getCosts(){
		return costs;
	}
	
	public void setClustering(LinkedList<Cluster> c){
		this.cluster = c;
	}
	
	public LinkedList<Cluster> getClustering(){
		return cluster;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	public void addCluster(Cluster c){
		this.cluster.add(c);
	}
}
