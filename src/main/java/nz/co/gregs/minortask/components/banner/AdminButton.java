/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.banner;

import com.vaadin.flow.component.Tag;
import nz.co.gregs.minortask.components.generic.SecureSpan;
import com.vaadin.flow.component.icon.VaadinIcon;
import nz.co.gregs.minortask.MinorTaskViews;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
@Tag("admin-button")
//@StyleSheet("styles/admin-button.css")
public class AdminButton extends SecureSpan implements MinorTaskEventNotifier {

	public AdminButton() {
		super();
		init_();
	}

	private void init_() {
		addClassName("admin-button");

		IconWithToolTip profile = new IconWithToolTip(VaadinIcon.COGS, "Admin", Position.BOTTOM_LEFT);
		profile.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(this, MinorTaskViews.CLUSTER, true));
//			minortask().showProfile();
		});

		add(profile);
	}

}
