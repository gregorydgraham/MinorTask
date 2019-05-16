/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Task;

public class AllOpenTasksList extends AbstractTaskListOfDBQueryRow {

	public AllOpenTasksList() {
		super();
		setToolTip();
	}

	public AllOpenTasksList(Task project) {
		super(project);
		setToolTip();
	}

	private void setToolTip() {
		setTooltipText("All the tasks that are still be done");
	}

	@Override
	protected String getListClassName() {
		return "allopentaskslist";
	}

	@Override
	protected Component getListCaption(List<DBQueryRow> tasks) {
		return new Label("" + tasks.size() + " Open Tasks");
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		Task example = new Task();
		example.completionDate.permitOnlyNull();
		DBQuery query = Globals.getDatabase().getDBQuery(example).addOptional(new Task.Project());
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(minortask().getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(minortask().getCurrentUserID())
						)
		);
		if (getProjectID() == null) {
			return query.getAllRows();
		} else {
			return minortask().getDBQueryRowOfProjectFiltered(getTaskID(), (t) -> t.completionDate.getValue() == null );
		}
	}

	@Override
	public boolean checkForPermission(DBQueryRow item) {
		return minortask().checkUserCanViewTask(item.get(new Task()));
	}

}
