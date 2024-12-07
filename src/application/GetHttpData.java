package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetHttpData {
	private static String api = "http://49.234.70.238:9001/book/worm/isbn?isbn=";
	private boolean isFound = true;
	private Map BookInfo;
	
	public GetHttpData() {
		
	}
	public GetHttpData(String isbn) {
		BookInfo = dealWithData(getData(isbn));
	}
	
	public boolean getIsFound() {
		return this.isFound;
	}
	
	public Map getBookInfo() {
		return BookInfo;
	}
	
    public String getData(String isbn) {
    	StringBuffer url = new StringBuffer(api);
    	url.append(isbn);
    	StringBuffer HtmlText = new StringBuffer();
    	try {
    		URL Address = new URL(url.toString());
    		HttpURLConnection Conn = (HttpURLConnection)Address.openConnection();
    		Conn.setDoInput(true);
    		Conn.setDoOutput(false);
    		Conn.setRequestMethod("GET");
    		Conn.setInstanceFollowRedirects(true);
    		Conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    		Conn.connect();
    		
    		InputStreamReader ISR = new InputStreamReader(Conn.getInputStream());
    		BufferedReader BR = new BufferedReader(ISR);
    		String line;
    		while((line = BR.readLine()) != null)
    		{
    			line = new String(line.getBytes(), "utf-8");
    			HtmlText.append(line);
    		}
    		BR.close();
    		Conn.disconnect();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return HtmlText.toString();
    }
    public Map dealWithData(String HtmlText) {
    	JSONObject JO,BookJson;
    	JSONArray JA;
    	JO = JSONObject.fromObject(HtmlText);
    	Map<String, Object> Book = new HashMap<String, Object>();
    	if(JO.get("data") == null) {
    		isFound = false;
    		return Book;
    	}
    	try {
    		JA = JO.getJSONArray("data");
    		
    		if(JA.isEmpty()) {
	    		isFound = false;
	    		return Book;
	    	}
        	BookJson = JA.getJSONObject(0);
	    	Book.put("BookName", BookJson.get("name"));
	    	Book.put("Author", BookJson.get("author"));
	    	Book.put("Publisher", BookJson.get("publisher"));
	    	Book.put("Publish Date", BookJson.get("publishingTime"));
	    	Book.put("Price", BookJson.get("price"));
	    	Book.put("ISBN", BookJson.get("isbn"));
	    	if(Book.get("ISBN") == null) {
	    		isFound = false;
	    	}
	    	BookInfo = Book;
    	}catch(Exception e) {
    		e.printStackTrace();
    		isFound = false;
    	}
    	return Book;
    }
}

