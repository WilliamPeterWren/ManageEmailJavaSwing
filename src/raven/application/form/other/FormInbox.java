package raven.application.form.other;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import raven.application.form.LoginForm;

public class FormInbox extends javax.swing.JPanel {

    public FormInbox() {
        initComponents();
        lb.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$h1.font");
    }

    // @SuppressWarnings("unchecked")
    private void initComponents() {

        String accessToken = LoginForm.accesstoken;
        String maxResults = "2";
        String labelIds = "INBOX";

        inboxTextArea = fetchData(accessToken, maxResults, labelIds);

        lb = new javax.swing.JLabel();

        JScrollPane scrollPane = new JScrollPane(inboxTextArea);

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Inbox");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE, 794, Short.MAX_VALUE)
                                .addContainerGap())
                        .addComponent(scrollPane));

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lb, javax.swing.GroupLayout.PREFERRED_SIZE, 50,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400,
                                        Short.MAX_VALUE)
                                .addContainerGap()));
    }

    public static byte[] decodeBase64(String base64String) {
        String correctedBase64 = base64String.replace('-', '+').replace('_', '/');
        int padding = correctedBase64.length() % 4;
        if (padding > 0) {
            correctedBase64 += "====".substring(padding);
        }

        return Base64.getDecoder().decode(correctedBase64);
    }

    public static String getMimeType(String filename) {
        // Get the file extension
        String extension = "";
        int index = filename.lastIndexOf('.');
        if (index > 0 && index < filename.length() - 1) {
            extension = filename.substring(index + 1).toLowerCase(); // Get extension and convert to lowercase
        }

        // Determine MIME type based on file extension
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "txt":
                return "text/plain";
            default:
                return "application/octet-stream"; // Default for unknown types
        }
    }

    public static void createFileFromBytes(byte[] bytes, String filePath) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
            fos.flush();
        }
    }

    @SuppressWarnings("finally")
    public static JTextArea fetchData(String accessToken, String maxResults, String labelIds) {

        inboxTextArea = new JTextArea(10, 50);
        inboxTextArea.setLineWrap(true);
        inboxTextArea.setWrapStyleWord(true);
        inboxTextArea.setEditable(false);

        try {

            String urlStr = "http://localhost:3000/api/gmail?accessToken=" + accessToken
                    + "&maxResults=" + maxResults + "&labelIds=" + labelIds;

            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.toString());
                JsonNode attachments = jsonResponse.get("attachments");

                inboxTextArea.setText(attachments.toString());

                if (attachments != null && attachments.isArray()) {
                    StringBuilder displayText = new StringBuilder();
                    for (JsonNode attachment : attachments) {
                        JsonNode filenameNode = attachment.get("filename");
                        JsonNode dataNode = attachment.get("data");
                        JsonNode dataBase64 = dataNode.get("data");
                        if (dataBase64 != null) {
                            String fileName = filenameNode.asText();
                            displayText.append("File Name: ").append(fileName).append("\n");
                            try {
                                String base64String = dataBase64.asText();
                                String filename = filenameNode.asText();
                                byte[] bytes = decodeBase64(base64String);
                                // String mimeType = getMimeType(filename);
                                // System.out.println("MIME Type: " + mimeType);
                                String desktopPath = System.getProperty("user.home") + "/Desktop/" + filename;

                                createFileFromBytes(bytes, desktopPath);

                                // System.out.println("File created successfully at: " + desktopPath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            displayText.append("File Name: Not available\n");
                        }
                    }
                    inboxTextArea.setText(displayText.toString());
                } else {
                    inboxTextArea.setText("No messages found.");
                }

            } else {
                // JOptionPane.showMessageDialog(this,
                // "Failed to fetch inbox data. Response code: " + responseCode);
                inboxTextArea.setText("Failed to fetch inbox data. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            inboxTextArea.setText("Error fetching data: " + e.getMessage());
        } finally {
            return inboxTextArea;
        }
    }

    private javax.swing.JLabel lb;
    private static JTextArea inboxTextArea;

}
