/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.AbstractCompositeField;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import java.time.LocalDate;

/**
 *
 * @author gregorygraham
 */
public class OptionalDatePicker extends AbstractCompositeField<Div, OptionalDatePicker, LocalDate> {

	private final DatePicker input = new DatePicker();
	private final Checkbox enabler = new Checkbox(false);
	private LocalDate previousValue = null;

	public OptionalDatePicker(String label) {
		this((LocalDate) null);
		enabler.setLabel(label);
	}

	public OptionalDatePicker(LocalDate defaultValue) {
		super(defaultValue);
		
		input.addValueChangeListener((event) -> {
			setModelValue(getValue(), true);
		});
		
		enabler.addValueChangeListener((event) -> {
			toggleDateField(event);
			setModelValue(getValue(), true);
		});

		input.setEnabled(enabler.getValue());

		final Div content = getContent();
		content.add(enabler, input);
		content.addClassName("optional-date-picker");
	}

	@Override
	protected void setPresentationValue(LocalDate newPresentationValue) {
		input.setValue(newPresentationValue);
	}

	public void setMin(LocalDate date) {
		input.setMin(date);
	}

	public void setMax(LocalDate date) {
		input.setMax(date);
	}

	private void toggleDateField(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
		final Boolean value = event.getValue();
		if (value) {
			input.setValue(previousValue);
		} else {
			previousValue = input.getValue();
			input.setValue(null);
		}
		input.setEnabled(value);
	}

	@Override
	public LocalDate getValue() {
		if (enabler.getValue()) {
			return input.getValue();
		} else {
			return null;
		}
	}

	@Override
	public void setValue(LocalDate value) {
		if (value == null) {
			enabler.setValue(Boolean.FALSE);
		} else {
			enabler.setValue(Boolean.TRUE);
		}
		super.setValue(value);
	}

	public void setDefaultValue(LocalDate suggestion) {
		this.previousValue = suggestion;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		enabler.setReadOnly(readOnly);
		input.setReadOnly(readOnly);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		enabler.setEnabled(enabled);
		input.setEnabled(enabled);
	}
}
