package events.sales;

import com.fasterxml.jackson.databind.JsonNode;

public class PddSyncEvent {
	
	private JsonNode main;
	
	private String email;
	

	public JsonNode getMain() {
		return main;
	}

	public void setMain(JsonNode main) {
		this.main = main;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public PddSyncEvent(JsonNode main, String email) {
		super();
		this.main = main;
		this.email = email;
	}
	
	
	
}
