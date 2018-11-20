package io.ioforge.elements;

import lombok.Data;

@Data
public class SteelBar {
    private String alloyBatch;
    private Integer carbonPercent;
    private Integer ironPercent;
    private Integer otherMetalsPercent;
    private Integer otherNonMetalsPercent;
    private Integer strength;
}