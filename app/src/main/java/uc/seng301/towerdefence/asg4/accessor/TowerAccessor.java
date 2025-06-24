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
import uc.seng301.towerdefence.asg4.creator.TowerCreator;
import uc.seng301.towerdefence.asg4.creator.TowerType;
import uc.seng301.towerdefence.asg4.model.Player;
import uc.seng301.towerdefence.asg4.model.Tower;

/**
 * This class offers helper methods for saving, removing, and fetching tower records from
 * persistence {@link Tower} entities
 */
public class TowerAccessor {
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LogManager.getLogger(TowerAccessor.class);

    private static final int DEFAULT_RANGE = 40;
    private static final int DEFAULT_RATE = 1;

    /**
     * default constructor
     *
     * @param sessionFactory the JPA session factory to talk to the persistence implementation.
     */
    public TowerAccessor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    /**
     * Create a {@link Tower} object with the given parameters
     *
     * @param name The Tower name, cannot be empty, must be alphanumeric (but not only numbers)
     * @param player The Player whose tower it is (cannot be null), if not null, assumed it has been
     *        persisted.
     * @param damage The damage the tower does each attack, must be strictly positive (>0)
     * @param towerType The type of tower to create, cannot be null
     * @return The Tower object with given parameters
     * @throws IllegalArgumentException If any of the above preconditions for input arguments are
     *         violated
     */
    public Tower createTower(String name, Player player, int damage, String towerType) {
        String normalisedName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        if (null == name || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (normalisedName.matches("\\d+") || !normalisedName.matches("[a-zA-Z0-9 ]+")) {
            throw new IllegalArgumentException("Name be alphanumerical but cannot only be numeric");
        }
        if (null == player) {
            throw new IllegalArgumentException("Player must exist");
        }
        if (player.getTowers().stream().anyMatch(tower -> tower.getName().equals(normalisedName))) {
            throw new IllegalArgumentException(
                    "Tower named " + normalisedName + " already exists for player");
        }
        if (player.getTowers().size() >= 5) {
            throw new IllegalArgumentException("5 or more towers already exist for player");
        }
        if (damage <= 0) {
            throw new IllegalArgumentException("Damage must be strictly positive");
        }
        if (null == towerType) {
            throw new IllegalArgumentException("Tower type cannot be null");
        }
        TowerType towerTypeEnum = TowerType.fromString(towerType);
        TowerCreator creator = towerTypeEnum.getTowerCreator();
        creator.makeNewTower(name, damage);
        creator.setTowerRate(DEFAULT_RATE);
        creator.setTowerRange(DEFAULT_RANGE);
        Tower tower = creator.getTower();
        tower.setPlayer(player);
        return tower;
    }

    /**
     * Get tower from persistence layer by id
     *
     * @param towerId id of tower to fetch
     * @return Tower with id given if it exists in database, no user object
     */
    public Tower getTowerById(Long towerId) {
        if (null == towerId) {
            throw new IllegalArgumentException("cannot retrieve tower with null id");
        }
        Tower tower = null;
        try (Session session = sessionFactory.openSession()) {
            tower = session.createQuery("FROM Tower WHERE towerId =" + towerId, Tower.class)
                    .uniqueResult();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return tower;
    }

    public List<Tower> getPlayerTowers(Player player) {
        List<Tower> towers = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            towers = session.createQuery("FROM Tower WHERE player=:player", Tower.class)
                    .setParameter("player", player).list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return towers;
    }


    /**
     * Gets all towers belonging to player by id
     *
     * @param playerId id of player to fetch tower
     * @return Towers belonging to player with id provided
     */
    public List<Tower> getPlayerTowersById(Long playerId) {
        List<Tower> towers = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            towers = session.createQuery("FROM Tower WHERE playerId=" + playerId, Tower.class)
                    .list();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return towers;
    }

    /**
     * Save given tower to persistence
     *
     * @param tower tower to be saved
     * @return The id of the persisted tower
     */
    public Long persistTower(Tower tower) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(tower);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return tower.getTowerId();
    }

    /**
     * Update (aka merge) given tower to persistence
     *
     * @param tower an existing tower to update
     * @return the updated tower, or null if an error occurred with the persistence layer
     */
    public Tower mergeTower(Tower tower) {
        Tower merged = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            merged = session.merge(tower);
            transaction.commit();
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return merged;
    }

    /**
     * remove given tower from persistence by id
     *
     * @param towerId id of tower to be deleted
     * @return true if the record is deleted
     */
    public boolean deleteTowerById(Long towerId) throws IllegalArgumentException {
        Tower tower = getTowerById(towerId);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(tower);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            LOGGER.error("Unable to open session or transaction", e);
        }
        return false;
    }


}
