package com.example.oauth2.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

@Service
public class GeoCorderService {
	
	private final static String apikey = "ED0E21FD-6D20-314C-BAA6-CFBE8CC6F46E";
	private final static String searchType = "road";
	private final static String epsg = "epsg:4326";
	

	@SuppressWarnings({ "unchecked", "finally" })
	public JSONObject geocheck(String searchAddr) {
		JSONObject result = new JSONObject();
		StringBuilder sb = new StringBuilder("https://api.vworld.kr/req/address");
		
		sb.append("?service=address");
		sb.append("&request=getcoord");
		sb.append("&version=2.0");
		sb.append("&format=json");
		sb.append("&crs=" + epsg);
		sb.append("&key=" + apikey);
		sb.append("&type=" + searchType);
		sb.append("&address=" + URLEncoder.encode(searchAddr, StandardCharsets.UTF_8));
		
		try{
		    URL url = new URL(sb.toString());
		    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

		    JSONParser jspa = new JSONParser();
		    JSONObject jsob = (JSONObject) jspa.parse(reader);
		    JSONObject jsrs = (JSONObject) jsob.get("response");
		    JSONObject jsResult = (JSONObject) jsrs.get("result");
		    
		    if(jsResult==null) {
		    	return (JSONObject) result.put("error", "error");
		    }
		    JSONObject jspoitn = (JSONObject) jsResult.get("point");

		    result.put("x", jspoitn.get("x"));
		    result.put("y", jspoitn.get("y"));
		} catch (IOException | ParseException | NullPointerException e) {
			e.printStackTrace();
			result.put("error", "error");
		} finally {
			return result;
		} 
	}
}

