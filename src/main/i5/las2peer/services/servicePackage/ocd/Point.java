package i5.las2peer.services.servicePackage.ocd;

import org.apache.commons.math3.linear.ArrayRealVector;

public class Point {
	private int nodeid;
	private ArrayRealVector coord;
	
	///////////////////
	////Constructor////
	///////////////////

	public Point(int i, ArrayRealVector vector) {
		nodeid = i;
		coord = vector;
	}
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public void setNodeId(int id){
		this.nodeid = id;
	}
	
	public int getNodeId(){
		return nodeid;
	}
	
	public void setCoordinates(ArrayRealVector coord){
		this.coord = coord;
	}
	
	public ArrayRealVector getCoordinates(){
		return coord;
	}
}
