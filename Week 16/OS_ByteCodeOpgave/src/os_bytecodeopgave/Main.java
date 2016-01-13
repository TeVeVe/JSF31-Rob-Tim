package os_bytecodeopgave;

import javax.swing.JFrame;

/**
 *
 * @author erik
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // maak bank
        Bank bank = new Bank();

        // maak bankframe
        BankFrame bf = new BankFrame(bank);
        bf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bf.setVisible(true);

    }

}
