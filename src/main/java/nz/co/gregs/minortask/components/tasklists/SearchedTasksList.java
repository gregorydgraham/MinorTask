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
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.dbvolution.expressions.StringExpression;
import nz.co.gregs.minortask.components.HasDefaultButton;
import nz.co.gregs.minortask.datamodel.Task;

public class SearchedTasksList extends AbstractTaskList implements HasDefaultButton {

	TextField searchField;
	Checkbox includeDescriptionOption;
	Checkbox includeCompletedTasksOption;

	public SearchedTasksList(Long taskID) {
		super(taskID);
		getSearchField();
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
			searchField.setValueChangeMode(ValueChangeMode.ON_CHANGE);
			searchField.addValueChangeListener((event) -> {
				refreshList();
			});
		}
		return searchField;
	}

	@Override
	protected String getListClassName() {
		return "searchedtaskslist";
	}

	@Override
	protected String getListCaption(List<Task> tasks) {
		return "Found " + tasks.size() + " Tasks";
	}

	@Override
	protected List<Task> getTasksToList() throws SQLException {
		try {
			String[] terms = getSearchTerms();
			if (terms.length > 0) {
				Task example = minortask().getSafeTaskExample(this);
				DBQuery query = getDatabase().getDBQuery(example);
				StringExpression column = example.column(example.name);
				BooleanExpression boolExpr = null;
				boolExpr = column.searchFor(terms);

				if (getIncludeDescriptionOption().getValue()) {
					column = column.append(" ").append(example.column(example.description));
				}
				if (getIncludeCompletedTasksOption().getValue() == false) {
					query.addCondition(example.column(example.completionDate).isNull());
				}
				query.addCondition(column.searchFor(terms));
				query.setSortOrder(column.searchForRanking(terms).descending());

				return query.getAllInstancesOf(example);
			} else {
				return new ArrayList<Task>();
			}
		} catch (NothingToSearchFor ex) {
			return new ArrayList<Task>();
		}
	}

	private String[] getSearchTerms() throws NothingToSearchFor {
		String value = getSearchField().getValue();
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
		setAsDefaultButton(
				searchButton,
				(event) -> refreshList(),
				(event) -> refreshList()
		);
		HorizontalLayout layout = new HorizontalLayout(new Component[]{
			searchButton,
			getSearchField(),
			getIncludeDescriptionOption(),
			getIncludeCompletedTasksOption()
		});
		return new Component[]{layout};
	}

	private static class NothingToSearchFor extends Exception {

		public NothingToSearchFor() {
		}
	}

}
