package gartham.c10ver.games.rpg.random;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.alixia.javalibrary.JavaTools;

public class Seed {
	private final byte[] seed;

	public Seed pick(String index) {
		return pick(index.getBytes(StandardCharsets.UTF_8));
	}

	public Seed(long seed) {
		this.seed = JavaTools.longToBytes(seed);
	}

	public Seed(byte[] seed) {
		this.seed = seed;
	}

	public static Seed next(Seed source) {
		return new Seed(new Random(hash(source.seed)).nextLong());
	}

	/**
	 * Deterministically generates a new seed based off of this seed. One can walk
	 * through "random" seeds by calling <code>...next().next().next()...</code>.
	 * 
	 * @return The {@link Seed} generated deterministically but pseudorandomly from
	 *         this one.
	 */
	public Seed next() {
		return next(this);
	}

	public long khash(byte... bytes) {
		return hash(JavaTools.combineArrays(HASH(seed), bytes));
	}

	private static byte[] HASH(byte[] in) {
		for (int i = 0; i < in.length; i++)
			in[i] = HASH_BYTES[in[i] + 128];
		return in;
	}

	public long khash(long value) {
		return khash(JavaTools.longToBytes(value));
	}

	public Random rand() {
		return new Random(hash(seed));
	}

	/**
	 * Deterministically picks a new {@link Seed} given the provided
	 * <code>value</code>. The resulting {@link Seed} can be reobtained from this
	 * seed from the same provided value at any time.
	 * 
	 * @param value The new seed to "pick" from this one. This {@link Seed} can be
	 *              imagined as an array or generator of many seeds and
	 *              <code>value</code> can be imagined as the index of those many to
	 *              pick from.
	 * @return A new seed that is a deterministic, repeatedably product of a call to
	 *         this method on this {@link Seed} with value <code>value</code>.
	 */
	public Seed pick(long value) {
		return new Seed(JavaTools.combineArrays(seed, JavaTools.longToBytes(value)));
	}

	public Seed pickReduced(long value) {
		return new Seed(hash(JavaTools.combineArrays(seed, JavaTools.longToBytes(value))));
	}

	public Seed pick(long... values) {
		byte[] bytes = new byte[values.length * 8];
		for (int i = 0; i < values.length; i++)
			System.arraycopy(JavaTools.longToBytes(values[i]), 0, bytes, i * 8, 8);
		return new Seed(JavaTools.combineArrays(seed, JavaTools.combineArrays(bytes)));
	}

	public Seed pick(byte... bytes) {
		return new Seed(JavaTools.combineArrays(seed, JavaTools.combineArrays(bytes)));
	}

	public Seed pickReduced(long... values) {
		byte[] bytes = new byte[values.length * 8];
		for (int i = 0; i < values.length; i++)
			System.arraycopy(JavaTools.longToBytes(values[i]), 0, bytes, i * 8, 8);
		return new Seed(hash(JavaTools.combineArrays(seed, bytes)));
	}

	private static final byte[] HASHBYTES = { 8, -61, -13, -44, -126, 100, -39, -35 };

	// Not proven to be good. Likely not secure, but "random enough."
	public static long hash(byte... bytes) {
		long res = 0;

		boolean a = false, b = false;
		for (int i = 0; i < bytes.length; i++, a ^= true, b ^= a)
			res = Long.rotateLeft(res ^ (HASH_BYTES[(int) (HASH_BYTES[(int) (res & 257)] ^ HASH_BYTES[bytes[i] + 128]
					^ HASH_BYTES[i % 256 + (a ? 0 : 512) + (b ? 0 : 256)]) % 256 + 128 + (a ? 256 : 0)
					+ (b ? 512 : 0)]), 8);

		for (int i = 0; i < 8; i++, a ^= true, b ^= a)
			res = Long.rotateLeft(res ^ (HASH_BYTES[(HASH_BYTES[(int) (res & 256)] ^ HASH_BYTES[HASHBYTES[i] + 128]
					^ HASH_BYTES[i % 256 + (a ? 0 : 512) + (b ? 0 : 256)]) % 256 + 128 + (a ? 256 : 0)
					+ (b ? 512 : 0)]), 8);

		return res;
	}

	// Not proven to be good too.
	public static byte bhash(byte input) {
		return (byte) (HASH_BYTES[(int) input + 128] ^ HASH_BYTES[(int) input + 384]
				^ HASH_BYTES[(int) HASH_BYTES[(int) input + 640] + 384]);
	}

	public byte[] getBytes() {
		return Arrays.copyOf(seed, seed.length);
	}

	public static Seed nextSecure(Seed source) {
		return new Seed(new SecureRandom(source.seed).nextLong());
	}

	// Needs to be checked for non-transience.
	private static final byte[] HASH_BYTES = new byte[] { 5, -8, 83, -28, 3, 71, -63, 33, -58, -119, 93, 10, -123, -80,
			-14, -42, 66, 68, 101, 65, 51, -115, 57, 44, -7, 53, -101, 60, 13, 37, 127, -48, 112, -111, 15, 9, -50, -36,
			-19, -91, 113, -15, 56, -46, 24, 80, 120, 8, 98, -29, -3, -18, 110, 59, 18, 69, -40, 77, 0, 23, 103, 17, 85,
			126, -16, 90, 49, 97, 36, 92, -106, -13, -87, -37, 19, -57, 79, 30, 45, -68, 31, 50, 118, -75, 41, 2, -90,
			34, -5, 11, 106, -47, 95, -76, -53, -86, 58, 99, -126, -59, -124, -105, 117, 73, -12, -121, 123, -64, 47,
			125, -70, -69, -21, 115, -4, 116, 70, 64, -104, -24, -41, -98, 21, -117, -1, 81, -93, 25, -103, -38, -100,
			62, 46, -107, -10, 20, 48, 76, -44, 119, -17, 55, 43, -97, -84, -2, 82, -20, -99, -83, 86, 32, -11, -112,
			-125, -108, -74, 6, -71, 89, 29, -27, -32, 91, -25, 114, -51, 94, -113, 108, -35, -120, -94, 22, -6, -102,
			16, 72, -52, 54, 26, 7, -122, -22, 109, -56, -95, 27, 40, -73, -34, 12, 1, -60, -96, -55, 87, -118, 88, 78,
			-39, -79, 42, -33, -49, 63, 124, 104, -9, -23, 111, 84, 35, 14, -109, -89, -61, -127, -43, 52, 96, -110,
			100, -66, -72, -54, 74, -114, -45, -31, -67, 75, -116, 38, 121, -26, 102, -30, -85, -78, 122, 28, -81, -88,
			61, 67, -62, -65, -128, 4, 107, -82, -92, 105, -77, 39, -2, -33, -1, 98, 73, -60, -31, 85, -95, -99, 58, 89,
			114, -88, 28, -17, 5, 83, 11, 109, -9, 86, 127, 76, -94, -67, -39, 39, -18, 94, 0, 69, 46, -24, -69, -83,
			-14, -80, 61, -110, -102, -82, -85, -58, 125, 99, 124, -12, -6, 97, 120, 64, 107, -63, 15, 34, -62, -36, 20,
			27, 12, -45, 119, -43, -22, -16, -74, 1, 68, -68, 113, -65, -89, -49, 71, 45, -103, -56, -53, 33, 90, 3,
			-127, -64, -13, -106, 55, -72, -76, 57, -75, -90, -116, 75, -54, 59, -46, -4, -50, 62, 96, 88, -98, 10, -34,
			-92, 63, 54, -44, -108, -40, -20, -70, 51, -78, 87, -25, -73, 16, 6, -52, -7, -91, 13, 91, 82, -81, 4, 115,
			-32, 26, 29, -112, 43, 7, -87, 118, -79, 65, -101, 35, 101, -109, 23, -15, 81, 77, 31, -100, 111, 105, 25,
			42, 74, 32, 14, -113, -5, 50, 103, -29, -111, 60, -128, -117, -42, 52, -35, -8, 9, 84, -55, 38, 95, -84,
			-10, 8, 18, -121, -11, 41, -21, 92, 24, -118, -120, -26, 122, -57, 36, 102, 37, 78, -93, -123, 100, 21, -86,
			-126, 104, 56, -27, -3, -125, -19, 22, -107, -105, 121, -47, -41, -104, 44, 123, -66, 53, 2, 116, -38, -119,
			40, -48, -114, 80, 66, 70, -30, -71, 48, 117, 79, 17, -97, 106, 47, -51, 49, -115, 67, -23, 108, 110, 30,
			-59, 72, 112, -37, -96, -122, 93, 19, -61, -77, 126, -28, -124, 71, -54, -2, 15, 95, 7, 100, -3, -99, -26,
			81, -13, -46, 121, 17, 117, -42, -105, 37, 125, -84, 66, 19, -55, 40, -87, 73, -79, 58, 67, 83, 10, 115,
			-106, -51, -112, -117, 16, -15, -54, 50, 115, -70, -38, 98, 25, -90, -59, 44, 4, 97, 85, -115, 42, 31, 55,
			104, -62, 72, -22, -27, 88, -58, -39, -67, 38, -101, -49, 47, 30, -9, 72, 99, 11, -21, -63, 29, -120, -61,
			96, 99, 63, 20, 51, 112, 126, -18, -80, -73, 27, 68, -43, 66, -111, -108, -106, -95, 22, 2, 61, -27, -114,
			-53, 65, 100, -22, -120, -10, 121, 77, 110, 14, 7, 37, -71, 119, -123, 35, 122, -44, 34, 12, -36, 111, 24,
			5, -1, -98, 118, -6, -39, -8, -15, -64, -122, -77, 48, 47, 83, 21, -33, 114, -32, -57, 96, 6, 11, 29, 85,
			23, -11, -74, -88, 36, 110, -5, -47, -52, -19, -20, 102, 20, 12, 13, 90, -82, -48, 105, 74, -127, 75, 82,
			86, -118, -14, -35, 21, -107, -91, -117, -43, 34, 2, 125, -23, -52, 15, -40, -103, 70, 41, -96, -42, 24, 42,
			71, -4, 92, -12, 44, -65, 46, 23, -34, -26, -71, -3, 78, -85, -75, -97, 97, -128, -104, 54, 117, -29, -70,
			-68, 13, -56, -80, 109, 49, -51, 118, 113, 39, 33, -8, -118, -102, -96, -116, -47, 40, -30, 54, 124, -93,
			22, 120, -116, -24, -113, -104, -17, -65, 89, -64, 93, -7, -125, 58, -128, 8, 74, 91, -69, -109, -10, -112,
			-94, -101, 61, -56, -75, 43, 112, -86, -49, -31, 81, -111, 9, -125, 4, 127, -124, -127, 86, 65, -37, 103,
			102, 78, -32, -108, -88, 111, -25, 39, -28, 76, -79, -89, -13, 38, -81, -5, -35, 62, -77, -28, 28, 31, 70,
			-119, -91, 59, -30, -29, 84, 80, -113, -38, 10, 8, 59, -33, -6, 94, 49, -4, 25, 79, -57, 33, -34, -44, -62,
			101, -72, -55, -37, -76, 53, 18, 19, -19, -97, 1, -98, -122, -67, -24, 56, 14, 114, 103, -21, -20, 3, -53,
			9, 75, -115, 52, -2, 28, -92, 41, 62, 87, 105, -86, 53, -14, -72, 107, -121, 35, -126, -66, -50, 57, -93,
			108, 64, 94, -59, -58, -123, 93, -66, -99, 30, -48, 73, 123, 119, 88, 36, 32, 50, 101, 17, 3, -78, 109, -60,
			79, -31, 45, -17, 60, -83, -114, -46, 32, -107, -11, 84, -25, -103, -105, -16, 80, 91, -69, -61, -126, 124,
			-50, -87, -83, 0, 69, 48, 92, -90, -78, 123, 64, -100, -85, 67, -74, -110, 26, 5, 55, -7, -45, 27, -12, 52,
			98, 122, 95, -82, 113, -16, 26, -119, 45, 120, -89, -41, 77, -36, 43, -1, -102, -76, 16, -94, 51, 116, -110,
			68, 104, -18, 63, -109, -40, -84, 89, 56, 57, -124, 107, 76, 106, 87, -68, 6, 60, -95, -121, -41, -92, 18,
			-60, 108, 126, -100, -73, 82, -23, -45, -9, -63, 127, -81, 1, 0, 69, 90, 106, 46, 116 };

	public long getHash() {
		var seed = this.seed.clone();
		return hash(HASH(seed));
	}

}
