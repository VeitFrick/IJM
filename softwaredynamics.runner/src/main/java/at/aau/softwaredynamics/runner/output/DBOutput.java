package at.aau.softwaredynamics.runner.output;


import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class DBOutput implements OutputWriter {


    private String connectionUrl = "jdbc:postgresql://localhost:5432/depdatabase";

    public String schemaname = "change_schema";
    private String user = "postgres";
    private String password = "";

    public final String SEPARATOR = ",";



    public final String REVISIONTABLENAME = schemaname + ".revision";
    public final String PROJECTTABLENAME = schemaname + ".project";
    public final String DEPENDENCYTABLENAME = schemaname + ".dependency";
    public final String DEPENDENCYINREVISIONTABLENAME = schemaname + ".dependencies_in_revision";


    public final String CHANGESTABLENAME = schemaname + ".changes";
    public final String FILEREVISIONTABLENAME = schemaname + ".filerevision";
    public final String PROJECTTABLENAMEINCLMATCHER = schemaname + ".project";

    public final int DELETED = 1;
    public final int INSERTED = 2;
    public final int UNCHANGED = 0;

    private int currentProjectIDinDatabase = -1;

    private boolean skipUnchanged = false;

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

        try (Connection connection = DriverManager.getConnection(connectionUrl, user, password)) {

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

    @Override
    public void writeChangeInformation(Map<String, List<SourceCodeChange>> changes, RevCommit srcCommit, RevCommit dstCommit, String module, String project, long timeStamp) throws SQLException {

        StringBuilder queryChangesInFileRevision = new StringBuilder("INSERT INTO " + CHANGESTABLENAME + "(\n" +
                "  filerevision_id        ,\n" +
                "  action        ,\n" +
                "  change_type        ,\n" +
                "  node_type        ,\n" +
                "  ct_element        ,\n" +
                "  start_line_src        ,\n" +
                "  start_line_offset_src        ,\n" +
                "  end_line_src        ,\n" +
                "  end_line_offset_src        ,\n" +
                "  start_line_dst        ,\n" +
                "  start_line_offset_dst        ,\n" +
                "  end_line_dst       ,\n" +
                "  end_line_offset_dst        \n" +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

        try (
                Connection connection = DriverManager.getConnection(connectionUrl, user, password);
                PreparedStatement pst = connection.prepareStatement(queryChangesInFileRevision.toString())
        ) {

            for (Map.Entry<String, List<SourceCodeChange>> mapEntry : changes.entrySet()){

                long revisionID = writeFileRevisionIntoDB(mapEntry.getKey(), currentProjectIDinDatabase, srcCommit, dstCommit, connection);

                int i = 0;
                for (SourceCodeChange scc: mapEntry.getValue()) {
                    if(scc!=null){
                        addSourceCodeChangeToPreparedStatement(pst, scc, revisionID);
                        i++;
                        if (i % 1000 == 0 || i == mapEntry.getValue().size()) {
                            pst.executeBatch(); // Execute every 1000 items.
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
            throw e;
        }
    }

    private void addSourceCodeChangeToPreparedStatement(PreparedStatement pst, SourceCodeChange scc, long revisionID) throws SQLException {
        pst.setLong(1,revisionID);
        pst.setString(2,scc.getAction().getName());
        pst.setString(3, scc.getChangeType().getName());
        pst.setInt(4, scc.getNodeType());
        pst.setString(5, scc.getCtElementName());
        if (scc.getSrcInfo()!= null){
            pst.setInt(6, scc.getSrcInfo().getStartLineNumber());
            pst.setInt(7, scc.getSrcInfo().getStartOffset());
            pst.setInt(8, scc.getSrcInfo().getEndLineNumber());
            pst.setInt(9, scc.getSrcInfo().getEndOffset());
        }
        if (scc.getDstInfo()!= null){
            pst.setInt(10, scc.getDstInfo().getStartLineNumber());
            pst.setInt(11, scc.getDstInfo().getStartOffset());
            pst.setInt(12, scc.getDstInfo().getEndLineNumber());
            pst.setInt(13, scc.getDstInfo().getEndOffset());
        }
        pst.addBatch();
    }

    private long writeFileRevisionIntoDB(String filename, int projectID, RevCommit srcCommit, RevCommit dstCommit, Connection connection) {
        int retVal = -1;
        StringBuilder query = new StringBuilder("INSERT INTO " + FILEREVISIONTABLENAME + "(\n" +
                "  filename           ,\n" +
                "  commit_src         ,\n" +
                "  commit_dst         ,\n" +
                "  project_id                \n" +
                ") VALUES (?,?,?,?)");

        try (
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, filename);
            pst.setString(2, srcCommit.getName());
            pst.setString(3, dstCommit.getName());
            pst.setInt(4, projectID);

            retVal = executePreparedStatementWithGeneratedKeys(pst);

        } catch (SQLException e) {
            System.out.println("Something went wrong in writeFileRevisionIntoDB");
            e.printStackTrace();
        }
        return retVal;

    }

    /**
     * Executes a given Prepared Statement and returns the value of the generated Key.
     * @param pst
     * @return generated Key, -1 if something went wrong
     * @throws SQLException
     */
    private int executePreparedStatementWithGeneratedKeys(PreparedStatement pst) throws SQLException {
        pst.execute();
        ResultSet rs = pst.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }
        System.out.println("Error at getting the AUTO INC Value for revision insert into DB.");
        return -1;

    }

    private int writeProjectIntoDB(String project, String module, long runInitiatedTimestamp){
        int retVal = -1;
        StringBuilder query = new StringBuilder(
                "INSERT INTO " + PROJECTTABLENAME + "(\n" +
                "  project_name           ,\n" +
                "  module           ,\n" +
                "  run_initiated         \n" +
                ") VALUES ( ?,?,?)");

        try (
                Connection connection = DriverManager.getConnection(connectionUrl, user, password);
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, project);
            pst.setString(2, module);
            pst.setLong(3, runInitiatedTimestamp);

            retVal = executePreparedStatementWithGeneratedKeys(pst);


        } catch (SQLException e) {
            System.out.println("Connection failure. (writeProjectIntoDB)");
            e.printStackTrace();
        }
        return retVal;
    }

    private int writeProjectIntoDB(String project, String module, long runInitiatedTimestamp, String matchingAlgorithm) {
        int retVal = -1;
        StringBuilder query = new StringBuilder(
                "INSERT INTO " + PROJECTTABLENAMEINCLMATCHER + "(\n" +
                        "  project_name           ,\n" +
                        "  module           ,\n" +
                        "  run_initiated         ,\n" +
                        " matcher \n"+
                        ") VALUES ( ?,?,?,?)");

        try (
                Connection connection = DriverManager.getConnection(connectionUrl, user, password);
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, project);
            pst.setString(2, module);
            pst.setLong(3, runInitiatedTimestamp);
            pst.setString(4, matchingAlgorithm);

            retVal = executePreparedStatementWithGeneratedKeys(pst);


        } catch (SQLException e) {
            System.out.println("Connection failure. (writeProjectIntoDB)");
            e.printStackTrace();
        }
        return retVal;
    }


    private int writeRevisionIntoDB(RevCommit srcCommit, RevCommit dstCommit, int projectID, String pathDst, String pathSrc, Connection connection){
        int retVal = -1;
        StringBuilder query = new StringBuilder("INSERT INTO " + REVISIONTABLENAME + "(\n" +
                "  path_src           ,\n" +
                "  path_dst           ,\n" +
                "  commit_src         ,\n" +
                "  commit_dst         ,\n" +
                "  project_id         \n" +
                ") VALUES ( ?,?,?,?,?)");

        try (
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, pathSrc);
            pst.setString(2, pathDst);
            pst.setString(3, srcCommit.getName());
            pst.setString(4, dstCommit.getName());
            pst.setInt(5, projectID);

            retVal = executePreparedStatementWithGeneratedKeys(pst);

        } catch (SQLException e) {
            System.out.println("Something went wrong in writeRevisionIntoDB");
            e.printStackTrace();
        }
        return retVal;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void writeProjectInformation(String projectName, String module, long timeStamp) {
        this.currentProjectIDinDatabase = writeProjectIntoDB(projectName,module,timeStamp);
    }

    public void writeProjectInformation(String projectName, String module, long timeStamp, String matchingAlgorithm) {
        this.currentProjectIDinDatabase = writeProjectIntoDB(projectName,module,timeStamp, matchingAlgorithm);
    }

    public String getSchemaname() {
        return schemaname;
    }

    public void setSchemaname(String schemaname) {
        this.schemaname = schemaname;
    }


}