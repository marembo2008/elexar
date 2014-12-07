package com.anosym.elexar.util;

import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author marembo
 */
public class VotingResultTest {

    /**
     * Test of toString method, of class VotingResult.
     */
    @Test
    public void testToString() {
        try {
            System.out.println("toString");
            VotingResult instance = new VotingResult();
            instance.setAgent("0786456789");
            instance.setDisputed(56);
            instance.setRejected(4);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("R", 7889);
            map.put("U", 678);
            map.put("P", 67);
            instance.setResult(map);
            VObjectMarshaller<VotingResult> v = new VObjectMarshaller<VotingResult>(VotingResult.class);
            VDocument doc = v.marshall(instance);
            System.out.println(doc.toXmlString());
            VotingResult vr = v.unmarshall(doc);
            String result = v.doMarshall(instance);
            String expectedResult = v.doMarshall(vr);
            assertEquals(expectedResult, result);
            System.out.println(result);
            System.out.println(expectedResult);
        } catch (VXMLMemberNotFoundException ex) {
            Logger.getLogger(VotingResultTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VXMLBindingException ex) {
            Logger.getLogger(VotingResultTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
