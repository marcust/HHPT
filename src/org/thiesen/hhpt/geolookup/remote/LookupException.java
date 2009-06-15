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


public class LookupException extends Exception {

    public LookupException() {
        super();
    }

    public LookupException( final String message, final Throwable cause ) {
        super( message, cause );
    }

    public LookupException( final String message ) {
        super( message );
    }

    public LookupException( final Throwable cause ) {
        super( cause );
    }

    
}
