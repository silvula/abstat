package it.unimib.disco.summarization.export;

public class UnorderedPair {

	String first;
	String second;

	public UnorderedPair(String first, String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return first;
	}

	public String getSecond() {
		return second;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UnorderedPair))
			return false;

		final UnorderedPair pair = (UnorderedPair) o;

		return (((first == null ? pair.first == null : first.equals(pair.first))
				&& (second == null ? pair.second == null : second.equals(pair.second)))
				|| ((first == null ? pair.second == null : first.equals(pair.second))
						&& (second == null ? pair.first == null : second.equals(pair.first))));
	}

	public int hashCode() {
		int firstHashCode = (first == null ? 0 : first.hashCode());
		int secondHashCode = (second == null ? 0 : second.hashCode());
		if (firstHashCode != secondHashCode) {
			return (((firstHashCode & secondHashCode) << 16) ^ ((firstHashCode | secondHashCode)));
		} else {
			return firstHashCode;
		}
	}

	public String toString() {
		String firstString = getFirst().toString();
		String secondString = getSecond().toString();
		if (firstString.compareTo(secondString) > 0) {
			String tempString = firstString;
			firstString = secondString;
			secondString = tempString;
		}
		return "(" + firstString + ", " + secondString + ")";
	}
}