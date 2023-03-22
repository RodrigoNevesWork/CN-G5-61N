import java.rmi.RemoteException;

public class Worker implements Runnable {

    int startNumber;
    int endNumber;
    ICallback callback;

    public Worker(int startNumber, int endNumber, ICallback callback){
           this.startNumber = startNumber;
           this.endNumber = endNumber;
           this.callback = callback;

    }

    private boolean isPrime(int num) {
        if (num <= 1) return false;
        if (num == 2 || num == 3) return true;
        if (num % 2 == 0) return false;
        for (int i=3; i <= Math.sqrt(num); i+=2) {
            if (num % i == 0) return false;
        }
        return true;
    }

    @Override
    public void run() {
        System.out.println( " StartNumber: " + startNumber + ", EndNumber: " + endNumber);
        for( int i = startNumber; i <= endNumber; i++ ){
            if(isPrime(i)){
                try {
                    callback.nextPrime(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
