/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.DBTable;
import nz.co.gregs.minortask.datamodel.Task;

@Tag("active-task-list")
public class TodaysTasksList extends VerticalLayout implements RequiresLogin {


	public TodaysTasksList() {
		buildComponent();
		this.addClassName("tasklist");
	}

	public final void buildComponent() {

		VerticalLayout well = new VerticalLayout();
		well.addClassName("activetasklist");
		well.setSpacing(false);
		well.addClassName("well");
		try {

			List<Task.Project> tasks = getTasksToList();

			final String caption = tasks.size() + " Active Tasks";
			final Label label = new Label(caption);
			label.setWidth("100%");

			HorizontalLayout header = new HorizontalLayout();
			header.add(label);
			header.setWidth("100%");

			well.add(header);
			for (Task task : tasks) {
				well.add(new TaskSummary(task));
			}

			HorizontalLayout footer = new HorizontalLayout();
			footer.setWidth("100%");
			footer.addClassNames("activelist", "footer");
			well.add(footer);

		} catch (SQLException ex) {
			minortask().sqlerror(ex);
		}
		add(well);
	}

	protected List<Task.Project> getTasksToList() throws SQLException {
		Task.Project example = new Task.Project();
		example.userID.permittedValues(minortask().getUserID());
		example.completionDate.permittedValues((Date) null);
		final Task task = new Task();
		final DBQuery query = minortask().getDatabase().getDBQuery(example).addOptional(task);
		query.setSortOrder(
				example.column(example.finalDate),
				example.column(example.startDate)
		);
		// add the leaf requirement
		query.addCondition(task.column(task.taskID).isNull());
		List<Task.Project> tasks =query.getAllInstancesOf(new Task.Project());
		return tasks;
	}
}
