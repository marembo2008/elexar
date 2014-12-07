package com.anosym.elexar.util;

import com.anosym.vjax.VXMLMemberNotFoundException;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.anosym.vjax.xml.VDocument;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author marembo
 */
public class VotingTurnoutTest {

    /**
     * Test of unmarshalling of voting turnout data
     */
    @Test
    public void testVotingTurnoutUnmarshalling() {
        try {
            System.out.println("testVotingTurnoutUnmarshalling");
            VotingTurnout instance = new VotingTurnout(7899, "0789345678");
            VObjectMarshaller<VotingTurnout> v = new VObjectMarshaller<>(VotingTurnout.class);
            VDocument doc = v.marshall(instance);
            System.out.println(doc.toXmlString());
            String str = v.doMarshall(instance);
            System.out.println(str);
            VotingTurnout expectedTurnout = v.unmarshall(doc);
            assertEquals(instance, expectedTurnout);
        } catch (VXMLMemberNotFoundException ex) {
            Logger.getLogger(VotingTurnoutTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(VotingTurnoutTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
