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


public class LookupException extends Exception {

    private static final long serialVersionUID = -7450310554926450316L;

    public LookupException() {
        super();
    }

    public LookupException( final String detailMessage, final Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public LookupException( final String detailMessage ) {
        super( detailMessage );
    }

    public LookupException( final Throwable throwable ) {
        super( throwable );
    }

    
    
}
