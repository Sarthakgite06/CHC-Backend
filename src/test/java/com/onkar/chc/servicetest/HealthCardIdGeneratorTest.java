package com.onkar.chc.servicetest;

import com.onkar.chc.repo.UserRepo;
import com.onkar.chc.service.HealthCardIdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class HealthCardIdGeneratorTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private HealthCardIdGenerator generator;

    @Test
    public void testGenerateHealthCardIdKnownDistrict() {
        Mockito.when(userRepo.countByDistrict("Pune")).thenReturn(0L);

        String id = generator.generateHealthCardId("Pune");
        Assertions.assertEquals("PUN00000001", id);
    }

    @Test
    public void testGenerateHealthCardIdSequential() {
        Mockito.when(userRepo.countByDistrict("Mumbai")).thenReturn(42L);

        String id = generator.generateHealthCardId("Mumbai");
        Assertions.assertEquals("MUM00000043", id);
    }

    @Test
    public void testGenerateHealthCardIdUnknownDistrict() {
        Mockito.when(userRepo.countByDistrict("UnknownCity")).thenReturn(5L);

        String id = generator.generateHealthCardId("UnknownCity");
        Assertions.assertEquals("UNK00000006", id);
    }

    @Test
    public void testGetSupportedDistricts() {
        Map<String, String> districts = HealthCardIdGenerator.getSupportedDistricts();
        Assertions.assertNotNull(districts);
        Assertions.assertTrue(districts.containsKey("Pune"));
        Assertions.assertEquals("PUN", districts.get("Pune"));
        Assertions.assertTrue(districts.containsKey("Delhi"));
        Assertions.assertEquals("DEL", districts.get("Delhi"));
    }
}
