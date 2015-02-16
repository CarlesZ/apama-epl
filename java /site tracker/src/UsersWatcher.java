
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

import com.apama.EngineException;
import com.apama.engine.EngineManagement;
import com.apama.event.Event;


public class UsersWatcher implements Runnable {
	private String wbLogFile = null;
	private EngineManagement engine = null;
	private EventsSender events = null;
	
	public UsersWatcher(String file, EventsSender events) {
		wbLogFile = file;
		this.events = events;
	}
	
	public void run() {
		try
		{
			Reader reader = new FileReader(wbLogFile);
			BufferedReader input = new BufferedReader(reader);
			String line = null;			
			boolean run = true;
			boolean ignore = true;
			
			while ( run ) {
				if ( (line = input.readLine()) != null ) {
					if ( ignore ) continue; 					
					sendUrlHit(line.split(" "));
				}
				
				try {
					ignore = false;
					Thread.sleep(100);
				}
				catch (InterruptedException  e) {
					Thread.currentThread().interrupt();
					run = false;
				}
			}
			input.close();		
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	private void sendUrlHit(String[] parts) throws EngineException  {
		if ( parts.length == 3) {
			if ( "IN".compareTo(parts[2]) == 0 ) {
				String eventStr = String.format("com.apama.tracker.UserLoggedIn(%s, \"%s\")", parts[0], parts[1]);
				events.Send(new Event(eventStr));
			}
		}
	}	

}
