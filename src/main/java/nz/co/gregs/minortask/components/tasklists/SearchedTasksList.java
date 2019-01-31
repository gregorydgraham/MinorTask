/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.StringExpression;
import nz.co.gregs.minortask.components.HasDefaultButton;
import nz.co.gregs.minortask.datamodel.Task;

public class SearchedTasksList extends AbstractTaskList implements HasDefaultButton {

	TextField searchField;
	Checkbox includeDescriptionOption;
	Checkbox includeCompletedTasksOption;
	private String searchFor = "";

	public SearchedTasksList() {
		super();
		getSearchField();
		setTooltipText("Search for your tasks based on their name and description");
	}

	private Checkbox getIncludeDescriptionOption() {
		if (includeDescriptionOption == null) {
			includeDescriptionOption = new Checkbox();
			includeDescriptionOption.setValue(true);
			includeDescriptionOption.setLabel("Search Description");
			includeDescriptionOption.addValueChangeListener((event) -> {
				refreshList();
			});
		}
		return includeDescriptionOption;
	}

	private Checkbox getIncludeCompletedTasksOption() {
		if (includeCompletedTasksOption == null) {
			includeCompletedTasksOption = new Checkbox();
			includeCompletedTasksOption.setValue(false);
			includeCompletedTasksOption.setLabel("Include Completed Tasks");
			includeCompletedTasksOption.addValueChangeListener((event) -> {
				refreshList();
			});
		}
		return includeCompletedTasksOption;
	}

	private TextField getSearchField() {
		if (searchField == null) {
			searchField = new TextField();
			searchField.setPlaceholder("use +/- to improve results");
			searchField.setValueChangeMode(ValueChangeMode.EAGER);
			searchField.addValueChangeListener((event) -> {
				searchFor = event.getValue();
				refreshList();
			});
			searchField.setAutofocus(true);
		}
		return searchField;
	}

	@Override
	protected String getListClassName() {
		return "searchedtaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		if (tasks.isEmpty()) {
			if (searchFor == null || searchFor.isEmpty()) {
				return "Search above";
			} else {
				return "No Results Found";
			}
		} else {
			return "Found " + tasks.size() + " Tasks for \"" + searchFor + "\"";
		}
	}

	@Override
	protected boolean thereAreRowsToShow() {
		return true;
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		try {
			String[] terms = getSearchTerms();
			if (terms.length > 0) {
				Task example = new Task();//minortask().getSafeTaskExample(this);
				DBQuery query = getQuery(example, terms);
				return query.getAllInstancesOf(example);
			} else {
				return new ArrayList<Task>();
			}
		} catch (NothingToSearchFor ex) {
			return new ArrayList<Task>();
		}
	}

	private DBQuery getQuery(Task example, String[] terms) {
		DBQuery query = getDatabase().getDBQuery(example);
		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(getCurrentUserID())
						)
		);
		StringExpression column = example.column(example.name);
		if (getIncludeDescriptionOption().getValue()) {
			column = column.append(" ").append(example.column(example.description));
		}
		if (getIncludeCompletedTasksOption().getValue() == false) {
			query.addCondition(example.column(example.completionDate).isNull());
		}
		query.addCondition(column.searchFor(terms));
		query.setSortOrder(column.searchForRanking(terms).descending());
		return query;
	}

	private String[] getSearchTerms() throws NothingToSearchFor {
		String value = getSearchField().getValue();
		return getSearchTerms(value);
	}

	private String[] getSearchTerms(String value) throws NothingToSearchFor {
		if (value != null) {
			String[] split = value.split(" ");
			List<String> results = new ArrayList<>(0);
			for (String string : split) {
				if (!string.isEmpty()) {
					results.add(string);
				}
			}
			return results.toArray(new String[]{});
		} else {
			throw new NothingToSearchFor();
		}
	}

	@Override
	protected Component[] getControlsAbove() {
		Button searchButton = new Button("Search");
		searchButton.addClickListener((event) -> {
			refreshList();
		});
		setAsDefaultButton(
				searchButton,
				(event) -> refreshList()
		);
		HorizontalLayout controls = new HorizontalLayout(new Component[]{
			getIncludeDescriptionOption(),
			getIncludeCompletedTasksOption(), searchButton
		});
		controls.setSpacing(false);
		return new Component[]{getSearchField(), controls};
	}

	private static class NothingToSearchFor extends Exception {

		public NothingToSearchFor() {
		}
	}

}
