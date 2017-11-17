package assign3;

import java.util.LinkedList;
import java.util.Queue;

public class Functions {

	static int MEMOSIZE = 256;
	static int failReq = 0;
	Queue<Integer> splitMemoSize = new LinkedList<Integer>();

	public Functions() {
		// TODO Auto-generated constructor stub
	}

	public void initTree(Node node) {

		Node root = node;
		root.setMemoSize(MEMOSIZE);
		addNode(root, MEMOSIZE);

	}

	public void addNode(Node focusNode, int memosize) {

		if (memosize > 1) {

			focusNode.leftChild = new Node(0);
			focusNode.leftChild.setMemoSize(memosize / 2);
			focusNode.rightChild = new Node(0);
			focusNode.rightChild.setMemoSize(memosize / 2);

			addNode(focusNode.leftChild, memosize / 2);
			addNode(focusNode.rightChild, memosize / 2);

		}
	}

	public void printTreeWithMemoSize(Node root) {
		if (root == null)
			return;
		Queue<Node> queue = new LinkedList<>();

		int current;
		int next;

		queue.offer(root);
		current = 1;
		next = 0;
		while (!queue.isEmpty()) {
			Node currentNode = queue.poll();
			System.out.print(currentNode.MemoSize);
			current--;

			if (currentNode.leftChild != null) {
				queue.offer(currentNode.leftChild);
				next++;
			}
			if (currentNode.rightChild != null) {
				queue.offer(currentNode.rightChild);
				next++;
			}
			if (current == 0) {
				System.out.println();
				current = next;
				next = 0;
			}
		}
	}

	public void printTree(Node root) {
		if (root == null)
			return;
		Queue<Node> queue = new LinkedList<>();

		int current;
		int next;

		queue.offer(root);
		current = 1;
		next = 0;
		while (!queue.isEmpty()) {
			Node currentNode = queue.poll();
			System.out.print(currentNode.state);
			current--;

			if (currentNode.leftChild != null) {
				queue.offer(currentNode.leftChild);
				next++;
			}
			if (currentNode.rightChild != null) {
				queue.offer(currentNode.rightChild);
				next++;
			}
			if (current == 0) {
				System.out.println();
				current = next;
				next = 0;
			}
		}

	}

	public void request(Node node, int memo, int time) {
		int size;
		Node focusNode;
		split(memo);
		if (preTest(node)) {
			while (!splitMemoSize.isEmpty()) {
				
				size = splitMemoSize.poll();
				focusNode = traverseBySize(node, size);
				mark(focusNode, time);

			}
		} else {
			System.out.println("Request Fail");
			failReq++;
		}

	}

	public void release(Node node) {
		if (node != null && node.timeFrame == 0) {
			node.state = "□";
			release(node.leftChild);
			release(node.rightChild);

		}

	}

	public void timePass(Node node) {
		if (node != null) {
			if (node.timeFrame > 0) {
				node.timeFrame -= 1;
			}
			timePass(node.leftChild);
			timePass(node.rightChild);
		}

	}

	public Node traverseBySize(Node node, int size) {
		if (node != null) {
			if (node.MemoSize == size && node.state.equals("□")) {
				if (isBusy(node)) {
					return null;
				} else {
					return node;
				}
			} else {
				Node foundNode = traverseBySize(node.leftChild, size);
				if (foundNode == null) {
					foundNode = traverseBySize(node.rightChild, size);
				}
				return foundNode;
			}

		} else {
			return null;
		}
	}

	public boolean isBusy(Node node) {
		if (node != null) {
			if (node.state.equals("■")) {
				return true;
			} else {
				Boolean next = isBusy(node.leftChild);
				if (next == false) {
					next = isBusy(node.rightChild);
				}
				return next;
			}

		} else {
			return false;
		}

	}

	public void mark(Node node, int time) {
		if (node != null) {
			node.setTimeFrame(time);
			node.setState("■");
			mark(node.leftChild, time);
			mark(node.rightChild, time);

		}

	}

	public void split(int memo) {
		if (memo > 1) {
			int factor = (int) (((int) Math.pow(2, ((int) (Math.ceil(Math.log(memo) / Math.log(2)))))) / 2);
			if (memo == factor * 2) {
				splitMemoSize.offer(factor * 2);
				return;
			}
			int rem = memo - factor;
			splitMemoSize.offer(factor);
			split(rem);

		} else {
			splitMemoSize.offer(1);
		}

	}

	public boolean preTest(Node node) {
		for (int i : splitMemoSize) {
			if (traverseBySize(node, i) == null) {
				return false;
			}

		}
		return true;

	}

}
