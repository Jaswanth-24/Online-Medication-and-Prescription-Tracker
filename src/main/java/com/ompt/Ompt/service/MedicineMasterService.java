package com.ompt.Ompt.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ompt.Ompt.DTO.MedicineMasterDTO;
import com.ompt.Ompt.model.MedicineMaster;
import com.ompt.Ompt.repository.MedicineMasterRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MedicineMasterService {

    private final MedicineMasterRepository medicineMasterRepository;
    private final ObjectMapper objectMapper;

  /* =========================
     PUBLIC CATALOG
     ========================= */

    @Transactional(readOnly = true)
    public List<MedicineMasterDTO> listAll() {
        return medicineMasterRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

  /* =========================
     INTERNAL MAPPER
     ========================= */

    private MedicineMasterDTO toDto(MedicineMaster entity) {
        return new MedicineMasterDTO(
                entity.getId(),
                entity.getName(),
                entity.getStrength(),
                entity.getType(),
                parseSchedule(entity.getDefaultScheduleJson()));
    }

    private List<String> parseSchedule(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}