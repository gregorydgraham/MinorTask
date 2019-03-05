/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBRecursiveQuery;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class ClarityAndProgress {
	
	private final Task task;
	private int absoluteClarity;
	private Double deltaClarity;
	private Long absoluteProgress;
	private Double deltaProgress;
	private long recentClarity;
	private long recentProgress;

	public ClarityAndProgress(Task task) {
		this.task = task;
		generateValues();
	}

	protected final void generateValues() {
		Task task1 = getTask();
		DBQuery primingQuery = Globals.getDatabase().getDBQuery(task1);
		DBRecursiveQuery<Task> recQuery = Globals.getDatabase().getDBRecursiveQuery(primingQuery, task1.column(task1.projectID), task1);
		try {
			List<Task> descendants = recQuery.getDescendants();
			absoluteClarity = descendants.size();
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(GregorianCalendar.WEEK_OF_YEAR, -1);
			final Date aWeekAgo = cal.getTime();
			recentClarity = descendants
					.stream()
					.filter((t) -> t != null && t.createdDate != null && t.createdDate.getValue() != null && t.createdDate.getValue().after(aWeekAgo))
					.count();
			absoluteProgress = descendants
					.stream()
					.filter((t) -> t != null && t.completionDate != null && t.completionDate.getValue() != null)
					.count();
			recentProgress = descendants
					.stream()
					.filter((t) -> t != null && t.completionDate != null && t.completionDate.getValue() != null && t.completionDate.getValue().after(aWeekAgo))
					.count();
			System.out.println("Math.round((" + recentClarity + " / " + absoluteClarity + ") * 100)");
			deltaClarity = (0.0d + recentClarity) / absoluteClarity;
			deltaProgress = (0.0d + recentProgress) / absoluteClarity;
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			Globals.sqlerror(ex);
		}
	}

	/**
	 * @return the absoluteClarity
	 */
	public int getAbsoluteClarity() {
		return absoluteClarity;
	}

	/**
	 * @return the deltaClarity
	 */
	public String getDeltaClarity() {
		return "+" + (Math.round((deltaClarity*100) * 100) / 100) + "%";
	}

	/**
	 * @return the absoluteProgress
	 */
	public Long getAbsoluteProgress() {
		return absoluteProgress;
	}

	/**
	 * @return the deltaProgress
	 */
	public String getDeltaProgress() {
		return "+" + (Math.round((deltaProgress*100) * 100) / 100) + "%";
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @return the recentClarity
	 */
	public long getRecentClarity() {
		return recentClarity;
	}

	/**
	 * @return the recentProgress
	 */
	public long getRecentProgress() {
		return recentProgress;
	}

	public void refresh() {
		generateValues();
	}
	
}
