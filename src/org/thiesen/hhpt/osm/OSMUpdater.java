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
package org.thiesen.hhpt.osm;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class OSMUpdater {

    private final static String OSM_SOURCE = "http://download.geofabrik.de/osm/europe/germany/hamburg.osm.bz2";

    public static Stations getNewOSMStations() throws IOException {
        final InputStream osmStream = getOSMInputStream(); 

        try {

            final XmlPullParserFactory factory = XmlPullParserFactory.newInstance(
                    System.getProperty(XmlPullParserFactory.PROPERTY_NAME), null);
            factory.setNamespaceAware(true);
            final XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( osmStream, "utf8" );

            final OSMPullParser opp = new OSMPullParser( xpp );
            
            final Stations stations =  opp.processDocument();
            
            System.out.println("Found " + stations.size() + " stations");

            return stations;


        } catch ( final Exception e ) {
            e.printStackTrace();
            return new Stations();
        }
    }



    private static InputStream getOSMInputStream() throws ClientProtocolException, IOException {
        final HttpClient client = new DefaultHttpClient();

        final HttpGet get = new HttpGet( OSM_SOURCE );
        
        
        final HttpResponse response = client.execute( get );

        final HttpEntity entity = response.getEntity();

        if ( entity != null ) {
            final InputStream content = entity.getContent();

            if ( content != null ) {

                content.read( new byte[2], 0, 2 ); // Skip first two bytes, because they are invalid

                final CBZip2InputStream bzipStream = new CBZip2InputStream( new BufferedInputStream( content ) );

                return bzipStream;
            }
        }

        return null;
    }




}
