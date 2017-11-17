package assign3;

public class Node {
	
	int MemoSize;
	String state = "□";
	int timeFrame = 0;
	Node leftChild = null;
	Node rightChild = null;
	
	
	
	public Node(int timeFrame) {
		super();
		this.timeFrame = timeFrame;
	}




	public int getTimeFrame() {
		return timeFrame;
	}




	public void setTimeFrame(int timeFrame) {
		this.timeFrame = timeFrame;
	}


	
	public int getMemoSize() {
		return MemoSize;
	}




	public void setMemoSize(int memoSize) {
		MemoSize = memoSize;
	}




	public String isState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}




	public Node getLeftChild() {
		return leftChild;
	}




	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}




	public Node getRightChild() {
		return rightChild;
	}




	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}




	@Override
	public String toString() {
		return "Node [MemoSize=" + MemoSize + ", state=" + state + ", timeFrame=" + timeFrame + "]";
	}

	
}
