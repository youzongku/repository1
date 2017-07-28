package service.discart.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.libs.Json;
import service.discart.IUserService;
import session.ISessionService;

public class UserService implements IUserService {
	
	private static final String USER_KEY = "user";
	
	@Inject
	private ISessionService sessionService;

	@Override
	public String getDisAccount() {
		String email = null;
		if(sessionService.get(USER_KEY) != null){
			try {
				JsonNode login = Json.parse(sessionService.get(USER_KEY).toString());
				email = login.get("email").asText();
			} catch (Exception e) {
			}
		}
		return email;
	}

	@Override
	public String  getDismember(){
		String dis = null;
		if(sessionService.get(USER_KEY) != null){
			try {
				dis = sessionService.get(USER_KEY).toString();
			} catch (Exception e) {
			}
		}
		return dis;
	}

}
