package de.a0h.math.algo;

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import de.a0h.math.algo.ExtendedEuclideanAlgorithm.ResultBigInteger;
import de.a0h.math.algo.ExtendedEuclideanAlgorithm.ResultInt;
import de.a0h.math.algo.ExtendedEuclideanAlgorithm.ResultLong;

public class ExtendedEuclideanAlgorithmTest {

	int[][][] testCaseList = { //
			// { input , expectedOutput }
			// { a, b }, { s, t, gcd(a,b) } },
			{ { 5, 2 }, { 1, -2, 1 } }, //
			{ { 120, 23 }, { -9, 47, 1 } }, //
			{ { 17, 129 }, { 38, -5, 1 } }, //
			{ { 240, 46 }, { -9, 47, 2 } }, //
			{ { 122, 22 }, { 2, -11, 2 } }, //
			{ { 99, 78 }, { -11, 14, 3 } }, //
			{ { 5, -2 }, { 1, 2, 1 } }, //
			{ { 17, -20 }, { -7, -6, 1 } }, //
			{ { -20, 17 }, { -6, -7, 1 } }, //
			{ { 5, 0 }, { 1, 0, 5 } }, //
			{ { 0, 5 }, { 0, 1, 5 } }, //
			{ { 0, 0 }, null }, //
	};

	@Test
	public void testSolve() {
		for (int[][] testCase : testCaseList) {
			int[] input = testCase[0];
			int[] expectedOutput = testCase[1];

			for (int j = 0; j < 4; j++) {
				testSolveBigInteger(input, expectedOutput);
				testSolveLong(input, expectedOutput);
				testSolveInt(input, expectedOutput);

				// variate sign of input and sign of expected output accordingly
				int idx = j & 1;
				input[idx] = -input[idx];
				if (expectedOutput != null) {
					expectedOutput[idx] = -expectedOutput[idx];
				}
			}
		}
	}

	private void testSolveInt(int[] input, int[] expectedOutput) {
		int a = input[0];
		int b = input[1];

		ResultInt euclideanAlgoResult = ExtendedEuclideanAlgorithm.solve(a, b);
		int[] gcd_s_t = toIntArray(euclideanAlgoResult);

		myAssertArrayEquals(input, expectedOutput, gcd_s_t);
	}

	private void testSolveLong(int[] input, int[] expectedOutput) {
		long a = input[0];
		long b = input[1];

		ResultLong euclideanAlgoResult = ExtendedEuclideanAlgorithm.solve(a, b);
		int[] gcd_s_t = toIntArray(euclideanAlgoResult);

		myAssertArrayEquals(input, expectedOutput, gcd_s_t);		
	}

	private void testSolveBigInteger(int[] input, int[] expectedOutput) {
		BigInteger a = BigInteger.valueOf(input[0]);
		BigInteger b = BigInteger.valueOf(input[1]);

		ResultBigInteger euclideanAlgoResult = ExtendedEuclideanAlgorithm.solve(a, b);
		int[] gcd_s_t = toIntArray(euclideanAlgoResult);
		
		ExtendedEuclideanAlgorithm.shrink(euclideanAlgoResult, a, b);

		myAssertArrayEquals(input, expectedOutput, gcd_s_t);
	}

	public void myAssertArrayEquals(int[] input, int[] expectedOutput, int[] gcd_s_t) throws ArrayComparisonFailure {
		if (!Arrays.equals(expectedOutput, gcd_s_t)) {
			throw new AssertionError(String.format( //
					"input %s should produce output %s, but actually produced %s", //
					Arrays.toString(input), //
					Arrays.toString(expectedOutput), //
					Arrays.toString(gcd_s_t) //
			));
		}
	}

	public int[] toIntArray(ResultInt euclideanAlgoResult) {
		if (euclideanAlgoResult == null) {
			return null;
		}

		int[] result = new int[3];

		result[0] = euclideanAlgoResult.s;
		result[1] = euclideanAlgoResult.t;
		result[2] = euclideanAlgoResult.gcd;

		return result;
	}

	public int[] toIntArray(ResultLong euclideanAlgoResult) {
		if (euclideanAlgoResult == null) {
			return null;
		}

		int[] result = new int[3];

		result[0] = intValueExact(euclideanAlgoResult.s);
		result[1] = intValueExact(euclideanAlgoResult.t);
		result[2] = intValueExact(euclideanAlgoResult.gcd);

		return result;
	}

	private int intValueExact(long l) {
		if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
			throw new ArithmeticException("value out of range for cast to int: " + l);
		}

		return (int) l;
	}

	public int[] toIntArray(ResultBigInteger euclideanAlgoResult) {
		if (euclideanAlgoResult == null) {
			return null;
		}

		int[] result = new int[3];

		result[0] = euclideanAlgoResult.s.intValueExact();
		result[1] = euclideanAlgoResult.t.intValueExact();
		result[2] = euclideanAlgoResult.gcd.intValueExact();

		return result;
	}
}
