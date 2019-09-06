/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.polymer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import java.util.Arrays;

/**
 *
 * @author gregorygraham
 */
@Tag("details")
public class Details extends Component implements HasComponents, HasStyle {

	final Summary summary = new Summary("");

	private Details() {
		super();
		add(summary);
	}

	public Details(String text) {
		this();
		summary.setText(text);
	}

	public Details(Component... components) {
		this();
		add(components);
	}

	public Details(String summary, Component... components) {
		this(summary);
		add(components);
	}

	public void setSummary(String text) {
		summary.setText(text);
	}

	public void clearSummary() {
		summary.setText("");
	}

	@Override
	public void removeAll() {
		HasComponents.super.removeAll();
		add(summary);
	}

	@Override
	public void remove(Component... components) {
		Arrays.asList(components)
				.stream().filter((t) -> t != summary)
				.forEach((t) -> HasComponents.super.remove(t));
	}

}
