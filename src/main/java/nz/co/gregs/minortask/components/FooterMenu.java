/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Label;

/**
 *
 * @author gregorygraham
 */
@Tag("footer-menu")
@StyleSheet("frontend://styles/footer-menu.css")
public class FooterMenu extends Footer implements MinorTaskComponent {

	public FooterMenu() {
		addClassName("footer-menu");
		final Label label = new Label("every project is a collection of minor tasks.");
		add(label);
		final Label label2 = new Label("MinorTask simplifies your projects so you complete them successfully.");
		add(label2);
		setSizeUndefined();
	}

}
