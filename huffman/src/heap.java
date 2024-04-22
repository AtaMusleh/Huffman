public class heap {

	final int DEFAULTMAX = 256;

	huffNode[] nodes;
	int capacity, total;

	public heap() {
		capacity = DEFAULTMAX;
		total = 0;
		nodes = new huffNode[capacity];
	}

	public heap(int max) {
		capacity = max;
		total = 0;
		nodes = new huffNode[capacity];

	}

	public boolean Enqueue(huffNode hNode) {

		if (isFull())
			return false;

		if (isEmpty()) { // first element?
			nodes[total++] = hNode;
			return true;
		}
		int i = total - 1, pos;
		while (i >= 0) {
			if (nodes[i].freq < hNode.freq) {
				break;
			}
			i--;
		}
		pos = total - 1;
		while (pos >= i + 1) {
			nodes[pos + 1] = nodes[pos];
			pos--;
		}
		nodes[i + 1] = hNode;
		total++;
		return true;
	}

	public huffNode Dequeue() {

		if (isEmpty())
			return null;
		huffNode ret = nodes[0];
		total--;
		for (int i = 0; i < total; i++)
			nodes[i] = nodes[i + 1];
		return ret;
	}

	public boolean isEmpty() {
		return (total == 0);
	}

	public boolean isFull() {
		return (total == capacity);
	}

	public int totalNodes() {
		return total;
	}

	// debug
	public void displayQ() {
		for (int i = 0; i < total; i++) {
			System.out.println("Q" + i + ") " + nodes[i].ch + " : " + nodes[i].freq);
		}

	}

}
