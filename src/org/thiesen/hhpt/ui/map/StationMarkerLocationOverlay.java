/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of HHPT.
 *
 *  HHPT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  HHPT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with HHPT.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesen.hhpt.ui.map;

import java.util.Iterator;

import org.thiesen.hhpt.common.GeoLocationUtils;
import org.thiesen.hhpt.shared.model.station.Station;
import org.thiesen.hhpt.shared.model.station.Stations;
import org.thiesen.hhpt.ui.activity.StationDetailsActivity;
import org.thiesen.hhpt.ui.activity.main.MainActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class StationMarkerLocationOverlay extends Overlay {

	//  Store these as global instances so we don't keep reloading every time
    private final Bitmap _busBmp;
    private final Bitmap _trainBmp;
    
    private final Stations _mapLocations;
    
	private Paint innerPaint, borderPaint, textPaint;
    
    //  The currently selected Map Location...if any is selected.  This tracks whether an information  
    //  window should be displayed & where...i.e. whether a user 'clicked' on a known map location
    private Station _selectedMapLocation;  
    
    private final MainActivity _shower;
    
	public StationMarkerLocationOverlay( final MainActivity shower, final Stations locations, final Bitmap busBmp, final Bitmap trainBmp ) {
	    _shower = shower;
	    
		_mapLocations = locations;
		
		_busBmp = busBmp;
		_trainBmp = trainBmp;
		
	}
	
	@Override
	public boolean onTap(final GeoPoint p, final MapView	mapView)  {
		
		//  Store whether prior popup was displayed so we can call invalidate() & remove it if necessary.
	    //final boolean isRemovePriorPopup = _selectedMapLocation != null;  

		//  Next test whether a new popup should be displayed
		_selectedMapLocation = getHitMapLocation(mapView,p);
		//		if ( isRemovePriorPopup || selectedMapLocation != null) {
		    //mapView.invalidate();
			
		if ( _selectedMapLocation != null ) {
		    final Intent intent = new Intent( _shower.getApplicationContext(), StationDetailsActivity.class );
		    intent.putExtra( Station.STATION, _selectedMapLocation );
		    _shower.startActivity( intent );
		    
		}
		//}		
		
		//  Lastly return true if we handled this onTap()
		return _selectedMapLocation != null;
	}
	
    @Override
    public void draw(final Canvas canvas, final MapView	mapView, final boolean shadow) {
    	
   		drawMapLocations(canvas, mapView, shadow);
   		//drawInfoWindow(canvas, mapView, shadow);
    }

    /**
     * Test whether an information balloon should be displayed or a prior balloon hidden.
     */
    private Station getHitMapLocation(final MapView	mapView, final GeoPoint tapPoint) {
    	
    	//  Track which MapLocation was hit...if any
        Station hitMapLocation = null;
		
    	final RectF hitTestRecr = new RectF();
		final Point screenCoords = new Point();
    	final Iterator<Station> iterator = _mapLocations.iterator();
    	while(iterator.hasNext()) {
    		final Station testLocation = iterator.next();
    		
    		//  Translate the MapLocation's lat/long coordinates to screen coordinates
    		mapView.getProjection().toPixels(GeoLocationUtils.pointFromStation( testLocation ), screenCoords);

	    	// Create a 'hit' testing Rectangle w/size and coordinates of our icon
	    	// Set the 'hit' testing Rectangle with the size and coordinates of our on screen icon
    		hitTestRecr.set(-_trainBmp.getWidth()/2,-_trainBmp.getHeight(),_trainBmp.getWidth()/2,0);
    		hitTestRecr.offset(screenCoords.x,screenCoords.y);

	    	//  Finally test for a match between our 'hit' Rectangle and the location clicked by the user
    		mapView.getProjection().toPixels(tapPoint, screenCoords);
    		if (hitTestRecr.contains(screenCoords.x,screenCoords.y)) {
    			hitMapLocation = testLocation;
    			break;
    		}
    	}
    	
    	//  Lastly clear the newMouseSelection as it has now been processed
    	//tapPoint = null;
    	
    	return hitMapLocation; 
    }
    
    private void drawMapLocations(final Canvas canvas, final MapView	mapView, final boolean shadow) {
    	
		final Iterator<Station> iterator = _mapLocations.iterator();
		final Point screenCoords = new Point();
    	while(iterator.hasNext()) {	   
    		final Station location = iterator.next();
    		mapView.getProjection().toPixels(GeoLocationUtils.pointFromStation( location ), screenCoords);
			
	    	if (shadow) {
	    		//  Only offset the shadow in the y-axis as the shadow is angled so the base is at x=0; 
	    	    // canvas.drawBitmap(shadowIcon, screenCoords.x, screenCoords.y - shadowIcon.getHeight(),null);
	    	} else {
	    	    if ( location.isBus() ) {
	    	        canvas.drawBitmap(_busBmp, screenCoords.x - _busBmp.getWidth()/2, screenCoords.y - _busBmp.getHeight(),null);
	    	    } 
	    	    if ( location.isTrain() ) {
	    	        canvas.drawBitmap(_trainBmp, screenCoords.x - _trainBmp.getWidth()/2, screenCoords.y - _trainBmp.getHeight(),null);
	    	    }
	    	}
    	}
    }

//    private void drawInfoWindow(final Canvas canvas, final MapView	mapView, final boolean shadow) {
//    	
//    	if ( _selectedMapLocation != null) {
//    		if ( shadow) {
//    			//  Skip painting a shadow in this tutorial
//    		} else {
//				//  First determine the screen coordinates of the selected MapLocation
//				final Point selDestinationOffset = new Point();
//				mapView.getProjection().toPixels( GeoLocationUtils.pointFromStation( _selectedMapLocation ), selDestinationOffset);
//		    	
//		    	//  Setup the info window with the right size & location
//				final int INFO_WINDOW_WIDTH = 125;
//				final int INFO_WINDOW_HEIGHT = 25;
//				final RectF infoWindowRect = new RectF(0,0,INFO_WINDOW_WIDTH,INFO_WINDOW_HEIGHT);				
//				final int infoWindowOffsetX = selDestinationOffset.x-INFO_WINDOW_WIDTH/2;
//				final int infoWindowOffsetY = selDestinationOffset.y-INFO_WINDOW_HEIGHT-_trainBmp.getHeight();
//				infoWindowRect.offset(infoWindowOffsetX,infoWindowOffsetY);
//
//				//  Draw inner info window
//				canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
//				
//				//  Draw border for info window
//				canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
//					
//				//  Draw the MapLocation's name
//				final int TEXT_OFFSET_X = 10;
//				final int TEXT_OFFSET_Y = 15;
//				canvas.drawText(_selectedMapLocation.getName(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y,getTextPaint());
//			}
//    	}
//    }

	public Paint getInnerPaint() {
		if ( innerPaint == null) {
			innerPaint = new Paint();
			innerPaint.setARGB(225, 75, 75, 75); //gray
			innerPaint.setAntiAlias(true);
		}
		return innerPaint;
	}

	public Paint getBorderPaint() {
		if ( borderPaint == null) {
			borderPaint = new Paint();
			borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);
		}
		return borderPaint;
	}

	public Paint getTextPaint() {
		if ( textPaint == null) {
			textPaint = new Paint();
			textPaint.setARGB(255, 255, 255, 255);
			textPaint.setAntiAlias(true);
		}
		return textPaint;
	}
}