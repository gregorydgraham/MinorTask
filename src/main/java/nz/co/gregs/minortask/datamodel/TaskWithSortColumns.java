/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.datamodel;

import nz.co.gregs.dbvolution.annotations.DBColumn;
import nz.co.gregs.dbvolution.datatypes.DBBoolean;
import nz.co.gregs.dbvolution.expressions.DateExpression;
import nz.co.gregs.minortask.datamodel.Task;

/**
 *
 * @author gregorygraham
 */
public class TaskWithSortColumns extends Task {
	
	@DBColumn
	public DBBoolean hasStarted = new DBBoolean(this.column(this.startDate).isLessThan(DateExpression.currentDate()));
	@DBColumn
	public DBBoolean isOverdue = new DBBoolean(this.column(this.finalDate).isLessThan(DateExpression.currentDate()));
	{
		this.hasStarted.setSortOrderDescending();
		this.isOverdue.setSortOrderDescending();
		this.startDate.setSortOrderAscending();
		this.preferredDate.setSortOrderAscending();
		this.finalDate.setSortOrderAscending();
	}
	
}
