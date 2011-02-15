package gui;


public class StartMe {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("App started");
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            	new BaseFrame();
            }
        }); 

	}

}
