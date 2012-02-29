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

package boofcv.alg.geo.epipolar;

import boofcv.abst.geo.epipolar.EpipolarMatrixEstimator;
import boofcv.alg.geo.AssociatedPair;
import boofcv.factory.geo.d3.epipolar.FactoryEpipolar;
import boofcv.misc.Performer;
import boofcv.misc.ProfileOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Abeles
 */
public class BenchmarkRuntimeHomography extends ArtificialStereoScene {
	static final long TEST_TIME = 1000;
	static final int NUM_POINTS = 500;
	static final boolean PIXELS = true;

	List<AssociatedPair> pairs4 = new ArrayList<AssociatedPair>();


	public class Estimate implements Performer {

		EpipolarMatrixEstimator alg;
		String name;
		List<AssociatedPair> list;
		
		public Estimate( String name , EpipolarMatrixEstimator alg , List<AssociatedPair> list ) {
			this.alg = alg;
			this.name = name;
			this.list = list;
		}

		@Override
		public void process() {
			alg.process(list);
			alg.getEpipolarMatrix();
		}

		@Override
		public String getName() {
			return name;
		}
	}
	
	public void runAll() {
		System.out.println("=========  Profile numFeatures "+NUM_POINTS);
		System.out.println();

		init(NUM_POINTS, PIXELS,true);
		
		for( int i = 0; i < 4; i++ ) {
			pairs4.add(pairs.get(i));
		}

		System.out.println("Minimum Number");
		ProfileOperation.printOpsPerSec(new Estimate("Linear 4 Norm", FactoryEpipolar.computeHomography(true), pairs4), TEST_TIME);
		ProfileOperation.printOpsPerSec(new Estimate("Linear 4 Unorm",FactoryEpipolar.computeHomography(false),pairs4), TEST_TIME);

		System.out.println("N");
		ProfileOperation.printOpsPerSec(new Estimate("Linear 4",FactoryEpipolar.computeHomography(true),pairs), TEST_TIME);

	}
	
	public static void main( String args[] ) {
		BenchmarkRuntimeHomography alg = new BenchmarkRuntimeHomography();

		alg.runAll();
	}
}