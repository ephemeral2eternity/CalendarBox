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
import CalendarService.CalendarBackup;
import CalendarService.CalendarEdge;
import CalendarService.RendezVousTest;

public class BackupUI {

	private JFrame frame;
	private JTextField textField;
	private JButton btnUpload;
	private JLabel lblFileName;
	private JButton  btnBrowse;
	private JButton btnCancel;
	private JLabel lblWelcomeToCalendarbox;
	private File file1=null;
	private JButton btnModifyEvent;
	private JButton btnNewButton;
	private AddEvent window;
	private ModifyEvent mwindow;
	//private ArrayList<Event> elist = new ArrayList<Event>();
	//private ArrayList<Event> wholeList = new ArrayList<Event>();
	
	private CalendarBackup backupPoint;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BackupUI window = new BackupUI();
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BackupUI() {
		startNetwork();
		//initialize();
		Tools.PopInformationMessage("", "Backup is running!!");
		endNetwork();
	}

	
//	/**
//	 * Initialize the contents of the frame.
//	 */
//	private void initialize() {
//		frame = new JFrame(CalendarEdge.Name);
//		frame.setBounds(100, 100, 557, 334);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().setLayout(null);
//		
//		lblFileName = new JLabel("File:");
//		lblFileName.setBounds(26, 198, 30, 14);
//		frame.getContentPane().add(lblFileName);
//		
//		textField = new JTextField();
//		textField.setBounds(66, 195, 211, 20);
//		frame.getContentPane().add(textField);
//		textField.setColumns(10);
//		
//		btnBrowse = new JButton("Browse");
//        btnBrowse.setBounds(297, 195, 85, 20);
//        btnBrowse.addActionListener(new ActionListener() {
//
//          public void actionPerformed(ActionEvent e) {
//        	  selectFile(e);
//         }
//         });
//        
//        frame.getContentPane().add(btnBrowse);
//        
//        btnCancel = new JButton("Cancel");
//        btnCancel.setBounds(221, 249, 89, 23);
//        btnCancel.addActionListener(new ActionListener() {
//
//          @Override
//          public void actionPerformed(ActionEvent e) {
//              // TODO Auto-generated method stub
//        	  endNetwork();
//        	  System.exit(0); 
//          }
//      });
//        
//        frame.getContentPane().add(btnCancel);
//        
//		btnUpload = new JButton("Upload");
//		btnUpload.setBounds(81, 249, 89, 23);
//		btnUpload.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				doUpload(e);
//				// Upload a file to the tomcat7 server. First try on localhost
//				
//			}
//		});
//		
//		frame.getContentPane().add(btnUpload);
//		
//		btnNewButton = new JButton("Add Event");
//		btnNewButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				callnewjframe(edgePoint);
//			}
//		});
//		btnNewButton.setBounds(54, 67, 116, 23);
//		frame.getContentPane().add(btnNewButton);
//		
//		lblWelcomeToCalendarbox = new JLabel("Welcome to CalendarBox. Upload a file");
//		lblWelcomeToCalendarbox.setBounds(75, 11, 325, 14);
//		frame.getContentPane().add(lblWelcomeToCalendarbox);
//		
//		btnModifyEvent = new JButton("View/Modify Events");
//		btnModifyEvent.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// TODO Auto-generated method stub
//				modifyeventjframe();
//				
//			}
//		});
//		btnModifyEvent.setBounds(188, 67, 107, 23);
//		frame.getContentPane().add(btnModifyEvent);
//		
//		
//	}
//
//	private void selectFile(ActionEvent e) {
//		// TODO Auto-generated method stub
//		   JFileChooser filedilg=new JFileChooser();
//           filedilg.showOpenDialog(filedilg);
//           String filename=filedilg.getSelectedFile().getAbsolutePath();
//           textField.setText(filename);
//
//           file1 = new File(filename);
//           String fname = file1.getName();
//           fname = file1.getPath();
//           //System.out.println(fname);
//	}
//
//	private void doUpload(ActionEvent e) {
//		// TODO Auto-generated method stub
//		try {
//			if (textField.getText().equals("")) {
//	            JOptionPane.showMessageDialog(frame, "Please choose a file to upload!", "Error",
//	                    JOptionPane.ERROR_MESSAGE);
//	            return;
//			}
//			HttpUpload ht = new HttpUpload(file1, "http://128.237.250.157:8080/dsdemo/ds","UTF-8");
//			ht.execute();
//			textField.setText("");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//	
//	}
//	private void callnewjframe(CalendarEdge edgePoint)
//	{
//		
//		window = new AddEvent(this, edgePoint, null, "New Event");
//		window.frame.setVisible(true);
//		
//	}
//	
//	private void modifyeventjframe()
//	{
//		
//		mwindow = new ModifyEvent(this, this.edgePoint);
//		mwindow.frame.setVisible(true);
//	}
//	
//	public void getModifiedEventData(Event e, String eventname, String eventinfo, 
//			Date selectedDate, Date timeStart, Date timeEnd, ArrayList<String> people)
//	{
//		window.frame.dispose();
//		//Tools.PopInformationMessage("", "after window disappear");
//		System.out.println(eventname);
//		System.out.println(eventinfo);
//		
//		
//		// write the update logic here
//		Calendar datecalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//		datecalendar.setTime(selectedDate);
////System.out.println(datecalendar.get(Calendar.MONTH) + ":" + datecalendar.get(Calendar.DATE) + ":" + datecalendar.get(Calendar.YEAR));
//		
//		Calendar startteimecalendar = GregorianCalendar.getInstance(); 
//		startteimecalendar.setTime(timeStart);
////System.out.println(startteimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + startteimecalendar.get(Calendar.MINUTE));
//		
//
//		Calendar endtimecalendar = GregorianCalendar.getInstance(); 
//		endtimecalendar.setTime(timeEnd);
////System.out.println(endtimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + endtimecalendar.get(Calendar.MINUTE));
//		
//		
//		e.setDate(datecalendar);
//		e.setStartTime(startteimecalendar);
//		e.setEndTime(endtimecalendar);
//		e.setTitle(eventname);
//		e.setInfo(eventinfo);
//		
//		/*ArrayList<String> participants = new ArrayList<String>();
//		participants.add("Di");
//		Event e1 = new Event(datecalendar, startteimecalendar, endtimecalendar, "darsh", eventname, eventinfo, participants);
//		elist.add(e1);
//		*/
//		
//		//Tools.PopInformationMessage("", "before sending out in Modify");		
////System.out.println("Done with the screen");
//		try {
//			this.edgePoint.sendOutNewEvent(e, "modify");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		//Tools.PopInformationMessage("", "after sending out in Modify");	
//	}
//	
//	public void getEventData(String eventname, String eventinfo, Date selectedDate, Date timeStart, Date timeEnd, ArrayList<String> people)
//	{
//
//		window.frame.dispose();
//		//Tools.PopInformationMessage("window is closed!!\n", "window is closed!!\n");	
//		//System.out.println(eventname);
//		//System.out.println(eventinfo);
//		
//		Calendar datecalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//		datecalendar.setTime(selectedDate);
//		//System.out.println(datecalendar.get(Calendar.MONTH) + ":" + datecalendar.get(Calendar.DATE) + ":" + datecalendar.get(Calendar.YEAR));
//	
//		Calendar startteimecalendar = GregorianCalendar.getInstance(); 
//		startteimecalendar.setTime(timeStart);
//		//System.out.println(startteimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + startteimecalendar.get(Calendar.MINUTE));
//
//		
//		Calendar endtimecalendar = GregorianCalendar.getInstance(); 
//		endtimecalendar.setTime(timeEnd);
//		//System.out.println(endtimecalendar.get(Calendar.HOUR_OF_DAY) + ":" + endtimecalendar.get(Calendar.MINUTE));
//
//	
//		Event e1 = new Event(datecalendar, startteimecalendar, endtimecalendar, this.edgePoint.comm.getPeer_name(), eventname, eventinfo, people);
//		System.out.println("event created");
//
//		e1.setSeqNum(this.edgePoint.getEventNum());
//		this.edgePoint.increaseEventNum();
//	
//		this.edgePoint.comm.inEvent.add(e1);
//		System.out.println("SIZE IS" + this.edgePoint.comm.inEvent.size());
//		this.edgePoint.comm.localEvent.add(e1);
//		
//		//Tools.PopInformationMessage("New Event Created!!\n", "before New Event Created!!\n");
//		
//		try {
//			edgePoint.sendOutNewEvent(e1, "create");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//System.out.println("Done with the screen");
//		//Tools.PopInformationMessage("New Event Created!!\n", "after New Event Created!!\n");	
//
//	}
//	
//	public void modifyexistingevent(Event e)
//	{
//		mwindow.frame.dispose();
//		window = new AddEvent(this, this.edgePoint, e, "Modify Event");
//		window.frame.setVisible(true);
//	}
//	
//	public void cancelEvent()
//	{
//		window.frame.dispose();
//	}
//	
//	public void cancelModifyEvent()
//	{
//		mwindow.frame.dispose();
//	}
//	
//	public void DeletedValues()
//	{
//		mwindow.frame.dispose();
//		System.out.println("yo yo");
//		modifyeventjframe();
//	}
	
	/*
	 * This function will start the Jxta Network
	 */
	public void startNetwork() {
		this.backupPoint = new CalendarBackup();
        this.backupPoint.configBackUp();
        //this.backupPoint.checkForNewEvents();
	}
	
	/*
	 * This function will end the Jxta newtwork
	 */
	public void endNetwork() {
        // Stopping the network
        Tools.PopInformationMessage(CalendarBackup.Name, "Stop the JXTA network");
        CalendarBackup.MyNetworkManager.stopNetwork();
	}
}
