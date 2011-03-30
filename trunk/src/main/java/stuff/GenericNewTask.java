package stuff;

import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class GenericNewTask extends SwingWorker<Void, Void> { 
	Method runMyMet;
	Object[] arguments;
	
	//constructor1
	public GenericNewTask(Method meth, Object[] args) {
		this.runMyMet = meth;
		this.arguments = args;
		
	}
	
	//constructor2
	public GenericNewTask(Method meth){
		this.runMyMet = meth;
		this.arguments = null;
	}
	
	//constructor2
	public GenericNewTask(boolean showDiag, JFrame f){
		if(showDiag){
			new gui.ProgressDialog(f);
		}
	}
	
    @Override
    public Void doInBackground() {

        try {
            while (!isCancelled()) {
                Thread.sleep(1000);

        /*************Aici se cheama metoda runMyMet******************/        
                try {
					runMyMet.invoke(runMyMet.getClass(), arguments);
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		/*************************************************************/       
            }
        } catch (InterruptedException ignore) {}
        return null;
    }

    @Override
    public void done() {
    	System.out.println("I'm gone");
        Toolkit.getDefaultToolkit().beep();
        
        //TODO add "when done" orders
    }
}