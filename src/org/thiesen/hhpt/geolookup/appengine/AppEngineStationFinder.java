/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of HHPT.
 *
 *  HHPT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  HHPT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HHPT.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.thiesen.hhpt.geolookup.appengine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.shared.model.station.StationType;
import org.thiesen.hhpt.shared.model.station.Stations;

public class AppEngineStationFinder implements StationFinder {

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) {
        final HttpClient client = new DefaultHttpClient();
        final HttpGet get = new HttpGet( "http://hhpt-search.appspot.com/search?lat=" + lat + "&lng=" + lon +  "&radius=" + defaultSearchRadiusMiles );
        
        try {
            final ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String body = client.execute( get, responseHandler  );
            
            return parse( body );
            
            
            
            
        } catch ( final ClientProtocolException e ) {
            e.printStackTrace();
            
        } catch ( final IOException e ) {
            e.printStackTrace();
            
            
        } catch ( final JSONException e ) {
            e.printStackTrace();
        }
        
        
        
        
        return new Stations();
        

        
        
    }

    private Stations parse( final String body ) throws JSONException {
        final JSONObject obj = new JSONObject(body);
        
        final JSONArray values = obj.getJSONArray( "results" ); 

        final Stations retval = new Stations();
        for ( int i = 0; i < values.length(); i++ ) {
            final JSONObject stationObj = values.getJSONObject( i);
            
            retval.add( stationObj.getString( "id" ),
                    stationObj.getString( "lat" ),
                    stationObj.getString( "lng" ),
                    StationType.valueOf( stationObj.getString( "stationType" ) ),
                    stationObj.getString( "stationName" ),
                    stationObj.getString( "operator" ) );
        }
 
        return retval;
    }

}
