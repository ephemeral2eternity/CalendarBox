package GUI;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import calendarBox.RendezVous;
import calendarBox.Tools;
import CalendarService.CalendarEdge;
import CalendarService.CalendarMessage;
import CalendarService.Enumeration;
import CalendarService.RendezVousTest;

public class CalendarUI {

	private JFrame frame;

	private JLabel lblWelcomeToCalendarbox;
	private JButton btnModifyEvent;
	private JButton btnNewButton;
	private AddEvent window;
	private ModifyEvent mwindow;
	//private ArrayList<Event> elist = new ArrayList<Event>();
	//private ArrayList<Event> wholeList = new ArrayList<Event>();
	
	private CalendarEdge edgePoint;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalendarUI window = new CalendarUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CalendarUI() {
		startNetwork();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(this.edgePoint.Name);
		frame.setBounds(100, 100, 448, 178);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
				
		btnNewButton = new JButton("Add Event");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				callnewjframe(edgePoint);
			}
		});
		btnNewButton.setBounds(54, 67, 116, 23);
		frame.getContentPane().add(btnNewButton);
		
		lblWelcomeToCalendarbox = new JLabel("Welcome to CalendarBox. Upload a file");
		lblWelcomeToCalendarbox.setBounds(75, 11, 325, 14);
		frame.getContentPane().add(lblWelcomeToCalendarbox);
		
		btnModifyEvent = new JButton("View/Modify Events");
		btnModifyEvent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				modifyeventjframe();
				
			}
		});
		btnModifyEvent.setBounds(188, 67, 120, 23);
		frame.getContentPane().add(btnModifyEvent);
		
		JButton btnCancelEvent = new JButton("Cancel");
		btnCancelEvent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
				
			}
		});
		btnCancelEvent.setBounds(350, 67, 80, 23);
		
		frame.getContentPane().add(btnCancelEvent);
		
		
	}

	private void callnewjframe(CalendarEdge edgePoint)
	{
		
		window = new AddEvent(this, edgePoint, null, "New Event");
		window.frame.setVisible(true);
		
	}
	
	private void modifyeventjframe()
	{
		
		mwindow = new ModifyEvent(this, this.edgePoint);
		mwindow.frame.setVisible(true);
	}
	
	public void getModifiedEventData(Event e, String eventname, String eventinfo, 
			Date selectedDate, Date timeStart, Date timeEnd, ArrayList<String> people)
	{
		window.frame.dispose();
		//Tools.PopInformationMessage("", "after window disappear");
		System.out.println(eventname);
		System.out.println(eventinfo);
		
		
		// write the update logic here
		Calendar datecalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		datecalendar.setTime(selectedDate);
//System.out.println(datecalendar.get(Calendar.MONTH) + ":" + datecalendar.get(Calendar.DATE) + ":" + datecalendar.get(Calendar.YEAR));
		
		Calendar startteimecalendar = GregorianCalendar.getInstance(); 
		startteimecalendar.setTime(timeStart);
//System.out.println(startteimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + startteimecalendar.get(Calendar.MINUTE));
		

		Calendar endtimecalendar = GregorianCalendar.getInstance(); 
		endtimecalendar.setTime(timeEnd);
//System.out.println(endtimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + endtimecalendar.get(Calendar.MINUTE));
		
		
		e.setDate(datecalendar);
		e.setStartTime(startteimecalendar);
		e.setEndTime(endtimecalendar);
		e.setTitle(eventname);
		e.setInfo(eventinfo);
		
		/*ArrayList<String> participants = new ArrayList<String>();
		participants.add("Di");
		Event e1 = new Event(datecalendar, startteimecalendar, endtimecalendar, "darsh", eventname, eventinfo, participants);
		elist.add(e1);
		*/
		
		//Tools.PopInformationMessage("", "before sending out in Modify");		
//System.out.println("Done with the screen");
		try {
			this.edgePoint.sendOutNewEvent(e, "modify");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Tools.PopInformationMessage("", "after sending out in Modify");	
	}
	
	public void getEventData(String eventname, String eventinfo, Date selectedDate, 
			Date timeStart, Date timeEnd, ArrayList<String> people, String filename, byte[] fileData)
	{

		window.frame.dispose();
		//Tools.PopInformationMessage("window is closed!!\n", "window is closed!!\n");	
		//System.out.println(eventname);
		//System.out.println(eventinfo);
		
		Calendar datecalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		datecalendar.setTime(selectedDate);
		//System.out.println(datecalendar.get(Calendar.MONTH) + ":" + datecalendar.get(Calendar.DATE) + ":" + datecalendar.get(Calendar.YEAR));
	
		Calendar startteimecalendar = GregorianCalendar.getInstance(); 
		startteimecalendar.setTime(timeStart);
		//System.out.println(startteimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + startteimecalendar.get(Calendar.MINUTE));

		
		Calendar endtimecalendar = GregorianCalendar.getInstance(); 
		endtimecalendar.setTime(timeEnd);
		//System.out.println(endtimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + endtimecalendar.get(Calendar.MINUTE));

	
		Event e1 = new Event(datecalendar, startteimecalendar, endtimecalendar, this.edgePoint.comm.getPeer_name(), 
				eventname, eventinfo, people, filename, fileData);
		System.out.println("event created");

		e1.setSeqNum(this.edgePoint.getEventNum());
		this.edgePoint.increaseEventNum();
	
		this.edgePoint.comm.inEvent.add(e1);
		System.out.println("SIZE IS" + this.edgePoint.comm.inEvent.size());
		this.edgePoint.comm.localEvent.add(e1);
		
		//Tools.PopInformationMessage("New Event Created!!\n", "before New Event Created!!\n");
		
		try {
			edgePoint.sendOutNewEvent(e1, "create");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Done with the screen");
		//Tools.PopInformationMessage("New Event Created!!\n", "after New Event Created!!\n");	

	}
	
	public void modifyexistingevent(Event e)
	{
		mwindow.frame.dispose();
		if(e.isOK()) {
			
			for(String s : e.getParticipants()) {
Tools.PopInformationMessage("", "s is" + s + "this name" + this.edgePoint.Name);
				if(!s.equals(this.edgePoint.Name)) {
					if(this.edgePoint.comm.getMap().containsKey(s)) {
						this.edgePoint.comm.send_to_peer(new CalendarMessage(Enumeration.ASKFOROK, e), 
								this.edgePoint.comm.getMap().get(s));
					}
					else {
						Tools.PopInformationMessage("", "1.Cannot find:  " + s);
					}
				}
			}
Tools.PopInformationMessage("", "s is" + e.getOrganizer() + "this name" + this.edgePoint.Name);
			if(!e.getOrganizer().equals(this.edgePoint.Name)) {
				if(this.edgePoint.comm.getMap().containsKey(e.getOrganizer())) {
					this.edgePoint.comm.send_to_peer(new CalendarMessage(Enumeration.ASKFOROK, e), 
							this.edgePoint.comm.getMap().get(e.getOrganizer()));
				}
				else {
					Tools.PopInformationMessage("", "2. Cannot find:  " + e.getOrganizer());
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(e.isOK()) {
				e.setOK(false);
				window = new AddEvent(this, this.edgePoint, e, "Modify Event");
				window.frame.setVisible(true);
			}
			else {
				Tools.PopInformationMessage("", "Cannot modify now!");
			}
		}
		else {
			Tools.PopInformationMessage("", "Cannot modify now!");
		}
		
	}
	
	public void cancelEvent()
	{
		window.frame.dispose();
	}
	
	public void cancelModifyEvent()
	{
		mwindow.frame.dispose();
	}
	
	public void DeletedValues()
	{
		mwindow.frame.dispose();
		System.out.println("yo yo");
		modifyeventjframe();
	}
	
	/*
	 * This function will start the Jxta Network
	 */
	public void startNetwork() {
		this.edgePoint = new CalendarEdge();
        this.edgePoint.configEdge();
        this.edgePoint.checkForNewEvents();
	}
	
	/*
	 * This function will end the Jxta newtwork
	 */
	public void endNetwork() {
        // Stopping the network
        Tools.PopInformationMessage(this.edgePoint.Name, "Stop the JXTA network");
        CalendarEdge.MyNetworkManager.stopNetwork();
	}
}
