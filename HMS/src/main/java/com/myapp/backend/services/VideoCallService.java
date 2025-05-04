package com.myapp.backend.services;

import com.myapp.backend.model.Doctor;
import com.myapp.backend.model.Patient;
import com.myapp.backend.model.VideoCallSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCallService {
    private static Map<String, VideoCallSession> activeSessions = new HashMap<>();
    
    // Start a new video call session between doctor and patient
    public static String initiateCall(Doctor doctor, Patient patient) {
        VideoCallSession session = new VideoCallSession(doctor.getId(), patient.getId());
        activeSessions.put(session.getSessionId(), session);
        System.out.println("Video call initiated between Dr. " + doctor.getName() + 
                           " and patient " + patient.getName());
        return session.getSessionId();
    }
    
    // End an active video call session
    public static boolean endCall(String sessionId) {
        if (activeSessions.containsKey(sessionId)) {
            VideoCallSession session = activeSessions.remove(sessionId);
            System.out.println("Video call ended: Session " + sessionId);
            return true;
        }
        System.out.println("Error: Session not found");
        return false;
    }
    
    // Get all active sessions for a specific doctor
    public static List<VideoCallSession> getDoctorActiveSessions(String doctorId) {
        List<VideoCallSession> doctorSessions = new ArrayList<>();
        for (VideoCallSession session : activeSessions.values()) {
            if (session.getDoctorId().equals(doctorId)) {
                doctorSessions.add(session);
            }
        }
        return doctorSessions;
    }
    
    // Get all active sessions for a specific patient
    public static List<VideoCallSession> getPatientActiveSessions(String patientId) {
        List<VideoCallSession> patientSessions = new ArrayList<>();
        for (VideoCallSession session : activeSessions.values()) {
            if (session.getPatientId().equals(patientId)) {
                patientSessions.add(session);
            }
        }
        return patientSessions;
    }
    
    // Check if a session is currently active
    public static boolean isSessionActive(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }
}