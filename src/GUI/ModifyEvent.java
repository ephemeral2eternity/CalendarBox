package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import calendarBox.Tools;

import CalendarService.CalendarEdge;


public class ModifyEvent {

	public JFrame frame;
	private JTable table;
	private CalendarUI fsp;
	private JButton btnCancel;
	private JButton btnNewButton;
	private int selection;
	private CalendarEdge edgePoint;
	private AddEvent window;
	
	Object[][] data2=null;

	/**
	 * Create the application.
	 * @param firstSwingApp 
	 */
	public ModifyEvent(CalendarUI firstSwingApp, CalendarEdge edgePoint) {

		this.fsp = firstSwingApp;
		this.edgePoint = edgePoint;
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(this.edgePoint.Name + ":" + "Modify Event");
		frame.setBounds(100, 100, 770, 354);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		
		// populate data
		String col[] = {"Event Date","StartTime","EndTime", "Organizer", "Title", "Info", "Filename", "Participants"};
		//Calendar date, Calendar startTime, Calendar endTime, String organizer, String title,
		//String info, ArrayList<String> participants		
		//System.out.println(elist.get(0).getTitle());
		if(!this.edgePoint.comm.inEvent.isEmpty())
		{
			data2 = new Object[this.edgePoint.comm.inEvent.size()][8];
System.out.println("the size is" + this.edgePoint.comm.inEvent.size());
			for(int i = 0;i < this.edgePoint.comm.inEvent.size();i ++)
			{
//Tools.PopInformationMessage("", "Title" + this.edgePoint.comm.inEvent.get(i).getTitle());
				Event e = this.edgePoint.comm.inEvent.get(i);
System.out.println("i is" + i);
				Calendar c = e.getDate();
				String x = (c.get(Calendar.MONTH) +1) +"-"+ c.get(Calendar.DATE) +"-"+ c.get(Calendar.YEAR);
				
				data2[i][0] = x;
				
				c = e.getStartTime();
				x = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
				data2[i][1] = x;
				
				c = e.getEndTime();
				x = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
				data2[i][2] = x;
				data2[i][3] = e.getOrganizer();
				data2[i][4] = e.getTitle();
				data2[i][5] = e.getInfo();
				data2[i][6] = e.getFname().toString();
				data2[i][7] = e.getParticipants().toString();
				
			}
			
			
			table = new JTable(data2, col);
			
			/*table.setModel(new DefaultTableModel()
			{
				@Override
				public boolean isCellEditable(int row,int column) {
					return false;}
			});*/
					
			table.setBounds(0, 25, 745, 199);
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				
				@Override
				public void valueChanged(ListSelectionEvent eve) {
					// TODO Auto-generated method stub
					if(!eve.getValueIsAdjusting())
					{
						selection = table.getSelectionModel().getLeadSelectionIndex();
						System.out.println(selection);		
					}
				}
			});
			
			JScrollPane scrollPane = new JScrollPane( table );
			scrollPane.setBounds(0, 25, 750, 199);
		    frame.getContentPane().add( scrollPane );
			
		}
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fsp.cancelModifyEvent();
				
			}
		});
		btnCancel.setBounds(380, 248, 89, 23);
		frame.getContentPane().add(btnCancel);
		
		
		
		
		//frame.getContentPane().add(table.getTableHeader(), BorderLayout.PAGE_START);
		//frame.getContentPane().add(table, BorderLayout.CENTER);
		
		btnNewButton = new JButton("Delete");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteRow();
			}
		});
		btnNewButton.setBounds(257, 248, 89, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnModify = new JButton("Modify");
		btnModify.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// get the data - which event he is modifiying 
				callnewjframe();
			}
		});
		btnModify.setBounds(145, 248, 89, 23);
		frame.getContentPane().add(btnModify);
		
		System.out.println("Survived");
		
		
	}
	


	private void deleteRow()
	{
		System.out.println("Deleting the selection " + selection);
		
		Event deleteEvent = deleteList(selection);
//Tools.PopInformationMessage("after deleteList","after deleteList");
System.out.println("finish deleteList");
		try {
			edgePoint.sendOutNewEvent(deleteEvent, "delete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//Tools.PopInformationMessage("After sending out msg", "After sending out msg");
System.out.println("finish sendout ");
		fsp.DeletedValues();
		
		
	}
	
	/*
	public void modifyList(int sel) {
		Event modifiedEvent = edgePoint.comm.inEvent.get(sel);
		if(edgePoint.comm.localEvent.contains(modifiedEvent)) {
			Event eventLocalEvent = edgePoint.comm.localEvent.get(
					edgePoint.comm.localEvent.indexOf(modifiedEvent));
			
			updateEvent(eventLocalEvent, sel);
//			eventLocalEvent.setDate((Calendar)this.data2[sel][0]);
//			eventLocalEvent.setStartTime((Calendar)this.data2[sel][1]);
//			eventLocalEvent.setEndTime((Calendar)this.data2[sel][2]);
//			eventLocalEvent.setOrganizer((String)this.data2[sel][3]);
//			eventLocalEvent.setTitle((String)this.data2[sel][4]);
//			eventLocalEvent.setInfo((String)this.data2[sel][5]);
//			eventLocalEvent.setParticipants((ArrayList<String>)this.data2[sel][6]);//not sure about this 
			
		}
		updateEvent(modifiedEvent, sel);
//		modifiedEvent.setDate((Calendar)this.data2[sel][0]);
//		modifiedEvent.setStartTime((Calendar)this.data2[sel][1]);
//		modifiedEvent.setEndTime((Calendar)this.data2[sel][2]);
//		modifiedEvent.setOrganizer((String)this.data2[sel][3]);
//		modifiedEvent.setTitle((String)this.data2[sel][4]);
//		modifiedEvent.setInfo((String)this.data2[sel][5]);
//		modifiedEvent.setParticipants((ArrayList<String>)this.data2[sel][6]);//not sure about this
	}
	*/
	public Event deleteList(int sel) {
		Event deleteEvent = edgePoint.comm.inEvent.get(sel);
//Tools.PopInformationMessage("Enter deleteList!!\n", "Enter deleteList!!\n");
		if(edgePoint.comm.localEvent.contains(deleteEvent)) {
			edgePoint.comm.localEvent.remove(deleteEvent);
		}
		edgePoint.comm.inEvent.remove(sel);
		return deleteEvent;
	}
	
	private void callnewjframe()
	{
		fsp.modifyexistingevent(this.edgePoint.comm.inEvent.get(selection));
		
	}
	/*
	public void updateEvent(Event e, int sel) {
		//0
		DateFormat formatter = new SimpleDateFormat("M-dd-yyyy");
		Date date;
		try {
			date = formatter.parse((String)this.data2[sel][0]);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			e.setDate(calendar);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//1
		DateFormat timeFormatter = new SimpleDateFormat("HH:mm");
		try {
			Date timeDate = timeFormatter.parse((String)this.data2[sel][1]);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(timeDate);
			e.setStartTime(calendar);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//2
		try {
			Date endDate = timeFormatter.parse((String)this.data2[sel][2]);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(endDate);
			e.setEndTime(endCalendar);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		e.setOrganizer((String)this.data2[sel][3]);
		e.setTitle((String)this.data2[sel][4]);
		e.setInfo((String)this.data2[sel][5]);
		
		//6
		String replace = ((String)this.data2[sel][6]).replace("[", "").replace("]", "");
		e.setParticipants(new ArrayList<String>(Arrays.asList(replace.split(","))));//not sure about this 
	}
	
	*/
}