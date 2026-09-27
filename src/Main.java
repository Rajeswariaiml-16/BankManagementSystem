import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== BANK MANAGEMENT SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Transfer Money");
            System.out.println("6. Transaction History");
            System.out.println("7. Close Account");
            System.out.println("8. Exit");

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            try {

                Connection con = DBConnection.getConnection();

                switch (choice) {

                    // 1. CREATE ACCOUNT
                    case 1:

                        sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Phone: ");
                        String phone = sc.nextLine();

                        String createSql =
                                "INSERT INTO account(name, phone) VALUES (?, ?)";

                        PreparedStatement createPs =
                                con.prepareStatement(createSql);

                        createPs.setString(1, name);
                        createPs.setString(2, phone);

                        int result = createPs.executeUpdate();

                        if (result > 0) {
                            System.out.println("Account Created Successfully!");
                        }

                        break;


                    // 2. DEPOSIT
                    case 2:

                        System.out.print("Enter Account ID: ");
                        int depositId = sc.nextInt();

                        System.out.print("Enter Deposit Amount: ");
                        double depositAmount = sc.nextDouble();

                        String depositSql =
                                "UPDATE account SET balance = balance + ? " +
                                "WHERE account_id = ? AND status = 'ACTIVE'";

                        PreparedStatement depositPs =
                                con.prepareStatement(depositSql);

                        depositPs.setDouble(1, depositAmount);
                        depositPs.setInt(2, depositId);

                        int depositResult = depositPs.executeUpdate();

                        if (depositResult > 0) {

                            String transactionSql =
                                    "INSERT INTO transaction_history " +
                                    "(account_id, transaction_type, amount) " +
                                    "VALUES (?, ?, ?)";

                            PreparedStatement transactionPs =
                                    con.prepareStatement(transactionSql);

                            transactionPs.setInt(1, depositId);
                            transactionPs.setString(2, "DEPOSIT");
                            transactionPs.setDouble(3, depositAmount);

                            transactionPs.executeUpdate();

                            System.out.println(
                                    "Amount Deposited Successfully!");

                        } else {
                            System.out.println(
                                    "Account Not Found or Account is Closed!");
                        }

                        break;


                    // 3. WITHDRAW
                    case 3:

                        System.out.print("Enter Account ID: ");
                        int withdrawId = sc.nextInt();

                        System.out.print("Enter Withdraw Amount: ");
                        double withdrawAmount = sc.nextDouble();

                        String balanceSql =
                                "SELECT balance FROM account " +
                                "WHERE account_id = ? AND status = 'ACTIVE'";

                        PreparedStatement balancePs =
                                con.prepareStatement(balanceSql);

                        balancePs.setInt(1, withdrawId);

                        ResultSet balanceRs =
                                balancePs.executeQuery();

                        if (balanceRs.next()) {

                            double balance =
                                    balanceRs.getDouble("balance");

                            if (balance >= withdrawAmount) {

                                String withdrawSql =
                                        "UPDATE account SET balance = balance - ? " +
                                        "WHERE account_id = ?";

                                PreparedStatement withdrawPs =
                                        con.prepareStatement(withdrawSql);

                                withdrawPs.setDouble(1, withdrawAmount);
                                withdrawPs.setInt(2, withdrawId);

                                withdrawPs.executeUpdate();

                                String transactionSql =
                                        "INSERT INTO transaction_history " +
                                        "(account_id, transaction_type, amount) " +
                                        "VALUES (?, ?, ?)";

                                PreparedStatement transactionPs =
                                        con.prepareStatement(transactionSql);

                                transactionPs.setInt(1, withdrawId);
                                transactionPs.setString(2, "WITHDRAW");
                                transactionPs.setDouble(3, withdrawAmount);

                                transactionPs.executeUpdate();

                                System.out.println(
                                        "Amount Withdrawn Successfully!");

                            } else {
                                System.out.println("Insufficient Balance!");
                            }

                        } else {
                            System.out.println(
                                    "Account Not Found or Account is Closed!");
                        }

                        break;


                    // 4. CHECK BALANCE
                    case 4:

                        System.out.print("Enter Account ID: ");
                        int checkId = sc.nextInt();

                        String checkSql =
                                "SELECT name, balance, status FROM account " +
                                "WHERE account_id = ?";

                        PreparedStatement checkPs =
                                con.prepareStatement(checkSql);

                        checkPs.setInt(1, checkId);

                        ResultSet checkRs =
                                checkPs.executeQuery();

                        if (checkRs.next()) {

                            System.out.println(
                                    "Name: " +
                                    checkRs.getString("name"));

                            System.out.println(
                                    "Balance: ₹" +
                                    checkRs.getDouble("balance"));

                            System.out.println(
                                    "Status: " +
                                    checkRs.getString("status"));

                        } else {
                            System.out.println("Account Not Found!");
                        }

                        break;


                    // 5. TRANSFER MONEY
                    case 5:

                        System.out.print("Enter Sender Account ID: ");
                        int senderId = sc.nextInt();

                        System.out.print("Enter Receiver Account ID: ");
                        int receiverId = sc.nextInt();

                        System.out.print("Enter Transfer Amount: ");
                        double transferAmount = sc.nextDouble();

                        if (senderId == receiverId) {
                            System.out.println(
                                    "Sender and Receiver cannot be same!");
                            break;
                        }

                        String senderSql =
                                "SELECT balance FROM account " +
                                "WHERE account_id = ? AND status = 'ACTIVE'";

                        PreparedStatement senderPs =
                                con.prepareStatement(senderSql);

                        senderPs.setInt(1, senderId);

                        ResultSet senderRs =
                                senderPs.executeQuery();

                        if (!senderRs.next()) {
                            System.out.println(
                                    "Sender Account Not Found!");
                            break;
                        }

                        double senderBalance =
                                senderRs.getDouble("balance");

                        if (senderBalance < transferAmount) {
                            System.out.println("Insufficient Balance!");
                            break;
                        }

                        String receiverSql =
                                "SELECT account_id FROM account " +
                                "WHERE account_id = ? AND status = 'ACTIVE'";

                        PreparedStatement receiverPs =
                                con.prepareStatement(receiverSql);

                        receiverPs.setInt(1, receiverId);

                        ResultSet receiverRs =
                                receiverPs.executeQuery();

                        if (!receiverRs.next()) {
                            System.out.println(
                                    "Receiver Account Not Found!");
                            break;
                        }

                        String deductSql =
                                "UPDATE account SET balance = balance - ? " +
                                "WHERE account_id = ?";

                        PreparedStatement deductPs =
                                con.prepareStatement(deductSql);

                        deductPs.setDouble(1, transferAmount);
                        deductPs.setInt(2, senderId);

                        deductPs.executeUpdate();


                        String addSql =
                                "UPDATE account SET balance = balance + ? " +
                                "WHERE account_id = ?";

                        PreparedStatement addPs =
                                con.prepareStatement(addSql);

                        addPs.setDouble(1, transferAmount);
                        addPs.setInt(2, receiverId);

                        addPs.executeUpdate();


                        String sentSql =
                                "INSERT INTO transaction_history " +
                                "(account_id, transaction_type, amount) " +
                                "VALUES (?, ?, ?)";

                        PreparedStatement sentPs =
                                con.prepareStatement(sentSql);

                        sentPs.setInt(1, senderId);
                        sentPs.setString(2, "TRANSFER_SENT");
                        sentPs.setDouble(3, transferAmount);

                        sentPs.executeUpdate();


                        PreparedStatement receivedPs =
                                con.prepareStatement(sentSql);

                        receivedPs.setInt(1, receiverId);
                        receivedPs.setString(2, "TRANSFER_RECEIVED");
                        receivedPs.setDouble(3, transferAmount);

                        receivedPs.executeUpdate();

                        System.out.println(
                                "Money Transferred Successfully!");

                        break;


                    // 6. TRANSACTION HISTORY
                    case 6:

                        System.out.print("Enter Account ID: ");
                        int historyId = sc.nextInt();

                        String historySql =
                                "SELECT transaction_id, transaction_type, " +
                                "amount, transaction_date " +
                                "FROM transaction_history " +
                                "WHERE account_id = ? " +
                                "ORDER BY transaction_date DESC";

                        PreparedStatement historyPs =
                                con.prepareStatement(historySql);

                        historyPs.setInt(1, historyId);

                        ResultSet historyRs =
                                historyPs.executeQuery();

                        System.out.println(
                                "\n----- Transaction History -----");

                        boolean found = false;

                        while (historyRs.next()) {

                            found = true;

                            System.out.println(
                                    historyRs.getInt("transaction_id")
                                    + " | "
                                    + historyRs.getString("transaction_type")
                                    + " | ₹"
                                    + historyRs.getDouble("amount")
                                    + " | "
                                    + historyRs.getTimestamp(
                                            "transaction_date"));
                        }

                        if (!found) {
                            System.out.println(
                                    "No Transactions Found!");
                        }

                        break;


                    // 7. CLOSE ACCOUNT
                    case 7:

                        System.out.print("Enter Account ID: ");
                        int closeId = sc.nextInt();

                        String closeCheckSql =
                                "SELECT balance, status FROM account " +
                                "WHERE account_id = ?";

                        PreparedStatement closeCheckPs =
                                con.prepareStatement(closeCheckSql);

                        closeCheckPs.setInt(1, closeId);

                        ResultSet closeRs =
                                closeCheckPs.executeQuery();

                        if (closeRs.next()) {

                            double closeBalance =
                                    closeRs.getDouble("balance");

                            String status =
                                    closeRs.getString("status");

                            if (!status.equals("ACTIVE")) {

                                System.out.println(
                                        "Account is already closed!");

                            } else if (closeBalance > 0) {

                                System.out.println(
                                        "Cannot close account!");

                                System.out.println(
                                        "Please withdraw the remaining balance first.");

                            } else {

                                String closeSql =
                                        "UPDATE account SET status = 'CLOSED' " +
                                        "WHERE account_id = ?";

                                PreparedStatement closePs =
                                        con.prepareStatement(closeSql);

                                closePs.setInt(1, closeId);

                                closePs.executeUpdate();

                                System.out.println(
                                        "Account Closed Successfully!");
                            }

                        } else {
                            System.out.println("Account Not Found!");
                        }

                        break;


                    // 8. EXIT
                    case 8:

                        System.out.println(
                                "Thank you for using Bank Management System!");

                        con.close();
                        sc.close();

                        return;


                    default:

                        System.out.println(
                                "Invalid Choice! Please enter 1 to 8.");
                }

                con.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
}
