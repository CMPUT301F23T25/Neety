package com.team25.neety;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.team25.neety.Item;


public class ItemTest {
    private Item item;
    UUID id = UUID.randomUUID();
    Date purchaseDate = new Date();
    String make = "Apple";
    String model = "iPhone 12";
    String description = "Smartphone";
    String serial = "123456789";
    float estimatedValue = 999.99f;
    String comments = "Good condition";

    @Before
    public void setUp() {


        item = new Item(id, purchaseDate, make, model, description, serial, estimatedValue, comments);
    }

    @Test
    public void testGetPurchaseDate() {
        assertEquals(purchaseDate, item.getPurchaseDate());
    }

    @Test
    public void testGetMake() {
        assertEquals(make, item.getMake());
    }

    @Test
    public void testGetModel() {
        assertEquals(model, item.getModel());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, item.getDescription());
    }

    @Test
    public void testGetSerial() {
        String expectedSerial = "123456789";
        assertEquals(expectedSerial, item.getSerial());
    }

    @Test
    public void testGetEstimatedValue() {
        assertEquals(estimatedValue, item.getEstimatedValue(), 0.01);
    }

    @Test
    public void testGetComments() {
        assertEquals(comments, item.getComments());
    }

    @Test
    public void testSetSelected() {
        item.setSelected(true);
        assertTrue(item.isSelected());
    }

    @Test
    public void testEquals() {
        UUID id = item.getId();
        Item sameItem = new Item(id, new Date(), "Apple", "iPhone 12", "Smartphone", "123456789", 999.99f, "Good condition");
        assertTrue(item.equals(sameItem));

        Item differentItem = new Item(UUID.randomUUID(), new Date(), "Samsung", "Galaxy S21", "Smartphone", "987654321", 799.99f, "Used condition");
        assertFalse(item.equals(differentItem));
    }

    @Test
    public void testGetId() {
        assertEquals(id, item.getId());
    }
}
