package com.lsmsdb.task3.neo4jmanager;

import com.lsmsdb.task3.Configuration;
import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.computation.InfectionRiskCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

/**
 * This class manages the Neo4j database. It is responsible for the connection
 * to the database and the queries needed by the whole application to work
 */
public class Neo4JManager {

    private static final Logger LOGGER = Logger.getLogger(Neo4JManager.class.getName());
    
    private static Neo4JManager istance = null;
    private static String hostUri;
    private static Driver driver;
    private static Boolean connected = false;
    private static Integer port_;
    private static String hostname_;

    /**
     * This function return the istance of Neo4JManager
     *
     * @return the Neo4JManager istance
     */
    public static Neo4JManager getIstance() {
        if (istance == null) {
            istance = new Neo4JManager();
        }
        return istance;
    }

    /**
     * This function must be used to initiate a connection to the database
     *
     * @param hostname the hostname
     * @param port the port
     * @param user the user for the Neo4J graph connection
     * @param password the password for the Neo4J graph connection
     * @return true if the connection is opened successfully, false otherwise
     */
    public Boolean connect(String hostname, Integer port, String user, String password) {
        String uri = "bolt://" + hostname + ":" + port + "/";
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        try {
            driver.verifyConnectivity();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"",e);
            driver.close();
        }
        try (Session session = driver.session()) {
            connected = true;
            hostUri = uri;
            port_ = port;
            hostname_ = hostname;
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"",e);
            driver.close();
            connected = false;
            return false;
        }
    }

    /**
     * This function must be used to initiate a connection to the database with
     * standard parameter
     *
     * @return true if the connection is opened successfully, false otherwise
     */
    public Boolean connect() {
        String host = "localhost";
        Integer port = 7687;
        String user = "neo4j";
        String password = "password4j";
        return connect(host, port, user, password);
    }

    /**
     * @return true if it is connected, false otherwise
     */
    public Boolean isConnected() {
        return connected;
    }

    /**
     * Get the host bolt uri
     *
     * @return the host bolt uri
     */
    public String getUri() {
        if (!connected) {
            return null;
        }
        return hostUri;
    }

    /**
     * Get the hostname
     *
     * @return the hostname if the database is connected, null otherwise
     */
    public String getHostname() {
        if (!connected) {
            return null;
        }
        return hostname_;
    }

    /**
     * Get the port
     *
     * @return the port if the database is connected, -1 otherwise
     */
    public Integer getPort() {
        if (!connected) {
            return -1;
        }
        return port_;
    }

    /**
     * Explicit close function for the Neo4J driver
     *
     * @return true if the driver is now closed
     */
    public Boolean close() {
        try {
            if (driver != null) {
                driver.close();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,"",e);
            return false;
        }
        return false;
    }

    
    /* ####################### other methods ################# */
    
    /**
     * 
     * @return the number of Places in the database
     */
    public Long countPlaces() {
        if (!connected) {
            return -1L;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {
                    String query = "MATCH (a:Place) "
                            + " RETURN COUNT(a) as howmuch";
                    Result result = tx.run(query);
                    if (!result.hasNext()) {
                        return -1L;
                    } else {
                        return result.next().get("howmuch").asLong();
                    }
                }
            });
        }
    }    
    
    /**
     * the person login function
     * @param fiscalCode the fiscalCode of the person which want to login
     * @return the Person object, null if there are no person in the system with
     * the idPerson specified
     */
    public Person login(String fiscalCode) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Person>() {
                @Override
                public Person execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MATCH (a:Person { fiscalCode: $fiscalCode})"
                            + " RETURN a as person";
                    map.put("fiscalCode", fiscalCode);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return null;
                    } else {
                        return new Person(result.next().get("person").asMap());
                    }
                }
            });
        }
    }

    /**
     * Return the place by name
     *
     * @param name the place name
     * @return the place
     */
    public Place getPlace(String name) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Place>() {
                @Override
                public Place execute(Transaction tx) {
                    Place place = null;
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MATCH (a:Place { name: $name})"
                            + " RETURN a as place";
                    map.put("name", name);
                    Result result = tx.run(query, map);

                    if (result.hasNext()) {
                        Record r = result.next();
                        place = new Place(r.get("place").asMap());
                    }
                    return place;
                }
            });
        }
    }

    /**
     * Add a Person node to the database
     *
     * @param p the person to be added
     * @return
     */
    public Boolean addPerson(Person p) {
        if (!connected) {
            return false;
        }

        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MERGE (a:Person { fiscalCode: $fiscalCode})"
                            + " ON CREATE SET a.fiscalCode = $fiscalCode, "
                            + " a.name = $name, "
                            + " a.surname = $surname, "
                            + " a.timestampInfected = $timestampInfected, "
                            + " a.timestampHealed = $timestampHealed "
                            + " RETURN a.id as idperson";
                    map.put("fiscalCode", p.getFiscalCode());
                    map.put("name", p.getName());
                    map.put("surname", p.getSurname());
                    map.put("timestampInfected", p.getTimestampInfected());
                    map.put("timestampHealed", p.getTimestampHealed());
                    Result result = tx.run(query, map);
                    return result.hasNext();
                }
            });
        }
    }

    /**
     * Add a Place node to the database
     *
     * @param p the place to be added
     * @return the place updated with the id
     */
    public Boolean addPlace(Place p) {
        if (!connected) {
            return false;
        }

        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MERGE (a:Place { name: $name})"
                            + " ON CREATE SET a.name = $name, "
                            + " a.infectionRisk = $infectionRisk, "
                            + " a.latitude = $latitude, "
                            + " a.longitude = $longitude, "
                            + " a.type = $type, "
                            + " a.area = $area, "
                            + " a.city = $city "
                            + " RETURN a as place";
                    map.put("name", p.getName());
                    map.put("infectionRisk", p.getInfectionRisk());
                    map.put("latitude", p.getLatitude());
                    map.put("longitude", p.getLongitude());
                    map.put("type", p.getType());
                    map.put("area", p.getArea());
                    map.put("city", p.getCity());

                    Result result = tx.run(query, map);
                    return result.hasNext();
                }
            });
        }
    }

    /**
     * Remove a Person from the database by fiscalCode
     *
     * @param fiscalCode the person fiscalCode 
     * @return true on success, false otherwise
     */
    public Boolean removePerson(String fiscalCode) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person "
                            + "{ "
                            + "fiscalCode: $fiscalCode"
                            + "}) "
                            + "DETACH DELETE a";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fiscalCode", fiscalCode);

                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /**
     * Remove a Place from the database by name
     *
     * @param name the place name 
     * @return true on success, false otherwise
     */
    public Boolean removePlace(String name) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Place "
                            + "{ "
                            + "name: $name"
                            + "}) "
                            + "DETACH DELETE a";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name);

                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /**
     * Create a Person node, a house Place node, and the Person and a relation
     * lives_in between them.
     *
     * @param person the person
     * @param place the place
     * @param timestamp the starting timestamp
     * @return true if the db is updated
     */
    public Boolean registerPersonAndCreateItsHouse(Person person, Place place, Long timestamp) {
        if (!connected) {
            return false;
        }
        if (place.getType().compareTo(Place.HOUSE_TYPE_IDENTIFICATOR) != 0) {
            return false;
        }
        try (Session session = driver.session()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                /* CREATE PERSON */
                HashMap<String, Object> map = new HashMap<>();
                String query = "MERGE (a:Person { fiscalCode: $fiscalCode})"
                        + " ON CREATE SET a.fiscalCode = $fiscalCode, "
                        + " a.name = $name, "
                        + " a.surname = $surname, "
                        + " a.timestampInfected = $timestampInfected, "
                        + " a.timestampHealed = $timestampHealed";
                map.put("fiscalCode", person.getFiscalCode());
                map.put("name", person.getName());
                map.put("surname", person.getSurname());
                map.put("timestampInfected", person.getTimestampInfected());
                map.put("timestampHealed", person.getTimestampHealed());
                tx.run(query, map);

                /* CREATE house PLACE */
                map = new HashMap<>();
                query = "MERGE (a:Place { name: $name}) "
                        + " ON CREATE SET a.name = $name, "
                        + " a.infectionRisk = $infectionRisk, "
                        + " a.latitude = $latitude, "
                        + " a.longitude = $longitude, "
                        + " a.type = $type, "
                        + " a.area = $area, "
                        + " a.city = $city";

                map.put("name", place.getName());
                map.put("infectionRisk", place.getInfectionRisk());
                map.put("latitude", place.getLatitude());
                map.put("longitude", place.getLongitude());
                map.put("type", place.getType());
                map.put("area", place.getArea());
                map.put("city", place.getCity());
                tx.run(query, map);

                /* LIVES_IN  */
                query = "MATCH (a:Person), (b:Place) "
                        + "WHERE a.fiscalCode = $fiscalCode AND b.name = $namePlace "
                        + "CREATE (a)-[ r:lives_in {timestamp: $timestamp} ] ->(b)";
                map = new HashMap<String, Object>();
                //map.put("timestamp", System.currentTimeMillis());
                map.put("fiscalCode", person.getFiscalCode());
                map.put("namePlace", place.getName());
                map.put("timestamp", timestamp);
                tx.run(query, map);

                tx.commit();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE,"",e);
                if (tx != null) {
                    tx.rollback();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return all place by city
     *
     * @param city the specified city.
     * @return all the places by the city parameter.
     */
    public ArrayList<Place> getAllPlaceByCity(String city) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<ArrayList<Place>>() {
                @Override
                public ArrayList<Place> execute(Transaction tx) {
                    ArrayList<Place> list = new ArrayList<>();
                    String query = "MATCH (a:Place) "
                            + "WHERE a.city = $city AND a.type <> $houseTypeIdentificator "
                            + "RETURN a AS place "
                            + "ORDER BY a.name";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("city", city);
                    map.put("houseTypeIdentificator", Place.HOUSE_TYPE_IDENTIFICATOR);

                    Result result = tx.run(query, map);
                    while (result.hasNext()) {
                        Record r = result.next();
                        list.add(new Place(r.get("place").asMap()));
                    }

                    return list;
                }
            });
        }
    }

    /**
     * Return the house by the person fiscalCode
     *
     * @param fiscalCode the person name
     * @return the house.
     */
    public Place getHouse(String fiscalCode) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Place>() {
                @Override
                public Place execute(Transaction tx) {
                    Place house = null;
                    String query = "MATCH (a:Person)-[k:lives_in]->(b:Place) "
                            + "WHERE a.fiscalCode = $fiscalCode AND b.type = $houseTypeIdentificator "
                            + "RETURN b AS place";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("fiscalCode", fiscalCode);
                    map.put("houseTypeIdentificator", Place.HOUSE_TYPE_IDENTIFICATOR);
                    Result result = tx.run(query, map);
                    if (result.hasNext()) {
                        Record r = result.next();
                        house = new Place(r.get("place").asMap());
                    }

                    return house;
                }
            });
        }

    }

    /**
     * Insert a visit of a Person to a Place
     *
     * @param fiscalCode the person fiscalCode
     * @param namePlace the place name
     * @param timestamp timestamp on which the visit occurred
     * @return
     */
    public Boolean visit(String fiscalCode, String namePlace, Long timestamp) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person), (b:Place) "
                            + "WHERE a.fiscalCode = $fiscalCode AND b.name = $namePlace "
                            + "CREATE (a)-[ r:visited {timestamp: $timestamp} ] ->(b)";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    //map.put("timestamp", System.currentTimeMillis());
                    map.put("fiscalCode", fiscalCode);
                    map.put("namePlace", namePlace);
                    map.put("timestamp", timestamp);

                    tx.run(query, map);
                    return true;
                }

            });
        }

    }

    /**
     * Insert a lives_in between a Person and a house Place
     *
     * @param fiscalCode the person fiscal code
     * @param namePlace the place name
     * @param timestamp timestamp on which the visit occurred
     * @return
     */
    public Boolean lives_in(String fiscalCode, String namePlace, Long timestamp) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person), (b:Place) "
                            + "WHERE a.fiscalCode = $fiscalCode AND b.name = $namePlace "
                            + "CREATE (a)-[ r:lives_in {timestamp: $timestamp} ] ->(b)";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    //map.put("timestamp", System.currentTimeMillis());
                    map.put("fiscalCode", fiscalCode);
                    map.put("namePlace", namePlace);
                    map.put("timestamp", timestamp);

                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /* ################################### QUERY ########################################## */
    
    /**
     * Retrieve the number of people who are linked with a path to a given
     * person within a given social distance (number of hops).
     *
     * @param fiscalCode the starting person name
     * @param n_hops maximum number of hops
     * @param timestamp the starting timestamp
     * @param validityTimeMillis interval of time on which each visit
     * relationships in the path are relevant statring from the timestamp
     * @return number of infected people, -1 in case of errors
     */
    public Long infectedInAGivenSocialDistance(String fiscalCode, Long n_hops, Long validityTimeMillis, Long timestamp) {
        if (!connected) {
            return -1L;
        }

        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {
                    Long infectedNumber = 0L;
                    String query = "MATCH (a:Person {fiscalCode: $fiscalCode}), "
                            + " p=(a)-[r:visited*1.." + n_hops + "]-(c:Person) "
                            + " WHERE c.fiscalCode <> a.fiscalCode "
                            + " AND all(rel in relationships(p) WHERE rel.timestamp > $time1) "
                            + " AND (last(relationships(p)).timestamp > c.timestampInfected AND c.timestampInfected <> 0) "
                            + " AND (last(relationships(p)).timestamp < c.timestampHealed OR c.timestampHealed = 0) "
                            + " RETURN COUNT(DISTINCT(c)) as howmuch";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fiscalCode", fiscalCode);
                    //map.put("n_hops", n_hops);
                    map.put("time1", timestamp - validityTimeMillis);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return -1L;
                    }
                    infectedNumber = result.next().get("howmuch").asLong();
                    return infectedNumber;
                }
            });
        }
    }

    /**
     * Find the closest infected person starting from a given person
     *
     * @param fiscalCode starting person fiscal code
     * @param timestamp the starting timestamp
     * @param validityTimeMillis the interval of time on which each visit
     * relationships in the path are relevant from the starting timestamp
     * @return length of the path to the closest infected person, -1 in case of
     * errors.
     */
    public Long userRiskOfInfection(String fiscalCode, Long validityTimeMillis, Long timestamp) {
        if (!connected) {
            return -1L;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {

                    Long res = -1L;
                    String query = "MATCH (a:Person { fiscalCode: $fiscalCode }), "
                            + "(b:Person { "
                            + "}), "
                            + "p = shortestPath((a)-[r:visited*..30]-(b)) "
                            + "WHERE a.fiscalCode <> b.fiscalCode AND b.timestampInfected >$val1 AND b.timestampHealed < $val2 AND all(rel in relationships(p) WHERE rel.timestamp > $val1) "
                            + "RETURN length(p) AS length ORDER BY length(p) LIMIT 1";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("fiscalCode", fiscalCode);
                    map.put("val1", timestamp - validityTimeMillis);
                    map.put("val2", timestamp);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return 0L;
                    }
                    Record r = result.next();
                    res = r.get("length").asLong();
                    return res;
                }
            });
        }
    }

    /**
     * Find the top K most riskful place that a user has visited
     *
     * @param fiscalCode fiscal code of the starting person
     * @param numberOfNodes number of nodes to return (i.e the function retrieve
     * the top "numberOfNodes" places)
     * @param timestamp the starting timestamp
     * @param validityTimeMillis interval of time on which relationships are
     * relevant from the starting timestamp
     * @return list of the most riskful places for the given user, null in case
     * of errors.
     */
    public ArrayList<Place> userMostRiskfulPlace(String fiscalCode, Long numberOfNodes, Long validityTimeMillis, Long timestamp) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<ArrayList<Place>>() {
                @Override
                public ArrayList<Place> execute(Transaction tx) {
                    ArrayList<Place> list = new ArrayList<>();
                    String query = "MATCH (a:Person)-[k:visited]->(b:Place) "
                            + "WHERE a.fiscalCode = $fiscalCode AND k.timestamp > $val1 AND b.type <> $houseTypeIdentificator "
                            + "RETURN b AS place "
                            + "ORDER BY b.infectionRisk "
                            + "LIMIT $numb";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("fiscalCode", fiscalCode);
                    map.put("numb", numberOfNodes);
                    map.put("val1", timestamp - validityTimeMillis);
                    map.put("houseTypeIdentificator", Place.HOUSE_TYPE_IDENTIFICATOR);

                    Result result = tx.run(query, map);
                    while (result.hasNext()) {
                        Record r = result.next();
                        list.add(new Place(r.get("place").asMap()));
                    }

                    return list;
                }
            });
        }
    }

    /**
     * Retrieve risk of infection of a given place
     *
     * @param name the starting place on which the query must be performed
     * @return risk index of the given place, -1.0 in case of errors.
     */
    public Double riskOfInfectionIndex(String name) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Double>() {
                @Override
                public Double execute(Transaction tx) {
                    Double res = 0.0;
                    String query = "MATCH (a:Place {name: $name}) RETURN a.infectionRisk AS infectionIndex";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return -1.0;
                    }
                    Record r = result.next();
                    res = r.get("infectionIndex").asDouble();
                    return res;
                }
            });
        }

    }

    /**
     * Find the most riskful places in the overall database
     *
     * @param max the top "max" places are retrieved
     * @return list of the most riskful places in the overall database, null in
     * case of errors.
     */
    public ArrayList<Place> mostCriticalPlace(Long max) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<ArrayList<Place>>() {
                @Override
                public ArrayList<Place> execute(Transaction tx) {
                    ArrayList<Place> list = new ArrayList<>();
                    String query = "MATCH (a:Place) WHERE a.type <> $houseTypeIdentificator RETURN a AS place ORDER BY a.infectionRisk LIMIT $max";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("max", max);
                    map.put("houseTypeIdentificator", Place.HOUSE_TYPE_IDENTIFICATOR);
                    Result result = tx.run(query, map);

                    while (result.hasNext()) {
                        Record r = result.next();
                        list.add(new Place(r.get("place").asMap()));
                    }

                    return list;
                }
            });
        }
    }

    /**
     * Set to infected a person by name
     *
     * @param fiscalCode the fiscal code of the starting person 
     * @param timestampInfectedMills timestamp on which the person was
     * recognized as infected
     * @return true on success, false otherwise
     */
    public Boolean userUpdateStatus_infected(String fiscalCode, Long timestampInfectedMills) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person {fiscalCode: $fiscalCode }) "
                            + "SET a.timestampInfected = $time";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fiscalCode", fiscalCode);
                    map.put("time", timestampInfectedMills);
                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /**
     * Set to healed a person
     *
     * @param fiscalCode the person fiscal code
     * @param timestampHealedMills timestamp on which the person was recognized
     * as healed
     * @return true on success, false otherwise
     */
    public Boolean userUpdateStatus_healed(String fiscalCode, Long timestampHealedMills) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person {fiscalCode: $fiscalCode }) "
                            + "SET a.timestampHealed = $time";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fiscalCode", fiscalCode);
                    map.put("time", timestampHealedMills);
                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /**
     * Compute infection risk index of a given place by name
     *
     * @param namePlace the place name
     * @param now the starting timestamp
     * @param validityTimeMills interval of time on which relationship entering
     * the Place node are relevant
     * @return infection risk index of the given place, -1.0 in case of errors.
     */
    private Double computeRiskInfection(String namePlace, Long validityTimeMills, Long now) {
        if (!connected) {
            return -1.0;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Double>() {
                @Override
                public Double execute(Transaction tx) {
                    Long placeArea = 0L;
                    ArrayList<Person> listPerson = new ArrayList<>();
                    ArrayList<Long> listTimestamp = new ArrayList<>();

                    String query = "MATCH (a:Person), (b:Place {name: $namePlace}),  ((a)-[r:visited]->(b)) WHERE r.timestamp > $val1 RETURN b.area AS placeArea, r.timestamp AS timestamp, a AS Person";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("namePlace", namePlace);
                    map.put("val1", now - validityTimeMills);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return -1.0;
                    }
                    while (result.hasNext()) {
                        Record r = result.next();
                        placeArea = r.get("placeArea").asLong();
                        listPerson.add(new Person(r.get("Person").asMap()));
                        listTimestamp.add(r.get("timestamp").asLong());
                    }
                    InfectionRiskCalculator calculator = new InfectionRiskCalculator(placeArea);
                    for (int i = 0; i < listTimestamp.size(); ++i) {
                        Long timestamp = listTimestamp.get(i);
                        Person person = listPerson.get(i);
                        boolean isInfected = false;
                        if (timestamp > person.getTimestampInfected() - validityTimeMills && timestamp < person.getTimestampHealed()) {
                            isInfected = true;
                        }
                        calculator.addIncomingArc(isInfected, timestamp);
                    }

                    return calculator.getInfectionRisk();
                }
            });
        }
    }

    /**
     * Compute infection risk index of a given place by name
     *
     * @param tx the currenctly opened Transaction
     * @param namePlace the place name
     * @param now the starting timestamp
     * @param validityTimeMills interval of time on which relationship entering
     * the Place node are relevant
     * @return infection risk index of the given place, -1.0 in case of errors.
     */
    private Double computeRiskInfection(Transaction tx, String namePlace, Long validityTimeMills, Long now) {
        if (!connected) {
            return -1.0;
        }

        Long placeArea = 0L;
        ArrayList<Person> listPerson = new ArrayList<>();
        ArrayList<Long> listTimestamp = new ArrayList<>();

        String query = "MATCH (a:Person), (b:Place {name: $namePlace}),  ((a)-[r:visited]->(b)) WHERE r.timestamp > $val1 RETURN b.area AS placeArea, r.timestamp AS timestamp, a AS Person";
        HashMap<String, Object> map = new HashMap<>();
        map.put("namePlace", namePlace);
        map.put("val1", now - validityTimeMills);
        Result result = tx.run(query, map);
        if (!result.hasNext()) {
            return -1.0;
        }
        while (result.hasNext()) {
            Record r = result.next();
            placeArea = r.get("placeArea").asLong();
            listPerson.add(new Person(r.get("Person").asMap()));
            listTimestamp.add(r.get("timestamp").asLong());
        }
        InfectionRiskCalculator calculator = new InfectionRiskCalculator(placeArea);
        for (int i = 0; i < listTimestamp.size(); ++i) {
            Long timestamp = listTimestamp.get(i);
            Person person = listPerson.get(i);
            boolean isInfected = false;
            if (timestamp > person.getTimestampInfected() - validityTimeMills && timestamp < person.getTimestampHealed()) {
                isInfected = true;
            }
            calculator.addIncomingArc(isInfected, timestamp);
        }

        return calculator.getInfectionRisk();
    }
    
    /**
     * For all places (that are not houses) update the infectionRisk
     */
    public void updateRisksOfInfectionForPlaces() {
        if (!connected) {
            return;
        }
        try (Session session = driver.session()) {
            session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    HashMap<Integer,Double> mapPlaceIdRisk = new HashMap();
                    
                    String query = "MATCH (a:Place) WHERE a.type <> $houseIdenfifier RETURN a.name as name, ID(a) as id";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("houseIdenfifier", Place.HOUSE_TYPE_IDENTIFICATOR);
                    Result result = tx.run(query,map);
                    if (!result.hasNext()) {
                        return false;
                    }
                    while (result.hasNext()) {
                        Record r = result.next();
                        String placeName = r.get("name").asString();
                        Integer placeId = r.get("id").asInt();
                        Double risk = computeRiskInfection(tx, placeName, Configuration.getValidityPeriod(), Configuration.now());
                        if(risk == -1.0d)
                            continue;
                        mapPlaceIdRisk.put(placeId,risk);
                    }
                    
                    for(Integer placeId : mapPlaceIdRisk.keySet()) {
                        String query2 = "MATCH (a:Place) WHERE ID(a) = $id SET a.infectionRisk = $risk";
                        HashMap<String,Object> map2 = new HashMap<>();
                        map2.put("risk",mapPlaceIdRisk.get(placeId));
                        map2.put("id",placeId);
                        tx.run(query2,map2);
                    }
                    
                    return true;
                }
            });
        }
    }
    
    /**
     * Count the number of healed people
     *
     * @return number of healed people, -1 in case of errors.
     */
    public Long numberOfHealed() {
        if (!connected) {
            return -1L;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {
                    String query = "MATCH (a:Person) "
                            + "WHERE a.timestampInfected <> 0 AND a.timestampHealed <> 0"
                            + " RETURN count(a) as howmuch";
                    Result result = tx.run(query);
                    if (!result.hasNext()) {
                        return -1L;
                    }
                    Record r = result.next();
                    Long res = r.get("howmuch").asLong();
                    return res;
                }
            });
        }
    }

    /* ################################### END QUERY ########################################## */
}
