/*
 * $ Id $
 * (c) Copyright 2009 freiheit.com technologies gmbh
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies gmbh. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies gmbh.
 *
 * Initial version by Marcus Thiesen (marcus.thiesen@freiheit.com)
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
import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.geolookup.StationFinderBase;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.StationType;
import org.thiesen.hhpt.shared.model.station.Stations;

public class AppEngineStationFinder extends StationFinderBase {

    @Override
    protected void indexOneStation( final Station s ) {
        throw new UnsupportedOperationException("Auto generated method stub");
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
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
                    null,
                    StationType.valueOf( stationObj.getString( "stationType" ) ),
                    stationObj.getString( "stationName" ),
                    stationObj.getString( "operator" ) );
        }
 
        return retval;
    }

}
