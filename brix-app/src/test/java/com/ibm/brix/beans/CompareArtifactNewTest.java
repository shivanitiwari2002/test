package com.ibm.brix.beans;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ibm.brix.Constants;
import com.ibm.brix.beans.CompareArtifactsNew;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.model.GenericArtifact;

public class CompareArtifactNewTest {

    private CompareArtifactsNew compareArtifactsNew;
    private Exchange mockExchange;
    private Message mockMessage;

    @BeforeEach
    public void setUp() {
        compareArtifactsNew = new CompareArtifactsNew();
        mockExchange = mock(Exchange.class);
        mockMessage = mock(Message.class);
        when(mockExchange.getIn()).thenReturn(mockMessage);
    }

    @Test
    public void testCompareArtifactsDifferencesExist() {
        // Setup mock source and target artifacts
        GenericArtifact sourceArtifact = new GenericArtifact();
        GenericArtifact targetArtifact = new GenericArtifact();

        Map<String, AttributeProperties> sourceAttributes = new HashMap<>();
        Map<String, AttributeProperties> targetAttributes = new HashMap<>();

        AttributeProperties attr1 = new AttributeProperties("value1", null);
        AttributeProperties attr2 = new AttributeProperties("value2", null);

        sourceAttributes.put("key1", attr1);
        sourceAttributes.put("key2", attr2);
        targetAttributes.put("key1", attr1); // identical
        // key2 will differ

        sourceArtifact.setAttributes(sourceAttributes);
        targetArtifact.setAttributes(targetAttributes);

        // Mock headers
        when(mockMessage.getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT)).thenReturn(sourceArtifact);
        when(mockMessage.getHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT)).thenReturn(targetArtifact);

        // Run method
        GenericArtifact delta = compareArtifactsNew.compareArtifacts(mockExchange);

        // Assert that one attribute (key2) is added to the delta
        assertEquals(1, delta.getAttributes().size());
        assertTrue(delta.getAttributes().containsKey("key2"));

        // Assert header is set to true since we had a delta
        verify(mockMessage).setHeader(Constants.BRIX_DELTAS_EXIST, true);
    }

    @Test
    public void testCompareArtifactsNoDifferences() {
        GenericArtifact sourceArtifact = new GenericArtifact();
        GenericArtifact targetArtifact = new GenericArtifact();

        AttributeProperties attr = new AttributeProperties("sameValue", null);

        Map<String, AttributeProperties> attributes = new HashMap<>();
        attributes.put("key1", attr);

        sourceArtifact.setAttributes(attributes);
        targetArtifact.setAttributes(attributes);

        when(mockMessage.getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT)).thenReturn(sourceArtifact);
        when(mockMessage.getHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT)).thenReturn(targetArtifact);

        GenericArtifact delta = compareArtifactsNew.compareArtifacts(mockExchange);

        assertTrue(delta.getAttributes().isEmpty());
        verify(mockMessage).setHeader(Constants.BRIX_DELTAS_EXIST, false);
    }
    
    @Test
    public void testCompareArtifactsSourceValueIsNull() {
        GenericArtifact sourceArtifact = new GenericArtifact();
        GenericArtifact targetArtifact = new GenericArtifact();

        Map<String, AttributeProperties> sourceAttributes = new HashMap<>();
        Map<String, AttributeProperties> targetAttributes = new HashMap<>();

        // Source has null value for key1
        sourceAttributes.put("key1", null);

        // Target has an actual value
        AttributeProperties targetAttr = new AttributeProperties("value1", null);
        targetAttributes.put("key1", targetAttr);

        sourceArtifact.setAttributes(sourceAttributes);
        targetArtifact.setAttributes(targetAttributes);

        when(mockMessage.getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT)).thenReturn(sourceArtifact);
        when(mockMessage.getHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT)).thenReturn(targetArtifact);

        GenericArtifact delta = compareArtifactsNew.compareArtifacts(mockExchange);

        assertEquals(1, delta.getAttributes().size());
        assertTrue(delta.getAttributes().containsKey("key1"));
        verify(mockMessage).setHeader(Constants.BRIX_DELTAS_EXIST, true);
    }
}
