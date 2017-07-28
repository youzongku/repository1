package services.sales.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;

import play.libs.Json;
import services.sales.IUserService;
import session.ISessionService;

public class UserService implements IUserService {
	
	private static final String USER_KEY = "user";
	
	private static final String ADMIN_KEY = "admin";
	
	private static final String ACCOUNTS_KEY = "account";
	
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
	public String getAdminAccount() {
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

	@Override
	public String getRelateAccounts() {
		String account = "";
		if(sessionService.get(ACCOUNTS_KEY) != null){
			try {
				account = sessionService.get(ACCOUNTS_KEY).toString();
			} catch (Exception e) {
			}
		}
		return account;
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
