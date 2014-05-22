package CalendarService;

import java.io.Serializable;

public class CalendarMessage implements Serializable {



	private Enumeration kind;
	private Object data;
	
	public CalendarMessage(Enumeration kind, Object data) {
		super();
		this.kind = kind;
		this.data = data;
	}
	
	public Enumeration getKind() {
		return kind;
	}

	public void setKind(Enumeration kind) {
		this.kind = kind;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		return "CalendarMessage [kind=" + kind + ", data=" + data + "]";
	}
}
