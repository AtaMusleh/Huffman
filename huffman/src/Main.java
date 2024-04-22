import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {
	byte[] treeBytes;
	int index = 0;
	File f = null;

	byte[] fileBytes = null;
	short treeSizeLastIndex = 0;
	int arrPosition = 0;
	String path = "";

	File f1 = null;
	String path1 = "";
	static String statics = "";
	TextArea textArea = new TextArea();
	TextArea freqTa = new TextArea();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Label top = new Label("Files Compress/Uncompress");
		top.setStyle("-fx-font-size: 22px;  -fx-font-weight: bold;");
		top.setTextFill(Color.WHITE);
		Button comp = new Button("Compress");
		comp.setStyle(
				"-fx-background-radius: 15;-fx-min-width: 100px; -fx-min-height: 40px; -fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #ffffff;");

		Button uncomp = new Button("Uncompress");
		uncomp.setStyle(
				"-fx-background-radius: 15;-fx-min-width: 100px; -fx-min-height: 40px; -fx-font-size: 16px;-fx-font-weight: bold;-fx-background-color: #ffffff;");
		textArea.setPrefWidth(246);
		textArea.setPrefHeight(248);
		textArea.setLayoutX(543);
		textArea.setLayoutY(21);
		textArea.setEditable(false);

		freqTa.setPrefWidth(246);
		freqTa.setPrefHeight(248);
		freqTa.setLayoutX(543);
		freqTa.setLayoutY(21);
		freqTa.setEditable(false);
		comp.setOnAction(e -> {
			try {
				FileChooser chooser = new FileChooser();
				f = chooser.showOpenDialog(stage);
				path = f.getAbsolutePath();
				String getname = f.getName();
				int pos = getname.lastIndexOf(".");
				String exe = "";
				if (pos > 0) {
					exe = getname.substring(pos + 1);
				}

				if (exe.equals("huff")) {
					Alert alert = new Alert(Alert.AlertType.NONE, "The File is Already Encoded ", ButtonType.OK);
					if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
					}
					return;
				}

				freqTa.clear();
				textArea.clear();
				String getname1 = f.getName();
				int pos1 = getname1.lastIndexOf(".");
				if (pos1 > 0) {
					getname1 = getname1.substring(0, pos1);
				}
				String parnt = f.getParent();
				parnt = parnt + "\\" + getname1 + ".huff";

				compress(stage, getname, exe, parnt);
				freqTa.appendText(statics);

				Alert alert = new Alert(Alert.AlertType.NONE, "The compressing Process is Done", ButtonType.OK);
				alert.showAndWait();

			} catch (NullPointerException d) {
				Alert alert = new Alert(Alert.AlertType.NONE, "No file selected.", ButtonType.OK);
				alert.showAndWait();

				return;

			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}

		});
		uncomp.setOnAction(e -> {
			FileChooser chooser = new FileChooser();
			f1 = chooser.showOpenDialog(stage);

			if (f1 == null) {
				Alert alert = new Alert(Alert.AlertType.NONE, "No file selected.", ButtonType.OK);
				alert.showAndWait();
				return;
			}

			path1 = f1.getAbsolutePath();
			String getname = f1.getName();
			int pos = getname.lastIndexOf(".");
			String ext = "";

			if (pos > 0) {
				ext = getname.substring(pos + 1);
			}

			if (!ext.equals("huff")) {
				Alert alert = new Alert(Alert.AlertType.NONE, "The File does not have the Extension huff ",
						ButtonType.OK);
				if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
				}

				return;
			}

			try {
				freqTa.clear();
				textArea.clear();
				uncompress(stage);
				Alert alert = new Alert(Alert.AlertType.NONE, "The decompressing Process is Done", ButtonType.OK);
				alert.showAndWait();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});

		HBox HT = new HBox(15, textArea, freqTa);
		HT.setAlignment(Pos.CENTER);
		HBox btH = new HBox(15, comp, uncomp);
		btH.setAlignment(Pos.CENTER);
		VBox v = new VBox(15, top, HT, btH);
		v.setAlignment(Pos.CENTER);
		BorderPane bp = new BorderPane(v);
		bp.setBackground(new Background(new BackgroundFill(Color.DIMGREY, null, null)));
		Scene s = new Scene(bp, 700, 400);
		stage.setScene(s);
		stage.setTitle("Huffman");
		stage.show();
	}

	public static void writeSize(String inputFileName, String outputFileName) throws IOException {
		File file = new File(inputFileName);
		long originalSize = file.length();
		File file2 = new File(outputFileName);
		long compressedSize = file2.length();
		double ratio = (double) (originalSize - compressedSize) / originalSize * 100;
		statics = "\nOriginal file size: " + originalSize + " bytes.\n";
		statics += "Compression ratio: %" + ratio + ".\n";

	}

	public void compress(Stage stage, String getname, String exe, String parnt) throws IOException {

		String fileName = getname;
		String extention = exe;
		byte extentionLength = (byte) extention.length();
		File file = new File(path);
		long[] freq = new long[256];
		byte[] fileBytes = null;

		try (InputStream inputStream = Files.newInputStream(file.toPath());
				ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[8]; // Change buffer size to 8 bytes
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

			fileBytes = baos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace(); // Handle the exception according to your needs
		}

		// count the chars
		for (int i = 0; i < fileBytes.length; i++) {
			if (fileBytes[i] < 0) {
				short tempNum = (short) (fileBytes[i] + 256);
				freq[tempNum]++;
			} else {
				freq[fileBytes[i]]++;
			}

		}

		if (fileBytes != null) {
			heap heap = new heap();
			huffNode[] huffmanTable = new huffNode[256];

			// make Huffman table and
			// add the nodes to the heap
			short leafs = 0;
			for (short i = 0; i < 256; i++) {
				huffmanTable[i] = new huffNode((char) i, freq[i]);
				if (freq[i] != 0) {
					heap.Enqueue(huffmanTable[i]);
					leafs++;
				}
			}

			int n = leafs; // Number of Huffman tree nodes
			// build Huffman tree
			for (short i = 1; i < leafs; i++) {
				huffNode node = new huffNode();
				huffNode left = heap.Dequeue();
				huffNode right = heap.Dequeue();
				node.setRight(right);
				node.setLeft(left);
				node.setFreq(left.getFreq() + right.getFreq());
				heap.Enqueue(node);
				n++;
			}
			// Assign code to each char
			HuffmanTree huffmanTree = new HuffmanTree(heap.Dequeue());
			heap = null;
			huffmanTree.buildCode();

			int fileSize = fileBytes.length; // original file size in bytes

			// get the byte array that represent the tree
			short treeBytesSize = (short) ((leafs * 2) + (n - leafs));
			treeBytes = new byte[treeBytesSize];
			index = 0;
			treeBytes(huffmanTree.getRoot());

			// Create the buffer
			ByteBuffer buffer = ByteBuffer.allocate(1024);

			// Extention and length
			textArea.appendText(extentionLength + " " + extention + " ");
			buffer.put(extentionLength);
			for (int i = 0; i < extentionLength; i++) {
				buffer.put((byte) extention.charAt(i));
			}
			textArea.appendText(fileBytes.length + " ");
			buffer.putInt(fileSize);

			// The tree and it's size
			buffer.putShort(treeBytesSize);
			textArea.appendText(treeBytesSize + "\n ");
			String tree = "";
			for (int i = 0; i < treeBytesSize; i++) {
				if (treeBytes[i] == 1) {
					i++;
					if (treeBytes[i] < 0) {
						tree = tree + "1" + (char) (treeBytes[i] + 256) + "\n";
					} else {
						tree = tree + "1" + (char) treeBytes[i] + "\n";
					}
				} else {
					tree = tree + "0 ";
				}
			}
			textArea.appendText(tree);

			buffer.put(treeBytes);
			freqTa.appendText("\nFrequency of Each Letter and Number:\n");
			for (int i = 0; i < 256; i++) {
				if (huffmanTable[i] != null && huffmanTable[i].getFreq() > 0) {
					char asciiChar = (char) i;
					freqTa.appendText(
							"Char: " + asciiChar + ", Freq: " + huffmanTable[i].getFreq() + ", ASCII: " + i + "\n");
				}
			}

			// creat the outfile and channel to print

			String outFilePath = parnt;

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outFilePath);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			if (fos != null) {
				FileChannel fc = fos.getChannel();

				// Print the header and clear the buffer
				buffer.flip();
				try {
					fc.write(buffer);
					buffer.clear();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				String buffCode = "";
				Conv convert = new Conv();
				for (int i = 0; i < fileBytes.length; i++) {
					// Get the byte with Huffman Representation
					String charCode = null;
					if (fileBytes[i] < 0) {
						charCode = huffmanTable[fileBytes[i] + 256].huffCode;
					} else {
						charCode = huffmanTable[fileBytes[i]].huffCode;
					}

					for (int j = 0; j < charCode.length(); j++) {

						if (buffCode.length() == 8) {
							if (buffer.position() != buffer.limit()) {
								buffer.put(convert.binaryToByte(buffCode));
								buffCode = "";
							} else {
								buffer.flip();
								try {
									fc.write(buffer);
									buffer.clear();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								buffer.put(convert.binaryToByte(buffCode));
								buffCode = "";
							}

						}
						buffCode = buffCode + charCode.charAt(j);
					}
				}

				if (buffCode.length() != 0) {
					if (buffCode.length() != 8) {
						int bits = 8 - buffCode.length();
						for (int i = 0; i < bits; i++) {
							buffCode = buffCode + "0";
						}
					}

					if (buffer.position() != buffer.limit()) {
						buffer.put(convert.binaryToByte(buffCode));
						buffCode = "";
					} else {
						try {
							buffer.flip();
							fc.write(buffer);
							buffer.clear();
							buffer.put(convert.binaryToByte(buffCode));
							buffer.flip();
							fc.write(buffer);
							buffer.clear();
							buffCode = "";
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

				if (buffer.remaining() != 0) {
					buffer.flip();
					try {
						fc.write(buffer);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					buffer.clear();
				}
				writeSize(path, outFilePath);

			}
		}

	}

	private void treeBytes(huffNode root) {
		if (root == null) {
			return;
		}

		if (root.getLeft() == null && root.getRight() == null) {
			treeBytes[index] = 1;
			index++;
			treeBytes[index] = (byte) root.getCh();
			index++;
			String leafInfo = "1";
			freqTa.appendText(leafInfo);
		} else {
			treeBytes[index] = 0;
			index++;
			String nonLeafInfo = "0";
			freqTa.appendText(nonLeafInfo);
			treeBytes(root.getLeft());
			treeBytes(root.getRight());

		}
	}

	public void uncompress(Stage stage) throws IOException {

		try {
			// Read 8 bytes at a time until the end of the file
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream input = new FileInputStream(f1)) {
				byte[] buffer = new byte[8];
				int bytesRead;
				while ((bytesRead = input.read(buffer)) != -1) {
					baos.write(buffer, 0, bytesRead);
				}
			}
			fileBytes = baos.toByteArray();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		int position = 0; // Start from the beginning

		// read the extension length
		byte extensionLength = fileBytes[position++];
		if (extensionLength < 0) {
			extensionLength = (byte) (extensionLength + 256);
		}

		// The actual size index = 1 + extensionLength
		int actualSizeIndex = position + extensionLength;

		// read the extension
		String extension = "";
		for (int i = position; i < actualSizeIndex; i++) {
			if (fileBytes[i] < 0) {
				char c = (char) (fileBytes[i] + 256);
				extension = extension + c;
			} else {
				char c = (char) fileBytes[i];
				extension = extension + c;
			}
		}
		position = actualSizeIndex;

		// read the actual size
		String binarySize = "";
		for (int i = actualSizeIndex; i < actualSizeIndex + 4; i++) {
			binarySize = binarySize + Conv.byteToBinary(fileBytes[i]);
		}
		position += 4;
		// getting the actual size
		int actualSize = Integer.parseInt(binarySize, 2);

		binarySize = "";
		for (int i = actualSizeIndex + 4; i < actualSizeIndex + 6; i++) {
			binarySize = binarySize + Conv.byteToBinary(fileBytes[i]);
		}
		short treeSize = Short.parseShort(binarySize, 2);
		position += 2;
		// Where the Tree begins

		// Rebuild the tree from the header
		treeSizeLastIndex = (short) (position + treeSize);
		arrPosition = position - 1;
		huffNode root = readTree();
		HuffmanTree tree = new HuffmanTree(root);
		tree.buildCode();

		position = position + treeSize; // Point position to data

		// Create the buffer
		ByteBuffer buffer = ByteBuffer.allocate(1024); // Reduced buffer size

		// create the outfile and channel to print
		int index = path1.lastIndexOf(".");
		String outputFileName = path1.substring(0, index);

		String outFilePath = outputFileName + "." + extension;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		if (fos != null) {
			FileChannel fc = fos.getChannel();

			huffNode tempNode = tree.getRoot();
			String tempHuffCode = "";
			String temp = "";
			for (; position < fileBytes.length; position++) {
				tempHuffCode = Conv.byteToBinary(fileBytes[position]);
				for (int j = 0, i = 0; i < actualSize && j < tempHuffCode.length(); j++) {

					char c = tempHuffCode.charAt(j);
					temp = temp + c;

					if (c == '1') {
						tempNode = tempNode.getRight();
					} else {
						tempNode = tempNode.getLeft();
					}

					if (tempNode.getLeft() == null && tempNode.getRight() == null) {
						if (buffer.position() == buffer.limit()) {
							buffer.flip();
							try {
								fc.write(buffer);
								buffer.clear();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						buffer.put((byte) tempNode.getCh());
						i++;
						temp = "";
						tempNode = tree.getRoot();
					}

				}
			}

			if (buffer.remaining() != 0) {
				buffer.flip();
				try {
					fc.write(buffer);
					buffer.clear();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public huffNode readTree() {
		arrPosition++;
		huffNode root = null;
		if (arrPosition >= treeSizeLastIndex - 1) {
			return null;
		}
		if (fileBytes[arrPosition] == 1) {
			if (fileBytes[arrPosition] < 0) {
				root = new huffNode((char) (fileBytes[arrPosition + 1] + 256));
			} else {
				root = new huffNode((char) fileBytes[arrPosition + 1]);
			}
			arrPosition++;
		} else {
			root = new huffNode();
			root.left = readTree();
			root.right = readTree();
		}

		return root;
	}

}
