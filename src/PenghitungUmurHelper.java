
import java.time.LocalDate;
import java.time.Period;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

public class PenghitungUmurHelper {

    // Menghitung umur (tahun, bulan, hari)
    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period selisih = Period.between(lahir, sekarang);
        return selisih.getYears() + " tahun, " + selisih.getMonths() + " bulan, " + selisih.getDays() + " hari";
    }

    // Menentukan hari ulang tahun berikutnya
    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahun = lahir.withYear(sekarang.getYear());
        if (!ulangTahun.isAfter(sekarang)) {
            ulangTahun = ulangTahun.plusYears(1);
        }
        return ulangTahun;
    }

    // Mengubah nama hari ke Bahasa Indonesia
    public String getDayOfWeekInIndonesian(LocalDate tanggal) {
        switch (tanggal.getDayOfWeek()) {
            case MONDAY:
                return "Senin";
            case TUESDAY:
                return "Selasa";
            case WEDNESDAY:
                return "Rabu";
            case THURSDAY:
                return "Kamis";
            case FRIDAY:
                return "Jumat";
            case SATURDAY:
                return "Sabtu";
            case SUNDAY:
                return "Minggu";
            default:
                return "";
        }
    }
}

