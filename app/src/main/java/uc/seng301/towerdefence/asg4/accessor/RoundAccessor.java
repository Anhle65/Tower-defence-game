package uc.seng301.towerdefence.asg4.accessor;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import uc.seng301.towerdefence.asg4.model.Enemy;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Round;

/**
 * This class offers helper methods for saving, removing, and fetching round records from
 * persistence {@link Round} entities
 */
public class RoundAccessor {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LogManager.getLogger(RoundAccessor.class);

    /**
     * default constructor
     *
     * @param sessionFactory the JPA session factory to talk to the persistence implementation.
     */
    public RoundAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Round} object with the given parameters
     *
     * @param number The Round number must be strictly positive (>0)
     * @param distance The distance must be strictly positive (>0)
     * @param player The Player whose round it is (cannot be null), if not null, assumed it has been
     *        persisted.
     * @param enemies The enemies to be in the round, must not be null, but can be empty
     * @return The Round object with given parameters
     * @throws IllegalArgumentException If any of the above preconditions for input arguments are
     *         violated
     */
    public Round createRound(int number, int distance, Player player, List<Enemy> enemies) {
        if (number <= 0) {
            throw new IllegalArgumentException("Round number must be strictly positive");
        }
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be strictly positive");
        }
        if (null == player) {
            throw new IllegalArgumentException("Player must exist");
        }
        if (null == enemies) {
            throw new IllegalArgumentException("Round must have a place to store enemies");
        }
        if (!player.getRounds().stream().filter(round -> round.getNumber() == number).toList()
                .isEmpty()) {
            throw new IllegalArgumentException("A player's round numbers must be unique");
        }
        Round round = new Round();
        round.setPlayer(player);
        round.setNumber(number);
        round.setDistance(distance);
        round.setEnemies(enemies);
        return round;
    }

    /**
     * Get round from persistence layer by id
     *
     * @param roundId id of round to fetch
     * @return Round with id given if it exists in database, no user object
     */
    public Round getRoundById(Long roundId) {
        if (null == roundId) {
            throw new IllegalArgumentException("cannot retrieve round with null id");
        }
        Round round = null;
        try (Session session = sessionFactory.openSession()) {
            round = session.createQuery("FROM Round WHERE roundId =" + roundId, Round.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return round;
    }

    /**
     * Gets all rounds belonging to player
     *
     * @param player player to fetch rounds
     * @return Rounds belonging to player provided
     */
    public List<Round> getPlayerRounds(Player player) {
        List<Round> rounds = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            rounds = session.createQuery("FROM Round WHERE player=:player", Round.class)
                    .setParameter("player", player).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return rounds;
    }

    /**
     * Get round for given player and number
     * 
     * @param player player the fetch a round for
     * @param number round number to fetch
     * @return Round with given number for player, or null if it does not exist
     */
    public Round getPlayerRoundByNumber(Player player, int number) {
        if (null == player) {
            throw new IllegalArgumentException("Player must exist");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("Round number must be strictly positive");
        }
        Round round = null;
        try (Session session = sessionFactory.openSession()) {
            round = session
                    .createQuery("FROM Round WHERE number=:number AND player=:player", Round.class)
                    .setParameter("number", number).setParameter("player", player).uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return round;
    }

    /**
     * Save given round to persistence
     *
     * @param round round to be saved
     * @return The id of the persisted round
     */
    public Long persistRound(Round round) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(round);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return round.getRoundId();
    }

    /**
     * Update (aka merge) given round to persistence
     *
     * @param round an existing round to update
     * @return the updated round, or null if an error occurred with the persistence layer
     */
    public Round mergeRound(Round round) {
        Round merged = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            merged = session.merge(round);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return merged;
    }

    /**
     * remove given round from persistence by id
     *
     * @param roundId id of round to be deleted
     * @return true if the record is deleted
     */
    public boolean deleteRoundById(Long roundId) throws IllegalArgumentException {
        Round round = getRoundById(roundId);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(round);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return false;
    }
}
