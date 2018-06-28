package de_test;

import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;

import io.scif.img.IO;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import net.imglib2.type.numeric.integer.IntType;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.Opener;
import net.imglib2.algorithm.region.Ellipses;

public  class Test < T extends NumericType< T > > implements MouseListener, MouseWheelListener {

	private int D = 30;
	private static String path = "/media/dmitry/Data/projects/TGregor/2018-03-21 Ago2/";
	private static String fileName = "IF-1_AcquisitionBlock1_pt1-Scene-2-P2_dapi.tif";
	
	private ImagePlus imp;
	private Img< UnsignedByteType > img;
	private Img< UnsignedByteType > mask;
	private ImagePlus output;

	private ArrayList<Sphere> sphere_list = new ArrayList<>();
	
	private int cur_slice = 0;
	private int min_slice = 0;
	private int max_slice;
	
	
	
	private  < T extends RealType< T > >  Test(){
		File file = new File( new File(path), fileName );
//		img = (Img<T>) IO.openImgs( file.getAbsolutePath() ).get( 0 );
		imp = new Opener().openImage( file.getAbsolutePath() );

		max_slice = imp.getNSlices();
		
//		final ImgFactory factory = new ArrayImgFactory();
//		final Img< UnsignedByteType > mask = factory.create( img, new UnsignedByteType() );
		
		//final ImgFactory< UnsignedByteType > imgFactory = new CellImgFactory<>( new UnsignedByteType(), 3 );
		//mask = imgFactory.create( imp.getDimensions() );
		final ImgFactory< UnsignedByteType > factory  = new ArrayImgFactory< UnsignedByteType >();
		mask = factory.create( imp.getDimensions(), new UnsignedByteType() );
		
		int[] dims = imp.getDimensions();
		dims[2] = 2;
		img = factory.create( dims, new UnsignedByteType() );

		//RandomAccessibleInterval< UnsignedByteType > target_view = Views.interval( img, new long[] { 1,1, 1}, new long[]{ 100, 100, 1} );
		RandomAccessibleInterval< UnsignedByteType > target_view = Views.hyperSlice( img, 2, 1); 
		
		Cursor< UnsignedByteType > sourceCursor = Views.iterable(target_view).localizingCursor();
		RandomAccess< UnsignedByteType > targetCursor = img.randomAccess();
		
		 // create a cursor that automatically localizes itself on every move
        //Cursor< T > targetCursor = target.localizingCursor();
       // RandomAccess< T > sourceRandomAccess = source.randomAccess();
 
        // iterate over the input cursor
        while ( sourceCursor.hasNext())
        {
            // move input cursor forward
        	sourceCursor.fwd();
 
            // set the output cursor to the position of the input cursor
        	targetCursor.setPosition( sourceCursor );
 
            // set the value of this pixel of the output image, every Type supports T.set( T type )
            targetCursor.get().set( sourceRandomAccess.get() );
        }
        
		
		
//		ImageJFunctions.show( img );
		imp.show();
		ImageWindow win = imp.getWindow();
		ImageCanvas canvas = win.getCanvas();
		canvas.addMouseListener( this );
		canvas.addMouseWheelListener( this );
		output = ImageJFunctions.show( mask );	
		ImageJFunctions.show( img );	
		
		ImageJFunctions.show( target_view );

	}

	
	

		@Override
		public void mouseClicked(MouseEvent e) {
			 
			 switch( e.getButton() ) {
			 	case MouseEvent.BUTTON1:
	               //  System.out.println("Left Button Pressed");
	               //IJ.log("mouseClicked: ");				
	     			Sphere sph = new Sphere( e.getX(), e.getY(), imp.getZ(), (int)D/2 );
	     			//System.out.println( sph );
	     					
	     			sphere_list.add( sph );	     			
	                 break;
	                 
			 	case    MouseEvent.BUTTON2:
			 		System.out.println("2 Button Pressed");
			 		//remove last:
	     			sphere_list.remove( sphere_list.size() - 1);  	
			 		break;
			 		
			 	default: 
			 		System.out.println("default");
			 		break;
			 }
			 
			
			redraw_spheres( );
			
			//IJ.log("Right button: "+((e.getModifiers()&Event.META_MASK)!=0));	
//			int offscreenX = canvas.offScreenX(x);
//			int offscreenY = canvas.offScreenY(y);
		}

		
		
		private void redraw_spheres( ) {
			//take coords and make a sphere
			//HyperSphere< UnsignedByteType > hyperSphere = new HyperSphere<>( mask, center, D/2 );

			//HyperSphereCursor< UnsignedByteType > sphere_cursor = hyperSphere.localizingCursor();
			Cursor< UnsignedByteType > mask_cursor = mask.localizingCursor();

			// set to 0:
			while ( mask_cursor.hasNext() )
			{
				mask_cursor.fwd();
				mask_cursor.get().setZero();
			}
			
			//check for each sphere
			for( Sphere sphere : sphere_list)
			{
				if( true)//!sphere.isDrawn() )
				{
					mask_cursor.reset();
					while ( mask_cursor.hasNext() )
					{
						mask_cursor.fwd();
						int xx = mask_cursor.getIntPosition( 0 );
						int yy = mask_cursor.getIntPosition( 1 );
						int zz = mask_cursor.getIntPosition( 3 );

						if ( Util.pow( xx- sphere.getX(), 2 ) +
							Util.pow(yy-  sphere.getY(), 2 ) +
							Util.pow(zz- sphere.getZ(), 2 ) < Util.pow( D/2, 2 ))
						{
							mask_cursor.get().set(255);
						}
					}
					sphere.setDrawn();
				}
			}
			update_output_windows();
		}
			
			
			
		private void update_output_windows() {
			System.out.println( "updating" );

			imp.setSlice( cur_slice );
			//output.repaintWindow();
			//output.updateAndRepaintWindow();
			
			//output.setSlice( 0 );	
			//output.updateAndRepaintWindow();

			output.setSlice( imp.getZ() );
			//output.getProcessor().setPixels( mask.getProcessor().getPixels() );

			// update the already displayed ImagePlus
			//output.updateAndRepaintWindow();
		}
		
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {

			int notches = e.getWheelRotation();
			cur_slice += notches;
			if (cur_slice > max_slice)
			{
				//System.out.println("up: " + notches);
				cur_slice = max_slice;
			} 
			
			if ( cur_slice < min_slice )
			{
				//System.out.println("down: " + notches);
				cur_slice = min_slice;
			}
			
			update_output_windows();
		}

		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			//IJ.log("mouseEntered: ");

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
//			IJ.log("mouseExited: ");
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
//			IJ.log("mousePressed: ");
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
//			IJ.log("mouseReleased: ");
		}
		

	
	public static void main(String[] args) {
		new ImageJ();
		
		new Test();
	}





	
	

}
