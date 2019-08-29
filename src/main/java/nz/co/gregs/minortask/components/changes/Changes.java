/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.changes;

import java.util.List;
import java.util.stream.Collectors;
import nz.co.gregs.dbvolution.DBRow;
import nz.co.gregs.dbvolution.annotations.AutoFillDuringQueryIfPossible;
import nz.co.gregs.dbvolution.annotations.DBAutoIncrement;
import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.annotations.DBForeignKey;
import nz.co.gregs.dbvolution.annotations.DBPrimaryKey;
import nz.co.gregs.dbvolution.annotations.DBRequiredTable;
import nz.co.gregs.dbvolution.datatypes.DBDate;
import nz.co.gregs.dbvolution.datatypes.DBInteger;
import nz.co.gregs.dbvolution.datatypes.DBString;
import nz.co.gregs.dbvolution.datatypes.QueryableDatatype;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.dbvolution.internal.properties.PropertyWrapper;
import nz.co.gregs.minortask.components.upload.Document;
import nz.co.gregs.minortask.datamodel.Task;
import nz.co.gregs.minortask.datamodel.User;
import nz.co.gregs.minortask.place.Place;
import nz.co.gregs.minortask.weblinks.Weblink;

/**
 *
 * @author gregorygraham
 */
@DBRequiredTable
public class Changes extends DBRow {

	public static DBRow[] getChanges(User user, Task task) {
		List<PropertyWrapper> props = task.getColumnPropertyWrappers();
		List<Changes> list = props.stream()
				.filter((t) -> {
					final QueryableDatatype<?> qdt = t.getQueryableDatatype();
					return qdt.hasChanged()
							&& (qdt.getPreviousValue() != qdt.getValue());
				})
				.map((t) -> {
			final QueryableDatatype<?> qdt = t.getQueryableDatatype();
					return new Changes(
							user,
							task,
							t.javaName(),
							"" + qdt.getPreviousValue(),
							"" + qdt.getValue(),
							describeChange(t));
				})
				.collect(Collectors.toList());
		return list.toArray(new DBRow[]{});
	}

	private static String describeChange(PropertyWrapper t) {
		final String javaName = t.javaName();
		final QueryableDatatype<?> qdt = t.getQueryableDatatype();
		final Object newValue = qdt.getValue();
		final Object oldValue = qdt.getPreviousValue();
		if (qdt.isLargeObject()) {
			return javaName + " was updated.";
		} else if (oldValue == null) {
			return javaName + " set to \"" + newValue
					+ "'\"";
		} else if (newValue == null) {
			return javaName + " has been cleared (was \"" + oldValue + "\")";
		} else {
			return javaName
					+ " changed to \"" + newValue
					+ "'\" from \"" + oldValue + "\"";
		}
	}

	@DBAutoIncrement
	@DBPrimaryKey
	@DBColumn
	DBInteger changeID = new DBInteger();

	@DBColumn
	@DBForeignKey(User.class)
	DBInteger userid = new DBInteger();

	@DBColumn
	@DBForeignKey(Task.class)
	DBInteger taskid = new DBInteger();

	@AutoFillDuringQueryIfPossible
	Task task;

	@DBColumn
	@DBForeignKey(Document.class)
	DBInteger docid = new DBInteger();

	@DBColumn
	@DBForeignKey(Place.class)
	DBInteger locationid = new DBInteger();

	@DBColumn
	@DBForeignKey(Weblink.class)
	DBInteger weblinkid = new DBInteger();

	@DBColumn
	DBString field = new DBString();

	@DBColumn
	DBString oldValue = new DBString();

	@DBColumn
	DBString newValue = new DBString();

	@DBColumn
	DBString description = new DBString();

	@DBColumn
	public final DBDate createdDate = new DBDate()
			.setDefaultInsertValue(DateExpression.currentDate());

	@DBColumn
	public final DBDate modifiedDate = new DBDate()
			.setDefaultInsertValue(DateExpression.currentDate())
			.setDefaultUpdateValue(DateExpression.currentDate());

	public Changes() {
	}

	public Changes(User user, Task task, String javaName, String oldValue, String newValue, String desc) {
		this.userid.setValue(user.getUserID());
		this.taskid.setValue(task.taskID.getValue());
		this.field.setValue(javaName);
		this.oldValue.setValue(oldValue);
		this.newValue.setValue(newValue);
		this.description.setValue(desc);
	}

	public Changes(User user, Document doc) {
		this.userid.setValue(user.getUserID());
		this.docid.setValue(doc.documentID.getValue());
		this.description.setValue("Document added: " + doc.filename.getValue());
	}

	public Changes(User user, Task task) {
		this.userid.setValue(user.getUserID());
		this.taskid.setValue(task.taskID.getValue());
		this.description.setValue("Created Task: " + task.name.getValue());
	}

	public Changes(User user, Task task, String description) {
		this.userid.setValue(user.getUserID());
		this.taskid.setValue(task.taskID.getValue());
		this.description.setValue(description);
	}

	public Changes(User user, Place location) {
		this.userid.setValue(user.getUserID());
		this.locationid.setValue(location.locationID.getValue());
		this.description.setValue("Created Place: " + location.displayName);
	}

	public Changes(User user, Task task, Weblink weblink) {
		this.userid.setValue(user.getUserID());
		this.taskid.setValue(task.taskID.getValue());
		this.description.setValue("Added web bookmark " + weblink.description.getValue() + " to " + task.taskID.getValue());
	}

}
