package gui;

import java.lang.reflect.Method;

import javax.swing.JFrame;

public class GenericThread extends Thread  { 
	Method runMyMet;
	Object[] arguments;
	
	//constructor1
	public GenericThread(Method meth, Object[] args) {
		this.runMyMet = meth;
		this.arguments = args;
		this.setPriority(MAX_PRIORITY);
		
	}
	
	//constructor1
	public GenericThread(Method meth){
		this.runMyMet = meth;
		this.arguments = null;
		//this.setPriority(MAX_PRIORITY);

	}

	public GenericThread(boolean showDiag, JFrame f){
		if(showDiag){
			new ProgressDialog(f);
			this.setPriority(MAX_PRIORITY);

		}
	}
	
    
    public void run() {
/*
        *//*************Aici se cheama metoda runMyMet******************//*        
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
				}*/
    }
        
		/*************************************************************/       
            
        
        

   /* public void done() {
    	System.out.println("I'm gone");
        Toolkit.getDefaultToolkit().beep();
        
        //TODO add "when done" orders
    }*/
}