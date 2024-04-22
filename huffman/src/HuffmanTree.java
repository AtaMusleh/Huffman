public class HuffmanTree {

	huffNode root;
	huffNode[] table = null;
	int nodes = 0;
	String[] codes = null;

	public HuffmanTree(huffNode root) {
		super();
		this.root = root;
	}

	public huffNode getRoot() {
		return root;
	}

	public void setRoot(huffNode root) {
		this.root = root;
	}

	public void buildCode() {
		buildCode(root, "");
	}

	private void buildCode(huffNode root, String code) {
		if (root == null) {
			return;
		}

		if (root.getLeft() == null && root.getRight() == null) {
			root.setHuffCode(code);
		}

		buildCode(root.getLeft(), code + "0");
		buildCode(root.getRight(), code + "1");
	}

	public int getNodes() {
		return nodes;
	}

	public String[] getCodes() {
		codes = new String[256];
		getCodesRe(this.root);
		return codes;
	}

	private void getCodesRe(huffNode root) {
		if (root == null) {
			return;
		}

		if (root.getLeft() == null && root.getRight() == null) {
			byte b = (byte) root.getCh();
			if (b < 0) {
				codes[b + 256] = root.getHuffCode();
				return;
			}
			codes[root.getCh()] = root.getHuffCode();
			return;
		}

		getCodesRe(root.getLeft());
		getCodesRe(root.getRight());
	}

	public void setCodes(String[] codes) {
		this.codes = codes;
	}

}
