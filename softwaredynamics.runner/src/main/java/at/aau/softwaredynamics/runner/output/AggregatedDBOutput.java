package at.aau.softwaredynamics.runner.output;


import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.NodeDependency;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;

/**
 * Supports writing the aggregated dependencies of a single commit
 * (no diffing!) to a db.
 */
public class AggregatedDBOutput extends DBOutput {

    public final String SEPARATOR = ",";
    public final String SCHEMANAME = "aggregated_schema";
    public final String PROJECTTABLENAME = SCHEMANAME + ".project";
    public final String DEPENDENCYINREVISIONTABLENAME = SCHEMANAME + ".aggregated_dependencies_in_revision";

    public AggregatedDBOutput(String connectionUrl, String user, String password) {
        super(connectionUrl, user, password);
    }

    @Override
    public String getSeparator() {
        return SEPARATOR;
    }

    @Override
    public void writeToDefaultOutput(String statement) {
        writeToOutputIdentifier(DEPENDENCYTABLENAME, statement);
    }

    @Override
    public void writeToOutputIdentifier(String tableName, String statement) {

        try (Connection connection = connectToDatabase()) {

            // When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within
            // the class path. Note that your application must manually load any JDBC drivers prior to version 4.0.
//          Class.forName("org.postgresql.Driver");

            System.out.println("Connected to PostgreSQL database!");
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(statement);

        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }

    public void writeDependencyInformation(Collection<DependencyChanges> dependencyChanges, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException {
        // we do not support diffing, so only one commit can be supplied
        assert dstCommit == null;

        HashMap<Pair<String, String>, Integer> filenamesToRevisionID = new HashMap<>();

        StringBuilder queryDependenciesInRevision = new StringBuilder("INSERT INTO " + DEPENDENCYINREVISIONTABLENAME + "\n" +
                "(id, commithash, calls, reads, writes, interfaces, inheritances, types, casts, project_id, time) " +
                "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
                "ON CONFLICT DO NOTHING;");

        try (
                Connection connection = connectToDatabase();
                PreparedStatement pst = connection.prepareStatement(queryDependenciesInRevision.toString())
        ) {
            int calls = 0, reads = 0, writes = 0, interfaces = 0, inheritances = 0, types = 0, casts = 0;
            for (DependencyChanges fileDependencyChange : dependencyChanges) {
                if (fileDependencyChange == null) continue;

                for (NodeDependency nodeDependency : fileDependencyChange.getAllUnchangedNodeDependenciesSource()) {
                    switch (nodeDependency.getDependency().getType()) {
                        case CALL:
                            calls++;
                            break;
                        case READ:
                            reads++;
                            break;
                        case WRITE:
                            writes++;
                            break;
                        case INTERFACE:
                            interfaces++;
                            break;
                        case INHERITANCE:
                            inheritances++;
                            break;
                        case TYPE:
                            types++;
                            break;
                        case CAST:
                            casts++;
                            break;
                    }
                }

            }
            pst.setString(1, srcCommit.getName());
            pst.setInt(2, calls);
            pst.setInt(3, reads);
            pst.setInt(4, writes);
            pst.setInt(5, interfaces);
            pst.setInt(6, inheritances);
            pst.setInt(7, types);
            pst.setInt(8, casts);
            pst.setInt(9, getCurrentProjectIDinDatabase());
            pst.setTimestamp(10, new Timestamp(srcCommit.getAuthorIdent().getWhen().getTime()));

            pst.execute(); // Execute query
        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
            throw e;
        }

    }

    private int writeProjectIntoDB(String project, String module, long runInitiatedTimestamp) {
        int retVal = -1;
        StringBuilder query = new StringBuilder(
                "INSERT INTO " + PROJECTTABLENAME + "(\n" +
                        "  project_name           ,\n" +
                        "  module           ,\n" +
                        "  run_initiated         \n" +
                        ") VALUES ( ?,?,?)");

        try (
                Connection connection = connectToDatabase();
                PreparedStatement pst = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, project);
            pst.setString(2, module);
            pst.setLong(3, runInitiatedTimestamp);

            pst.execute();
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                retVal = rs.getInt(1);
            } else {
                System.out.println("Error at getting the AUTO INC Value for project insert into DB.");
            }


        } catch (SQLException e) {
            System.out.println("Connection failure. (writeProjectIntoDB)");
            e.printStackTrace();
        }
        return retVal;
    }

//    public String getConnectionUrl() {
//        return connectionUrl;
//    }
//
//    public void setConnectionUrl(String connectionUrl) {
//        this.connectionUrl = connectionUrl;
//    }
//
//    public String getUser() {
//        return user;
//    }
//
//    public void setUser(String user) {
//        this.user = user;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

//    public void writeProjectInformation(String projectName, String module, long timeStamp) {
//        super.currentProjectIDinDatabase = writeProjectIntoDB(projectName, module, timeStamp);
//    }
}