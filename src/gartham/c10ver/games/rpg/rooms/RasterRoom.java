package gartham.c10ver.games.rpg.rooms;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Supplier;

import org.alixia.javalibrary.JavaTools;

public class RasterRoom<T> implements Room<T> {
	private final T[][] elements;

	@SuppressWarnings("unchecked")
	public RasterRoom(int height, int width) {
		elements = (T[][]) new Object[height][width];
	}

	@SuppressWarnings("unchecked")
	public RasterRoom(int height, int width, Class<T> type) {
		elements = (T[][]) Array.newInstance(type, height, width);
	}

	protected T[][] getElements() {
		return elements;
	}

	@Override
	public T[][] tilemap() {
		// Copies ALL sub arrays, not just two levels. This class is not prepared to
		// handle cases where T=?[] (i.e., where T itself is an array type).
		return JavaTools.deepCopy(elements);
	}

	public void setElement(int depth, int breadth, T value) {
		elements[depth][breadth] = value;
	}

	public T getElement(int depth, int breadth) {
		return elements[depth][breadth];
	}

	public void fillRect(int topLeftDepth, int topLeftBreadth, int bottomRightDepth, int bottomRightBreadth, T[][] in) {
		if (topLeftDepth < 0 || topLeftBreadth < 0 || bottomRightDepth > elements.length
				|| bottomRightBreadth > elements[0].length || topLeftDepth > bottomRightDepth
				|| topLeftBreadth > bottomRightBreadth)
			throw new IllegalArgumentException("Illegal input values.");
		if (topLeftDepth == bottomRightDepth || topLeftBreadth == bottomRightBreadth)
			return;

		for (int i = topLeftDepth; i < bottomRightDepth; i++)
			System.arraycopy(in[i - topLeftDepth], 0, elements[i], topLeftBreadth, bottomRightBreadth - topLeftBreadth);
	}

	public void fillRect(int topLeftDepth, int topLeftBreadth, int bottomRightDepth, int bottomRightBreadth, T[] in) {
		if (topLeftDepth < 0 || topLeftBreadth < 0 || bottomRightDepth > elements.length
				|| bottomRightBreadth > elements[0].length || topLeftDepth > bottomRightDepth
				|| topLeftBreadth > bottomRightBreadth)
			throw new IllegalArgumentException("Illegal input values.");
		if (topLeftDepth == bottomRightDepth || topLeftBreadth == bottomRightBreadth)
			return;

		for (int i = topLeftDepth; i < bottomRightDepth; i++)
			System.arraycopy(in, 0, elements[i], topLeftBreadth, bottomRightBreadth - topLeftBreadth);
	}

	public void fillRect(int topLeftDepth, int topLeftBreadth, int bottomRightDepth, int bottomRightBreadth, T val) {
		if (topLeftDepth < 0 || topLeftBreadth < 0 || bottomRightDepth > elements.length
				|| bottomRightBreadth > elements[0].length || topLeftDepth > bottomRightDepth
				|| topLeftBreadth > bottomRightBreadth)
			throw new IllegalArgumentException("Illegal input values.");
		if (topLeftDepth == bottomRightDepth || topLeftBreadth == bottomRightBreadth)
			return;

		for (int i = topLeftDepth; i < bottomRightDepth; i++)
			Arrays.fill(elements[i], topLeftBreadth, bottomRightBreadth - topLeftBreadth, val);
	}

	public void fill(Supplier<? extends T> supplier) {
		for (T[] a : elements)
			for (int i = 0; i < a.length; i++)
				a[i] = supplier.get();
	}

	public interface Rasterizer<T> {
		T rasterize(int depth, int breadth);
	}

	public void fill(Rasterizer<? extends T> rasterizer) {
		for (int i = 0; i < elements.length; i++)
			for (int j = 0; j < elements[i].length; j++)
				elements[i][j] = rasterizer.rasterize(i, j);
	}

	public static <T> RasterRoom<T> discordSquare(int size) {
		return new RasterRoom<>((int) (2.2 * size), size);
	}

}
