package com.cos730;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    
    private static final String URL = "jdbc:sqlite:cos730_assignment2.db";
    private final Connection conn;

    public Database(){
        try {
            conn = DriverManager.getConnection(URL);

            if (conn != null) {
                System.out.println("Connected to the database");
                initializeDatabase();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to connect to the database.", e);
        }
    }

    private void initializeDatabase() throws SQLException {
        createTables();
        removeDuplicateReviewers();
        removeDuplicateEvaluationScores();
        createIndexes();
        seedReviewersIfMissing();
    }

    private void createTables() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS submissions (id integer PRIMARY KEY, title text NOT NULL, researcher text NOT NULL, keywords text NOT NULL, abstract text NOT NULL);";
        conn.createStatement().execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS reviewers (id integer PRIMARY KEY, name text NOT NULL, available boolean NOT NULL, workload integer NOT NULL);";
        conn.createStatement().execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS evaluation_scores (id integer PRIMARY KEY, reviewer_name text NOT NULL, score integer NOT NULL, submission_id integer NOT NULL REFERENCES submissions(id));";
        conn.createStatement().execute(sql);

        sql = "CREATE TABLE IF NOT EXISTS reviewer_assignments (id integer PRIMARY KEY, reviewer_name text NOT NULL, submission_id integer NOT NULL REFERENCES submissions(id));";
        conn.createStatement().execute(sql);
    }

    private void removeDuplicateReviewers() throws SQLException {
        String sql = "DELETE FROM reviewers WHERE id NOT IN (SELECT MIN(id) FROM reviewers GROUP BY name)";
        conn.createStatement().executeUpdate(sql);
    }

    private void removeDuplicateEvaluationScores() throws SQLException {
        String sql = "DELETE FROM evaluation_scores WHERE id NOT IN (SELECT MIN(id) FROM evaluation_scores GROUP BY reviewer_name, submission_id)";
        conn.createStatement().executeUpdate(sql);
    }

    private void createIndexes() throws SQLException {
        conn.createStatement().execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_reviewers_name ON reviewers(name)");
        conn.createStatement().execute(
            "CREATE UNIQUE INDEX IF NOT EXISTS idx_evaluation_scores_reviewer_submission ON evaluation_scores(reviewer_name, submission_id)"
        );
        conn.createStatement().execute(
            "CREATE UNIQUE INDEX IF NOT EXISTS idx_reviewer_assignments_reviewer_submission ON reviewer_assignments(reviewer_name, submission_id)"
        );
    }

    private void seedReviewersIfMissing() throws SQLException {
        insertReviewerIfMissing("Alice", false, 0);
        insertReviewerIfMissing("Bob", true, 0);
        insertReviewerIfMissing("Charlie", false, 0);
        insertReviewerIfMissing("David", true, 0);
        insertReviewerIfMissing("Eve", true, 0);
    }

    private void insertReviewerIfMissing(String name, boolean available, int workload) throws SQLException {
        String sql = "INSERT OR IGNORE INTO reviewers(name, available, workload) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setBoolean(2, available);
            pstmt.setInt(3, workload);
            pstmt.executeUpdate();
        }
    }

    public void saveSubmission(String title, String researcher, String keywords, String abstractText) {
        String sql = "INSERT INTO submissions(title, researcher, keywords, abstract) VALUES(?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, researcher);
            pstmt.setString(3, keywords);
            pstmt.setString(4, abstractText);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error saving submission.", e);
        }
    }

    public List<Reviewer> fetchReviewers(){
       String sql = "SELECT * FROM reviewers ORDER BY workload ASC, name ASC";
       List<Reviewer> reviewers = new ArrayList<>();

       try (PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                boolean isAvailable = rs.getBoolean("available");
                int workload = rs.getInt("workload");

                reviewers.add(new Reviewer(name, isAvailable, workload));
            }
       } catch (SQLException e) {
            throw new IllegalStateException("Error fetching reviewers.", e);
       }

       return reviewers;
    }

    public void incrementReviewerWorkload(String reviewerName) {
        String sql = "UPDATE reviewers SET workload = workload + 1 WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewerName);
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new IllegalArgumentException("Reviewer not found: " + reviewerName);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error updating reviewer workload.", e);
        }
    }

    public void saveReviewerAssignment(String reviewerName, String submissionTitle) {
        int submissionId = findSubmissionIdByTitle(submissionTitle);
        if (submissionId == -1) {
            throw new IllegalArgumentException("No submission found with title: " + submissionTitle);
        }

        String sql = "INSERT OR IGNORE INTO reviewer_assignments(reviewer_name, submission_id) VALUES(?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewerName);
            pstmt.setInt(2, submissionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error saving reviewer assignment.", e);
        }
    }

    public boolean submissionExists(String submissionTitle) {
        return findSubmissionIdByTitle(submissionTitle) != -1;
    }

    public boolean hasReviewerSubmittedScore(String reviewerName, String submissionTitle) {
        int submissionId = findSubmissionIdByTitle(submissionTitle);
        if (submissionId == -1) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM evaluation_scores WHERE reviewer_name = ? AND submission_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewerName);
            pstmt.setInt(2, submissionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking for existing reviewer scores.", e);
        }
    }

    public boolean isReviewerAssignedToSubmission(String reviewerName, String submissionTitle) {
        int submissionId = findSubmissionIdByTitle(submissionTitle);
        if (submissionId == -1) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM reviewer_assignments WHERE reviewer_name = ? AND submission_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewerName);
            pstmt.setInt(2, submissionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error checking reviewer assignment.", e);
        }
    }

    public void saveEvaluationScore(String reviewerName, int score, String submissionTitle) {
        int submissionId = findSubmissionIdByTitle(submissionTitle);
        if (submissionId == -1) {
            throw new IllegalArgumentException("No submission found with title: " + submissionTitle);
        }

        String sql = "INSERT INTO evaluation_scores(reviewer_name, score, submission_id) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reviewerName);
            pstmt.setInt(2, score);
            pstmt.setInt(3, submissionId);  
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error saving evaluation score.", e);
        }
    }

    public List<Integer> fetchScoresForSubmission(String submissionTitle) {
        String sql = "SELECT es.score FROM evaluation_scores es JOIN submissions s ON s.id = es.submission_id WHERE s.title = ? ORDER BY es.id ASC";
        List<Integer> scores = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, submissionTitle);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(rs.getInt("score"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error fetching evaluation scores.", e);
        }

        return scores;
    }

    private int findSubmissionIdByTitle(String submissionTitle) {
        String sql = "SELECT id FROM submissions WHERE title = ? ORDER BY id DESC LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, submissionTitle);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error fetching submission ID.", e);
        }

        return -1;
    }
}
