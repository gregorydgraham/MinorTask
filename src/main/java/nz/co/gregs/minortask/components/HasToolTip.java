/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

/**
 *
 * @author gregorygraham
 */
@StyleSheet("styles/tooltip.css")
public interface HasToolTip extends HasStyle {

	static final String TOOLTIPTEXT_CLASS = "tooltiptext";
	static final String TOOLTIPPOSN_PREFIX = "tooltipposn-";

	default public void setTooltipText(String text) {
		setTooltipText(text, Position.BOTTOM_CENTRE);
	}

	default public void setTooltipText(String text, Position posn) {
		getElement()
				.getChildren()
				.filter((t) -> {return t.getClassList().contains(TOOLTIPTEXT_CLASS);})
				.forEach((t) -> {getElement().removeChild(t);});
		this.addClassName("tooltip");
		Div span = new Div(new Paragraph(text));
		getElement().insertChild(0, span.getElement());
		span.addClassName(TOOLTIPTEXT_CLASS);
		span.addClassName(posn.getClassName());
	}

	default public void setToolTipPosition(Position posn) {
		getElement()
				.getChildren()
				.filter((t) -> {
					return t.getClassList().contains(TOOLTIPTEXT_CLASS);
				})
				.forEach((t) -> {
					t.getClassList().remove(Position.BOTTOM_CENTRE.getClassName());
					t.getClassList().remove(Position.BOTTOM_LEFT.getClassName());
					t.getClassList().remove(Position.BOTTOM_RIGHT.getClassName());
					t.getClassList().add(posn.getClassName());
				});
		;
	}

	public static enum Position {

		BOTTOM_LEFT,
		BOTTOM_CENTRE,
		BOTTOM_RIGHT;

		private Position() {
		}

		public String getClassName() {
			return TOOLTIPPOSN_PREFIX + this.name().toLowerCase().replaceAll("_", "-");
		}

	}
}
