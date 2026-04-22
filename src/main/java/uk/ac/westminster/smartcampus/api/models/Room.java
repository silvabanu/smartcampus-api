/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uk.ac.westminster.smartcampus.api.models;

/**
 *
 * @author banuka
 */
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {

    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new CopyOnWriteArrayList<String>();

    public Room() {
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds == null
                ? new CopyOnWriteArrayList<String>()
                : new CopyOnWriteArrayList<String>(sensorIds);
    }
}
