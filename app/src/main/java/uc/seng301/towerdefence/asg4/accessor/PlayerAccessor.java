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
import uc.seng301.towerdefence.asg4.model.Player;

/**
 * This class offers helper methods for saving, removing, and fetching players from persistence
 * {@link Player} entities
 */
public class PlayerAccessor {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LogManager.getLogger(PlayerAccessor.class);

    /**
     * default constructor
     * 
     * @param sessionFactory the JPA session factory to talk to the persistence implementation.
     */
    public PlayerAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Create a {@link Player} object with the given parameters
     * 
     * @param name The Player name must be [a-zA-Z0-9] (not null, empty, or only numerics)
     * @return The Player object with given parameters
     * @throws IllegalArgumentException If any of the above preconditions for input arguments are
     *         violated
     */
    public Player createPlayer(String name) {
        String normalisedName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (normalisedName.matches("\\d+") || !normalisedName.matches("[a-zA-Z0-9 ]+")) {
            throw new IllegalArgumentException("Name be alphanumerical but cannot only be numeric");
        }
        Player player = new Player();
        player.setName(name);
        player.setRounds(new ArrayList<>());
        return player;
    }

    /**
     * Gets player from persistence by name
     * 
     * @param name name of player to fetch
     * @return Player with given name
     */
    public Player getPlayerByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name '" + name + "' cannot be null or blank");
        }
        Player player = null;
        try (Session session = sessionFactory.openSession()) {
            player = session.createQuery("FROM Player where name='" + name + "'", Player.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return player;
    }

    /**
     * Gets player from persistence by id
     * 
     * @param playerId id of player to fetch
     * @return Player with given id
     */
    public Player getPlayerById(Long playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("cannot retrieve player with null id");
        }
        Player player = null;
        try (Session session = sessionFactory.openSession()) {
            player = session.createQuery("FROM Player WHERE playerId =" + playerId, Player.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return player;
    }

    /**
     * Gets all players from persistence
     * 
     * @return List of all players, empty list of none exist
     */
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            players = session.createQuery("FROM Player", Player.class).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return players;
    }

    /**
     * Saves player to persistence
     * 
     * @param player player to save
     * @return id of saved player
     * @throws IllegalArgumentException if player object is invalid (e.g. missing properties)
     */
    public Long persistPlayer(Player player) throws IllegalArgumentException {
        if (null == player || null == player.getName() || player.getName().isBlank()) {
            throw new IllegalArgumentException("cannot save null or blank player");
        }

        Player existingPlayer = getPlayerByName(player.getName());
        if (null != existingPlayer) {
            player.setPlayerId(existingPlayer.getPlayerId());
            return existingPlayer.getPlayerId();
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(player);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return player.getPlayerId();
    }

    /**
     * remove given player from persistence by id
     * 
     * @param playerId id of player to be deleted
     * @return true if the record is deleted
     */
    public boolean removePlayerById(Long playerId) {
        Player player = getPlayerById(playerId);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(player);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return false;
    }

    /**
     * Update (aka merge) given player to persistence
     *
     * @param player an existing player to update
     * @return the updated player, or null if an error occurred with the persistence layer
     */
    public Player mergePlayer(Player player) {
        Player merged = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            merged = session.merge(player);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return merged;
    }
}
