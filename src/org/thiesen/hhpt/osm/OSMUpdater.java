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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.thiesen.hhpt.shared.model.station.StationType;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.shared.model.tag.StationTypeTagKey;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OSMUpdater {

    private final static String OSM_SOURCE = "http://download.geofabrik.de/osm/europe/germany/hamburg.osm.bz2";

    public static Stations getNewOSMStations() throws IOException {
        final InputStream osmStream = getOSMInputStream(); 

        final Document doc = parseXmlFile( osmStream );

        final Element root = getRoot( doc );

        final Stations stations = new Stations();

        final NodeList list = root.getChildNodes();
        for (int i=0; i<list.getLength(); i++) {
            final Node node = list.item( i );
            if (node instanceof Element) {
                addStationIfValid( (Element)node, stations );        
            }
        }

        System.out.println("Found " + stations.size() + " stations");

        return stations;
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


    private static void addStationIfValid( final Element element, final Stations stations ) {
        if ( "node".equalsIgnoreCase( element.getTagName() ) ) {
            final String id = element.getAttribute( "id" );
            final String longitude = element.getAttribute( "lon" );
            final String latitude = element.getAttribute( "lat" );
            String name = null;
            StationType type = null;
            String operator = null;
            final NodeList childs = element.getChildNodes();
            for (int i=0; i< childs.getLength(); i++) {
                final Node node = childs.item( i );
                if (node instanceof Element) {
                    final Element el = (Element) node;

                    final XMLTag t = XMLTag.valueOf( el.getAttribute( "k" ), el.getAttribute( "v" ) );
                    if ( isOpertor( t ) ) {
                        operator = t.getValue();
                    }
                    if ( isName( t ) ) {
                        name = t.getValue();
                    }
                    if ( isStationType( t ) ) {
                        type = StationType.valueOf( t.getKey(), t.getValue() );
                    }

                }
            }

            if ( type != null ) {
                System.out.println("Added " + type + " with name " + name );
                stations.add( id, latitude, longitude, type, name, operator );
            }


        }
    }


    private static boolean isStationType( final XMLTag t ) {
        return t.keyAnyOf( StationTypeTagKey.toXMLKeys() );

    }

    private static boolean isName( final XMLTag t ) {
        return t.keyIs( "name" );
    }

    private static boolean isOpertor( final XMLTag t ) {
        return t.keyIs( "operator" );
    }

    private static Element getRoot( final Document doc ) {
        // Get the XML root node by examining the children nodes
        final NodeList list = doc.getChildNodes();
        for (int i=0; i<list.getLength(); i++) {
            final Node node = list.item( i );
            if (node instanceof Element) {
                return (Element) node;
            }
        }

        return null;
    }

    public static Document parseXmlFile(final InputStream input ) {
        try {
            // Create a builder factory
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);

            // Create the builder and parse the file
            final Document doc = factory.newDocumentBuilder().parse( input );
            return doc;
        } catch (final SAXException e) {
            handleException( e );
        } catch (final ParserConfigurationException e) {
            handleException( e );
        } catch (final IOException e) {
            handleException( e );
        }
        return null;
    }

    private static void handleException( final Exception e ) {
        e.printStackTrace();

    }

    
    

}
