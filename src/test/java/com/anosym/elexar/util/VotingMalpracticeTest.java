package com.anosym.elexar.util;

import com.anosym.vjax.VXMLBindingException;
import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author marembo
 */
public class VotingMalpracticeTest {

    /**
     * Test of toString method, of class VotingMalpractice.
     */
    @Test
    public void testVotingMalpractice() {
        try {
            System.out.println("testVotingMalpractice");
            VotingMalpractice instance = new VotingMalpractice("0123456789", ElectionMalpracticeType.COMPUTER_FAILURE);
            VObjectMarshaller<VotingMalpractice> m = new VObjectMarshaller<>(VotingMalpractice.class);
            VDocument doc = m.marshall(instance);
            System.out.println(doc.toXmlString());
            VotingMalpractice result = m.unmarshall(doc);
            String data = m.doMarshall(result);
            System.out.println(data);
            assertEquals(instance, result);
            for (ElectionMalpracticeType emt : ElectionMalpracticeType.values()) {
                System.out.println(emt.getCode() + "=" + emt);
            }
        } catch (VXMLMemberNotFoundException ex) {
            Logger.getLogger(VotingMalpracticeTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (VXMLBindingException ex) {
            Logger.getLogger(VotingMalpracticeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
