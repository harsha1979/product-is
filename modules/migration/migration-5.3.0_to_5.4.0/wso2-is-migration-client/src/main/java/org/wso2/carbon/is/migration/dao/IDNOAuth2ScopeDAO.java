package org.wso2.carbon.is.migration.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.claim.metadata.mgt.dao.ClaimDialectDAO;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.is.migration.ISMigrationException;
import org.wso2.carbon.is.migration.SQLConstants;
import org.wso2.carbon.is.migration.bean.OAuth2Scope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class IDNOAuth2ScopeDAO {

    private static Log log = LogFactory.getLog(ClaimDialectDAO.class);

    private static IDNOAuth2ScopeDAO idnoAuth2ScopeDAO = new IDNOAuth2ScopeDAO();

    private IDNOAuth2ScopeDAO() {
    }

    public static IDNOAuth2ScopeDAO getInstance() {
        return idnoAuth2ScopeDAO;
    }

    public List<OAuth2Scope> getOAuth2ScopeRoles(Connection connection) throws
                                                                        ISMigrationException {

        List<OAuth2Scope> oAuth2ScopeList = new ArrayList<>();

        Statement statement = null;
        ResultSet rs = null;

        String query = SQLConstants.GET_SCOPE_ROLES;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                String scopeId = rs.getString("SCOPE_ID");
                String roleString = rs.getString("ROLES");
                String scopeKey = rs.getString("SCOPE_KEY");
                String name = rs.getString("NAME");
                String description = rs.getString("DESCRIPTION");

                oAuth2ScopeList.add(new OAuth2Scope(scopeId, roleString, scopeKey, name, description));
            }
        } catch (SQLException e) {
            throw new ISMigrationException("Error while retrieving  OAuth2Scope", e);
        } finally {
            IdentityDatabaseUtil.closeResultSet(rs);
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException var2) {
                    log.error(
                            "Database error. Could not close statement. Continuing with others. - " + var2.getMessage(),
                            var2);
                }
            }
        }
        return oAuth2ScopeList;
    }

    public void updateOAuth2ScopeBinding(List<OAuth2Scope> oAuth2ScopeList) throws
                                                                            ISMigrationException {

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement prepStmt = null;

        String query = SQLConstants.UPDATE_SCOPE_ROLES;
        try {
            prepStmt = connection.prepareStatement(query);

            for (OAuth2Scope oAuth2Scope : oAuth2ScopeList) {
                prepStmt.setString(1, oAuth2Scope.getScopeKey());
                prepStmt.setString(2, (oAuth2Scope.getDescription() == null ? oAuth2Scope.getName() : oAuth2Scope
                        .getDescription()));
                prepStmt.setString(3, oAuth2Scope.getScopeId());
                prepStmt.addBatch();
            }

            prepStmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            throw new ISMigrationException("Error while update OAuth2ScopeBinding", e);
        } finally {
            IdentityDatabaseUtil.closeStatement(prepStmt);
            IdentityDatabaseUtil.closeConnection(connection);
        }
    }
}