package calendarBox;

import java.io.Serializable;

public class NameSeqPair implements Serializable {




	String name;
	int seqNum;
	
	public NameSeqPair(String name, int seqNum) {
		super();
		this.name = name;
		this.seqNum = seqNum;
	}
	
	@Override
	public String toString() {
		return "NameSeqPair [name=" + name + ", seqNum=" + seqNum + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + seqNum;
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
		NameSeqPair other = (NameSeqPair) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (seqNum != other.seqNum)
			return false;
		return true;
	}
}
