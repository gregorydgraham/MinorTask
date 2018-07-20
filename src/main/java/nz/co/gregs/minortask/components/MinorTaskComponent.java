/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.server.VaadinSession;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.databases.DBDatabaseCluster;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public interface MinorTaskComponent {

	default MinorTask minortask() {
		final VaadinSession session = VaadinSession.getCurrent();
		MinorTask minortask = session.getAttribute(MinorTask.class);
		if (minortask == null) {
			session.setAttribute(MinorTask.class, new MinorTask());
			minortask = session.getAttribute(MinorTask.class);
		}
		return minortask;
	}

	default DBDatabaseCluster getDatabase() {
		return minortask().getDatabase();
	}

	default Task getTask(Long taskID) throws MinorTask.InaccessibleTaskException {
		return minortask().getTask(taskID);
	}

	default Long getUserID() {
		return minortask().getUserID();
	}

	default LocalDate asLocalDate(Date date) {
		return MinorTask.asLocalDate(date);
	}

	default Date asDate(LocalDate localdate) {
		return MinorTask.asDate(localdate);
	}

	default List<Task> getProjectPathTasks(Long taskID) {
		return minortask().getProjectPathTasks(taskID, getUserID());
	}

	default void warning(String topic, String warning) {
		minortask().warning(topic, warning);
	}

	default void chat(String topic) {
		minortask().chat(topic);
	}

	default void error(String topic, String warning) {
		minortask().error(topic, warning);
	}

	default void sqlerror(Exception ex) {
		minortask().sqlerror(ex);
	}

	default public Task.TaskAndProject getTaskAndProject(Long taskID) throws MinorTask.InaccessibleTaskException {
		return minortask().getTaskAndProject(taskID);
	}

	default public Task.Project getProjectOfTask(Long taskID) throws MinorTask.InaccessibleTaskException {
		return getTaskAndProject(taskID).getProject();
	}
}
