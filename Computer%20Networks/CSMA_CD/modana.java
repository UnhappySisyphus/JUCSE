import java.lang.Thread;
import java.lang.Math;
import java.io.*;
import java.util.*;
 
class backoffTimer {
	
	public int backoffTime(int transNum) { 
		int rndom;
		int temp;
		temp=Math.min(transNum,10);
		rndom=(int)(Math.random()*(Math.pow(2,temp)-1))+1;
		return rndom; 
	}
}
class ReceiverNode extends Thread{
	private String name;			
	private String filename;
	public ReceiverNode(String name, String fname) { 
		this.name = name;
		filename=fname;
	}
	public void run() {
		while(true)
		{
			File f=new File(filename);
			if(f.exists()&&f.isFile())
			{
				try{
					Scanner s=new Scanner(f);
					System.out.println("Receiver "+name+" reads "+s.nextInt()+" from Sender "+s.nextLine());
					break;
				}
				catch(Exception e) { }
			}
		}	
	}
}
class SenderNode extends Thread{
	private int count = 0;			
	public static Integer bus = 0;	
	private String name;			
	private int num;				
	private int propDelay;			
	private String receiverfile;
	
	public SenderNode(String name, int total, int propDelay,String receiver) { 
		this.name = name;
		num = total;
		this.propDelay = propDelay;
		receiverfile=receiver;
	}
	public void run() {
		
		for (int n = 1; n <= num; n++) {
			
			count = 0;
			while (count < 10) {
				
				//System.out.println("Sensing channel...");
				int flag=0,collide=0;
				synchronized(bus){
					if(bus==0) {
						bus++;
						System.out.println(name+" finds channel idle.\n"+name+" Transmitting packet "+n+"\nChannel now busy");	
						try{
							PrintWriter file = new PrintWriter(receiverfile);
							file.println(((Math.random()/100)+1)+" "+name);
							//System.out.println(name+");
							collide=0;
							//System.out.println("Channel now busy");
						}
						catch(Exception e) { }
					}
				}
				if(flag==1){
					//try{
						flag=0;
						int flag2=0;
						//Thread.sleep(propDelay);
						for(float f=-10000;f<=10000;f++);
							synchronized(bus){
								if(bus>1)
									collide=1;
							}
					//} catch (InterruptedException e) {
					//	System.err.println("Interrupted: Interrupt exception");
					//}
				}
				
				else {
					System.out.println(name+ " finds channel busy. Continue sensing...");
					continue;
				}    
				
				//try {
					for(float f=-10000;f<=10000;f++);
							synchronized(bus){
								if(bus>1)
									collide=1;
							}
				//} catch (InterruptedException e) {
				//	System.err.println("Interrupted: Interrupt exception");
				//}
				
				if(collide==0){
					synchronized(bus){
						bus=0;
					}
					System.out.println(name+" Packet: " +n+ " Sent successfully!");
					
					break;
				}
				else{
					count++;
					System.out.println(name+" Packet: " +n+" Collision "+count);
					backoffTimer timer = new backoffTimer();
					int bckof=2*propDelay*timer.backoffTime(count);
					System.out.println(name+" backing off for "+(bckof));
					synchronized(bus){
						bus=0;
					}
						
					try {
						Thread.sleep(bckof);
					} catch (InterruptedException e) {
						System.err.println("Interrupted: Interrupt exception");
					}
					continue;
				}
				
			}
			if (count >= 10){
				System.out.println(name + " Packet: " +n+ " Transmission failure.");
			}
		}
	}	
}

public class modana {
	public static void main(String[] args) {
		
		int nodeNumber;				
		int transNumber;				
		int delayTime;					
		
		Scanner scan = new Scanner(System.in);

		System.out.println("Number of sender(and receiver) nodes:");
		nodeNumber=scan.nextInt();

		System.out.println("Number of packets per node: ");
		transNumber=scan.nextInt();
		       
		System.out.println("Propagation delay time: ");
		delayTime=scan.nextInt();
		 
		SenderNode[] SenderNodes=new SenderNode[nodeNumber];
		ReceiverNode[] ReceiverNodes=new ReceiverNode[nodeNumber];
		for(int j=0;j<nodeNumber;j++){
			SenderNodes[j]=new SenderNode("SNode"+(char)('A'+j),transNumber,delayTime,(char)('A'+j)+".txt");
		}
		for(int j=0;j<nodeNumber;j++){
			ReceiverNodes[j]=new ReceiverNode("RNode"+(char)('A'+j),(char)('A'+j)+".txt");
			ReceiverNodes[j].start();
		}
		for (int i = 0; i < nodeNumber; i++) {
			SenderNodes[i].start();
		}
	}
}
