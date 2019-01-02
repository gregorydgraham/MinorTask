/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Component;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.databases.DBDatabase;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public interface MinorTaskComponent {

	default MinorTask minortask() {
		return MinorTask.getMinorTask();
	}

	default DBDatabase getDatabase() {
		return MinorTask.getDatabase();
	}

	default Task getTask(Long taskID) throws MinorTask.InaccessibleTaskException {
		try {
			return minortask().getTask(taskID);
		} catch (Globals.InaccessibleTaskException ex) {
			error("Inaccessible Task " + taskID, ex);
			throw ex;
		}
	}

	default Long getUserID() {
		return minortask().getUserID();
	}

	default User getUser() {
		return minortask().getUser();
	}

	default LocalDate asLocalDate(Date date) {
		return MinorTask.asLocalDate(date);
	}

	default Date asDate(LocalDate localdate) {
		return MinorTask.asDate(localdate);
	}

	default List<Task> getProjectPathTasks(Long taskID) {
		return MinorTask.getProjectPathTasks(taskID, getUserID());
	}

	default void warning(String topic, String warning) {
		MinorTask.warning(topic, warning);
	}

	default void chat(String topic) {
		MinorTask.chat(topic);
	}

	default void error(String topic, String warning) {
		MinorTask.error(topic, warning);
	}

	default void error(String topic, Exception ex) {
		MinorTask.error(topic, ex);
	}

	default void sqlerror(Exception ex) {
		MinorTask.sqlerror(ex);
	}

	default public Task.TaskAndProject getTaskAndProject(Long taskID) throws MinorTask.InaccessibleTaskException {
		return minortask().getTaskAndProject(taskID);
	}

	default public Task.Project getProjectOfTask(Long taskID) throws MinorTask.InaccessibleTaskException {
		return getTaskAndProject(taskID).getProject();
	}

	default boolean isLoggedIn() {
		return minortask().isLoggedIn();
	}

	default boolean isAccessDenied(Component component) {
		return !minortask().isLoggedIn();
	}
}
