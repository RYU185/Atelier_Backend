package com.dw.artgallery.DTO;

public record ReserveAvailabilityDTO(
        int capacity,
        int reservedCount,
        boolean full
) {}