package de.a0h.math.algo;

import java.math.BigInteger;

/**
 * <p>
 * This class contains methods for calculating the bezout coefficients of two
 * numbers, a and b. It does so by using the Extended Euclidean algorithm, which
 * can be found at https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm
 * .<br>
 * You don't need to instantiate this class, because all methods are static,
 * thread-safe, reentrant and non-blocking. The static methods are sufficient
 * for the purpose of the algorithm.
 * </p>
 * 
 * <p>
 * All solve() methods work the same way, just with different integer data
 * types, which can be either int, long, or BigInteger. Use the one which suits
 * the range of values that you want to process.<br>
 * A solve() method takes two integers <code>a</code> and <code>b</code> and
 * returns a triplet <code>[s, t, gcd(a,b)]</code> so that
 * <code>s*a + t*b = gcd(a,b)</code>. <code>s</code> and <code>t</code> are
 * called the bézout coefficients of <code>a</code> and <code>b</code>.
 * </p>
 * 
 * <p>
 * The s and t of direct results of solve() are usually not minimal, meaning
 * they are somewhat bloated. You can use shrink() to shrink them to a minimal
 * solution.
 * </p>
 */
public class ExtendedEuclideanAlgorithm {

	/**
	 * Solves the diophantine equation s*a + t*b = gcd(a,b) with data type
	 * BigInteger. Returns null if (a, b) = (0, 0).
	 */
	public static ResultBigInteger solve(BigInteger a, BigInteger b) {
		final BigInteger O = BigInteger.ZERO;
		final BigInteger I = BigInteger.ONE;

		boolean swapped = (compareToIgnoreSign(a, b) < 0);
		if (swapped) {
			BigInteger tmp = a;
			a = b;
			b = tmp;
		}

		if (b.signum() == 0) {
			if (a.signum() == 0) {
				return null;
			}

			b = a;
			swapped = !swapped;
		}

		BigInteger r2, /**/s2, t2;
		BigInteger r1, /**/s1, t1;
		BigInteger r0, q0, s0, t0;

		r2 = a;
		s2 = I;
		t2 = O;

		r1 = b;
		s1 = O;
		t1 = I;

//		System.out.printf("%s\t%s\t%s\t%s\n", "r", "q", "s", "t");
//		System.out.printf("%d\t%s\t%d\t%d\n", r2, "-", s2, t2);
//		System.out.printf("%d\t%s\t%d\t%d\n", r1, "-", s1, t1);

		for (;;) {
			BigInteger[] q_and_r = r2.divideAndRemainder(r1);
			r0 = q_and_r[1];

			if (r0.signum() == 0) {
//					System.out.printf("%d\t%s\t%s\t%s\n", r0, "-", "-", "-");
				break;
			}

			q0 = q_and_r[0];
			s0 = s2.subtract(q0.multiply(s1));
			t0 = t2.subtract(q0.multiply(t1));

//				System.out.printf("%d\t%d\t%d\t%d\n", r0, q0, s0, t0);

			r2 = r1;
			s2 = s1;
			t2 = t1;

			r1 = r0;
			s1 = s0;
			t1 = t0;
		}

		if (r1.signum() < 0) {
			r1 = r1.negate();
			s1 = s1.negate();
			t1 = t1.negate();
		}

		if (swapped) {
			BigInteger tmp = t1;
			t1 = s1;
			s1 = tmp;
		}

		return new ResultBigInteger(s1, t1, r1);
	}

	private static int compareToIgnoreSign(BigInteger a, BigInteger b) {
		if (a.signum() >= 0) {
			if (b.signum() >= 0) {
				return a.compareTo(b);
			} else {
				return a.compareTo(b.abs());
			}
		} else {
			if (b.signum() < 0) {
				return b.compareTo(a);
			} else {
				return a.abs().compareTo(b);
			}
		}
	}

	/**
	 * Direct results of the euclidean algorithm often use large numbers although
	 * that's not necessary. This method shrinks a result to the smallest absolute
	 * values.
	 * 
	 * @param eea the result of a run of the Extended Euclidean Algorithm
	 * @param a   the value given to the EEA as first input
	 * @param b   the value given to the EEA as second input
	 */
	public static void shrink(ResultBigInteger eea, BigInteger a, BigInteger b) {
		// a and b are proud parents of a daughter, the gcd, and of two younger sons,
		// who are the greatest coprime divisors, usually called the gcod brothers, or
		// just gcods.
		// the gcods are twins. they are so different from gcd. while the gcd has all
		// the good things that her mum and her dad have in common, the gcods each come
		// after just one of them, each having inherited only the individual quirks of
		// one of their parents.

		BigInteger gcd = eea.gcd;
		BigInteger aGcod = a.divide(gcd);
		BigInteger bGcod = b.divide(gcd);

		BigInteger[] a_div_bGcod = a.divideAndRemainder(bGcod);
		BigInteger a_div_bGcod_q = a_div_bGcod[0];
		BigInteger a_div_bGcod_r = a_div_bGcod[1];

		eea.s = a_div_bGcod_r;
		eea.t = eea.t.add(a_div_bGcod_q.multiply(aGcod));

		// System.out.printf("shrinkResult() a [%d %d] eea:%s\n", a, b, eea);

		if (eea.s.signum() < 0) {
			eea.s = eea.s.add(bGcod);
			eea.t = eea.t.subtract(aGcod);
		}

		// System.out.printf("shrinkResult() b [%d %d] eea:%s\n", a, b, eea);
	}

	/**
	 * Solves the diophantine equation s*a + t*b = gcd(a,b) with data type long.
	 * Returns null if (a, b) = (0, 0).
	 */
	public static ResultLong solve(long a, long b) {
		boolean swapped = (Math.abs(a) < Math.abs(b));
		if (swapped) {
			long tmp = a;
			a = b;
			b = tmp;
		}

		if (b == 0) {
			if (a == 0) {
				return null;
			}

			b = a;
			swapped = !swapped;
		}

		long r2, /**/s2, t2;
		long r1, /**/s1, t1;
		long r0, q0, s0, t0;

		r2 = a;
		s2 = 1;
		t2 = 0;

		r1 = b;
		s1 = 0;
		t1 = 1;

//		System.out.printf("%s\t%s\t%s\t%s\n", "r", "q", "s", "t");
//		System.out.printf("%d\t%s\t%d\t%d\n", r2, "-", s2, t2);
//		System.out.printf("%d\t%s\t%d\t%d\n", r1, "-", s1, t1);

		for (;;) {
			q0 = r2 / r1;
			r0 = r2 % r1;

			if (r0 == 0) {
//				System.out.printf("%d\t%s\t%s\t%s\n", r0, "-", "-", "-");
				break;
			}

			s0 = s2 - q0 * s1;
			t0 = t2 - q0 * t1;

//			System.out.printf("%d\t%d\t%d\t%d\n", r0, q0, s0, t0);

			r2 = r1;
			s2 = s1;
			t2 = t1;

			r1 = r0;
			s1 = s0;
			t1 = t0;
		}

		if (r1 < 0) {
			r1 = -r1;
			s1 = -s1;
			t1 = -t1;
		}

		if (swapped) {
			long tmp = t1;
			t1 = s1;
			s1 = tmp;
		}

		return new ResultLong(s1, t1, r1);
	}

	/**
	 * Solves the diophantine equation s*a + t*b = gcd(a,b) with data type int.
	 * Returns null if (a, b) = (0, 0).
	 */
	public static ResultInt solve(int a, int b) {
		boolean swapped = (Math.abs(a) < Math.abs(b));
		if (swapped) {
			int tmp = a;
			a = b;
			b = tmp;
		}

		if (b == 0) {
			if (a == 0) {
				return null;
			}

			b = a;
			swapped = !swapped;
		}

		int r2, /**/s2, t2;
		int r1, /**/s1, t1;
		int r0, q0, s0, t0;

		r2 = a;
		s2 = 1;
		t2 = 0;

		r1 = b;
		s1 = 0;
		t1 = 1;

//		System.out.printf("%s\t%s\t%s\t%s\n", "r", "q", "s", "t");
//		System.out.printf("%d\t%s\t%d\t%d\n", r2, "-", s2, t2);
//		System.out.printf("%d\t%s\t%d\t%d\n", r1, "-", s1, t1);

		for (;;) {
			q0 = r2 / r1;
			r0 = r2 % r1;

			if (r0 == 0) {
//				System.out.printf("%d\t%s\t%s\t%s\n", r0, "-", "-", "-");
				break;
			}

			s0 = s2 - q0 * s1;
			t0 = t2 - q0 * t1;

//			System.out.printf("%d\t%d\t%d\t%d\n", r0, q0, s0, t0);

			r2 = r1;
			s2 = s1;
			t2 = t1;

			r1 = r0;
			s1 = s0;
			t1 = t0;
		}

		if (r1 < 0) {
			r1 = -r1;
			s1 = -s1;
			t1 = -t1;
		}

		if (swapped) {
			int tmp = t1;
			t1 = s1;
			s1 = tmp;
		}

		return new ResultInt(s1, t1, r1);
	}

	public static class ResultBigInteger {
		public BigInteger s;
		public BigInteger t;
		public BigInteger gcd;

		public ResultBigInteger(BigInteger s, BigInteger t, BigInteger gcd) {
			this.s = s;
			this.t = t;
			this.gcd = gcd;
		}

		public String toString() {
			return "(" + s + "," + t + "," + gcd + ")";
		}
	}

	public static class ResultLong {
		public long s;
		public long t;
		public long gcd;

		public ResultLong(long s, long t, long gcd) {
			this.s = s;
			this.t = t;
			this.gcd = gcd;
		}

		public String toString() {
			return "(" + s + "," + t + "," + gcd + ")";
		}
	}

	public static class ResultInt {
		public int s;
		public int t;
		public int gcd;

		public ResultInt(int s, int t, int gcd) {
			this.s = s;
			this.t = t;
			this.gcd = gcd;
		}

		public String toString() {
			return "(" + s + "," + t + "," + gcd + ")";
		}
	}

	/**
	 * Constructor is private to clarify that this class should not be instantiated.
	 */
	private ExtendedEuclideanAlgorithm() {
	}
}