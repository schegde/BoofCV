/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.geo.bundle;

import georegression.geometry.RotationMatrixGenerator;
import georegression.struct.point.Point3D_F64;
import georegression.struct.se.Se3_F64;
import org.ejml.ops.MatrixFeatures;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestCalibPoseAndPointRodiguesCodec {

	Random rand = new Random(123);

	/**
	 * Test by endcoding data, then decoding the same data
	 */
	@Test
	public void encode_decode() {
		int numViews = 2;
		int numPoints = 3;

		// randomly configure the model
		CalibratedPoseAndPoint model = new CalibratedPoseAndPoint();

		configure(model,numViews,numPoints,rand);

		// encode the model
		CalibPoseAndPointRodiguesCodec codec = new CalibPoseAndPointRodiguesCodec();
		codec.configure(numViews,numPoints);
		
		double param[] = new double[ codec.getParamLength() ];

		codec.encode(model,param);

		// decode the model
		CalibratedPoseAndPoint found = new CalibratedPoseAndPoint();
		found.configure(numViews,numPoints);

		codec.decode(param,found);
		
		// compare results
		for( int i = 0; i < numViews; i++ ) {
			Se3_F64 o = model.getWorldToCamera(i);
			Se3_F64 f = found.getWorldToCamera(i);
			
			assertEquals(0,o.getT().distance(f.getT()),1e-8);
			assertTrue(MatrixFeatures.isIdentical(o.getR(),f.getR(),1e-8));
		}

		for( int i = 0; i < numPoints; i++ ) {
			Point3D_F64 o = model.getPoint(i);
			Point3D_F64 f = found.getPoint(i);

			assertEquals(0,o.distance(f),1e-8);
		}
	}
	
	public static void configure(CalibratedPoseAndPoint model ,
								 int numViews , int numPoints , Random rand ) {
		model.configure(numViews,numPoints);

		for( int i = 0; i < numViews; i++ ) {
			setPose(model.getWorldToCamera(i),rand);
		}

		for( int i = 0; i < numPoints; i++ ) {
			model.getPoint(i).set(rand.nextGaussian(),rand.nextGaussian(),rand.nextGaussian());
		}
	}
	
	private static void setPose( Se3_F64 pose , Random rand )  {
		
		double rotX = 2*(rand.nextDouble()-0.5);
		double rotY = 2*(rand.nextDouble()-0.5);
		double rotZ = 2*(rand.nextDouble()-0.5);

		double x = rand.nextGaussian();
		double y = rand.nextGaussian();
		double z = rand.nextGaussian();

		RotationMatrixGenerator.eulerXYZ(rotX,rotY,rotZ,pose.getR());
		pose.getT().set(x,y,z);
	}
}