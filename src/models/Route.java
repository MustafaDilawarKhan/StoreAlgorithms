package models;

/**
 * Route Model
 * Represents a route between two cities with distance
 */
public class Route {
    private int id;
    private int fromCityId;
    private String fromCityName;
    private int toCityId;
    private String toCityName;
    private int distance; // in kilometers
    private String roadType; // highway, city_road, etc.
    private boolean isBidirectional;

    // Default constructor
    public Route() {
        this.isBidirectional = true; // Most routes are bidirectional
    }

    // Basic constructor
    public Route(int fromCityId, int toCityId, int distance) {
        this.fromCityId = fromCityId;
        this.toCityId = toCityId;
        this.distance = distance;
        this.isBidirectional = true;
    }

    // Constructor with city names
    public Route(int fromCityId, String fromCityName, int toCityId, String toCityName, int distance) {
        this.fromCityId = fromCityId;
        this.fromCityName = fromCityName;
        this.toCityId = toCityId;
        this.toCityName = toCityName;
        this.distance = distance;
        this.isBidirectional = true;
    }

    // Full constructor
    public Route(int id, int fromCityId, String fromCityName, int toCityId, String toCityName, 
                 int distance, String roadType, boolean isBidirectional) {
        this.id = id;
        this.fromCityId = fromCityId;
        this.fromCityName = fromCityName;
        this.toCityId = toCityId;
        this.toCityName = toCityName;
        this.distance = distance;
        this.roadType = roadType;
        this.isBidirectional = isBidirectional;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromCityId() {
        return fromCityId;
    }

    public void setFromCityId(int fromCityId) {
        this.fromCityId = fromCityId;
    }

    public String getFromCityName() {
        return fromCityName;
    }

    public void setFromCityName(String fromCityName) {
        this.fromCityName = fromCityName;
    }

    public int getToCityId() {
        return toCityId;
    }

    public void setToCityId(int toCityId) {
        this.toCityId = toCityId;
    }

    public String getToCityName() {
        return toCityName;
    }

    public void setToCityName(String toCityName) {
        this.toCityName = toCityName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public boolean isBidirectional() {
        return isBidirectional;
    }

    public void setBidirectional(boolean bidirectional) {
        isBidirectional = bidirectional;
    }

    // Business methods
    public boolean connectsCities(int cityId1, int cityId2) {
        if (isBidirectional) {
            return (fromCityId == cityId1 && toCityId == cityId2) || 
                   (fromCityId == cityId2 && toCityId == cityId1);
        } else {
            return fromCityId == cityId1 && toCityId == cityId2;
        }
    }

    public int getOtherCity(int cityId) {
        if (fromCityId == cityId) {
            return toCityId;
        } else if (toCityId == cityId && isBidirectional) {
            return fromCityId;
        }
        return -1; // City not connected by this route
    }

    public String getRouteDescription() {
        String arrow = isBidirectional ? " ↔ " : " → ";
        return fromCityName + arrow + toCityName + " (" + distance + " km)";
    }

    @Override
    public String toString() {
        return String.format("Route{from='%s', to='%s', distance=%d km, bidirectional=%s}", 
                           fromCityName, toCityName, distance, isBidirectional);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Route route = (Route) obj;
        return id == route.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
