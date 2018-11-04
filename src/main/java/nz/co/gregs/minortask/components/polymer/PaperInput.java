/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.polymer;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

/**
 *
 * @author gregorygraham
 */
@Tag("paper-input")
@HtmlImport("bower_components/paper-input/paper-input.html")
public class PaperInput extends AbstractSinglePropertyField<PaperInput, String> implements BlurNotifier<PaperInput>, FocusNotifier<PaperInput>{
	
	private PaperInput(String propertyName, String defaultValue, boolean acceptNullValues) {
		super(propertyName, defaultValue, acceptNullValues);
	}
	public PaperInput() {
		this("value", "", false);
	}
	
	public void addClassName(String className) {
		this.getElement().getClassList().add(className);
	}
}
