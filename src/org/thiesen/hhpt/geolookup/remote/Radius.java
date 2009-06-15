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


public class Radius {
    
    private final Miles _value;

    private Radius( final Miles value  ) {
        _value = value;
    }
    
    public static Radius valueOf( final double value ) {
        return valueOf( Double.valueOf( value ) );
    }
    
    public static Radius valueOf( final Double value ) {
        return valueOf( Miles.valueOf( value ) );
    }
    
    
    public static Radius valueOf( final Miles value ) {
        return new Radius( value );
    }
    
    public String asSolrQueryPart() {
        return "&radius=" + _value.getValue();
    }
    
    
    
}
