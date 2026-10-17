
package com.cet.finder.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CutoffPredictorDAO extends BaseDAO {

    public List<String[]> predictColleges(
            double studentPercentile,
            String category,
            String course,
            int capRound) throws SQLException {

        String sql = """
            SELECT
                c.institute_name,
                co.course_name,
                cu.year,
                cu.cap_round,
                cu.category,
                cu.percentile
            FROM cutoffs cu
            JOIN college_courses cc
                ON cu.choice_code = cc.choice_code
            JOIN colleges c
                ON cc.institute_code = c.institute_code
            JOIN courses co
                ON cc.course_id = co.course_id
            WHERE cu.percentile IS NOT NULL
              AND (? = '' OR cu.category = ?)
              AND (? = '' OR co.course_name LIKE ?)
              AND (? = 0 OR cu.cap_round = ?)
            ORDER BY cu.year DESC, cu.percentile ASC
            LIMIT 500
            """;

        List<String[]> results = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String selectedCategory =
                    category == null ? "" : category;

            String selectedCourse =
                    course == null ? "" : course;

            ps.setString(1, selectedCategory);
            ps.setString(2, selectedCategory);

            ps.setString(3, selectedCourse);
            ps.setString(4, "%" + selectedCourse + "%");

            ps.setInt(5, capRound);
            ps.setInt(6, capRound);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    double cutoff = rs.getDouble("percentile");

                    String status;

                    if (studentPercentile >= cutoff + 2.0) {
                        status = "Likely";
                    } else if (studentPercentile >= cutoff) {
                        status = "Borderline";
                    } else {
                        status = "Reach";
                    }

                    results.add(new String[]{
                            rs.getString("institute_name"),
                            rs.getString("course_name"),
                            String.valueOf(rs.getInt("year")),
                            String.valueOf(rs.getInt("cap_round")),
                            rs.getString("category"),
                            String.format("%.4f", cutoff),
                            status
                    });
                }
            }
        }

        return results;
    }
}
