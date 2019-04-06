/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.changes;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import nz.co.gregs.minortask.MinorTaskEvent;
import nz.co.gregs.minortask.MinorTaskEventNotifier;

/**
 *
 * @author gregorygraham
 */
public class ChangeDescriptionDiv extends Div implements MinorTaskEventNotifier {

	private final Changes change;

	public ChangeDescriptionDiv(Changes change) {
		this.change = change;
		this.addClickListener((event) -> {
			fireEvent(new MinorTaskEvent(this, this.change.task, true));
		});
		this.getStyle().set("cursor", "pointer");
		Div name = new Div();
		Div desc = new Div();
		if (change != null && change.task != null) {
			name = new Div(new Span(change.task.name.getValue()));
			desc = new Div(new Span(change.description.getValue()));
		}
		name.addClassName("changelist-change-taskname");
		desc.addClassName("changelist-change-description");
		this.add(name, desc);
	}

}
