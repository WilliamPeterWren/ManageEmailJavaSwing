package raven.application.form.other;

import com.formdev.flatlaf.FlatClientProperties;

import raven.application.form.LoginForm;
import javax.swing.*;

public class FormRead extends javax.swing.JPanel {
        private static JTextArea inboxTextArea;

        public FormRead() {
                initComponents();
                lb.putClientProperty(FlatClientProperties.STYLE, ""
                                + "font:$h1.font");
        }

        // @SuppressWarnings("unchecked")
        private void initComponents() {

                String accessToken = LoginForm.accesstoken;
                String maxResults = "2";
                String labelIds = "SENT";

                inboxTextArea = FormInbox.fetchData(accessToken, maxResults, labelIds);

                lb = new javax.swing.JLabel();
                JScrollPane scrollPane = new JScrollPane(inboxTextArea);

                lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                lb.setText("Sent");

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                794, Short.MAX_VALUE)
                                                                .addContainerGap())
                                                .addComponent(scrollPane));

                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(lb,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                50,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(scrollPane,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                400,
                                                                                Short.MAX_VALUE)
                                                                .addContainerGap()));
        }

        private javax.swing.JLabel lb;

}
