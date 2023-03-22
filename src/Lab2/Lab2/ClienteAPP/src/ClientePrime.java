import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class ClientePrime implements ICallback{

    static String registryIP;
    static int registryPort;
    static String clientIP;
    static int clientPort;

    public static void main(String[] args) {

        if(args.length < 4){
            System.exit(9);
        }

        registryIP = args[0];
        registryPort = Integer.parseInt(args[1]);
        clientIP = args[2];
        clientPort = Integer.parseInt(args[3]);

        try {
            Registry registry = LocateRegistry.getRegistry(registryIP, registryPort);
            IPrimesService svc = (IPrimesService) registry.lookup("RemoteServer");

            System.out.println("Forneça o primeiro número: ");
            Scanner scanner = new Scanner(System.in);
            int number1 = scanner.nextInt();

            System.out.println( "Forneça o segundo número: ");
            int number2 = scanner.nextInt();

            ClientePrime client = new ClientePrime();

            ICallback callback = (ICallback) UnicastRemoteObject.exportObject(client, clientPort);

            svc.findPrimes(number1, number2, callback);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception ex){
            System.err.println("Client unhandled exception: " + ex);
        }
    }

    @Override
    public void nextPrime(int i) throws RemoteException {
        System.out.println(" O próximo primo é: " + i);
    }
}
