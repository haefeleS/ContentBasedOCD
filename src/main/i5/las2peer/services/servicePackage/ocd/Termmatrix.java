package i5.las2peer.services.servicePackage.ocd;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.json.JSONArray;

import i5.las2peer.services.servicePackage.entities.Node;
import i5.las2peer.services.servicePackage.preprocessing.WordConverter;
import i5.las2peer.services.servicePackage.util.ToJSON;

public class Termmatrix {

	private Array2DRowRealMatrix matrix;
	private LinkedList<String> wordlist;
	private LinkedList<Integer> nodelist;
	
	/////////////////////////
	////Getter and Setter////
	/////////////////////////
	
	public void setMatrix(Array2DRowRealMatrix matrix){
		this.matrix = matrix;
	}
	
	public Array2DRowRealMatrix getMatrix(){
		return matrix;
	}
	
	public void setWordlist(LinkedList<String> wordlist){
		this.wordlist = wordlist;
	}
	
	public LinkedList<String> getWordlist(){
		return wordlist;
	}
	
	public void setNodelist(LinkedList<Integer> nodelist){
		this.nodelist = nodelist;
	}
	
	public LinkedList<Integer> getNodeIdList(){
		return nodelist;
	}
	
	////////////////////////
	////Update Functions////
	////////////////////////
	
	public void addNode(int nodeid){
		this.nodelist.add(nodeid);
	}
	
	public void addWord(String word){
		this.wordlist.add(word);
	}
	
	/////////////////////////////
	////Computation Functions////
	/////////////////////////////
	
	/*public Termmatrix convertTFIDF(LinkedList<Node> nodes) throws Exception{
		LinkedList<String> wordlist = new LinkedList<String>();
		WordConverter conv = new WordConverter();
		String threads = "";
		String word = null;
		int column = 0;
		int row = 0;
		Log log = new Log();
		LinkedList<Node> tempList = new LinkedList<Node>();
		

		for(Iterator<Node> it = nodes.iterator(); it.hasNext();){ 
			boolean add = true;
			Node currNode = it.next();
			threads = currNode.getContent() + threads;      // compute all concatenated threads
			
			/*for(Iterator<Node> it2 = tempList.iterator(); it2.hasNext();){ //group by users
				Node currTemp = it2.next();
				if(currTemp.getUserID().equals(currNode.getUserID())){
					currTemp.setContent(currTemp.getContent()+currNode.getContent());
					add = false;
				}
				
			}
			if(add){
				tempList.add(currNode);
			}*/			
		/*}
		
		//int nodesSize = tempList.size();
		int nodesSize = nodes.size();
		
		if(threads == null || threads.isEmpty()){
			return null;
		}
		
		wordlist = conv.listWords(threads);
		this.setWordlist(wordlist);
		//wordlistdup = listWordsDup(threads);
		//len = stringLength(thread);
		
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(nodesSize ,wordlist.size());
		
		for(Iterator<Node>iter = tempList.iterator(); iter.hasNext();){
			Node node = iter.next();
			this.addNode(node.getNodeID());
			String thr = node.getContent();
			LinkedList<String> temp = conv.listWordsDup(thr);
			RealVector vector = new ArrayRealVector(wordlist.size());
			
			for(Iterator<String> it = wordlist.iterator(); it.hasNext();){
				word = it.next();
				int freq = conv.countWord(word, temp);
				int docFreq = conv.countDoc(word, nodes);
				double tfidf = freq * log.value((double)nodesSize/docFreq);
				
				vector.setEntry(column, tfidf);
				column++;
			}
			column = 0;
			matrix.setRowVector(row, vector);
			row++;
		}
		
		this.setMatrix(matrix);//json = converter.matrixToJson(matrix, wordlist);
		
		//return json;
		return this;
	}*/
}
