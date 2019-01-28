/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.datepicker.DatePicker;
import java.time.LocalDate;
import java.util.Locale;

/**
 *
 * @author gregorygraham
 */
public class SecureDatePicker extends DatePicker implements HasToolTip{

	public SecureDatePicker() {
	}

	public SecureDatePicker(LocalDate initialDate) {
		super(initialDate);
	}

	public SecureDatePicker(String label) {
		super(label);
	}

	public SecureDatePicker(String label, LocalDate initialDate) {
		super(label, initialDate);
	}

	public SecureDatePicker(ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(listener);
	}

	public SecureDatePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(label, listener);
	}

	public SecureDatePicker(LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(initialDate, listener);
	}

	public SecureDatePicker(String label, LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(label, initialDate, listener);
	}

	public SecureDatePicker(LocalDate initialDate, Locale locale) {
		super(initialDate, locale);
	}
	
}
