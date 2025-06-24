package uc.seng301.towerdefence.asg4.accessor;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Round;

/**
 * This class offers helper methods for saving, removing, and fetching Enemy records from
 * persistence {@link Enemy} entities
 */
public class EnemyAccessor {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LogManager.getLogger(EnemyAccessor.class);

    /**
     * default constructor
     *
     * @param sessionFactory the JPA session factory to talk to the persistence implementation.
     */
    public EnemyAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Enemy} object with the given parameters
     *
     * @param name the enemy name (not null, not empty, unique, or not only numerics, can be
     *        [a-zA-Z0-9 ])
     * @param speed speed value of enemy (must be > 0)
     * @param health health value of enemy (must be >= 0)
     * @return The Card object with given parameters
     * @throws IllegalArgumentException if any of the above preconditions for input arguments are
     *         violated
     */
    public Enemy createEnemy(String name, int speed, int health) {
        String normalisedName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (normalisedName.matches("\\d+") || !normalisedName.matches("[a-zA-Z0-9 ]+")) {
            throw new IllegalArgumentException(
                    "Name must be alphanumerical but cannot only be numeric");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be strictly positive");
        }
        if (health < 0) {
            throw new IllegalArgumentException("Health must be positive");
        }
        if (null != getEnemyByName(name)) {
            throw new IllegalArgumentException(
                    String.format("Enemy with name %s already exists", name));
        }
        Enemy enemy = new Enemy();
        enemy.setName(name);
        enemy.setSpeed(speed);
        enemy.setHealth(health);
        return enemy;
    }

    /**
     * Get an enemy from persistence layer by its id
     *
     * @param enemyId id of enemy to fetch
     * @return the enemy with id given if it exists in database
     */
    public Enemy getEnemyById(Long enemyId) {
        if (null == enemyId) {
            throw new IllegalArgumentException("cannot retrieve enemy with null id");
        }
        Enemy enemy = null;
        try (Session session = sessionFactory.openSession()) {
            enemy = session.createQuery("FROM Enemy WHERE enemyId =" + enemyId, Enemy.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return enemy;
    }

    /**
     * Get an enemy from persistence layer by its name
     *
     * @param enemyName a name to look for
     * @return the enemy with given name if such enemy exists, null otherwise
     */
    public Enemy getEnemyByName(String enemyName) {
        Enemy enemy = null;
        try (Session session = sessionFactory.openSession()) {
            enemy = session.createQuery("FROM Enemy WHERE name ='" + enemyName + "'", Enemy.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return enemy;
    }

    /**
     * Gets all enemies belonging to a round
     *
     * @param round round to get enemies from
     * @return Enemies belonging to round with
     */
    public List<Enemy> getRoundEnemies(Round round) {
        List<Enemy> enemies = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            enemies = session.createQuery("FROM Enemy WHERE round=:round", Enemy.class)
                    .setParameter("round", round).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return enemies;
    }

    /**
     * Save given enemy to persistence
     *
     * @param enemy enemy to be saved
     * @return The id of the persisted enemy
     */
    public Long persistEnemy(Enemy enemy) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(enemy);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return enemy.getEnemyId();
    }

    /**
     * Remove given enemy from persistence by id
     *
     * @param enemyId id of enemy to be deleted
     * @return true if the record is deleted
     */
    public boolean deleteEnemyById(Long enemyId) throws IllegalArgumentException {
        Enemy enemy = getEnemyById(enemyId);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(enemy);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return false;
    }
}
