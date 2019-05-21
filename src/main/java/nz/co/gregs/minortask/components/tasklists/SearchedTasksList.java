/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.tasklists;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.sql.SQLException;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.expressions.search.SearchAcross;
import nz.co.gregs.minortask.components.HasDefaultButton;
import nz.co.gregs.minortask.datamodel.Task;

@StyleSheet("styles/search-task-list.css")
public class SearchedTasksList extends AbstractTaskListOfDBQueryRow implements HasDefaultButton {

	TextField searchField;
	Checkbox includeDescriptionOption;
	Checkbox includeCompletedTasksOption;
	private String searchFor = "";

	public SearchedTasksList() {
		super();
		getSearchField();
		setTooltipText("Search for your tasks based on their name and description. Use quotes to group terms, + to prioritise terms, - to reduce terms, and name: and desc: to limit the term to only the name or description respectively.  For example: 'improve \"too many\" -exercise +robert name:stretch'");
	}

	@Override
	public Component getDefaultLabel() {
		return new Label("Search Above");
	}

	private Checkbox getIncludeDescriptionOption() {
		if (includeDescriptionOption == null) {
			includeDescriptionOption = new Checkbox();
			includeDescriptionOption.setValue(true);
			includeDescriptionOption.setLabel("Search Description");
			includeDescriptionOption.addValueChangeListener((event) -> {
				refresh();
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
				refresh();
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
				refresh();
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
	protected Component getListCaption(List<DBQueryRow> tasks) {
		if (tasks.isEmpty()) {
			if (searchFor == null || searchFor.isEmpty()) {
				return new Label("Search above");
			} else {
				return new Label("No Results Found");
			}
		} else {
			return new Span(new Span("Found " + tasks.size() + " Tasks for "), new Emphasis(searchFor));
		}
	}

	@Override
	protected boolean thereAreRowsToShow() {
		return true;
	}

	@Override
	protected List<DBQueryRow> getTasksToList() throws SQLException {
		DBQuery query = getQuery();
		return query.getAllRows();
	}

	private DBQuery getQuery() {
		Task example = new Task();
		DBQuery query = getDatabase().getDBQuery(example).addOptional(new Task.Project());

		// add user requirement
		query.addCondition(
				example.column(example.userID).is(getCurrentUserID())
						.or(
								example.column(example.assigneeID).is(getCurrentUserID())
						)
		);

		SearchAcross terms = SearchAcross
				.searchFor(getSearchField().getValue())
				.addSearchColumn(example.column(example.name), "name");

		if (getIncludeDescriptionOption().getValue()) {
			terms.addSearchColumn(example.column(example.description), "desc");
		}

		if (getIncludeCompletedTasksOption().getValue() == false) {
			query.addCondition(example.column(example.completionDate).isNull());
		}

		query.addCondition(terms);
		query.setSortOrder(terms.descending(), example.column(example.name).ascending());

		return query;
	}

	@Override
	protected Component[] getControlsAbove() {
		Button searchButton = new Button("Search");
		searchButton.addClickListener((event) -> {
			refresh();
		});
		setAsDefaultButton(
				searchButton,
				(event) -> refresh()
		);
		HorizontalLayout controls = new HorizontalLayout(new Component[]{
			new Span(getIncludeDescriptionOption()),
			new Span(getIncludeCompletedTasksOption()),
			searchButton
		});
		controls.setSpacing(false);
		return new Component[]{getSearchField(), controls};
	}

	@Override
	protected Component[] getFooterExtras() {
		final Paragraph summary = new Paragraph("Search for your tasks based on their name and description. ");
		final Paragraph example = new Paragraph(
				new Span("For example: "),
				new Emphasis("improve \"too many\" -exercise +vibrant name:stretch"));
		final Paragraph explainExample = new Paragraph(
				new Span("The above example will search for tasks containing 'improve', 'too many', and 'vibrant' with a name containing 'stretch'. Results with 'vibrant' will be placed higher in the list and those with 'exercise' will be lower")
		);
		final Paragraph descriptionOfTerms = new Paragraph("Use quotes to group terms, + to prioritise terms, - to reduce terms, and name: and desc: to limit the term to only the name or description respectively.  ");
		final Div div = new Div(summary, example, explainExample, descriptionOfTerms);
		div.setClassName("explanation");
		return new Component[]{div};
	}

}
