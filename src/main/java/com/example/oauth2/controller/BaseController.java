package com.example.oauth2.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.oauth2.domain.Member;
import com.example.oauth2.service.GeoCorderService;
import com.example.oauth2.service.TransferXY;
import com.example.oauth2.service.WeatherAPI;

import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class BaseController {
	
	private static final String authorizationRequestBaseUri = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    @Autowired
    private final ClientRegistrationRepository clientRegistrationRepository;
    private GeoCorderService geoCoderService;
    private TransferXY transferXY;
    private WeatherAPI weatherAPI;

	@GetMapping(value = "/")
	public String index(Model model, HttpSession session) {
		Member user = (Member) session.getAttribute("user");
		if(user==null) {
			return "index";
		} else {
			model.addAttribute("id",user.getEmail());
			return "service";
		}
	}

    @SuppressWarnings("unchecked")
    @GetMapping("/login")
    public String getLoginPage(Model model) throws Exception {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }
        assert clientRegistrations != null;
        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);
 
        return "login";
    }
    
    @GetMapping(value = "/login/{oauth2}")
    public String loginGoogle(@PathVariable String oauth2, HttpServletResponse httpServletResponse) {
    	return "redirect:/oauth2/authorization/" + oauth2;
    }
    
    @RequestMapping(value = "/accessDenied")
    public String accessDenied() {
    	return "accessDenied";
    }
    
    @SuppressWarnings("unchecked")
	@ResponseBody
    @GetMapping(value = "/getWeather")
    public JSONObject windforce(String searchAddr) {
    	JSONObject result = new JSONObject();
    	System.out.println("요청주소 : "+searchAddr);
    	System.out.println();
    	
    	//주소를 위.경도로 변환
		JSONObject value = new JSONObject(geoCoderService.geocheck(searchAddr));
		System.out.println("value : "+value);
		 
		if(value.containsKey("error")) {
			return (JSONObject) result.put("error", "error");
		}
		double lat = Double.parseDouble((String) value.get("y"));
    	double lng = Double.parseDouble((String) value.get("x"));
    	System.out.println("위도 : "+lat);
    	System.out.println("경도 : "+lng);
    	System.out.println();
    	
    	//위.경도를 기상청 X,Y 좌표로 변환
    	JSONObject XY = new JSONObject(transferXY.ConvertXY(lat,lng));
    	String gridX = String.valueOf(XY.get("x"));
    	String gridY = String.valueOf(XY.get("y"));
    	System.out.println("latX : "+gridX);
    	System.out.println("lngY : "+gridY);
    	    	
    	//X,Y 좌표로 단기 날씨 API 호출
    	result = new JSONObject(weatherAPI.getVilageFcst(gridX, gridY));
    	return result;
    }
}
