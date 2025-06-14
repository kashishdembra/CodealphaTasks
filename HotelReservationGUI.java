import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    boolean isBooked;

    Room(int roomNumber, String category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isBooked = false;
    }

    public String toString() {
        return roomNumber + "," + category + "," + isBooked;
    }

    static Room fromString(String line) {
        String[] parts = line.split(",");
        Room r = new Room(Integer.parseInt(parts[0]), parts[1]);
        r.isBooked = Boolean.parseBoolean(parts[2]);
        return r;
    }
}

class Booking {
    String guestName;
    int roomNumber;
    String category;
    String date;

    Booking(String guestName, int roomNumber, String category, String date) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.date = date;
    }

    public String toString() {
        return guestName + "," + roomNumber + "," + category + "," + date;
    }

    static Booking fromString(String line) {
        String[] parts = line.split(",");
        return new Booking(parts[0], Integer.parseInt(parts[1]), parts[2], parts[3]);
    }
}

public class HotelReservationGUI extends JFrame {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    final String ROOMS_FILE = "rooms.txt";
    final String BOOKINGS_FILE = "bookings.txt";

    JComboBox<String> categoryDropdown;
    JTextArea outputArea;
    JTextField nameInput, dateInput;

    public HotelReservationGUI() {
        setTitle("🏨 Hotel Room Reservation");
        setSize(650, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        loadRooms();
        loadBookings();

        // Top Panel for Form Inputs
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Reservation Details"));

        formPanel.add(new JLabel("👤 Guest Name:"));
        nameInput = new JTextField();
        formPanel.add(nameInput);

        formPanel.add(new JLabel("📅 Check-in Date (dd-mm-yyyy):"));
        dateInput = new JTextField();
        formPanel.add(dateInput);

        formPanel.add(new JLabel("🏷️ Room Category:"));
        categoryDropdown = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite"});
        formPanel.add(categoryDropdown);

        JButton bookBtn = new JButton("💳 Book & Pay");
        JButton cancelBtn = new JButton("❌ Cancel Booking");
        JButton searchBtn = new JButton("🔍 Check Availability");
        JButton viewBtn = new JButton("📘 View All Bookings");

        formPanel.add(bookBtn);
        formPanel.add(cancelBtn);
        formPanel.add(searchBtn);
        formPanel.add(viewBtn);

        add(formPanel, BorderLayout.NORTH);

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBorder(BorderFactory.createTitledBorder("System Messages"));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Button Actions
        bookBtn.addActionListener(e -> handleBooking());
        cancelBtn.addActionListener(e -> handleCancellation());
        searchBtn.addActionListener(e -> displayAvailableRooms());
        viewBtn.addActionListener(e -> showAllBookings());

        // Save on close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveRooms();
                saveBookings();
            }
        });
    }

    void handleBooking() {
        String name = nameInput.getText().trim();
        String date = dateInput.getText().trim();
        String category = (String) categoryDropdown.getSelectedItem();

        if (name.isEmpty() || date.isEmpty()) {
            showMessage("⚠️ Please enter both name and check-in date.");
            return;
        }

        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Proceed to payment for Room #" + r.roomNumber + " (" + category + ")?",
                        "Confirm Payment", JOptionPane.OK_CANCEL_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    r.isBooked = true;
                    bookings.add(new Booking(name, r.roomNumber, category, date));
                    showMessage("✅ Booking confirmed! " + name + " has reserved Room #" + r.roomNumber + " on " + date + ".");
                } else {
                    showMessage("❌ Booking was cancelled.");
                }
                return;
            }
        }
        showMessage("😞 Sorry! No " + category + " rooms are currently available.");
    }

    void handleCancellation() {
        String name = nameInput.getText().trim();
        if (name.isEmpty()) {
            showMessage("⚠️ Please enter your name to cancel a booking.");
            return;
        }

        Booking toCancel = null;
        for (Booking b : bookings) {
            if (b.guestName.equalsIgnoreCase(name)) {
                toCancel = b;
                break;
            }
        }

        if (toCancel != null) {
            for (Room r : rooms) {
                if (r.roomNumber == toCancel.roomNumber) {
                    r.isBooked = false;
                    break;
                }
            }
            bookings.remove(toCancel);
            showMessage("🗑️ Booking for " + name + " has been cancelled.");
        } else {
            showMessage("❌ No booking found under the name " + name + ".");
        }
    }

    void displayAvailableRooms() {
        String category = (String) categoryDropdown.getSelectedItem();
        StringBuilder sb = new StringBuilder("🔍 Available " + category + " Rooms:\n");
        boolean found = false;
        for (Room r : rooms) {
            if (!r.isBooked && r.category.equalsIgnoreCase(category)) {
                sb.append("• Room #").append(r.roomNumber).append("\n");
                found = true;
            }
        }
        if (!found) sb.append("None available right now.");
        showMessage(sb.toString());
    }

    void showAllBookings() {
        if (bookings.isEmpty()) {
            showMessage("📃 No bookings have been made yet.");
            return;
        }

        StringBuilder sb = new StringBuilder("📘 Current Bookings:\n");
        for (Booking b : bookings) {
            sb.append("👤 ").append(b.guestName)
                    .append(" | 🛏️ Room #").append(b.roomNumber)
                    .append(" | 🏷️ ").append(b.category)
                    .append(" | 📅 ").append(b.date).append("\n");
        }
        showMessage(sb.toString());
    }

    void showMessage(String msg) {
        outputArea.append(msg + "\n");
    }

    void loadRooms() {
        File file = new File(ROOMS_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null)
                    rooms.add(Room.fromString(line));
            } catch (IOException e) {
                showMessage("❌ Error loading room data.");
            }
        } else {
            // Default room setup if file doesn't exist
            for (int i = 1; i <= 5; i++) rooms.add(new Room(i, "Standard"));
            for (int i = 6; i <= 8; i++) rooms.add(new Room(i, "Deluxe"));
            for (int i = 9; i <= 10; i++) rooms.add(new Room(i, "Suite"));
        }
    }

    void saveRooms() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room r : rooms)
                bw.write(r.toString() + "\n");
        } catch (IOException e) {
            showMessage("❌ Failed to save room data.");
        }
    }

    void loadBookings() {
        File file = new File(BOOKINGS_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null)
                    bookings.add(Booking.fromString(line));
            } catch (IOException e) {
                showMessage("❌ Error loading bookings.");
            }
        }
    }

    void saveBookings() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking b : bookings)
                bw.write(b.toString() + "\n");
        } catch (IOException e) {
            showMessage("❌ Failed to save bookings.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelReservationGUI().setVisible(true));
    }
}
