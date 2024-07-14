package com.jtc.dao;

import com.jtc.model.Schedule;
import com.jtc.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {
	public boolean addSchedule(Schedule schedule) {
	    String sql = "INSERT INTO schedules (user_id, start_time, end_time, title, description) VALUES (?, ?, ?, ?, ?)";
	    try (Connection conn = DatabaseUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, schedule.getUserId());
	        pstmt.setTimestamp(2, new Timestamp(schedule.getStartTime().getTime()));
	        pstmt.setTimestamp(3, new Timestamp(schedule.getEndTime().getTime()));
	        pstmt.setString(4, schedule.getTitle());
	        pstmt.setString(5, schedule.getDescription());
	        
	        System.out.println("Executing SQL: " + sql);
	        System.out.println("With parameters: " + schedule);
	        
	        int affectedRows = pstmt.executeUpdate();
	        System.out.println("Affected rows: " + affectedRows);
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("SQL State: " + e.getSQLState());
	        System.out.println("Error Code: " + e.getErrorCode());
	        System.out.println("Message: " + e.getMessage());
	        return false;
	    }
	}

    public boolean deleteSchedule(int scheduleId, int userId) {
        String sql = "DELETE FROM schedules WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, scheduleId);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Schedule> querySchedules(int userId, java.sql.Date startDate, java.sql.Date endDate) {
        String sql = "SELECT * FROM schedules WHERE user_id = ? AND start_time >= ? AND end_time <= ? ORDER BY start_time";
        List<Schedule> schedules = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Schedule schedule = new Schedule();
                schedule.setId(rs.getInt("id"));
                schedule.setUserId(rs.getInt("user_id"));
                schedule.setStartTime(rs.getTimestamp("start_time"));
                schedule.setEndTime(rs.getTimestamp("end_time"));
                schedule.setTitle(rs.getString("title"));
                schedule.setDescription(rs.getString("description"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    public boolean clearSchedules(int userId) {
        String sql = "DELETE FROM schedules WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasConflict(Schedule newSchedule) {
        String sql = "SELECT COUNT(*) FROM schedules WHERE user_id = ? AND ((start_time <= ? AND end_time >= ?) OR (start_time <= ? AND end_time >= ?) OR (start_time >= ? AND end_time <= ?))";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newSchedule.getUserId());
            pstmt.setTimestamp(2, new Timestamp(newSchedule.getStartTime().getTime()));
            pstmt.setTimestamp(3, new Timestamp(newSchedule.getStartTime().getTime()));
            pstmt.setTimestamp(4, new Timestamp(newSchedule.getEndTime().getTime()));
            pstmt.setTimestamp(5, new Timestamp(newSchedule.getEndTime().getTime()));
            pstmt.setTimestamp(6, new Timestamp(newSchedule.getStartTime().getTime()));
            pstmt.setTimestamp(7, new Timestamp(newSchedule.getEndTime().getTime()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
