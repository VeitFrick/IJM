package at.aau.softwaredynamics.runner.output;


import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import at.aau.softwaredynamics.dependency.DependencyChanges;
import at.aau.softwaredynamics.dependency.NodeDependency;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.revwalk.RevCommit;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBOutput implements OutputWriter {


    private String connectionUrl = "jdbc:postgresql://localhost:5432/depdatabase";

    public  String schemaname = "change_schema";
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
    public final String CHANGEREVISIONTABLENAME = schemaname + ".commit";


    public final int DELETED = 1;
    public final int INSERTED = 2;
    public final int UNCHANGED = 0;

    private int currentProjectIDinDatabase = -1;

    private boolean skipUnchanged = false;

    public DBOutput(String connectionUrl, String user, String password) {
        this.connectionUrl = connectionUrl;
        this.schemaname = schemaname;
        this.user = user;
        this.password = password;
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


        HashMap<Pair<String, String>, Integer>  filenamesToRevisionID = new HashMap<>();

        StringBuilder queryDependenciesInRevision = new StringBuilder("INSERT INTO " + DEPENDENCYINREVISIONTABLENAME + "(\n" +
                "  revision_id        ,\n" +
                "  start_line         ,\n" +
                "  start_line_offset  ,\n" +
                "  end_line           ,\n" +
                "  end_line_offset    ,\n" +
                "  self               ,\n" +
                "  containing_element ,\n" +
                "  action             ,\n" +
                "  is_in_src          ,\n" +
                "  dependency_id       \n"+
                ") VALUES (?,?,?,?,?,?,?,?,?,?)");


        try (
                Connection connection = connectToDatabase();
                PreparedStatement pst = connection.prepareStatement(queryDependenciesInRevision.toString())
        ) {
            for (DependencyChanges fileDependencyChange : dependencyChanges) {

                if (fileDependencyChange == null) continue;

                String srcFileName = "";
                String dstFileName = "";
                int revisionID = -1;

                if (fileDependencyChange.getDepStruct().getRootStrucSrc() != null) {
                    srcFileName = fileDependencyChange.getDepStruct().getRootStrucSrc().getCtElement().getPosition().getCompilationUnit().getFile().toString();
                }
                if (fileDependencyChange.getDepStruct().getRootStrucDst() != null) {
                    dstFileName = fileDependencyChange.getDepStruct().getRootStrucDst().getCtElement().getPosition().getCompilationUnit().getFile().toString();
                }

                Pair<String, String> keypair = new ImmutablePair<>(srcFileName, dstFileName);

                if (filenamesToRevisionID.containsKey(keypair)) {
                    revisionID = filenamesToRevisionID.get(keypair);
                } else {
                    revisionID = writeRevisionIntoDB(srcCommit, dstCommit, currentProjectIDinDatabase, srcFileName, dstFileName,connection);
                }


                int i = 0;
                for (NodeDependency nodeDependency : fileDependencyChange.getAllInsertedNodeDependencies()) {
                    int dependencyID = writeDependencyIntoDB(nodeDependency.getDependency().getDependentOnClass(), nodeDependency.getDependency().getFullyQualifiedName(), nodeDependency.getDependency().getType().name(), connection);
                    addDependencyInRevisionToPreparedStmt(nodeDependency, pst, INSERTED, false, revisionID, dependencyID);
                    i++;
                    if (i % 1000 == 0 || i == fileDependencyChange.getAllInsertedNodeDependencies().size()) {
                        pst.executeBatch(); // Execute every 1000 items.
                    }
                }
                i = 0;
                for (NodeDependency nodeDependency : fileDependencyChange.getAllDeletedNodeDependencies()) {
                    int dependencyID = writeDependencyIntoDB(nodeDependency.getDependency().getDependentOnClass(), nodeDependency.getDependency().getFullyQualifiedName(), nodeDependency.getDependency().getType().name(), connection);
                    addDependencyInRevisionToPreparedStmt(nodeDependency, pst, DELETED, true, revisionID, dependencyID);
                    i++;
                    if (i % 1000 == 0 || i == fileDependencyChange.getAllDeletedNodeDependencies().size()) {
                        pst.executeBatch(); // Execute every 1000 items.
                    }
                }
                if (!skipUnchanged) {
                    i = 0;
                    for (NodeDependency nodeDependency : fileDependencyChange.getAllUnchangedNodeDependenciesSource()) {
                        int dependencyID = writeDependencyIntoDB(nodeDependency.getDependency().getDependentOnClass(), nodeDependency.getDependency().getFullyQualifiedName(), nodeDependency.getDependency().getType().name(), connection);
                        addDependencyInRevisionToPreparedStmt(nodeDependency, pst, UNCHANGED, true, revisionID, dependencyID);
                        i++;
                        if (i % 1000 == 0 || i == fileDependencyChange.getAllUnchangedNodeDependenciesSource().size()) {
                            pst.executeBatch(); // Execute every 1000 items.
                        }
                    }
                    i = 0;
                    for (NodeDependency nodeDependency : fileDependencyChange.getAllUnchangedNodeDependenciesDestination()) {
                        int dependencyID = writeDependencyIntoDB(nodeDependency.getDependency().getDependentOnClass(), nodeDependency.getDependency().getFullyQualifiedName(), nodeDependency.getDependency().getType().name(), connection);
                        addDependencyInRevisionToPreparedStmt(nodeDependency, pst, UNCHANGED, false, revisionID, dependencyID);
                        i++;
                        if (i % 1000 == 0 || i == fileDependencyChange.getAllUnchangedNodeDependenciesDestination().size()) {
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
                Connection connection = connectToDatabase();
                PreparedStatement pst = connection.prepareStatement(queryChangesInFileRevision.toString())
        ) {

            long revisionID = writeChangeRevisionIntoDB(currentProjectIDinDatabase,srcCommit,dstCommit,connection);

            for (Map.Entry<String, List<SourceCodeChange>> mapEntry : changes.entrySet()){

                long fileRevisionID = writeFileRevisionIntoDB(mapEntry.getKey(), revisionID, connection);

                int i = 0;
                for (SourceCodeChange scc: mapEntry.getValue()) {
                    if(scc!=null){
                        addSourceCodeChangeToPreparedStatement(pst, scc, fileRevisionID);
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

    private long writeFileRevisionIntoDB(String filename, long revisionId, Connection connection) {
        int retVal = -1;
        StringBuilder query = new StringBuilder("INSERT INTO " + FILEREVISIONTABLENAME + "(\n" +
                "  filename           ,\n" +
                "  revision_id         \n" +
                ") VALUES (?,?)");

        try (
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, filename);
            pst.setLong(2, revisionId);

            retVal = executePreparedStatementWithGeneratedKeys(pst);

        } catch (SQLException e) {
            System.out.println("Something went wrong in writeFileRevisionIntoDB");
            e.printStackTrace();
        }
        return retVal;

    }


    private long writeChangeRevisionIntoDB(int projectID, RevCommit srcCommit, RevCommit dstCommit, Connection connection) {
        int retVal = -1;
        StringBuilder query = new StringBuilder("INSERT INTO " + CHANGEREVISIONTABLENAME + "(\n" +
                "  commit_src         ,\n" +
                "  commit_dst         ,\n" +
                "  commit_msg         ,\n"+
                "  timestamp          ,\n"+
                "  project_id          \n" +
                ") VALUES (?,?,?,?,?)");

        try (
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, srcCommit.getName());
            pst.setString(2, dstCommit.getName());
            pst.setString(3,dstCommit.getFullMessage());
            pst.setTimestamp(4, new Timestamp(srcCommit.getAuthorIdent().getWhen().getTime()));
            pst.setInt(5, projectID);

            retVal = executePreparedStatementWithGeneratedKeys(pst);

        } catch (SQLException e) {
            System.out.println("Something went wrong in writeChangeRevisionIntoDB");
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

    /**
     * @param nodeDependency
     * @param pst
     * @param action -- SMALLINT! Use Constants provided
     * @param isInSource
     * @param revisionID
     * @throws SQLException
     */
    private void addDependencyInRevisionToPreparedStmt(NodeDependency nodeDependency, PreparedStatement pst, int action, boolean isInSource, int revisionID, int dependencyID) throws SQLException {
        pst.setInt(1,revisionID);
        pst.setInt(2, nodeDependency.getLineNumbers().getStartLine());
        pst.setInt(3, nodeDependency.getLineNumbers().getStartOffset());
        pst.setInt(4, nodeDependency.getLineNumbers().getEndLine());
        pst.setInt(5, nodeDependency.getLineNumbers().getEndOffset());
        pst.setBoolean(6, nodeDependency.getDependency().getSelfDependency());
        pst.setString(7, nodeDependency.getContainingElement().toString());
        pst.setInt(8, action);
        pst.setBoolean(9, isInSource);
        pst.setInt(10, dependencyID);
        pst.addBatch();
    }

    private void addNodeDependencyToPreparedStmt(NodeDependency nodeDependency, PreparedStatement pst, DependencyChanges fileDependencyChange, RevCommit commit, int diffId, String action, boolean isInSource) throws SQLException {
        pst.setString(1, nodeDependency.getDependency().getType().name());
        pst.setInt(2, diffId);
        pst.setString(3, nodeDependency.getDependency().getDependentOnClass());
        pst.setString(4, nodeDependency.getDependency().getFullyQualifiedName());
        pst.setBoolean(5, nodeDependency.getDependency().getSelfDependency());
        pst.setString(6, nodeDependency.getContainingElement().toString());
        pst.setInt(7, nodeDependency.getLineNumbers().getStartLine());
        pst.setInt(8, nodeDependency.getLineNumbers().getStartOffset());
        pst.setInt(9, nodeDependency.getLineNumbers().getEndLine());
        pst.setInt(10, nodeDependency.getLineNumbers().getEndOffset());
        pst.setString(11, commit.getName());
        //TODO: if null --> write NULL
        if (fileDependencyChange.getDepStruct().getRootStrucSrc() != null) {
            pst.setString(12, fileDependencyChange.getDepStruct().getRootStrucSrc().getCtElement().getPosition().getCompilationUnit().getFile().toString());

        } else {
            pst.setString(12, null);

        }
        if (fileDependencyChange.getDepStruct().getRootStrucDst() != null) {
            pst.setString(13, fileDependencyChange.getDepStruct().getRootStrucDst().getCtElement().getPosition().getCompilationUnit().getFile().toString());

        } else {
            pst.setString(13, null);

        }
        pst.setBoolean(14, isInSource);
        pst.setString(15, action);

        pst.addBatch();
    }

    private int writeDependencyIntoDB(String dependentOnClass, String fullDependency, String type, Connection connection) throws SQLException {
        int retVal = -1;

        StringBuilder query = new StringBuilder("INSERT INTO " + DEPENDENCYTABLENAME + "(\n" +
                "  dependent_on_class ,\n" +
                "  full_dependency    ,\n" +
                "  type               \n" +
                ") VALUES (?,?,?)");

        try (
                PreparedStatement pst = connection.prepareStatement(query.toString(),Statement.RETURN_GENERATED_KEYS)
        ) {
            pst.setString(1, dependentOnClass);
            pst.setString(2, fullDependency);
            pst.setString(3, type);

            retVal = executePreparedStatementWithGeneratedKeys(pst);


        } catch (SQLException e) {
            if(e.getSQLState().equals("23505")){
                return getIDForDependency(dependentOnClass,fullDependency,type, connection);
            }
            System.out.println("Something went wrong in writeDependencyIntoDB");
            e.printStackTrace();
            throw e;
        }
        return retVal;
    }

    private int getIDForDependency(String dependentOnClass, String fullDependency, String type, Connection connection) {
        String SQL = "SELECT id FROM " + DEPENDENCYTABLENAME + " WHERE dependent_on_class  = '" + dependentOnClass + "' AND  full_dependency = '" + fullDependency + "' AND type = '" + type + "';";
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL))
        {
            rs.next();
           return rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
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
                Connection connection = connectToDatabase();
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
                Connection connection = connectToDatabase();
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

    public void writeProjectInformation(String projectName, String module, long timeStamp) {
        this.currentProjectIDinDatabase = writeProjectIntoDB(projectName,module,timeStamp);
    }

    public void writeProjectInformation(String projectName, String module, long timeStamp, String matchingAlgorithm) {
        this.currentProjectIDinDatabase = writeProjectIntoDB(projectName,module,timeStamp, matchingAlgorithm);
    }

    public Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection(connectionUrl, user, password);
    }

    public int getCurrentProjectIDinDatabase() {
        return currentProjectIDinDatabase;
    }


}