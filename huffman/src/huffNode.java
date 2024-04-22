import java.util.HashMap;

public class huffNode implements Comparable<huffNode> {

	public huffNode left, right;
	public long freq;
	public char ch;
	public String huffCode;

	public huffNode() {
		freq = 0;
		ch = 0;
		huffCode = "";
		left = null;
		right = null;
	}

	public huffNode(char ch) {
		super();
		this.ch = ch;
	}

	public huffNode(char ch, long freq) {
		this.freq = freq;
		this.ch = ch;
		this.left = null;
		this.right = null;
		this.huffCode = "";
	}

	public huffNode(char ch, long freq, String huffCode, huffNode left, huffNode right) {
		super();
		this.left = left;
		this.right = right;
		this.freq = freq;
		this.ch = ch;
		this.huffCode = huffCode;
	}

	public huffNode getLeft() {
		return left;
	}

	public void setLeft(huffNode left) {
		this.left = left;
	}

	public huffNode getRight() {
		return right;
	}

	public void setRight(huffNode right) {
		this.right = right;
	}

	public long getFreq() {
		return freq;
	}

	public void setFreq(long freq) {
		this.freq = freq;
	}

	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public String getHuffCode() {
		return huffCode;
	}

	public void setHuffCode(String huffCode) {
		this.huffCode = huffCode;
	}

	@Override
	public int compareTo(huffNode o) {
		// TODO Auto-generated method stub
		if (this.freq > o.freq) {
			return 1;
		} else if (this.freq < o.freq) {
			return -1;
		} else {
			return 0;
		}
	}
}