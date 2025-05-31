package models;

/**
 * City Model
 * Represents a city in the delivery network
 */
public class City {
    private int id;
    private String name;
    private String province;
    private double latitude;
    private double longitude;
    private int population;

    // Default constructor
    public City() {}

    // Basic constructor
    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Constructor with province
    public City(int id, String name, String province) {
        this.id = id;
        this.name = name;
        this.province = province;
    }

    // Full constructor
    public City(int id, String name, String province, double latitude, double longitude, int population) {
        this.id = id;
        this.name = name;
        this.province = province;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    // Business methods
    public String getFullName() {
        return province != null ? name + ", " + province : name;
    }

    public boolean isMetroCity() {
        return population > 1000000; // Cities with population > 1M
    }

    @Override
    public String toString() {
        return String.format("City{id=%d, name='%s', province='%s', population=%d}", 
                           id, name, province, population);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        City city = (City) obj;
        return id == city.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
