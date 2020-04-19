/*
 * (C) Quartet FS 2007-2016
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.model;

import java.io.Serializable;

/**
 * All the information about a city
 *
 * @author Quartet FS
 */
public class City implements Serializable {

    /**
     * for serialization
     */
    private static final long serialVersionUID = -7397382479958670799L;

    /**
     * City name
     */
    protected String name;

    /**
     * Latitude of the city
     */
    protected double latitude;

    /**
     * Longitude of the city
     */
    protected double longitude;

    /**
     * Constructor
     *
     * @param cityName  The city name
     * @param latitude  Latitude of the city
     * @param longitude Longitude of the city
     */
    public City(String cityName, double latitude, double longitude) {
        this.name = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return The city name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * @return The longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        City other = (City) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
