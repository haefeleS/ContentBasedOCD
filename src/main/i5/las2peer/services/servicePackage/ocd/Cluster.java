package i5.las2peer.services.servicePackage.ocd;

import java.util.LinkedList;

import i5.las2peer.services.servicePackage.entities.Node;

import org.apache.commons.math3.linear.ArrayRealVector;

public class Cluster {
	
	private LinkedList<Integer> points;
	private ArrayRealVector centroid;
	private int id;
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////

	public LinkedList<Integer> getPoints(){
		return this.points;
	}
	
	public void setPoints(LinkedList<Integer> points){
		this.points = points;
	}
	
	public ArrayRealVector getCentroid(){
		return this.centroid;
	}
	
	public void setCentroid(ArrayRealVector cent){
		this.centroid = cent;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	//add corresponding nodeid to the cluster for assignment of the node
	public void assignPoint(int point){
		points.add(point);
	}

}
