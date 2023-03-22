import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerPrime implements IPrimesService {

    static String ipServer;
    static int portServer;
    static String ipRegistry;
    static int portRegistry; // Porto do serviço register
    static ServerPrime srv = null;
    private static final int NUM_OF_THREADS = 5;
    static ExecutorService executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.exit(9);
        }

        ipServer = args[0];
        portServer = Integer.parseInt(args[1]);
        // IP registry e IP server são igual porque estão no contexto da mesma máquina. Caso registry noutra máquina ipRegistry teria que ser diferente
        ipRegistry = ipServer;
        portRegistry = Integer.parseInt(args[2]);

        try {
            Properties props = System.getProperties();
            props.put("java.rmi.server.hostname", ipRegistry);

            srv = new ServerPrime();
            IPrimesService stubSrv = (IPrimesService) UnicastRemoteObject.exportObject(srv, portServer);

            Registry registry = LocateRegistry.createRegistry(portRegistry);
            registry.rebind("RemoteServer", stubSrv);

            System.out.println("Server ready: Press any key to finish server");

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            System.exit(0);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("Server unhandled exception: " + ex);
        }
    }

    @Override
    public void findPrimes(int startNumber, int endNumber, ICallback callback) throws RemoteException {
        int intervalSize = (endNumber - startNumber + 1) / NUM_OF_THREADS;
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            int subIntervalStart = startNumber + i * intervalSize;
            int subIntervalEnd = (i == NUM_OF_THREADS - 1) ? endNumber : subIntervalStart + intervalSize - 1;
            executor.execute(new Worker(subIntervalStart,subIntervalEnd,callback));
        }
    }
}
