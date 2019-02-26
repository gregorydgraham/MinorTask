/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.polymer.Details;
import nz.co.gregs.minortask.datamodel.Task;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/taskoverview.css")
public class TaskOverviewSpan extends SecureTaskSpan {

	private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, YYYY");

	;

	public TaskOverviewSpan(Task task) {
		super(task);

		Span name = new Span();
		name.setText(task.name.getValue());
		name.setSizeFull();
		name.addClassNames("taskoverview-name");

		Span desc = new Span();
		desc.setText(task.description.getValue());
		desc.setSizeFull();
		desc.addClassNames("taskoverview-description");

		Span summary = new Span(name, desc);
		summary.addClickListener((event) -> {
			Globals.showTask(task.taskID.getValue());
		});
		summary.addClassName("taskoverview-summary");

		Details details = new Details("more...");
		setDetails(details);

		Span span = new Span(summary, details);
		span.addClassName("taskoverview-all");

		this.add(span);
		this.addClassName("taskoverview");
	}

	private void setDetails(Details details) {
		final Task task = getTask();
		if (task != null) {
			Long projectID = task.projectID.getValue();
			if (projectID != null) {
				try {
					details.add(new Div(new Span("Part Of: " + minortask().getTask(projectID).name.getValue())));
				} catch (Globals.InaccessibleTaskException ex) {
					// I don't care, if they can't see we won't add it.
				}
			}

			final Task.Owner owner = task.getOwner();
			if (owner != null) {
				details.add(new Div(new Span("Owner: " + owner.getUsername())));
			} else if (task.userID.getValue() != null) {
				details.add(new Div(new Span("Owner: " + getUser(task.userID.getValue()).getUsername())));
			}
			final Task.Assignee assigneeUser = task.getAssigneeUser();
			if (assigneeUser != null) {
				details.add(new Div(new Span("Assigned: " + assigneeUser.getUsername())));
			} else if (task.assigneeID.getValue() != null) {
				details.add(new Div(new Span("Assigned: " + getUser(task.assigneeID.getValue()).getUsername())));
			}

			Date starts = task.startDate.dateValue();
			if (starts != null) {
				details.add(new Div(new Span("Starts: " + DATE_FORMAT.format(starts))));
			}
			Date reminder = task.preferredDate.dateValue();
			if (reminder != null) {
				details.add(new Div(new Span("Reminder: " + DATE_FORMAT.format(reminder))));
			}
			Date deadline = task.finalDate.dateValue();
			if (deadline != null) {
				details.add(new Div(new Span("Deadline: " + DATE_FORMAT.format(deadline))));
			}
			Date completed = task.completionDate.dateValue();
			if (completed != null) {
				details.add(new Div(new Span("Completed: " + DATE_FORMAT.format(completed))));
			}
			Period repeat = task.repeatOffset.getValue();
			if (repeat != null) {
				details.add(new Div(new Span("Repeat: " + repeat.toString(PeriodFormat.wordBased()))));
			}

			String notes = task.notes.stringValue();
			if (notes != null && notes.length() > 0) {
				final int lengthOfNotes = 100;
				if (notes.length() > lengthOfNotes) {
					details.add(new Div(new Span("Notes: " + notes.substring(0, lengthOfNotes) + "...")));
				} else {
					details.add(new Div(new Span("Notes: " + notes)));
				}
			}
		}
	}
}
