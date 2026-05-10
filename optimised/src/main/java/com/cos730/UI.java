package com.cos730;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UI extends JFrame {

    private final JTextField titleField;
    private final JTextField researcherField;
    private final JTextField keywordsField;
    private final JTextArea abstractArea;
    private final JTextField submissionTitleField;
    private final JTextField scoreField;
    private final JComboBox<String> reviewerComboBox;
    private final SubmissionController submissionController;

    public UI() {
        super("COS 730 Assignment 2 - Research Submission");
        this.submissionController = new SubmissionController();

        titleField = new JTextField(25);
        researcherField = new JTextField(25);
        keywordsField = new JTextField(25);
        abstractArea = new JTextArea(8, 25);
        submissionTitleField = new JTextField(25);
        scoreField = new JTextField(25);
        reviewerComboBox = new JComboBox<>();
        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        refreshReviewerOptions();
        add(buildMainPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildMainPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        mainPanel.add(buildSubmissionPanel());
        mainPanel.add(buildReviewerPanel());
        return mainPanel;
    }

    private JPanel buildSubmissionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Research Title:"), gbc);

        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Researcher Name:"), gbc);

        gbc.gridx = 1;
        panel.add(researcherField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Keywords:"), gbc);

        gbc.gridx = 1;
        panel.add(keywordsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Abstract:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(abstractArea), gbc);

        JButton submitButton = new JButton("Submit Research");
        submitButton.addActionListener(e -> submitResearchOutput());

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(submitButton, gbc);

        return panel;
    }

    private JPanel buildReviewerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Submission Title:"), gbc);

        gbc.gridx = 1;
        panel.add(submissionTitleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Available Reviewer:"), gbc);

        gbc.gridx = 1;
        panel.add(reviewerComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Score (0-100):"), gbc);

        gbc.gridx = 1;
        panel.add(scoreField, gbc);

        JButton submitScoreButton = new JButton("Submit Review Score");
        submitScoreButton.addActionListener(e -> submitReviewerScore());

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(submitScoreButton, gbc);

        return panel;
    }

    public void submitResearchOutput() {
        String title = titleField.getText().trim();
        String researcherName = researcherField.getText().trim();
        String keywords = keywordsField.getText().trim();
        String abstractText = abstractArea.getText().trim();

        String errorMessage = submissionController.submit(title, researcherName, keywords, abstractText);
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Submission Error",
                JOptionPane.ERROR_MESSAGE
            );
        } else {
            refreshReviewerOptions();
            JOptionPane.showMessageDialog(
                this,
                "Research submission successful!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public void submitReviewerScore() {
        String submissionTitle = submissionTitleField.getText().trim();
        String reviewerName = (String) reviewerComboBox.getSelectedItem();
        String scoreText = scoreField.getText().trim();

        if (submissionTitle.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter the research submission title for this score.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (reviewerName == null || reviewerName.isBlank()) {
            JOptionPane.showMessageDialog(
                this,
                "No available reviewer is selected.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                this,
                "Score must be a whole number.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (score < 0 || score > 100) {
            JOptionPane.showMessageDialog(
                this,
                "Score must be between 0 and 100.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String errorMessage = submissionController.submitReviewerScore(reviewerName, score, submissionTitle);
        if (errorMessage != null) {
            JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Submission Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        refreshReviewerOptions();

        JOptionPane.showMessageDialog(
            this,
            "Score submitted successfully for " + submissionTitle + ".",
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void refreshReviewerOptions() {
        reviewerComboBox.removeAllItems();
        List<Reviewer> reviewers = submissionController.getAvailableReviewers();

        if (reviewers == null) {
            return;
        }

        for (Reviewer reviewer : reviewers) {
            reviewerComboBox.addItem(reviewer.getName());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UI().setVisible(true));
    }
}
