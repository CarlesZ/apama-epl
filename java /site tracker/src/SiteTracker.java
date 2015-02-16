
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

import com.apama.EngineException;
import com.apama.engine.EngineManagement;
import com.apama.engine.EngineManagementFactory;
import com.apama.event.Event;

public class SiteTracker implements EventsSender {
	private EngineManagement engine = null;
	private String wbLogFile = null;
	
	public static void main(String[] args) {
		new SiteTracker().run(args);
	}

	
	public void run(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try
		{
			// Connect to the correlator
			engine = EngineManagementFactory.connectToEngine(host, port);
			doLog("Connected");
						
			Thread th1 = new Thread(new WeblogWatcher(args[2], this));
			//Thread th2 = new Thread(new UsersWatcher(args[3], this));
			th1.start();
			//th2.start();
			
			th1.join();
			//th2.join();
			
			engine.disconnect();
			doLog("Disconnected");
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
		
	private void doLog(String msg) {
		System.out.println(msg);
	}	


	@Override
	public synchronized void Send(Event event) {
		Event[] events = new Event[1];
		events[0] = event;
		try {
			engine.sendEvents(events);
		} catch (EngineException e) {
			e.printStackTrace();
		}
	}
}

