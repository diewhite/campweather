package com.example.oauth2.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

@Service
public class WeatherAPI {
	private static final String ServiceKey = "CWFCMS77IDM2r1072aB5GcLl7npVib71qicv4I62Ls%2BrSVYGz%2FfJfTYX6zQuGr6p5Lch%2BUgTyHaUkASBylEfHw%3D%3D";
	private final int START_TIME = 02;	
	@SuppressWarnings("unchecked")
	public JSONObject getVilageFcst(String gridX,String gridY) {
		String BaseDate = calDate();
		String BaseTime = calTime();
		JSONObject result = new JSONObject();
		StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
		URL url;
		HttpURLConnection conn = null;
        BufferedReader rd = null;
        StringBuilder sb = new StringBuilder();
        String line;
        JSONParser parser = new JSONParser();
        
        try {
        	urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + ServiceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(BaseDate, "UTF-8")); /*‘21년 6월 28일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(BaseTime, "UTF-8")); /*06시 발표(정시단위) */
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(gridX, "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(gridY, "UTF-8")); /*예보지점의 Y 좌표값*/
            
            url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            
            result = (JSONObject) parser.parse(sb.toString());
        } catch (Exception e){
        	result.put("error", "error");
        	e.printStackTrace();
        } finally {
        	try {
				rd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
            conn.disconnect();
		}
        return result;
    }
	
	private String calDate() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		String date = LocalDate.now().format(dateFormat);
		if(LocalTime.now().getHour()<3) {
			date = LocalDate.now().minusDays(1).format(dateFormat);
		}
		return date;
	}
	
	private String calTime() {
		int time = START_TIME;
		String result ="";
		while(true) {
			if(LocalTime.now().getHour()>time) {
				time = time + 3;
			} else {
				if(time<3){
					time = time - 3 + 24;					
				} else {
					time = time - 3;
				}
				break;
			}
		}
		System.out.println("time : "+time);
		System.out.println("LocalTime : "+LocalTime.now().getHour());
		if(time<10) {
			result = "0"+String.valueOf(time)+"00";
		} else {
			result = String.valueOf(time)+"00";
		}
		return result;
	}
}