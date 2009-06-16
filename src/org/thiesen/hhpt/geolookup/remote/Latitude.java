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

import java.io.Serializable;


public class Latitude implements Serializable {
    
    private static final long serialVersionUID = 5369362391583755772L;
    private final Double _valueInDegrees;
    
    private Latitude( final Double valueInDegrees ) {
        _valueInDegrees = valueInDegrees;
    }
    
    
    public static Latitude valueOf( final double valueInDegrees ) {
        return valueOf( Double.valueOf( valueInDegrees ) );
    }

    public static Latitude valueOf( final Double valueInDegrees ) {
        return new Latitude( valueInDegrees );
    }

    public String asSolrQueryPart() {
        return "&lat=" + _valueInDegrees;
    }

    @Override
    public String toString() {
        return ""+_valueInDegrees;
    }


    public int getValueE6() {
        return (int)Math.round( _valueInDegrees.doubleValue() * 1E6 );
    }


    public double getDoubleValue() {
        return _valueInDegrees.doubleValue();
        
    }
}
