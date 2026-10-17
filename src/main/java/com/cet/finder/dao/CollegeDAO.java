
package com.cet.finder.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollegeDAO extends BaseDAO {

    public List<String[]> searchColleges(
            String city, String course, String category, int capRound)
            throws SQLException {

        String sql = """
            SELECT DISTINCT
                cc.choice_code,
                c.institute_code,
                c.institute_name,
                ci.city_name,
                co.course_name
            FROM colleges c
            JOIN college_courses cc
                ON c.institute_code = cc.institute_code
            JOIN courses co
                ON cc.course_id = co.course_id
            LEFT JOIN cities ci
                ON c.city_id = ci.city_id
            WHERE (? = '' OR ci.city_name LIKE ?)
              AND (? = '' OR co.course_name LIKE ?)
              AND (
                    (? = '' AND ? = 0)
                    OR EXISTS (
                        SELECT 1
                        FROM cutoffs cu
                        WHERE cu.choice_code = cc.choice_code
                          AND (? = '' OR cu.category = ?)
                          AND (? = 0 OR cu.cap_round = ?)
                    )
              )
            ORDER BY c.institute_name, co.course_name
            """;

        List<String[]> results = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String selectedCity = city == null ? "" : city;
            String selectedCourse = course == null ? "" : course;
            String selectedCategory = category == null ? "" : category;

            ps.setString(1, selectedCity);
            ps.setString(2, "%" + selectedCity + "%");

            ps.setString(3, selectedCourse);
            ps.setString(4, "%" + selectedCourse + "%");

            ps.setString(5, selectedCategory);
            ps.setInt(6, capRound);

            ps.setString(7, selectedCategory);
            ps.setString(8, selectedCategory);

            ps.setInt(9, capRound);
            ps.setInt(10, capRound);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new String[]{
                            rs.getString("choice_code"),
                            rs.getString("institute_code"),
                            rs.getString("institute_name"),
                            rs.getString("city_name"),
                            rs.getString("course_name")
                    });
                }
            }
        }

        return results;
    }
}
