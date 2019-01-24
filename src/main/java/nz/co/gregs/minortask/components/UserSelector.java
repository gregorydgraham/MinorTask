/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nz.co.gregs.minortask.components;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import nz.co.gregs.dbvolution.DBQuery;
import nz.co.gregs.dbvolution.expressions.BooleanExpression;
import nz.co.gregs.dbvolution.expressions.IntegerExpression;
import nz.co.gregs.minortask.datamodel.Colleagues;
import nz.co.gregs.minortask.datamodel.User;

public class UserSelector extends ComboBox<User> implements RequiresLogin, MinorTaskComponent {

	public UserSelector() {
		setDataProvider(new UserProvider());
	}

	public UserSelector(AbstractUserDataProvider provider) {
		setDataProvider(provider);
		setItemLabelGenerator((item) -> {
			return item.getUsername();
		});
	}
	
	public static class ColleagueSelector extends UserSelector{
		public ColleagueSelector(){
			super(new ColleagueProvider());
		}
	}
	
	public static class PotentialColleagueSelector extends UserSelector{
		public PotentialColleagueSelector(){
			super(new PotentialColleagueProvider());
		}
	}

	protected static abstract class AbstractUserDataProvider extends AbstractBackEndDataProvider<User, BooleanExpression> implements MinorTaskComponent {

		public AbstractUserDataProvider() {
			super();
		}

		public abstract DBQuery getDBQuery(User example, Query<User, BooleanExpression> query);

		@Override
		public Object getId(User item) {
			return item.getUserID();
		}

		@Override
		protected int sizeInBackEnd(Query<User, BooleanExpression> query) {
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				System.out.println("" + dbquery.getSQLForQuery());
				return dbquery.count().intValue();
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return 0;
		}

		@Override
		public Stream<User> fetchFromBackEnd(Query<User, BooleanExpression> query) {
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				System.out.println("" + dbquery.getSQLForQuery());
				List<User> listOfUsers = dbquery.getAllInstancesOf(example);
				return listOfUsers.stream();
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return new ArrayList<User>().stream();
		}
	}

	protected static class UserProvider extends AbstractUserDataProvider {

		public UserProvider() {
			super();
		}

		@Override
		public DBQuery getDBQuery(User example, Query<User, BooleanExpression> query) {
			final DBQuery dbquery = getDatabase()
					.getDBQuery(new User())
					.setBlankQueryAllowed(true);
			query.getFilter().ifPresent((t) -> {
				dbquery.addCondition(t);
			});
			return dbquery;
		}

		@Override
		public Stream<User> fetchFromBackEnd(Query<User, BooleanExpression> query) {
			return super.fetchFromBackEnd(query);
		}

		@Override
		protected int sizeInBackEnd(Query<User, BooleanExpression> query) {
			return super.sizeInBackEnd(query);
		}
	}

	public static class PotentialColleagueProvider extends UserProvider {

		private final User user;

		public PotentialColleagueProvider() {
			user = getUser();
		}
		
		public PotentialColleagueProvider(User currentUser) {
			user = currentUser;
		}

		@Override
		public Stream<User> fetchFromBackEnd(Query<User, BooleanExpression> query) {
			ArrayList<User> listOfColleagues = new ArrayList<User>();
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				List<User> listOfUsers = dbquery.getAllInstancesOf(example);
				listOfUsers.forEach((t) -> {
					listOfColleagues.add(t);
				});
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return listOfColleagues.stream();
		}

		@Override
		public int sizeInBackEnd(Query<User, BooleanExpression> query) {
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				dbquery.count();
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return 0;
		}

		@Override
		public DBQuery getDBQuery(User example, Query<User, BooleanExpression> query) {
			Colleagues colleagues = new Colleagues();
			final User exampleUser = new User();
			exampleUser.queryUserID().excludedValues(user.getUserID());
			colleagues.ignoreAllForeignKeys();
			final DBQuery dbquery = getDatabase().getDBQuery(exampleUser).addOptional(colleagues);
			dbquery.addCondition(// connect the user table and the colleagues table
					exampleUser.column(exampleUser.queryUserID())
							.isIn(
									colleagues.column(colleagues.requestor),
									colleagues.column(colleagues.invited))
							.and(
									IntegerExpression.value(user.getUserID()) // this needs to be added as part of the FK
											.isIn(// and make sure we're looking for colleagues of the current user
													colleagues.column(colleagues.requestor),
													colleagues.column(colleagues.invited))));
			dbquery.addCondition( // But we only want the outer rows where we don't find a connection
					colleagues.column(colleagues.requestor).isNull()
			);
			System.out.println("" + dbquery.getSQLForQuery());
			return dbquery;
		}
	}

	public static class ColleagueProvider extends UserProvider {

		private final User user;

		public ColleagueProvider() {
			user = getUser();
		}
		public ColleagueProvider(User currentUser) {
			user = currentUser;
		}

		@Override
		public Stream<User> fetchFromBackEnd(Query<User, BooleanExpression> query) {
			ArrayList<User> listOfColleagues = new ArrayList<User>();
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				List<User> listOfUsers = dbquery.getAllInstancesOf(example);
				listOfUsers.forEach((t) -> {
					listOfColleagues.add(t);
				});
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return listOfColleagues.stream();
		}

		@Override
		public int sizeInBackEnd(Query<User, BooleanExpression> query) {
			try {
				User example = new User();
				example.queryUsername().setSortOrderAscending();
				DBQuery dbquery = getDBQuery(example, query);
				dbquery.count();
			} catch (SQLException ex) {
				Logger.getLogger(UserSelector.class.getName()).log(Level.SEVERE, null, ex);
			}
			return 0;
		}

		@Override
		public DBQuery getDBQuery(User example, Query<User, BooleanExpression> query) {
			Colleagues colleagues = new Colleagues();
			colleagues.acceptanceDate.permitOnlyNotNull();
			final User exampleUser = new User();
			exampleUser.queryUserID().excludedValues(user.getUserID());
			colleagues.ignoreAllForeignKeys();
			final DBQuery dbquery = getDatabase().getDBQuery(exampleUser, colleagues);
			dbquery.addCondition(// connect the user table and the colleagues table
					exampleUser.column(exampleUser.queryUserID())
							.isIn(
									colleagues.column(colleagues.requestor),
									colleagues.column(colleagues.invited))
							.and(
									IntegerExpression.value(user.getUserID()) // this needs to be added as part of the FK
											.isIn(// and make sure we're looking for colleagues of the current user
													colleagues.column(colleagues.requestor),
													colleagues.column(colleagues.invited))));
			System.out.println("" + dbquery.getSQLForQuery());
			return dbquery;
		}
	}
}
