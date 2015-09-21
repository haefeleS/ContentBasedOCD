package i5.las2peer.services.servicePackage.ocd;

import java.util.LinkedList;

import org.apache.commons.math3.linear.ArrayRealVector;

public class ClusteringMethod {

	public LinkedList<Cluster> gradDescClustering(Termmatrix termMat, int k){
		LinkedList<Cluster> clustering = new LinkedList<Cluster>();
		
		return clustering;
	}
	
	public double distanceCosSim(ArrayRealVector v, ArrayRealVector u){
		double dist = 0;
		Similarities sim = new Similarities();
		
		dist = 1 - sim.cosineSim(v, u);
		
		return dist;
	}
}
