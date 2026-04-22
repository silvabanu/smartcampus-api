/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.storage;

/**
 *
 * @author banuka
 */
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import uk.ac.westminster.smartcampus.api.models.Room;
import uk.ac.westminster.smartcampus.api.models.Sensor;
import uk.ac.westminster.smartcampus.api.models.SensorReading;

public final class CampusStore {

    private static final CampusStore INSTANCE = new CampusStore();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readingsBySensor = new ConcurrentHashMap<>();

    private CampusStore() {
    }

    public static CampusStore getInstance() {
        return INSTANCE;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, List<SensorReading>> getReadingsBySensor() {
        return readingsBySensor;
    }

    public List<SensorReading> getOrCreateReadings(String sensorId) {
        return readingsBySensor.computeIfAbsent(sensorId, key -> new CopyOnWriteArrayList<SensorReading>());
    }
}
