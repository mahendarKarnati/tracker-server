package com.tracker.server.service;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

import lombok.RequiredArgsConstructor;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final ProcessActivityRepository processRepository;
    private final ActiveWindowActivityRepository windowRepository;
    private final IdleActivityRepository idleRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<Device> getDevices(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    public List<ProcessActivity> getProcesses(Long deviceId) {
        return processRepository.findByDeviceId(deviceId);
    }

    public List<ActiveWindowActivity> getWindows(Long deviceId) {
        return windowRepository.findByDeviceIdOrderByIdDesc(deviceId);
    }

    public List<IdleActivity> getIdleActivities(Long deviceId) {
        return idleRepository.findByDeviceIdOrderByIdDesc(deviceId);
    }
    
    public ResponseEntity<byte[]> exportProcesses(
            Long deviceId)
            throws Exception {

        List<ProcessActivity> processes =
                processRepository.findByDeviceId(
                        deviceId);

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet(
                        "Processes");

        Row header =
                sheet.createRow(0);

        header.createCell(0)
                .setCellValue("ID");

        header.createCell(1)
                .setCellValue("Process");

        header.createCell(2)
                .setCellValue("Status");

        header.createCell(3)
                .setCellValue("Start");

        header.createCell(4)
                .setCellValue("End");

        int rowNum = 1;

        for (ProcessActivity p : processes) {

            Row row =
                    sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(p.getId());

            row.createCell(1)
                    .setCellValue(
                            p.getProcessName());

            row.createCell(2)
                    .setCellValue(
                            p.getStatus());

            row.createCell(3)
                    .setCellValue(
                            String.valueOf(
                                    p.getStartTime()));

            row.createCell(4)
                    .setCellValue(
                            String.valueOf(
                                    p.getEndTime()));
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=processes.xlsx")
                .body(
                        out.toByteArray());
    }
    
}