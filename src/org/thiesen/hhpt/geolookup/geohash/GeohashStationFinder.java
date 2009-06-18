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
package org.thiesen.hhpt.geolookup.geohash;

import java.io.IOException;
import java.io.InputStream;

import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.shared.utils.GeoHashUtils;

public class GeohashStationFinder implements StationFinder  {
    
    private final TreeNode _rootNode = new TreeNode();
    
    public void createIndex( final InputStream stations ) throws IOException {
        
        final long startTime = System.currentTimeMillis();
        
        final StationReader reader = new StationReader( stations );

        for ( final Station s : reader ) {
            
            final int[] poshash = GeoHashUtils.convert( s.getGeoHashValue() );
        
            _rootNode.addNode( poshash, s );
            
        }
        
        System.out.println("Indexing took " + ( System.currentTimeMillis() - startTime ) + " ms" );

        
    }


    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles )  {
        final long startTime = System.currentTimeMillis();

        final String geohash = GeoHashUtils.encode( lat, lon );
        
        System.out.println( "Looking for " + geohash );
        
        final int[] poshash = GeoHashUtils.convert( geohash );

        Stations retval = _rootNode.lookup( poshash );
        
        if ( retval == null ) {
            retval = new Stations(  );
        }
        
        System.out.println( "Lookup returned " + retval.size() + " stations" );
        
        final DistanceApproximation approx = new DistanceApproximation();
        
        approx.setTestPoint( lat, lon );
        
        final Stations filtered = new Stations();
        
        for ( final Station s : retval ) {
            final double d = Math.sqrt( approx.getDistanceSq( s.getPosition().getLatitude().doubleValue(), s.getPosition().getLongitude().longValue()  ) );
                    
            if ( d < defaultSearchRadiusMiles ) {
                filtered.add( s );
            }
            
        }
        
        
        System.out.println("Searching took " + ( System.currentTimeMillis() - startTime ) + ", found " + filtered.size() +  " stations" );
        
        return filtered;
    }



}
