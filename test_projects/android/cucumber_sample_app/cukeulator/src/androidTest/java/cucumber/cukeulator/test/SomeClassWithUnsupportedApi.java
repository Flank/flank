package cucumber.cukeulator.test;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class SomeClassWithUnsupportedApi implements Comparator<Integer> {
	

	@Override
	public int compare(Integer o1, Integer o2) {
		return 0;
	}

	@Override
	public Comparator<Integer> reversed() {
		return null;
	}

	@Override
	public Comparator<Integer> thenComparing(Comparator<? super Integer> other) {
		return null;
	}

	@Override
	public <U> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
		return null;
	}

	@Override
	public <U extends Comparable<? super U>> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor) {
		return null;
	}

	@Override
	public Comparator<Integer> thenComparingInt(ToIntFunction<? super Integer> keyExtractor) {
		return null;
	}

	@Override
	public Comparator<Integer> thenComparingLong(ToLongFunction<? super Integer> keyExtractor) {
		return null;
	}

	@Override
	public Comparator<Integer> thenComparingDouble(ToDoubleFunction<? super Integer> keyExtractor) {
		return null;
	}
}
