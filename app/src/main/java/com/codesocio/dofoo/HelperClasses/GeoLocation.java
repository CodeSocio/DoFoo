 /*
  * DoFoo - A donation application
  * Copyright (C) 2021  CodeSocio
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  * Contact us at --> codesociodevs@gmail.com
  */

 package com.codesocio.dofoo.HelperClasses;
/*
 * Equation for setting for radius based search
 * acos(sin(curLat) * sin(Lat) + cos(CurLat) * cos(Lat) * cos(Lon - (curLong))) * 6371 <= searchRadius;
 *
 * Equation for getting angular radius of current search circle
 *  r = searchRadius/6371
 *
 * Computing Minimum maximum Latitude
 *  latMin = currLat - r
 *  latMax = currLat + r
 *
 * Computing Minimum Maximum Longitude
 *      latT = arcsin(sin(currLat)/cos(r))
 *      delLong = arcsin( ( cos(r) - sin(latT)*sin(currLat) ) / ( cos(latT)*cos(currLat)))
 *      delLong = arcsin(sin(r)/cos(currLat))
 *      longMin = currLong - delLong
 *      longMax = currLong - delLong
*/

public class GeoLocation {

	private double radLat;  // latitude in radians
	private double radLon;  // longitude in radians

	private double degLat;  // latitude in degrees
	private double degLon;  // longitude in degrees

	private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
	private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
	private static final double MIN_LON = Math.toRadians(-180d); // -PI
	private static final double MAX_LON = Math.toRadians(180d);  //  PI

	private GeoLocation () {
	}


	public static GeoLocation fromDegrees(double latitude, double longitude) {
		GeoLocation result = new GeoLocation();
		result.radLat = Math.toRadians(latitude);
		result.radLon = Math.toRadians(longitude);
		result.degLat = latitude;
		result.degLon = longitude;
		result.checkBounds();
		return result;
	}

	/**
	 * @param latitude the latitude, in radians.
	 * @param longitude the longitude, in radians.
	 */
	public static GeoLocation fromRadians(double latitude, double longitude) {
		GeoLocation result = new GeoLocation();
		result.radLat = latitude;
		result.radLon = longitude;
		result.degLat = Math.toDegrees(latitude);
		result.degLon = Math.toDegrees(longitude);
		result.checkBounds();
		return result;
	}

	private void checkBounds() {
		if (radLat < MIN_LAT || radLat > MAX_LAT ||
				radLon < MIN_LON || radLon > MAX_LON)
			throw new IllegalArgumentException();
	}

	/**
	 * @return the latitude, in degrees.
	 */
	public double getLatitudeInDegrees() {
		return degLat;
	}

	/**
	 * @return the longitude, in degrees.
	 */
	public double getLongitudeInDegrees() {
		return degLon;
	}

	/**
	 * @return the latitude, in radians.
	 */
	public double getLatitudeInRadians() {
		return radLat;
	}

	/**
	 * @return the longitude, in radians.
	 */
	public double getLongitudeInRadians() {
		return radLon;
	}

	@Override
	public String toString() {
		return "(" + degLat + "\u00B0, " + degLon + "\u00B0) = (" +
				 radLat + " rad, " + radLon + " rad)";
	}


	public double distanceTo(GeoLocation location, double radius) {
		return Math.acos(Math.sin(radLat) * Math.sin(location.radLat) +
				Math.cos(radLat) * Math.cos(location.radLat) *
				Math.cos(radLon - location.radLon)) * radius;
	}

	public GeoLocation[] boundingCoordinates(double distance, double radius) {

		if (radius < 0d || distance < 0d)
			throw new IllegalArgumentException();

		// angular distance in radians on a great circle
		double radDist = distance / radius;

		double minLat = radLat - radDist;
		double maxLat = radLat + radDist;

		double minLon, maxLon;
		if (minLat > MIN_LAT && maxLat < MAX_LAT) {
			double deltaLon = Math.asin(Math.sin(radDist) /
				Math.cos(radLat));
			minLon = radLon - deltaLon;
			if (minLon < MIN_LON) minLon += 2d * Math.PI;
			maxLon = radLon + deltaLon;
			if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;
		} else {
			// a pole is within the distance
			minLat = Math.max(minLat, MIN_LAT);
			maxLat = Math.min(maxLat, MAX_LAT);
			minLon = MIN_LON;
			maxLon = MAX_LON;
		}

		return new GeoLocation[]{fromRadians(minLat, minLon),
				fromRadians(maxLat, maxLon)};
	}

}
