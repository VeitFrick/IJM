package at.aau.softwaredynamics.runner.io.db;

import at.aau.softwaredynamics.classifier.entities.FileChangeSummary;
import at.aau.softwaredynamics.classifier.entities.SourceCodeChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by thomas on 20.02.2017.
 */
public class JdbcChangeBulkInserter {
    private static final Logger logger = LogManager.getLogger(JdbcChangeBulkInserter.class);

    private final Supplier<Connection> connectionFatory;
    private int batchSize = 500;

    public JdbcChangeBulkInserter(Supplier<Connection> connectionFactory) {
        this.connectionFatory = connectionFactory;
    }

    public void write(FileChangeSummary summary, long projectId, long matcherId, long diffId) throws SQLException {
        List<SourceCodeChange> changes = summary.getChanges();
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();

            if (connection != null) {
                for (int i = 0; i < changes.size(); i++) {
                    // create connection and prepared statement
                    statement = connection.prepareStatement(
                            "INSERT INTO changes " +
                                    "(projectid, diffid, matcherid,action,changetype,nodetype" +
                                    ",srcid,srcstart,srcend,srclabel" +
                                    ",dstid,dststart,dstend,dstlabel) " +
                                    "VALUES " +
                                    "(?,?,?,?,?,?" +
                                    ",?,?,?,?" +
                                    ",?,?,?,?)");

                    // insert batch
                    int j = 0;
                    for (; j < batchSize && i + j < changes.size(); j++)
                        addQuery(statement, changes.get(i + j), projectId, diffId, matcherId);

                    // add processed changes to outer loop counter
                    i+=j-1;

                    // store batch + cleanup
                    statement.executeBatch();
                    statement.close();
                }
                connection.close();
            } else {
                String message = String.format("Could not open connection. commit: %s src: %s dst: %s",
                        summary.getCommit(),
                        summary.getSrcFileName(),
                        summary.getDstFileName());

                logger.error(message);
            }
        } catch (Exception e) {
            logger.error("Bulk insert failed", e);
        } finally {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }

            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    private void addQuery(PreparedStatement statement, SourceCodeChange scc, long projectId, long diffId, long matcherId) throws SQLException {
        // general info
        statement.setLong(1, projectId);
        statement.setLong(2, diffId);
        statement.setLong(3, matcherId);
        statement.setString(4, scc.getAction().getName());
        statement.setString(5, scc.getChangeType().getName());
        statement.setInt(6, scc.getNodeType());

        //src info
        statement.setInt(7, scc.getSrcInfo().getId());
        statement.setInt(8, scc.getSrcInfo().getStartLineNumber());
        statement.setInt(9, scc.getSrcInfo().getEndLineNumber());
        statement.setString(10, replaceNullCharacter(scc.getSrcInfo().getLabel()));

        //dst info
        statement.setInt(11, scc.getDstInfo().getId());
        statement.setInt(12, scc.getDstInfo().getStartLineNumber());
        statement.setInt(13, scc.getDstInfo().getEndLineNumber());
        statement.setString(14, replaceNullCharacter(scc.getDstInfo().getLabel()));

        statement.addBatch();
    }

    private String replaceNullCharacter(String string) {
        return string.replaceAll("\\x00", "");
    }

    private Connection getConnection() {
        Connection retVal = this.connectionFatory.get();

        // retry up to 3 times
        for (int i = 0; retVal == null && i < 3; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error(e);
            }
            retVal = this.connectionFatory.get();
        }

        return retVal;
    }
}
