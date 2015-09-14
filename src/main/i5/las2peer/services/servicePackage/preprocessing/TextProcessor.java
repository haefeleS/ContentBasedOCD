package i5.las2peer.services.servicePackage.preprocessing;

import org.json.JSONArray;
import org.json.JSONObject;

//import java.util.*;

public class TextProcessor {

	public JSONArray executeTextProc(JSONArray ja) throws Exception{
		JSONArray result = new JSONArray();
		String thread = null;
		String columnName = "content";
		int n = ja.length();
		
		for(int i=0; i< n; i++){
			JSONObject jo = new JSONObject();
			jo = ja.getJSONObject(i);
			thread = jo.getString(columnName);
			thread = deleteNonWords(thread);
			//thread = stemming(thread);
			jo.put(columnName, thread);
			result.put(jo);
		}
		
		return result;
	}
	
	public String preprocText(String thread){
		thread  = deleteNonWords(thread);
		//thread = stemming(thread);
		return thread;
	}
	
	private String deleteNonWords(String thread){
		String result = null;
		
		thread = thread.replaceAll("<[^>]*>", ""); 	// remove html tags
		result = thread.replaceAll("\\p{Punct}","");		// remove Punctuation
		
		return result;
	}
	
	private String stemming(String thread){
		String result = null;
		
		return result;
	}
	/*private List<String> deleteNonWords(List<String> threads){
		List<String> result = new LinkedList<String>();
		Iterator<String> iter = threads.iterator();
		String curr = null;
		
		while(iter.hasNext()){
			curr = iter.next();
			curr = curr.replaceAll("\\<(.|\n)*?>/g",""); 	// remove html tags
			curr = curr.replaceAll("\\p{Punct}","");		// remove Punctuation
		}
		return result;
	} 
	
	private List<String> stemming(List<String> threads){
		List<String> result = null;
		
		return result;
	}
	*/
}
