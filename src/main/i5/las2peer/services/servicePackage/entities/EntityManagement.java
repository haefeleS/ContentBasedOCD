package i5.las2peer.services.servicePackage.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/*import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;*/

import i5.las2peer.services.servicePackage.preprocessing.TextProcessor;

public class EntityManagement {
	
	public void persistNewNode(ResultSet rs, Connection conn) throws Exception{
		
		try{
		    ResultSetMetaData rsmd = rs.getMetaData();
		    String dataset = rsmd.getTableName(1);
		    List<Node> temp = new LinkedList<Node>();
		    PreparedStatement stmnt = null;
		    int i = 0;
		    
		    
		    // read the existing entries
		    //PreparedStatement stmnt = conn.prepareStatement("select graphid from graph where origin = ? ;");
		    //stmnt.setString(1, dataset);
		    //Query q = em.createQuery("select graphid from graph where origin = ? ");
		    //q.setParameter(1, dataset);
		    
		    //boolean createEntry = (q.getResultList().size() == 0);
		    
		    //if(createEntry){
		    	//Graph graph = new Graph();
		    	//graph.setOrigin(dataset);
		    	
		    while(rs.next()){
		    		Node node = new Node();
		    		node.setUserID(rs.getString("author"));
		    		node.setContent(rs.getString("content"));
		    		
		    		String temp1 = node.getUserID();
		    		String temp2 = node.getContent();
		    		
		    		stmnt = conn.prepareStatement("insert into node (nodeid,userid, content) values(?, ?, ?)");
		    		stmnt.setInt(1, i);
		    		stmnt.setString(2,temp1);
		    		stmnt.setString(3, temp2);
		    		stmnt.executeUpdate();
		    		
		    		
		    		i++;
		    		//temp.add(node);
		    		
		    	}
		    	stmnt.close();
		    	//graph.setNodes(temp);
		    	//em.persist(graph);
		    //}
		    //em.getTransaction().commit();
	    
	    
	    }catch(Exception e){
	    	/*if( et != null && et.isActive() ) {
				et.rollback();
			}*/
	    	
	    	
	    }
	    
	    //em.close();
	    //factory.close();
	     
	}
	
	public void persistNewLinkedNode(ResultSet rs, Connection conn) throws Exception{
		
		try{
		    PreparedStatement stmnt = null;
		    int i = 0;
		      	
		    while(rs.next()){
		    		LinkedNode node = new LinkedNode();
		    		node.setSender(rs.getInt("sender"));
		    		node.setReceiver(rs.getInt("receiver"));
		    		node.setContent(rs.getString("content"));
		    		
		    		int temp1 = node.getSender();
		    		int temp2 = node.getReceiver();
		    		String temp3 = node.getContent();
		    		
		    		stmnt = conn.prepareStatement("insert into linkednode (nodeid,sender,receiver, content) values(?, ?, ?,?)");
		    		stmnt.setInt(1, i);
		    		stmnt.setInt(2,temp1);
		    		stmnt.setInt(3, temp2);
		    		stmnt.setString(4, temp3);
		    		stmnt.executeUpdate();
		    		
		    		
		    		i++;
		    		
		    }
		    
		    stmnt.close();
		
		}catch(Exception e){
	    	System.out.println("problem at persisting linked nodes");
	    	
	    	
	    }     
	}
	
	public LinkedList<Node> listNodes(ResultSet rs) throws Exception{
		LinkedList<Node> res = new LinkedList<Node>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnNumb = 0;
		TextProcessor tp = new TextProcessor();
		
		while(rs.next()){
			Node node = new Node();
			columnNumb = rsmd.getColumnCount();
				
			for(int i=1; i<columnNumb+1; i++){
				String columnName = rsmd.getColumnName(i);
							
				if(columnName.equals("author")){
					node.setUserID(rs.getString(columnName));
				}
				if(columnName.equals("content")){
					//String content = rs.getString(columnName);
					//content = tp.preprocText(content);
					node.setContent(rs.getString(columnName));
				}
			}
			
			res.add(node);
		}
		
		return res;
	}
	
	public LinkedList<LinkedNode> listLinkedNodes(ResultSet rs) throws Exception{
		LinkedList<LinkedNode> res = new LinkedList<LinkedNode>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnNumb = 0;
		TextProcessor tp = new TextProcessor();
		
		while(rs.next()){
			LinkedNode node = new LinkedNode();
			columnNumb = rsmd.getColumnCount();
				
			for(int i=1; i<columnNumb+1; i++){
				String columnName = rsmd.getColumnName(i);
							
				if(columnName.equals("sender")){
					node.setSender(rs.getInt(columnName));
				}
				if(columnName.equals("receiver")){
					node.setReceiver(rs.getInt(columnName));
				}
				if(columnName.equals("content")){
					String content = rs.getString(columnName);
					//content = tp.preprocText(content);
					node.setContent(content);
				}
			}
			
			res.add(node);
		}
		
		return res;
	}
}
