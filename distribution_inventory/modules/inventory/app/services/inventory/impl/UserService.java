package services.inventory.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.libs.Json;
import services.inventory.IUserService;
import session.ISessionService;

public class UserService implements IUserService {
	
	private static final String USER_KEY = "user";
	
	private static final String ADMIN_KEY = "admin";
	
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
	public String getAdminAccoun() {
		String email = null;
		if(sessionService.get(ADMIN_KEY) != null){
			try {
				JsonNode login = Json.parse(sessionService.get(ADMIN_KEY).toString());
				email = login.get("email").asText();
			} catch (Exception e) {
			}
			
		}
		return email;
	}

}
