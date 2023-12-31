// package src.backend;
// import java.util.Scanner;

// public class Driver {
//     public static void main(String[] args) {
//         Initializer.main();
//         Scanner scan = new Scanner(System.in);
//         UserManager userManager = new UserManager();

//         Runtime.getRuntime().addShutdownHook(new Thread() {
//             public void run() {FileHandler.closeVault();}});

//         while (true) {
//             User user = User.authenticate(scan, userManager);
//             Vault vault = new Vault(scan, userManager, user);
//             vault.mainMenu();
//             System.out.println("____________________________________________________________\n");
//         }
//     }
// }
