package tads.imotech.conceito;

import java.util.Calendar;

import com.google.api.services.calendar.model.Event;

public class Main {

	public static void main(String[] args) throws Exception {
		String message = "Lembrete: visita marcada às 4h com Fulano.";
		String to = "+558498314884";
		
		new SmsWrapper(message).send(to);
		
		String description =  "Visita marcada com Fulano";
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 11, 26, 6, 30);
		
		Event event = new CalendarWrapper().addEvent(calendar.getTime(), description);
		System.out.println(event.getHtmlLink());
	}

}
