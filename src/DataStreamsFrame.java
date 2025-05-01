import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DataStreamsFrame extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextArea searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private File selectedFile;

    public DataStreamsFrame(){
        setTitle("Data Streams Filter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top search field and buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextArea(1,20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);
        add(topPanel, BorderLayout.NORTH);

        // Center text areas
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);

        JScrollPane scrollPane1 = new JScrollPane(originalTextArea);
        JScrollPane scrollPane2 = new JScrollPane(filteredTextArea);

        JPanel textPanel = new JPanel(new GridLayout(1, 2));
        textPanel.add(scrollPane1);
        textPanel.add(scrollPane2);

        add(textPanel, BorderLayout.CENTER);

        //button functionality
        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));

        setVisible(true);

    }
    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                List<String> lines = Files.readAllLines(selectedFile.toPath());
                originalTextArea.setText(String.join("\n", lines));
                filteredTextArea.setText(""); // clear previous search
            } catch (Exception ex) {
                showError("Error loading file: " + ex.getMessage());
            }
        }
    }

    private void searchFile(ActionEvent e){
        if (selectedFile == null){
            showError("Please load a file first.");
            return;
        }
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()){
            showError("Please enter a search term.");
            return;
        }
        try (Stream<String> lines = Files.lines(selectedFile.toPath())) {
            List<String> filtered = lines
                    .filter(line -> line.contains(searchTerm))
                    .collect(Collectors.toList());

            filteredTextArea.setText(String.join("\n", filtered));
        } catch (Exception ex) {
            showError("Error reading file: " + ex.getMessage());

        }
    }
private void showError(String message){
        JOptionPane.showMessageDialog(this, message, "error", JOptionPane.ERROR_MESSAGE);
}
public static void main(String[] args){
        SwingUtilities.invokeLater(DataStreamsFrame::new);

}

}
