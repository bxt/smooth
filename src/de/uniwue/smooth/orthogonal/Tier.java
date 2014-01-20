package de.uniwue.smooth.orthogonal;

import java.util.Iterator;

/**
 * A row/column to place vertices in. Also, a doubly linked list entry.
 */
class Tier implements Iterable<Tier> {
	private int coordinate;
	private Tier next;
	private Tier prev;
	
	public Tier newLeftOf() {
		Tier newTier = new Tier();
		Tier prePrev = prev;
		if(prePrev!=null) prePrev.next = newTier;
		prev = newTier;
		newTier.next = this;
		newTier.prev = prePrev;
		return newTier;
	}
	
	public Tier newRightOf() {
		Tier newTier = new Tier();
		Tier preNext = next;
		if(preNext!=null) preNext.prev = newTier;
		next = newTier;
		newTier.prev = this;
		newTier.next = preNext;
		return newTier;
	}
	
	public int getCoordinate() {
		return coordinate;
	}
	
	public void setTierCoordinates() {
		int i = 0;
		for(Tier t : this) t.coordinate = i++;
	}

	@Override
	public String toString() {
		return coordinate + "~" + super.toString();
	}
	@Override
	public Iterator<Tier> iterator() {
		Tier start = this;
		while (start.prev != null) start = start.prev;
		return new TierIterator(start);
	}
	
	private static class TierIterator implements Iterator<Tier> {
		Tier entry;

		public TierIterator(Tier entry) {
			super();
			this.entry = entry;
		}

		@Override
		public boolean hasNext() {
			return entry != null;
		}

		@Override
		public Tier next() {
			Tier tmp = entry;
			entry = entry.next;
			return tmp;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove is not implemented");
		}
		
	}
}
