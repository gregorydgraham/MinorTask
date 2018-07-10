/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.templatemodel.TemplateModel;

/**
 *
 * @author gregorygraham
 */
@Tag("minortask-template")
@HtmlImport("src/minortask-template.html")
public class MinorTaskTemplate extends PolymerTemplate<MinorTaskTemplate.MinorTaskModel>{
	

    /**
     * Template model which defines the single "value" property.
     */
    public interface MinorTaskModel extends TemplateModel {}
}
