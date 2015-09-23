package i5.las2peer.services.servicePackage.ocd;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

public class ClusteringMethod {
	
	//find clustering with smallest costs, by computing sizes between 1 and maxClust
	
	public Clustering optCosts(Termmatrix termMat, int maxClust){
		Clustering opt = new Clustering();
		Clustering temp = new Clustering();
		if(maxClust > termMat.getNodeIdList().size()){
			return null;
		}
		for(int i = 1; i <= maxClust; i++){
			opt = gradDescClustering(termMat, i);
			temp = gradDescClustering(termMat, i+1);
			if(opt.getCosts() > temp.getCosts()){
				opt = temp;
			}
		}
		return opt;
	}
	
	// compute k-clustering

	public Clustering gradDescClustering(Termmatrix termMat, int k){
		Clustering clustering = new Clustering();
		CostFunction costFunc = new CostFunction();
		Random randGen = new Random();
		int vectorLength = termMat.getMatrix().getColumnDimension();
		boolean change = true;
		double costs = 0;
		
		//initialize clusters with centroids randomly generated
		for(int i = 0; i < k; i++){
			Cluster c = new Cluster();
			ArrayRealVector cent = new ArrayRealVector();
			for(int j = 0; j < vectorLength; j++)
				cent.setEntry(j, (randGen.nextDouble() + randGen.nextInt()));
			c.setCentroid(cent);
			clustering.addCluster(c);
		}
		
		//assign nodes and update centroids as long as they change
		while(change){
		clustering.setClustering(assignCluster(clustering.getClustering(),termMat.getMatrix(), termMat.getNodeIdList()));
		LinkedList<ArrayRealVector> centroids = getCentroids(clustering.getClustering());
		clustering.setClustering(updateCentroids(clustering.getClustering()));
		change = different(centroids,getCentroids(clustering.getClustering()));
		}
		
		k++;
		costs = costFunc.value(clustering.getClustering(), termMat.getNodeIdList().size());
		clustering.setCosts(costs);
		
		return clustering;
	}
	
	private LinkedList<Cluster> assignCluster(LinkedList<Cluster> clustering, Array2DRowRealMatrix matrix, LinkedList<Integer> nodeList){
		double dist = 0;
		double tempDist = 0;
		int index;
		Cluster match;
		 		
		for(int i = 0; i < matrix.getRowDimension(); i++){

			ArrayRealVector vector = (ArrayRealVector) matrix.getRowVector(i);
			Point point = new Point(nodeList.get(i),vector);
			Iterator<Cluster> it = clustering.iterator();
			match = it.next();
			dist = distanceCosSim(match.getCentroid(), vector);
			index = clustering.indexOf(match);
			
			while(it.hasNext()){
				Cluster curr = it.next();
				tempDist = distanceCosSim(curr.getCentroid(), vector);
				if(tempDist <= dist){
					match = curr;
					dist = tempDist;
					index = clustering.indexOf(match);
				}
			}
			match.assignPoint(point);
			clustering.set(index, match);
		}
		
		return clustering;
	}
	
	private LinkedList<Cluster> updateCentroids(LinkedList<Cluster> clust){
		CostFunction costFunc = new CostFunction();
		for(Iterator<Cluster> it = clust.iterator(); it.hasNext();){
			Cluster curr = it.next();
			ArrayRealVector cent = curr.getCentroid();
			cent = cent.add(costFunc.derivativeValue(curr));
			curr.setCentroid(cent);
		}
		
		return clust;
	}
	
	private double distanceCosSim(ArrayRealVector v, ArrayRealVector u){
		double dist = 0;
		Similarities sim = new Similarities();
		
		dist = 1 - sim.cosineSim(v, u);
		
		return dist;
	}
	
	private LinkedList<ArrayRealVector> getCentroids(LinkedList<Cluster> clust){
		LinkedList<ArrayRealVector> res = new LinkedList<ArrayRealVector>();
		for(Iterator<Cluster> it = clust.iterator(); it.hasNext();){
			res.add(it.next().getCentroid());
		}
		return res;
	}
	
	private boolean different(LinkedList<ArrayRealVector> a, LinkedList<ArrayRealVector> b){
		for(int i = 0; i < a.size(); i++){
			if(!a.get(i).equals(b.get(i))){
				return false;
			}
		}
		return true;
	}
}
