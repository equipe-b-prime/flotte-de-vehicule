package com.flottevehicule.flottevehicule.dto;

public class VehiculeStatsResponseDto implements ResponseDto {
    private Double coutMoyen;
    private Integer kilometrageTotal;
    private Integer nbEntretiens;

    public VehiculeStatsResponseDto() {}

    public Double getCoutMoyen() { return coutMoyen; }
    public void setCoutMoyen(Double coutMoyen) { this.coutMoyen = coutMoyen; }

    public Integer getKilometrageTotal() { return kilometrageTotal; }
    public void setKilometrageTotal(Integer kilometrageTotal) { this.kilometrageTotal = kilometrageTotal; }

    public Integer getNbEntretiens() { return nbEntretiens; }
    public void setNbEntretiens(Integer nbEntretiens) { this.nbEntretiens = nbEntretiens; }
}