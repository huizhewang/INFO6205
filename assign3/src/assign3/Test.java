package assign3;

public class Test {

	static int TESTTIMES = 100;

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Node root = new Node(0);
		Functions test = new Functions();
		System.out.println("The sturck of binary tree is as follows:");
		test.initTree(root);
		test.printTreeWithMemoSize(root);

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < TESTTIMES; i++) {
			int memo = (int) (Math.random() * 128);
			int time = (int) (Math.random() * 8);
			System.out.println("Timeframe: "+ (i+1) + " Failed request: "+ Functions.failReq);
			System.out.println("-----------------------------------------------------------------------------------------------------");
			System.out.println("Request for memory size: "+ memo+" and timeframe as: "+time);
			test.request(root,memo ,time );
			test.printTree(root);
			test.timePass(root);
			System.out.println("Releasing memory...");
			test.release(root);
			System.out.println();
			

		}
		System.out.println("-----------------------------------------------------------------------------------------------------");
		System.out.println("Program finished.");
		long endTime = System.currentTimeMillis();
		System.out.println("Total running time: " + (-startTime + endTime));
		System.out.println("Total failed request times: " + Functions.failReq);

	}

}
