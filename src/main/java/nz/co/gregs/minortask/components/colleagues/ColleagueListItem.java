/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.colleagues;

import nz.co.gregs.minortask.MinorTask;
import nz.co.gregs.minortask.datamodel.User;

/**
 *
 * @author gregorygraham
 */
public class ColleagueListItem {
	
	private User otherUser;
	private boolean accepted;
	private boolean canAccept;
	private Colleagues colleaguesRow;
	private boolean declined;
	private MinorTask minortask;

	public ColleagueListItem(MinorTask minortask) {
		this.minortask = minortask;
	}

	public ColleagueListItem(MinorTask minorTask, Colleagues colleagues, User requester, User invitedUser) {
		this(minorTask);
		this.colleaguesRow = colleagues;
		if (minortask.getCurrentUserID() == invitedUser.getUserID()) {
			otherUser = requester;
			canAccept = true;
		} else {
			otherUser = invitedUser;
			canAccept = false;
		}
		accepted = colleagues.acceptanceDate.isNotNull();
		declined = colleagues.denialDate.isNotNull();
		System.out.println("ColleagueListItem");
	}

	public Colleagues getColleaguesRow() {
		return colleaguesRow;
	}

	public User getOtherUser() {
		return otherUser;
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
