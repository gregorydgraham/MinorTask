/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.expressions.IntegerExpression;
import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.Colleagues;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/colleague-list.css")
public class ColleagueList extends VerticalLayout implements RequiresLogin {

	protected final Long taskID;
	private final Grid<ColleagueListItem> grid = new Grid<ColleagueListItem>();
	private final Label label = new Label();
	private List<ColleagueListItem> list = new ArrayList<>(0);

	public ColleagueList() {
		this((Long) null);
	}

	public ColleagueList(Long taskID) {
		this.taskID = taskID;
		buildComponent();
		this.setSpacing(false);
		this.addClassName("colleaguelist");
	}

	protected ColleagueList(List<ColleagueListItem> list) {
		this.taskID = null;
		this.list = list;
		buildComponent();
		this.addClassName("colleaguelist");
	}

	public final void buildComponent() {
		VerticalLayout well = new VerticalLayout();
		well.addClassName(getListClassName());
		well.setSpacing(false);
		well.addClassName("well");
		try {
			add(getControlsAbove());
			List<ColleagueListItem> allRows = getColleaguesToList();
			setLabel(allRows);
			HorizontalLayout header = new HorizontalLayout();
			header.addClassName("colleaguelist-header");
			header.add(label);
			Div headerRight = new Div();
			headerRight.addClassName("right");
			final Component[] headerExtras = getHeaderExtras();
			if (headerExtras.length > 0) {
				headerRight.add(headerExtras);
			}
			header.add(headerRight);
			well.add(header);

			setGridItems(allRows);
			setGridColumns();
			well.add(grid);

			well.add(getFooter());
		} catch (SQLException ex) {
			MinorTask.sqlerror(ex);
		}
		add(well);
	}

	protected String getListClassName() {
		return "colleague-list";
	}

	protected String getListCaption(List<ColleagueListItem> tasks) {
		return "Team Members";
	}

	protected List<ColleagueListItem> getColleaguesToList() throws SQLException {
		list.clear();
		final Colleagues colleague = new Colleagues();
		final User requesterExample = new Colleagues.RequestingUser();
		final User requestedExample = new Colleagues.RequestedUser();
		DBQuery dbQuery = getDatabase().getDBQuery()
				.add(colleague, requesterExample, requestedExample);
		dbQuery.addCondition(// at least one of the invite fields is the current user
				IntegerExpression.value(getUserID())
						.isIn(
								colleague.column(colleague.requestor),
								colleague.column(colleague.invited)
						)
		);

		System.out.println("getColleaguesToList: \n" + dbQuery.getSQLForQuery());
		List<DBQueryRow> allRows = dbQuery.getAllRows();
		allRows.forEach((row) -> {
			final User requester = row.get(requesterExample);
			final User requestee = row.get(requestedExample);
			list.add(new ColleagueListItem(row.get(colleague), requester, requestee));
		});
		return list;
	}

	protected Component[] getFooterExtras() {
		Button button = new Button("Grow Your Team");
		button.addClickListener((event) -> {
			showPotentialColleagues(event);
		});
		return new Component[]{button};
	}

	protected Component[] getHeaderExtras() {
		return new Component[]{};
	}

	private void setGridItems(List<ColleagueListItem> allRows) {
		grid.setItems();//clear it first
		grid.setItems(allRows);
	}

	private void setLabel(List<ColleagueListItem> allRows) {
		final String caption = getListCaption(allRows);
		label.setText(caption);
	}

	private void setGridColumns() {
		grid.setHeightByRows(true);
		grid.addComponentColumn((ColleagueListItem source) -> getPrefixComponent(source)).setWidth("2em").setFlexGrow(0);
		grid.addComponentColumn((ColleagueListItem source) -> getDescriptionComponent(source)).setFlexGrow(20);
		grid.addComponentColumn((ColleagueListItem source) -> getSuffixComponent(source)).setWidth("30em").setFlexGrow(0);
	}

	private Component getPrefixComponent(ColleagueListItem source) {
		final IconWithClickHandler icon = new IconWithClickHandler(VaadinIcon.USER);
		icon.addClassName("colleaguelist-entry-prefix");
		if (source.hasDeclined()) {
			icon.addClassName("colleaguelist-entry-declined");
		} else if (source.isInvited() && source.canAccept()) {
			icon.addClassName("colleaguelist-entry-canaccept");
		} else if (source.isInvited()) {
			icon.addClassName("colleaguelist-entry-invited");
		} else {
			icon.addClassName("colleaguelist-entry-accepted");
		}
		return icon;
	}

	private Component getDescriptionComponent(ColleagueListItem source) {
		Div name = new Div();
		name.setText(source.getColleague().getUsername());
		Div desc = new Div();
		desc.setText(source.getColleague().getBlurb());

		name.setSizeFull();
		name.addClassNames("colleaguelist-name");
		desc.setSizeFull();
		desc.addClassNames("colleaguelist-description");

		final Div summary = new Div(name, desc);

		return summary;
	}

	private Component getSuffixComponent(ColleagueListItem item) {
		HorizontalLayout layout = new HorizontalLayout();
		final Label statusLabel = new Label();
		statusLabel.addClassName("colleaguelist-status-label");
			layout.add(statusLabel);
		if (item.hasDeclined() && !item.canAccept) {
			statusLabel.setText("declined");
			final Button rescindButton = new Button("Remove");
			rescindButton.addClassName("colleaguelist-ejectbutton");
			rescindButton.addClickListener((event) -> {
				rescindInvitation(item);
			});
			layout.add(rescindButton);
		} else if (item.hasDeclined() && item.canAccept()) {
			statusLabel.setText("declined");
			final Button acceptButton = new Button("Accept");
			acceptButton.addClassName("colleaguelist-acceptbutton");
			acceptButton.addClickListener((event) -> {
				acceptInvitation(item);
			});
			layout.add(acceptButton);
			final Button rescindButton = new Button("Remove");
			rescindButton.addClassName("colleaguelist-ejectbutton");
			rescindButton.addClickListener((event) -> {
				rescindInvitation(item);
			});
			layout.add(rescindButton);
		} else if (item.isInvited() && item.canAccept()) {
			statusLabel.setText("invitation");
			final Button acceptButton = new Button("Accept");
			acceptButton.addClassName("colleaguelist-acceptbutton");
			acceptButton.addClickListener((event) -> {
				acceptInvitation(item);
			});
			layout.add(acceptButton);
			final Button declineButton = new Button("Decline");
			declineButton.addClassName("colleaguelist-declinebutton");
			declineButton.addClickListener((event) -> {
				declineInvitation(item);
			});
			layout.add(declineButton);
		} else if(item.isInvited()){
			statusLabel.setText("invited");
			final Button rescindButton = new Button("Rescind");
			rescindButton.addClassName("colleaguelist-rescindbutton");
			rescindButton.addClickListener((event) -> {
				rescindInvitation(item);
			});
			layout.add(rescindButton);
		} else {
			statusLabel.setText("joined");
			final Button rescindButton = new Button("Remove");
			rescindButton.addClassName("colleaguelist-ejectbutton");
			rescindButton.addClickListener((event) -> {
				rescindInvitation(item);
			});
			layout.add(rescindButton);
		}
		return layout;
	}

	protected void refreshList() {
		try {
			if (thereAreRowsToShow()) {
				List<ColleagueListItem> allRows = getColleaguesToList();
				setLabel(allRows);
				setGridItems(allRows);
			}
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	protected Component[] getControlsAbove() {
		return new Component[]{};
	}

	protected boolean thereAreRowsToShow() {
		return true;
	}

	private void showPotentialColleagues(ClickEvent<Button> startingEvent) {
		Button inviteButton = new Button("Invite");
		Button cancelButton = new Button("cancel");

		UserSelector potentialColleagueSelector = new UserSelector.PotentialColleagueSelector();

		Div contentDiv = new Div(potentialColleagueSelector);
		contentDiv.addClassName("colleague-list-content-div");

		Div buttonDiv = new Div(cancelButton, inviteButton);
		contentDiv.addClassName("colleague-list-button-div");

		Dialog dialog = new Dialog(contentDiv, buttonDiv);
		dialog.setCloseOnEsc(true);
		dialog.setCloseOnOutsideClick(true);

		cancelButton.addClickListener((e) -> {
			dialog.close();
		});
		inviteButton.addClickListener((e) -> {
			inviteNewColleague(potentialColleagueSelector.getValue());
			refreshList();
			dialog.close();
		});

		dialog.open();
	}

	private Div getFooter() {
		Div footer = new Div();
		final Component[] footerExtras = getFooterExtras();
		if (footerExtras.length > 0) {
			footer.add(footerExtras);
		}
		footer.addClassNames(getListClassName(), "footer", getListClassName() + "-footer");
		return footer;
	}

	private void inviteNewColleague(User selectedUser) {
		Colleagues colleagueInvite = new Colleagues(getUser(), selectedUser);
		try {
			getDatabase().insert(colleagueInvite);
			chat("Invitation sent...");
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void acceptInvitation(ColleagueListItem item) {
		Colleagues colleagues = item.getColleaguesRow();
		colleagues.acceptanceDate.setValue(new Date());
		colleagues.denialDate.setValueToNull();
		try {
			getDatabase().update(colleagues);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		refreshList();
	}

	private void rescindInvitation(ColleagueListItem item) {
		try {
			getDatabase().delete(item.getColleaguesRow());
			refreshList();
		} catch (SQLException ex) {
			sqlerror(ex);
		}
	}

	private void declineInvitation(ColleagueListItem item) {
		Colleagues colleagues = item.getColleaguesRow();
		colleagues.denialDate.setValue(new Date());
		try {
			getDatabase().update(colleagues);
		} catch (SQLException ex) {
			sqlerror(ex);
		}
		refreshList();
	}

	public static class ColleagueListItem implements MinorTaskComponent {

		private User colleague;
		private boolean accepted;
		private boolean canAccept;
		private Colleagues colleaguesRow;
		private boolean declined;

		public ColleagueListItem() {
		}

		public ColleagueListItem(Colleagues colleagues, User requester, User invitedUser) {
			this.colleaguesRow = colleagues;
			if (getUserID().equals(invitedUser.getUserID())) {
				colleague = requester;
				canAccept = true;
			} else {
				colleague = invitedUser;
				canAccept = false;
			}
			accepted = colleagues.acceptanceDate.isNotNull();
			declined = colleagues.denialDate.isNotNull();
			System.out.println("ColleagueListItem");
		}

		public Colleagues getColleaguesRow() {
			return colleaguesRow;
		}

		public User getColleague() {
			return colleague;
		}

		public boolean hasAcceptedInvitation() {
			return accepted && !hasDeclined();
		}

		public boolean isInvited() {
			return !accepted && !hasDeclined();
		}

		public boolean canAccept() {
			return canAccept;
		}

		public boolean hasDeclined() {
			return declined;
		}
	}

}
