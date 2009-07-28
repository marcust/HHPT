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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.spatial.geometry.shape.Point2D;
import org.apache.lucene.spatial.tier.DistanceUtils;
import org.thiesen.hhpt.geolookup.LookupException;
import org.thiesen.hhpt.geolookup.StationFinder;
import org.thiesen.hhpt.geolookup.StationFinderBase;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.StationId;
import org.thiesen.hhpt.shared.model.station.Stations;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

public class RTreeStationFinder extends StationFinderBase implements StationFinder {

    private final RTree _tree = new RTree();
    private final Map<StationId, Station> _stationsById = new HashMap<StationId, Station>();
    
    public RTreeStationFinder() {
        _tree.init( new Properties() );
    }
    
    @Override
    protected void indexOneStation( final Station s ) 
    {
        
        final StationId id = s.getId();
        _tree.add( makeRectangle( s ), id.intValue() );
        _stationsById.put( id, s );
    }

    private Rectangle makeRectangle( final Station s ) {
        final float x = s.getPosition().getLatitude().floatValue();
        final float y = s.getPosition().getLongitude().floatValue();
        
        final Rectangle r = new Rectangle( x, y, x + 1, y + 1 );
        return r;
    }

    public Stations makeGeoLookup( final double lat, final double lon, final double defaultSearchRadiusMiles ) throws LookupException {
        final org.apache.lucene.spatial.geometry.shape.Rectangle boundary = DistanceUtils.getInstance().getBoundary( lat, lon, 1000 );
        
        final CustomProcedure procedure = new CustomProcedure( _stationsById );
        _tree.intersects( convertRectangle( boundary ), procedure );
        
        
        return procedure.getStations();
    }

    private Rectangle convertRectangle( final org.apache.lucene.spatial.geometry.shape.Rectangle boundary ) {
        final Point2D minPoint = boundary.getMinPoint();
        final Point2D maxPoint = boundary.getMaxPoint();
        return new Rectangle( (float)minPoint.getX(), (float)minPoint.getY(), (float)maxPoint.getX(), (float)maxPoint.getY() );
        
    }

    
    
    
}
