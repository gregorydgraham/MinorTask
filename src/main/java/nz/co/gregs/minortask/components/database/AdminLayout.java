/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.database;

import nz.co.gregs.minortask.Globals;
import nz.co.gregs.minortask.components.generic.SecureDiv;

/**
 *
 * @author gregorygraham
 */
public class AdminLayout extends SecureDiv{
	
	public AdminLayout(){
		this.add(new DatabaseComponent(Globals.getDatabase()));
		this.add(new ClusterMonitorComponent());
	}
	
}
