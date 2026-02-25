package com.ompt.Ompt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ompt.Ompt.DTO.AssignMedicineRequestDTO;
import com.ompt.Ompt.DTO.DoctorRatingRequestDTO;
import com.ompt.Ompt.DTO.MedicineStatusUpdateDTO;
import com.ompt.Ompt.model.Delivery;
import com.ompt.Ompt.model.Doctor;
import com.ompt.Ompt.model.InventoryItem;
import com.ompt.Ompt.model.PatientRecord;
import com.ompt.Ompt.model.Pharmacy;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.DeliveryRepository;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.InventoryItemRepository;
import com.ompt.Ompt.repository.PatientRecordRepository;
import com.ompt.Ompt.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class PatientRecordService {

  private final PatientRecordRepository patientRecordRepository;
  private final UserRepository userRepository;
  private final InventoryItemRepository inventoryItemRepository;
  private final DeliveryRepository deliveryRepository;
  private final ObjectMapper objectMapper;
  private final DoctorRepository doctorRepository;

  @Transactional
  public void createForNewPatient(User patient, Long doctorId) {

    User assignedDoctor = null;

    if (doctorId != null) {
      Doctor doctor =
              doctorRepository.findById(doctorId)
                      .orElseThrow(() ->
                              new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

      assignedDoctor = doctor.getUser();
    }

    ObjectNode data = buildPatientTemplate(patient, assignedDoctor);

    PatientRecord record = new PatientRecord();
    record.setUser(patient);
    record.setAssignedDoctor(assignedDoctor);
    record.setDataJson(data.toString());

    patientRecordRepository.save(record);
  }
  public JsonNode getOrCreateRecord(User patient) {
    PatientRecord record =
        patientRecordRepository
            .findByUser(patient)
            .orElseGet(() -> createEntityForPatient(patient, null));
    ObjectNode data = parseObject(record.getDataJson());

    // 🔑 SYNC doctorAssignedId from DB
    if (record.getAssignedDoctor() != null) {
      data.put("doctorAssignedId", record.getAssignedDoctor().getId());
    } else {
      data.putNull("doctorAssignedId");
    }

    record.setDataJson(data.toString());
    patientRecordRepository.save(record);

    return data;
  }
  public List<JsonNode> listByHospital(Long hospitalId) {
    return patientRecordRepository.findByUser_Hospital_Id(hospitalId).stream()
        .map(record -> parse(record.getDataJson()))
        .toList();
  }

  public List<JsonNode> listByDoctor(User doctor) {
    return patientRecordRepository.findByAssignedDoctorId(doctor.getId()).stream()
            .map(record -> parse(record.getDataJson()))
            .toList();
  }

  public JsonNode updatePatientRecord(User patient, JsonNode updatedData) {
    PatientRecord record =
        patientRecordRepository
            .findByUser(patient)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

    ObjectNode normalized = updatedData.deepCopy();
    normalized.put("id", patient.getId());

    ObjectNode contact = normalized.with("contact");
    contact.put("email", patient.getEmail());

    if (record.getAssignedDoctor() != null) {
      normalized.put("doctorAssignedId", record.getAssignedDoctor().getId());
    } else {
      normalized.putNull("doctorAssignedId");
    }

    record.setDataJson(normalized.toString());
    patientRecordRepository.save(record);
    return normalized;
  }

  @Transactional
  public JsonNode assignMedicine(User doctor, Long patientId, AssignMedicineRequestDTO request) {
    if (doctor.getRole() != Role.DOCTOR) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only doctor can assign medicines");
    }

    if (request.getInventoryItemId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacy selection is required");
    }

    User patientUser =
        userRepository
            .findById(patientId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

    PatientRecord record =
        patientRecordRepository
            .findByUser(patientUser)
            .orElseGet(() -> createEntityForPatient(patientUser, doctor));

    InventoryItem inventoryItem =
        inventoryItemRepository
            .findById(request.getInventoryItemId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Selected pharmacy item not found"));

    if (inventoryItem.getQuantity() <= 0) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Selected pharmacy is out of stock");
    }

    if (!inventoryItem.getName().equalsIgnoreCase(request.getName())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Selected pharmacy does not carry this medicine");
    }

    Pharmacy pharmacy = inventoryItem.getPharmacy();

    inventoryItem.setQuantity(inventoryItem.getQuantity() - 1);
    inventoryItem.setLowStock(isLowStock(inventoryItem.getQuantity()));
    inventoryItemRepository.save(inventoryItem);

    Delivery delivery = new Delivery();
    delivery.setPharmacy(pharmacy);
    delivery.setPatientName(patientUser.getName());
    delivery.setMedicineName(request.getName());
    delivery.setStatus("pending");
    delivery.setPrescribedAt(LocalDateTime.now());
    Delivery savedDelivery = deliveryRepository.save(delivery);

    ObjectNode data = parseObject(record.getDataJson());
    ArrayNode medicines = data.withArray("medicines");

    ObjectNode medicine = objectMapper.createObjectNode();
    medicine.put("id", UUID.randomUUID().toString());
    medicine.put("name", request.getName());
    String dosage = request.getDosage();
    if (dosage == null || dosage.isBlank()) {
      dosage = inventoryItem.getDosage();
    }
    medicine.put("dosage", dosage == null ? "" : dosage);
    medicine.put("type", request.getType() == null ? "Tablet" : request.getType());
    medicine.put(
        "instructions",
        request.getInstructions() == null ? "As advised" : request.getInstructions());
    medicine.put("deliveryStatus", "pending");
    medicine.put("prescribedAt", LocalDateTime.now().toString());
    medicine.put("pharmacyId", pharmacy.getId());
    medicine.put("pharmacyName", pharmacy.getPharmacyName());
    medicine.put("pharmacyLocation", pharmacy.getLocation());
    medicine.put("inventoryItemId", inventoryItem.getId());
    medicine.put("deliveryId", savedDelivery.getId());
    if (inventoryItem.getPrice() != null) {
      medicine.put("price", inventoryItem.getPrice());
    } else {
      medicine.putNull("price");
    }

    ArrayNode schedule = medicine.putArray("schedule");
    for (String time : request.getScheduleTimes()) {
      ObjectNode slot = schedule.addObject();
      slot.put("time", time);
      slot.put("status", "pending");
      slot.putNull("takenAt");
    }

    medicines.add(medicine);

    data.put("doctorAssignedId", doctor.getId());
    record.setAssignedDoctor(doctor);
    record.setDataJson(data.toString());
    patientRecordRepository.save(record);

    return data;
  }

  public JsonNode updateMedicineStatus(User patient, MedicineStatusUpdateDTO request) {
    PatientRecord record =
        patientRecordRepository
            .findByUser(patient)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

    ObjectNode data = parseObject(record.getDataJson());
    ArrayNode medicines = data.withArray("medicines");
    String status = request.getStatus();

    for (JsonNode medNode : medicines) {
      if (!medNode.path("id").asText().equals(request.getMedicineId())) {
        continue;
      }
      ArrayNode schedule = ((ObjectNode) medNode).withArray("schedule");
      for (JsonNode slotNode : schedule) {
        if (!slotNode.path("time").asText().equals(request.getTime())) {
          continue;
        }
        ObjectNode slot = (ObjectNode) slotNode;
        slot.put("status", status);
        if (request.getReason() != null) {
          slot.put("reason", request.getReason());
        }
        if (!"missed".equalsIgnoreCase(status)) {
          slot.put("takenAt", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
          slot.putNull("takenAt");
        }
      }
    }

    record.setDataJson(data.toString());
    patientRecordRepository.save(record);
    return data;
  }

  @Transactional
  public JsonNode updateDeliveryStatus(User patient, String medicineId, String status) {
    PatientRecord record =
        patientRecordRepository
            .findByUser(patient)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

    ObjectNode data = parseObject(record.getDataJson());
    ArrayNode medicines = data.withArray("medicines");

    for (JsonNode medNode : medicines) {
      if (!medNode.path("id").asText().equals(medicineId)) {
        continue;
      }
      ObjectNode medObject = (ObjectNode) medNode;
      medObject.put("deliveryStatus", status);
      if ("delivered".equalsIgnoreCase(status)) {
        medObject.put("deliveredAt", LocalDateTime.now().toString());
      }

      long deliveryId = medObject.path("deliveryId").asLong(-1);
      if (deliveryId > 0) {
        deliveryRepository
            .findById(deliveryId)
            .ifPresent(
                delivery -> {
                  delivery.setStatus(status);
                  deliveryRepository.save(delivery);
                });
      }
    }

    record.setDataJson(data.toString());
    patientRecordRepository.save(record);
    return data;
  }

  @Transactional
  public void rateDoctor(User patient, DoctorRatingRequestDTO request) {

    PatientRecord record =
        patientRecordRepository
            .findByUser(patient)
            .orElseThrow(
                () ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient record not found"));

    User assignedDoctorUser = record.getAssignedDoctor();
    if (assignedDoctorUser == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No doctor assigned to this patient");
    }

    Doctor doctor =
        doctorRepository
            .findByUserId(assignedDoctorUser.getId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

    double total = doctor.getRatingTotal() + request.getRating();
    int count = doctor.getRatingCount() + 1;

    double average = Math.round((total / count) * 10.0) / 10.0;

    doctor.setRatingTotal(total);
    doctor.setRatingCount(count);
    doctor.setRating(average);

    doctorRepository.save(doctor);
  }

  private PatientRecord createEntityForPatient(User patient, User doctor) {
    PatientRecord record = new PatientRecord();
    record.setUser(patient);
    record.setAssignedDoctor(doctor);
    record.setDataJson(buildPatientTemplate(patient, doctor).toString());
    return patientRecordRepository.save(record);
  }

  private ObjectNode buildPatientTemplate(User patient, User doctor) {
    ObjectNode root = objectMapper.createObjectNode();
    root.put("id", patient.getId());
    root.put("name", patient.getName());
    root.put("age", 0);
    root.put("gender", "N/A");
    root.put("bloodGroup", "N/A");

    ObjectNode contact = root.putObject("contact");
    contact.put("phone", "");
    contact.put("email", patient.getEmail());
    contact.put("address", "");

    if (doctor != null) {
      root.put("doctorAssignedId", doctor.getId());
    } else {
      root.putNull("doctorAssignedId");
    }

    ObjectNode history = root.putObject("history");
    history.put("diagnosis", "New Patient");
    history.putArray("allergies");
    history.putArray("surgeries");
    ObjectNode vitals = history.putObject("vitals");
    vitals.put("bp", "");
    vitals.put("heartRate", "");
    vitals.put("weight", "");
    vitals.put("height", "");

    root.putArray("medicines");
    return root;
  }

  private boolean isLowStock(int quantity) {
    return quantity < 50;
  }

  private JsonNode parse(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (Exception ex) {
      return objectMapper.createObjectNode();
    }
  }

  private ObjectNode parseObject(String json) {
    JsonNode node = parse(json);
    if (node instanceof ObjectNode objectNode) {
      return objectNode;
    }
    return objectMapper.createObjectNode();
  }
}
