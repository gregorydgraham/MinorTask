/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.colleagues;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
//import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
//import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.DBQueryRow;
import nz.co.gregs.dbvolution.expressions.IntegerExpression;
import nz.co.gregs.minortask.components.banner.IconWithToolTip;
import nz.co.gregs.minortask.components.RequiresLogin;
import nz.co.gregs.minortask.components.generic.SecureDiv;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import nz.co.gregs.minortask.components.UserSelector;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/colleague-list.css")
public class ColleagueList extends SecureDiv implements RequiresLogin {

	protected final Long taskID;
	private final Label label = new Label();
	private final Div gridDiv = new Div();
	private List<ColleagueListItem> list = new ArrayList<>(0);

	public ColleagueList() {
		this((Long) null);
	}

	public ColleagueList(Long taskID) {
		this.taskID = taskID;
		_init();
	}

	protected ColleagueList(List<ColleagueListItem> list) {
		this.taskID = null;
		this.list = list;
		_init();
	}

	private void _init() {
		this.addClassName("colleaguelist");
		buildComponent();
		refreshList();
	}

	public final void buildComponent() {
		SecureSpan well = new SecureSpan();
		well.addClassName(getListClassName());
		well.addClassName("well");
		add(getControlsAbove());
		SecureSpan header = new SecureSpan();
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

		gridDiv.addClassName("colleaguelist-grid");
		well.add(gridDiv);

		well.add(getFooter());
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
		final Colleagues colleagueExample = new Colleagues();
		final User requesterExample = new Colleagues.RequestingUser();
		final User invitedExample = new Colleagues.InvitedUser();
		DBQuery dbQuery = getDatabase().getDBQuery(colleagueExample, requesterExample, invitedExample);
		dbQuery.addCondition(// at least one of the invite fields is the current user
				IntegerExpression.value(getCurrentUserID())
						.isIn(
								requesterExample.column(requesterExample.queryUserID()),
								invitedExample.column(invitedExample.queryUserID())
						)
		);

		System.out.println("getColleaguesToList: \n" + dbQuery.getSQLForQuery());
		List<DBQueryRow> allRows = dbQuery.getAllRows();
		for (DBQueryRow row : allRows) {
			Colleagues colleagueRow = row.get(new Colleagues());
			User requester = row.get(new Colleagues.RequestingUser());
			User invited = row.get(new Colleagues.InvitedUser());
			System.out.println(colleagueRow);
			System.out.println(requester);
			System.out.println(invited);
			list.add(new ColleagueListItem(minortask(), colleagueRow, requester, invited));
		}
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
		gridDiv.removeAll();
		allRows
				.forEach((t) -> {
					Div div = new Div();
					div.add(
							getPrefixComponent(t),
							getDescriptionComponent(t),
							getSuffixComponent(t)
					);
					div.addClassName("colleaguelist-entry");
					gridDiv.add(div);
				});
	}

	private void setLabel(List<ColleagueListItem> allRows) {
		final String caption = getListCaption(allRows);
		label.setText(caption);
	}

	private Component getPrefixComponent(ColleagueListItem source) {
		final IconWithToolTip icon = new IconWithToolTip(VaadinIcon.USER, source.getOtherUser().getUsername());
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
		SecureSpan name = new SecureSpan();
		name.setText(source.getOtherUser().getUsername());
		SecureSpan desc = new SecureSpan();
		desc.setText(source.getOtherUser().getBlurb());

		name.setSizeFull();
		name.addClassNames("colleaguelist-name");
		desc.setSizeFull();
		desc.addClassNames("colleaguelist-description");

		final SecureSpan summary = new SecureSpan(name, desc);
		summary.addClassName("colleaguelist-entry-summary");

		return summary;
	}

	private Component getSuffixComponent(ColleagueListItem item) {
		SecureSpan layout = new SecureSpan();
		layout.addClassName("colleaguelist-entry-suffix");
		final Label statusLabel = new Label();
		statusLabel.addClassName("colleaguelist-status-label");
		layout.add(statusLabel);
//		layout.add(new Div(
//				new Label(item.canAccept() ? "Acceptable" : ""),
//				new Label(item.hasAcceptedInvitation() ? "Accepted" : ""),
//				new Label(item.hasDeclined() ? "Declined" : ""),
//				new Label(item.isInvited() ? "Invited" : ""),
//				new Label(item.getOtherUser().getUsername())
//		));
		if (item.hasDeclined() && !item.canAccept()) {
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
		} else if (item.isInvited()) {
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

	protected final void refreshList() {
		try {
			List<ColleagueListItem> allRows = new ArrayList<>();
			if (thereAreRowsToShow()) {
				allRows = getColleaguesToList();
			}
			setLabel(allRows);
			setGridItems(allRows);
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

		Div contntDiv = new Div(potentialColleagueSelector);
		contntDiv.addClassName("colleague-list-content-div");

		Div buttonDiv = new Div(cancelButton, inviteButton);
		contntDiv.addClassName("colleague-list-button-div");

		Dialog dialog = new Dialog(contntDiv, buttonDiv);
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
		Colleagues colleagueInvite = new Colleagues(getCurrentUser(), selectedUser);
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
}
