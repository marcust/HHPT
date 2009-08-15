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
package org.thiesen.hhpt.ui.activity.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.openintents.intents.WikitudeARIntent;
import org.openintents.intents.WikitudePOI;
import org.thiesen.hhpt.shared.model.position.Position;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;

import android.app.Application;
import android.content.Intent;


public class WikitudeARHelper {
   

    private final Application _application;
    private final String _busResourceName;
    private final String _trainResourceName;

    public WikitudeARHelper( final Application application, final String trainResourceName, final String busResourceName ) {
        _application = application;
        _trainResourceName = trainResourceName;
        _busResourceName = busResourceName;
    }

    public Intent makeWikitudeIntent( final Stations stations ) {
        return makeWikitudeIntent( convertToPOIs( stations ) );
    }
    
    public Intent makeWikitudeIntent( final Station station ) {
        return makeWikitudeIntent( convertToPOI( station ) );
    }
    
    
    private Collection<WikitudePOI> convertToPOI( final Station station ) {
       return Collections.singleton( stationToPoi( station ) );
    }

    private Intent makeWikitudeIntent( final Collection<WikitudePOI> pois ) {
        final WikitudeARIntent intent = new WikitudeARIntent( _application, null, null);
        // add the POIs (points of interest)

        intent.addPOIs(pois);
        // Add a title
        intent.addTitleText( "HHPT - Augumented Reality View" );
        return intent;
    }

    private Collection<WikitudePOI> convertToPOIs( final Stations stations ) {
        final Collection<WikitudePOI> pois = new ArrayList<WikitudePOI>();
        for ( final Station station : stations ) {
            final WikitudePOI poi = stationToPoi( station );
            pois.add(poi);
        }
        return pois;
    }

    private WikitudePOI stationToPoi( final Station station ) {
        final Position p = station.getPosition();
        final float longitude = p.getLongitude().floatValue();
        final float latitude = p.getLatitude().floatValue();
        final String name = station.getName();
        final String description = station.getType() + " "  + station.getOperator();
        
        final String iconResouce = station.isBus() ? _busResourceName : _trainResourceName;
        
        final WikitudePOI poi = new WikitudePOI(latitude, longitude, 0, name, description, iconResouce, null);
        return poi;
    }

}
