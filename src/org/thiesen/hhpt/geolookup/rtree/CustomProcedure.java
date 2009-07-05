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

import java.util.Map;

import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.StationId;
import org.thiesen.hhpt.shared.model.station.Stations;

import com.infomatiq.jsi.IntProcedure;

public class CustomProcedure implements IntProcedure {

    public final Map<StationId, Station> _stationsById;
    private final Stations _containedStations;
    
    public CustomProcedure( final Map<StationId, Station> stationsById ) {
        _stationsById = stationsById;
        _containedStations = new Stations();
    }
    
    
    public boolean execute( final int stationId ) {
        _containedStations.add( _stationsById.get( StationId.valueOf( stationId ) ) );
        return true;
    }


    public Stations getStations() {
        return _containedStations;
    }

}
