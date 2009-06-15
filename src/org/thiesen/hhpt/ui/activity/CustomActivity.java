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
package org.thiesen.hhpt.ui.activity;

import org.thiesen.hhpt.common.GlobalConstants;
import org.thiesen.hhpt.config.CustomPreferences;

import android.app.Activity;

public class CustomActivity extends Activity {

    protected CustomPreferences preferences() {
        return new CustomPreferences(  getSharedPreferences( GlobalConstants.MY_PREFS,  0 ) );
    }
    
    
}
