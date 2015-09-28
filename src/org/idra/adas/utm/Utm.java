package org.idra.adas.utm;

public class Utm
{
	private final static double RADIANS_PER_DEGREE = Math.PI/180.0;
	
	private final static double WGS84_A = 6378137.0; 				//major axis
	private final static double WGS84_E	= 0.0818191908;				// first eccentricity
	
	private final static double UTM_K0 = 0.9996;					// scale factor
	private final static double UTM_FE = 500000.0;					// false easting
	private final static double UTM_FN_N = 0.0;						// false northing, northern hemisphere
	private final static double UTM_FN_S = 10000000.0;    			// false northing, southern hemisphere
	private final static double UTM_E2 = (WGS84_E*WGS84_E);			// e^2
	private final static double UTM_E4 = (UTM_E2*UTM_E2);			// e^4
	private final static double UTM_E6 = (UTM_E4*UTM_E2);			// e^6
	private final static double UTM_EP2 = (UTM_E2/(1-UTM_E2));		// e'^2
	
	
	public static double[] UTM(double lat, double lon)
	{
		double utm_location[] = { 0, 0 };
		
		// constants
		double m0 = (1 - UTM_E2/4 - 3*UTM_E4/64 - 5*UTM_E6/256);
		double m1 = -(3*UTM_E2/8 + 3*UTM_E4/32 + 45*UTM_E6/1024);
		double m2 = (15*UTM_E4/256 + 45*UTM_E6/1024);
		double m3 = -(35*UTM_E6/3072);

		// compute the central meridian
		int cm = ((lon >= 0.0) ? ((int)lon - ((int)lon)%6 + 3) : ((int)lon - ((int)lon)%6 - 3));

		// convert degrees into radians
		double rlat = lat * RADIANS_PER_DEGREE;
		double rlon = lon * RADIANS_PER_DEGREE;
		double rlon0 = cm * RADIANS_PER_DEGREE;

		// compute trigonometric functions
		double slat = Math.sin(rlat);
		double clat = Math.cos(rlat);
		double tlat = Math.tan(rlat);

		// decide the false northing at origin
		double fn = (lat > 0) ? UTM_FN_N : UTM_FN_S;

		double T = tlat * tlat;
		double C = UTM_EP2 * clat * clat;
		double A = (rlon - rlon0) * clat;
		double M = WGS84_A * (m0*rlat + m1*Math.sin(2*rlat) + m2*Math.sin(4*rlat) + m3*Math.sin(6*rlat));
		double V = WGS84_A / Math.sqrt(1 - UTM_E2*slat*slat);

		// compute the easting-northing coordinates
		utm_location[0] = UTM_FE + UTM_K0 * V * (A + (1-T+C)*Math.pow(A,3)/6 + (5-18*T+T*T+72*C-58*UTM_EP2)*Math.pow(A,5)/120);
		utm_location[1] = fn + UTM_K0 * (M + V * tlat * (A*A/2 + (5-T+9*C+4*C*C)*Math.pow(A,4)/24 
			  		  + ((61-58*T+T*T+600*C-330*UTM_EP2) * Math.pow(A,6)/720)));
		return utm_location;
	}
	
}
