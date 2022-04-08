package ovh.npk;

import java.util.Arrays;

public record IntTuple(int... data) implements Cloneable, Comparable<IntTuple> {
	
	@Override
	@SuppressWarnings("all")
	public IntTuple clone() {
		return new IntTuple(data.clone());
	}
	
	@Override
	public int compareTo(IntTuple o) {
		return Arrays.compare(data, o.data);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(data);
	}
	
	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof IntTuple intTuple
				&& Arrays.equals(data, intTuple.data);
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}
