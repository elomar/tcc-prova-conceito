package tads.imotech.conceito;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;

public class SmsWrapper {
	public static final String ACCOUNT_SID = "[SID]";
	public static final String AUTH_TOKEN = "[TOKEN]";
	public static final String FROM = "[FROM]";
	  
	private String message;
	private TwilioRestClient client;
	
	public SmsWrapper(String message) {
		this.message = message;
		this.client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
	}
	
	public void send(String to) {
	    try {
			getMessageFactory().create(getMessageParams(to));
		} catch (TwilioRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<NameValuePair> getMessageParams(String to) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    params.add(new BasicNameValuePair("Body", message));
	    params.add(new BasicNameValuePair("From", FROM));
	    params.add(new BasicNameValuePair("To", to));
	    return params;
	}
	
	private MessageFactory getMessageFactory() {
		return client.getAccount().getMessageFactory();
	}
}
