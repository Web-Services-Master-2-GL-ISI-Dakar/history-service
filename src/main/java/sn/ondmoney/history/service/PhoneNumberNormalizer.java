package sn.ondmoney.history.service;

import org.springframework.stereotype.Service;

@Service
public class PhoneNumberNormalizer {

    public String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }

        // Nettoyage
        String cleaned = phone.trim()
            .replaceAll("\\s+", "")
            .replaceAll("[.-]", "");

        // Convertit +221 en 00221
        if (cleaned.startsWith("+221")) {
            return "00221" + cleaned.substring(4);
        }

        // Si commence par 221 sans 00, ajoute 00
        if (cleaned.startsWith("221") && !cleaned.startsWith("00221")) {
            return "00" + cleaned;
        }

        // Si commence par 0 (numéro local), convertit en 00221
        if (cleaned.startsWith("0") && cleaned.length() >= 10) {
            // Ex: 0771234567 -> 00221771234567
            return "00221" + cleaned.substring(1);
        }

        return cleaned;
    }
}
