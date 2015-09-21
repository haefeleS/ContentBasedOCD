package i5.las2peer.services.servicePackage.entities;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedList;

import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.security.Context;

/*import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;*/

import i5.las2peer.services.servicePackage.preprocessing.TextProcessor;

public class EntityManagement {
	
	public void createNodeTable(Connection conn) throws Exception{
		Statement stmnt = null;
		stmnt = conn.createStatement();
		stmnt.executeUpdate("create table if not exists node ("+ "nodeid int not null auto_increment primary key," +"userid varchar(255),"+ " content text," + "graphid int)");
		
	}
	
	public void createGraphTable(Connection conn) throws Exception{
		Statement stmnt = null;
		stmnt = conn.createStatement();
		stmnt.executeUpdate("create table if not exists graph ("+ "graphid int not null auto_increment primary key," +"origin varchar(255))");
		
	}
	
	public Graph persistNewGraph(ResultSet rs, Connection conn) throws Exception{
		
		PreparedStatement stmnt = null;
	    PreparedStatement stmnt1 = null;
		
		try{
		    ResultSetMetaData rsmd = rs.getMetaData();
		    String dataset = rsmd.getTableName(1);
		    Graph graph = new Graph();
		    String author = null;
		    String content = "";
		    String tempString = "";
		    LinkedList<Node> nodes = new LinkedList<Node>();
		    
		    
		    int graphid = 0;
		    int nodeid = 0;
		    
		    stmnt1 = conn.prepareStatement("insert into graph (origin) values(?)", Statement.RETURN_GENERATED_KEYS);
    		stmnt1.setString(1, dataset);
    		stmnt1.executeUpdate();
    		ResultSet res = stmnt1.getGeneratedKeys();
    		if(res.next()){
    			graphid = res.getInt(1);
    		}
    		stmnt1.close();
    		graph.setGraphID(graphid);
    		graph.setOrigin(dataset);
    		graph.setNodes(nodes);
    		
    		while(rs.next()){
		    	author = rs.getString("author");
		    	content = rs.getString("content");
		    	if(!rs.next()){
		    		break;
		    	}
		    	
		    	
		    	while(rs.getString("author").equals(author)){
		    		
		    		content = content + rs.getString("content");
		    		if(!rs.next()){
		    			break;
		    		}

		    		/*if(!rs.getString("author").equals(author)&& rs.isLast()){
			    		rs.previous();
			    		break;
		    		}*/
		    	}
			    rs.previous(); 
		    
		    	Node node = new Node();
    			node.setUserID(author);
	    		node.setContent(content);
	    		
	    		String temp1 = node.getUserID();
	    		String temp2 = node.getContent();
	    				    		
	    		stmnt = conn.prepareStatement("insert into node (userid, content, graphid) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
	    		stmnt.setString(1,temp1);
	    		stmnt.setString(2, temp2);
	    		stmnt.setInt(3, graphid);
	    		stmnt.executeUpdate();
	    		ResultSet res1 = stmnt.getGeneratedKeys();
	    		if(res1.next()){
	    			nodeid = res1.getInt(1);
	    		}
	    		 
	    		node.setNodeID(nodeid);
	    		
	    		graph.addNodes(node);
		    }
	    		stmnt.close();
	    
		return graph;
	    
	    }catch(Exception e){
	    	if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception ex) {
					Context.logError(this, ex.getMessage());
				}
			}	    	
	    	return null;
	    }
	    
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
