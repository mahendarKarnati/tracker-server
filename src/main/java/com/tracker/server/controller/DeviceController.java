package com.tracker.server.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.server.dto.DeviceRequest;
import com.tracker.server.entity.Device;
import com.tracker.server.entity.DeviceSession;
import com.tracker.server.entity.User;
import com.tracker.server.repository.DeviceRepository;
import com.tracker.server.repository.DeviceSessionRepository;
import com.tracker.server.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceSessionRepository deviceSessionRepository;

    @PostMapping("/register/{userId}")
    public Device registerDevice(
            @PathVariable Long userId,
            @RequestBody DeviceRequest request) {
    	
    	 Optional<Device> existing =
    	            deviceRepository.findByMacAddressAndUserId(
    	                    request.getMacAddress(),
    	                    userId);

    	    if (existing.isPresent()) {
    	        return existing.get();
    	    }

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                        new RuntimeException(
                                "User not found : " + userId));
System.out.println("ip address is: "+request.getLastIpAddress());
        Device device =
                Device.builder()
                        .macAddress(request.getMacAddress())
                        .machineName(request.getMachineName())
                        .osName(request.getOsName())
                        .lastIpAddress(request.getLastIpAddress())
                        .user(user)
                        .build();

        return deviceRepository.save(device);
    }
    @GetMapping("/sessions/{deviceId}")
    public List<DeviceSession> getSessions(
            @PathVariable Long deviceId) {
    	
    	System.out.println("device sessions request");

        return deviceSessionRepository.findByDeviceIdOrderByIdDesc(
                deviceId);
    }
    
    @PostMapping("/heartbeat/{deviceId}")
    public void heartbeat(@PathVariable Long deviceId) {

        System.out.println("HEARTBEAT RECEIVED : " + deviceId);

        Device device =
                deviceRepository.findById(deviceId)
                        .orElseThrow();

        device.setLastSeen(LocalDateTime.now());
        device.setOnline(true);

        deviceRepository.save(device);

        System.out.println("Heartbeat Saved");
    }
    
}