/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.AbstractSinglePropertyField;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;

/**
 *
 * @author gregorygraham
 */
@Tag("paper-input")
@HtmlImport("bower_components/paper-input/paper-input.html")
public class PaperInput extends AbstractSinglePropertyField<PaperInput, String> implements BlurNotifier<PaperInput>{
	
//	private static final PropertyDescriptor<String, String> VALUE_PROPERTY = PropertyDescriptors.propertyWithDefault("value", "");

	private PaperInput(String propertyName, String defaultValue, boolean acceptNullValues) {
		super(propertyName, defaultValue, acceptNullValues);
	}
	public PaperInput() {
		this("value", "", false);
	}
	
//	public void setValue(String value){
//		VALUE_PROPERTY.set(getElement(), value);
//	}
//	
//	public String getValue(){
//		String value = VALUE_PROPERTY.get(getElement());
//		return value;
//	}
}
