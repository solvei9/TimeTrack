package TimeTrack;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class Journal implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Project project;
	private Task task;
	private Calendar date_start;
	private Calendar date_finish;
	private BigDecimal length_hr;
	private BigDecimal length_dt;

	public Journal() {

	}

	public Journal(Integer id, Project project, Task task, Calendar date_start, Calendar date_finish,
			BigDecimal length_hr, BigDecimal length_dt) {
		super();
		this.id = id;
		this.project = project;
		this.task = task;
		this.date_start = date_start;
		this.date_finish = date_finish;
		this.length_hr = length_hr;
		this.length_dt = length_dt;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;

	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;

	}

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;

	}

	public Calendar getDateStart() {
		return this.date_start;
	}

	public void setDateStart(Calendar date_start) {
		this.date_start = date_start;
	}

	public Calendar getDateFinish() {
		return this.date_finish;
	}

	public void setDateFinish(Calendar date_finish) {
		this.date_finish = date_finish;
	}

	public BigDecimal getLengthHr() {
		return this.length_hr;
	}

	public void setLengthHr(BigDecimal length_hr) {
		this.length_hr = length_hr;
	}

	public BigDecimal getLengthDt() {
		return this.length_dt;
	}

	public void setLengthDt(BigDecimal length_dt) {
		this.length_dt = length_dt;
	}

}