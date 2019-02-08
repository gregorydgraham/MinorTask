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
@Tag("paper-textarea")
@HtmlImport("bower_components/paper-input/paper-textarea.html")
public class PaperTextArea extends AbstractSinglePropertyField<PaperTextArea, String> implements BlurNotifier<PaperTextArea>, FocusNotifier<PaperTextArea>{
	
	private PaperTextArea(String propertyName, String defaultValue, boolean acceptNullValues) {
		super(propertyName, defaultValue, acceptNullValues);
	}
	public PaperTextArea(String label) {
		this(label, "");
	} 
	public PaperTextArea(String label, String value) {
		this("value", value, false);
		setLabel(label);
	} 
	public PaperTextArea() {
		this("value", "", false);
	} 
	
	public void addClassName(String className) {
		this.getElement().getClassList().add(className);
	}

	public void setRows(int rows) {
		getElement().setProperty("rows", rows);
	}

	public int getRows() {
		return getElement().getProperty("rows", 1);
	}

	@Override
	public void setValue(String val) {
		getElement().setProperty("value", val);
	}

	@Override
	public String getValue() {
		return getElement().getProperty("value", "");
	}

	public final void setLabel(String val) {
		getElement().setProperty("label", val);
	}

	public String getLabel() {
		return getElement().getProperty("label", "");
	}

	@Override
	public void setReadOnly(boolean readonly) {
		getElement().setProperty("readonly", readonly);
	}

	@Override
	public boolean isReadOnly() {
		return getElement().getProperty("readonly", false);
	}

	public void setDisabled(boolean readonly) {
		getElement().setProperty("disabled", readonly);
	}

	public boolean isDisabled() {
		return getElement().getProperty("disabled", false);
	}
}
