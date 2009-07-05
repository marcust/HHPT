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

import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.StationFinderBase;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.shared.utils.GeoHashUtils;

public class GeohashStationFinder extends StationFinderBase implements StationFinder  {
    
    private final TreeNode _rootNode = new TreeNode();
    
    @Override
    protected void indexOneStation( final Station s ) {
        final int[] poshash = GeoHashUtils.convert( s.getGeoHashValue() );
        
        _rootNode.addNode( poshash, s );
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
