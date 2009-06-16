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
package org.thiesen.hhpt.geolookup.remote;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

public class GeoClient {

    // /solr/select?&qt=geo&lat=xx.xx&long=yy.yy&q=abc&radius=zz
    //private final static String BASE_URL = "http://lindan:8080/localsolr/select?qt=geo&q=type:PUBLIC_TRANSPORT&wt=json&rows=150";
    private final static String BASE_URL = "http://androit.dyndns.org:8080/localsolr/select?qt=geo&q=type:PUBLIC_TRANSPORT&wt=json&rows=200";

    public static Stations makeGeoLookup( final Latitude lat, final Longitude lon, final Radius r ) throws LookupException {
        final HttpClient client = new DefaultHttpClient();

        final String url = makeQueryUrl( lat, lon, r );

        final HttpGet method = new HttpGet( url.toString() );

        System.out.println("Url: " + url.toString() );

        try {

            final ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String httpResponse = client.execute( method, responseHandler );

            final JSONObject object = new JSONObject( httpResponse );

            final JSONObject response = object.getJSONObject( "response" );
            
            final JSONArray docs = response.getJSONArray( "docs" );
            
            final Stations stations = new Stations();
            
            for ( int i = 0; i < docs.length(); i++ ) {
                stations.add( convert( docs.getJSONObject(  i ) ) );
            }
            
            return stations;

        } catch ( final IOException e ) {
            throw new LookupException( e );
        } catch ( final JSONException e ) {
            throw new LookupException( e );
        }


      
    }

    private static Station convert( final JSONObject object ) throws JSONException {
        return Station.createStation( 
                object.getString( "id" ),
                object.getString( "lat" ),
                object.getString( "lng" ),
                object.getString( "stationType" ),
                object.getString( "stationName" ),
                object.getString( "operator" )
        );
        
    }

    private static String makeQueryUrl( final Latitude lat, final Longitude lon, final Radius r ) {
        final StringBuilder url = new StringBuilder( BASE_URL );

        url.append( lat.asSolrQueryPart() );
        url.append( lon.asSolrQueryPart() );
        url.append( r.asSolrQueryPart() );
        return url.toString();
    }

    static Stations makeGeoLookup( final double lat, final double lon, final double radius ) throws LookupException {
        return makeGeoLookup( Latitude.valueOf( lat ), Longitude.valueOf( lon ), Radius.valueOf( radius ) );
    }


}
