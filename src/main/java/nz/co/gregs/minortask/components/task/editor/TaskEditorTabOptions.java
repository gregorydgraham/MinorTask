/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components.task.editor;

/**
 *
 * @author gregorygraham
 */
public interface TaskEditorTabOptions {

	void showDetails();
	void showTodayForThisTask();
	void showUpcomingList();
	void showOverdueList();
	void showIdeasList();
	void showOpenList();
	void showCompletedList();
	void showSearchList();
	
}
