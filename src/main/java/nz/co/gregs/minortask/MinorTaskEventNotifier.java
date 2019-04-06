/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.shared.Registration;

/**
 *
 * @author gregorygraham
 */
public interface MinorTaskEventNotifier{

    default Registration addMinorTaskEventListener(
           // ComponentEventListener<TaskMoveEvent<T>> listener) {
				MinorTaskEventListener listener) {
        if (this instanceof Component) {
            return ComponentUtil.addListener((Component) this, MinorTaskEvent.class, listener);
        } else {
            throw new IllegalStateException(String.format(
                    "The class '%s' doesn't extend '%s'. "
                            + "Make your implementation for the method '%s'.",
                    getClass().getName(), Component.class.getSimpleName(),
                    "addClickListener"));
        }
    }
}
