package runner.io.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import differ.entities.DifferMetrics;
import differ.entities.FileChangeSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import runner.io.db.entities.*;

import javax.persistence.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas on 07.02.2017.
 */
public class HibernateChangeWriter implements DbChangeWriter {
    private static final Logger logger = LogManager.getLogger(HibernateChangeWriter.class);

    private final EntityManagerFactory entityManagerFactory;
    private final String driver;
    private final String url;
    private final String user;
    private final String pass;
    private final HikariDataSource pool;
    private long projectId;
    private long matcherId;
    private IdCache cache;

    public HibernateChangeWriter(String matcherName, String projectName, String driver, String url, String user, String pass) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pass = pass;

        Map hibernateConfig = new HashMap();
        hibernateConfig.put("hibernate.connection.driver_class", driver);
        hibernateConfig.put("hibernate.connection.url", url);
        hibernateConfig.put("hibernate.connection.username", user);
        hibernateConfig.put("hibernate.connection.password", pass);

        this.entityManagerFactory = Persistence.createEntityManagerFactory("DiffDB", hibernateConfig);
        this.pool = createConnectionPool();

        this.init(matcherName, projectName);
        this.cache = new IdCache();
    }

    @Override
    public void setIdCache(IdCache cache) {
        this.cache = cache;
    }

    @Override
    public void write(FileChangeSummary change) {
        EntityManager em = this.entityManagerFactory.createEntityManager();

        long diffId = 0;
        try {
            // store commit
            long commitId = storeCommit(change, em);
            // store diff
            diffId = storeDiff(change, commitId, em);
            // store metrics
            storeMetrics(change, diffId, em);
        } finally {
            em.clear();
            em.close();
        }

        // store changes
        storeChanges(change, diffId);
    }

    @Override
    public void close() throws IOException {
        this.entityManagerFactory.close();
        this.pool.close();
    }

    private void storeMetrics(FileChangeSummary summary, long diffId, EntityManager em) {
        DifferMetrics metrics = summary.getMetrics();

        Metric metric = new Metric();
        metric.setDiffId(diffId);
        metric.setMatcherId(this.matcherId);
        metric.setProjectid(this.projectId);
        metric.setTreeGenTime(metrics.getTreeGenerationTime());
        metric.setMatchingTime(metrics.getMatchingTime());
        metric.setActionGenTime(metrics.getActionGenerationTime());
        metric.setClassifyingTime(metrics.getClassifyingTime());
        metric.setSrcNodes(metrics.getNumSrcNodes());
        metric.setDstNodes(metrics.getNumDstNodes());
        metric.setActions(metrics.getNumActions());

        em.getTransaction().begin();
        em.persist(metric);
        em.flush();
        em.getTransaction().commit();
    }

    private void storeChanges(FileChangeSummary summary, long diffId) {
        try {
            JdbcChangeBulkInserter inserter = new JdbcChangeBulkInserter(() -> {
                try {
                    return this.pool.getConnection();
                } catch (SQLException e) {
                    logger.warn("GetConnection failed", e);
                }

                return null;
            });
            inserter.write(summary, this.projectId, this.matcherId, diffId);
        }
        catch (SQLException e) {
            String message = String.format("Error writing changes. commit: %s src: %s dst: %s",
                    summary.getCommit(),
                    summary.getSrcFileName(),
                    summary.getDstFileName());
            logger.error(message, e);
        }
    }

    private  HikariDataSource createConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(this.user);
        config.setPassword(this.pass);
        config.setMaximumPoolSize(10);

        return new HikariDataSource(config);
    }

    private void init(String matcherName, String projectName) {
        EntityManager em = this.entityManagerFactory.createEntityManager();

        try {
            Project project = new Project();
            project.setName(projectName);

            project = storeIfNotExists(
                    em.createNamedQuery("Project.findByName", Project.class).setParameter("name", projectName),
                    project,
                    em);

            this.projectId = project.getId();

            Matcher matcher = new Matcher();
            matcher.setName(matcherName);

            matcher = storeIfNotExists(
                    em.createNamedQuery("Matcher.findByName", Matcher.class).setParameter("name", matcherName),
                    matcher,
                    em);

            this.matcherId = matcher.getId();

        } catch (Exception e) {
            logger.error("Init failed", e);
        } finally {
            em.close();
        }
    }

    private long storeCommit(FileChangeSummary change, EntityManager em) {
        long commitId = this.cache.getCommitId(change.getCommit());
        if (commitId != 0)
            return commitId;

        TypedQuery<Commit> uniqueQuery =
                em.createNamedQuery("Commit.findByCommitHash", Commit.class)
                        .setParameter("commit", change.getCommit())
                        .setParameter("projectId", this.projectId);

        Commit commit = new Commit();
        commit.setCommit(change.getCommit());
        commit.setParentCommit(change.getParentCommit());
        commit.setProjectId(this.projectId);

        this.cache.addCommit(change.getCommit(), commitId);

        return storeIfNotExists(uniqueQuery, commit, em).getId();
    }

    private long storeDiff(FileChangeSummary change, long commitId, EntityManager em) {
        long diffId = this.cache.getDiffId(commitId, change.getSrcFileName(), change.getDstFileName());
        if (diffId != 0)
            return diffId;

        TypedQuery<Diff> uniqueQuery =
                em.createNamedQuery("Diff.findUnique", Diff.class)
                        .setParameter("commitId", commitId)
                        .setParameter("srcFile", change.getSrcFileName())
                        .setParameter("dstFile", change.getDstFileName());

        Diff diff  = new Diff();
        diff.setCommitid(commitId);
        diff.setProjectid(this.projectId);
        diff.setSrcfile(change.getSrcFileName());
        diff.setDstfile(change.getDstFileName());

        this.cache.addDiff(commitId, change.getSrcFileName(), change.getDstFileName(), diffId);

        return storeIfNotExists(uniqueQuery, diff, em).getId();
    }

    private <T> T storeIfNotExists(TypedQuery<T> uniqueQuery, T newEntity, EntityManager em) {
        T stored = singleOrDefault(uniqueQuery);
        if (stored != null)
            return stored;

        em.getTransaction().begin();

        try {
            em.persist(newEntity);
            em.flush();
            em.getTransaction().commit();
            stored = newEntity;
        } catch (Exception e) {
            logger.warn("Duplicate entry", e);
            em.getTransaction().rollback();

            stored = singleOrDefault(uniqueQuery);
        }

        return stored;
    }

    private <T> T singleOrDefault(TypedQuery<T> query) {
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            logger.info(e);
            return null;
        }
    }
}
