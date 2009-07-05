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
package org.thiesen.hhpt.geolookup.rtree;

import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.StationFinderBase;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

import android.location.Location;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

public class RTreeStationFinder extends StationFinderBase implements StationFinder {

    private final RTree _tree = new RTree();
    
    @Override
    protected void indexOneStation( final Station s ) {
        _tree.add( makeRectangle( s ), s.getId().intValue() );
    }

    private Rectangle makeRectangle( final Station s ) {
        final float x = s.getPosition().getLatitude().floatValue();
        final float y = s.getPosition().getLongitude().floatValue();
        
        final Rectangle r = new Rectangle( x, y, x, y );
        return r;
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
        return null;
        
        
        
    }

    
    
    
}
