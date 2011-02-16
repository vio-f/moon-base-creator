package gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class ProgressDialog extends JDialog{
	
	JFrame bf;
	JDialog progDiag;
	JProgressBar pBar = new JProgressBar(0, 500); 		
	//TODO JButton cancelButton = new JButton("Cancel process...");
	

	

	public ProgressDialog (JFrame f) {
		this.bf = f;
		progDiag = new JDialog(bf, "Patience is a virtue...", false);
		//progDiag.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		progDiag.setSize(250, 50);
		progDiag.setResizable(false);
		progDiag.setLocationRelativeTo(bf);
		
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											} catch (UnsupportedLookAndFeelException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

		
		
		progDiag.add(pBar);
		pBar.setString("Good things in progress...");
		pBar.setStringPainted(true);
		//pBar.setValue(0);
		pBar.setIndeterminate(true);
		progDiag.addWindowListener(   
						new java.awt.event.WindowAdapter(){
							public void windowClosing( java.awt.event.WindowEvent e ){
								System.out.println( "good bye" );
								dispose() ;
							//TODO Stop task
							} 
						}  
				 );

		progDiag.setVisible(true);
	}

}
		

	


