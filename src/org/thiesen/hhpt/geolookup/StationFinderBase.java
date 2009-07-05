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
package org.thiesen.hhpt.geolookup;

import java.io.IOException;
import java.io.InputStream;

import org.thiesen.hhpt.shared.io.StationReader;
import org.thiesen.hhpt.shared.model.station.Station;

public abstract class StationFinderBase implements StationFinder {

    public void createIndex( final InputStream stations ) throws IOException {
        
        final long startTime = System.currentTimeMillis();
        
        final StationReader reader = new StationReader( stations );

        for ( final Station s : reader ) {
            indexOneStation( s );
        }
        
        System.out.println("Indexing took " + ( System.currentTimeMillis() - startTime ) + " ms with strategy " + getClass().getSimpleName() );

    }

    protected abstract void indexOneStation( Station s );
   

}
