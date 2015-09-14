package i5.las2peer.services.servicePackage.preprocessing;

import i5.las2peer.services.servicePackage.entities.EntityManagement;
import i5.las2peer.services.servicePackage.entities.LinkedNode;
import i5.las2peer.services.servicePackage.entities.Node;
import i5.las2peer.services.servicePackage.ocd.Similarities;
import i5.las2peer.services.servicePackage.util.ToJSON;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.json.JSONArray;
import org.json.JSONObject;

public class WordConverter {
	
	public JSONArray convertTFIDF(LinkedList<Node> nodes) throws Exception{
		LinkedList<String> wordlist = new LinkedList<String>();
		LinkedList<String> wordlistdup = new LinkedList<String>();
		JSONArray json = new JSONArray();
		ToJSON converter = new ToJSON();
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
			
			for(Iterator<Node> it2 = tempList.iterator(); it2.hasNext();){ //group by users
				Node currTemp = it2.next();
				if(currTemp.getUserID().equals(currNode.getUserID())){
					currTemp.setContent(currTemp.getContent()+currNode.getContent());
					add = false;
				}
				
			}
			if(add){
				tempList.add(currNode);
			}			
		}
		
		int nodesSize = tempList.size();
		
		if(threads == null || threads.isEmpty()){
			return null;
		}
		
		wordlist = listWords(threads);
		wordlistdup = listWordsDup(threads);
		//len = stringLength(thread);
		
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(nodesSize ,wordlist.size());
		
		for(Iterator<Node>iter = tempList.iterator(); iter.hasNext();){
			String thr = iter.next().getContent();
			LinkedList<String> temp = listWordsDup(thr);
			RealVector vector = new ArrayRealVector(wordlist.size());
			
			for(Iterator<String> it = wordlist.iterator(); it.hasNext();){
				word = it.next();
				int freq = countWord(word, temp);
				int docFreq = countWord(word, wordlistdup);
				double tfidf = freq * log.value((double)nodesSize/docFreq);
				
				vector.setEntry(column, tfidf);
				column++;
			}
			column = 0;
			matrix.setRowVector(row, vector);
			row++;
		}
		json = converter.matrixToJson(matrix, wordlist);
		
		return json;
	}
	
	/*private int stringLength(String thread){
		int count = 0;
		
		if(thread == null || thread.isEmpty()){
			return 0;
		}
		
		for(int i = 0; i < thread.length(); i++){
			if(thread.charAt(i) != ' '){
				count++;
				while(thread.charAt(i) != ' ' && i < thread.length() - 1){
					i++;
				}
			}
		}
		return count;
	}*/
	
	private int countWord(String word, LinkedList<String> list){
		int res = 0;
		for(Iterator<String> it = list.iterator(); it.hasNext();){
			if(word.equals(it.next())){
				res++;
			}
		}
		
		return res;
	}
	
	private LinkedList<String> listWords(String thread){
		LinkedList<String> res = new LinkedList<String>();
		int begin = 0;
		int end = 0;
		String temp = null;
		boolean add;
		int len = thread.length();
		
		if(thread == null || thread.isEmpty()){
			return null;
		}
		
		for(int i = 0; i < len; i++){
			if(thread.charAt(i) != ' '){
				begin = i;
				end = i;
				
				while(i < len && thread.charAt(i) != ' '){
						end++;
						i++;
				}
				add = true;

				temp = thread.substring(begin, end);
				
				for(Iterator<String> it = res.iterator(); it.hasNext();){
					if(temp.equals(it.next())){
						add = false;
					}
				}
				if(add){
					res.add(temp);
				}
			}
		}
		
		return res;
	}
	
	private LinkedList<String> listWordsDup(String thread){
		LinkedList<String> res = new LinkedList<String>();
		int begin = 0;
		int end = 0;
		String temp = null;
		int len = thread.length();
				
		if(thread == null || thread.isEmpty()){
			return null;
		}
		
		for(int i = 0; i < len; i++){
			if(thread.charAt(i) != ' '){
				begin = i;
				end = i;
				while(i < len && thread.charAt(i) != ' '){
					end++;
					i++;
				}
				
				temp = thread.substring(begin, end);
				
				res.add(temp);
				
			}
		}
		
		return res;
	}
	
	public JSONObject generateWeights(int sender,int receiver, Connection conn) throws Exception{
		JSONObject json = new JSONObject();
		PreparedStatement stmnt = null;
		PreparedStatement stm = null;
		ResultSet rs;
		ResultSet nodeSet;
		ArrayRealVector vectorS = new ArrayRealVector();
		ArrayRealVector vectorR = new ArrayRealVector();
		LinkedList<LinkedNode> nodes = new LinkedList<LinkedNode>();
		EntityManagement em = new EntityManagement();
		Similarities sim = new Similarities();
		int deg = 0;
		int together = 0;
		double r = 0;
		double s = 0;
		
		try{
		stmnt = conn.prepareStatement("select receiver,content from linkednode where sender = ?");
		stmnt.setInt(1, sender);
		rs = stmnt.executeQuery();
			
		while(rs.next()){
			deg++;
			if(rs.getInt("receiver") == receiver){
				together++;
			}
		}
		
		if(deg != 0){
		r = together / deg;
		}
		
		stm = conn.prepareStatement("select sender,receiver,content from linkednode;");
		nodeSet = stm.executeQuery();
		nodes = em.listLinkedNodes(nodeSet);
		PreparedStatement stm1 = conn.prepareStatement("select sender,receiver,content from linkednode;");
		ResultSet nodeSetDup = stm1.executeQuery();
		LinkedList<LinkedNode> nodesDup = em.listLinkedNodes(nodeSetDup);
		
		vectorS = convertTFIDFLinked(nodes, sender);
		vectorR = convertTFIDFLinked(nodesDup, receiver);
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(2, vectorS.getDimension());
		matrix.setRowVector(0,vectorS);
		matrix.setRowVector(1,vectorR);
		
		s = sim.cosineSim(matrix);
		stmnt.close();
		json.put("sender", sender);
		json.put("receiver", receiver);
		json.put("r", r);
		json.put("s", s);
		
		return json;
		
		}catch (Exception e){
			if(stmnt != null){
				stmnt.close();
			}
			System.out.println("problem when generating weights");
			return null;
		}
	}
	
	public ArrayRealVector convertTFIDFLinked(LinkedList<LinkedNode> nodes, int sender) throws Exception{
		LinkedList<String> wordlist = new LinkedList<String>();
		LinkedList<String> wordlistdup = new LinkedList<String>();
		//JSONArray json = new JSONArray();
		String threads = "";
		String word = null;
		int column = 0;
		//int row = 0;
		Log log = new Log();
		LinkedList<LinkedNode> tempList = new LinkedList<LinkedNode>();
		
		for(Iterator<LinkedNode> it = nodes.iterator(); it.hasNext();){ 
		threads = it.next().getContent()+ " " + threads;      // compute all concatenated threads
		}
		
		if(threads == null || threads.isEmpty()){
			return null;
		}
		
		wordlist = listWords(threads);
		wordlistdup = listWordsDup(threads);
		
		for(Iterator<LinkedNode> it = nodes.iterator(); it.hasNext();){ 
			boolean add = true;
			LinkedNode currNode = it.next();
			
			for(Iterator<LinkedNode> it2 = tempList.iterator(); it2.hasNext();){ //group by users
				LinkedNode currTemp = it2.next();
				if(currTemp.getSender() == currNode.getSender()){
					currTemp.setContent(currTemp.getContent()+currNode.getContent());
					add = false;
				}
				
			}
			if(add){
				tempList.add(currNode);
			}			
		}
		//nodes = tempList;
		int nodesSize = tempList.size();
		
		
				
		//Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(2 ,wordlist.size());
		ArrayRealVector vector = new ArrayRealVector(wordlist.size());
		
		for(Iterator<LinkedNode>iter = tempList.iterator(); iter.hasNext();){
			LinkedNode node = iter.next();
			if(node.getSender() == sender){
				String thr = node.getContent();
				LinkedList<String> temp = listWordsDup(thr);
				
				
				for(Iterator<String> it = wordlist.iterator(); it.hasNext();){
					word = it.next();
					int freq = countWord(word, temp);
					int docFreq = countWord(word, wordlistdup);
					double tfidf = freq * log.value((double)nodesSize/docFreq);
					
					vector.setEntry(column, tfidf);
					column++;
				}
			}
		}
				
		return vector;
	}
	
}	