package com.lsmsdb.task3.neo4jmanager;

import com.lsmsdb.task3.beans.Person;
import com.lsmsdb.task3.beans.Place;
import com.lsmsdb.task3.computation.InfectionRiskCalculator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.neo4j.driver.SessionConfig.builder;
import org.neo4j.driver.AccessMode;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Bookmark;
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
            e.printStackTrace();
            driver.close();
        }
        try (Session session = driver.session()) {
            connected = true;
            hostUri = uri;
            port_ = port;
            hostname_ = hostname;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     *
     * @param idPerson the id of the person which want to login
     * @return the Person object, null if there are no person in the system with
     * the idPerson specified
     */
    public Person login(String idPerson) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Person>() {
                @Override
                public Person execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MATCH (a:Person { id: $id})"
                            + " RETURN a as person";
                    map.put("id", idPerson);
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
                    String query = "MERGE (a:Person { id: $id})"
                            + " ON CREATE SET a.id = $id, "
                            + " a.name = $name, "
                            + " a.surname = $surname, "
                            + " a.timestampInfected = $timestampInfected, "
                            + " a.timestampHealed = $timestampHealed "
                            + " RETURN a.id";
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("surname", p.getSurname());
                    map.put("timestampInfected", p.getTimestampInfected());
                    map.put("timestampHealed", p.getTimestampHealed());
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return false;
                    } else {
                        return true;
                    }
                }

            });
        }
    }

    /**
     * Add a Place node to the database
     *
     * @param p the place to be added
     * @return 
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
                    String query = "MERGE (a:Place { id: $id})"
                            + " ON CREATE SET a.id = $id, "
                            + " a.name = $name, "
                            + " a.infectionRisk = $infectionRisk, "
                            + " a.latitude = $latitude, "
                            + " a.longitude = $longitude, "
                            + " a.type = $type, "
                            + " a.area = $area, "
                            + " a.city = $city, "
                            + " RETURN a.id";
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("infectionRisk", p.getInfectionRisk());
                    map.put("latitude", p.getLatitude());
                    map.put("longitude", p.getLongitude());
                    map.put("type", p.getType());
                    map.put("area", p.getArea());
                    map.put("city", p.getCity());

                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return false;
                    } else {
                        return true;
                    }

                }

            });
        }
    }

    /**
     * Remove a Person from the database
     *
     * @param idPerson id of the Person to be removed
     * @return true on success, false otherwise
     */
    public Boolean removePerson(String idPerson) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person "
                            + "{ "
                            + "id: $id"
                            + "}) "
                            + "DETACH DELETE a";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPerson);

                    tx.run(query, map);
                    return true;

                }
            });
        }
    }

    /**
     * Remove a Place from the database
     *
     * @param idPlace id of the Place to be removed
     * @return true on success, false otherwise
     */
    public Boolean removePlace(String idPlace) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Place "
                            + "{ "
                            + "id: $id"
                            + "}) "
                            + "DETACH DELETE a";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPlace);

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
     * @return
     */
    public Boolean registerPersonAndCreateItsHouse(Person person, Place place) {
        if (!connected) {
            return false;
        }
        if (place.getType().compareTo("house") != 0) {
            return false;
        }

        // To collect the session bookmarks
        List<Bookmark> savedBookmarks = new ArrayList<>();

        try (Session session1 = driver.session(builder().withDefaultAccessMode( AccessMode.WRITE ).build())) {
            session1.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MERGE (a:Person { id: $id})"
                            + " ON CREATE SET a.id = $id, "
                            + " a.name = $name, "
                            + " a.surname = $surname, "
                            + " a.timestampInfected = $timestampInfected, "
                            + " a.timestampHealed = $timestampHealed "
                            + " RETURN a.id";
                    map.put("id", person.getId());
                    map.put("name", person.getName());
                    map.put("surname", person.getSurname());
                    map.put("timestampInfected", person.getTimestampInfected());
                    map.put("timestampHealed", person.getTimestampHealed());
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return false;
                    } else {
                        return true;
                    }
                }

            });
            savedBookmarks.add(session1.lastBookmark());
        }

        try (Session session2 = driver.session(builder().withDefaultAccessMode( AccessMode.WRITE ).build())) {
            session2.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    HashMap<String, Object> map = new HashMap<>();
                    String query = "MERGE (a:Place { id: $id})"
                            + " ON CREATE SET a.id = $id, "
                            + " a.name = $name, "
                            + " a.infectionRisk = $infectionRisk, "
                            + " a.latitude = $latitude, "
                            + " a.longitude = $longitude, "
                            + " a.type = $type, "
                            + " a.area = $area, "
                            + " a.city = $city, "
                            + " RETURN a.id";
                    map.put("id", place.getId());
                    map.put("name", place.getName());
                    map.put("infectionRisk", place.getInfectionRisk());
                    map.put("latitude", place.getLatitude());
                    map.put("longitude", place.getLongitude());
                    map.put("type", place.getType());
                    map.put("area", place.getArea());
                    map.put("city", place.getCity());

                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return false;
                    } else {
                        return true;
                    }

                }

            });
            savedBookmarks.add(session2.lastBookmark());
        }

        try (Session session3 = driver.session(builder().withDefaultAccessMode( AccessMode.WRITE ).withBookmarks( savedBookmarks ).build())) {
            return session3.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person), (b:Place) "
                            + "WHERE a.id = $idPerson AND b.id = $idPlace "
                            + "CREATE (a)-[ r:lives_in {timestamp: $timestamp} ] ->(b)";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("idPerson", person.getId());
                    map.put("idPlace", place.getId());
                    map.put("timestamp", System.currentTimeMillis());

                    tx.run(query, map);
                    return true;
                }

            });
        }

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
                            + "WHERE a.city = $city AND b.type <> 'house' "
                            + "RETURN b AS place "
                            + "ORDER BY b.name";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("city", city);

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
     * Return the house by idPerson
     *
     * @param idPerson the id of the owner.
     * @return the house.
     */
    public Place getHouse(String idPerson) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Place>() {
                @Override
                public Place execute(Transaction tx) {
                    Place house = null;
                    String query = "MATCH (a:Person)-[k:lives_in]->(b:Place) "
                            + "WHERE a.id = $id AND b.type <> 'house' "
                            + "RETURN b AS place";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("id", idPerson);

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
     * Function used to delete all nodes and relationships from the databse.Used only for testing.
     * @return
     */
    public Boolean clearDB() {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (n) DETACH DELETE n";
                    tx.run(query);
                    return true;
                }
            });
        }

    }

    /**
     * Insert a visit of a Person to a Place
     *
     * @param idPerson id of the person that has visited the place
     * @param idPlace id of the place that was visited
     * @param timestamp timestamp on which the visit occurred
     * @return
     */
    public Boolean visit(String idPerson, Long idPlace, Long timestamp) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person), (b:Place) "
                            + "WHERE a.id = $idPerson AND b.id = $idPlace "
                            + "CREATE (a)-[ r:visited {timestamp: $timestamp} ] ->(b)";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    //map.put("timestamp", System.currentTimeMillis());
                    map.put("idPerson", idPerson);
                    map.put("idPlace", idPlace);
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
     * @param idPerson id of the person that lives in the house
     * @param idPlace id of the place that was visited
     * @param timestamp timestamp on which the visit occurred
     * @return
     */
    public Boolean lives_in(String idPerson, Long idPlace, Long timestamp) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person), (b:Place) "
                            + "WHERE a.id = $idPerson AND b.id = $idPlace "
                            + "CREATE (a)-[ r:lives_in {timestamp: $timestamp} ] ->(b)";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    //map.put("timestamp", System.currentTimeMillis());
                    map.put("idPerson", idPerson);
                    map.put("idPlace", idPlace);
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
     * @param idPerson start person
     * @param n_hops maximum number of hops
     * @param validityTimeMillis interval of time on which each visit
     * relationships in the path are relevant
     * @return number of infected people, -1 in case of errors
     */
    public Long infectedInAGivenSocialDistance(String idPerson, Long n_hops, Long validityTimeMillis) {
        if (!connected) {
            return -1L;
        }

        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {
                    Long infectedNumber = 0L;
                    String query = "MATCH (a:Person {id: $id}), "
                            + " p=(a)-[r:visited*1.." + n_hops + "]-(c:Person) "
                            + " WHERE c.id <> a.id "
                            + " AND all(rel in relationships(p) WHERE rel.timestamp > $time1) "
                            + " AND (last(relationships(p)).timestamp > c.timestampInfected AND c.timestampInfected <> 0) "
                            + " AND (last(relationships(p)).timestamp < c.timestampHealed OR c.timestampHealed = 0) "
                            + " RETURN COUNT(DISTINCT(c)) as howmuch";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPerson);
                    //map.put("n_hops", n_hops);
                    map.put("time1", System.currentTimeMillis() - validityTimeMillis);
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
     * @param idPerson the start person
     * @param validityTimeMillis the interval of time on which each visit
     * relationships in the path are relevant
     * @return length of the path to the closest infected person, -1 in case of
     * errors.
     */
    public Long userRiskOfInfection(String idPerson, Long validityTimeMillis) {
        if (!connected) {
            return -1L;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Long>() {
                @Override
                public Long execute(Transaction tx) {

                    Long res = -1L;
                    String query = "MATCH (a:Person { id: $id }), "
                            + "(b:Person { "
                            + "}), "
                            + "p = shortestPath((a)-[r:visited*..30]-(b)) "
                            + "WHERE a.id <> b.id AND b.timestampInfected >$val1 AND b.timestampHealed < $val2 AND all(rel in relationships(p) WHERE rel.timestamp > $val1) "
                            + "RETURN length(p) AS length ORDER BY length(p) LIMIT 1";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("id", idPerson);
                    map.put("val1", System.currentTimeMillis() - validityTimeMillis);
                    map.put("val2", System.currentTimeMillis());
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
     * @param idPerson start person
     * @param numberOfNodes number of nodes to return (i.e the function retrieve
     * the top "numberOfNodes" places)
     * @param validityTimeMillis interval of time on which relationships are
     * relevant
     * @return list of the most riskful places for the given user, null in case
     * of errors.
     */
    public ArrayList<Place> userMostRiskfulPlace(String idPerson, Long numberOfNodes, Long validityTimeMillis) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<ArrayList<Place>>() {
                @Override
                public ArrayList<Place> execute(Transaction tx) {
                    ArrayList<Place> list = new ArrayList<>();
                    String query = "MATCH (a:Person)-[k:visited]->(b:Place) "
                            + "WHERE a.id = $id AND k.timestamp > $val1 AND b.type <> 'house' "
                            + "RETURN b AS place "
                            + "ORDER BY b.infectionRisk "
                            + "LIMIT $numb";
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("id", idPerson);
                    map.put("numb", numberOfNodes);
                    map.put("val1", System.currentTimeMillis() - validityTimeMillis);

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
     * @param idPlace place on which the query must be performed
     * @return risk index of the given place, -1.0 in case of errors.
     */
    public Double riskOfInfectionIndex(Long idPlace) {
        if (!connected) {
            return null;
        }
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<Double>() {
                @Override
                public Double execute(Transaction tx) {
                    Double res = 0.0;
                    String query = "MATCH (a:Place {id: $id}) RETURN a.infectionRisk AS infectionIndex";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPlace);
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
                    String query = "MATCH (a:Place) WHERE a.type <> 'house' RETURN a AS place ORDER BY a.infectionRisk LIMIT $max";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("max", max);
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
     * Set to infected a person
     *
     * @param idPerson
     * @param timestampInfectedMills timestamp on which the person was
     * recognized as infected
     * @return true on success, false otherwise
     */
    public Boolean userUpdateStatus_infected(String idPerson, Long timestampInfectedMills) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person {id: $id }) "
                            + "SET a.timestampInfected = $time";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPerson);
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
     * @param idPerson
     * @param timestampHealedMills timestamp on which the person was recognized
     * as healed
     * @return true on success, false otherwise
     */
    public Boolean userUpdateStatus_healed(String idPerson, Long timestampHealedMills) {
        if (!connected) {
            return false;
        }
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    String query = "MATCH (a:Person {id: $id }) "
                            + "SET a.timestampHealed = $time";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", idPerson);
                    map.put("time", timestampHealedMills);
                    tx.run(query, map);
                    return true;
                }
            });
        }
    }

    /**
     * Compute infection risk index of a given place
     *
     * @param idPlace
     * @param validityTimeMills interval of time on which relationship entering
     * the Place node are relevant
     * @return infection risk index of the given place, -1.0 in case of errors.
     */
    public Double computeRiskInfection(Long idPlace, Long validityTimeMills) {
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

                    String query = "MATCH (a:Person), (b:Place {id: $idPlace}),  ((a)-[r:visited]->(b)) WHERE r.timestamp > $val1 RETURN b.area AS placeArea, r.timestamp AS timestamp, a AS Person";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("idPlace", idPlace);
                    map.put("val1", System.currentTimeMillis() - validityTimeMills);
                    Result result = tx.run(query, map);
                    if (!result.hasNext()) {
                        return 0.0;
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

    /* ################################### FINE QUERY ########################################## */
}
