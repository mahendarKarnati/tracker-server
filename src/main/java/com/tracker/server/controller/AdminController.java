package com.tracker.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.server.dto.AdminLoginRequest;
import com.tracker.server.dto.LoginRequest;
import com.tracker.server.dto.LoginResponse;
import com.tracker.server.entity.ActiveWindowActivity;
import com.tracker.server.entity.Device;
import com.tracker.server.entity.IdleActivity;
import com.tracker.server.entity.ProcessActivity;
import com.tracker.server.entity.User;
import com.tracker.server.repository.ActiveWindowActivityRepository;
import com.tracker.server.repository.DeviceRepository;
import com.tracker.server.repository.IdleActivityRepository;
import com.tracker.server.repository.ProcessActivityRepository;
import com.tracker.server.repository.UserRepository;
import com.tracker.server.service.AdminService;
import com.tracker.server.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final ProcessActivityRepository processRepository;
    private final ActiveWindowActivityRepository windowRepository;
    private final IdleActivityRepository idleRepository;
    private final AuthService authService;
    
    
   

    @GetMapping("/users")
    public List<User> users() {
        return adminService.getUsers();
    }

    @GetMapping("/devices/{userId}")
    public List<Device> devices(
            @PathVariable Long userId) {

        return adminService.getDevices(userId);
    }

    @GetMapping("/processes/{deviceId}")
    public List<ProcessActivity> processes(
            @PathVariable Long deviceId) {

        return adminService.getProcesses(deviceId);
    }

    @GetMapping("/windows/{deviceId}")
    public List<ActiveWindowActivity> windows(
            @PathVariable Long deviceId) {

        return adminService.getWindows(deviceId);
    }

    @GetMapping("/idle/{deviceId}")
    public List<IdleActivity> idle(
            @PathVariable Long deviceId) {

        return adminService.getIdleActivities(deviceId);
    }
    
    @GetMapping("/stats")
    public Map<String, Long> stats() {

        Map<String, Long> stats =
                new HashMap<>();

        stats.put(
                "users",
                userRepository.count());

        stats.put(
                "devices",
                deviceRepository.count());

        stats.put(
                "processes",
                processRepository.count());

        stats.put(
                "windows",
                windowRepository.count());

        stats.put(
                "idle",
                idleRepository.count());

        return stats;
    }
    @GetMapping("/summary")
    public Map<String, Object> summary() {

        Map<String, Object> map =
                new HashMap<>();

        map.put(
                "totalUsers",
                userRepository.count());

        map.put(
                "totalDevices",
                deviceRepository.count());

        map.put(
                "totalProcesses",
                processRepository.count());

        map.put(
                "totalWindows",
                windowRepository.count());

        map.put(
                "totalIdle",
                idleRepository.count());

        return map;
    }
    @GetMapping("/recent-processes")
    public List<ProcessActivity> recentProcesses() {

        return processRepository
                .findTop10ByOrderByIdDesc();
    }
    @GetMapping("/export/processes/{deviceId}")
    public ResponseEntity<byte[]> exportProcesses(
            @PathVariable Long deviceId)
            throws Exception {

        return adminService.exportProcesses(
                deviceId);
    }
}