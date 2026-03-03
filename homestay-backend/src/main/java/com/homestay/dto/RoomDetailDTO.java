package com.homestay.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RoomDetailDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private Integer capacity;
    private List<String> images;
    private List<String> facilities;
    private String address;
    private String city;
    private Integer status;
    private Long hostId;
    
    // Host Info
    private String hostName;
    private String hostAvatar;
    private String hostPhone;

    // Manual Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getHostId() { return hostId; }
    public void setHostId(Long hostId) { this.hostId = hostId; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public String getHostAvatar() { return hostAvatar; }
    public void setHostAvatar(String hostAvatar) { this.hostAvatar = hostAvatar; }
    public String getHostPhone() { return hostPhone; }
    public void setHostPhone(String hostPhone) { this.hostPhone = hostPhone; }
}
