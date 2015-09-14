package i5.las2peer.services.servicePackage.ocd;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

public class Similarities {
	
	public double cosineSim(Array2DRowRealMatrix matrix){
		double res = 0;
		double dot = 0;
		double normS = 0;
		double normR = 0;
		ArrayRealVector sender = (ArrayRealVector)matrix.getRowVector(0);
		ArrayRealVector receiver = (ArrayRealVector)matrix.getRowVector(1);
		
		dot = sender.dotProduct(receiver);
		normS = sender.getNorm();
		normR = receiver.getNorm();
		
		if(normS != 0 && normR != 0){
			res = dot / (normS * normR);
		}
		
		return res;
	}

}
