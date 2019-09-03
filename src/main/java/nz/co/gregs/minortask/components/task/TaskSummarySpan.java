/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.shared.Registration;
import java.text.SimpleDateFormat;
import java.util.Date;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.components.polymer.Details;
import nz.co.gregs.minortask.datamodel.Task;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import nz.co.gregs.minortask.MinorTaskEventNotifier;
import nz.co.gregs.minortask.components.RequiresPermission;
import nz.co.gregs.minortask.datamodel.Task.Project;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/tasksummary.css")
public class TaskSummarySpan extends Span implements MinorTaskEventNotifier, HasTaskAndProject, RequiresPermission {

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM dd, YYYY");
	Span name = new Span();
	private Task.TaskAndProject taskAndProject;

	public TaskSummarySpan(Task.TaskAndProject taskAndProject) {
//		super(task);
		setTaskAndProject(taskAndProject);
		final String taskName = getTask().name.getValue();
		this.setTitle("Summary of "+taskName);

		name.setText(taskName == null ? "" : taskName);
		name.addClassNames("tasksummary-name");

		Span projectSpan = createProjectSpan(getTask(), getProject());

		Div topDiv = new Div(name, projectSpan);
		topDiv.setSizeUndefined();
		topDiv.addClassName("tasksummary-nameandproject");

		Span desc = new Span();
		desc.setText(getTask().description.getValue());
		desc.setSizeFull();
		desc.addClassNames("tasksummary-description");

		Span summary = new Span(topDiv, desc);
		summary.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(this, getTask(), true));
		});
		summary.addClassName("tasksummary-summary");

		Details details = new Details("more...");
		setDetails(details);

		Div summaryAndDetails = new Div(summary, details);
		summaryAndDetails.addClassName("tasksummary-all");

		this.add(summaryAndDetails);
		this.addClassName("tasksummary");
	}

	protected final Span createProjectSpan(Task task, Project project) {
		Span projectSpan = new Span();
		projectSpan.setSizeUndefined();
		if (project != null) {
			projectSpan.setText(project.name.getValue());
		}
		projectSpan.addClassNames("tasksummary-project");
		return projectSpan;
	}

	private void setDetails(Details details) {
		final Task task = getTask();
		if (task != null) {
			if (getProject() != null) {
				details.add(new Div(new Span("Part Of: " + getProject().name.getValue()))); // I don't care, if they can't see we won't add it.
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

	public Registration addTaskMoveListener(
			ComponentEventListener<MinorTaskEvent> listener) {
		return addListener(MinorTaskEvent.class, listener);
	}

	@Override
	public void setTaskAndProject(Task.TaskAndProject taskAndProject) {
		this.taskAndProject = taskAndProject;
	}

	@Override
	public Task.TaskAndProject getTaskAndProject() throws Globals.InaccessibleTaskException {
		return taskAndProject;
	}
}
