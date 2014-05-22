package GUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Event implements Serializable {



	private Calendar date;

	private Calendar startTime;
	private Calendar endTime;
	private String organizer;

	private int seqNum = -1;
	private String title;
	private String info;
	private String fname;
	private ArrayList<String> participants;

	private byte[] fileData;
	private boolean isOK;
	
	public Event(Calendar date, Calendar startTime, Calendar endTime, String organizer, String title,
			String info, ArrayList<String> participants, String filename, byte[] fileData) {
		super();
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.organizer = organizer;
		this.title = title;
		this.info = info;
		this.participants = participants;
		this.fname = filename;
		this.fileData = fileData;
		this.isOK = true;
	}
	
	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public Calendar getStartTime() {
		return startTime;
	}

	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public int getSeqNum() {
		return seqNum;
	}

	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public ArrayList<String> getParticipants() {
		return participants;
	}

	public void setParticipants(ArrayList<String> participants) {
		this.participants = participants;
	}
	
	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public boolean isOK() {
		return isOK;
	}

	public void setOK(boolean isOK) {
		this.isOK = isOK;
	}
	
	@Override
	public String toString() {
		return "Event [date=" + date + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", organizer=" + organizer
				+ ", seqNum=" + seqNum + ", title=" + title + ", info=" + info
				+ ", fname=" + fname + ", participants=" + participants
				+ ", fileData=" + Arrays.toString(fileData) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + Arrays.hashCode(fileData);
		result = prime * result + ((fname == null) ? 0 : fname.hashCode());
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + (isOK ? 1231 : 1237);
		result = prime * result
				+ ((organizer == null) ? 0 : organizer.hashCode());
		result = prime * result
				+ ((participants == null) ? 0 : participants.hashCode());
		result = prime * result + seqNum;
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (!Arrays.equals(fileData, other.fileData))
			return false;
		if (fname == null) {
			if (other.fname != null)
				return false;
		} else if (!fname.equals(other.fname))
			return false;
		if (info == null) {
			if (other.info != null)
				return false;
		} else if (!info.equals(other.info))
			return false;
		if (isOK != other.isOK)
			return false;
		if (organizer == null) {
			if (other.organizer != null)
				return false;
		} else if (!organizer.equals(other.organizer))
			return false;
		if (participants == null) {
			if (other.participants != null)
				return false;
		} else if (!participants.equals(other.participants))
			return false;
		if (seqNum != other.seqNum)
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	
	
	


	
	
}
