package tads.imotech.conceito;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class CalendarWrapper {
	private static final String APPLICATION_NAME = "tcc-prova-conceito";
	private static final String CALENDAR_ID = "elomar.f@gmail.com";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
	 * globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private HttpTransport httpTransport;

	private Calendar client;

	public Event addEvent(java.util.Date date, String description) throws Exception {
		return insertEvent(buildEvent(date, description));
	}

	private Event buildEvent(java.util.Date start, String description) {
		Event event = new Event();
		event.setSummary(description);
	
		DateTime startDate = new DateTime(start, TimeZone.getTimeZone("Recife"));
		event.setStart(new EventDateTime().setDateTime(startDate));
		
		java.util.Calendar end = java.util.Calendar.getInstance();
		end.setTime(start);
		end.add(java.util.Calendar.HOUR_OF_DAY, 1);
		
		DateTime endDate = new DateTime(end.getTime(), TimeZone.getTimeZone("Recife"));
		event.setEnd(new EventDateTime().setDateTime(endDate));
		
		return event;
	}

	private Event insertEvent(Event event) throws IOException, Exception {
		return getClient().events().insert(CALENDAR_ID, event).execute();
	}
	
	/** Authorizes the installed application to access user's protected data. */
	private Credential authorize() throws Exception {
		// load client secrets
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(CalendarWrapper.class.getResourceAsStream("/resources/client_secrets.json")));
		
		// Set up authorization code flow.
		// Ask for only the permissions you need. Asking for more permissions will
		// reduce the number of users who finish the process for giving you access
		// to their accounts. It will also increase the amount of effort you will
		// have to spend explaining to users what you are doing with their data.
		// Here we are listing all of the available scopes. You should remove scopes
		// that you are not actually using.
		Set<String> scopes = new HashSet<String>();
		scopes.add(CalendarScopes.CALENDAR);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, JSON_FACTORY, clientSecrets, scopes)
		.setDataStoreFactory(dataStoreFactory)
		.build();
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	private Calendar getClient() throws Exception {
		if (client == null) {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			// initialize the data store factory
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// authorization
			Credential credential = authorize();

			// set up global Calendar instance
			client = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
			.setApplicationName(APPLICATION_NAME).build();
		}

		return client;
	}

}
