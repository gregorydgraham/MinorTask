/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import java.sql.SQLException;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.exceptions.AccidentalBlankQueryException;
import nz.co.gregs.dbvolution.exceptions.AccidentalCartesianJoinException;
import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.datamodel.Colleagues;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/colleagues-button.css")
public class ColleaguesButton extends Button implements MinorTaskComponent {

	private Label inviteLabel;
	String defaultText = "";
	Icon defaultIcon = new Icon(VaadinIcon.USERS);

	public ColleaguesButton(Long invites) {
		super();
		init_(defaultText, defaultIcon);
	}

	public ColleaguesButton() {
		super();
		init_(defaultText, defaultIcon);
	}

	public ColleaguesButton(String text) {
		super(new Label(text));
		init_(text, defaultIcon);
	}

	public ColleaguesButton(Icon icon) {
		super(icon);
		init_(defaultText, icon);
	}

	public ColleaguesButton(String text, Icon icon) {
		super(text, icon);
		init_(text, icon);
	}

	private void init_(String text, Icon icon) {
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
			addToSuffix(inviteLabel);
		}
	}

	private void handleIcon(Icon icon) {
		icon.addClassName("colleagues-button-icon");
		setIcon(icon);

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
