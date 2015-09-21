package i5.las2peer.services.servicePackage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import i5.las2peer.api.Service;
import i5.las2peer.restMapper.HttpResponse;
import i5.las2peer.restMapper.MediaType;
import i5.las2peer.restMapper.RESTMapper;
import i5.las2peer.restMapper.annotations.Version;
import i5.las2peer.restMapper.tools.ValidationResult;
import i5.las2peer.restMapper.tools.XMLCheck;
import i5.las2peer.security.Context;
import i5.las2peer.security.UserAgent;
import i5.las2peer.services.servicePackage.database.DatabaseManager;
import i5.las2peer.services.servicePackage.entities.EntityManagement;
import i5.las2peer.services.servicePackage.entities.Graph;
import i5.las2peer.services.servicePackage.entities.LinkedNode;
import i5.las2peer.services.servicePackage.entities.Node;
import i5.las2peer.services.servicePackage.ocd.Termmatrix;
import i5.las2peer.services.servicePackage.preprocessing.TextProcessor;
import i5.las2peer.services.servicePackage.preprocessing.WordConverter;
import i5.las2peer.services.servicePackage.util.ToJSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
//import net.minidev.json.JSONObject;

/**
 * LAS2peer Service
 * 
 * This is a template for a very basic LAS2peer service
 * that uses the LAS2peer Web-Connector for RESTful access to it.
 * 
 * Note:
 * If you plan on using Swagger you should adapt the information below
 * in the ApiInfo annotation to suit your project.
 * If you do not intend to provide a Swagger documentation of your service API,
 * the entire ApiInfo annotation should be removed.
 * 
 */
@Path("/example")
@Version("0.1") // this annotation is used by the XML mapper
@Api
@SwaggerDefinition(
		info = @Info(
				title = "LAS2peer Template Service",
				version = "0.1",
				description = "A LAS2peer Template Service for demonstration purposes.",
				termsOfService = "http://your-terms-of-service-url.com",
				contact = @Contact(
						name = "John Doe",
						url = "provider.com",
						email = "john.doe@provider.com"
				),
				license = @License(
						name = "your software license name",
						url = "http://your-software-license-url.com"
				)
		))
public class TemplateService extends Service {

	/*
	 * Database configuration
	 */
	private String jdbcDriverClassName ;
	private String jdbcLogin ;
	private String jdbcPass ;
	private String jdbcUrl ;
	private String jdbcSchema;
	private DatabaseManager dbm;

	public TemplateService() {
		// read and set properties values
		// IF THE SERVICE CLASS NAME IS CHANGED, THE PROPERTIES FILE NAME NEED TO BE CHANGED TOO!
		setFieldValues();
		// instantiate a database manager to handle database connection pooling and credentials
		dbm = new DatabaseManager(jdbcDriverClassName, jdbcLogin, jdbcPass, jdbcUrl, jdbcSchema);
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// Service methods.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Simple function to validate a user login.
	 * Basically it only serves as a "calling point" and does not really validate a user
	 * (since this is done previously by LAS2peer itself, the user does not reach this method
	 * if he or she is not authenticated).
	 * 
	 */
	@GET
	@Path("/validation")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value = "User Validation",
			notes = "Simple function to validate a user login.")
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Validation Confirmation"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	public HttpResponse validateLogin() {
		String returnString = "";
		returnString += "You are " + ((UserAgent) getActiveAgent()).getLoginName() + " and your login is valid!";

		return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
	}

	/**
	 * Example method that returns a phrase containing the received input.
	 * 
	 * @param myInput
	 * 
	 */
	@POST
	@Path("/myResourcePath/{input}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Input Phrase"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
	})
	@ApiOperation(value = "Sample Resource",
			notes = "Example method that returns a phrase containing the received input.")
	public HttpResponse exampleMethod(@PathParam("input") String myInput) {
		String returnString = "";
		returnString += "You have entered " + myInput + "!";

		return new HttpResponse(returnString, HttpURLConnection.HTTP_OK);
	}

	/**
	 * Example method that shows how to retrieve a user email address from a database 
	 * and return an HTTP response including a JSON object.
	 * 
	 * WARNING: THIS METHOD IS ONLY FOR DEMONSTRATIONAL PURPOSES!!! 
	 * IT WILL REQUIRE RESPECTIVE DATABASE TABLES IN THE BACKEND, WHICH DON'T EXIST IN THE TEMPLATE.
	 * 
	 */
	@GET
	@Path("/userEmail/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "User Email"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
			@ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "User not found"),
			@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal Server Error")
	})
	@ApiOperation(value = "Email Address Administration",
			notes = "Example method that retrieves a user email address from a database."
					+ " WARNING: THIS METHOD IS ONLY FOR DEMONSTRATIONAL PURPOSES!!! "
					+ "IT WILL REQUIRE RESPECTIVE DATABASE TABLES IN THE BACKEND, WHICH DON'T EXIST IN THE TEMPLATE.")
	public HttpResponse getUserEmail(@PathParam("username") String username) {
		String result = "";
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		try {
			
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT email FROM users WHERE username = ?;");
			stmnt.setString(1, username);

			// retrieve result set
			rs = stmnt.executeQuery();

			// process result set
			if (rs.next()) {
				result = rs.getString(1);

				// setup resulting JSON Object
				JSONObject ro = new JSONObject();
				ro.put("email", result);

				// return HTTP Response on success
				return new HttpResponse(ro.toString(), HttpURLConnection.HTTP_OK);
			} else {
				result = "No result for username " + username;

				// return HTTP Response on error
				return new HttpResponse(result, HttpURLConnection.HTTP_NOT_FOUND);
			}
		} catch (Exception e) {
			// return HTTP Response on error
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		} finally {
			// free resources
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
	}

	/**
	 * Example method that shows how to change a user email address in a database.
	 * 
	 * WARNING: THIS METHOD IS ONLY FOR DEMONSTRATIONAL PURPOSES!!! 
	 * IT WILL REQUIRE RESPECTIVE DATABASE TABLES IN THE BACKEND, WHICH DON'T EXIST IN THE TEMPLATE.
	 * 
	 */
	@POST
	@Path("/userEmail/{username}/{email}")
	@Produces(MediaType.TEXT_PLAIN)
	@ApiResponses(value = {
			@ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Update Confirmation"),
			@ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized"),
			@ApiResponse(code = HttpURLConnection.HTTP_INTERNAL_ERROR, message = "Internal Server Error")
	})
	@ApiOperation(value = "setUserEmail",
			notes = "Example method that changes a user email address in a database."
					+ " WARNING: THIS METHOD IS ONLY FOR DEMONSTRATIONAL PURPOSES!!! "
					+ "IT WILL REQUIRE RESPECTIVE DATABASE TABLES IN THE BACKEND, WHICH DON'T EXIST IN THE TEMPLATE.")
	public HttpResponse setUserEmail(@PathParam("username") String username, @PathParam("email") String email) {

		String result = "";
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		try {
			conn = dbm.getConnection();
			stmnt = conn.prepareStatement("UPDATE users SET email = ? WHERE username = ?;");
			stmnt.setString(1, email);
			stmnt.setString(2, username);
			int rows = stmnt.executeUpdate(); // same works for insert
			result = "Database updated. " + rows + " rows affected";

			// return
			return new HttpResponse(result, HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			// return HTTP Response on error
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		} finally {
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
	}
	
	@GET
	@Path("/persist/graph/{dataset}")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getNewGraph(@PathParam("dataset") String dataset) {
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		ToJSON converter = new ToJSON();
		JSONObject json = new JSONObject();
		EntityManagement em = new EntityManagement();
		String query = null;
		Graph graph = new Graph();
		
		try{
			// get connection from connection pool
			conn = dbm.getConnection();
			
			switch(dataset){
			case "urch": query = "SELECT id,content,author FROM urch order by author";
						break;
			case "stdoctor": query = "SELECT id, content author FROM stdoctor order by author";
						break;
			}

			// prepare statement
			stmnt = conn.prepareStatement(query);
			//stmnt.setString(1, dataSet);

			// retrieve result set
			rs = stmnt.executeQuery();
			
			//create necessary tables
			em.createNodeTable(conn);
			em.createGraphTable(conn);
			
			//persist graph built form dataset
			graph = em.persistNewGraph(rs, conn);
			
			//for print information about persisted graph
			json = converter.graphToJson(graph);
			
			return new HttpResponse("Graph with "+ json.toString() + " saved in database" , HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
	}
	@GET
	@Path("/graphs/termmatrix")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getTermMatrix() {
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		JSONArray json = new JSONArray();
		EntityManagement em = new EntityManagement();
		WordConverter wordConv = new WordConverter();
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix();
		ToJSON converter = new ToJSON();
						
		try{
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT id,content,author FROM urch order by author;");
			
			// retrieve result set
			rs = stmnt.executeQuery();
	
			//LinkedList<Node> nodes = em.listNodes(rs);	// listing nodes and textpreprocessing of content
			em.createNodeTable(conn);
			em.createGraphTable(conn);
			
			//persist graph built form dataset
			Graph graph = em.persistNewGraph(rs, conn);
			
			Termmatrix termMat = wordConv.convertTFIDF(graph.getNodes());
			//matrix = wordConv.convertTFIDF(nodes);		// creating term matrix, computing tf- idf, converting to json array for visualization	
			
			json = converter.termmatrixToJson(termMat);
			//json = converter.termmatrixToJson(matrix);
			return new HttpResponse(json.toString() , HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
	}
	
	@GET
	@Path("/persist/linkednodes/biojava")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getNewLinkedNodes() {
		Connection conn = null;
		PreparedStatement stmnt = null;
		Statement stmnt1 = null;
		ResultSet rs = null;
		EntityManagement em = new EntityManagement();
				
		try{
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT sender,receiver,content FROM biojava;");

			// retrieve result set
			rs = stmnt.executeQuery();

			stmnt1 = conn.createStatement();
			stmnt1.executeUpdate("create table linkednode ("+ "nodeid int not null primary key," +"sender varchar(255),"+ "receiver varchar(255),"+" content varchar(5000))");
			
			em.persistNewLinkedNode(rs, conn);
									
			return new HttpResponse("Data saved in database" , HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
	}
	
	@GET
	@Path("/linkedGraphs/graphweight")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getGraphWeight() {
		Connection conn = null;
		PreparedStatement stmnt = null;
		PreparedStatement stmnt1 = null;
		ResultSet senders = null;
		ResultSet receivers = null;
		ToJSON converter = new ToJSON();
		TextProcessor tp = new TextProcessor();
		JSONArray json = new JSONArray();
		EntityManagement em = new EntityManagement();
		WordConverter wordConv = new WordConverter();
		Array2DRowRealMatrix matrix = new Array2DRowRealMatrix();
		
		try{
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT sender,receiver FROM biojava group by sender,receiver;");
			senders = stmnt.executeQuery();
			
			while(senders.next()){
				JSONObject obj = wordConv.generateWeights(senders.getInt("sender"),senders.getInt("receiver") , conn);
				json.put(obj);
			}	
			
			//LinkedList<LinkedNode> nodes = em.listLinkedNodes(rs);	// listing nodes and textpreprocessing of content
			//json = wordConv.getWeightedGraph();		// creating term matrix, computing tf- idf, converting to json array for visualization
									
			return new HttpResponse(json.toString(), HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (senders != null) {
				try {
					senders.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (receivers != null) {
				try {
					receivers.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
		
		
		
	}
	
	@GET
	@Path("/users/{userID}")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getUserContent(@PathParam("userID") String userID) {
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		ToJSON converter = new ToJSON();
		JSONArray json = new JSONArray();
				
		try{
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT id,content,author FROM urch where author = ?;");
			stmnt.setString(1, userID);

			// retrieve result set
			rs = stmnt.executeQuery();
			
			json = converter.toJSONArray(rs);
							
			return new HttpResponse(json.toString(), HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
		
		
		
	}
	
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getUsers() {
		Connection conn = null;
		PreparedStatement stmnt = null;
		ResultSet rs = null;
		ToJSON converter = new ToJSON();
		JSONArray json = new JSONArray();
				
		try{
			// get connection from connection pool
			conn = dbm.getConnection();

			// prepare statement
			stmnt = conn.prepareStatement("SELECT author FROM urch group by author;");
			
			// retrieve result set
			rs = stmnt.executeQuery();
			
			json = converter.toJSONArray(rs);
							
			return new HttpResponse(json.toString(), HttpURLConnection.HTTP_OK);
			
		}catch(Exception e){
			
			return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		
		}finally{
			// free resources if exception or not
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (stmnt != null) {
				try {
					stmnt.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					Context.logError(this, e.getMessage());

					// return HTTP Response on error
					return new HttpResponse("Internal error: " + e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
			}
		}
		
		
		
	}
	

	// //////////////////////////////////////////////////////////////////////////////////////
	// Methods required by the LAS2peer framework.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Method for debugging purposes.
	 * Here the concept of restMapping validation is shown.
	 * It is important to check, if all annotations are correct and consistent.
	 * Otherwise the service will not be accessible by the WebConnector.
	 * Best to do it in the unit tests.
	 * To avoid being overlooked/ignored the method is implemented here and not in the test section.
	 * @return  true, if mapping correct
	 */
	public boolean debugMapping() {
		String XML_LOCATION = "./restMapping.xml";
		String xml = getRESTMapping();

		try {
			RESTMapper.writeFile(XML_LOCATION, xml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		XMLCheck validator = new XMLCheck();
		ValidationResult result = validator.validate(xml);

		if (result.isValid()) {
			return true;
		}
		return false;
	}

	/**
	 * This method is needed for every RESTful application in LAS2peer. There is no need to change!
	 * 
	 * @return the mapping
	 */
	public String getRESTMapping() {
		String result = "";
		try {
			result = RESTMapper.getMethodsAsXML(this.getClass());
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// Methods providing a Swagger documentation of the service API.
	// //////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the API documentation of all annotated resources
	 * for purposes of Swagger documentation.
	 * 
	 * Note:
	 * If you do not intend to use Swagger for the documentation
	 * of your service API, this method may be removed.
	 * 
	 * @return The resource's documentation.
	 */
	@GET
	@Path("/swagger.json")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse getSwaggerJSON() {
		Swagger swagger = new Reader(new Swagger()).read(this.getClass());
		if (swagger == null) {
			return new HttpResponse("Swagger API declaration not available!", HttpURLConnection.HTTP_NOT_FOUND);
		}
		try {
			return new HttpResponse(Json.mapper().writeValueAsString(swagger), HttpURLConnection.HTTP_OK);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new HttpResponse(e.getMessage(), HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}

}
