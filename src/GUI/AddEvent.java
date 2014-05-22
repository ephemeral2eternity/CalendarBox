package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;
import CalendarService.CalendarEdge;

public class AddEvent extends JFrame{

	public JFrame frame;
	private JTextField eventname;
	private JTextField eventinfo;
	private JDatePickerImpl datePicker;
	private JSpinner timeStart;
	private JSpinner timeEnd;
	private JTextField textField;
	private JLabel lblFileName;
	private JButton  btnBrowse;	
	CalendarUI fsp;
	private File file1=null;
	public CalendarEdge edgePoint;
	
	private JCheckBox cb;
	private Event existingevent = null;
	public ArrayList<JCheckBox> checkboxlist = new ArrayList<JCheckBox>();
	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the application.
	 * @param firstSwingApp 
	 */
	public AddEvent(CalendarUI firstSwingApp, CalendarEdge edgePoint, Event eve, String title) {
		this.fsp = firstSwingApp;
		this.edgePoint = edgePoint;
		this.existingevent = eve;
		initialize(title);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String title) {
		System.out.println("Enter init for addEvent");
		frame = new JFrame(this.edgePoint.Name + ":" + title);
		frame.setBounds(100, 100, 624, 521);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblEventName = new JLabel("Event Name:");
		lblEventName.setBounds(37, 33, 73, 14);
		frame.getContentPane().add(lblEventName);
		
		eventname = new JTextField();
		eventname.setBounds(120, 30, 86, 20);
		
		if(this.existingevent != null)
			eventname.setText(existingevent.getTitle());

		frame.getContentPane().add(eventname);
		eventname.setColumns(10);

		

		JLabel lblEventInfo = new JLabel("Event Info:");
		lblEventInfo.setBounds(37, 72, 61, 14);
		frame.getContentPane().add(lblEventInfo);
		
		eventinfo = new JTextField();
		eventinfo.setBounds(120, 69, 86, 20);
		frame.getContentPane().add(eventinfo);
		eventinfo.setColumns(10);
		
		if(existingevent != null)
			eventinfo.setText(existingevent.getInfo());

		JLabel lblDate = new JLabel("Date:");
		lblDate.setBounds(37, 109, 73, 14);
		frame.getContentPane().add(lblDate);
		
		//JDateChooser dateChooser = new JDateChooser();
		
		UtilDateModel model = new UtilDateModel();
		
		if(existingevent != null)
		{
			Calendar cal = existingevent.getDate();
			model.setDate(cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DATE));
			
		}
		else
		{
			Calendar cal = Calendar.getInstance();
			model.setDate(cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DATE));
		}
		model.setSelected(true);
		
		JDatePanelImpl datePanel = new JDatePanelImpl(model);
		datePicker = new JDatePickerImpl(datePanel);
		datePicker.setBounds(120, 109, 150 , 50);
		frame.getContentPane().add(datePicker);
		
		
		JLabel lblStart = new JLabel("Start:");
		lblStart.setBounds(37, 173, 33, 14);
		frame.getContentPane().add(lblStart);
		
		
		
		timeStart = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeStart, "HH:mm");
		timeStart.setEditor(timeEditor);
		
		if(existingevent != null) {
			Calendar cal = existingevent.getStartTime();
			timeStart.setValue(cal.getTime());
		}
		else
			timeStart.setValue(new Date()); // will only show the current time
		timeStart.setBounds(120, 170, 65, 20);
		frame.getContentPane().add(timeStart);
		
		
		JLabel lblEnd = new JLabel("End:");
		lblEnd.setBounds(37, 214, 28, 14);
		frame.getContentPane().add(lblEnd);
		
		
		timeEnd = new JSpinner( new SpinnerDateModel() );
		JSpinner.DateEditor timeEditor2 = new JSpinner.DateEditor(timeEnd, "HH:mm");
		timeEnd.setEditor(timeEditor2);
		
		if(existingevent != null) {
			Calendar cal = existingevent.getEndTime();
			timeEnd.setValue(cal.getTime());
		}
		else
			timeEnd.setValue(new Date()); // will only show the current time
		
		
		timeEnd.setBounds(120, 211, 65, 20);
		frame.getContentPane().add(timeEnd);
		
		
		
		JLabel lblParticipants = new JLabel("Participants:");
		lblParticipants.setBounds(37, 283, 73, 14);
		frame.getContentPane().add(lblParticipants);
		
		
		JPanel jPanel1 = new javax.swing.JPanel();
		  
		JScrollPane scrollPane = new JScrollPane( );
		jPanel1.setLayout(new java.awt.GridLayout(0, 1, 10, 10));
		scrollPane.setBounds(120, 250, 170, 80);
		scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(jPanel1);
		frame.getContentPane().add( scrollPane );
		
		if(existingevent == null)
		{
			//dummy for test purpose. to be removed
			ArrayList<String> participants = new ArrayList<String>();

			for(String name : this.edgePoint.comm.getMap().keySet()) {
				participants.add(name);
			}
				
			for (int i = 0; i < participants.size(); i++)
			{
				cb = new JCheckBox(participants.get(i));
				cb.setName(participants.get(i));
				//cb.setSelected(true);
				jPanel1.add(cb);
				checkboxlist.add(cb);
			}
		}
		
		JButton btnCreateEvent = new JButton(title);
		btnCreateEvent.setBounds(121, 416, 108, 23);
		btnCreateEvent.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				if(existingevent == null)
					getAndSendEvent();
				else {
					existingevent.setOK(true);
					ModifyAndSendEvent();
				}
			}
		});
		frame.getContentPane().add(btnCreateEvent);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(268, 416, 89, 23);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(existingevent != null) {
					existingevent.setOK(true);
				}
				fsp.cancelEvent();
			}
		});
		frame.getContentPane().add(btnCancel);
		
		JLabel lblCreateANew = new JLabel(title);
		lblCreateANew.setBounds(181, 5, 129, 14);
		frame.getContentPane().add(lblCreateANew);
		
		if(existingevent == null)
		{
		lblFileName = new JLabel("File:");
		lblFileName.setBounds(37, 369, 30, 14);
		frame.getContentPane().add(lblFileName);
		
		textField = new JTextField();
		textField.setColumns(10);
		textField.setBounds(120, 366, 211, 20);
		frame.getContentPane().add(textField);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(348, 366, 85, 20);
	    btnBrowse.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent e) {
        	  selectFile(e);
         }
        });

		frame.getContentPane().add(btnBrowse);
		}
	}
	
	private void selectFile(ActionEvent e) {
		// TODO Auto-generated method stub
		   JFileChooser filedilg=new JFileChooser();
           filedilg.showOpenDialog(filedilg);
           String filename=filedilg.getSelectedFile().getAbsolutePath();
           textField.setText(filename);

      }
	
	private void getAndSendEvent()
	{
		//System.out.println(eventname.getText());
		//System.out.println(eventinfo.getText());
				
		 String fname = "";
		 byte[] fileArray=null;
			if (!textField.getText().equals(""))
			{
				file1 = new File(textField.getText());
		          fname = file1.getName();
		           
		           String fpath = file1.getPath();           
	
		           Path file = Paths.get(fpath);
		          
		           try {           
		        	   fileArray = Files.readAllBytes(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		           
		           /// ~~~~~~~~~ This logic is for write the byte array to a file on the receiver side. Remove it from here~~~~~~~\\\
		           //  ~~~ check path for linux. we put it in home
		          
			}
			
			ArrayList<String> people = new ArrayList<String>();
			for(int i=0; i<checkboxlist.size();i++)
			{
				if(checkboxlist.get(i).isSelected())
				{
					people.add(checkboxlist.get(i).getName());
				}
			}	
		
		fsp.getEventData(eventname.getText(), eventinfo.getText(), 
				(Date) datePicker.getModel().getValue(), (Date) timeStart.getValue(), 
				(Date) timeEnd.getValue() , people, fname, fileArray);

		
		
	}

	public void ModifyAndSendEvent() {

		fsp.getModifiedEventData(existingevent, eventname.getText(), eventinfo.getText(), 
				(Date) datePicker.getModel().getValue(), (Date) timeStart.getValue(), 
				(Date) timeEnd.getValue(), null);
		
	}

}
