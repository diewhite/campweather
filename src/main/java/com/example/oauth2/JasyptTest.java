package com.example.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JasyptTest {
	
	@Value("${server.ssl.key-store-password}")
	private String pass;
	
	public String getPass() {
		return pass;
	}
}
