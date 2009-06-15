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


public class Longitude implements Serializable {

    private final Double _valueInDegrees;
    
    private Longitude( final Double valueInDegrees ) {
        _valueInDegrees = valueInDegrees;
    }
    
    public static Longitude valueOf( final double valueInDegrees ) {
        return valueOf( Double.valueOf( valueInDegrees ) );
    }
    
    public static Longitude valueOf( final Double valueInDegrees ) {
        return new Longitude( valueInDegrees );
    }
    
    public String asSolrQueryPart() {
       return "&long=" + _valueInDegrees;
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
