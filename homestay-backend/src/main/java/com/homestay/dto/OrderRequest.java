package com.homestay.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OrderRequest {
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;
    private String guestName;
    private String guestPhone;
    private String remark;
    
    // Manual getters/setters if Lombok fails
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
