/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.colleagues.Colleagues;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/colleagues-button.css")
public class ColleaguesButton extends SecureSpan {

	private Label inviteLabel;
	String defaultText = "";
	IconWithToolTip defaultIcon = new IconWithToolTip(VaadinIcon.USERS, "Your Team", Position.BOTTOM_LEFT);

	public ColleaguesButton() {
		super();
		init_(defaultText, defaultIcon);
	}

	private void init_(String text, IconWithToolTip icon) {
		addClassName("colleagues-button");
		addClickListener((event) -> {
			minortask().showColleagues();
		});
		handleText(text);
		handleIcon(icon);
		handlePendingInvites();
	}

	private void handlePendingInvites() {
		final long pending = getNumberOfPendingInvites();
		if (pending > 0) {
			inviteLabel = new Label("" + pending);
			inviteLabel.addClassName("colleague-button-invites");
			add(inviteLabel);
		}
	}

	private void handleIcon(IconWithToolTip icon) {
		icon.addClassName("colleagues-button-icon");
		add(icon);
	}

	private void handleText(String text) {
		setText(text);
	}

	public long getNumberOfPendingInvites() {
		final Colleagues colleague = new Colleagues();
		colleague.invited.permittedValues(getCurrentUserID());
		colleague.acceptanceDate.permitOnlyNull();
		colleague.denialDate.permitOnlyNull();
		DBQuery dbQuery
				= getDatabase()
						.getDBQuery()
						.add(colleague);

		System.out.println("" + dbQuery.getSQLForQuery());
		try {
			return dbQuery.count();
		} catch (SQLException | AccidentalCartesianJoinException | AccidentalBlankQueryException ex) {
			Globals.sqlerror(ex);
		}
		return 0;
	}

}
