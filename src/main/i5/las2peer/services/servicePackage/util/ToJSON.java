package i5.las2peer.services.servicePackage.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.math3.linear.RealMatrix;
import org.json.JSONArray;
import org.json.JSONObject;

public class ToJSON{

	public JSONArray toJSONArray(ResultSet rs) throws Exception{
		JSONArray result = new JSONArray();
				
		try{
			// get meta-data, because we need information like the column names
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnNumb = rsmd.getColumnCount();
			
			
			while(rs.next()){
				
				JSONObject obj = new JSONObject();
				
				for(int i=1; i<columnNumb+1; i++){
					
					String columnName = rsmd.getColumnName(i);
				
					switch(rsmd.getColumnType(i)){
					case java.sql.Types.INTEGER : obj.put(columnName,rs.getInt(columnName));
						break;
					case java.sql.Types.VARCHAR : obj.put(columnName,rs.getString(columnName));
						break;						
					case java.sql.Types.DATE : obj.put(columnName,rs.getDate(columnName));
						break;
					case java.sql.Types.TIMESTAMP : obj.put(columnName,rs.getTimestamp(columnName));
						break;
					case java.sql.Types.BOOLEAN : obj.put(columnName,rs.getBoolean(columnName));
						break;
					case java.sql.Types.ARRAY : obj.put(columnName,rs.getArray(columnName));
						break;
					case java.sql.Types.BIGINT : obj.put(columnName,rs.getInt(columnName));
						break;
					case java.sql.Types.BLOB : obj.put(columnName,rs.getBlob(columnName));
						break;
					case java.sql.Types.DOUBLE : obj.put(columnName,rs.getDouble(columnName));
						break;
					case java.sql.Types.FLOAT : obj.put(columnName,rs.getFloat(columnName));
						break;
					case java.sql.Types.NVARCHAR : obj.put(columnName,rs.getString(columnName));
						break;
					case java.sql.Types.TINYINT : obj.put(columnName,rs.getInt(columnName));
						break;
					case java.sql.Types.SMALLINT : obj.put(columnName,rs.getInt(columnName));
						break;
					default : obj.put(columnName,rs.getObject(columnName));
					}
				}
				
				result.put(obj);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}			
			return result;
		}
	
	public JSONArray matrixToJson(RealMatrix matrix, LinkedList<String> wordlist) throws Exception{
		JSONArray result = new JSONArray();
		int rows = matrix.getRowDimension();
		int columns = matrix.getColumnDimension();
		

		for(int i = 0; i < rows; i++){
			JSONObject obj = new JSONObject();
			Iterator<String> it = wordlist.iterator();
			
			for(int j = 0; j < columns; j++){
				obj.put(it.next(),matrix.getEntry(i,j));
			}
			result.put(obj);
		}
		
		return result;
	}
			
}		

