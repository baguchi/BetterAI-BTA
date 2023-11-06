package baguchan.better_ai.path;

public class BetterBinaryHeap {
	public BetterNode[] heap = new BetterNode[1024];
	private int size = 0;

	public BetterBinaryHeap() {
	}

	public BetterNode insert(BetterNode node) {
		if (node.heapIdx >= 0) {
			throw new IllegalStateException("OW KNOWS!");
		} else {
			if (this.size == this.heap.length) {
				BetterNode[] resizedArray = new BetterNode[this.size << 1];
				System.arraycopy(this.heap, 0, resizedArray, 0, this.size);
				this.heap = resizedArray;
			}

			this.heap[this.size] = node;
			node.heapIdx = this.size;
			this.upHeap(this.size++);
			return node;
		}
	}

	public void clear() {
		this.size = 0;
	}

	public BetterNode pop() {
		BetterNode node = this.heap[0];
		this.heap[0] = this.heap[--this.size];
		this.heap[this.size] = null;
		if (this.size > 0) {
			this.downHeap(0);
		}

		node.heapIdx = -1;
		return node;
	}

	public void changeCost(BetterNode node, float cost) {
		float f = node.f;
		node.f = cost;
		if (cost < f) {
			this.upHeap(node.heapIdx);
		} else {
			this.downHeap(node.heapIdx);
		}

	}

	private void upHeap(int index) {
		BetterNode node = this.heap[index];

		int j;
		for (float f = node.f; index > 0; index = j) {
			j = index - 1 >> 1;
			BetterNode pathpoint1 = this.heap[j];
			if (f >= pathpoint1.f) {
				break;
			}

			this.heap[index] = pathpoint1;
			pathpoint1.heapIdx = index;
		}

		this.heap[index] = node;
		node.heapIdx = index;
	}

	private void downHeap(int index) {
		BetterNode pathpoint = this.heap[index];
		float f = pathpoint.f;

		while (true) {
			int j = 1 + (index << 1);
			int k = j + 1;
			if (j >= this.size) {
				break;
			}

			BetterNode pathpoint1 = this.heap[j];
			float f1 = pathpoint1.f;
			BetterNode pathpoint2;
			float f2;
			if (k >= this.size) {
				pathpoint2 = null;
				f2 = Float.POSITIVE_INFINITY;
			} else {
				pathpoint2 = this.heap[k];
				f2 = pathpoint2.f;
			}

			if (f1 < f2) {
				if (f1 >= f) {
					break;
				}

				this.heap[index] = pathpoint1;
				pathpoint1.heapIdx = index;
				index = j;
			} else {
				if (f2 >= f) {
					break;
				}

				this.heap[index] = pathpoint2;
				pathpoint2.heapIdx = index;
				index = k;
			}
		}

		this.heap[index] = pathpoint;
		pathpoint.heapIdx = index;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}
}
