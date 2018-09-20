/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.columns.StringColumn;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.minortask.datamodel.Task;

public class SearchedTasksList extends AbstractTaskList {

	TextField searchField;

	public SearchedTasksList(Long taskID) {
		super(taskID);
		getSearchField();
	}

	private TextField getSearchField() {
		if (searchField == null) {
			searchField = new TextField();
			searchField.setPlaceholder("search terms: use +/- to improve results");
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
			System.out.println("SEARCHING NOW...");
			String[] terms = getSearchTerms();
			for (String term : terms) {
				System.out.print(" ? " + term);
			}
			System.out.println("");
			if (terms.length > 0) {
				Task example = new Task();
				example.userID.permittedValues(getUserID());
				DBQuery query = getDatabase().getDBQuery(example);
				StringColumn column = example.column(example.name);
				BooleanExpression boolExpr = null;
				boolExpr = column.searchFor(terms);
				query.addCondition(boolExpr);
				query.setSortOrder(column.searchForRanking(terms).descending());
				System.out.println(query.getSQLForQuery());
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
		HorizontalLayout layout = new HorizontalLayout(searchButton, getSearchField());
		return new Component[]{layout};
	}

	private static class NothingToSearchFor extends Exception {

		public NothingToSearchFor() {
		}
	}

}
