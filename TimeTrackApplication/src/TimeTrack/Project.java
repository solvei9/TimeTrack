package TimeTrack;

import java.io.Serializable;
import java.util.Date;

public class Project implements Comparable<Object>, Serializable {
	private static final long serialVersionUID = 1L;
	private String project_name;
	private Date date_start;
	private Date date_finish;
	private Number length_hr;
	private Number length_dt;

	public Project() {

	}

	public Project(String name, Date date_start, Date date_finish,
			Number length_hr, Number length_dt) {
		super();
		this.project_name = name;
		this.date_start = date_start;
		this.date_finish = date_finish;
		this.length_hr = length_hr;
		this.length_dt = length_dt;
	}

	public String getName() {
		return this.project_name;
	}

	public void setName(String name) {
		this.project_name = name;

	}

	public Date getDateStart() {
		return this.date_start;
	}

	public void setDateStart(Date date_start) {
		this.date_start = date_start;
	}

	public Date getDateFinish() {
		return this.date_finish;
	}

	public void setDateFinish(Date date_finish) {
		this.date_finish = date_finish;
	}

	public Number getLengthHr() {
		return this.length_hr;
	}

	public void setLengthHr(Number length_hr) {
		this.length_hr = length_hr;
	}

	public Number getLengthDt() {
		return this.length_dt;
	}

	public void setLengthDt(Number length_dt) {
		this.length_dt = length_dt;
	}

	public int compareTo(Object arg0) {
		Project project = (Project) arg0;
		int res = this.project_name.compareTo(project.getName());
		return res;
	}
}